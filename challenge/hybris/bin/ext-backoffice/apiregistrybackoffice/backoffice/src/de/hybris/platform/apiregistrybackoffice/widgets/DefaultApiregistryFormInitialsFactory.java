/*
 * [y] hybris Platform
 *)a
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.apiregistrybackoffice.widgets;

import de.hybris.platform.apiregistrybackoffice.data.ApiregistryResetCredentialsForm;
import de.hybris.platform.util.Config;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for ApiregistryResetCredentialsForm creation.
 */
public class DefaultApiregistryFormInitialsFactory
{

	private static final String GRACE_PERIOD_PROP = "apiregistryservices.credentials.gracePeriod";
	private static final String SECRET_ALG_PROP = "apiregistryservices.credentials.secretAlgorithm";
	private static final String SECRET_SIZE_PROP = "apiregistryservices.credentials.secretSize";

	private static final Logger LOG = LoggerFactory.getLogger(DefaultApiregistryFormInitialsFactory.class);

    /**
     * Factory-method for ApiregistryResetCredentialsForm default creation.
     */
    public ApiregistryResetCredentialsForm getApiregistryResetCredentialsForm()
    {
        final ApiregistryResetCredentialsForm resetCredentialsForm = new ApiregistryResetCredentialsForm();
		resetCredentialsForm.setGracePeriod(Config.getInt(GRACE_PERIOD_PROP, 0));
		final String secretAlg = Config.getString(SECRET_ALG_PROP, "AES");
		try
		{
			final KeyGenerator keyGenerator = KeyGenerator.getInstance(secretAlg);
			keyGenerator.init(Config.getInt(SECRET_SIZE_PROP, 256));
			resetCredentialsForm.setClientSecret(Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded()));

		}
		catch (final NoSuchAlgorithmException e)
		{
			LOG.error(String.format("Cannot set default secret for Reset Credentials Form. Inexistent algorithm : %s", secretAlg),
					e);
		}
        return resetCredentialsForm;
    }
}

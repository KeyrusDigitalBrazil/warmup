/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.kymaintegrationsampledata.setup;

import de.hybris.platform.apiregistryservices.jalo.ExposedOAuthCredential;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;
import de.hybris.platform.webservicescommons.oauth2.client.ClientDetailsDao;

import java.security.SecureRandom;
import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * System setup class for importing and adding hooks for impex files
 */
public class KymaIntegrationSampleDataSystemSetup
{

	static final String VALID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+{}[]|:;<>?,./";
	static final  Integer DEFAULT_PASSWORD_LENGTH = 12;

	private ClientDetailsDao clientDetailsDao;
	private ModelService modelService;

	public void saveClientSecretForClientDetails(String clientId, ExposedOAuthCredential exposedOAuthCredential)
	{
		char[] generatedClientSecret;

		final OAuthClientDetailsModel oAuthClientDetailsModel = getClientDetailsDao().findClientById(clientId);
		if (StringUtils.isEmpty(oAuthClientDetailsModel.getClientSecret()))
		{
			generatedClientSecret = RandomStringUtils.random(DEFAULT_PASSWORD_LENGTH, 0, VALID_CHARS.length(), false, false,
					VALID_CHARS.toCharArray(), new SecureRandom()).toCharArray();

			final ExposedOAuthCredentialModel exposedOAuthCredentialModel = getModelService().get(exposedOAuthCredential.getPK());

			oAuthClientDetailsModel.setClientSecret(String.valueOf(generatedClientSecret));
			exposedOAuthCredentialModel.setPassword(String.valueOf((generatedClientSecret)));

			getModelService().saveAll(oAuthClientDetailsModel, exposedOAuthCredentialModel);

			Arrays.fill(generatedClientSecret, '\u0000');
		}

	}

	protected ClientDetailsDao getClientDetailsDao()
	{
		return clientDetailsDao;
	}

	@Required
	public void setClientDetailsDao(ClientDetailsDao clientDetailsDao)
	{
		this.clientDetailsDao = clientDetailsDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

}

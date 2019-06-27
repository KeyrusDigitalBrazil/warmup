/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.core.configuration.http.validation;

import de.hybris.platform.sap.core.configuration.enums.HTTPAuthenticationType;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.util.localization.Localization;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Perform validation of HTTPDestination model.
 * 
 */
public class HTTPDestinationValidateInterceptor implements ValidateInterceptor<SAPHTTPDestinationModel>
{

	private static final Logger LOG = Logger.getLogger(HTTPDestinationValidateInterceptor.class.getName());

	@Override
	public void onValidate(final SAPHTTPDestinationModel sapHTTPDestinationModel, final InterceptorContext ctx)
			throws InterceptorException
	{
		//------------------------------
		// Validate target URL
		//------------------------------

		// Check if URL is available
		if (StringUtils.isBlank(sapHTTPDestinationModel.getTargetURL()))
		{
			throw new InterceptorException(Localization.getLocalizedString("validation.HttpDestination.EmptyTargetUrl"));
		}
		else
		{
			// Check if URL is well formed
			try
			{
				final URL checkedURL = new URL(sapHTTPDestinationModel.getTargetURL());
				LOG.info(checkedURL.toString() + " is a valid URL destination.");
			}
			catch (final MalformedURLException e)
			{
				throw new InterceptorException(Localization.getLocalizedString("validation.HttpDestination.WrongURLFormat"), e);
			}
		}

		//------------------------------
		// Validate authentication data
		//------------------------------

		// Check if user-id and password are available in case of BASIC AUTHENTICATION is selected
		if (sapHTTPDestinationModel.getAuthenticationType() != null
				&& sapHTTPDestinationModel.getAuthenticationType().getCode() != null
				&& sapHTTPDestinationModel.getAuthenticationType().getCode().equals(HTTPAuthenticationType.BASIC_AUTHENTICATION.toString()))
		{
			validateUserIdAndPassword(sapHTTPDestinationModel);
		}
	}

	private void validateUserIdAndPassword(SAPHTTPDestinationModel sapHTTPDestinationModel) throws InterceptorException {
		if(StringUtils.isEmpty(sapHTTPDestinationModel.getPassword()) || StringUtils.isEmpty(sapHTTPDestinationModel.getUserid())) {
			throw new InterceptorException(
					Localization.getLocalizedString("validation.HttpDestination.IncompleteAuthenticationData"));
		}
	}

}

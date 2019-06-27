/*
* [y] hybris Platform
*
* Copyright (c) 2018 SAP SE or an SAP affiliate company.
* All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with SAP.
*
*/

package de.hybris.platform.yaasconfiguration.service.impl;

import static com.google.common.base.Preconditions.checkArgument;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.yaasconfiguration.CharonFactory;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasClientCredentialLocator;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class DefaultYaasServiceFactory implements YaasServiceFactory
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultYaasServiceFactory.class);

	private YaasConfigurationService yaasConfigurationService;

	private CharonFactory charonFactory;


	private List<YaasClientCredentialLocator> lookupLocators;


	@Override
	public <T> T lookupService(final Class<T> serviceType)
	{
		checkArgument(serviceType != null, "serviceType must not be null");

		try
		{
			// Get configured YaasService for the given identifier
			final YaasServiceModel serviceModel = yaasConfigurationService.getYaasServiceForId(serviceType.getSimpleName());

			LOG.debug("Found the YaaS service configuration for the given serviceType {}", serviceType.getSimpleName());

			final YaasClientCredentialModel yaasClientCredential = lookupCurrentCredential(serviceModel);

			if (null == yaasClientCredential)
			{
				throw new SystemException("Failed to find Yaas client credential configuration for the given serviceType :"
						+ serviceType.getSimpleName());
			}

			// Build the configuration based on given yaasClientCredential and service information
			final Map<String, String> yaasConfig = yaasConfigurationService.buildYaasConfig(yaasClientCredential, serviceType);

			// Delegate the call to Charon Factory, which returns the client proxy for the given configuration
			return charonFactory.client(yaasClientCredential.getIdentifier(), serviceType, yaasConfig, builder -> builder.build());

		}
		catch (final ModelNotFoundException exp)
		{
			throw new SystemException(
					"Failed to find YaaS service configuration for the given serviceType :" + serviceType.getSimpleName());
		}

	}

	/**
	 * Helper method to find the client credential details from the strategy that have been
	 *
	 * @param serviceModel
	 * @return YaasClientCredentialModel
	 */
	protected YaasClientCredentialModel lookupCurrentCredential(final YaasServiceModel serviceModel)
	{
		checkArgument(serviceModel != null, "serviceModel must not be null");

		for (final YaasClientCredentialLocator strategy : lookupLocators)
		{
			final YaasClientCredentialModel yaasClientCredential = strategy.lookup(serviceModel);

			if (null != yaasClientCredential)
			{
				return yaasClientCredential;
			}
		}

		return null;
	}


	public void setLookupLocators(final List<YaasClientCredentialLocator> lookupLocators)
	{
		this.lookupLocators = lookupLocators;
	}

	protected List<YaasClientCredentialLocator> getLookupLocators()
	{
		return lookupLocators;
	}

	@Required
	public void setYaasConfigurationService(final YaasConfigurationService yaasConfigurationService)
	{
		this.yaasConfigurationService = yaasConfigurationService;
	}

	@Required
	public void setCharonFactory(final CharonFactory charonFactory)
	{
		this.charonFactory = charonFactory;
	}

}

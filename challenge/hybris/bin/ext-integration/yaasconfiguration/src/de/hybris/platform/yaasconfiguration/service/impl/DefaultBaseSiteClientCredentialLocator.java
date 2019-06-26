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
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yaasconfiguration.model.BaseSiteServiceMappingModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasClientCredentialLocator;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class DefaultBaseSiteClientCredentialLocator implements YaasClientCredentialLocator
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultBaseSiteClientCredentialLocator.class);

	private YaasConfigurationService yaasConfigurationService;

	private BaseSiteService baseSiteService;

	private int order;

	@Override
	public YaasClientCredentialModel lookup(final YaasServiceModel serviceModel)
	{
		checkArgument(serviceModel != null, "serviceModel must not be null");

		try
		{
			//Get the BaseSiteServiceMappingModel for the configured basesite.
			final BaseSiteServiceMappingModel siteMapping = (BaseSiteServiceMappingModel) yaasConfigurationService
					.getBaseSiteServiceMappingForId(getCurrentBaseSite(), serviceModel);

			return siteMapping.getYaasClientCredential();

		}
		catch (final ModelNotFoundException exp)
		{
			LOG.warn("No data model found for the request basesite {}", getCurrentBaseSite());
		}

		return null;

	}

	protected YaasClientCredentialModel getDefaultCredential(final String serviceId)
	{
		return yaasConfigurationService.getYaasClientCredentialForId(serviceId);
	}

	protected String getCurrentBaseSite()
	{
		return baseSiteService.getCurrentBaseSite().getUid();
	}

	@Required
	public void setYaasConfigurationService(final YaasConfigurationService yaasConfigurationService)
	{
		this.yaasConfigurationService = yaasConfigurationService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	@Override
	public int getOrder()
	{
		return this.order;
	}

	@Override
	public void setOrder(final int order)
	{
		this.order = order;
	}

}

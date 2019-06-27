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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.ServiceVersionProvider;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import org.springframework.beans.factory.annotation.Required;


public class ServiceVersionProviderImpl implements ServiceVersionProvider
{

	private YaasConfigurationService yaasConfigurationService;

	@Override
	public String getVersion(final String clientName)
	{
		final String url = getYaasConfigurationService().getYaasServiceForId(clientName).getServiceURL();
		return url.substring(url.lastIndexOf('/') + 1);
	}

	protected YaasConfigurationService getYaasConfigurationService()
	{
		return yaasConfigurationService;
	}

	@Required
	public void setYaasConfigurationService(final YaasConfigurationService yaasConfigurationService)
	{
		this.yaasConfigurationService = yaasConfigurationService;
	}

}

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
package de.hybris.platform.sap.productconfig.services.testutil;

import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;

import java.util.Map;

import javax.annotation.Resource;


/**
 * Dummy implementation for {@link ProductConfigurationCacheAccessService} used only for spring mvc tests
 */
public class DummyCacheAccessService implements ProductConfigurationCacheAccessService
{
	@Resource(name = "sapProductConfigDummySessionAccessService")
	protected DummySessionAccessService sessionAccessService;

	@Override
	public void setAnalyticData(final String configId, final AnalyticsDocument analyticsDocument)
	{
		sessionAccessService.setAnalyticData(configId, analyticsDocument);
	}

	@Override
	public AnalyticsDocument getAnalyticData(final String configId)
	{
		return sessionAccessService.getAnalyticData(configId);
	}

	@Override
	public PriceSummaryModel getPriceSummaryState(final String configId)
	{
		return sessionAccessService.getPriceSummaryState(configId);
	}

	@Override
	public void setPriceSummaryState(final String configId, final PriceSummaryModel priceSummaryModel)
	{
		sessionAccessService.setPriceSummaryState(configId, priceSummaryModel);
	}

	@Override
	public ConfigModel getConfigurationModelEngineState(final String configId)
	{
		return sessionAccessService.getConfigurationModelEngineState(configId);
	}

	@Override
	public void setConfigurationModelEngineState(final String configId, final ConfigModel configModel)
	{
		sessionAccessService.setConfigurationModelEngineState(configId, configModel);
	}

	@Override
	public void removeConfigAttributeState(final String configId)
	{
		sessionAccessService.removeConfigAttributeState(configId);
	}


	@Override
	public Map<String, ClassificationSystemCPQAttributesContainer> getCachedNameMap(final String productCode)
	{
		return sessionAccessService.getCachedNameMap();
	}

}

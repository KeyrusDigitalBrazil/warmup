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
package de.hybris.platform.sap.productconfig.facades.analytics.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticCsticData;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticsPopulatorInput;
import de.hybris.platform.sap.productconfig.facades.analytics.ConfigurationAnalyticsFacade;
import de.hybris.platform.sap.productconfig.facades.populator.analytics.AnalyticsPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.services.analytics.impl.AnalyticsServiceImpl;
import de.hybris.platform.sap.productconfig.services.analytics.intf.AnalyticsService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Deafult implemntaion of the @{link ConfigurationAnalyticsFacade}.
 */
public class ConfigurationAnalyticsFacadeImpl implements ConfigurationAnalyticsFacade
{

	private AnalyticsService analyticsService;
	private Populator<AnalyticsPopulatorInput, List<AnalyticCsticData>> analyticsPopulator;

	@Override
	public List<AnalyticCsticData> getAnalyticData(final List<String> csticUiKeys, final String configId)
	{
		final AnalyticsDocument analyticDocument = getAnalyticsService().getAnalyticData(configId);
		final List<AnalyticCsticData> analyticData = new ArrayList<>(csticUiKeys.size());
		final AnalyticsPopulatorInput populatorInput = createPopulatorInput(csticUiKeys, analyticDocument);
		getAnalyticsPopulator().populate(populatorInput, analyticData);
		return analyticData;
	}


	protected AnalyticsPopulatorInput createPopulatorInput(final List<String> csticUiKeys, final AnalyticsDocument analyticDocument)
	{
		final AnalyticsPopulatorInput populatorInput = new AnalyticsPopulatorInput();
		populatorInput.setCsticUiKeys(csticUiKeys);
		populatorInput.setDocument(analyticDocument);
		return populatorInput;
	}


	protected AnalyticsService getAnalyticsService()
	{
		return analyticsService;
	}

	/**
	 * @param analyticsService
	 *           service providing analytical data
	 */
	@Required
	public void setAnalyticsService(final AnalyticsServiceImpl analyticsService)
	{
		this.analyticsService = analyticsService;
	}


	protected Populator<AnalyticsPopulatorInput, List<AnalyticCsticData>> getAnalyticsPopulator()
	{
		return analyticsPopulator;
	}


	/**
	 * @param analyticsPopulator
	 *           populator to fill the DTO
	 */
	public void setAnalyticsPopulator(final AnalyticsPopulator analyticsPopulator)
	{
		this.analyticsPopulator = analyticsPopulator;
	}

}

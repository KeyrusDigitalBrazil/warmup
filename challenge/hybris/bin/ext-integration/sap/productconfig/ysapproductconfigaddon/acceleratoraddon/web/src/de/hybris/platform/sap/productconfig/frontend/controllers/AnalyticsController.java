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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticCsticData;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticCsticValueData;
import de.hybris.platform.sap.productconfig.facades.analytics.ConfigurationAnalyticsFacade;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.util.impl.JSONProviderFactory;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller for analytics
 */
@Controller
@RequestMapping()
public class AnalyticsController extends AbstractProductConfigController
{
	static final String JSON_VALUE_PLACE_HOLDER = "XX";
	static final String JSON_NAME_MESSAGE_TEMPLATE = "messageTemplate";
	static final String JSON_NAME_PLACE_HOLDER = "placeHolder";
	static final String JSON_NAME_ANALYTIC_CSTIC_LIST = "analyticCstics";
	static final String JSON_NAME_CSTIC_UI_KEY = "csticUiKey";
	static final String JSON_NAME_VALUE_LIST = "analyticValues";
	static final String JSON_NAME_VALUE_NAME = "csticValueName";
	static final String JSON_NAME_POPULARITY_IN_PERCENT = "popularityInPercent";

	@Resource(name = "sapProductConfigAnalyticsFacade")
	private ConfigurationAnalyticsFacade analyticsFacade;

	/**
	 * Fetches all analytical data available for the currently on the UI visible characteristics, so that the UI can be
	 * updated with the analytical data.
	 *
	 * @param configId
	 *           configuration id
	 * @return view to render
	 */
	@RequestMapping(value = "/cpq/updateAnalytics", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	public String updateAnalytics(@RequestParam final String configId)
	{
		final List<AnalyticCsticData> analyticalData;
		final UiStatus uiStatus = getUiStatusForConfigId(configId);

		if (configId == null || uiStatus == null)
		{
			// config ID != null && uiStaus == null can happen in case of session failover
			analyticalData = new ArrayList<>();
		}
		else
		{
			final List<String> visibleCsticKeys = new ArrayList<String>();
			getUiStateHandler().fillAllVisibleCsticIdsOfGroup(uiStatus.getGroups(), visibleCsticKeys);
			analyticalData = getAnalyticsFacade().getAnalyticData(visibleCsticKeys, configId);
		}
		return toJson(analyticalData).build().toString();
	}


	protected JsonObjectBuilder toJson(final List<AnalyticCsticData> analyticalData)
	{
		final JsonObjectBuilder rootObj = JSONProviderFactory.getJSONProvider().createObjectBuilder();
		rootObj.add(JSON_NAME_POPULARITY_IN_PERCENT, toJson(JSON_NAME_POPULARITY_IN_PERCENT));
		final JsonArrayBuilder arrayBuilder = JSONProviderFactory.getJSONProvider().createArrayBuilder();
		for (final AnalyticCsticData analyticCstic : analyticalData)
		{
			arrayBuilder.add(toJson(analyticCstic));
		}
		rootObj.add(JSON_NAME_ANALYTIC_CSTIC_LIST, arrayBuilder);
		return rootObj;
	}

	protected JsonObjectBuilder toJson(final String kpiName)
	{
		final JsonObjectBuilder kpiObj = JSONProviderFactory.getJSONProvider().createObjectBuilder();
		kpiObj.add(JSON_NAME_PLACE_HOLDER, JSON_VALUE_PLACE_HOLDER);
		kpiObj.add(JSON_NAME_MESSAGE_TEMPLATE, callLocalisation(kpiName, new Object[]
		{ JSON_VALUE_PLACE_HOLDER }));
		return kpiObj;
	}

	protected String callLocalisation(final String kpiName, final Object[] arguments)
	{
		return Localization.getLocalizedString("sapproductconfig.analytics." + kpiName, arguments);
	}

	protected JsonObjectBuilder toJson(final AnalyticCsticData analyticCstic)
	{
		final JsonObjectBuilder objectBuilder = JSONProviderFactory.getJSONProvider().createObjectBuilder();
		objectBuilder.add(JSON_NAME_CSTIC_UI_KEY, analyticCstic.getCsticUiKey());
		final JsonArrayBuilder arrayBuilder = JSONProviderFactory.getJSONProvider().createArrayBuilder();
		for (final Entry<String, AnalyticCsticValueData> analyticValueEntry : analyticCstic.getAnalyticValues().entrySet())
		{
			arrayBuilder.add(toJson(analyticValueEntry.getKey(), analyticValueEntry.getValue()));
		}
		objectBuilder.add(JSON_NAME_VALUE_LIST, arrayBuilder);
		return objectBuilder;
	}

	protected JsonObjectBuilder toJson(final String key, final AnalyticCsticValueData value)
	{
		final JsonObjectBuilder objectBuilder = JSONProviderFactory.getJSONProvider().createObjectBuilder();
		objectBuilder.add(JSON_NAME_VALUE_NAME, key);
		final long percent = Math.round(value.getPopularityPercentage());
		objectBuilder.add(JSON_NAME_POPULARITY_IN_PERCENT, percent);
		return objectBuilder;
	}

	protected ConfigurationAnalyticsFacade getAnalyticsFacade()
	{
		return analyticsFacade;
	}

	/**
	 * @param analyticsFacade
	 *           analytic facade
	 */
	public void setAnalyticsFacade(final ConfigurationAnalyticsFacade analyticsFacade)
	{
		this.analyticsFacade = analyticsFacade;
	}

}

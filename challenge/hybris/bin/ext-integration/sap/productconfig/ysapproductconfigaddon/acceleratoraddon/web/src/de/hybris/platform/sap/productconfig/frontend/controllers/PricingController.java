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

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationPricingFacade;
import de.hybris.platform.sap.productconfig.facades.PriceDataPair;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.impl.NoConfigPrice;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.util.impl.JSONProviderFactory;
import de.hybris.platform.util.localization.Localization;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * Controller for pricing
 */
@Controller
@RequestMapping()
public class PricingController extends AbstractProductConfigController
{
	private static final String SAPCE = " ";
	static final String JSON_NAME_CSTIC_KEY = "csticKey";
	static final String JSON_NAME_CSTIC_VALUE_ARRAY = "csticValuesArray";
	static final String JSON_NAME_CSTIC_VALUE_KEY = "csticValueKey";
	static final String JSON_NAME_VALUE_PRICE_ARRAY = "valuePricesArray";
	static final String JSON_NAME_VALUE_PRICE = "valuePrice";
	static final String JSON_NAME_VALUE_OBSOLETE_PRICE = "valueObsoletePrice";
	static final String JSON_NAME_CURRENT_TOTAL_VALUE = "currentTotalValue";
	static final String JSON_NAME_CURRENT_TOTAL_SAVINGS = "currentTotalSavings";
	static final String JSON_NAME_PRICING_ERROR = "pricingError";
	static final String JSON_NAME_SELECTED_OPTIONS_VALUE = "selectedOptionsValue";
	static final String JSON_NAME_BASE_PRICE_VALUE = "basePriceValue";
	static final String VALUE_PRICES_INCLUDED = "sapproductconfig.deltaprcices.selected";
	static final String SAVE_PREFIX = "sapproductconfig.value.message.save";
	static final String SAVING_PREFIX = "sapproductconfig.value.message.saving";
	static final String JSON_NAME_SHOW_DELTA_PRICES = "showDeltaPrices";

	@Resource(name = "sapProductConfigPricingFacade")
	private ConfigurationPricingFacade configPricingFacade;

	/**
	 * Provides price information for the configuration (Summary on configuration level and absolute values or delta
	 * prices (dependent on backoffice setting) for possible values)
	 *
	 * @param configId
	 * 	configuration id
	 * @return Response as JSON string
	 */
	@RequestMapping(value = "/cpq/updatePricing", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	public String updatePricing(@RequestParam final String configId)
	{
		final UiStatus uiStatus = getUiStatusForConfigId(configId);
		final List<String> pricingInput = new ArrayList<>();
		final PricingData priceSummary;
		final List<PriceValueUpdateData> valuePrices;
		if (configId == null || uiStatus == null)
		{
			priceSummary = new PricingData();
			priceSummary.setBasePrice(new NoConfigPrice());
			priceSummary.setCurrentTotal(new NoConfigPrice());
			priceSummary.setCurrentTotalSavings(new NoConfigPrice());
			priceSummary.setSelectedOptions(new NoConfigPrice());
			valuePrices = new ArrayList<>();
		}
		else
		{
			getUiStateHandler().fillAllVisibleCsticIdsOfGroup(uiStatus.getGroups(), pricingInput);

			priceSummary = getConfigPricingFacade().getPriceSummary(configId);
			valuePrices = getConfigPricingFacade().getValuePrices(pricingInput, configId);
		}
		return toJson(priceSummary, valuePrices);

	}

	protected String toJson(final PricingData priceSummary, final List<PriceValueUpdateData> valuePrices)
	{
		final JsonObjectBuilder builder = toJson(priceSummary);
		builder.add(JSON_NAME_VALUE_PRICE_ARRAY, toJson(valuePrices));
		return builder.build().toString();
	}

	protected JsonArrayBuilder toJson(final List<PriceValueUpdateData> valuePrices)
	{
		final JsonArrayBuilder arrayBuilder = JSONProviderFactory.getJSONProvider().createArrayBuilder();
		final JsonObjectBuilder csticBuilder = JSONProviderFactory.getJSONProvider().createObjectBuilder();
		final JsonArrayBuilder csticValuesArrayBuilder = JSONProviderFactory.getJSONProvider().createArrayBuilder();
		for (final PriceValueUpdateData valuePrice : valuePrices)
		{
			if (addValuePriceToCsticValueArray(csticValuesArrayBuilder, valuePrice))
			{
				csticBuilder.add(JSON_NAME_SHOW_DELTA_PRICES, valuePrice.isShowDeltaPrices());
				csticBuilder.add(JSON_NAME_CSTIC_KEY, valuePrice.getCsticUiKey()).add(JSON_NAME_CSTIC_VALUE_ARRAY,
						csticValuesArrayBuilder);
				arrayBuilder.add(csticBuilder);
			}
		}
		return arrayBuilder;
	}

	protected boolean addValuePriceToCsticValueArray(final JsonArrayBuilder csticValuesArrayBuilder,
			final PriceValueUpdateData valuePrice)
	{
		boolean atLeastOneValuePrice = false;
		final boolean showDeltaPrices = valuePrice.isShowDeltaPrices();
		final Map<String, PriceDataPair> csticValuePrices = valuePrice.getPrices();

		for (final Map.Entry<String, PriceDataPair> entry : csticValuePrices.entrySet())
		{
			final PriceData csticValuePrice = entry.getValue().getPriceValue();
			if (ConfigPricing.NO_PRICE != csticValuePrice)
			{
				atLeastOneValuePrice = true;
				final boolean isSelected = valuePrice.getSelectedValues().contains(entry.getKey());
				final String valuePriceAsText = retrieveValuePriceAsText(showDeltaPrices, csticValuePrice, isSelected);
				final PriceData obsoletePrice = entry.getValue().getObsoletePriceValue();
				final String obsoletePriceAsText = retrieveObsoletepriceAsText(showDeltaPrices, obsoletePrice, isSelected);
				csticValuesArrayBuilder
						.add(JSONProviderFactory.getJSONProvider().createObjectBuilder().add(JSON_NAME_CSTIC_VALUE_KEY, entry.getKey())
								.add(JSON_NAME_VALUE_PRICE, valuePriceAsText).add(JSON_NAME_VALUE_OBSOLETE_PRICE, obsoletePriceAsText));
			}
		}
		return atLeastOneValuePrice;
	}

	protected String retrieveObsoletepriceAsText(final boolean showDeltaPrices, final PriceData obsoletePrice,
			final boolean isSelected)
	{
		String obsoletePriceAsText = "";
		if (obsoletePrice != null)
		{
			obsoletePriceAsText = obsoletePrice.getFormattedValue();
			if (showDeltaPrices)
			{
				final StringBuilder builder = new StringBuilder(32);
				builder.append(callLocalization(isSelected ? SAVING_PREFIX : SAVE_PREFIX));
				obsoletePriceAsText = builder.append(SAPCE).append(obsoletePriceAsText).toString();
			}
		}
		return obsoletePriceAsText;
	}

	protected String retrieveValuePriceAsText(final boolean showDeltaPrices, final PriceData csticValuePrice,
			final boolean isSelected)
	{
		String valuePriceAsText = "";
		if (BigInteger.ZERO.equals(csticValuePrice.getValue().unscaledValue()))
		{
			if (showDeltaPrices && isSelected)
			{
				valuePriceAsText = callLocalization(VALUE_PRICES_INCLUDED);
			}
		}
		else
		{
			valuePriceAsText = csticValuePrice.getFormattedValue();
		}
		return valuePriceAsText;
	}

	protected JsonObjectBuilder toJson(final PricingData priceSummary)
	{
		final JsonObjectBuilder builder = JSONProviderFactory.getJSONProvider().createObjectBuilder();
		final PriceData currentTotal = priceSummary.getCurrentTotal();
		if (currentTotal != null)
		{
			builder.add(JSON_NAME_BASE_PRICE_VALUE, priceSummary.getBasePrice().getFormattedValue());
			builder.add(JSON_NAME_SELECTED_OPTIONS_VALUE, priceSummary.getSelectedOptions().getFormattedValue());
			builder.add(JSON_NAME_CURRENT_TOTAL_VALUE, currentTotal.getFormattedValue());
			builder.add(JSON_NAME_CURRENT_TOTAL_SAVINGS, priceSummary.getCurrentTotalSavings().getFormattedValue());
		}
		else
		{
			builder.add(JSON_NAME_PRICING_ERROR, true);
			builder.add(JSON_NAME_BASE_PRICE_VALUE, ConfigPricing.NO_PRICE.getFormattedValue());
			builder.add(JSON_NAME_SELECTED_OPTIONS_VALUE, ConfigPricing.NO_PRICE.getFormattedValue());
			builder.add(JSON_NAME_CURRENT_TOTAL_VALUE, ConfigPricing.NO_PRICE.getFormattedValue());
			builder.add(JSON_NAME_CURRENT_TOTAL_SAVINGS, ConfigPricing.NO_PRICE.getFormattedValue());
		}
		return builder;
	}

	protected String callLocalization(final String key)
	{
		return Localization.getLocalizedString(key);
	}

	protected ConfigurationPricingFacade getConfigPricingFacade()
	{
		return configPricingFacade;
	}

	/**
	 * @param configPricingFacade
	 *           pricing facade
	 */
	public void setConfigPricingFacade(final ConfigurationPricingFacade configPricingFacade)
	{
		this.configPricingFacade = configPricingFacade;
	}

}

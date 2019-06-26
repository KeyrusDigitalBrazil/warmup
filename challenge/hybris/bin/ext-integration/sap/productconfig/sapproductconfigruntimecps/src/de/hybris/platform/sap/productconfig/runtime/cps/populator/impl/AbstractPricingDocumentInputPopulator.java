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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingConfigurationParameterCPS;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;


/**
 * Tools used for different populators (subclasses) dealing with constructing the input for the pricing REST call from
 * the cps representation of a configuration
 */
public class AbstractPricingDocumentInputPopulator
{

	protected static final boolean GROUP_CONDITION = false;
	protected static final boolean ITEM_CONDITION_REQUIRED = true;
	private PricingConfigurationParameterCPS pricingConfigurationParameter;
	private CommonI18NService i18NService;

	protected CommonI18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18nService
	 *           I18nService for getting the current locale
	 */
	public void setI18NService(final CommonI18NService i18nService)
	{
		i18NService = i18nService;
	}

	protected PricingConfigurationParameterCPS getPricingConfigurationParameter()
	{
		return pricingConfigurationParameter;
	}

	/**
	 * @param pricingConfigurationParameter
	 *           Represents customizing settings for filling the pricing context (like sales area)
	 */
	public void setPricingConfigurationParameter(final PricingConfigurationParameterCPS pricingConfigurationParameter)
	{
		this.pricingConfigurationParameter = pricingConfigurationParameter;
	}

	protected void fillCoreAttributes(final PricingDocumentInput target)
	{
		final CurrencyModel currencyModel = getI18NService().getCurrentCurrency();
		final String currencyIsoCode = getPricingConfigurationParameter().retrieveCurrencyIsoCode(currencyModel);
		target.setDocCurrency(currencyIsoCode);
		target.setLocCurrency(currencyIsoCode);
		target.setPricingProcedure(getPricingConfigurationParameter().getPricingProcedure());
		target.setGroupCondition(GROUP_CONDITION);
		target.setItemConditionsRequired(ITEM_CONDITION_REQUIRED);
	}


}

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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.impl.DefaultPricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.ssc.PricingConfigurationParameterSSC;


/**
 * Default implementation of {@link PricingConfigurationParameterSSC}
 */
public class DefaultPricingConfigurationParameterSSC extends DefaultPricingConfigurationParameter
		implements PricingConfigurationParameterSSC
{

	@Override
	public String getTargetForBasePrice()
	{
		return getSAPConfiguration().getSapproductconfig_condfunc_baseprice();
	}

	@Override
	public String getTargetForSelectedOptions()
	{
		return getSAPConfiguration().getSapproductconfig_condfunc_selectedoptions();
	}

	@Override
	public String getPricingProcedure()
	{
		return getSAPConfiguration().getSapproductconfig_pricingprocedure();
	}
}

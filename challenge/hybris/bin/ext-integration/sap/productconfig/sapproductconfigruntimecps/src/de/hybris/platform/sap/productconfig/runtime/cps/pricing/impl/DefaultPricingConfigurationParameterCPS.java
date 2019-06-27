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
package de.hybris.platform.sap.productconfig.runtime.cps.pricing.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingConfigurationParameterCPS;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DefaultPricingConfigurationParameter;


/**
 * Default implementation of {@link PricingConfigurationParameterCPS}
 */
public class DefaultPricingConfigurationParameterCPS extends DefaultPricingConfigurationParameter
		implements PricingConfigurationParameterCPS
{

	@Override
	public String getTargetForBasePrice()
	{
		return getSAPConfiguration().getSapproductconfig_condfunc_baseprice_cps();
	}

	@Override
	public String getTargetForSelectedOptions()
	{
		return getSAPConfiguration().getSapproductconfig_condfunc_selectedoptions_cps();
	}

	@Override
	public String getPricingProcedure()
	{
		return getSAPConfiguration().getSapproductconfig_pricingprocedure_cps();
	}
}

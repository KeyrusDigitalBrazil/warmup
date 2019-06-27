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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;

import java.util.List;


/**
 * Default implementation of the pricing provider
 */
public class DefaultPricingProviderImpl implements PricingProvider
{

	private static final String THE_PRICING_PROVIDER_IS_NOT_SUPPORTED = "The pricing provider is not supported by default but requires specific runtime implementation";

	/**
	 * @deprecated since 18.11.0
	 */
	@Deprecated
	@Override
	public PriceSummaryModel getPriceSummary(final String configId)
	{
		throw new UnsupportedOperationException(THE_PRICING_PROVIDER_IS_NOT_SUPPORTED);
	}

	@Override
	public PriceSummaryModel getPriceSummary(final String configId, final ConfigurationRetrievalOptions options)
			throws PricingEngineException
	{
		throw new UnsupportedOperationException(THE_PRICING_PROVIDER_IS_NOT_SUPPORTED);
	}

	@Override
	public boolean isActive()
	{
		return false;
	}

	@Override
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId)
	{
		throw new UnsupportedOperationException(THE_PRICING_PROVIDER_IS_NOT_SUPPORTED);
	}

	@Override
	public void fillValuePrices(final ConfigModel configModel)
	{
		throw new UnsupportedOperationException(THE_PRICING_PROVIDER_IS_NOT_SUPPORTED);
	}

}

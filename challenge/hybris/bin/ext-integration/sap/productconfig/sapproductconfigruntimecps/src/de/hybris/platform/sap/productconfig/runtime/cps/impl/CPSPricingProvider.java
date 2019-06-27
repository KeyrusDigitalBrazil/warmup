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

import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingHandler;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Facilitates pricing calls for CPS engine
 */
public class CPSPricingProvider implements PricingProvider
{
	private static final String USE_PRICING_PROVIDER_GET_PRICE_SUMMARY_STRING_CONFIGURATION_RETRIEVAL_OPTIONS = "Use PricingProvider#getPriceSummary(String, ConfigurationRetrievalOptions)";
	private PricingHandler pricingHandler;
	private ConfigurationMasterDataService masterDataService;

	/**
	 * @deprecated since 18.11.0 - use {@link PricingProvider#getPriceSummary(String, ConfigurationRetrievalOptions)}
	 *             instead
	 */
	@Deprecated
	@Override
	public PriceSummaryModel getPriceSummary(final String configId) throws PricingEngineException
	{
		throw new UnsupportedOperationException(USE_PRICING_PROVIDER_GET_PRICE_SUMMARY_STRING_CONFIGURATION_RETRIEVAL_OPTIONS);
	}

	@Override
	public PriceSummaryModel getPriceSummary(final String configId, final ConfigurationRetrievalOptions options)
			throws PricingEngineException, ConfigurationEngineException
	{
		return getPricingHandler().getPriceSummary(configId, options);
	}

	@Override
	public boolean isActive()
	{
		return true;
	}

	@Override
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId) throws PricingEngineException
	{
		fillValuePrices(updateModels, kbId, null);
	}

	@Override
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId,
			final ConfigurationRetrievalOptions options) throws PricingEngineException
	{
		final MasterDataContext ctxt = prepareMasterDataContext(kbId, options);
		for (final PriceValueUpdateModel updateModel : updateModels)
		{
			getPricingHandler().fillValuePrices(ctxt, updateModel);
		}
	}

	public void fillValuePrices(final ConfigModel configModel) throws PricingEngineException
	{
		fillValuePrices(configModel, null);
	}

	@Override
	public void fillValuePrices(final ConfigModel configModel, final ConfigurationRetrievalOptions options)
			throws PricingEngineException
	{
		final String kbId = configModel.getKbId();
		final MasterDataContext ctxt = prepareMasterDataContext(kbId, options);
		final InstanceModel rootInstance = configModel.getRootInstance();
		fillValuePricesForInstance(rootInstance, ctxt);
	}

	protected MasterDataContext prepareMasterDataContext(final String kbId, final ConfigurationRetrievalOptions options)
	{
		final MasterDataContext ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(getMasterDataService().getMasterData(kbId));
		if (null == options || null == options.getDiscountList())
		{
			ctxt.setDiscountList(Collections.emptyList());
		}
		else
		{
			ctxt.setDiscountList(options.getDiscountList());
		}
		if (null != options)
		{
			ctxt.setPricingProduct(options.getPricingProduct());
		}
		return ctxt;
	}

	protected void fillValuePricesForInstance(final InstanceModel instance, final MasterDataContext ctxt)
			throws PricingEngineException
	{
		for (final CsticModel cstic : instance.getCstics())
		{
			getPricingHandler().fillValuePrices(ctxt, cstic);
		}
		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			fillValuePricesForInstance(subInstance, ctxt);
		}
	}

	protected PricingHandler getPricingHandler()
	{
		return pricingHandler;
	}

	/**
	 * @param pricingHandler
	 *           Bean that handles REST call, delta price calculation and caching
	 */
	@Required
	public void setPricingHandler(final PricingHandler pricingHandler)
	{
		this.pricingHandler = pricingHandler;
	}

	protected ConfigurationMasterDataService getMasterDataService()
	{
		return masterDataService;
	}

	/**
	 * @param masterDataService
	 *           master data service
	 */
	@Required
	public void setMasterDataService(final ConfigurationMasterDataService masterDataService)
	{
		this.masterDataService = masterDataService;
	}

}

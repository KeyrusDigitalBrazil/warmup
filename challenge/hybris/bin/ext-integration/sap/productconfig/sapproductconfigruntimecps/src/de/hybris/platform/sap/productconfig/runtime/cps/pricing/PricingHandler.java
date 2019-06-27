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
package de.hybris.platform.sap.productconfig.runtime.cps.pricing;

import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;


/**
 * Obtains and exposes pricing information from CPS
 */
public interface PricingHandler
{

	/**
	 * Retrieves the price summary
	 *
	 * @param configId
	 *           id of the runtime configuration
	 * @param options
	 *           configuration retrieval options
	 *
	 * @return price summary
	 * @throws PricingEngineException
	 * @throws ConfigurationEngineException
	 */
	PriceSummaryModel getPriceSummary(String configId, ConfigurationRetrievalOptions options)
			throws PricingEngineException, ConfigurationEngineException;

	/**
	 * Attaches value prices to the cstic model
	 *
	 * @param ctxt
	 *           master data context
	 * @param cstic
	 *           cstic model
	 * @throws PricingEngineException
	 */
	void fillValuePrices(MasterDataContext ctxt, CsticModel cstic) throws PricingEngineException;

	/**
	 * Attaches absolute value prices or delta prices for all passed PriceValueUpdateModels dependent setting in
	 * backoffice
	 *
	 * @param ctxt
	 *           master data context
	 *
	 * @param updateModel
	 *           represents a characteristic to be updated with absolute value prices or delta prices for every possible
	 *           value
	 * @throws PricingEngineException
	 */
	void fillValuePrices(MasterDataContext ctxt, PriceValueUpdateModel updateModel) throws PricingEngineException;

}

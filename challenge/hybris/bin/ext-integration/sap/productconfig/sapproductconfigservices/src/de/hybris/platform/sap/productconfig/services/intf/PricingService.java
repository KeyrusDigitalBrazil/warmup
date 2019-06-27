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
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;

import java.util.List;


/**
 * Gets pricing information from pricing provider
 */
public interface PricingService
{
	/**
	 * Retrieves if present current total price, base price and selected options price
	 *
	 * @param configId runtime id of the configuration
	 * @return map of current total price, base price and selected options price
	 */
	PriceSummaryModel getPriceSummary(final String configId);

	/**
	 * Fills absolute value prices or delta prices into the price value update models that represent a characteristic
	 * dependent on setting in backoffice
	 *
	 * @param updateModels each list entry represents a characteristic for which absolute value prices or delta prices are filled
	 * @param kbId         KnowledgeBase id belonging to the runtime configuration for which absolute value prices or delta price
	 *                     are calculated
	 * @deprecated since 18.08 use {@link PricingService#fillValuePrices(List, ConfigModel)} instead
	 */
	@Deprecated
	void fillValuePrices(List<PriceValueUpdateModel> updateModels, String kbId);

	/**
	 * Fills absolute value prices or delta prices into the price value update models that represent a characteristic
	 * dependent on setting in backoffice
	 *
	 * @param updateModels each list entry represents a characteristic for which absolute value prices or delta prices are filled
	 * @param configModel  Configuration for which absolute value prices or delta price are calculated
	 */
	void fillValuePrices(List<PriceValueUpdateModel> updateModels, ConfigModel configModel);

	/**
	 * Enriches ConfigModel with value prices and price summary
	 *
	 * @param configModel configuration for which prices are filled
	 */
	void fillOverviewPrices(ConfigModel configModel);

	/**
	 * Indicates whether the underlying pricing provider is active
	 *
	 * @return true if the underlying pricing provider is active
	 */
	boolean isActive();
}

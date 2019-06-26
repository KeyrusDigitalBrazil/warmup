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
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * Handler to modify the CPSConfiguration, after it was retrieved from the rest service and before it is converted to
 * the configuration model.
 */
public interface ConfigurationModificationHandler
{
	/**
	 * adjusts the variant conditions used for calculating prices in sap pricing
	 *
	 * @param config
	 *           configuration model
	 * @param options
	 *           modification options
	 */
	void adjustVariantConditions(ConfigModel config, ConfigurationRetrievalOptions options);

	/**
	 * applies discount to a variant condition
	 *
	 * @param condition
	 *           variant condition
	 * @param variantConditionDiscounts
	 *           map containing variant condition discounts
	 */
	void applyConditionDiscount(CPSVariantCondition condition, Map<String, BigDecimal> variantConditionDiscounts);

	/**
	 * retrieves variant condition discounts
	 *
	 * @param kbId
	 *           knowledge base id
	 * @param itemKey
	 *           CPSItem key
	 * @param discountList
	 *           discount list
	 * @return map containing variant condition discounts
	 */
	Map<String, BigDecimal> retrieveVarCondDiscounts(String kbId, String itemKey, List<ProductConfigurationDiscount> discountList);
}

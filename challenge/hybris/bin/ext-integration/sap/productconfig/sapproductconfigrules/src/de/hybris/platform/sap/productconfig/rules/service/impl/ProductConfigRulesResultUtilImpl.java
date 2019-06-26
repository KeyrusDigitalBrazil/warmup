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
package de.hybris.platform.sap.productconfig.rules.service.impl;

import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRulesResultUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class ProductConfigRulesResultUtilImpl implements ProductConfigRulesResultUtil
{

	@Override
	public List<ProductConfigurationDiscount> retrieveRulesBasedVariantConditionModifications(final String configId)
	{
		return Collections.emptyList();
	}

	@Override
	public void deleteRulesResultsByConfigId(final String configId)
	{
		return;
	}

	@Override
	public Map<String, Map<String, List<ProductConfigMessage>>> retrieveDiscountMessages(final String configId)
	{
		return Collections.emptyMap();
	}
}

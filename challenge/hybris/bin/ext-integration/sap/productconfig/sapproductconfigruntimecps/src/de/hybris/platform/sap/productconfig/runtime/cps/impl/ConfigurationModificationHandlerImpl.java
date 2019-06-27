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

import de.hybris.platform.sap.productconfig.runtime.cps.ConfigurationModificationHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class ConfigurationModificationHandlerImpl implements ConfigurationModificationHandler
{
	private ConfigurationMasterDataService configurationMasterDataService;

	@Override
	public void adjustVariantConditions(final ConfigModel config, final ConfigurationRetrievalOptions options)
	{
		processInstance(config.getKbId(), config.getRootInstance(), options.getDiscountList());
	}

	protected void processInstance(final String kbId, final InstanceModel instance,
			final List<ProductConfigurationDiscount> discountList)
	{
		final Map<String, BigDecimal> variantConditionDiscounts = retrieveVarCondDiscounts(kbId, instance.getName(), discountList);
		adjustVariantCondition(instance, variantConditionDiscounts);
		processSubInstances(kbId, instance, discountList);
	}

	protected void processSubInstances(final String kbId, final InstanceModel instance,
			final List<ProductConfigurationDiscount> discountList)
	{
		final List<InstanceModel> subInstances = instance.getSubInstances();
		if (subInstances != null)
		{
			for (final InstanceModel subInstance : subInstances)
			{
				processInstance(kbId, subInstance, discountList);
			}
		}
	}

	protected void adjustVariantCondition(final InstanceModel instance, final Map<String, BigDecimal> variantConditionDiscounts)
	{
		final List<VariantConditionModel> variantConditions = instance.getVariantConditions();
		if (variantConditions != null)
		{
			for (final VariantConditionModel condition : variantConditions)
			{
				applyConditionDiscount(condition, variantConditionDiscounts);
			}
		}
	}

	protected void applyConditionDiscount(final VariantConditionModel condition,
			final Map<String, BigDecimal> variantConditionDiscounts)
	{
		final BigDecimal discountPercentage = variantConditionDiscounts.get(condition.getKey());
		if (discountPercentage != null)
		{
			condition.setFactor(computeFactorWithDiscount(condition.getFactor().toString(), discountPercentage));
		}
	}

	@Override
	public void applyConditionDiscount(final CPSVariantCondition condition,
			final Map<String, BigDecimal> variantConditionDiscounts)
	{
		final BigDecimal discountPercentage = variantConditionDiscounts.get(condition.getKey());
		if (discountPercentage != null)
		{
			condition.setFactor(computeFactorWithDiscount(condition.getFactor(), discountPercentage).toString());
		}
	}

	@Override
	public Map<String, BigDecimal> retrieveVarCondDiscounts(final String kbId, final String itemKey,
			final List<ProductConfigurationDiscount> discountList)
	{
		final Map<String, BigDecimal> variantConditionDiscounts = new java.util.HashMap<>();
		for (final ProductConfigurationDiscount discount : discountList)
		{
			final String variantConditionKey = getConfigurationMasterDataService().getValuePricingKey(kbId, itemKey,
					discount.getCsticName(), discount.getCsticValueName());
			if (variantConditionKey != null)
			{
				variantConditionDiscounts.put(variantConditionKey, discount.getDiscount());
			}
		}
		return variantConditionDiscounts;
	}

	protected BigDecimal computeFactorWithDiscount(final String originalFactor, final BigDecimal discount)
	{
		final BigDecimal factor = new BigDecimal(originalFactor);
		final BigDecimal relativeDiscount = discount.divide(BigDecimal.valueOf(100));
		final BigDecimal discountFactor = BigDecimal.ONE.subtract(relativeDiscount);
		return factor.multiply(discountFactor);
	}

	protected ConfigurationMasterDataService getConfigurationMasterDataService()
	{
		return configurationMasterDataService;
	}

	@Required
	public void setConfigurationMasterDataService(final ConfigurationMasterDataService configurationMasterDataService)
	{
		this.configurationMasterDataService = configurationMasterDataService;
	}

}

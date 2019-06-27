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
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.services.impl.PricingServiceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public class PricingRuleAwareServiceImpl extends PricingServiceImpl
{
	private ProductConfigRulesResultUtil rulesResultUtil;

	@Override
	protected void retrieveValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId, final String configId)
			throws PricingEngineException
	{
		final ConfigurationRetrievalOptions options = prepareRetrievalOptions(loadConfigModel(configId));
		getProviderFactory().getPricingProvider().fillValuePrices(updateModels, kbId, options);
	}

	@Override
	protected void retrieveValuePrices(final ConfigModel configModel) throws PricingEngineException
	{
		final ConfigurationRetrievalOptions options = prepareRetrievalOptions(configModel);
		getProviderFactory().getPricingProvider().fillValuePrices(configModel, options);
		restoreDiscountMessages(configModel);
	}


	protected void restoreDiscountMessages(final ConfigModel configModel)
	{
		final Map<String, Map<String, List<ProductConfigMessage>>> messagesByCstic = getRulesResultUtil()
				.retrieveDiscountMessages(configModel.getId());
		if (!messagesByCstic.isEmpty())
		{
			final InstanceModel rootInstance = configModel.getRootInstance();
			restoreDiscountMessageForInstance(rootInstance, messagesByCstic);
		}

	}

	public void restoreDiscountMessageForInstance(final InstanceModel instance,
			final Map<String, Map<String, List<ProductConfigMessage>>> messagesByCstic)
	{
		for (final CsticModel csticModel : instance.getCstics())
		{
			final Map<String, List<ProductConfigMessage>> messagesByValue = messagesByCstic.get(csticModel.getName());
			if (null != messagesByValue)
			{
				restoreDiscountMessageForCstic(csticModel, messagesByValue);
			}
		}
		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			restoreDiscountMessageForInstance(subInstance, messagesByCstic);
		}
	}

	protected void restoreDiscountMessageForCstic(final CsticModel csticModel,
			final Map<String, List<ProductConfigMessage>> messagesByValue)
	{
		for (final CsticValueModel valueModel : csticModel.getAssignedValues())
		{
			final List<ProductConfigMessage> messageList = messagesByValue.get(valueModel.getName());
			if (null != messageList)
			{
				removePromoAppliedMessages(valueModel);

				for (final ProductConfigMessage discountMessage : messageList)
				{
					valueModel.getMessages().add(discountMessage);
				}
			}
		}
	}

	protected void removePromoAppliedMessages(final CsticValueModel valueModel)
	{
		if (CollectionUtils.isNotEmpty(valueModel.getMessages()))
		{
			final Set<ProductConfigMessage> messagesToRemove = new HashSet<>();
			for (final ProductConfigMessage message : valueModel.getMessages())
			{
				if (message.getPromoType().equals(ProductConfigMessagePromoType.PROMO_APPLIED))
				{
					messagesToRemove.add(message);
				}
			}

			valueModel.getMessages().removeAll(messagesToRemove);
		}
	}

	@Override
	protected ConfigurationRetrievalOptions prepareRetrievalOptions(final ConfigModel configModel)
	{
		final ConfigurationRetrievalOptions options = super.prepareRetrievalOptions(configModel);
		options.setDiscountList(getRulesResultUtil().retrieveRulesBasedVariantConditionModifications(configModel.getId()));
		return options;
	}

	@Override
	protected ConfigurationRetrievalOptions prepareRetrievalOptionsWithDate(final ConfigModel configModel)
	{
		final ConfigurationRetrievalOptions options = super.prepareRetrievalOptionsWithDate(configModel);
		options.setDiscountList(getRulesResultUtil().retrieveRulesBasedVariantConditionModifications(configModel.getId()));
		return options;
	}

	protected ProductConfigRulesResultUtil getRulesResultUtil()
	{
		return rulesResultUtil;
	}

	@Required
	public void setRulesResultUtil(final ProductConfigRulesResultUtil rulesResultUtil)
	{
		this.rulesResultUtil = rulesResultUtil;
	}

}

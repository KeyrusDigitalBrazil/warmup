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
package de.hybris.platform.sap.productconfig.rules.cps.service.impl;

import de.hybris.platform.sap.productconfig.rules.cps.handler.CharacteristicValueRulesResultHandler;
import de.hybris.platform.sap.productconfig.rules.cps.model.CharacteristicValueRulesResultModel;
import de.hybris.platform.sap.productconfig.rules.cps.model.DiscountMessageRulesResultModel;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRulesResultUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ProductConfigurationDiscount;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class ProductConfigRulesResultUtilCPSImpl implements ProductConfigRulesResultUtil
{

	private CharacteristicValueRulesResultHandler rulesResultHandler;
	private ConfigModelFactory configModelFactory;

	@Override
	public List<ProductConfigurationDiscount> retrieveRulesBasedVariantConditionModifications(final String configId)
	{
		final List<CharacteristicValueRulesResultModel> rulesResults = getRulesResultHandler().getRulesResultsByConfigId(configId);
		if (rulesResults == null)
		{
			return Collections.emptyList();
		}
		final List<ProductConfigurationDiscount> modifications = new ArrayList<>();
		for (final CharacteristicValueRulesResultModel rulesResult : rulesResults)
		{
			modifications.add(mapRulesResultToVariantConditionModfication(rulesResult));
		}
		return modifications;
	}

	@Override
	public Map<String, Map<String, List<ProductConfigMessage>>> retrieveDiscountMessages(final String configId)
	{
		final List<CharacteristicValueRulesResultModel> rulesResultList = getRulesResultHandler()
				.getRulesResultsByConfigId(configId);
		if (rulesResultList == null)
		{
			return Collections.emptyMap();
		}
		final Map<String, Map<String, List<ProductConfigMessage>>> messagesByCstic = new HashMap<>();
		for (final CharacteristicValueRulesResultModel rulesResult : rulesResultList)
		{
			final String csticName = rulesResult.getCharacteristic();
			final Map<String, List<ProductConfigMessage>> messagesByValue = getOrCreateValueMap(messagesByCstic, csticName);
			messagesByValue.put(rulesResult.getValue(), convertDiscountMessageList(rulesResult.getMessageRulesResults()));
		}
		return messagesByCstic;

	}

	protected List<ProductConfigMessage> convertDiscountMessageList(
			final List<DiscountMessageRulesResultModel> messageRulesResults)
	{
		if (messageRulesResults.isEmpty())
		{
			return Collections.emptyList();
		}
		final List<ProductConfigMessage> ret = new ArrayList<>(messageRulesResults.size());
		for (final DiscountMessageRulesResultModel discountMessage : messageRulesResults)
		{
			ret.add(convertDiscountMessage(discountMessage));
		}
		return ret;
	}

	public Map<String, List<ProductConfigMessage>> getOrCreateValueMap(
			final Map<String, Map<String, List<ProductConfigMessage>>> messagesByCstic, final String csticName)
	{
		Map<String, List<ProductConfigMessage>> messagesByValue = messagesByCstic.get(csticName);
		if (null == messagesByValue)
		{
			messagesByValue = new HashMap<>();
			messagesByCstic.put(csticName, messagesByValue);
		}
		return messagesByValue;
	}


	protected ProductConfigurationDiscount mapRulesResultToVariantConditionModfication(
			final CharacteristicValueRulesResultModel rulesResult)
	{
		final ProductConfigurationDiscount varCondModification = new ProductConfigurationDiscount();
		varCondModification.setCsticName(rulesResult.getCharacteristic());
		varCondModification.setCsticValueName(rulesResult.getValue());
		varCondModification.setDiscount(rulesResult.getDiscountValue());
		return varCondModification;
	}

	protected ProductConfigMessage convertDiscountMessage(final DiscountMessageRulesResultModel discountMessage)
	{
		final ProductConfigMessageBuilder builder = getConfigModelFactory().createProductConfigMessageBuilder();
		builder.appendMessage(discountMessage.getMessage()).appendSeverity(ProductConfigMessageSeverity.INFO);
		builder.appendSource(ProductConfigMessageSource.RULE);
		builder.appendSubType(ProductConfigMessageSourceSubType.DISPLAY_PROMO_MESSAGE);
		builder.appendPromoType(ProductConfigMessagePromoType.PROMO_APPLIED).appendEndDate(discountMessage.getEndDate());
		return builder.build();
	}

	@Override
	public void deleteRulesResultsByConfigId(final String configId)
	{
		getRulesResultHandler().deleteRulesResultsByConfigId(configId);
	}


	protected CharacteristicValueRulesResultHandler getRulesResultHandler()
	{
		return rulesResultHandler;
	}

	@Required
	public void setRulesResultHandler(final CharacteristicValueRulesResultHandler rulesResultHandler)
	{
		this.rulesResultHandler = rulesResultHandler;
	}

	protected ConfigModelFactory getConfigModelFactory()
	{
		return configModelFactory;
	}

	@Required
	public void setConfigModelFactory(final ConfigModelFactory configModelFactory)
	{
		this.configModelFactory = configModelFactory;
	}

}

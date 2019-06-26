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
package de.hybris.platform.sap.productconfig.rules.cps.handler.impl;

import de.hybris.platform.sap.productconfig.rules.cps.handler.CharacteristicValueRulesResultHandler;
import de.hybris.platform.sap.productconfig.rules.cps.model.CharacteristicValueRulesResultModel;
import de.hybris.platform.sap.productconfig.rules.cps.model.DiscountMessageRulesResultModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class CharacteristicValueRulesResultHandlerImpl implements CharacteristicValueRulesResultHandler
{

	private static final Logger LOG = Logger.getLogger(CharacteristicValueRulesResultHandlerImpl.class);

	private static final BigDecimal MAX_DISCOUNT = new BigDecimal(100);

	private ModelService modelService;
	private ProductConfigurationPersistenceService persistenceService;


	@Override
	public List<CharacteristicValueRulesResultModel> getRulesResultsByConfigId(final String configId)
	{
		final ProductConfigurationModel configModel = getPersistenceService().getByConfigId(configId);
		return configModel.getCharacteristicValueRulesResults() == null ? new ArrayList<>()
				: configModel.getCharacteristicValueRulesResults();
	}

	@Override
	public void deleteRulesResultsByConfigId(final String configId)
	{
		final ProductConfigurationModel configModel = getPersistenceService().getByConfigId(configId);
		final List<CharacteristicValueRulesResultModel> characteristicValueRulesResults = configModel
				.getCharacteristicValueRulesResults();

		if (CollectionUtils.isNotEmpty(characteristicValueRulesResults))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Deleting " + characteristicValueRulesResults.size() + " rules result(s) associated with configuration'"
						+ configId + "' for user session '" + configModel.getUserSessionId() + "'");
			}
			getModelService().removeAll(characteristicValueRulesResults);
			configModel.setCharacteristicValueRulesResults(null);
		}
	}

	@Override
	public void mergeDiscountAndPersistResults(final CharacteristicValueRulesResultModel result, final String configId)
	{
		final ProductConfigurationModel configModel = getPersistenceService().getByConfigId(configId);

		final List<CharacteristicValueRulesResultModel> characteristicValueRulesResults = configModel
				.getCharacteristicValueRulesResults() == null ? new ArrayList<>(2)
						: new ArrayList<>(configModel.getCharacteristicValueRulesResults());
		result.setProductConfiguration(configModel);
		addDiscount(result, characteristicValueRulesResults);
		configModel.setCharacteristicValueRulesResults(characteristicValueRulesResults);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Persisting new discount '" + result.getDiscountValue() + " for characteristic '" + result.getCharacteristic()
					+ "', value '" + result.getValue() + "' for configuration with id '" + configId + "' for user session '"
					+ configModel.getUserSessionId() + "'");
		}
		getModelService().save(configModel);
	}


	@Override
	public void addMessageToRulesResult(final DiscountMessageRulesResultModel message, final String configId,
			final String csticName, final String csticValueName)
	{
		final List<CharacteristicValueRulesResultModel> resultList = findResult(csticName, csticValueName,
				getRulesResultsByConfigId(configId));
		if (resultList.isEmpty())
		{
			throw new NoSuchElementException(
					String.format("No rules result for csticname/valueName (%s/%s) found", csticName, csticValueName));
		}
		boolean isFirst = true;
		for (final CharacteristicValueRulesResultModel result : resultList)
		{
			final DiscountMessageRulesResultModel messageToAdd = cloneMessageIfRequired(message, isFirst);
			isFirst = false;
			final ArrayList list = null == result.getMessageRulesResults() ? new ArrayList<>(2)
					: new ArrayList(result.getMessageRulesResults());
			list.add(messageToAdd);
			result.setMessageRulesResults(list);
			messageToAdd.setCsticValueRulesResult(result);
			getModelService().save(result);
		}

	}

	public DiscountMessageRulesResultModel cloneMessageIfRequired(final DiscountMessageRulesResultModel message,
			final boolean isFirst)
	{
		DiscountMessageRulesResultModel messageToAdd = message;
		if (!isFirst)
		{
			final DiscountMessageRulesResultModel newMessage = createMessageInstance();
			newMessage.setMessage(message.getMessage());
			newMessage.setEndDate(message.getEndDate());
			messageToAdd = newMessage;
		}
		return messageToAdd;
	}


	@Override
	public CharacteristicValueRulesResultModel createInstance()
	{
		final CharacteristicValueRulesResultModel rulesResultModel = getModelService()
				.create(CharacteristicValueRulesResultModel.class);
		return rulesResultModel;
	}

	@Override
	public DiscountMessageRulesResultModel createMessageInstance()
	{
		final DiscountMessageRulesResultModel rulesResultModel = getModelService().create(DiscountMessageRulesResultModel.class);
		return rulesResultModel;
	}

	protected void addDiscount(final CharacteristicValueRulesResultModel newRulesResult,
			final List<CharacteristicValueRulesResultModel> characteristicValueRulesResults)
	{
		final List<CharacteristicValueRulesResultModel> result = findResult(newRulesResult.getCharacteristic(),
				newRulesResult.getValue(), characteristicValueRulesResults);
		if (!result.isEmpty())
		{
			final CharacteristicValueRulesResultModel existingRulesResult = result.get(0);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Discount for characteristic '" + existingRulesResult.getCharacteristic() + "', value '"
						+ existingRulesResult.getValue() + "' for configuration with id '"
						+ existingRulesResult.getProductConfiguration().getConfigurationId()
						+ "' already exists. Cumulating discounts.");
			}
			existingRulesResult
					.setDiscountValue(calculateDiscount(existingRulesResult.getDiscountValue(), newRulesResult.getDiscountValue()));
		}
		else
		{
			characteristicValueRulesResults.add(newRulesResult);
		}
	}

	public List<CharacteristicValueRulesResultModel> findResult(final String csticName, final String csticValueName,
			final List<CharacteristicValueRulesResultModel> characteristicValueRulesResults)
	{
		final List<CharacteristicValueRulesResultModel> result = characteristicValueRulesResults.stream()
				.filter(existingResult -> existingResult.getCharacteristic().equals(csticName)
						&& (StringUtils.isEmpty(csticValueName) || existingResult.getValue().equals(csticValueName)))
				.collect(Collectors.toList());
		return result;
	}

	protected BigDecimal calculateDiscount(final BigDecimal existingDiscount, final BigDecimal newDiscount)
	{
		BigDecimal cumulatedDiscount = existingDiscount.add(newDiscount);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Adding discount '" + newDiscount + "' to already existing discount '" + existingDiscount + "': '"
					+ cumulatedDiscount + "'");
		}
		if (cumulatedDiscount.compareTo(MAX_DISCOUNT) > 0)
		{
			cumulatedDiscount = MAX_DISCOUNT;
			LOG.warn("Cumulated discount exceeds maximum discount. Setting discount to '" + cumulatedDiscount + "'");
		}
		return cumulatedDiscount;
	}

	@Override
	public void copyAndPersistRuleResults(final String sourceConfigId, final String targetConfigId)
	{
		final ProductConfigurationModel sourceConfigModel = getPersistenceService().getByConfigId(sourceConfigId);
		final ProductConfigurationModel targetConfigModel = getPersistenceService().getByConfigId(targetConfigId);
		if (sourceConfigModel != null && targetConfigModel != null
				&& CollectionUtils.isNotEmpty(sourceConfigModel.getCharacteristicValueRulesResults()))
		{
			final List<CharacteristicValueRulesResultModel> sourceRulesResultModelList = sourceConfigModel
					.getCharacteristicValueRulesResults();

			final List<CharacteristicValueRulesResultModel> targetRulesResultModelList = cloneRulesResultModelList(targetConfigModel,
					sourceRulesResultModelList);
			targetConfigModel.setCharacteristicValueRulesResults(targetRulesResultModelList);
			getModelService().save(targetConfigModel);
		}
	}

	protected List<CharacteristicValueRulesResultModel> cloneRulesResultModelList(
			final ProductConfigurationModel targetConfigModel,
			final List<CharacteristicValueRulesResultModel> sourceRulesResultModelList)
	{
		final List<CharacteristicValueRulesResultModel> targetRulesResultModelList = new ArrayList<>(
				sourceRulesResultModelList.size());
		for (final CharacteristicValueRulesResultModel rulesResultModel : sourceRulesResultModelList)
		{

			final CharacteristicValueRulesResultModel newRulesResultModel = getModelService()
					.create(CharacteristicValueRulesResultModel.class);

			targetRulesResultModelList.add(newRulesResultModel);

			newRulesResultModel.setProductConfiguration(targetConfigModel);

			newRulesResultModel.setCharacteristic(rulesResultModel.getCharacteristic());
			newRulesResultModel.setValue(rulesResultModel.getValue());
			newRulesResultModel.setDiscountValue(rulesResultModel.getDiscountValue());

			final List<DiscountMessageRulesResultModel> messageRulesResultList = rulesResultModel.getMessageRulesResults();
			if (CollectionUtils.isNotEmpty(messageRulesResultList))
			{
				final List<DiscountMessageRulesResultModel> newMessageRulesResultList = new ArrayList<>(
						messageRulesResultList.size());

				for (final DiscountMessageRulesResultModel messageRulesResult : messageRulesResultList)
				{
					final DiscountMessageRulesResultModel newMessage = createMessageInstance();
					newMessage.setMessage(messageRulesResult.getMessage());
					newMessage.setEndDate(messageRulesResult.getEndDate());
					newMessage.setCsticValueRulesResult(newRulesResultModel);
					newMessageRulesResultList.add(newMessage);
				}
				newRulesResultModel.setMessageRulesResults(newMessageRulesResultList);
			}

		}
		return targetRulesResultModelList;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ProductConfigurationPersistenceService getPersistenceService()
	{
		return persistenceService;
	}

	@Required
	public void setPersistenceService(final ProductConfigurationPersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}
}

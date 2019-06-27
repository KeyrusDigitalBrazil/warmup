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
package com.hybris.ymkt.recommendationbuffer.service.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.recommendationbuffer.constants.SapymktrecommendationbufferConstants;
import com.hybris.ymkt.recommendationbuffer.dao.RecommendationBufferDao;
import com.hybris.ymkt.recommendationbuffer.model.SAPOfferInteractionModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoClickthroughModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoImpressionAggrModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoImpressionModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoTypeMappingModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecommendationBufferModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecommendationMappingModel;
import com.hybris.ymkt.recommendationbuffer.service.RecommendationBufferService;


/**
 * Buffer service for reading and saving recommendation related entities.
 * 
 * @see RecommendationBufferService
 */
public class DefaultRecommendationBufferService implements RecommendationBufferService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRecommendationBufferService.class);

	protected boolean enableRecommendationBuffer;
	protected int expiryOffset;
	protected ModelService modelService;
	protected RecommendationBufferDao recommendationBufferDao;

	@Override
	public List<SAPRecoImpressionAggrModel> getAggregatedImpressions(final int batchSize)
	{
		return recommendationBufferDao.findImpressionsAggregated(batchSize);
	}

	@Override
	public List<SAPRecoClickthroughModel> getClickthroughs(final int readBatchSize)
	{
		return recommendationBufferDao.findClickthroughs(readBatchSize);
	}

	@Override
	public SAPRecommendationBufferModel getGenericRecommendation(final String scenarioId, final String leadingItems)
	{
		return getRecommendation(scenarioId, leadingItems, SapymktrecommendationbufferConstants.GENERIC_RECO_TYPE);
	}

	@Override
	public SAPRecommendationBufferModel getRestrictedRecommendation(final String scenarioId, final String leadingItems)
	{
		return getRecommendation(scenarioId, leadingItems, SapymktrecommendationbufferConstants.RESTRICTED_RECO_TYPE);
	}

	private SAPRecommendationBufferModel getRecommendation(final String scenarioId, final String leadingItems,
			final String recoType)
	{
		if (!enableRecommendationBuffer)
		{
			return null;
		}

		LOGGER.debug("Retrieving {} recommendation from buffer for scenarioId='{}', leadingItems='{}'", //
				recoType, scenarioId, leadingItems);

		final String hashIds = this.getHashIdsForType(scenarioId, recoType);

		if (hashIds.isEmpty())
		{
			return null;
		}

		return this.recommendationBufferDao.findRecommendation(scenarioId, hashIds, leadingItems) //
				.stream().findFirst().orElse(null);
	}

	@Override
	public List<SAPOfferInteractionModel> getOfferInteractions(final int batchSize)
	{
		return recommendationBufferDao.findOfferInteractions(batchSize);
	}

	protected String getHashIdsForType(final String scenarioId, final String recoType)
	{
		final List<SAPRecoTypeMappingModel> mappings = recommendationBufferDao.findRecoTypeMapping(recoType, scenarioId);
		return mappings.stream().map(SAPRecoTypeMappingModel::getHashId).collect(Collectors.joining(","));
	}

	public String getHashIdsForUser(final String userId, final String scenarioId)
	{
		final List<SAPRecommendationMappingModel> mappings = recommendationBufferDao.findRecommendationMapping(userId, scenarioId);
		return mappings.stream().map(SAPRecommendationMappingModel::getHashId).collect(Collectors.joining(","));
	}

	@Override
	public List<SAPRecoImpressionModel> getImpressions(final int readBatchSize)
	{
		return recommendationBufferDao.findImpressions(readBatchSize);
	}

	@Override
	public SAPRecommendationBufferModel getPersonalizedRecommendation(final String userId, final String scenarioId,
			final String leadingItems)
	{
		if (!enableRecommendationBuffer)
		{
			return null;
		}

		LOGGER.debug("Retrieving personalized recommendation from buffer for userId='{}', scenarioId='{}', leadingItems='{}'", //
				userId, scenarioId, leadingItems);

		final String hashIds = this.getHashIdsForUser(userId, scenarioId);
		return recommendationBufferDao.findRecommendation(scenarioId, hashIds, leadingItems) //
				.stream().findFirst().orElse(null);
	}

	@Override
	public boolean isRecommendationExpired(final SAPRecommendationBufferModel recommendation)
	{
		return recommendation.getExpiresOn().before(new Date());
	}

	protected void removeExpiredAny(final Function<Date, List<? extends ItemModel>> serviceMethod)
	{
		if (!enableRecommendationBuffer)
		{
			return;
		}

		LOGGER.debug("Removing expired recommendations from buffer");

		try
		{
			final Date expiryDate = DateUtils.addDays(new Date(), -this.expiryOffset);
			serviceMethod.apply(expiryDate).stream().map(ItemModel::getPk).forEach(modelService::remove);
		}
		catch (final RuntimeException e)
		{
			LOGGER.error("An error occurred while removing expired recommendations", e);
		}
	}

	@Override
	public void removeExpiredMappings()
	{
		this.removeExpiredAny(recommendationBufferDao::findExpiredRecommendationMappings);
	}

	@Override
	public void removeExpiredRecommendations()
	{
		this.removeExpiredAny(recommendationBufferDao::findExpiredRecommendations);
	}

	@Override
	public void removeExpiredTypeMappings()
	{
		this.removeExpiredAny(recommendationBufferDao::findExpiredRecoTypeMappings);
	}

	@Override
	public synchronized void saveRecommendation(final String userId, final String scenarioId, final String hashId,
			final String leadingItems, final String recoList, final String recoType, final Date expiresOn)
	{
		if (!enableRecommendationBuffer)
		{
			return;
		}

		LOGGER.debug(
				"Saving recommendation in buffer userId='{}', scenarioId='{}', hashId='{}', leadingItems='{}', recoList='{}', recoType='{}', expiresOn='{}'", //
				userId, scenarioId, hashId, leadingItems, recoList, recoType, expiresOn);

		if (!userId.isEmpty())
		{
			// Save User-Scenario-Hash mapping
			this.saveRecommendationMapping(userId, scenarioId, hashId, expiresOn);
		}

		if (SapymktrecommendationbufferConstants.GENERIC_RECO_TYPE.equals(recoType)
				|| SapymktrecommendationbufferConstants.RESTRICTED_RECO_TYPE.equals(recoType))
		{
			// Save Scenario-RecoType-Hash mapping
			this.saveRecommendationTypeMapping(scenarioId, hashId, recoType, expiresOn);
		}

		this.saveRecommendationBuffer(scenarioId, hashId, leadingItems, cutTo255(recoList), expiresOn);
	}

	/**
	 * Takes a csv string and cuts it to fit in 255 characters. Required to make the recommended product list fit in the
	 * column. This makes the number of products that can be buffered dependent on the product id length and the number
	 * of products returned by the SAP Marketing recommendation model.
	 *
	 * @param csvString
	 *           List of product IDs separated by comma.
	 * @return List of product IDs separated by comma with maximum size 255.
	 */
	protected String cutTo255(final String csvString)
	{
		String workingString = csvString;
		while (workingString.length() > 255)
		{
			final int lastIndexOfComma = workingString.lastIndexOf(',');
			workingString = workingString.substring(0, lastIndexOfComma);
		}
		return workingString;
	}

	protected void saveRecommendationBuffer(final String scenarioId, final String hashId, final String leadingItems,
			final String recoList, final Date expiresOn)
	{
		try
		{
			final SAPRecommendationBufferModel recoModel = this.recommendationBufferDao
					.findRecommendation(scenarioId, hashId, leadingItems).stream() //
					.findFirst().map(ItemModel::getPk).map(modelService::<SAPRecommendationBufferModel> get) //
					.orElseGet(() -> modelService.create(SAPRecommendationBufferModel.class));

			recoModel.setScenarioId(scenarioId);
			recoModel.setHashId(hashId);
			recoModel.setLeadingItems(leadingItems);
			recoModel.setRecoList(recoList);
			recoModel.setExpiresOn(expiresOn);
			modelService.save(recoModel);
		}
		catch (final RuntimeException e)
		{
			LOGGER.error("An error occurred while saving recommendation with scenarioId={} hashId={} leadingItems={}", scenarioId,
					hashId, leadingItems, e);
		}
	}

	protected void saveRecommendationMapping(final String userId, final String scenarioId, final String hashId,
			final Date expiresOn)
	{
		try
		{
			final SAPRecommendationMappingModel recoModel = this.recommendationBufferDao
					.findRecommendationMapping(userId, scenarioId, hashId).stream() //
					.findFirst().map(ItemModel::getPk).map(modelService::<SAPRecommendationMappingModel> get) //
					.orElseGet(() -> modelService.create(SAPRecommendationMappingModel.class));

			recoModel.setUserId(userId);
			recoModel.setScenarioId(scenarioId);
			recoModel.setHashId(hashId);
			recoModel.setExpiresOn(expiresOn);
			modelService.save(recoModel);
		}
		catch (final RuntimeException e)
		{
			LOGGER.error("An error occurred while saving recommendation mapping with scenarioId={} hashId={} userId={}", scenarioId,
					hashId, userId, e);
		}
	}

	protected void saveRecommendationTypeMapping(final String scenarioId, final String hashId, final String recoType,
			final Date expiresOn)
	{
		try
		{
			final SAPRecoTypeMappingModel recoModel = this.recommendationBufferDao.findRecoTypeMapping(recoType, scenarioId).stream() //
					.findFirst().map(ItemModel::getPk).map(modelService::<SAPRecoTypeMappingModel> get) //
					.orElseGet(() -> modelService.create(SAPRecoTypeMappingModel.class));

			recoModel.setScenarioId(scenarioId);
			recoModel.setHashId(hashId);
			recoModel.setRecoType(recoType);
			recoModel.setExpiresOn(expiresOn);
			modelService.save(recoModel);
		}
		catch (final RuntimeException e)
		{
			LOGGER.error("An error occurred while saving recommendation type mapping with scenarioId={} hashId={}", scenarioId,
					hashId, e);
		}
	}

	@Required
	public void setEnableRecommendationBuffer(final boolean enableRecommendationBuffer)
	{
		this.enableRecommendationBuffer = enableRecommendationBuffer;
	}

	@Required
	public void setExpiryOffset(final int expiryOffset)
	{
		LOGGER.debug("expiryOffset={}", expiryOffset);
		this.expiryOffset = expiryOffset;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setRecommendationBufferDao(final RecommendationBufferDao recommendationBufferDao)
	{
		this.recommendationBufferDao = recommendationBufferDao;
	}

}

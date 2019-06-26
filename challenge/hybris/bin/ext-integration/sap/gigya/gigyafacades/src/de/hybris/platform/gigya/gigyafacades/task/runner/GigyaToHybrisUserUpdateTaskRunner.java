/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.gigya.gigyafacades.task.runner;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.constants.GigyaservicesConstants;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSyncDirection;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.gigya.socialize.GSResponse;


/**
 * This class is used to synchronize data from gigya to commerce
 */
public class GigyaToHybrisUserUpdateTaskRunner implements TaskRunner<TaskModel>
{

	private static final Logger LOG = Logger.getLogger(GigyaToHybrisUserUpdateTaskRunner.class);

	private GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao;

	private ModelService modelService;

	private GigyaService gigyaService;

	private Converter<GSResponse, CustomerModel> gigyaUserReverseConverter;

	@Override
	public void handleError(final TaskService taskService, final TaskModel taskModel, final Throwable error)
	{
		LOG.error("Error while updating user information from gigya to hybris " + error);
	}

	@Override
	public void run(final TaskService taskService, final TaskModel taskModel)
	{
		if (taskModel.getContextItem() instanceof CustomerModel)
		{
			final CustomerModel gigyaUser = ((CustomerModel) taskModel.getContextItem());

			final Map<GigyaConfigModel, List<GigyaFieldMappingModel>> sorted = collectRequiredMappings();
			sorted.forEach((key, value) -> {

				final LinkedHashMap<String, Object> params = new LinkedHashMap<>();
				try
				{
					final String extraProfileFields = collectExtraProfieFieldsRequired(value);
					params.put("UID", gigyaUser.getGyUID());
					params.put("extraProfileFields",
							extraProfileFields.endsWith(",") ? extraProfileFields.substring(0, extraProfileFields.length() - 1)
									: extraProfileFields);
					params.put("include", "loginIDs,emails,profile,data");

					final GSResponse gsResponse = gigyaService.callRawGigyaApiWithConfig("accounts.getAccountInfo", params, key, 5, 1);
					gigyaUserReverseConverter.convert(gsResponse, gigyaUser);
					modelService.save(gigyaUser);
				}
				catch (final GigyaApiException e)
				{
					LOG.error(e);
					handleRetry(taskModel);
				}
			});
		}
	}

	/**
	 * Method to collect extra profile fields required based on mappings
	 *
	 * @param List<GigyaFieldMappingModel>
	 * @return String
	 */
	private String collectExtraProfieFieldsRequired(final List<GigyaFieldMappingModel> value)
	{
		final StringBuilder extraProfileFieldsBuilder = new StringBuilder();
		value.forEach(item -> {
			if (item.getGigyaAttributeName().contains("."))
			{
				final String[] split = item.getGigyaAttributeName().split("\\.");
				Arrays.stream(split).forEach(s -> {
					if (GigyaservicesConstants.EXTRA_FIELDS.contains(s))
					{
						extraProfileFieldsBuilder.append(split[0]).append(",");
					}
				});
			}
			else
			{
				if (GigyaservicesConstants.EXTRA_FIELDS.contains(item.getGigyaAttributeName()))
				{
					extraProfileFieldsBuilder.append(item.getGigyaAttributeName()).append(",");
				}
			}
		});
		return extraProfileFieldsBuilder.toString();
	}

	/**
	 * Handle retry
	 */
	private void handleRetry(final TaskModel taskModel)
	{
		if (taskModel.getRetry().intValue() <= GigyaservicesConstants.MAX_RETRIES)
		{
			final RetryLaterException ex = new RetryLaterException("Error while performing the task, will retry after sometime.");
			final long delay = (long) 60 * 1000;
			ex.setDelay(delay); // delay for 24h
			throw ex;
		}
		else
		{
			LOG.error("Max retries reached, task failed to execute.");
		}
	}

	/**
	 * Method to get all field mappings and collect them basedon the gigya sync direction.
	 *
	 * @return Map<GigyaConfigModel, List<GigyaFieldMappingModel>>
	 */
	private Map<GigyaConfigModel, List<GigyaFieldMappingModel>> collectRequiredMappings()
	{
		final List<GigyaFieldMappingModel> allMappings = gigyaFieldMappingGenericDao.find();
		final Map<GigyaConfigModel, List<GigyaFieldMappingModel>> sorted = new HashMap<>();
		if (CollectionUtils.isNotEmpty(allMappings))
		{
			allMappings.forEach(item -> {
				if ((GigyaSyncDirection.G2H.equals(item.getSyncDirection())
						|| GigyaSyncDirection.BOTH.equals(item.getSyncDirection()) && !item.isCustom()))
				{
					final GigyaConfigModel gigyaConfig = item.getGigyaConfig();
					addToMap(sorted, gigyaConfig, item);
				}
			});
		}
		return sorted;
	}

	/**
	 * Method to add field mappings to map
	 *
	 * @param map
	 * @param config
	 * @param mapping
	 */
	private void addToMap(final Map<GigyaConfigModel, List<GigyaFieldMappingModel>> map, final GigyaConfigModel config,
			final GigyaFieldMappingModel mapping)
	{
		if (map.containsKey(config))
		{
			final List<GigyaFieldMappingModel> listOfMappings = new ArrayList<>(map.get(config));
			listOfMappings.add(mapping);
			map.put(config, listOfMappings);
		}
		else
		{
			final List<GigyaFieldMappingModel> listOfMappings = new ArrayList<>();
			listOfMappings.add(mapping);
			map.put(config, listOfMappings);
		}
	}

	public GenericDao<GigyaFieldMappingModel> getGigyaFieldMappingGenericDao()
	{
		return gigyaFieldMappingGenericDao;
	}

	@Required
	public void setGigyaFieldMappingGenericDao(final GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao)
	{
		this.gigyaFieldMappingGenericDao = gigyaFieldMappingGenericDao;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public GigyaService getGigyaService()
	{
		return gigyaService;
	}

	@Required
	public void setGigyaService(final GigyaService gigyaService)
	{
		this.gigyaService = gigyaService;
	}

	public Converter<GSResponse, CustomerModel> getGigyaUserReverseConverter()
	{
		return gigyaUserReverseConverter;
	}

	@Required
	public void setGigyaUserReverseConverter(final Converter<GSResponse, CustomerModel> gigyaUserReverseConverter)
	{
		this.gigyaUserReverseConverter = gigyaUserReverseConverter;
	}

}

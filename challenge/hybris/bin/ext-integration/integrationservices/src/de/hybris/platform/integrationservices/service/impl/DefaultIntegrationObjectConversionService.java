/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.service.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.populator.ItemToMapConversionContext;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import de.hybris.platform.integrationservices.service.IntegrationObjectNotFoundException;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

/**
 * The default implementation of IntegrationObjectConversionService.
 */
public class DefaultIntegrationObjectConversionService implements IntegrationObjectConversionService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultIntegrationObjectConversionService.class);

	private IntegrationObjectService integrationObjectService;
	private Converter<ItemToMapConversionContext, Map<String, Object>> itemToIntegrationObjectMapConverter;

	@Override
	public Map<String, Object> convert(final ItemModel itemModel, final String integrationObjectCode)
	{
		Preconditions.checkArgument(itemModel != null, "item cannot be null");
		Preconditions.checkArgument(StringUtils.isNotEmpty(integrationObjectCode), "integrationObjectCode cannot be null or empty");

		checkIfIntegrationObjectExists(integrationObjectCode);
		final IntegrationObjectItemModel integrationObjectItemModel =
				getIntegrationObjectService().findIntegrationObjectItemByTypeCode(integrationObjectCode, itemModel.getItemtype());
		final ItemToMapConversionContext conversionContext = getConversionContext(itemModel, integrationObjectItemModel);
		return getItemToIntegrationObjectMapConverter().convert(conversionContext);
	}

	protected ItemToMapConversionContext getConversionContext(final ItemModel itemModel,
															  final IntegrationObjectItemModel integrationObjectItemModel)
	{
		final ItemToMapConversionContext itemToMapConversionContext = new ItemToMapConversionContext();
		itemToMapConversionContext.setItemModel(itemModel);
		itemToMapConversionContext.setIntegrationObjectItemModel(integrationObjectItemModel);
		return itemToMapConversionContext;
	}

	protected void checkIfIntegrationObjectExists(final String integrationObjectCode)
	{
		try
		{
			getIntegrationObjectService().findIntegrationObject(integrationObjectCode);
		}
		catch(final ModelNotFoundException e)
		{
			final RuntimeException exception = new IntegrationObjectNotFoundException(integrationObjectCode);
			LOGGER.error(exception.getMessage());
			throw exception;
		}
	}

	protected Converter<ItemToMapConversionContext, Map<String, Object>> getItemToIntegrationObjectMapConverter()
	{
		return itemToIntegrationObjectMapConverter;
	}

	@Required
	public void setItemToIntegrationObjectMapConverter(
			final Converter<ItemToMapConversionContext, Map<String, Object>> itemToIntegrationObjectMapConverter)
	{
		this.itemToIntegrationObjectMapConverter = itemToIntegrationObjectMapConverter;
	}

	protected IntegrationObjectService getIntegrationObjectService()
	{
		return integrationObjectService;
	}

	@Required
	public void setIntegrationObjectService(final IntegrationObjectService integrationObjectService)
	{
		this.integrationObjectService = integrationObjectService;
	}
}

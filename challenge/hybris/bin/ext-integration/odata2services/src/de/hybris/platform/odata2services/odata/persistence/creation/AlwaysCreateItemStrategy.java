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

package de.hybris.platform.odata2services.odata.persistence.creation;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.servicelayer.exceptions.ModelCreationException;
import de.hybris.platform.servicelayer.exceptions.ModelInitializationException;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class AlwaysCreateItemStrategy implements CreateItemStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(AlwaysCreateItemStrategy.class);

	private ModelService modelService;
	private IntegrationObjectService integrationObjectService;

	@Override
	public ItemModel createItem(final StorageRequest storageRequest) throws EdmException
	{
		final String entityName = storageRequest.getEntityType().getName();
		LOG.trace("Item '{}' -> '{}' does not exist, trying to create a new one.", entityName,
				storageRequest.getODataEntry().getProperties());

		final String itemTypeCode = getIntegrationObjectService()
				.findItemTypeCode(storageRequest.getIntegrationObjectCode(), entityName);

		try
		{
			return getModelService().create(itemTypeCode);
		}
		catch (final ModelCreationException | ModelInitializationException e)
		{
			LOG.trace("internal_error due to exception: ", e);
			throw new InternalProcessingException(e);
		}
	}

	protected ModelService getModelService()
	{
		return this.modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected IntegrationObjectService getIntegrationObjectService()
	{
		return this.integrationObjectService;
	}

	@Required
	public void setIntegrationObjectService(final IntegrationObjectService integrationObjectService)
	{
		this.integrationObjectService = integrationObjectService;
	}
}

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

package de.hybris.platform.odata2services.odata.processor;

import de.hybris.platform.odata2services.config.ODataServicesConfiguration;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequestFactory;
import de.hybris.platform.odata2services.odata.persistence.PersistenceService;
import de.hybris.platform.odata2services.odata.persistence.StorageRequestFactory;
import de.hybris.platform.odata2services.odata.processor.reader.EntityReaderRegistry;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.olingo.odata2.api.processor.ODataContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * A default implementation of the factory, which creates new instance of the processor for every invocation.
 */
public class DefaultODataProcessorFactory implements ODataProcessorFactory
{
	private PersistenceService persistenceService;
	private ModelService modelService;
	private ODataServicesConfiguration oDataServicesConfiguration;
	private EntityReaderRegistry entityReaderRegistry;
	private ItemLookupRequestFactory itemLookupRequestFactory;
	private StorageRequestFactory storageRequestFactory;

	@Override
	public DefaultODataProcessor createProcessor(final ODataContext context)
	{
		final DefaultODataProcessor processor = new DefaultODataProcessor();
		processor.setPersistenceService(getPersistenceService());
		processor.setModelService(getModelService());
		processor.setODataServicesConfiguration(getODataServicesConfiguration());
		processor.setEntityReaderRegistry(getEntityReaderRegistry());
		processor.setItemLookupRequestFactory(getItemLookupRequestFactory());
		processor.setStorageRequestFactory(getStorageRequestFactory());
		processor.setContext(context);
		return processor;
	}

	protected PersistenceService getPersistenceService()
	{
		return persistenceService;
	}

	@Required
	public void setPersistenceService(final PersistenceService service)
	{
		persistenceService = service;
	}

	protected ODataServicesConfiguration getODataServicesConfiguration()
	{
		return oDataServicesConfiguration;
	}

	@Required
	public void setODataServicesConfiguration(final ODataServicesConfiguration configuration)
	{
		oDataServicesConfiguration = configuration;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService service)
	{
		modelService = service;
	}

	protected EntityReaderRegistry getEntityReaderRegistry()
	{
		return entityReaderRegistry;
	}

	@Required
	public void setEntityReaderRegistry(final EntityReaderRegistry registry)
	{
		entityReaderRegistry = registry;
	}

	protected ItemLookupRequestFactory getItemLookupRequestFactory()
	{
		return itemLookupRequestFactory;
	}

	@Required
	public void setItemLookupRequestFactory(final ItemLookupRequestFactory factory)
	{
		itemLookupRequestFactory = factory;
	}

	public StorageRequestFactory getStorageRequestFactory()
	{
		return storageRequestFactory;
	}

	@Required
	public void setStorageRequestFactory(final StorageRequestFactory storageRequestFactory)
	{
		this.storageRequestFactory = storageRequestFactory;
	}
}

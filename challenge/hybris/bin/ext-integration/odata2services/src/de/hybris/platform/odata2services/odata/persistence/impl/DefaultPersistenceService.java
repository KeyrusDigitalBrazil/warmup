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
package de.hybris.platform.odata2services.odata.persistence.impl;

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;
import static de.hybris.platform.odata2services.odata.persistence.ConversionOptions.conversionOptionsBuilder;
import static de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest.itemConversionRequestBuilder;

import de.hybris.platform.core.locking.ItemLockedForProcessingException;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.suspend.SystemIsSuspendedException;
import de.hybris.platform.odata2services.odata.persistence.ConversionOptions;
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService;
import de.hybris.platform.odata2services.odata.persistence.PersistenceService;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.creation.CreateItemStrategy;
import de.hybris.platform.odata2services.odata.persistence.exception.ItemNotFoundException;
import de.hybris.platform.odata2services.odata.persistence.hook.PersistHookExecutor;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;
import de.hybris.platform.odata2services.odata.processor.RetrievalErrorRuntimeException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Objects;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Default implementation for {@link PersistenceService}
 */
public class DefaultPersistenceService implements PersistenceService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPersistenceService.class);

	private ModelEntityService modelEntityService;
	private CreateItemStrategy createItemStrategy;
	private SessionService sessionService;
	private ModelService modelService;
	private PersistHookExecutor persistHookRegistry;
	private TransactionTemplate transactionTemplate;

	@Override
	public ODataEntry createEntityData(final StorageRequest storageRequest) throws EdmException
	{
		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				saveEntitiesInTransaction(storageRequest);
			}
		});

		return getODataEntry(storageRequest);
	}

	private ODataEntry getODataEntry(final StorageRequest storageRequest) throws EdmException
	{
		final ConversionOptions conversionOptions = conversionOptionsBuilder().withIncludeCollections(false).build();

		final Optional<ItemModel> contextItem = storageRequest.getContextItem();
		return contextItem.isPresent() ?
				toODataEntry(storageRequest.toLookupRequest(), conversionOptions, contextItem.get()) :
				storageRequest.getODataEntry();
	}

	@Override
	public ODataEntry getEntityData(final ItemLookupRequest lookupRequest, final ConversionOptions options) throws EdmException
	{
		final ItemModel item = getModelEntityService().lookup(lookupRequest);

		if (item == null)
		{
			final String integrationKey = getEntryIntegrationKeyProperty(lookupRequest.getODataEntry());
			final String type = lookupRequest.getEntityType().getName();
			throw new ItemNotFoundException(type, integrationKey);
		}
		return toODataEntry(lookupRequest, options, item);
	}

	@Override
	public ItemLookupResult<ODataEntry> getEntities(final ItemLookupRequest lookupRequest, final ConversionOptions options) throws EdmException
	{
		final ItemLookupResult<ItemModel> result = getModelEntityService().lookupItems(lookupRequest);
		return result.map(item -> toODataEntry(lookupRequest, options, item));
	}

	private ODataEntry toODataEntry(final ItemLookupRequest lookupRequest, final ConversionOptions options, final ItemModel item)
	{
		try
		{
			final ItemConversionRequest conversionRequest =
					itemConversionRequestBuilder().withEntitySet(lookupRequest.getEntitySet())
							.withItemModel(item)
							.withAcceptLocale(lookupRequest.getAcceptLocale())
							.withOptions(options)
							.withIntegrationObject(lookupRequest.getIntegrationObjectCode())
							.build();
			return getModelEntityService().getODataEntry(conversionRequest);
		}
		catch (final EdmException e)
		{
			throw new RetrievalErrorRuntimeException(item.getItemtype(), e);
		}
	}

	protected void saveEntitiesInTransaction(final StorageRequest context)
	{
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult()
		{
			@Override
			protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus)
			{
				saveEntities(context);
			}
		});
	}

	protected void saveEntities(final StorageRequest request)
	{
		try
		{
			final String integrationKey = request.getIntegrationKey();
			final ItemModel item = getModelEntityService().createOrUpdateItem(request, getCreateItemStrategy());

			getPersistHookRegistry().runPrePersistHook(request.getPrePersistHook(), item, integrationKey)
					.ifPresent(it -> persistModel(request.getPostPersistHook(), it, integrationKey));
		}
		catch (final SystemIsSuspendedException | ItemLockedForProcessingException | ModelSavingException | EdmException e)
		{
			LOG.error("internal_error due to {}: ", e.getClass().getSimpleName(), e);
			throw new InternalProcessingException(e, request.getIntegrationKey());
		}
	}

	protected void persistModel(final String postPersistHookName, final ItemModel item, final String integrationKey)
	{
		LOG.trace("Saving all created ItemModels");
		getModelService().saveAll();
		persistHookRegistry.runPostPersistHook(postPersistHookName, item, integrationKey);
	}

	protected String getEntryIntegrationKeyProperty(final ODataEntry oDataEntry)
	{
		return Objects.toString(oDataEntry.getProperties().get(INTEGRATION_KEY_PROPERTY_NAME));
	}

	protected CreateItemStrategy getCreateItemStrategy()
	{
		return createItemStrategy;
	}

	@Required
	public void setCreateItemStrategy(final CreateItemStrategy strategy)
	{
		createItemStrategy = strategy;
	}

	protected ModelEntityService getModelEntityService()
	{
		return modelEntityService;
	}

	@Required
	public void setModelEntityService(final ModelEntityService service)
	{
		modelEntityService = service;
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

	protected PersistHookExecutor getPersistHookRegistry()
	{
		return persistHookRegistry;
	}

	@Required
	public void setPersistHookRegistry(final PersistHookExecutor registry)
	{
		persistHookRegistry = registry;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected TransactionTemplate getTransactionTemplate()
	{
		return transactionTemplate;
	}

	@Required
	public void setTransactionTemplate(final TransactionTemplate transactionTemplate)
	{
		this.transactionTemplate = transactionTemplate;
	}
}

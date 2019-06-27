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
import de.hybris.platform.odata2services.filter.IntegrationKeyFilteringNotSupported;
import de.hybris.platform.odata2services.filter.NestedFilterNotSupportedException;
import de.hybris.platform.odata2services.filter.OperatorNotSupportedException;
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;
import de.hybris.platform.odata2services.odata.persistence.InvalidDataException;
import de.hybris.platform.odata2services.odata.persistence.InvalidEntryDataException;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequestFactory;
import de.hybris.platform.odata2services.odata.persistence.LanguageNotSupportedException;
import de.hybris.platform.odata2services.odata.persistence.PersistenceRuntimeApplicationException;
import de.hybris.platform.odata2services.odata.persistence.PersistenceService;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.StorageRequestFactory;
import de.hybris.platform.odata2services.odata.persistence.exception.ItemNotFoundException;
import de.hybris.platform.odata2services.odata.persistence.exception.PropertyNotFoundException;
import de.hybris.platform.odata2services.odata.processor.reader.EntityReader;
import de.hybris.platform.odata2services.odata.processor.reader.EntityReaderRegistry;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.batch.BatchHandler;
import org.apache.olingo.odata2.api.batch.BatchRequestPart;
import org.apache.olingo.odata2.api.batch.BatchResponsePart;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderBatchProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.api.uri.PathInfo;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityUriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class DefaultODataProcessor extends ODataSingleProcessor
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultODataProcessor.class);
	private static final EntityProviderReadProperties READ_CFG =
			EntityProviderReadProperties.init().mergeSemantic(false).build();

	private PersistenceService persistenceService;
	private ModelService modelService;
	private ODataServicesConfiguration oDataServicesConfiguration;
	private EntityReaderRegistry entityReaderRegistry;
	private ItemLookupRequestFactory itemLookupRequestFactory;
	private StorageRequestFactory storageRequestFactory;


	protected String createLogStatements(
			final EdmEntityType entityType, final ODataEntry oDataEntry,
			final int level) throws EdmException
	{
		final StringBuilder logBuilder = new StringBuilder();

		if (level < 1 && entityType != null)
		{
			logBuilder.append(String.format("Payload: %s%n", entityType.getName()));
			logBuilder.append("================\n");
		}

		final String indent = Strings.repeat("   ", level);
		for (final Map.Entry<String, Object> entry : oDataEntry.getProperties().entrySet())
		{
			final String propertyName = entry.getKey();
			final Object propertyValue = entry.getValue();
			if (propertyValue instanceof ODataEntry)
			{
				logBuilder.append(String.format("%s+ %s%n", indent, propertyName));
				logBuilder.append(createLogStatements(null, (ODataEntry) propertyValue, level + 1));
			}
			else if (propertyValue instanceof ODataFeed)
			{
				final List<ODataEntry> entries = ((ODataFeed) propertyValue).getEntries();
				int i = 0;
				for (final ODataEntry e : entries)
				{
					logBuilder.append(String.format("%s+ %s[%s]:%n", indent, propertyName, i++));
					logBuilder.append(createLogStatements(null, e, level + 1));
				}
			}
			else
			{
				final String value = propertyValue != null ? propertyValue.toString() : "";
				logBuilder.append(String.format("%s- %s: %s%n", indent, propertyName, value.replace("\n", "\\n")
						.replace("\r", "\\r")
						.replace("\t", "\\t")));
			}
		}

		if (level < 1 && entityType != null)
		{
			logBuilder.append("================");
		}
		return logBuilder.toString();
	}

	protected void logRequestEntity(final EdmEntityType entityType, final ODataEntry entry) throws EdmException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(createLogStatements(entityType, entry, 0));
		}
	}

	@Override
	public ODataResponse createEntity(final PostUriInfo uriInfo, final InputStream content,
			final String requestContentType, final String responseContentType) throws ODataException
	{
		final EdmEntitySet entitySet = uriInfo.getStartEntitySet();
		final EdmEntityType entityType = entitySet.getEntityType();
		final ODataEntry entry;

		try
		{
			entry = EntityProvider.readEntry(requestContentType, entitySet, content, READ_CFG);
		}
		catch (final EntityProviderException | RuntimeException e)
		{
			LOG.error("Exception while creating entity of type {}", entityType.getName(), e);
			throw new ODataPayloadProcessingException(e);
		}

		final StorageRequest storageRequest =  getStorageRequestFactory().create(getContext(), responseContentType, entitySet, entry);
		LOG.info("Entity requested to persist under Hybris Commerce system : {}", entitySet.getName());
		logRequestEntity(entityType, entry);

		try
		{
			final ODataEntry persistedEntry = getPersistenceService().createEntityData(storageRequest);

			final EntityProviderWriteProperties writeProperties = EntityProviderWriteProperties
					.serviceRoot(getContext().getPathInfo().getServiceRoot()).build();
			return EntityProvider.writeEntry(responseContentType, entitySet, persistedEntry.getProperties(),
					writeProperties);
		}
		catch (final InvalidDataException e)
		{
			LOG.error(e.getMessage(), e);
			throw new InvalidEntryDataException(e.getCode(), e.getMessage(), e, storageRequest.getIntegrationKey());
		}
		catch (final PersistenceRuntimeApplicationException | LanguageNotSupportedException e)
		{
			LOG.error(e.getMessage(), e);
			throw e;
		}
		catch (final RuntimeException e)
		{
			LOG.error("RuntimeException while trying to persist an ODataEntry:", e);
			throw new PersistenceErrorRuntimeException(e, storageRequest.getIntegrationKey());
		}
	}

	@Override
	public ODataResponse executeBatch(final BatchHandler handler, final String contentType, final InputStream content)
			throws ODataException
	{
		final PathInfo pathInfo = getContext().getPathInfo();
		final EntityProviderBatchProperties batchProperties = EntityProviderBatchProperties.init()
				.pathInfo(pathInfo)
				.setStrict(true)
				.build();
		final List<BatchRequestPart> batchParts;
		try
		{
			batchParts = EntityProvider.parseBatchRequest(contentType, new NewLineSanitizerInputStream(content), batchProperties);
		}
		catch (final ODataException | RuntimeException e)
		{
			LOG.error(e.getMessage(), e);
			throw new ODataPayloadProcessingException(e);
		}

		final int batchLimit = getoDataServicesConfiguration().getBatchLimit();

		if (batchParts.size() > batchLimit)
		{
			throw new BatchLimitExceededException(batchLimit);
		}

		final List<BatchResponsePart> responseParts = Lists.newArrayList();
		for (final BatchRequestPart batchPart : batchParts)
		{
			// we are returning n responses in batch (aka bulk)
			responseParts.add(handler.handleBatchPart(batchPart));
		}
		return EntityProvider.writeBatchResponse(responseParts);
	}

	@Override
	public BatchResponsePart executeChangeSet(final BatchHandler handler, final List<ODataRequest> requests) throws ODataException
	{
		try
		{
			Transaction.current().begin();
			final Object response = Transaction.current().execute(new TransactionBody()
			{
				@Override
				public List<ODataResponse> execute() throws Exception
				{
					return executeChangeSetInTransaction(handler, requests);
				}
			});
			final List<ODataResponse> responses = (List<ODataResponse>) response;
			if (!responses.isEmpty() &&
					responses.get(0).getStatus().getStatusCode() >= HttpStatusCodes.BAD_REQUEST.getStatusCode())
			{
				Transaction.current().rollback();
				getModelService().detachAll();
			}
			else
			{
				Transaction.current().commit();
			}

			return BatchResponsePart.responses(responses)
					.changeSet(true)
					.build();
		}
		catch (final ODataException e)
		{
			LOG.error(e.getMessage(), e);
			throw e;
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			throw new InternalProcessingException(e);
		}
	}

	@Override
	public ODataResponse countEntitySet(final GetEntitySetCountUriInfo uriInfo, final String contentType) throws ODataException
	{
		return read((UriInfo) uriInfo, contentType);
	}

	@Override
	public ODataResponse readEntity(final GetEntityUriInfo uriInfo, final String contentType) throws ODataException
	{
		return read((UriInfo) uriInfo, contentType);
	}

	@Override
	public ODataResponse readEntitySet(final GetEntitySetUriInfo uriInfo, final String contentType) throws ODataException
	{
		return read((UriInfo) uriInfo, contentType);
	}

	private ODataResponse read(final UriInfo uriInfo, final String contentType) throws ODataException
	{
		try
		{
			final EntityReader entityReader = entityReaderRegistry.getReader(uriInfo);
			return entityReader.read(getItemLookupRequestFactory().create(uriInfo, getContext(), contentType));
		}
		catch (final PropertyNotFoundException | ItemNotFoundException | InvalidDataException |
				LanguageNotSupportedException | OperatorNotSupportedException | NestedFilterNotSupportedException |
				IntegrationKeyFilteringNotSupported ex)
		{
			LOG.error(ex.getMessage(), ex);
			throw ex;
		}
		catch (final RuntimeException ex)
		{
			LOG.error(ex.getMessage(), ex);
			throw new RetrievalErrorRuntimeException(uriInfo.getStartEntitySet().getEntityType().getName(), ex);
		}
	}

	protected List<ODataResponse> executeChangeSetInTransaction(final BatchHandler handler,
			final List<ODataRequest> requests) throws ODataException
	{
		final List<ODataResponse> responses = Lists.newArrayList();
		for (final ODataRequest request : requests)
		{
			final ODataResponse response = handler.handleRequest(request);
			if (response.getStatus().getStatusCode() >= HttpStatusCodes.BAD_REQUEST.getStatusCode())
			{
				return Collections.singletonList(response);
			}
			responses.add(response);
		}

		return responses;
	}

	protected PersistenceService getPersistenceService()
	{
		return persistenceService;
	}

	public void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected ODataServicesConfiguration getoDataServicesConfiguration()
	{
		return oDataServicesConfiguration;
	}

	public void setODataServicesConfiguration(final ODataServicesConfiguration oDataServicesConfiguration)
	{
		this.oDataServicesConfiguration = oDataServicesConfiguration;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected EntityReaderRegistry getEntityReaderRegistry()
	{
		return entityReaderRegistry;
	}

	public void setEntityReaderRegistry(final EntityReaderRegistry entityReaderRegistry)
	{
		this.entityReaderRegistry = entityReaderRegistry;
	}

	protected ItemLookupRequestFactory getItemLookupRequestFactory()
	{
		return itemLookupRequestFactory;
	}

	public void setItemLookupRequestFactory(final ItemLookupRequestFactory itemLookupRequestFactory)
	{
		this.itemLookupRequestFactory = itemLookupRequestFactory;
	}

	public StorageRequestFactory getStorageRequestFactory()
	{
		return storageRequestFactory;
	}

	public void setStorageRequestFactory(final StorageRequestFactory storageRequestFactory)
	{
		this.storageRequestFactory = storageRequestFactory;
	}
}

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

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.config.ODataServicesConfiguration;
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;
import de.hybris.platform.odata2services.odata.persistence.InvalidDataException;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequestFactory;
import de.hybris.platform.odata2services.odata.persistence.PersistenceRuntimeApplicationException;
import de.hybris.platform.odata2services.odata.persistence.PersistenceService;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.StorageRequestFactory;
import de.hybris.platform.odata2services.odata.persistence.exception.ItemNotFoundException;
import de.hybris.platform.odata2services.odata.processor.reader.EntityReader;
import de.hybris.platform.odata2services.odata.processor.reader.EntityReaderRegistry;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.olingo.odata2.api.batch.BatchHandler;
import org.apache.olingo.odata2.api.batch.BatchRequestPart;
import org.apache.olingo.odata2.api.batch.BatchResponsePart;
import org.apache.olingo.odata2.api.commons.HttpContentType;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.PathInfo;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Lists;

@UnitTest
@RunWith(PowerMockRunner.class)
@PrepareForTest({EntityProvider.class, EntityProviderWriteProperties.class, BatchResponsePart.class, Transaction.class, URI.class})
@PowerMockIgnore({"org.apache.logging.log4j.spi.Provider", "javax.management.*"})
public class DefaultODataProcessorUnitTest
{
	private static final String CONTENT = "BodyContent";
	private static final String ENTITY_REQUESTED = "Products";
	private static final String INTEGRATION_KEY = "defaultKey";
	private static final String SERVICE_NAME = "TestIntegrationObject";

	@Mock
	private StorageRequest storageRequest;
	@Mock
	private ODataContext oDataContext;
	@Mock
	private PersistenceService persistenceService;
	@Mock
	private EdmEntitySet edmEntitySet;
	@Mock
	private ModelService modelService;
	@Mock
	private Transaction tx;
	@Mock
	private ODataServicesConfiguration oDataServicesConfiguration;
	@Mock
	private ItemLookupRequestFactory itemLookupRequestFactory;
	@Mock
	private StorageRequestFactory storageRequestFactory;
	@Mock
	private EntityReaderRegistry entityReaderRegistry;
	@InjectMocks
	private final DefaultODataProcessor oDataProcessor = new DefaultODataProcessor();
	
	@Captor
	private ArgumentCaptor<List<ODataResponse>> changeSetListCaptor;
	@Captor
	private ArgumentCaptor<List<BatchResponsePart>> batchPartListCaptor;

	private final InputStream input = new ByteArrayInputStream(CONTENT.getBytes());

	@Before
	public void setUp() throws Exception
	{
		when(oDataServicesConfiguration.getBatchLimit()).thenReturn(2);
		PowerMockito.mockStatic(Transaction.class);
		PowerMockito.when(Transaction.current()).thenReturn(tx);

		when(tx.execute(any())).thenCallRealMethod();
		when(tx.execute(any(), any())).thenCallRealMethod();

		final PathInfo mock = mock(PathInfo.class);
		doReturn(new URI("/" + SERVICE_NAME + "/")).when(mock).getServiceRoot();
		doReturn(mock).when(oDataContext).getPathInfo();
	}

	@Test
	public void testCreateEntity() throws ODataException
	{
		mockOData();

		final ODataResponse oDataResponse = oDataProcessor.createEntity(createMockUriInfo(), input,
				HttpContentType.APPLICATION_XML, HttpContentType.APPLICATION_XML);

		//TODO: needs additional asserts once the response body is finalised and when it is integrates with DAO
		assertThat(oDataResponse)
				.isNotNull();
	}

	@Test
	public void testCountEntitySet() throws ODataException
	{
		final ODataResponse oDataResponse = givenoDataResponse();

		final ODataResponse response = oDataProcessor.countEntitySet(mock(UriInfo.class), MediaType.APPLICATION_JSON);

		assertThat(response).isEqualTo(oDataResponse);
	}

	@Test
	public void testReadEntity() throws ODataException
	{
		final ODataResponse oDataResponse = givenoDataResponse();

		final ODataResponse response = oDataProcessor.readEntity(mock(UriInfo.class), MediaType.APPLICATION_JSON);

		assertThat(response).isEqualTo(oDataResponse);
	}

	@Test
	public void testReadEntitySet() throws ODataException
	{
		final ODataResponse oDataResponse = givenoDataResponse();

		final ODataResponse response = oDataProcessor.readEntitySet(mock(UriInfo.class), MediaType.APPLICATION_JSON);

		assertThat(response).isEqualTo(oDataResponse);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testReadEntityWhenEntityReaderRegistryCannotFindApplicableReader() throws EdmException
	{
		final EdmEntityType entityType = mock(EdmEntityType.class);
		when(entityType.getName()).thenReturn("name");
		final EdmEntitySet entitySet = mock(EdmEntitySet.class);
		when(entitySet.getEntityType()).thenReturn(entityType);
		final UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getStartEntitySet()).thenReturn(entitySet);

		when(entityReaderRegistry.getReader(any(UriInfo.class))).thenThrow(InternalProcessingException.class);

		assertThatThrownBy(() -> oDataProcessor.readEntity(uriInfo, MediaType.APPLICATION_XML))
				.isInstanceOf(RetrievalErrorRuntimeException.class)
				.hasCauseInstanceOf(InternalProcessingException.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testReadEntitySetWhenEntityReaderThrowsException() throws ODataException
	{
		when(itemLookupRequestFactory.create(any(UriInfo.class), any(ODataContext.class), anyString()))
				.thenReturn(mock(ItemLookupRequest.class));

		final EntityReader entityReader = mock(EntityReader.class);
		when(entityReader.read(any(ItemLookupRequest.class))).thenThrow(ItemNotFoundException.class);
		when(entityReaderRegistry.getReader(any(UriInfo.class))).thenReturn(entityReader);

		assertThatThrownBy(() -> oDataProcessor.readEntity(mock(UriInfo.class), MediaType.APPLICATION_XML))
				.isInstanceOf(ItemNotFoundException.class);
	}

	private ODataResponse givenoDataResponse() throws ODataException
	{
		when(itemLookupRequestFactory.create(any(UriInfo.class), any(ODataContext.class), anyString()))
				.thenReturn(mock(ItemLookupRequest.class));
		final ODataResponse oDataResponse = mock(ODataResponse.class);
		final EntityReader entityReader = mock(EntityReader.class);
		when(entityReader.read(any(ItemLookupRequest.class))).thenReturn(oDataResponse);
		when(entityReaderRegistry.getReader(any(UriInfo.class))).thenReturn(entityReader);
		return oDataResponse;
	}

	@Test
	public void testCreateEntityThrowsInternalProcessingException_readEntry() throws ODataException
	{
		final String exceptionCause = "Something went wrong";
		mockOData();
		PowerMockito.mockStatic(EntityProvider.class);
		PowerMockito.when(EntityProvider.readEntry(any(), any(), any(), any()))
				.thenThrow(new RuntimeException(exceptionCause));

		assertThatThrownBy(() -> oDataProcessor.createEntity(createMockUriInfo(), input,
				HttpContentType.APPLICATION_XML, HttpContentType.APPLICATION_XML))
				.isInstanceOf(ODataPayloadProcessingException.class)
						.hasMessage(String.format(
								"An unexpected error occurred while processing the request. The most likely cause of this " +
										"error is the formatting of your OData request payload. The detailed cause of this error " +
										"is visible in the log. %s", exceptionCause))
				.hasFieldOrPropertyWithValue("errorCode", "odata_error");
	}

	@Test
	public void testExecuteBatchThrowsInternalProcessingException_parseBatchRequest() throws ODataException
	{
		final String exceptionCause = "Something went wrong";
		mockOData();
		PowerMockito.mockStatic(EntityProvider.class);
		PowerMockito.when(EntityProvider.parseBatchRequest(any(), any(), any())).thenThrow(new RuntimeException(exceptionCause));

		assertThatThrownBy(() -> oDataProcessor.executeBatch(mock(BatchHandler.class), HttpContentType.APPLICATION_XML, input))
				.isInstanceOf(ODataPayloadProcessingException.class)
				.hasMessage(String.format(
						"An unexpected error occurred while processing the request. The most likely cause of this error is the formatting of " +
								"your OData request payload. The detailed cause of this error is visible in the log. %s",
						exceptionCause))
				.hasFieldOrPropertyWithValue("errorCode", "odata_error");
	}

	@Test
	public void testEntityServiceThrowsRuntimeException() throws ODataException
	{
		mockOData();
		doThrow(new RuntimeException("Test Exception"))
				.when(persistenceService).createEntityData(any(StorageRequest.class));

		assertThatThrownBy(() -> oDataProcessor.createEntity(createMockUriInfo(), input,
				HttpContentType.APPLICATION_XML, HttpContentType.APPLICATION_XML))
				.isInstanceOf(PersistenceRuntimeApplicationException.class)
				.hasMessage("There was an error encountered during the processing of the integration object." +
						" The detailed cause of this error is visible in the log.")
				.hasFieldOrPropertyWithValue("errorCode", "runtime_error")
				.hasFieldOrPropertyWithValue("integrationKey", INTEGRATION_KEY);
	}

	@Test
	public void testEntityServiceThrowsInvalidDataException() throws ODataException
	{
		mockOData();
		doThrow(new InvalidDataException("test_code", "Test exception message"))
				.when(persistenceService).createEntityData(any(StorageRequest.class));

		assertThatThrownBy(() -> oDataProcessor.createEntity(createMockUriInfo(), input,
				HttpContentType.APPLICATION_XML, HttpContentType.APPLICATION_XML))
				.isInstanceOf(PersistenceRuntimeApplicationException.class)
				.hasCauseInstanceOf(InvalidDataException.class)
				.hasFieldOrPropertyWithValue("errorCode", "test_code")
				.hasFieldOrPropertyWithValue("integrationKey", INTEGRATION_KEY);
	}

	@Test
	public void testEntityServiceThrowsModelSavingException() throws ODataException
	{
		final String exceptionMessage = "There was an error encountered during the processing of the integration object. The detailed cause of this error is visible in the log.";
		mockOData();
		doThrow(new ModelSavingException(exceptionMessage))
				.when(persistenceService).createEntityData(any(StorageRequest.class));

		assertThatThrownBy(() -> oDataProcessor.createEntity(createMockUriInfo(), input,
				HttpContentType.APPLICATION_XML, HttpContentType.APPLICATION_XML))
				.isInstanceOf(PersistenceRuntimeApplicationException.class)
				.hasCauseInstanceOf(ModelSavingException.class)
				.hasMessage(exceptionMessage)
				.hasFieldOrPropertyWithValue("errorCode", "runtime_error")
				.hasFieldOrPropertyWithValue("integrationKey", INTEGRATION_KEY);
	}

	private List<KeyPredicate> keyPredicates(final Map<String, String> keys) throws EdmException
	{
		final List<KeyPredicate> keyPredicateList = new ArrayList<>();

		for (Map.Entry<String, String> entry : keys.entrySet())
		{
			final KeyPredicate keyPredicate = mock(KeyPredicate.class);
			when(keyPredicate.getLiteral()).thenReturn(entry.getValue());
			final EdmProperty property = mock(EdmProperty.class);
			when(property.getName()).thenReturn(entry.getKey());
			when(keyPredicate.getProperty()).thenReturn(property);
			keyPredicateList.add(keyPredicate);
		}
		return keyPredicateList;
	}

	@Test
	public void testProcessorThrowsInvalidDataException_whenBatchLimit() throws ODataException
	{
		final BatchHandler batchHandler = mock(BatchHandler.class);

		PowerMockito.mockStatic(EntityProvider.class);
		PowerMockito.when(EntityProvider.parseBatchRequest(any(), any(), any()))
				.thenReturn(Lists.newArrayList(mock(BatchRequestPart.class), mock(BatchRequestPart.class), mock(BatchRequestPart.class)));

		assertThatThrownBy(() -> oDataProcessor.executeBatch(batchHandler, "multipart/mixed", input))
				.isInstanceOf(BatchLimitExceededException.class)
				.hasMessage("The number of integration objects sent in the " +
						"request has exceeded the 'odata2services.batch.limit' setting currently set to 2")
				.hasFieldOrPropertyWithValue("errorCode", "batch_limit_exceeded");
	}

	@Test
	public void testProcessorParsesBatch() throws ODataException
	{
		final BatchRequestPart part1 = mock(BatchRequestPart.class);
		final BatchRequestPart part2 = mock(BatchRequestPart.class);

		final BatchHandler batchHandler = mock(BatchHandler.class);
		when(batchHandler.handleBatchPart(part1)).thenReturn(mock(BatchResponsePart.class));
		when(batchHandler.handleBatchPart(part2)).thenReturn(mock(BatchResponsePart.class));

		PowerMockito.mockStatic(EntityProvider.class);
		PowerMockito.when(EntityProvider.parseBatchRequest(any(), any(), any()))
				.thenReturn(Lists.newArrayList(part1, part2));
		PowerMockito.when(EntityProvider.writeBatchResponse(any())).thenReturn(mock(ODataResponse.class));

		oDataProcessor.executeBatch(batchHandler, "multipart/mixed", input);
		verify(batchHandler).handleBatchPart(part1);
		verify(batchHandler).handleBatchPart(part2);
		PowerMockito.verifyStatic();
		EntityProvider.writeBatchResponse(batchPartListCaptor.capture());
		assertThat(batchPartListCaptor.getValue()).hasSize(2);
	}

	@Test
	public void testProcessorParsesChangeSet_transaction() throws ODataException, TransactionException
	{
		final ODataRequest request1 = mock(ODataRequest.class);
		final ODataRequest request2 = mock(ODataRequest.class);
		final ODataResponse response1 = mock(ODataResponse.class);
		final ODataResponse response2 = mock(ODataResponse.class);
		when(response1.getStatus()).thenReturn(HttpStatusCodes.CREATED);
		when(response2.getStatus()).thenReturn(HttpStatusCodes.CREATED);

		final BatchResponsePart.BatchResponsePartBuilder builder = mock(BatchResponsePart.BatchResponsePartBuilder.class);
		PowerMockito.mockStatic(BatchResponsePart.class);
		PowerMockito.when(BatchResponsePart.responses(any())).thenReturn(builder);

		when(builder.changeSet(anyBoolean())).thenReturn(builder);
		when(builder.build()).thenReturn(mock(BatchResponsePart.class));
		final BatchHandler batchHandler = mock(BatchHandler.class);
		when(batchHandler.handleRequest(request1)).thenReturn(response1);
		when(batchHandler.handleRequest(request2)).thenReturn(response2);

		oDataProcessor.executeChangeSet(batchHandler, Lists.newArrayList(request1, request2));

		verify(tx, times(2)).begin();  // 1 internal
		verify(tx).commit();
		verifyStatic();
		BatchResponsePart.responses(changeSetListCaptor.capture());
		assertThat(changeSetListCaptor.getValue()).hasSize(2)
				.contains(response1, response2);
	}

	@Test
	public void testProcessorParsesChangeSet_transactionRollback() throws ODataException, TransactionException
	{
		final ODataRequest request1 = mock(ODataRequest.class);
		final ODataRequest request2 = mock(ODataRequest.class);
		final ODataResponse response1 = mock(ODataResponse.class);
		final ODataResponse response2 = mock(ODataResponse.class);
		when(response1.getStatus()).thenReturn(HttpStatusCodes.CREATED);
		when(response2.getStatus()).thenReturn(HttpStatusCodes.BAD_REQUEST);

		final BatchResponsePart.BatchResponsePartBuilder builder = mock(BatchResponsePart.BatchResponsePartBuilder.class);
		PowerMockito.mockStatic(BatchResponsePart.class);
		PowerMockito.when(BatchResponsePart.responses(any())).thenReturn(builder);

		when(builder.changeSet(anyBoolean())).thenReturn(builder);
		when(builder.build()).thenReturn(mock(BatchResponsePart.class));
		final BatchHandler batchHandler = mock(BatchHandler.class);
		when(batchHandler.handleRequest(request1)).thenReturn(response1);
		when(batchHandler.handleRequest(request2)).thenReturn(response2);

		oDataProcessor.executeChangeSet(batchHandler, Lists.newArrayList(request1, request2));

		verify(tx, times(2)).begin();  // 1 internal
		verify(tx).rollback();
		verify(modelService).detachAll();
		verifyStatic();
		BatchResponsePart.responses(changeSetListCaptor.capture());
		assertThat(changeSetListCaptor.getValue()).hasSize(1)
				.contains(response2);
	}

	@Test
	public void testProcessorParsesChangeSet_ODataException() throws Exception
	{
		final ODataRequest request1 = mock(ODataRequest.class);
		final ODataRequest request2 = mock(ODataRequest.class);
		final ODataResponse response1 = mock(ODataResponse.class);
		final ODataResponse response2 = mock(ODataResponse.class);
		when(response1.getStatus()).thenReturn(HttpStatusCodes.CREATED);
		when(response2.getStatus()).thenReturn(HttpStatusCodes.BAD_REQUEST);

		doThrow(ODataException.class).when(tx).execute(any());

		assertThatThrownBy(() -> oDataProcessor.executeChangeSet(mock(BatchHandler.class), Lists.newArrayList(request1, request2)))
				.isInstanceOf(ODataException.class);
	}

	@Test
	public void testProcessorParsesChangeSet_Exception() throws Exception
	{
		final ODataRequest request1 = mock(ODataRequest.class);
		final ODataRequest request2 = mock(ODataRequest.class);
		final ODataResponse response1 = mock(ODataResponse.class);
		final ODataResponse response2 = mock(ODataResponse.class);
		when(response1.getStatus()).thenReturn(HttpStatusCodes.CREATED);
		when(response2.getStatus()).thenReturn(HttpStatusCodes.BAD_REQUEST);

		doThrow(RuntimeException.class).when(tx).execute(any());

		assertThatThrownBy(() -> oDataProcessor.executeChangeSet(mock(BatchHandler.class), Lists.newArrayList(request1, request2)))
				.isInstanceOf(InternalProcessingException.class)
				.hasCauseInstanceOf(RuntimeException.class)
				.hasFieldOrPropertyWithValue("errorCode", "internal_error");
	}

	private PostUriInfo createMockUriInfo() throws EdmException
	{
		final String integrationKey = "Staged|Default|ProductCode";
		final PostUriInfo postUriInfo = mock(PostUriInfo.class);
		final EdmEntityType edmEntityType = mock(EdmEntityType.class);

		doReturn(keyPredicates(Maps.newHashMap(INTEGRATION_KEY_PROPERTY_NAME, integrationKey))).when(postUriInfo).getKeyPredicates();
		doReturn(edmEntitySet).when(postUriInfo).getStartEntitySet();
		doReturn(edmEntityType).when(edmEntitySet).getEntityType();
		doReturn(ENTITY_REQUESTED).when(edmEntityType).getName();

		return postUriInfo;
	}

	private void mockOData() throws ODataException
	{
		final ODataResponse oDataResponse = mock(ODataResponse.class);
		final ODataEntry entry = mock(ODataEntry.class);
		final ODataEntityProviderPropertiesBuilder builder = mock(ODataEntityProviderPropertiesBuilder.class);
		final Map<String, Object> prop = Maps.newHashMap("TestName", "TestValue");

		mockStatic(EntityProvider.class);
		when(EntityProvider.readEntry(eq(HttpContentType.APPLICATION_XML), eq(edmEntitySet), eq(input),
				any(EntityProviderReadProperties.class))).thenReturn(entry);
		when(EntityProvider.writeEntry(eq(HttpContentType.APPLICATION_XML), eq(edmEntitySet), eq(prop), any()))
				.thenReturn(oDataResponse);

		doReturn(prop).when(entry).getProperties();
		when(storageRequestFactory.create(any(), any(), any(), any())).thenReturn(storageRequest);
		when(storageRequest.getIntegrationKey()).thenReturn(INTEGRATION_KEY);

		when(persistenceService.createEntityData(any(StorageRequest.class))).thenReturn(entry);
		mockStatic(EntityProviderWriteProperties.class);
		when(EntityProviderWriteProperties.serviceRoot(oDataContext.getPathInfo().getServiceRoot())).thenReturn(builder);
	}
}

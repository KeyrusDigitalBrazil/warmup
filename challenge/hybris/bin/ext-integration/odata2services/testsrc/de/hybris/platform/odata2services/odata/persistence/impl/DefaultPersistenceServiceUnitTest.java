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

import static de.hybris.platform.odata2services.odata.persistence.StorageRequest.storageRequestBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.locking.ItemLockedForProcessingException;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.suspend.SystemIsSuspendedException;
import de.hybris.platform.odata2services.odata.persistence.ConversionOptions;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService;
import de.hybris.platform.odata2services.odata.persistence.PersistenceRuntimeApplicationException;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.creation.CreateItemStrategy;
import de.hybris.platform.odata2services.odata.persistence.exception.ItemNotFoundException;
import de.hybris.platform.odata2services.odata.persistence.hook.PersistHookExecutor;
import de.hybris.platform.odata2services.odata.persistence.hook.PersistHookNotFoundException;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHookException;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHookException;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;
import de.hybris.platform.odata2services.odata.persistence.utils.ODataEntryBuilder;
import de.hybris.platform.odata2services.odata.processor.RetrievalErrorRuntimeException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataRuntimeApplicationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.collect.ImmutableMap;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPersistenceServiceUnitTest
{
	private static final String PRE_PERSIST_HOOK_NAME = "BEFORE_SAVE";
	private static final String POST_PERSIST_HOOK_NAME = "AFTER_SAVE";
	private static final ItemModel CREATED_ITEM = mock(ItemModel.class);
	private static final ItemModel CHANGED_ITEM = mock(ItemModel.class);
	private static final String INTEGRATION_KEY = "integrationKey|Value";
	private StorageRequest storageRequest;

	@InjectMocks
	private DefaultPersistenceService persistenceService;

	@Mock
	private ModelService modelService;
	@Mock
	private CreateItemStrategy createItemStrategy;
	@Mock
	private SessionService sessionService;
	@Mock
	private ModelEntityService modelEntityService;
	@Mock
	private PersistHookExecutor hookRegistry;
	@Mock
	private TransactionTemplate transactionTemplate;

	@Before
	public void setUpHappyPathDefaults() throws EdmException
	{
		storageRequest = buildStorageRequest();
		mockSessionServiceToExecuteBody();
		mockTransactionTemplateToExecuteCallback();

		doReturn(CREATED_ITEM).when(modelEntityService).createOrUpdateItem(storageRequest, createItemStrategy);
		doReturn(Optional.of(CHANGED_ITEM)).when(hookRegistry).runPrePersistHook(PRE_PERSIST_HOOK_NAME, CREATED_ITEM, INTEGRATION_KEY);
	}

	@Test
	public void testCreateEntityDataInvokesPostPersistHookAfterSave() throws EdmException
	{
		persistenceService.createEntityData(storageRequest);

		final InOrder callOrder = Mockito.inOrder(modelService, hookRegistry);
		callOrder.verify(hookRegistry).runPrePersistHook(PRE_PERSIST_HOOK_NAME, CREATED_ITEM, INTEGRATION_KEY);
		callOrder.verify(modelService).saveAll();
		callOrder.verify(hookRegistry).runPostPersistHook(POST_PERSIST_HOOK_NAME, CHANGED_ITEM, INTEGRATION_KEY);
	}

	@Test
	public void testCreateEntityDataDoesNotSaveIfPrePersistHookDisabledPersistence() throws EdmException
	{
		givenPrePersistHookReturnsNoItem();

		final ODataEntry responseODataEntry = persistenceService.createEntityData(storageRequest);

		verify(modelService, never()).saveAll();
		verify(hookRegistry, never()).runPostPersistHook(any(), any(), any());
		assertThat(responseODataEntry).isEqualTo(storageRequest.getODataEntry());
	}

	@Test
	public void testCreateEntityDataReturnsGetEntityDataWhenItemIsPersisted() throws EdmException
	{
		final ODataEntry savedEntry = ODataEntryBuilder.oDataEntryBuilder().withProperties(ImmutableMap.of("integrationKey", INTEGRATION_KEY, "otherAttr", "otherValue")).build();
		storageRequest.putItem(CHANGED_ITEM);
		when(modelEntityService.getODataEntry(any())).thenReturn(savedEntry);

		final ODataEntry responseODataEntry = persistenceService.createEntityData(storageRequest);
		
		assertThat(responseODataEntry).isEqualTo(savedEntry);
	}

	@Test
	public void testCreateEntityData_HandlesEdmException() throws EdmException
	{
		doThrow(EdmException.class).when(modelEntityService).createOrUpdateItem(storageRequest, createItemStrategy);

		assertThatThrownBy(() -> persistenceService.createEntityData(storageRequest))
				.isInstanceOf(PersistenceRuntimeApplicationException.class)
				.hasFieldOrPropertyWithValue("errorCode", "internal_error")
				.hasCauseInstanceOf(EdmException.class);

		verify(modelService, never()).saveAll();
	}

	@Test
	public void testCreateEntityData_HandlesItemLockedForProcessingException() throws EdmException
	{
		doThrow(ItemLockedForProcessingException.class).when(modelEntityService).createOrUpdateItem(storageRequest, createItemStrategy);

		assertThatThrownBy(() -> persistenceService.createEntityData(storageRequest))
				.isInstanceOf(PersistenceRuntimeApplicationException.class)
				.hasFieldOrPropertyWithValue("errorCode", "internal_error")
				.hasCauseInstanceOf(ItemLockedForProcessingException.class);

		verify(modelService, never()).saveAll();
	}

	@Test
	public void testCreateEntityData_HandlesModelSavingException()
	{
		doThrow(ModelSavingException.class).when(modelService).saveAll();

		assertThatThrownBy(() -> persistenceService.createEntityData(storageRequest))
				.isInstanceOf(PersistenceRuntimeApplicationException.class)
				.hasFieldOrPropertyWithValue("errorCode", "internal_error")
				.hasCauseInstanceOf(ModelSavingException.class);
		verify(hookRegistry, never()).runPostPersistHook(any(), any(), any());
	}

	@Test
	public void testCreateEntityData_HandlesSystemIsSuspendedException()
	{
		doThrow(SystemIsSuspendedException.class).when(modelService).saveAll();

		assertThatThrownBy(() -> persistenceService.createEntityData(storageRequest))
				.isInstanceOf(PersistenceRuntimeApplicationException.class)
				.hasFieldOrPropertyWithValue("errorCode", "internal_error")
				.hasCauseInstanceOf(SystemIsSuspendedException.class);
		verify(hookRegistry, never()).runPostPersistHook(any(), any(), any());
	}

	@Test
	public void testPersistHookNotFound() throws EdmException
	{
		doThrow(new PersistHookNotFoundException("myHook message", INTEGRATION_KEY)).when(modelEntityService).createOrUpdateItem(any(), any());

		assertThatThrownBy(() -> persistenceService.createEntityData(storageRequest))
				.isInstanceOf(ODataRuntimeApplicationException.class)
				.hasFieldOrPropertyWithValue("errorCode", "hook_not_found")
				.hasMessageContaining("myHook");
	}

	@Test
	public void testPrePersistHookException() throws EdmException
	{
		doThrow(new PrePersistHookException("myPrePersistHook", null, "abc|123")).when(modelEntityService).createOrUpdateItem(any(), any());

		assertThatThrownBy(() -> persistenceService.createEntityData(storageRequest))
				.isInstanceOf(ODataRuntimeApplicationException.class)
				.hasFieldOrPropertyWithValue("errorCode", "pre_persist_error")
				.hasMessageContaining("myPrePersistHook")
				.hasFieldOrPropertyWithValue("integrationKey", "abc|123");
	}

	@Test
	public void testPostPersistHookException() throws EdmException
	{
		doThrow(new PostPersistHookException("myPostPersistHook", null, "abc|123")).when(modelEntityService).createOrUpdateItem(any(), any());

		assertThatThrownBy(() -> persistenceService.createEntityData(storageRequest))
				.isInstanceOf(ODataRuntimeApplicationException.class)
				.hasFieldOrPropertyWithValue("errorCode", "post_persist_error")
				.hasMessageContaining("myPostPersistHook")
				.hasFieldOrPropertyWithValue("integrationKey", "abc|123");
	}

	@Test
	public void testGetEntityData() throws EdmException
	{
		when(modelEntityService.lookup(any())).thenReturn(mock(ItemModel.class));
		when(modelEntityService.getODataEntry(any())).thenReturn(mock(ODataEntry.class));
		final ODataEntry oDataEntry = mock(ODataEntry.class);
		final ItemLookupRequest lookupRequest = itemLookupRequestWithEntry(oDataEntry);

		final ConversionOptions options = ConversionOptions.conversionOptionsBuilder().withIncludeCollections(false).build();
		final ODataEntry entityData = persistenceService.getEntityData(lookupRequest, options);

		assertThat(entityData).isNotNull();
		verify(modelEntityService).getODataEntry(any());
	}

	@Test
	public void testGetEntityData_notExistingItem() throws EdmException
	{
		final ODataEntry oDataEntry =
				ODataEntryBuilder.oDataEntryBuilder().withProperty("integrationKey", "Staged|Default|testProduct001").build();
		final ItemLookupRequest lookupRequest = itemLookupRequestWithEntry(oDataEntry);

		final ConversionOptions options = ConversionOptions.conversionOptionsBuilder().build();
		assertThatThrownBy(() -> persistenceService.getEntityData(lookupRequest, options))
				.isInstanceOf(ItemNotFoundException.class)
				.hasMessage("[Product] with integration key [Staged|Default|testProduct001] was not found.")
				.hasFieldOrPropertyWithValue("errorCode", "not_found");
	}

	private ItemLookupRequest itemLookupRequestWithEntry(final ODataEntry entry) throws EdmException
	{
		final EdmEntitySet entitySet = givenEntitySetForType("Product");

		final ItemLookupRequest itemLookupRequest = mock(ItemLookupRequest.class);
		when(itemLookupRequest.getAcceptLocale()).thenReturn(Locale.ENGLISH);
		when(itemLookupRequest.getEntitySet()).thenReturn(entitySet);
		when(itemLookupRequest.getODataEntry()).thenReturn(entry);
		when(itemLookupRequest.getIntegrationObjectCode()).thenReturn("Product");
		doReturn(entitySet.getEntityType()).when(itemLookupRequest).getEntityType();
		when(itemLookupRequest.getNavigationSegments()).thenReturn(Collections.emptyList());
		return itemLookupRequest;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetEntitiesWhenItemsFound() throws EdmException
	{
		final ItemLookupRequest lookupRequest = givenItemLookupRequest();
		final ItemLookupResult<ItemModel> lookupResult = searchResult(20, mock(ItemModel.class), mock(ItemModel.class));
		when(modelEntityService.lookupItems(lookupRequest)).thenReturn(lookupResult);
		when(modelEntityService.getODataEntry(mock(ItemConversionRequest.class))).thenThrow(EdmException.class);

		final ItemLookupResult<ODataEntry> result = persistenceService.getEntities(lookupRequest, mock(ConversionOptions.class));

		assertThat(result.getEntries()).hasSize(2);
		assertThat(result.getTotalCount()).isEqualTo(lookupResult.getTotalCount());
	}

	@Test
	public void testGetEntitiesWhenItemsNotFound() throws EdmException
	{
		final ItemLookupRequest lookupRequest = givenItemLookupRequest();
		when(modelEntityService.lookupItems(lookupRequest)).thenReturn(notFoundResult());

		final ItemLookupResult<ODataEntry> result = persistenceService.getEntities(lookupRequest, mock(ConversionOptions.class));

		assertThat(result.getEntries()).isEmpty();
	}

	@Test
	public void testGetEntitiesThrowsRetrievalRuntimeException() throws EdmException
	{
		final ItemLookupRequest lookupRequest = givenItemLookupRequest();
		final ItemLookupResult<ItemModel> lookupResult = searchResult(2, mock(ItemModel.class), mock(ItemModel.class));
		when(modelEntityService.lookupItems(lookupRequest)).thenReturn(lookupResult);
		when(modelEntityService.getODataEntry(any(ItemConversionRequest.class))).thenThrow(mock(EdmException.class));

		assertThatThrownBy(() -> persistenceService.getEntities(lookupRequest, mock(ConversionOptions.class)))
				.isInstanceOf(RetrievalErrorRuntimeException.class);
	}

	private ItemLookupRequest givenItemLookupRequest() throws EdmException
	{
		final EdmEntityType entityType = mock(EdmEntityType.class);
		when(entityType.getName()).thenReturn("entityName");

		final EdmEntitySet entitySet = mock(EdmEntitySet.class);
		when(entitySet.getEntityType()).thenReturn(entityType);

		final ODataEntry oDataEntry = mock(ODataEntry.class);
		when(oDataEntry.getProperties()).thenReturn(Collections.singletonMap("integrationKey", "key"));

		final ItemLookupRequest itemLookupRequest = mock(ItemLookupRequest.class);
		when(itemLookupRequest.getEntitySet()).thenReturn(entitySet);
		when(itemLookupRequest.getAcceptLocale()).thenReturn(Locale.ENGLISH);
		when(itemLookupRequest.getIntegrationObjectCode()).thenReturn("code");
		when(itemLookupRequest.getEntityType()).thenReturn(entityType);
		when(itemLookupRequest.getODataEntry()).thenReturn(oDataEntry);
		return itemLookupRequest;
	}

	private EdmEntitySet givenEntitySetForType(final String typeName) throws EdmException
	{
		final EdmEntitySet entitySet = mock(EdmEntitySet.class);
		final EdmEntityType type = givenEdmEntityTypeExists(typeName);
		when(entitySet.getEntityType()).thenReturn(type);
		return entitySet;
	}

	private EdmEntityType givenEdmEntityTypeExists(final String typeName) throws EdmException
	{
		final EdmEntityType entityType = mock(EdmEntityType.class);
		when(entityType.getBaseType()).thenReturn(entityType);
		when(entityType.getName()).thenReturn(typeName);
		return entityType;
	}

	private StorageRequest buildStorageRequest() throws EdmException
	{
		final ODataEntry entry =
				ODataEntryBuilder.oDataEntryBuilder().withProperties(ImmutableMap.of("integrationKey", INTEGRATION_KEY)).build();

		return storageRequestBuilder()
				.withIntegrationKey(INTEGRATION_KEY)
				.withODataEntry(entry)
				.withPrePersistHook(PRE_PERSIST_HOOK_NAME)
				.withPostPersistHook(POST_PERSIST_HOOK_NAME)
				.withIntegrationObject("IntegrationObjectCode")
				.withEntitySet(givenEntitySetForType("Category"))
				.withAcceptLocale(Locale.ENGLISH)
				.withContentLocale(Locale.ENGLISH)
				.build();
	}

	private void mockSessionServiceToExecuteBody()
	{
		when(sessionService.executeInLocalView(any(SessionExecutionBody.class))).thenAnswer(invocation -> {
			final SessionExecutionBody args = (SessionExecutionBody) invocation.getArguments()[0];
			return args.execute();
		});
	}

	private void mockTransactionTemplateToExecuteCallback()
	{
		when(transactionTemplate.execute(any(TransactionCallbackWithoutResult.class))).thenAnswer(invocation -> {
			final TransactionCallbackWithoutResult callback = (TransactionCallbackWithoutResult) invocation.getArguments()[0];
			return callback.doInTransaction(new SimpleTransactionStatus());
		});
	}

	private void givenPrePersistHookReturnsNoItem()
	{
		doReturn(Optional.empty()).when(hookRegistry).runPrePersistHook(PRE_PERSIST_HOOK_NAME, CREATED_ITEM, INTEGRATION_KEY);
	}

	private ItemLookupResult<ItemModel> notFoundResult()
	{
		return searchResult(0);
	}

	private ItemLookupResult<ItemModel> searchResult(final int count, final ItemModel... items)
	{
		return ItemLookupResult.createFrom(Arrays.asList(items), count);
	}
}

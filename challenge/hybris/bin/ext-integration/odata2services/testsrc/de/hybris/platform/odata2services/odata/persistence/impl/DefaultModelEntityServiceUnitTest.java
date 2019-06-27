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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyGenerator;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;
import de.hybris.platform.odata2services.odata.persistence.creation.CreateItemStrategy;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupStrategy;
import de.hybris.platform.odata2services.odata.persistence.populator.EntityModelPopulator;
import de.hybris.platform.odata2services.odata.persistence.validator.CreateItemValidator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultModelEntityServiceUnitTest
{
	private static final String INTERNAL_INTEGRATION_KEY = "myKey";
	@InjectMocks
	private DefaultModelEntityService modelEntityService;
	@Mock
	private EntityModelPopulator entityModelPopulator;
	@Mock
	private ItemLookupStrategy itemLookupStrategy;
	@Mock
	private CreateItemStrategy createItemStrategy;
	@Mock
	private IntegrationKeyGenerator<EdmEntitySet, ODataEntry> integrationKeyGenerator;
	@Mock
	private StorageRequest request;
	@Spy
	private final List<CreateItemValidator> createItemValidators = Lists.newArrayList();

	@Before
	public void setUp()
	{
		when(integrationKeyGenerator.generate(any(), any())).thenReturn(INTERNAL_INTEGRATION_KEY);
	}

	@Test
	public void insertEntry_Success() throws EdmException
	{
		givenStorageRequest();
		final CreateItemValidator[] validators = givenItemValidators(passingValidator(), passingValidator());

		modelEntityService.createOrUpdateItem(request, createItemStrategy);

		verifyCallToCreateItem();
		verifyModelPopulator();
		verifyAllValidatorsCalled(validators);
		verifyCallToPutItem();
	}

	@Test
	public void updateEntry_Success() throws EdmException
	{
		givenStorageRequest();
		final CreateItemValidator[] validators = givenItemValidators(passingValidator(), passingValidator(),
				passingValidator());

		final ItemModel item = mock(ItemModel.class);
		givenItemExists(item);

		modelEntityService.createOrUpdateItem(request, createItemStrategy);

		verifyNoCallToCreateItem();
		verifyModelPopulator();
		verifyAllNonCreateValidatorsCalled(validators);
		verifyCallToPutItem();

		final ItemModel itemReturned = modelEntityService.createOrUpdateItem(request, createItemStrategy);
		assertThat(itemReturned).isEqualTo(item);
	}

	@Test
	public void testCreateIsNeverCalledWhenValidatorThrowsException() throws EdmException
	{
		givenStorageRequest();
		final CreateItemValidator[] validators = givenItemValidators(failingCreateValidator(), passingValidator());

		assertThatThrownBy(() -> modelEntityService.createOrUpdateItem(request, createItemStrategy)).isNotNull();

		verifyNoCallToCreateItem();
		verify(validators[1], never()).beforeCreateItem(any(), any());
	}

	@Test
	public void testLookupIsNeverCalledWhenValidatorThrowsException() throws EdmException
	{
		givenStorageRequest();
		final CreateItemValidator[] validators = givenItemValidators(failingLookupValidator(), passingValidator());

		assertThatThrownBy(() -> modelEntityService.createOrUpdateItem(request, createItemStrategy)).isNotNull();

		verify(itemLookupStrategy, never()).lookup(any());
		verify(validators[1], never()).beforeItemLookup(any(), any());
	}

	@Test
	public void testPopulateIsNeverCalledWhenValidatorThrowsException() throws EdmException
	{
		givenStorageRequest();
		final CreateItemValidator[] validators = givenItemValidators(failingPopulateValidator(), passingValidator());

		assertThatThrownBy(() -> modelEntityService.createOrUpdateItem(request, createItemStrategy)).isNotNull();

		verify(entityModelPopulator, never()).populateItem(any(), any());
		verify(validators[1], never()).beforePopulateItem(any(), any());
	}

	@Test
	public void testAddIntegrationKeyToODataEntry()
	{
		final ODataEntry entry = mock(ODataEntry.class);
		final Map<String, Object> props = Maps.newHashMap();
		when(entry.getProperties()).thenReturn(props);
		when(integrationKeyGenerator.generate(any(), any())).thenReturn("my_key");

		final String integrationKey = modelEntityService.addIntegrationKeyToODataEntry(mock(EdmEntitySet.class), entry);

		assertThat(integrationKey).isEqualTo("my_key");
		assertThat(entry.getProperties()).contains(entry("integrationKey", "my_key"));
	}

	@Test
	public void testItemFoundInStorageRequestContextTakesPrecedenceOverExistingItem() throws EdmException
	{
		final ItemModel item = mock(ItemModel.class);
		givenItemExists(item);
		givenStorageRequest(item);

		final ItemModel itemReturned = modelEntityService.createOrUpdateItem(request, createItemStrategy);
		assertThat(itemReturned).isEqualTo(item);
		verifyNoCallToPutItem();
	}

	private void givenItemExists(final ItemModel item) throws EdmException
	{
		when(itemLookupStrategy.lookup(any())).thenReturn(item);
	}

	@Test
	public void testLookupItemsFoundItems() throws EdmException
	{
		final ItemModel item = mock(ItemModel.class);
		final ItemLookupResult<ItemModel> searchResult = ItemLookupResult.createFrom(Collections.singletonList(item));
		when(itemLookupStrategy.lookupItems(any(ItemLookupRequest.class))).thenReturn(searchResult);

		final ItemLookupResult<ItemModel> result = modelEntityService.lookupItems(mock(ItemLookupRequest.class));

		assertThat(result.getEntries()).containsExactly(item);
	}

	@Test
	public void testLookupItemsNotFoundItems() throws EdmException
	{
		final ItemLookupResult<ItemModel> searchResult = ItemLookupResult.createFrom(Collections.emptyList());
		when(itemLookupStrategy.lookupItems(any(ItemLookupRequest.class))).thenReturn(searchResult);

		final ItemLookupResult<ItemModel> result = modelEntityService.lookupItems(mock(ItemLookupRequest.class));

		assertThat(result.getEntries()).isEmpty();
	}

	@Test
	public void getCount() throws EdmException
	{
		final Integer expectedCount = 5;
		when(itemLookupStrategy.count(any())).thenReturn(expectedCount);
		final Integer actualCount = modelEntityService.count(mock(ItemLookupRequest.class));
		assertThat(actualCount).isEqualTo(expectedCount);
	}

	private void verifyModelPopulator() throws EdmException
	{
		verify(entityModelPopulator).populateItem(any(), any());
	}

	private void verifyCallToCreateItem() throws EdmException
	{
		verify(createItemStrategy).createItem(any());
	}

	private void verifyNoCallToCreateItem() throws EdmException
	{
		verify(createItemStrategy, never()).createItem(any());
	}

	private void verifyCallToPutItem() throws EdmException
	{
		verify(request).putItem(any());
	}

	private void verifyNoCallToPutItem() throws EdmException
	{
		verify(request, never()).putItem(any());
	}

	private EdmEntitySet entitySet() throws EdmException
	{
		final EdmEntityType type = mock(EdmEntityType.class);
		final EdmEntitySet entitySet = mock(EdmEntitySet.class);
		when(entitySet.getEntityType()).thenReturn(type);
		return entitySet;
	}

	private void defaultStorageRequest() throws EdmException
	{
		final EdmEntitySet entitySet = entitySet();
		when(request.getEntitySet()).thenReturn(entitySet);
		when(request.getAcceptLocale()).thenReturn(Locale.getDefault());
		when(request.getODataEntry()).thenReturn(mock(ODataEntry.class));
		final EdmEntityType entityType = entitySet.getEntityType();
		when(request.getEntityType()).thenReturn(entityType);
		when(request.toLookupRequest()).thenReturn(mock(ItemLookupRequest.class));
	}

	private void givenStorageRequest() throws EdmException
	{
		when(request.getContextItem()).thenReturn(Optional.empty());
		defaultStorageRequest();
	}

	private void givenStorageRequest(final ItemModel itemModel) throws EdmException
	{
		when(request.getContextItem()).thenReturn(Optional.of(itemModel));
		defaultStorageRequest();
	}

	private CreateItemValidator[] givenItemValidators(final CreateItemValidator... validators)
	{
		createItemValidators.addAll(Arrays.asList(validators));
		return validators;
	}

	private CreateItemValidator passingValidator()
	{
		return mock(CreateItemValidator.class);
	}

	private CreateItemValidator failingLookupValidator() throws EdmException
	{
		final CreateItemValidator validator = mock(CreateItemValidator.class);
		doThrow(new EdmException(EdmException.COMMON))
				.when(validator).beforeItemLookup(any(EdmEntityType.class), any(ODataEntry.class));
		return validator;
	}

	private CreateItemValidator failingCreateValidator() throws EdmException
	{
		final CreateItemValidator validator = mock(CreateItemValidator.class);
		doThrow(new EdmException(EdmException.COMMON))
				.when(validator).beforeCreateItem(any(EdmEntityType.class), any(ODataEntry.class));
		return validator;
	}

	private CreateItemValidator failingPopulateValidator() throws EdmException
	{
		final CreateItemValidator validator = mock(CreateItemValidator.class);
		doThrow(new EdmException(EdmException.COMMON))
				.when(validator).beforePopulateItem(any(EdmEntityType.class), any(ODataEntry.class));
		return validator;
	}

	private void verifyAllValidatorsCalled(final CreateItemValidator[] validators)
	{
		Stream.of(validators).forEach(this::verifyAllInteractions);
	}

	private void verifyAllInteractions(final CreateItemValidator v)
	{
		try
		{
			verify(v).beforeItemLookup(any(), any());
			verify(v).beforeCreateItem(any(), any());
			verify(v).beforePopulateItem(any(), any());
		}
		catch (final EdmException e)
		{
			e.printStackTrace();
		}
	}

	private void verifyAllNonCreateValidatorsCalled(final CreateItemValidator[] validators)
	{
		Stream.of(validators).forEach(this::verifyAllInteractionsExceptCreate);
	}

	private void verifyAllInteractionsExceptCreate(final CreateItemValidator v)
	{
		try
		{
			verify(v).beforeItemLookup(any(), any());
			verify(v).beforePopulateItem(any(), any());
		}
		catch (final EdmException e)
		{
			e.printStackTrace();
		}
	}
}

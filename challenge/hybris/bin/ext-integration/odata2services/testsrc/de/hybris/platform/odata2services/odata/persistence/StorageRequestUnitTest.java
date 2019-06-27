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

package de.hybris.platform.odata2services.odata.persistence;

import static de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest.itemLookupRequestBuilder;
import static de.hybris.platform.odata2services.odata.persistence.StorageRequest.storageRequestBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class StorageRequestUnitTest
{
	private static final String postHook = "postHook";
	private static final String preHook = "preHook";
	private static final String code = "IntegrationObjectCode";
	private static final Locale contentLocale = Locale.GERMAN;
	private static final Locale acceptLocale = Locale.ENGLISH;
	private static final String entityTypeName = "EntityTypeName";
	private static final String integrationKey = "testIntegrationKey";


	@Mock
	private EdmEntityType entityType;
	@Mock
	private EdmEntitySet entitySet;
	@Mock
	private ODataEntry oDataEntry;

	@Before
	public void setUp() throws EdmException
	{
		when(entitySet.getEntityType()).thenReturn(entityType);
	}

	@Test
	public void testBuild_NullEntitySet()
	{
		assertThatThrownBy(() -> storageRequestBuilder()
				.withEntitySet(null)
				.withContentLocale(contentLocale)
				.withAcceptLocale(acceptLocale)
				.withODataEntry(oDataEntry)
				.withIntegrationObject(code)
				.build())
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testBuild_NullEntityType() throws EdmException
	{
		when(entitySet.getEntityType()).thenReturn(null);

		assertThatThrownBy(() -> storageRequestBuilder()
				.withEntitySet(entitySet)
				.withContentLocale(contentLocale)
				.withAcceptLocale(acceptLocale)
				.withODataEntry(oDataEntry)
				.withIntegrationObject(code)
				.build())
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testBuild_NullIntegrationObjectCode() throws EdmException
	{
		when(entitySet.getEntityType()).thenReturn(null);

		assertThatThrownBy(() -> storageRequestBuilder()
				.withEntitySet(entitySet)
				.withContentLocale(contentLocale)
				.withAcceptLocale(acceptLocale)
				.withODataEntry(oDataEntry)
				.withIntegrationObject(null)
				.build())
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testBuild_NullAcceptLocale() throws EdmException
	{
		when(entitySet.getEntityType()).thenReturn(null);

		assertThatThrownBy(() -> storageRequestBuilder()
				.withEntitySet(entitySet)
				.withContentLocale(contentLocale)
				.withAcceptLocale(null)
				.withODataEntry(oDataEntry)
				.withIntegrationObject(code)
				.build())
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testBuild_NullContentLocale() throws EdmException
	{
		when(entitySet.getEntityType()).thenReturn(null);

		assertThatThrownBy(() -> storageRequestBuilder()
				.withEntitySet(entitySet)
				.withContentLocale(null)
				.withAcceptLocale(acceptLocale)
				.withODataEntry(oDataEntry)
				.withIntegrationObject(code)
				.build())
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testBuild_NullEntry() throws EdmException
	{
		final StorageRequest request = storageRequestBuilder()
				.withEntitySet(entitySet)
				.withContentLocale(contentLocale)
				.withAcceptLocale(acceptLocale)
				.withODataEntry(null)
				.withIntegrationObject(code)
				.build();

		assertThat(request).isNotNull()
				.hasFieldOrPropertyWithValue("entitySet", entitySet)
				.hasFieldOrPropertyWithValue("entityType", entityType)
				.hasFieldOrPropertyWithValue("contentLocale", contentLocale)
				.hasFieldOrPropertyWithValue("acceptLocale", acceptLocale)
				.hasFieldOrPropertyWithValue("oDataEntry", null)
				.hasFieldOrPropertyWithValue("integrationObjectCode", code)
				.hasFieldOrPropertyWithValue("items", new HashMap<String, HashMap>());
	}

	@Test
	public void testBuild_WithEmptyHooks() throws EdmException
	{
		final StorageRequest request = storageRequestBuilder()
				.withEntitySet(entitySet)
				.withContentLocale(contentLocale)
				.withAcceptLocale(acceptLocale)
				.withODataEntry(oDataEntry)
				.withIntegrationObject(code)
				.build();

		assertThat(request).isNotNull()
				.hasFieldOrPropertyWithValue("entitySet", entitySet)
				.hasFieldOrPropertyWithValue("entityType", entityType)
				.hasFieldOrPropertyWithValue("contentLocale", contentLocale)
				.hasFieldOrPropertyWithValue("acceptLocale", acceptLocale)
				.hasFieldOrPropertyWithValue("oDataEntry", oDataEntry)
				.hasFieldOrPropertyWithValue("postPersistHook", "")
				.hasFieldOrPropertyWithValue("prePersistHook", "")
				.hasFieldOrPropertyWithValue("integrationObjectCode", code)
				.hasFieldOrPropertyWithValue("items", new HashMap<String, HashMap>());
	}

	@Test
	public void testBuild_Successful() throws EdmException
	{
		final StorageRequest request = storageRequestBuilder()
				.withEntitySet(entitySet)
				.withContentLocale(contentLocale)
				.withAcceptLocale(acceptLocale)
				.withODataEntry(oDataEntry)
				.withPostPersistHook(postHook)
				.withPrePersistHook(preHook)
				.withIntegrationObject(code)
				.build();

		assertThat(request).isNotNull()
				.hasFieldOrPropertyWithValue("entitySet", entitySet)
				.hasFieldOrPropertyWithValue("entityType", entityType)
				.hasFieldOrPropertyWithValue("contentLocale", contentLocale)
				.hasFieldOrPropertyWithValue("acceptLocale", acceptLocale)
				.hasFieldOrPropertyWithValue("oDataEntry", oDataEntry)
				.hasFieldOrPropertyWithValue("postPersistHook", postHook)
				.hasFieldOrPropertyWithValue("prePersistHook", preHook)
				.hasFieldOrPropertyWithValue("integrationObjectCode", code)
				.hasFieldOrPropertyWithValue("items", new HashMap<String, HashMap>());
	}

	@Test
	public void testBuild_Successful_itemLookup() throws EdmException
	{
		final ItemLookupRequest request = itemLookupRequestBuilder()
				.withEntitySet(entitySet)
				.withAcceptLocale(acceptLocale)
				.withODataEntry(oDataEntry)
				.withIntegrationObject(code)
				.build();

		assertThat(request).isNotNull()
				.hasFieldOrPropertyWithValue("entitySet", entitySet)
				.hasFieldOrPropertyWithValue("entityType", entityType)
				.hasFieldOrPropertyWithValue("acceptLocale", acceptLocale)
				.hasFieldOrPropertyWithValue("oDataEntry", oDataEntry)
				.hasFieldOrPropertyWithValue("integrationObjectCode", code);
	}

	@Test
	public void testBuild_FromRequestSuccessful() throws EdmException
	{
		final Map<String, Map<String, ItemModel>> items = new HashMap<>();

		final StorageRequest preRequest = storageRequest();

		final StorageRequest request = storageRequestBuilder().from(preRequest).build();
		assertThat(request).isNotNull()
				.hasFieldOrPropertyWithValue("entitySet", entitySet)
				.hasFieldOrPropertyWithValue("entityType", entityType)
				.hasFieldOrPropertyWithValue("contentLocale", contentLocale)
				.hasFieldOrPropertyWithValue("acceptLocale", acceptLocale)
				.hasFieldOrPropertyWithValue("oDataEntry", oDataEntry)
				.hasFieldOrPropertyWithValue("postPersistHook", postHook)
				.hasFieldOrPropertyWithValue("prePersistHook", preHook)
				.hasFieldOrPropertyWithValue("integrationObjectCode", code)
				.hasFieldOrPropertyWithValue("items", items);
	}

	@Test
	public void testGetContextItemWhenNoMatchingEntityTypeFoundInMap() throws EdmException
	{
		final StorageRequest request = storageRequest();

		assertThat(request.getContextItem()).isEqualTo(Optional.empty());
	}

	@Test
	public void testGetContextItemWhenNoItemWithIntegrationKeyInMap() throws EdmException
	{
		final StorageRequest request = storageRequest();

		when(entityType.getName()).thenReturn(entityTypeName);
		final ItemModel itemModel = mock(ItemModel.class);
		request.putItem(itemModel);
		request.setIntegrationKey("differentKey");
		assertThat(request.getContextItem()).isEmpty();
	}

	@Test
	public void testGetContextItemWhenItemHasBeenPutInMap() throws EdmException
	{
		final StorageRequest request = storageRequest();

		when(entityType.getName()).thenReturn(entityTypeName);
		final ItemModel itemModel = mock(ItemModel.class);
		request.putItem(itemModel);
		assertThat(request.getContextItem()).isEqualTo(Optional.of(itemModel));
	}

	private StorageRequest storageRequest() throws EdmException
	{
		return storageRequestBuilder()
				.withEntitySet(entitySet)
				.withContentLocale(contentLocale)
				.withAcceptLocale(acceptLocale)
				.withODataEntry(oDataEntry)
				.withPostPersistHook(postHook)
				.withPrePersistHook(preHook)
				.withIntegrationObject(code)
				.withIntegrationKey(integrationKey)
				.build();
	}
}

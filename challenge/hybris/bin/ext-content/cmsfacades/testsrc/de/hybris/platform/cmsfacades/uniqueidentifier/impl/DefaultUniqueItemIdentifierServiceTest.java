/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.uniqueidentifier.impl;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultUniqueItemIdentifierServiceTest
{

	private static final java.lang.String SUPER_TYPE = "SUPER_TYPE";
	private static final java.lang.String TYPE = "TYPE";
	@Mock
	private TypeService typeService;

	@Mock
	private UniqueIdentifierConverter converterForType;

	@Mock
	private UniqueIdentifierConverter converterForSuperType;

	private Set<UniqueIdentifierConverter> converters = new HashSet<>();
	
	@InjectMocks
	private DefaultUniqueItemIdentifierService service;

	private ComposedTypeModel composedType = mock(ComposedTypeModel.class);
	private ComposedTypeModel superComposedType = mock(ComposedTypeModel.class);
	private ComposedTypeModel otherComposedType =mock(ComposedTypeModel.class);
	
	@Mock
	private ItemModel itemModel;
	@Mock
	private ItemData itemData;
	private String itemId = "itemId";
	
	private Collection<ComposedTypeModel> superTypes = new ArrayList();

	@Before
	public void setup() throws Exception
	{
		when(itemData.getItemId()).thenReturn(itemId);
		when(itemData.getItemType()).thenReturn(TYPE);
		
		// otherComposedType mocks
		when(otherComposedType.getAllSuperTypes()).thenReturn(Arrays.asList());

		// superComposedType mock
		when(superComposedType.getCode()).thenReturn(SUPER_TYPE);
		superTypes.add(superComposedType);
		
		// composedType mocks
		when(composedType.getCode()).thenReturn(TYPE);
		when(composedType.getAllSuperTypes()).thenReturn(superTypes);
		
		
		// typeService mocks
		when(typeService.getComposedTypeForCode(TYPE)).thenReturn(composedType);
		
		// typeFunction mocks
		when(converterForType.convert(itemModel)).thenReturn(itemData);
		when(converterForType.convert(itemData)).thenReturn(itemModel);
		when(converterForType.getItemType()).thenReturn(TYPE);

		// superTypeFunction mocks
		when(converterForSuperType.convert(itemModel)).thenReturn(itemData);
		when(converterForSuperType.getItemType()).thenReturn(SUPER_TYPE);

		// converters mocks
		converters.add(converterForType);
		converters.add(converterForSuperType);
		service.setUniqueIdentifierConverters(converters);

		// itemModel mocks
		when(itemModel.getItemtype()).thenReturn(TYPE);

		service.afterPropertiesSet();
	}

	@Test
	public void testWhenConverterExistsForItem()
	{
		final Optional<ItemData> uniqueItemData = service.getItemData(itemModel);
		assertThat(uniqueItemData, notNullValue());
		assertThat(uniqueItemData.isPresent(), is(true));
		assertThat(uniqueItemData.get(), is(itemData));
	}


	@Test
	public void testWhenConverterDoesNotExistForItem()
	{
		when(itemModel.getItemtype()).thenReturn("converter-not-present");
		when(typeService.getComposedTypeForCode(anyString())).thenReturn(otherComposedType);
		
		final Optional<ItemData> uniqueItemData = service.getItemData(itemModel);
		assertThat(uniqueItemData, notNullValue());
		assertThat(uniqueItemData.isPresent(), is(false));
	}


	@Test
	public void testWhenConverterIsPresentForSuperTpe()
	{
		when(itemModel.getItemtype()).thenReturn(TYPE);
		service.getConverterMap().remove(TYPE);
		
		final Optional<ItemData> uniqueItemData = service.getItemData(itemModel);
		assertThat(uniqueItemData, notNullValue());
		assertThat(uniqueItemData.isPresent(), is(true));
		assertThat(uniqueItemData.get(), is(itemData));
	}
	
	@Test
	public void testWhenConverterExistsForItemData()
	{
		final Optional<ItemModel> uniqueItemModel = service.getItemModel(itemData);
		assertThat(uniqueItemModel, notNullValue());
		assertThat(uniqueItemModel.isPresent(), is(true));
		assertThat(uniqueItemModel.get(), is(itemModel));
	}
	
	@Test
	public void testWhenConverterDoesNotExistForItemData()
	{
		when(itemData.getItemType()).thenReturn("converter-not-present");
		when(typeService.getComposedTypeForCode(anyString())).thenReturn(otherComposedType);
		
		final Optional<ItemModel> uniqueItemModel = service.getItemModel(itemData);
		assertThat(uniqueItemModel, notNullValue());
		assertThat(uniqueItemModel.isPresent(), is(false));
	}

	
	@Test
	public void testWhenConverterToItemDataIsPresentForSuperType()
	{
		service.getConverterMap().remove(TYPE);
		
		final Optional<ItemData> uniqueItemData = service.getItemData(itemModel);
		assertThat(uniqueItemData, notNullValue());
		assertThat(uniqueItemData.isPresent(), is(true));
		assertThat(uniqueItemData.get(), is(itemData));
	}


}

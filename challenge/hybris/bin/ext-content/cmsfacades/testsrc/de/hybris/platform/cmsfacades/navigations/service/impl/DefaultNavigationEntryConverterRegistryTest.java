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
package de.hybris.platform.cmsfacades.navigations.service.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryItemModelConverter;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
public class DefaultNavigationEntryConverterRegistryTest
{

	private static final String ITEM_TYPE_1 = "itemType1";
	private static final String ITEM_TYPE_2 = "itemType2";
	private static final String ITEM_TYPE_3 = "itemType3";
	private static final String ITEM_SUPER_TYPE = "superType";
	private static final String INVALID_TYPE = "invalid";

	@InjectMocks
	private DefaultNavigationEntryConverterRegistry defaultNavigationEntryConverterRegistry;

	@Mock
	private NavigationEntryItemModelConverter itemConverter1;

	@Mock
	private NavigationEntryItemModelConverter itemConverter2;

	@Mock
	private NavigationEntryItemModelConverter itemConverter3;

	@Mock
	private TypeService typeService;

	@Before
	public void setup() throws Exception
	{

		when(typeService.getComposedTypeForCode(INVALID_TYPE)).thenReturn(mock(ComposedTypeModel.class));

		final ComposedTypeModel composedType = mock(ComposedTypeModel.class);
		final ComposedTypeModel composedSuperTypeModel = mock(ComposedTypeModel.class);
		when(composedSuperTypeModel.getCode()).thenReturn(ITEM_SUPER_TYPE);
		when(composedType.getAllSuperTypes()).thenReturn(Arrays.asList(composedSuperTypeModel));
		when(typeService.getComposedTypeForCode(ITEM_TYPE_3)).thenReturn(composedType);

		when(itemConverter1.getItemType()).thenReturn(ITEM_TYPE_1);
		when(itemConverter2.getItemType()).thenReturn(ITEM_TYPE_2);
		when(itemConverter3.getItemType()).thenReturn(ITEM_SUPER_TYPE);
		final Set<NavigationEntryItemModelConverter> itemModelConverters = new HashSet<>(
				Arrays.asList(itemConverter1, itemConverter2, itemConverter3));
		defaultNavigationEntryConverterRegistry.setNavigationEntryItemModelConverters(itemModelConverters);

		defaultNavigationEntryConverterRegistry.afterPropertiesSet();
	}

	@Test
	public void testRegistryInitializationWillPopulateCorrectConverters()
	{
		assertThat(defaultNavigationEntryConverterRegistry.getNavigationEntryItemModelConverter(ITEM_TYPE_1).isPresent(), is(true));
		assertThat(defaultNavigationEntryConverterRegistry.getNavigationEntryItemModelConverter(ITEM_TYPE_2).isPresent(), is(true));

	}

	@Test
	public void testGetConverterForValidItemType()
	{
		final Optional<NavigationEntryItemModelConverter> navigationEntryItemModelConverter = defaultNavigationEntryConverterRegistry
				.getNavigationEntryItemModelConverter(ITEM_TYPE_1);
		assertThat(navigationEntryItemModelConverter.isPresent(), is(true));
	}


	@Test
	public void testGetConverterForInvalidItemType()
	{
		final Optional<NavigationEntryItemModelConverter> navigationEntryItemModelConverter = defaultNavigationEntryConverterRegistry
				.getNavigationEntryItemModelConverter(INVALID_TYPE);
		assertThat(navigationEntryItemModelConverter.isPresent(), is(false));
	}

	@Test
	public void testNavigationEntryTypesIsNotEmpty()
	{
		final Optional<List<String>> supportedItemTypes = defaultNavigationEntryConverterRegistry.getSupportedItemTypes();
		assertThat(supportedItemTypes, notNullValue());
		assertThat(supportedItemTypes.isPresent(), is(true));
		assertThat(supportedItemTypes.get().size(), is(3));
	}

	@Test
	public void testGetConverterForItemSuperType()
	{
		final Optional<NavigationEntryItemModelConverter> navigationEntryItemModelConverter = defaultNavigationEntryConverterRegistry
				.getNavigationEntryItemModelConverter(ITEM_TYPE_3);
		assertThat(navigationEntryItemModelConverter.isPresent(), is(true));
	}
}

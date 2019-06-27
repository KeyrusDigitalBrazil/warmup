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


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryConverterRegistry;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryItemModelConverter;
import de.hybris.platform.core.model.ItemModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Validator;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNavigationEntryServiceTest
{


	private static final String NODE_1 = "node1";
	private static final String ITEM_SUPER_TYPE = "Media";
	private static final String TYPE_1 = "type1";
	private static final String TYPE_2 = "type2";

	@Mock
	private CMSNavigationService navigationService;
	@Mock
	private NavigationEntryConverterRegistry navigationEntryConverterRegistry;
	@Mock
	private FacadeValidationService facadeValidationService;
	@Mock
	private Validator createNavigationEntryValidator;

	@InjectMocks
	private DefaultNavigationEntryService defaultNavigationEntryService;

	@Mock
	private CMSNavigationNodeModel node1;

	@Mock
	private CMSNavigationEntryModel entry1;
	@Mock
	private CMSNavigationEntryModel entry2;

	@Before
	public void setup() throws CMSItemNotFoundException
	{

		// entries
		final List<CMSNavigationEntryModel> entries = new ArrayList<>();
		entries.addAll(Arrays.asList(entry1, entry2));

		// node1
		when(node1.getEntries()).thenReturn(entries);

		// services
		when(navigationService.getNavigationNodeForId(NODE_1)).thenReturn(node1);
		final Optional<List<String>> supportedTypes = Optional.of(Arrays.asList(TYPE_1, TYPE_2));
		when(navigationEntryConverterRegistry.getSupportedItemTypes()).thenReturn(supportedTypes);
	}

	@Test
	public void testCreateNavigationEntry_shouldSaveSuccessfully() throws CMSItemNotFoundException
	{
		final NavigationEntryData navigationEntryData = createNavigationEntry();
		final NavigationEntryItemModelConverter converter = mock(NavigationEntryItemModelConverter.class);
		final Function<NavigationEntryData, ItemModel> conversionFunction = mock(Function.class);
		final ItemModel itemModel = mock(ItemModel.class);
		when(conversionFunction.apply(navigationEntryData)).thenReturn(itemModel);
		when(converter.getConverter()).thenReturn(conversionFunction);
		when(navigationEntryConverterRegistry.getNavigationEntryItemModelConverter(any())).thenReturn(Optional.of(converter));
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		defaultNavigationEntryService.createNavigationEntry(navigationEntryData, catalogVersion);

		verify(navigationService).createCmsNavigationEntry(catalogVersion, itemModel);
	}

	@Test
	public void testDeleteNavigationEntriesWithEmptyEntryList() throws CMSItemNotFoundException
	{
		// node1
		when(node1.getEntries()).thenReturn(new ArrayList<>());
		when(navigationService.removeNavigationEntryByUid(any(), any())).thenReturn(true);
		defaultNavigationEntryService.deleteNavigationEntries(NODE_1);
	}

	@Test
	public void testDeleteNavigationEntriesWithNonEmptyEntryList() throws CMSItemNotFoundException
	{
		// node1
		when(navigationService.removeNavigationEntryByUid(any(), any())).thenReturn(true);
		defaultNavigationEntryService.deleteNavigationEntries(NODE_1);
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void testDeleteNavigationEntriesWithNonEmptyEntryListAndNotDeleted() throws CMSItemNotFoundException
	{
		// node1
		when(navigationService.removeNavigationEntryByUid(any(), any())).thenReturn(false);
		defaultNavigationEntryService.deleteNavigationEntries(NODE_1);
	}



	protected NavigationEntryData createNavigationEntry()
	{
		final NavigationEntryData navigationEntryData = new NavigationEntryData();
		navigationEntryData.setItemSuperType(ITEM_SUPER_TYPE);
		return navigationEntryData;
	}
}

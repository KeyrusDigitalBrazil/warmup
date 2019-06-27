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
package de.hybris.platform.cmsfacades.navigations.impl;



import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.NavigationEntryTypeData;
import de.hybris.platform.cmsfacades.navigationentrytypes.impl.DefaultNavigationEntryTypesFacade;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryConverterRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNavigationEntryTypesFacadeTest
{

	private static final String TYPE_1 = "type1";
	private static final String TYPE_2 = "type2";

	@Mock
	private NavigationEntryConverterRegistry navigationEntryConverterRegistry;

	@InjectMocks
	private DefaultNavigationEntryTypesFacade defaultNavigationEntryTypesFacade;

	@Before
	public void setup()
	{
		final Optional<List<String>> supportedTypes = Optional.of(Arrays.asList(TYPE_1, TYPE_2));
		when(navigationEntryConverterRegistry.getSupportedItemTypes()).thenReturn(supportedTypes);
	}

	@Test
	public void testGetSupportedItemTypes()
	{
		final List<NavigationEntryTypeData> navigationEntryTypes = defaultNavigationEntryTypesFacade.getNavigationEntryTypes();
		assertThat(navigationEntryTypes.size(), is(2));
		assertThat(navigationEntryTypes.get(0).getItemType(), is(TYPE_1));
		assertThat(navigationEntryTypes.get(1).getItemType(), is(TYPE_2));
	}
}

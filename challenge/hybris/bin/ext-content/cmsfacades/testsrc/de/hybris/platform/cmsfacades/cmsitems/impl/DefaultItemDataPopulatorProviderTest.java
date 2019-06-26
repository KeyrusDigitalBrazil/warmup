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
package de.hybris.platform.cmsfacades.cmsitems.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.converters.Populator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultItemDataPopulatorProviderTest
{
	@InjectMocks
	private DefaultItemDataPopulatorProvider defaultItemDataPopulatorProvider;

	private Map<Predicate<CMSItemModel>, List<Populator<CMSItemModel, Map<String, Object>>>> itemDataPredicatePopulatorListMap = new HashMap<>();

	@Mock
	private Populator<CMSItemModel, Map<String, Object>> populator1;
	@Mock
	private Populator<CMSItemModel, Map<String, Object>> populator2;
	@Mock
	private Populator<CMSItemModel, Map<String, Object>> populator3;


	@Mock
	private Predicate<CMSItemModel> predicate1;

	@Mock
	private Predicate<CMSItemModel> predicate2;


	@Mock
	private CMSItemModel itemModel;

	@Test
	public void testWhenAllPredicatesReturnFalse_shouldReturnEmptyList()
	{
		// GIVEN
		itemDataPredicatePopulatorListMap.put(predicate1, Arrays.asList(populator1, populator2));
		defaultItemDataPopulatorProvider.setItemDataPredicatePopulatorListMap(itemDataPredicatePopulatorListMap);
		when(predicate1.test(itemModel)).thenReturn(false);

		// WHEN
		List<?> populators = defaultItemDataPopulatorProvider.getItemDataPopulators(itemModel);

		// THEN
		Assert.assertThat(populators.isEmpty(), is(true));
	}

	@Test
	public void testWhenOnePredicateReturnsTrue_shouldReturnPopulatorsForThisPredicate()
	{
		// GIVEN
		itemDataPredicatePopulatorListMap.put(predicate1, Arrays.asList(populator1));
		itemDataPredicatePopulatorListMap.put(predicate2, Arrays.asList(populator2, populator3));
		defaultItemDataPopulatorProvider.setItemDataPredicatePopulatorListMap(itemDataPredicatePopulatorListMap);
		when(predicate1.test(itemModel)).thenReturn(false);
		when(predicate2.test(itemModel)).thenReturn(true);

		// WHEN
		List<?> populators = defaultItemDataPopulatorProvider.getItemDataPopulators(itemModel);

		// THEN
		Assert.assertThat(populators.isEmpty(), is(false));
		Assert.assertThat(populators.containsAll(Arrays.asList(populator3, populator2)), is(true));
	}

	@Test
	public void testWhenMoreThatOnePredicateReturnTrue_shouldReturnPopulatorsForAllPredicates()
	{
		// GIVEN
		itemDataPredicatePopulatorListMap.put(predicate1, Arrays.asList(populator1));
		itemDataPredicatePopulatorListMap.put(predicate2, Arrays.asList(populator2, populator3));
		defaultItemDataPopulatorProvider.setItemDataPredicatePopulatorListMap(itemDataPredicatePopulatorListMap);
		when(predicate1.test(itemModel)).thenReturn(true);
		when(predicate2.test(itemModel)).thenReturn(true);

		// WHEN
		List<?> populators = defaultItemDataPopulatorProvider.getItemDataPopulators(itemModel);

		// THEN
		Assert.assertThat(populators.isEmpty(), is(false));
		Assert.assertThat(populators.containsAll(Arrays.asList(populator3, populator1, populator2)), is(true));
	}
}

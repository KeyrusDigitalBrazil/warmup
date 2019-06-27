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
package de.hybris.platform.cms2.cloning.service.predicate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSItemCloneablePredicateTest
{
	private static final String ITEM_TYPE_1 = "type123";
	private static final String ITEM_TYPE_2 = "type456";
	private static final String BLACKLIST_TYPE_1 = "blacklistType456";
	private static final String BLACKLIST_TYPE_2 = "blacklistType789";
	private static final String CLONEABLE_BLACKLIST_TYPE = BLACKLIST_TYPE_2;

	private static final String NON_CLONEABLE_TYPE = "nonCloneable";

	@InjectMocks
	private CMSItemCloneablePredicate predicate;

	@Mock
	private TypeService typeService;

	@Mock
	private List<String> typeNonCloneableList;

	@Mock
	private Set<String> blacklistedTypeSet;

	@Mock
	private Set<String> typeBlacklistCloneableSet;

	@Mock
	private ItemModel itemModel;

	@Before
	public void setup()
	{
		when(blacklistedTypeSet.stream()).thenReturn(Stream.of(BLACKLIST_TYPE_1, BLACKLIST_TYPE_2));
		when(typeNonCloneableList.stream()).thenReturn(Stream.of(NON_CLONEABLE_TYPE));
		when(typeBlacklistCloneableSet.stream()).thenReturn(Stream.of(CLONEABLE_BLACKLIST_TYPE));
	}

	@Test
	public void testItemModelIsNull()
	{
		assertThat(predicate.test(null), is(false));
	}

	@Test
	public void testItemModelIsCloneable()
	{
		// GIVEN
		when(itemModel.getItemtype()).thenReturn(ITEM_TYPE_1);

		// THEN
		assertThat(predicate.test(itemModel), is(true));
	}

	@Test
	public void testItemModelIsNotCloneable()
	{
		// GIVEN
		when(itemModel.getItemtype()).thenReturn(ITEM_TYPE_2);
		when(typeService.isAssignableFrom(NON_CLONEABLE_TYPE, ITEM_TYPE_2)).thenReturn(true);

		// THEN
		assertThat(predicate.test(itemModel), is(false));
	}

	@Test
	public void testItemModelIsBlacklisted()
	{
		// GIVEN
		when(itemModel.getItemtype()).thenReturn(BLACKLIST_TYPE_1);

		// THEN
		assertThat(predicate.test(itemModel), is(false));
	}

	@Test
	public void testItemModelIsBlacklistedAndCloneable()
	{
		// GIVEN
		when(itemModel.getItemtype()).thenReturn(BLACKLIST_TYPE_2);
		when(typeService.isAssignableFrom(NON_CLONEABLE_TYPE, BLACKLIST_TYPE_2)).thenReturn(true);

		// THEN
		assertThat(predicate.test(itemModel), is(true));
	}
}

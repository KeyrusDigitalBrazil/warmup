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
package de.hybris.platform.cms2.version.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.core.model.ItemModel;

import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractPageTypePredicateTest
{
	private static final String PAGE_ITEM_TYPE = "some page item type";
	private static final String NON_PAGE_ITEM_TYPE = "some other item type";

	@Mock
	private TypeService typeService;

	@Mock
	private ItemModel nonPageModel;

	@Mock
	private AbstractPageModel pageModel;

	@InjectMocks
	private AbstractPageTypePredicate abstractPageTypePredicate;

	@Before
	public void setUp()
	{
		when(pageModel.getItemtype()).thenReturn(PAGE_ITEM_TYPE);
		when(nonPageModel.getItemtype()).thenReturn(NON_PAGE_ITEM_TYPE);

		when(typeService.isAssignableFrom(AbstractPageModel._TYPECODE, PAGE_ITEM_TYPE)).thenReturn(true);
		when(typeService.isAssignableFrom(AbstractPageModel._TYPECODE, NON_PAGE_ITEM_TYPE)).thenReturn(false);
	}

	@Test
	public void givenItemIsNotPage_WhenTestIsCalled_ThenItReturnsFalse()
	{
		// WHEN
		final boolean isPage = abstractPageTypePredicate.test(nonPageModel);

		// THEN
		assertFalse(isPage);
	}

	@Test
	public void givenItemIsPage_WhenTestIsCalled_ThenItReturnsTrue()
	{
		// WHEN
		final boolean isPage = abstractPageTypePredicate.test(pageModel);

		// THEN
		assertTrue(isPage);
	}

}

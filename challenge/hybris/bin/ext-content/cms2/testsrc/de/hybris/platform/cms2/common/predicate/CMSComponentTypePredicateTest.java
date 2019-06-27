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
package de.hybris.platform.cms2.common.predicate;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSComponentTypePredicateTest
{
	private static final String COMPONENT_ITEM_TYPE = "componentItemType";
	private static final String NON_COMPONENT_ITEM_TYPE = "nonComponentItemType";

	@Mock
	private TypeService typeService;

	@Mock
	private ItemModel nonComponentModel;

	@Mock
	private AbstractCMSComponentModel componentModel;

	@InjectMocks
	private CMSComponentTypePredicate cmsComponentTypePredicate;

	@Before
	public void setUp()
	{
		when(componentModel.getItemtype()).thenReturn(COMPONENT_ITEM_TYPE);
		when(nonComponentModel.getItemtype()).thenReturn(NON_COMPONENT_ITEM_TYPE);

		when(typeService.isAssignableFrom(AbstractCMSComponentModel._TYPECODE, COMPONENT_ITEM_TYPE)).thenReturn(true);
		when(typeService.isAssignableFrom(AbstractCMSComponentModel._TYPECODE, NON_COMPONENT_ITEM_TYPE)).thenReturn(false);
	}

	@Test
	public void givenItemIsNotComponent_WhenTestIsCalled_ThenItReturnsFalse()
	{
		// WHEN
		final boolean isComponent = cmsComponentTypePredicate.test(nonComponentModel);

		// THEN
		assertFalse(isComponent);
	}

	@Test
	public void givenItemIsComponent_WhenTestIsCalled_ThenItReturnsTrue()
	{
		// WHEN
		final boolean isComponent = cmsComponentTypePredicate.test(componentModel);

		// THEN
		assertTrue(isComponent);
	}
}

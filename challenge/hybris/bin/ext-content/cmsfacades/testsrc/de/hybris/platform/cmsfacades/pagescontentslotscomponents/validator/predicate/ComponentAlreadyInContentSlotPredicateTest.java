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
package de.hybris.platform.cmsfacades.pagescontentslotscomponents.validator.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cmsfacades.dto.ComponentAndContentSlotValidationDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ComponentAlreadyInContentSlotPredicateTest
{
	private final Predicate<ComponentAndContentSlotValidationDto> predicate = new ComponentAlreadyInContentSlotPredicate();

	@Mock
	private AbstractCMSComponentModel component;
	@Mock
	private ContentSlotModel contentSlot;

	private ComponentAndContentSlotValidationDto target;

	@Before
	public void setUp()
	{
		target = new ComponentAndContentSlotValidationDto();
		target.setComponent(component);
		target.setContentSlot(contentSlot);

		when(contentSlot.getCmsComponents()).thenReturn(Collections.emptyList());
	}

	@Test
	public void shouldPass_ComponentAlreadyInSlot()
	{
		when(contentSlot.getCmsComponents()).thenReturn(Collections.singletonList(component));

		final boolean result = predicate.test(target);
		assertTrue(result);
	}

	@Test
	public void shouldFail_NoComponentsInSlot()
	{
		final boolean result = predicate.test(target);
		assertFalse(result);
	}

	@Test
	public void shouldFail_ComponentNotInSlot()
	{
		final AbstractCMSComponentModel component1 = Mockito.mock(AbstractCMSComponentModel.class);
		final AbstractCMSComponentModel component2 = Mockito.mock(AbstractCMSComponentModel.class);

		when(contentSlot.getCmsComponents()).thenReturn(Arrays.asList(component1, component2));

		final boolean result = predicate.test(target);
		assertFalse(result);
	}
}

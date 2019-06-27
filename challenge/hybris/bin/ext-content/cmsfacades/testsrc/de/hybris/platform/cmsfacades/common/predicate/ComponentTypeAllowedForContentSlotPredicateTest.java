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
package de.hybris.platform.cmsfacades.common.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.CMSComponentTypeModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminTypeRestrictionsService;
import de.hybris.platform.cmsfacades.dto.ComponentTypeAndContentSlotValidationDto;

import java.util.Collections;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ComponentTypeAllowedForContentSlotPredicateTest
{
	private static final String VALID_TYPE = "valid-type";
	private static final String INVALID_TYPE = "invalid-type";

	@InjectMocks
	private final Predicate<ComponentTypeAndContentSlotValidationDto> predicate = new ComponentTypeAllowedForContentSlotPredicate();

	@Mock
	private CMSAdminTypeRestrictionsService cmsAdminTypeRestrictionsService;
	@Mock
	private ContentSlotModel contentSlot;
	@Mock
	private CMSComponentTypeModel componentType;
	@Mock
	private AbstractPageModel page;

	private ComponentTypeAndContentSlotValidationDto target;

	@Before
	public void setUp() throws CMSItemNotFoundException
	{
		target = new ComponentTypeAndContentSlotValidationDto();
		target.setComponentType(VALID_TYPE);
		target.setContentSlot(contentSlot);
		target.setPage(page);

		when(cmsAdminTypeRestrictionsService.getTypeRestrictionsForContentSlot(page, contentSlot))
		.thenReturn(Collections.singleton(componentType));
		when(componentType.getCode()).thenReturn(VALID_TYPE);
	}

	@Test
	public void shouldPass_ComponentTypeInRestrictions()
	{
		final boolean result = predicate.test(target);
		assertTrue(result);
	}

	@Test
	public void shouldFail_ComponentTypeNotInRestrictions()
	{
		when(componentType.getCode()).thenReturn(INVALID_TYPE);

		final boolean result = predicate.test(target);
		assertFalse(result);
	}

	@Test
	public void shouldFail_GetRestrictionsThrowsException() throws CMSItemNotFoundException
	{
		when(cmsAdminTypeRestrictionsService.getTypeRestrictionsForContentSlot(page, contentSlot))
		.thenThrow(new CMSItemNotFoundException("exception"));

		final boolean result = predicate.test(target);
		assertFalse(result);
	}
}

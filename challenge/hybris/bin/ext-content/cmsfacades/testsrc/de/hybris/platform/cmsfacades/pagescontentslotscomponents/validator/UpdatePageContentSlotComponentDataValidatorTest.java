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
package de.hybris.platform.cmsfacades.pagescontentslotscomponents.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminComponentService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cmsfacades.common.validator.ValidationDtoFactory;
import de.hybris.platform.cmsfacades.data.PageContentSlotComponentData;
import de.hybris.platform.cmsfacades.dto.ComponentAndContentSlotValidationDto;
import de.hybris.platform.cmsfacades.dto.ComponentTypeAndContentSlotValidationDto;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdatePageContentSlotComponentDataValidatorTest
{
	private static final int INDEX = 3;
	private static final int INVALID_INDEX = -3;
	private static final String SLOT_ID = "slot-id";
	private static final String COMPONENT_ID = "component-id";
	private static final String PAGE_ID = "page-id";
	private static final String INVALID = "invalid";

	@InjectMocks
	private final Validator validator = new UpdatePageContentSlotComponentValidator();

	@Mock
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	@Mock
	private CMSAdminComponentService cmsAdminComponentService;
	@Mock
	private Predicate<String> componentExistsInCatalogVersionsPredicate;
	@Mock
	private Predicate<String> pageExistsPredicate;
	@Mock
	private Predicate<String> contentSlotExistsInCatalogVersionsPredicate;
	@Mock
	private Predicate<ComponentTypeAndContentSlotValidationDto> componentTypeAllowedForContentSlotPredicate;
	@Mock
	private ValidationDtoFactory validationDtoFactory;

	@Mock
	private ContentSlotModel contentSlot;
	@Mock
	private AbstractCMSComponentModel component;
	@Mock
	private ComponentAndContentSlotValidationDto componentAndContentSlotValidationDto;
	@Mock
	private ComponentTypeAndContentSlotValidationDto componentTypeAndContentSlotValidationDto;

	private PageContentSlotComponentData target;
	private Errors errors;

	@Before
	public void setUp()
	{
		target = new PageContentSlotComponentData();
		target.setComponentId(COMPONENT_ID);
		target.setSlotId(SLOT_ID);
		target.setPosition(INDEX);
		target.setPageId(PAGE_ID);

		errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());

		when(componentExistsInCatalogVersionsPredicate.test(COMPONENT_ID)).thenReturn(Boolean.TRUE);
		when(componentExistsInCatalogVersionsPredicate.test(INVALID)).thenReturn(Boolean.FALSE);

		when(pageExistsPredicate.test(PAGE_ID)).thenReturn(Boolean.TRUE);
		when(pageExistsPredicate.test(INVALID)).thenReturn(Boolean.FALSE);

		when(cmsAdminContentSlotService.getContentSlotForId(SLOT_ID)).thenReturn(contentSlot);
		when(cmsAdminComponentService.getCMSComponentForId(COMPONENT_ID)).thenReturn(component);

		when(contentSlotExistsInCatalogVersionsPredicate.test(SLOT_ID)).thenReturn(Boolean.TRUE);
		when(contentSlotExistsInCatalogVersionsPredicate.test(INVALID)).thenReturn(Boolean.FALSE);

		when(componentTypeAllowedForContentSlotPredicate.test(Mockito.any(ComponentTypeAndContentSlotValidationDto.class)))
		.thenReturn(Boolean.TRUE);

		when(componentAndContentSlotValidationDto.getComponent()).thenReturn(component);
		when(validationDtoFactory.buildComponentAndContentSlotValidationDto(Mockito.any(), Mockito.any()))
		.thenReturn(componentAndContentSlotValidationDto);
		when(validationDtoFactory.buildComponentTypeAndContentSlotValidationDto(Mockito.any(), Mockito.any(), Mockito.any()))
		.thenReturn(componentTypeAndContentSlotValidationDto);
	}

	@Test
	public void shouldHaveNoFailures()
	{
		validator.validate(target, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void shouldFail_PageIdNull()
	{
		target.setPageId(null);

		validator.validate(target, errors);
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_InvalidPageId()
	{
		target.setPageId(INVALID);

		validator.validate(target, errors);
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_SlotIdNull()
	{
		target.setSlotId(null);

		validator.validate(target, errors);
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_InvalidSlotId()
	{
		target.setSlotId(INVALID);

		validator.validate(target, errors);
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_ComponentIdNull()
	{
		target.setComponentId(null);

		validator.validate(target, errors);
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_IndexNull()
	{
		target.setPosition(null);

		validator.validate(target, errors);
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_InvalidIndex()
	{
		target.setPosition(INVALID_INDEX);

		validator.validate(target, errors);
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_ComponentDoesNotExist()
	{
		target.setComponentId(INVALID);

		validator.validate(target, errors);
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_ComponentTypeNotAllowed()
	{
		when(componentTypeAllowedForContentSlotPredicate.test(Mockito.any(ComponentTypeAndContentSlotValidationDto.class)))
		.thenReturn(Boolean.FALSE);

		validator.validate(target, errors);
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
	}
}

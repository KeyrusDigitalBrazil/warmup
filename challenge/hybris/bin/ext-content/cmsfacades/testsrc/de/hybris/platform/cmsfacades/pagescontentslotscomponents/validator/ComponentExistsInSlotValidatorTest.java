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
public class ComponentExistsInSlotValidatorTest
{
	private static final int INDEX = 3;
	private static final String SLOT_ID = "slot-id";
	private static final String COMPONENT_ID = "component-id";
	private static final String PAGE_ID = "page-id";

	@InjectMocks
	private final Validator validator = new ComponentExistsInSlotValidator();

	@Mock
	private Predicate<String> componentExistsPredicate;
	@Mock
	private Predicate<ComponentAndContentSlotValidationDto> componentAlreadyInContentSlotPredicate;
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

		when(componentExistsPredicate.test(COMPONENT_ID)).thenReturn(Boolean.TRUE);
		when(componentAlreadyInContentSlotPredicate.test(Mockito.any(ComponentAndContentSlotValidationDto.class)))
		.thenReturn(Boolean.FALSE);

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
	public void shouldFail_ComponentAlreadyInSlot()
	{
		when(componentAlreadyInContentSlotPredicate.test(Mockito.any(ComponentAndContentSlotValidationDto.class)))
		.thenReturn(Boolean.TRUE);

		validator.validate(target, errors);
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getFieldErrorCount());
	}

}

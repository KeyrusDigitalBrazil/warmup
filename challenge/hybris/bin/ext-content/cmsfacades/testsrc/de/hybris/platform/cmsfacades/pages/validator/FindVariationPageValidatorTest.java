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
package de.hybris.platform.cmsfacades.pages.validator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.common.predicate.TypeCodeExistsPredicate;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FindVariationPageValidatorTest
{
	private static final String DEFAULT_PAGE = "defaultPage";
	private static final String TYPECODE = "typeCode";

	@InjectMocks
	private FindVariationPageValidator validator;

	@Mock
	private TypeCodeExistsPredicate typeCodeExistsPredicate;
	@Mock
	private TypeService typeService;
	@Mock
	private ComposedTypeModel composedType;

	private AbstractPageData page;
	private Errors errors;

	@Before
	public void setUp()
	{
		page = new AbstractPageData();
		errors = new BeanPropertyBindingResult(page, page.getClass().getSimpleName());
	}

	@Test
	public void shouldValidateAllFields_Valid()
	{
		page.setTypeCode(ContentPageModel._TYPECODE);
		page.setDefaultPage(Boolean.TRUE);

		when(typeCodeExistsPredicate.test(page.getTypeCode())).thenReturn(Boolean.TRUE);
		when(typeService.getComposedTypeForCode(page.getTypeCode())).thenReturn(composedType);
		doAnswer((Answer<Class<?>>) invocationMock -> {
			return ContentPageModel.class;
		}).when(typeService).getModelClass(composedType);

		validator.validate(page, errors);

		verify(typeCodeExistsPredicate).test(page.getTypeCode());
		verify(typeService).getComposedTypeForCode(page.getTypeCode());
		verify(typeService).getModelClass(composedType);
		assertThat(errors.getFieldErrorCount(), is(0));
	}

	@Test
	public void shouldValidateAllFields_MissingTypecode()
	{
		page.setDefaultPage(Boolean.TRUE);

		validator.validate(page, errors);

		assertThat(errors.getFieldErrorCount(), is(1));
		assertThat(errors.getFieldError().getField(), is(TYPECODE));
	}

	@Test
	public void shouldValidateAllFields_MissingPrimary()
	{
		page.setTypeCode(ContentPageModel._TYPECODE);

		when(typeCodeExistsPredicate.test(page.getTypeCode())).thenReturn(Boolean.TRUE);
		when(typeService.getComposedTypeForCode(page.getTypeCode())).thenReturn(composedType);
		doAnswer((Answer<Class<?>>) invocationMock -> {
			return ContentPageModel.class;
		}).when(typeService).getModelClass(composedType);

		validator.validate(page, errors);

		assertThat(errors.getFieldErrorCount(), is(1));
		assertThat(errors.getFieldError().getField(), is(DEFAULT_PAGE));
	}

	@Test
	public void shouldValidateTypeCode_Invalid()
	{
		page.setTypeCode(CMSParagraphComponentModel._TYPECODE);
		page.setDefaultPage(Boolean.TRUE);

		when(typeCodeExistsPredicate.test(page.getTypeCode())).thenReturn(Boolean.TRUE);
		when(typeService.getComposedTypeForCode(page.getTypeCode())).thenReturn(composedType);
		doAnswer((Answer<Class<?>>) invocationMock -> {
			return CMSParagraphComponentModel.class;
		}).when(typeService).getModelClass(composedType);

		validator.validate(page, errors);

		assertThat(errors.getFieldErrorCount(), is(1));
		assertThat(errors.getFieldError().getField(), is(TYPECODE));
	}

	@Test
	public void shouldValidateTypeCode_UnsupportedType()
	{
		page.setTypeCode("invalid");
		page.setDefaultPage(Boolean.TRUE);

		when(typeCodeExistsPredicate.test(page.getTypeCode())).thenReturn(Boolean.FALSE);

		validator.validate(page, errors);

		verify(typeService, never()).getModelClass(composedType);
		assertThat(errors.getFieldErrorCount(), is(1));
		assertThat(errors.getFieldError().getField(), is(TYPECODE));
	}

	protected void reject(final String fieldName, final String errorCode, final String language, final Errors errors)
	{
		errors.rejectValue(fieldName, errorCode, new Object[]
				{ language }, null);
	}
}

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
package de.hybris.platform.cmsfacades.namedquery.validator;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.data.NamedQueryData;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NamedQueryDataValidatorTest
{
	private static final String NAMED_QUERY = "named-query";
	private static final String SORT = "code:ASC,url:DESC";
	private static final String PARAMS = "code:banner,description:banner,altText:banner";
	private static final String PAGE_SIZE = "5";
	private static final String CURRENT_PAGE = "1";
	private static final Class<?> QUERY_TYPE = MediaData.class;
	private static final Integer MAX_PAGE_SIZE = Integer.valueOf(10);

	@InjectMocks
	private final NamedQueryDataValidator validator = new NamedQueryDataValidator();

	@Mock
	private Predicate<String> namedQueryExistsPredicate;

	private NamedQueryData target;
	private Errors errors;

	@Before
	public void setUp()
	{
		target = new NamedQueryData();
		target.setCurrentPage(CURRENT_PAGE);
		target.setNamedQuery(NAMED_QUERY);
		target.setPageSize(PAGE_SIZE);
		target.setParams(PARAMS);
		target.setQueryType(QUERY_TYPE);
		target.setSort(SORT);

		errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());

		validator.setMaxPageSize(MAX_PAGE_SIZE);

		when(namedQueryExistsPredicate.test(NAMED_QUERY)).thenReturn(Boolean.TRUE);
	}

	@Test
	public void shouldHaveNoFailures_AllParamsPresent()
	{
		validator.validate(target, errors);
		assertFalse(errors.toString(), errors.hasErrors());
	}

	@Test
	public void shouldHaveNoFailures_NoSort()
	{
		target.setSort(null);

		validator.validate(target, errors);
		assertFalse(errors.toString(), errors.hasErrors());
	}

	@Test
	public void shouldHaveNoFailures_NoPageSize()
	{
		target.setPageSize(null);

		validator.validate(target, errors);
		assertFalse(errors.toString(), errors.hasErrors());
	}

	@Test
	public void shouldHaveNoFailures_NoCurrentPage()
	{
		target.setCurrentPage(null);

		validator.validate(target, errors);
		assertFalse(errors.toString(), errors.hasErrors());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_NoQueryType()
	{
		target.setQueryType(null);
		validator.validate(target, errors);
	}

	@Test
	public void shouldFail_NoNamedQuery()
	{
		target.setNamedQuery(null);
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_NamedQueryDoesNotExist()
	{
		when(namedQueryExistsPredicate.test(NAMED_QUERY)).thenReturn(Boolean.FALSE);

		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_PageSizeNaN()
	{
		target.setPageSize("NaN");
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_PageSizeTooSmall()
	{
		target.setPageSize("0");
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_PageSizeTooLarge()
	{
		target.setPageSize("11");
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_CurrentPageTooSmall()
	{
		target.setCurrentPage("-1");
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_CurrentPageNaN()
	{
		target.setCurrentPage("NaN");
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_SortInvalidFormat()
	{
		target.setSort("code-ASC");
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_SortInvalidParamName()
	{
		target.setSort("invalid:ASC");
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_SortInvalidSortDirection()
	{
		target.setSort("code:invalid");
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_ParamsNull()
	{
		target.setParams(null);
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_ParamsInvalidFormat()
	{
		target.setParams("code-banner");
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldHaveNoFailures_EmptyParamValue()
	{
		target.setParams("code:");
		validator.validate(target, errors);
		assertThat(errors.toString(), errors.getFieldErrors(), empty());
	}

	@Test
	public void shouldFail_ParamsInvalidParamName()
	{
		target.setParams("invalid:banner");
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

}

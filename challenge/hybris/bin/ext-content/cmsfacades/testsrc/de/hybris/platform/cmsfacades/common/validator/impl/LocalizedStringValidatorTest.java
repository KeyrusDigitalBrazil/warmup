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
package de.hybris.platform.cmsfacades.common.validator.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class LocalizedStringValidatorTest
{
	private static final String ENGLISH = Locale.ENGLISH.toString();
	private static final String NAME = "name";
	private static final String NAME_VALUE = "test-name-value";

	private final LocalizedStringValidator validator = new LocalizedStringValidator();

	private AbstractCMSComponentData data;
	private Errors errors;

	@Before
	public void setUp()
	{
		data = new AbstractCMSComponentData();
		errors = new BeanPropertyBindingResult(data, data.getClass().getSimpleName());
	}

	@Test
	public void shouldValidateString()
	{
		validator.validate(ENGLISH, NAME, NAME_VALUE, errors);

		assertThat(errors.getFieldErrorCount(), is(0));
	}

	@Test
	public void shouldValidateStringNull()
	{
		validator.validate(ENGLISH, NAME, null, errors);

		assertThat(errors.getFieldErrorCount(), is(1));
		assertThat(errors.getFieldError(NAME).getCode(), is(CmsfacadesConstants.FIELD_REQUIRED_L10N));
		assertThat(errors.getFieldError(NAME).getArguments()[0], is(ENGLISH));
	}

}

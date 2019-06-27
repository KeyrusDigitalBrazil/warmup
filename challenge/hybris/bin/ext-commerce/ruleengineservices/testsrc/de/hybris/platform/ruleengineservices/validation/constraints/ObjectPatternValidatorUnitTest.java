/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ruleengineservices.validation.constraints;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern.Flag;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ObjectPatternValidatorUnitTest
{
	private final static String REGEXP = "[A-Za-z0-9]*";

	private ObjectPatternValidator validator;
	@Mock
	private ObjectPattern parameters;
	@Mock
	private ConstraintValidatorContext context;

	@Before
	public void setUp()
	{
		validator = new ObjectPatternValidator();
		when(parameters.flags()).thenReturn(new Flag[]
		{ Flag.UNICODE_CASE });
		when(parameters.regexp()).thenReturn(REGEXP);
	}

	@Test
	public void testInitialize()
	{
		validator.initialize(parameters);
		verify(parameters, times(1)).regexp();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInitializeWrongRegexp()
	{
		when(parameters.regexp()).thenReturn("\\M");
		validator.initialize(parameters);
	}

	@Test
	public void testIsValidTrue()
	{
		validator.initialize(parameters);
		assertThat(validator.isValid("Az09", context)).isTrue();
	}

	@Test
	public void testIsValidFalse()
	{
		validator.initialize(parameters);
		assertThat(validator.isValid("Az0%", context)).isFalse();
	}

	@Test
	public void testIsValidTrueIfValueNull()
	{
		validator.initialize(parameters);
		assertThat(validator.isValid(null, context)).isTrue();
	}

}

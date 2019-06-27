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
package de.hybris.platform.platformbackoffice.validation.validators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.platformbackoffice.validation.annotations.HybrisEnumValueCode;

import javax.validation.ConstraintValidatorContext;

import org.junit.Test;


@UnitTest
public class HybrisEnumValueCodeValidatorTest
{

	private static final ConstraintValidatorContext ANY_CONSTRAINT_VALIDATOR_CONTEXT = null;

	@Test
	public void shouldValidateHybrisEnumValueCodeSuccessfully()
	{
		// given
		final HybrisEnumValueCode hybrisEnumValueCode = mock(HybrisEnumValueCode.class);
		given(hybrisEnumValueCode.value()).willReturn("someCode");

		final HybrisEnumValueCodeValidator validator = new HybrisEnumValueCodeValidator();
		validator.initialize(hybrisEnumValueCode);

		final HybrisEnumValue enumToValidate = createHybrisEnumValue("someType", "someCode");

		// when
		final boolean isValid = validator.isValid(enumToValidate, ANY_CONSTRAINT_VALIDATOR_CONTEXT);

		// then
		assertThat(isValid).isTrue();
	}

	@Test
	public void shouldValidateHybrisEnumValueCodeWithError()
	{
		// given
		final HybrisEnumValueCode hybrisEnumValueCode = mock(HybrisEnumValueCode.class);
		given(hybrisEnumValueCode.value()).willReturn("someOtherCode");

		final HybrisEnumValueCodeValidator validator = new HybrisEnumValueCodeValidator();
		validator.initialize(hybrisEnumValueCode);

		final HybrisEnumValue enumToValidate = createHybrisEnumValue("someType", "someCode");

		// when
		final boolean isValid = validator.isValid(enumToValidate, ANY_CONSTRAINT_VALIDATOR_CONTEXT);

		// then
		assertThat(isValid).isFalse();
	}

	private static HybrisEnumValue createHybrisEnumValue(final String type, final String code)
	{
		return new HybrisEnumValue()
		{
			@Override
			public String getType()
			{
				return type;
			}

			@Override
			public String getCode()
			{
				return code;
			}
		};
	}
}

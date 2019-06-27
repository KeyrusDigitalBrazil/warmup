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
package de.hybris.platform.platformbackoffice.validation.interceptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.platformbackoffice.validation.model.constraints.HybrisEnumValueCodeConstraintModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.validation.model.constraints.AttributeConstraintModel;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


@UnitTest
public class HybrisEnumValueCodeConstraintValidatorTest
{
	private static final InterceptorContext ANY_CTX = null;
	HybrisEnumValueCodeConstraintValidator validator = new HybrisEnumValueCodeConstraintValidator();

	@Test
	public void shouldPreventSavingConstraintWithEmptyValue()
	{
		// given
		InterceptorException caughtException = null;

		final HybrisEnumValueCodeConstraintModel constraint = mock(HybrisEnumValueCodeConstraintModel.class);
		given(constraint.getValue()).willReturn(StringUtils.EMPTY);

		// when
		try
		{
			validator.onValidate(constraint, ANY_CTX);
		}
		catch (InterceptorException e)
		{
			caughtException = e;
		}

		// then
		assertThat(caughtException).isNotNull()
				.hasMessage(String.format("[%s]:The value for a HybrisEnumValue code constraint is empty!", validator));
	}

	@Test
	public void shouldPreventSavingConstraintWithNullValue()
	{
		// given
		InterceptorException caughtException = null;

		final HybrisEnumValueCodeConstraintModel constraint = mock(HybrisEnumValueCodeConstraintModel.class);
		given(constraint.getValue()).willReturn(null);

		// when
		try
		{
			validator.onValidate(constraint, ANY_CTX);
		}
		catch (InterceptorException e)
		{
			caughtException = e;
		}

		// then
		assertThat(caughtException).isNotNull()
				.hasMessage(String.format("[%s]:The value for a HybrisEnumValue code constraint is empty!", validator));
	}

	@Test
	public void shouldAllowConstraintCreation()
	{
		// given
		InterceptorException caughtException = null;

		final HybrisEnumValueCodeConstraintModel constraint = mock(HybrisEnumValueCodeConstraintModel.class);
		given(constraint.getValue()).willReturn("acceptableNotEmptyValue");

		// when
		try
		{
			validator.onValidate(constraint, ANY_CTX);
		}
		catch (InterceptorException e)
		{
			caughtException = e;
		}

		// then
		assertThat(caughtException).isNull();
	}

	@Test
	public void shouldAllowCreationOfOtherModels()
	{
		// given
		InterceptorException caughtException = null;

		final AttributeConstraintModel constraint = mock(AttributeConstraintModel.class);

		// when
		try
		{
			validator.onValidate(constraint, ANY_CTX);
		}
		catch (InterceptorException e)
		{
			caughtException = e;
		}

		// then
		assertThat(caughtException).isNull();
	}
}

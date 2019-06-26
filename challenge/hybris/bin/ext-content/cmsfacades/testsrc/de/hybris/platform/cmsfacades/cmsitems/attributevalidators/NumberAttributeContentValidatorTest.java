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
package de.hybris.platform.cmsfacades.cmsitems.attributevalidators;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.validation.model.constraints.jsr303.MaxConstraintModel;
import de.hybris.platform.validation.model.constraints.jsr303.MinConstraintModel;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NumberAttributeContentValidatorTest
{

	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	@InjectMocks
	private NumberAttributeContentValidator validator;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private ValidationErrors validationErrors;
	@Mock
	private MinConstraintModel minConstraint;
	@Mock
	private MaxConstraintModel maxConstraint;

	private Long minValue = new Long(10);
	private Long maxValue = new Long(20);

	@Before
	public void setup()
	{
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);

		when(attributeDescriptor.getConstraints()).thenReturn(Sets.newHashSet(minConstraint, maxConstraint));

		when(minConstraint.getItemtype()).thenReturn(MinConstraintModel._TYPECODE);
		when(minConstraint.getValue()).thenReturn(minValue);

		when(maxConstraint.getItemtype()).thenReturn(MaxConstraintModel._TYPECODE);
		when(maxConstraint.getValue()).thenReturn(maxValue);
	}

	@Test
	public void testValidValue_shouldNotAddError()
	{

		validator.validate(new Integer(15), attributeDescriptor);
		validator.validate(new Long(15), attributeDescriptor);
		verifyZeroInteractions(validationErrorsProvider);
	}

	@Test
	public void testInValidValueGreater_shouldAddError()
	{
		final List<ValidationError> errors = validator.validate(new Integer(25), attributeDescriptor);
		assertThat(errors, not(empty()));
	}

	@Test
	public void testInValidValueLess_shouldAddError()
	{
		final List<ValidationError> errors = validator.validate(new Integer(5), attributeDescriptor);
		assertThat(errors, not(empty()));
	}
}

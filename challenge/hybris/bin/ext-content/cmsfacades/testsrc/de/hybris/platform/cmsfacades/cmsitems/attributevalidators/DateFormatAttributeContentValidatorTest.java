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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DateFormatAttributeContentValidatorTest
{

	private static final String INVALID_FORMAT = "2014 Mar 21, 12:45:33";
	private static final String VALID_FORMAT = "2017-03-24T23:35:46+0000";
	@Mock
	private ValidationErrorsProvider validationErrorsProvider;
	
	@InjectMocks
	private DateFormatAttributeContentValidator validator;
	
	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private ValidationErrors validationErrors;

	@Before
	public void setup()
	{
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
	}
	
	@Test
	public void testValidFormat_shouldNotAddError()
	{
		validator.validate(VALID_FORMAT, attributeDescriptor);
		verifyZeroInteractions(validationErrorsProvider);
	}

	@Test
	public void testInValidFormat_shouldAddError()
	{
		final List<ValidationError> errors = validator.validate(INVALID_FORMAT, attributeDescriptor);
		Assert.assertThat(errors, not(empty()));
	}
}

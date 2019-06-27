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
package de.hybris.platform.cmsfacades.cmsitems.validator;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.ITEM_WITH_NAME_ALREADY_EXIST;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultUniqueNameForAttributeValidatorTest
{

	private final String ATTRIBUTE = "someAttribute";
	private final String DUPLICATE_CMSITEM_NAME = "restriction 1";
	private final String UNIQUE_CMSITEM_NAME = "restriction 2";

	@InjectMocks
	private DefaultUniqueNameForAttributeValidator validator;

	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	@Mock
	private ModelService modelService;

	@Mock
	private TypeService typeService;

	@Mock
	private CMSItemModel validatee;

	@Mock
	private CMSItemModel cmsItem1;

	@Mock
	private CMSItemModel cmsItem2;

	@Mock
	private CMSItemModel cmsItem3;

	private final ValidationErrors validationErrors = new DefaultValidationErrors();

	@Before
	public void setUp()
	{
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
		when(cmsItem1.getName()).thenReturn(DUPLICATE_CMSITEM_NAME);
		when(cmsItem2.getName()).thenReturn(UNIQUE_CMSITEM_NAME);
		when(cmsItem3.getName()).thenReturn(DUPLICATE_CMSITEM_NAME);
		validator.setAttribute(ATTRIBUTE);
	}

	@Test
	public void givenItemHasNoAttributeWhenValidatedThenNoErrorsAreThrown()
	{

		when(typeService.hasAttribute(any(), any())).thenReturn(false);

		validator.validate(validatee);

		assertHasNoErrors();
	}

	@Test
	public void givenItemHasAttributeWhichIsNullWhenValidatedThenNoErrorsAreThrown()
	{

		when(typeService.hasAttribute(any(), any())).thenReturn(true);
		when(modelService.getAttributeValue(validatee, ATTRIBUTE)).thenReturn(null);

		validator.validate(validatee);

		assertHasNoErrors();
	}

	@Test
	public void givenItemHasEmptyCollectionInAttributeWhenValidatedThenNoErrorsAreThrown()
	{

		when(typeService.hasAttribute(any(), any())).thenReturn(true);
		when(modelService.getAttributeValue(validatee, ATTRIBUTE)).thenReturn(Arrays.asList());

		validator.validate(validatee);

		assertHasNoErrors();
	}

	@Test
	public void givenItemHasAttributeHasMultipleValuesWithDuplicateNamesWhenValidatedThenErrorIsThrown()
	{

		when(typeService.hasAttribute(any(), any())).thenReturn(true);
		when(modelService.getAttributeValue(validatee, ATTRIBUTE)).thenReturn(Arrays.asList(cmsItem1, cmsItem2, cmsItem3));

		validator.validate(validatee);

		assertHasError(validator.getAttribute(), ITEM_WITH_NAME_ALREADY_EXIST);
	}

	protected void assertHasNoErrors()
	{
		final List<ValidationError> errors = validationErrors.getValidationErrors();
		assertTrue(errors.isEmpty());
	}

	protected void assertHasError(final String field, final String errorCode)
	{
		final List<ValidationError> errors = validationErrors.getValidationErrors();

		assertThat(errors.get(0).getField(), is(field));
		assertThat(errors.get(0).getErrorCode(), is(errorCode));
	}

}

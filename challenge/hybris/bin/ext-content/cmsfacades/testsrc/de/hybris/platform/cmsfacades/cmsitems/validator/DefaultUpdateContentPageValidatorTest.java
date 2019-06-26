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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_DOES_NOT_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_LABEL_ALREADY_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;

import java.util.List;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultUpdateContentPageValidatorTest
{
	// ---------------------------------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------------------------------
	private final String PAGE_LABEL = "Some Page Label";

	@Mock
	private Predicate<AbstractPageModel> pageUpdateRequiresValidationPredicate;

	@Mock
	private Predicate<String> primaryPageWithLabelExistsPredicate;

	@Mock
	private Predicate<AbstractPageModel> pageRestoreWithReplacePredicate;

	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	@Mock
	private ContentPageModel pageToValidate;

	@InjectMocks
	private DefaultUpdateContentPageValidator defaultUpdateContentPageValidator;

	private final ValidationErrors validationErrors = new DefaultValidationErrors();

	// ---------------------------------------------------------------------------------------------------
	// SetUp
	// ---------------------------------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		// Page Setup
		when(pageToValidate.getLabel()).thenReturn(PAGE_LABEL);
		when(pageToValidate.getDefaultPage()).thenReturn(true);

		when(primaryPageWithLabelExistsPredicate.test(PAGE_LABEL)).thenReturn(false);
		when(pageUpdateRequiresValidationPredicate.test(pageToValidate)).thenReturn(true);
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
	}

	// ---------------------------------------------------------------------------------------------------
	// Test Methods
	// ---------------------------------------------------------------------------------------------------
	@Test
	public void givenPageRequiresValidation_AndPageNotHavingLabel_WhenValidated_ThenItMustFail()
	{
		// GIVEN
		when(pageToValidate.getLabel()).thenReturn(null);

		// WHEN
		defaultUpdateContentPageValidator.validate(pageToValidate);

		// THEN
		assertHasError(ContentPageModel.LABEL, FIELD_REQUIRED, null);
	}

	@Test
	public void givenPageRequiresValidation_AndPrimaryPageHavingLabelThatAlreadyExists_WhenValidated_ThenItMustFail()
	{
		// GIVEN
		when(primaryPageWithLabelExistsPredicate.test(PAGE_LABEL)).thenReturn(true);

		// WHEN
		defaultUpdateContentPageValidator.validate(pageToValidate);

		// THEN
		assertHasError(ContentPageModel.LABEL, DEFAULT_PAGE_LABEL_ALREADY_EXIST, null);
	}

	@Test
	public void givenPageRequiresValidation_AndPrimaryPageHavingLabelThatAlreadyExistsAndIsNotAPageRestoreOperation_WhenValidated_ThenItMustFail()
	{
		// GIVEN
		when(primaryPageWithLabelExistsPredicate.test(PAGE_LABEL)).thenReturn(true);
		when(pageRestoreWithReplacePredicate.test(pageToValidate)).thenReturn(false);

		// WHEN
		defaultUpdateContentPageValidator.validate(pageToValidate);

		// THEN
		assertHasError(ContentPageModel.LABEL, DEFAULT_PAGE_LABEL_ALREADY_EXIST, null);
	}

	@Test
	public void givenPageRequiresValidation_AndVariationWithALabelWithoutPrimary_WhenValidated_ThenItMustFail()
	{
		// GIVEN
		when(pageToValidate.getDefaultPage()).thenReturn(false);

		// WHEN
		defaultUpdateContentPageValidator.validate(pageToValidate);

		// THEN
		assertHasError(ContentPageModel.LABEL, DEFAULT_PAGE_DOES_NOT_EXIST, new Object[]
		{ ContentPageModel.LABEL, PAGE_LABEL });
	}

	@Test
	public void givenPageRequiresValidation_AndValidPrimaryPage_WhenValidated_ThenItMustPass()
	{
		// GIVEN

		// WHEN
		defaultUpdateContentPageValidator.validate(pageToValidate);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageRequiresValidation_AndValidVariationPage_WhenValidatied_ThenItMustPass()
	{
		// GIVEN
		when(pageToValidate.getDefaultPage()).thenReturn(false);
		when(primaryPageWithLabelExistsPredicate.test(PAGE_LABEL)).thenReturn(true);

		// WHEN
		defaultUpdateContentPageValidator.validate(pageToValidate);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageDoesNotRequireValidation_AndInvalidPrimaryPage_WhenValidated_ThenItMustPass()
	{
		// GIVEN
		when(primaryPageWithLabelExistsPredicate.test(PAGE_LABEL)).thenReturn(true);
		when(pageUpdateRequiresValidationPredicate.test(pageToValidate)).thenReturn(false);

		// WHEN
		defaultUpdateContentPageValidator.validate(pageToValidate);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageDoesNotRequireValidation_AndValidPrimaryPage_WhenValidated_ThenItMustPass()
	{
		// GIVEN
		when(pageUpdateRequiresValidationPredicate.test(pageToValidate)).thenReturn(false);

		// WHEN
		defaultUpdateContentPageValidator.validate(pageToValidate);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageDoesNotRequireValidation_AndInvalidVariationPage_WhenValidated_ThenItMustPass()
	{
		// GIVEN
		when(pageToValidate.getDefaultPage()).thenReturn(false);
		when(pageUpdateRequiresValidationPredicate.test(pageToValidate)).thenReturn(false);

		// WHEN
		defaultUpdateContentPageValidator.validate(pageToValidate);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageDoesNotRequireValidation_AndValidVariationPage_WhenValidated_ThenItMustPass()
	{
		// GIVEN
		when(pageToValidate.getDefaultPage()).thenReturn(false);
		when(primaryPageWithLabelExistsPredicate.test(PAGE_LABEL)).thenReturn(true);
		when(pageUpdateRequiresValidationPredicate.test(pageToValidate)).thenReturn(false);

		// WHEN
		defaultUpdateContentPageValidator.validate(pageToValidate);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageRequiresValidation_AndPrimaryPageHavingLabelThatAlreadyExistsAndIsAPageRestoreOperation_WhenValidated_ThenItMustPass()
	{
		// GIVEN
		when(primaryPageWithLabelExistsPredicate.test(PAGE_LABEL)).thenReturn(true);
		when(pageRestoreWithReplacePredicate.test(pageToValidate)).thenReturn(true);

		// WHEN
		defaultUpdateContentPageValidator.validate(pageToValidate);

		// THEN
		assertHasNoErrors();
	}

	// ---------------------------------------------------------------------------------------------------
	// Helper Methods
	// ---------------------------------------------------------------------------------------------------
	protected void assertHasNoErrors()
	{
		final List<ValidationError> errors = validationErrors.getValidationErrors();
		assertTrue(errors.isEmpty());
	}

	protected void assertHasError(final String field, final String errorCode, final Object[] errorArgs)
	{
		final List<ValidationError> errors = validationErrors.getValidationErrors();

		assertThat(errors.get(0).getField(), is(field));
		assertThat(errors.get(0).getErrorCode(), is(errorCode));
		assertThat(errors.get(0).getErrorArgs(), is(errorArgs));
	}
}

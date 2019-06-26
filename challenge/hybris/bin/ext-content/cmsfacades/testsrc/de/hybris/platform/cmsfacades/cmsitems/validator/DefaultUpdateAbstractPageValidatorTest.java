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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_ALREADY_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_DOES_NOT_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_HAS_VARIATIONS;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_NOT_ALLOWED;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverType;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.servicelayer.model.ItemModelContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultUpdateAbstractPageValidatorTest
{
	// ---------------------------------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------------------------------
	private static final String TEST_UID = "test-uid";
	private static final String OTHER_UID = "some-other-uid";
	private static final String ITEM_TYPE = "Some item type";

	@Mock
	private AbstractPageModel otherPage;

	@Mock
	private ItemModelContext itemModelContext;

	@Mock
	private AbstractPageModel pageUnderTest;

	@Mock
	private Predicate<String> pageExistsPredicate;

	@Mock
	private Predicate<AbstractPageModel> pageCanOnlyHaveOnePrimaryPredicate;

	@Mock
	private Predicate<AbstractPageModel> pageUpdateRequiresValidationPredicate;

	@Mock
	private Predicate<AbstractPageModel> pageRestoreWithReplacePredicate;

	@Mock
	private Predicate<AbstractPageModel> pageHasVariationsPredicate;

	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	@Mock
	private PageVariationResolver<AbstractPageModel> pageVariationResolver;

	@Mock
	private PageVariationResolverType pageVariationResolverType;

	@Mock
	private PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry;

	@InjectMocks
	private DefaultUpdateAbstractPageValidator validator;

	private final ValidationErrors validationErrors = new DefaultValidationErrors();

	// ---------------------------------------------------------------------------------------------------
	// SetUp
	// ---------------------------------------------------------------------------------------------------
	@Before
	public void setup()
	{
		// Pages Setup
		when(pageUnderTest.getUid()).thenReturn(TEST_UID);
		when(pageUnderTest.getDefaultPage()).thenReturn(true);
		when(pageUnderTest.getItemtype()).thenReturn(ITEM_TYPE);
		when(pageUnderTest.getPageStatus()).thenReturn(CmsPageStatus.ACTIVE);
		when(pageUnderTest.getItemModelContext()).thenReturn(itemModelContext);

		when(itemModelContext.getOriginalValue(AbstractPageModel.DEFAULTPAGE)).thenReturn(true);
		when(itemModelContext.getOriginalValue(AbstractPageModel.PAGESTATUS)).thenReturn(CmsPageStatus.ACTIVE);

		when(otherPage.getUid()).thenReturn(OTHER_UID);

		// Page VariationResolver
		when(pageVariationResolverTypeRegistry.getPageVariationResolverType(ITEM_TYPE))
				.thenReturn(Optional.of(pageVariationResolverType));
		when(pageVariationResolverType.getResolver()).thenReturn(pageVariationResolver);
		when(pageVariationResolver.findPagesByType(ITEM_TYPE, true)).thenReturn(Collections.singletonList(pageUnderTest));

		// Predicates
		when(pageCanOnlyHaveOnePrimaryPredicate.test(pageUnderTest)).thenReturn(true);
		when(pageExistsPredicate.test(TEST_UID)).thenReturn(true);

		// Others
		when(pageUpdateRequiresValidationPredicate.test(pageUnderTest)).thenReturn(true);
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
	}

	// ---------------------------------------------------------------------------------------------------
	// Test Methods
	// ---------------------------------------------------------------------------------------------------
	@Test
	public void testValidateDefaultPageModified()
	{
		// GIVEN
		when(itemModelContext.getOriginalValue(AbstractPageModel.DEFAULTPAGE)).thenReturn(false);

		// WHEN
		validator.validate(pageUnderTest);

		// THEN
		assertHasError(AbstractPageModel.DEFAULTPAGE, FIELD_NOT_ALLOWED, null);
	}

	@Test
	public void givenPageRequiresValidation_AndPrimaryPageAlreadyExistForThatType_WhenValidated_ThenItMustFail()
	{
		// GIVEN
		when(pageVariationResolver.findPagesByType(ITEM_TYPE, true)).thenReturn(Collections.singletonList(otherPage));

		// WHEN
		validator.validate(pageUnderTest);

		// THEN
		assertHasError(AbstractPageModel.TYPECODE, DEFAULT_PAGE_ALREADY_EXIST, new Object[]
		{ ITEM_TYPE });
	}

	@Test
	public void givenPageRequiresValidation_AndVariationDoesNotHavePrimary_WhenValidated_ThenItMustFail()
	{
		// GIVEN
		configurePageUnderTestAsVariation();
		when(pageVariationResolver.findPagesByType(ITEM_TYPE, true)).thenReturn(Collections.EMPTY_LIST);

		// WHEN
		validator.validate(pageUnderTest);

		// THEN
		assertHasError(AbstractPageModel.TYPECODE, DEFAULT_PAGE_DOES_NOT_EXIST, new Object[]
		{ ITEM_TYPE });
	}

	@Test
	public void givenPageRequiresValidation_AndPrimaryPageIsValid_WhenValidated_ThenItMustPass()
	{
		// GIVEN

		// WHEN
		validator.validate(pageUnderTest);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageRequiresValidation_AndPrimaryPageIsValidAndIsARestoreOperation_WhenValidated_ThenItMustPass()
	{
		// GIVEN
		when(pageRestoreWithReplacePredicate.test(pageUnderTest)).thenReturn(true);

		// WHEN
		validator.validate(pageUnderTest);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageRequiresValidation_AndPrimaryPageHasVariations_WhenValidated_ThenItMustFail()
	{
		// GIVEN
		when(pageUnderTest.getPageStatus()).thenReturn(CmsPageStatus.DELETED);
		when(itemModelContext.getOriginalValue(AbstractPageModel.PAGESTATUS)).thenReturn(CmsPageStatus.ACTIVE);
		when(pageHasVariationsPredicate.test(pageUnderTest)).thenReturn(true);

		// WHEN
		validator.validate(pageUnderTest);

		// THEN
		assertHasError(AbstractPageModel.TYPECODE, DEFAULT_PAGE_HAS_VARIATIONS, null);
	}

	@Test
	public void givenPageRequiresValidation_andVariationPageIsValid_WhenValidated_ThenItMustPass()
	{
		// GIVEN
		configurePageUnderTestAsVariation();

		// WHEN
		validator.validate(pageUnderTest);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageDoesNotRequireValidation_AndPrimaryPageAlreadyExistForThatType_WhenValidated_ThenItMustPass()
	{
		// GIVEN
		when(pageUpdateRequiresValidationPredicate.test(pageUnderTest)).thenReturn(false);
		when(pageVariationResolver.findPagesByType(ITEM_TYPE, true)).thenReturn(Collections.singletonList(otherPage));

		// WHEN
		validator.validate(pageUnderTest);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageDoesNotRequireValidation_AndVariationDoesNotHavePrimary_WhenValidated_ThenItMustPass()
	{
		// GIVEN
		when(pageUpdateRequiresValidationPredicate.test(pageUnderTest)).thenReturn(false);
		when(pageVariationResolver.findPagesByType(ITEM_TYPE, true)).thenReturn(Collections.EMPTY_LIST);

		// WHEN
		validator.validate(pageUnderTest);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageDoesNotRequireValidation_AndPrimaryPageIsValid_WhenValidated_ThenItMustPass()
	{
		// GIVEN
		when(pageUpdateRequiresValidationPredicate.test(pageUnderTest)).thenReturn(false);

		// WHEN
		validator.validate(pageUnderTest);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageDoesNotRequireValidation_andVariationPageIsValid_WhenValidated_ThenItMustPass()
	{
		// GIVEN
		when(pageUpdateRequiresValidationPredicate.test(pageUnderTest)).thenReturn(false);
		configurePageUnderTestAsVariation();

		// WHEN
		validator.validate(pageUnderTest);

		// THEN
		assertHasNoErrors();
	}

	// ---------------------------------------------------------------------------------------------------
	// Helper Methods
	// ---------------------------------------------------------------------------------------------------
	protected void configurePageUnderTestAsVariation()
	{
		when(pageUnderTest.getDefaultPage()).thenReturn(false);
		when(itemModelContext.getOriginalValue(AbstractPageModel.DEFAULTPAGE)).thenReturn(false);
	}

	protected void assertHasNoErrors()
	{
		final List<ValidationError> errors = validationErrors.getValidationErrors();
		assertTrue(errors.isEmpty());
	}

	protected void assertHasError(final String field, final String errorCode, final Object[] errorArgs)
	{
		final List<ValidationError> errors = validationErrors.getValidationErrors();

		assertThat(errors.get(0).getField(), Is.is(field));
		assertThat(errors.get(0).getErrorCode(), Is.is(errorCode));
		assertThat(errors.get(0).getErrorArgs(), Is.is(errorArgs));
	}
}

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
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_ALREADY_EXIST;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.cmsitems.predicates.CloneContextSameAsActiveCatalogVersionPredicate;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverType;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.servicelayer.model.ItemModelContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCreateAbstractPageValidatorTest
{
	private static final String TEST_UID = "test-uid";
	private static final String INVALID = "invalid";

	@InjectMocks
	private DefaultCreateAbstractPageValidator validator;

	@Mock
	private Predicate<String> pageExistsPredicate;
	@Mock
	private PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry;
	@Mock
	private PageVariationResolverType pageVariationResolverType;
	@Mock
	private PageVariationResolver<AbstractPageModel> pageVariationResolver;
	@Mock
	private ItemModelContext itemModelContext;
	@Mock
	private ValidationErrorsProvider validationErrorsProvider;
	@Mock
	private Predicate<AbstractPageModel> pageCanOnlyHaveOnePrimaryPredicate;

	@Mock
	private CloneContextSameAsActiveCatalogVersionPredicate cloneContextSameAsActiveCatalogVersionPredicate;

	private final List<AbstractPageModel> defaultPages = new ArrayList<>();
	private final ValidationErrors validationErrors = new DefaultValidationErrors();
	final AbstractPageModel pageModel = new AbstractPageModel();

	@Before
	public void setup()
	{
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
		when(pageExistsPredicate.test(TEST_UID)).thenReturn(false);
		when(pageExistsPredicate.test(INVALID)).thenReturn(true);
		when(pageVariationResolverTypeRegistry.getPageVariationResolverType(AbstractPageModel._TYPECODE)).thenReturn(Optional.of(pageVariationResolverType));
		when(pageVariationResolverType.getResolver()).thenReturn(pageVariationResolver);
		when(pageVariationResolver.findPagesByType(AbstractPageModel._TYPECODE, true)).thenReturn(defaultPages);
		when(itemModelContext.getItemType()).thenReturn(AbstractPageModel._TYPECODE);
		when(cloneContextSameAsActiveCatalogVersionPredicate.test(pageModel)).thenReturn(true);
	}

	@Test
	public void testValidatePageExists()
	{
		pageModel.setUid(INVALID);
		pageModel.setDefaultPage(true);

		validator.validate(pageModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(1, errors.size());
		assertThat(errors.get(0).getField(), is(AbstractPageModel.UID));
		assertThat(errors.get(0).getErrorCode(), is(FIELD_ALREADY_EXIST));
	}

	@Test
	public void testValidateDefaultPageDoesNotExist_ForPagesThatCanHaveOnlyOnePrimary()
	{
		pageModel.setUid(TEST_UID);
		pageModel.setDefaultPage(false);

		when(pageCanOnlyHaveOnePrimaryPredicate.test(pageModel)).thenReturn(true);

		validator.validate(pageModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(1, errors.size());
		assertThat(errors.get(0).getField(), is(AbstractPageModel.TYPECODE));
		assertThat(errors.get(0).getErrorCode(), is(DEFAULT_PAGE_DOES_NOT_EXIST));
	}

	@Test
	public void testValidateDefaultPageAlreadyExists_ForPagesThatCanHaveOnlyOnePrimary()
	{
		pageModel.setUid(TEST_UID);
		pageModel.setDefaultPage(true);

		when(pageCanOnlyHaveOnePrimaryPredicate.test(pageModel)).thenReturn(true);

		defaultPages.add(new AbstractPageModel());

		validator.validate(pageModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(1, errors.size());
		assertThat(errors.get(0).getField(), is(AbstractPageModel.TYPECODE));
		assertThat(errors.get(0).getErrorCode(), is(DEFAULT_PAGE_ALREADY_EXIST));
	}

	@Test
	public void testNoValidateDefaultPageAlreadyExists_IfPageIsCreatedInDifferentCatalogVersion()
	{
		pageModel.setUid(TEST_UID);
		pageModel.setDefaultPage(true);

		when(cloneContextSameAsActiveCatalogVersionPredicate.test(pageModel)).thenReturn(false);
		when(pageCanOnlyHaveOnePrimaryPredicate.test(pageModel)).thenReturn(true);

		defaultPages.add(new AbstractPageModel());

		validator.validate(pageModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(0, errors.size());
	}

	@Test
	public void testDoesNotValidateOnDefaultPage_ForPagesThatCanHaveMultiplePrimaryPages()
	{
		pageModel.setUid(TEST_UID);
		pageModel.setDefaultPage(false);

		when(pageCanOnlyHaveOnePrimaryPredicate.test(pageModel)).thenReturn(false);

		validator.validate(pageModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(0, errors.size());
	}
}

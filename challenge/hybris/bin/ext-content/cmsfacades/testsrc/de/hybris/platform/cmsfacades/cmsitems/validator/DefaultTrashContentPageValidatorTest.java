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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.TOP_LEVEL_HOMEPAGE_CANNOT_BE_REMOVED;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTrashContentPageValidatorTest
{
	// ---------------------------------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------------------------------
	private final String PAGE_UID = "some page uid";
	private final String FALLBACK_PAGE_UID = "some fallback page uid";

	private final ValidationErrors validationErrors = new DefaultValidationErrors();

	@Mock
	private ContentCatalogModel contentCatalogModel;

	@Mock
	private CatalogVersionModel catalogVersionModel;

	@Mock
	private ContentPageModel fallbackHomepage;

	@Mock
	private ContentPageModel contentPageModel;

	@Mock
	private ItemModelContext itemModelContext;

	@Mock
	private CMSAdminPageService cmsAdminPageService;

	@Mock
	private CatalogLevelService catalogLevelService;

	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	@InjectMocks
	private DefaultTrashContentPageValidator defaultTrashContentPageValidator;

	// ---------------------------------------------------------------------------------------------------
	// SetUp
	// ---------------------------------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		when(cmsAdminPageService.getHomepage(catalogVersionModel)).thenReturn(null);
		when(catalogLevelService.isTopLevel(contentCatalogModel)).thenReturn(true);
		when(catalogVersionModel.getCatalog()).thenReturn(contentCatalogModel);

		when(contentPageModel.getCatalogVersion()).thenReturn(catalogVersionModel);
		when(contentPageModel.getUid()).thenReturn(PAGE_UID);
		when(fallbackHomepage.getUid()).thenReturn(FALLBACK_PAGE_UID);

		when(itemModelContext.isDirty(any())).thenReturn(false);

		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
	}

	// ---------------------------------------------------------------------------------------------------
	// Tests
	// ---------------------------------------------------------------------------------------------------
	@Test
	public void givenPageIsNotHomepageAndIsNotBeingTrashed_WhenValidated_ItMustPass()
	{
		// GIVEN
		configureContentPage(CmsPageStatus.ACTIVE, false);

		// WHEN
		defaultTrashContentPageValidator.validate(contentPageModel);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageIsNotHomepageAndIsBeingTrashed_WhenValidated_ItMustPass()
	{
		// GIVEN
		configureContentPage(CmsPageStatus.DELETED, false);

		// WHEN
		defaultTrashContentPageValidator.validate(contentPageModel);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageIsHomepageAndIsNotBeingTrashed_WhenValidated_ItMustPass()
	{
		// GIVEN
		configureContentPage(CmsPageStatus.ACTIVE, true);

		// WHEN
		defaultTrashContentPageValidator.validate(contentPageModel);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageIsTopLevelHomepageAndIsBeingReplaced_WhenValidated_ItMustPass()
	{
		// GIVEN
		configureContentPage(CmsPageStatus.DELETED, true);
		when(cmsAdminPageService.getHomepage(catalogVersionModel)).thenReturn(fallbackHomepage);

		// WHEN
		defaultTrashContentPageValidator.validate(contentPageModel);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageIsNonTopLevelHomepageAndIsBeingTrashed_WhenValidated_ItMustPass()
	{
		// GIVEN
		configureContentPage(CmsPageStatus.DELETED, true);

		when(catalogLevelService.isTopLevel(contentCatalogModel)).thenReturn(false);
		when(fallbackHomepage.getUid()).thenReturn(PAGE_UID);
		when(cmsAdminPageService.getHomepage(catalogVersionModel)).thenReturn(fallbackHomepage);

		// WHEN
		defaultTrashContentPageValidator.validate(contentPageModel);

		// THEN
		assertHasNoErrors();
	}

	@Test
	public void givenPageIsTopLevelHomepageAndIsBeingTrashed_WhenValidated_ItMustFail()
	{
		// GIVEN
		configureContentPage(CmsPageStatus.DELETED, true);

		// WHEN
		defaultTrashContentPageValidator.validate(contentPageModel);

		// THEN
		assertHasError(ContentPageModel.HOMEPAGE, TOP_LEVEL_HOMEPAGE_CANNOT_BE_REMOVED);
	}

	@Test
	public void givenFrontEndDoesNotConsiderPageAsHomepage_ButPageIsActuallyHomepageAndIsBeingTrashed_WhenValidated_ItMustFail()
	{
		// GIVEN
		markAttributeAsDirty(ContentPageModel.HOMEPAGE);
		configureContentPage(CmsPageStatus.DELETED, false);

		// WHEN
		defaultTrashContentPageValidator.validate(contentPageModel);

		// THEN
		assertHasError(ContentPageModel.HOMEPAGE, TOP_LEVEL_HOMEPAGE_CANNOT_BE_REMOVED);
	}

	// ---------------------------------------------------------------------------------------------------
	// Helper Methods
	// ---------------------------------------------------------------------------------------------------
	protected void configureContentPage(final CmsPageStatus pageStatus, final boolean isHomePage)
	{
		when(contentPageModel.getPageStatus()).thenReturn(pageStatus);
		when(contentPageModel.isHomepage()).thenReturn(isHomePage);
		when(contentPageModel.getDefaultPage()).thenReturn(true);

		when(contentPageModel.getItemModelContext()).thenReturn(itemModelContext);
	}

	protected void markAttributeAsDirty(final String attributeName)
	{
		when(itemModelContext.isDirty(attributeName)).thenReturn(true);
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

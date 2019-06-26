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

import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.TOP_LEVEL_HOMEPAGE_CANNOT_BE_REMOVED;

/**
 * Validator to ensure that a content page can be moved to trash. These are the scenarios that it checks:
 * - It's possible to delete a homepage of a non-top level catalog.
 * - It shouldn't be possible to delete a homepage in a top level catalog.
 * - It should be possible to swap a homepage, even in a top level catalog.
 *
 */
public class DefaultTrashContentPageValidator implements Validator<ContentPageModel>
{
	// ---------------------------------------------------------------------------------------------------
	// Variables
	// ---------------------------------------------------------------------------------------------------
	private ValidationErrorsProvider validationErrorsProvider;
	private CMSAdminPageService cmsAdminPageService;
	private CatalogLevelService catalogLevelService;

	// ---------------------------------------------------------------------------------------------------
	// Public API
	// ---------------------------------------------------------------------------------------------------
	@Override
	public void validate(ContentPageModel validatee)
	{
		if (isPageBeingTrashed(validatee) && isPageUndeletable(validatee))
		{
			addValidationError(ContentPageModel.HOMEPAGE, TOP_LEVEL_HOMEPAGE_CANNOT_BE_REMOVED);
		}
	}

	// ---------------------------------------------------------------------------------------------------
	// Helper Method
	// ---------------------------------------------------------------------------------------------------

	/**
	 * This method checks if the provided page is being moved to the trash list.
	 * @param contentPageModel The page to check.
	 * @return true, if the page is being moved to the trash list. False, otherwise.
	 */
	protected boolean isPageBeingTrashed(final ContentPageModel contentPageModel)
	{
		return contentPageModel.getPageStatus().equals(CmsPageStatus.DELETED);
	}

	/**
	 * This method checks if the provided page can be removed. A page cannot be removed if it's a default page and is the top level
	 * homepage.
	 * @param contentPageModel The page to be checked.
	 * @return true, if the page cannot be removed. False, otherwise.
	 */
	protected boolean isPageUndeletable(final ContentPageModel contentPageModel)
	{
		return contentPageModel.getDefaultPage() && isOrWasOnlyTopLevelHomepage(contentPageModel);
	}

	/**
	 * This method checks if the provided page is the top level homepage.
	 * @param contentPageModel The page to be checked
	 * @return true, if the page is the top level homepage. False, otherwise.
	 */
	protected boolean isOrWasOnlyTopLevelHomepage(final ContentPageModel contentPageModel)
	{
		// We need to check from the database that the page is not a homepage (the front-end client might have out-of-date information).
		// Otherwise, we might end up trashing the only homepage of a top level catalog, leaving the site without a homepage.
		boolean homePageFlagWasModified = contentPageModel.getItemModelContext().isDirty(ContentPageModel.HOMEPAGE);
		boolean wasHomePage = homePageFlagWasModified && !contentPageModel.isHomepage();

		return (contentPageModel.isHomepage() || wasHomePage) && catalogDoesNotHaveFallbackHomepage(contentPageModel);
	}

	/**
	 * This method checks if the current catalog does not have a fallback page. This is the case if the catalog is a top-level catalog
	 * and it does not have a homepage or the current homepage is the page that is being removed.
	 *
	 * @param contentPageModel The page that is being removed.
	 * @return true if the catalog does not have a fallback page, false otherwise.
	 */
	protected boolean catalogDoesNotHaveFallbackHomepage(final ContentPageModel contentPageModel)
	{
		boolean isTopLevelCatalog = getCatalogLevelService().isTopLevel((ContentCatalogModel) contentPageModel.getCatalogVersion().getCatalog());
		ContentPageModel currentHomepage = getCmsAdminPageService().getHomepage(contentPageModel.getCatalogVersion());

		return isTopLevelCatalog && (currentHomepage == null || currentHomepage.getUid().equals(contentPageModel.getUid()));
	}

	/**
	 * This method pushes a new validation error.
	 * @param field The field affected by the error.
	 * @param errorCode The code identifying the type of error.
	 */
	protected void addValidationError(final String field, final String errorCode)
	{
		getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
				.field(field) //
				.errorCode(errorCode) //
				.build());
	}

	// ---------------------------------------------------------------------------------------------------
	// Getters/Setters
	// ---------------------------------------------------------------------------------------------------
	protected ValidationErrorsProvider getValidationErrorsProvider()
	{
		return validationErrorsProvider;
	}

	@Required
	public void setValidationErrorsProvider(final ValidationErrorsProvider validationErrorsProvider)
	{
		this.validationErrorsProvider = validationErrorsProvider;
	}

	protected CMSAdminPageService getCmsAdminPageService()
	{
		return cmsAdminPageService;
	}

	@Required
	public void setCmsAdminPageService(final CMSAdminPageService cmsAdminPageService)
	{
		this.cmsAdminPageService = cmsAdminPageService;
	}

	protected CatalogLevelService getCatalogLevelService()
	{
		return catalogLevelService;
	}

	@Required
	public void setCatalogLevelService(final CatalogLevelService catalogLevelService)
	{
		this.catalogLevelService = catalogLevelService;
	}
}

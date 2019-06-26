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

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_ALREADY_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_DOES_NOT_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_HAS_VARIATIONS;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_NOT_ALLOWED;

import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the validator for {@link AbstractPageModel}
 */
public class DefaultUpdateAbstractPageValidator implements Validator<AbstractPageModel>
{
	private Predicate<String> pageExistsPredicate;
	private ValidationErrorsProvider validationErrorsProvider;
	private Predicate<AbstractPageModel> pageCanOnlyHaveOnePrimaryPredicate;
	private Predicate<AbstractPageModel> pageUpdateRequiresValidationPredicate;
	private Predicate<AbstractPageModel> pageRestoreWithReplacePredicate;
	private Predicate<AbstractPageModel> pageHasVariationsPredicate;
	private PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry;

	// ---------------------------------------------------------------------------------------------------
	// Public API
	// ---------------------------------------------------------------------------------------------------
	@Override
	public void validate(final AbstractPageModel newPageModel)
	{
		final boolean previewDefaultPage = newPageModel.getItemModelContext().getOriginalValue(AbstractPageModel.DEFAULTPAGE);
		final CmsPageStatus previousPageStatus = newPageModel.getItemModelContext().getOriginalValue(AbstractPageModel.PAGESTATUS);

		//	Validate that defaultPage value has not been modified.
		if (!newPageModel.getDefaultPage().equals(previewDefaultPage))
		{
			addValidationError(AbstractPageModel.DEFAULTPAGE, FIELD_NOT_ALLOWED, null);
		}

		// Validate primary page with variations cannot be deleted
		if (!newPageModel.getPageStatus().equals(previousPageStatus) &&
				newPageModel.getPageStatus().equals(CmsPageStatus.DELETED) &&
				getPageHasVariationsPredicate().test(newPageModel))
		{
			addValidationError(AbstractPageModel.TYPECODE, DEFAULT_PAGE_HAS_VARIATIONS, null);
		}

		// Validate that category and product pages can only have one primary for each type.
		if (getPageUpdateRequiresValidationPredicate().test(newPageModel)
				&& getPageCanOnlyHaveOnePrimaryPredicate().test(newPageModel))
		{
			final List<AbstractPageModel> existingPrimaryPagesList = getExistingPrimaryPagesByType(newPageModel.getItemtype());
			final boolean isPageRestoreWithReplace = getPageRestoreWithReplacePredicate().test(newPageModel);

			if (isPrimaryPage(newPageModel) && primaryPageAlreadyExists(newPageModel, existingPrimaryPagesList)
					&& !isPageRestoreWithReplace)
			{
				addValidationError(AbstractPageModel.TYPECODE, DEFAULT_PAGE_ALREADY_EXIST, new Object[]
				{ newPageModel.getItemtype() });
			}
			else if (isVariationPage(newPageModel) && existingPrimaryPagesList.isEmpty())
			{
				addValidationError(AbstractPageModel.TYPECODE, DEFAULT_PAGE_DOES_NOT_EXIST, new Object[]
				{ newPageModel.getItemtype() });
			}
		}

	}

	// ---------------------------------------------------------------------------------------------------
	// Helper Methods
	// ---------------------------------------------------------------------------------------------------
	protected void addValidationError(final String field, final String errorCode, final Object[] errorArgs)
	{
		getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
				.field(field) //
				.errorCode(errorCode) //
				.errorArgs(errorArgs) //
				.build());
	}

	protected boolean isPrimaryPage(final AbstractPageModel pageModel)
	{
		return pageModel.getDefaultPage();
	}

	protected boolean isVariationPage(final AbstractPageModel pageModel)
	{
		return !pageModel.getDefaultPage();
	}

	// ---------------------------------------------------------------------------------------------------
	// Getters/Setters
	// ---------------------------------------------------------------------------------------------------
	protected List<AbstractPageModel> getExistingPrimaryPagesByType(final String pageType)
	{
		final PageVariationResolver<AbstractPageModel> pageVariationResolver = getPageVariationResolverTypeRegistry()
				.getPageVariationResolverType(pageType).get().getResolver();
		return pageVariationResolver.findPagesByType(pageType, true);
	}

	protected boolean primaryPageAlreadyExists(final AbstractPageModel currentPage, final List<AbstractPageModel> existingPages)
	{
		return !existingPages.isEmpty() && existingPages.stream().anyMatch(page -> !page.getUid().equals(currentPage.getUid()));
	}

	protected final Predicate<String> getPageExistsPredicate()
	{
		return pageExistsPredicate;
	}

	@Required
	public final void setPageExistsPredicate(final Predicate<String> pageExistsPredicate)
	{
		this.pageExistsPredicate = pageExistsPredicate;
	}

	protected ValidationErrorsProvider getValidationErrorsProvider()
	{
		return validationErrorsProvider;
	}

	@Required
	public void setValidationErrorsProvider(final ValidationErrorsProvider validationErrorsProvider)
	{
		this.validationErrorsProvider = validationErrorsProvider;
	}


	protected Predicate<AbstractPageModel> getPageCanOnlyHaveOnePrimaryPredicate()
	{
		return pageCanOnlyHaveOnePrimaryPredicate;
	}

	@Required
	public void setPageCanOnlyHaveOnePrimaryPredicate(final Predicate<AbstractPageModel> pageCanOnlyHaveOnePrimaryPredicate)
	{
		this.pageCanOnlyHaveOnePrimaryPredicate = pageCanOnlyHaveOnePrimaryPredicate;
	}

	protected Predicate<AbstractPageModel> getPageUpdateRequiresValidationPredicate()
	{
		return pageUpdateRequiresValidationPredicate;
	}

	@Required
	public void setPageUpdateRequiresValidationPredicate(final Predicate<AbstractPageModel> pageUpdateRequiresValidationPredicate)
	{
		this.pageUpdateRequiresValidationPredicate = pageUpdateRequiresValidationPredicate;
	}

	protected Predicate<AbstractPageModel> getPageRestoreWithReplacePredicate()
	{
		return pageRestoreWithReplacePredicate;
	}

	@Required
	public void setPageRestoreWithReplacePredicate(final Predicate<AbstractPageModel> pageRestoreWithReplacePredicate)
	{
		this.pageRestoreWithReplacePredicate = pageRestoreWithReplacePredicate;
	}

	protected Predicate<AbstractPageModel> getPageHasVariationsPredicate()
	{
		return pageHasVariationsPredicate;
	}

	@Required
	public void setPageHasVariationsPredicate(final Predicate<AbstractPageModel> pageHasVariationsPredicate)
	{
		this.pageHasVariationsPredicate = pageHasVariationsPredicate;
	}

	protected PageVariationResolverTypeRegistry getPageVariationResolverTypeRegistry()
	{
		return pageVariationResolverTypeRegistry;
	}

	@Required
	public void setPageVariationResolverTypeRegistry(final PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry)
	{
		this.pageVariationResolverTypeRegistry = pageVariationResolverTypeRegistry;
	}
}

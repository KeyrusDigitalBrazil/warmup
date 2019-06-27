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
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_DOES_NOT_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_LABEL_ALREADY_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;

import java.util.function.Predicate;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the validator for {@link AbstractPageModel}
 */
public class DefaultUpdateContentPageValidator implements Validator<ContentPageModel>
{
	private CMSAdminPageService cmsAdminPageService;
	private Predicate<String> primaryPageWithLabelExistsPredicate;
	private Predicate<String> hasPageLabelChangedPredicate;
	private Predicate<String> pageExistsPredicate;
	private Predicate<AbstractPageModel> pageUpdateRequiresValidationPredicate;
	private Predicate<AbstractPageModel> pageRestoreWithReplacePredicate;
	private ValidationErrorsProvider validationErrorsProvider;

	// ---------------------------------------------------------------------------------------------------
	// Public API
	// ---------------------------------------------------------------------------------------------------
	@Override
	public void validate(final ContentPageModel newPageModel)
	{
		if (getPageUpdateRequiresValidationPredicate().test(newPageModel))
		{
			final boolean primaryPageWithLabelExists = getPrimaryPageWithLabelExistsPredicate().test(newPageModel.getLabel());
			final boolean isPageRestoreWithReplace = getPageRestoreWithReplacePredicate().test(newPageModel);

			if (Strings.isBlank(newPageModel.getLabel()))
			{
				addValidationError(ContentPageModel.LABEL, FIELD_REQUIRED, null);
			}
			else if (isVariationPage(newPageModel) && !primaryPageWithLabelExists)
			{
				addValidationError(ContentPageModel.LABEL, DEFAULT_PAGE_DOES_NOT_EXIST, new Object[]
				{ ContentPageModel.LABEL, newPageModel.getLabel() });
			}
			else if (isPrimaryPage(newPageModel) && primaryPageWithLabelExists && !isPageRestoreWithReplace)
			{
				addValidationError(ContentPageModel.LABEL, DEFAULT_PAGE_LABEL_ALREADY_EXIST, null);
			}
		}
	}

	// ---------------------------------------------------------------------------------------------------
	// Helper Method
	// ---------------------------------------------------------------------------------------------------
	protected void addValidationError(final String field, final String errorCode, final Object[] errorArgs)
	{
		getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
				.field(field) //
				.errorCode(errorCode) //
				.errorArgs(errorArgs) //
				.build());
	}

	protected boolean isVariationPage(final ContentPageModel newPageModel)
	{
		return !newPageModel.getDefaultPage();
	}

	protected boolean isPrimaryPage(final ContentPageModel newPageModel)
	{
		return newPageModel.getDefaultPage();
	}

	// ---------------------------------------------------------------------------------------------------
	// Getters/Setters
	// ---------------------------------------------------------------------------------------------------
	protected Predicate<String> getHasPageLabelChangedPredicate()
	{
		return hasPageLabelChangedPredicate;
	}

	@Required
	public void setHasPageLabelChangedPredicate(final Predicate<String> hasPageLabelChangedPredicate)
	{
		this.hasPageLabelChangedPredicate = hasPageLabelChangedPredicate;
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

	protected Predicate<String> getPrimaryPageWithLabelExistsPredicate()
	{
		return primaryPageWithLabelExistsPredicate;
	}

	@Required
	public void setPrimaryPageWithLabelExistsPredicate(final Predicate<String> primaryPageWithLabelExistsPredicate)
	{
		this.primaryPageWithLabelExistsPredicate = primaryPageWithLabelExistsPredicate;
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

	protected Predicate<AbstractPageModel> getPageUpdateRequiresValidationPredicate()
	{
		return pageUpdateRequiresValidationPredicate;
	}

	@Required
	public final void setPageUpdateRequiresValidationPredicate(
			final Predicate<AbstractPageModel> pageUpdateRequiresValidationPredicate)
	{
		this.pageUpdateRequiresValidationPredicate = pageUpdateRequiresValidationPredicate;
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

	protected Predicate<AbstractPageModel> getPageRestoreWithReplacePredicate()
	{
		return pageRestoreWithReplacePredicate;
	}

	@Required
	public void setPageRestoreWithReplacePredicate(final Predicate<AbstractPageModel> pageRestoreWithReplacePredicate)
	{
		this.pageRestoreWithReplacePredicate = pageRestoreWithReplacePredicate;
	}


}

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

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_DOES_NOT_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_LABEL_ALREADY_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_ALREADY_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import java.util.function.Predicate;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the validator for {@link ContentPageModel}
 */
public class DefaultCreateContentPageValidator implements Validator<ContentPageModel>
{
	private Predicate<String> pageExistsPredicate;
	private Predicate<String> primaryPageWithLabelExistsPredicate;
	private ValidationErrorsProvider validationErrorsProvider;
	private Predicate<Object> cloneContextSameAsActiveCatalogVersionPredicate;

	@Override
	public void validate(final ContentPageModel validatee)
	{
		if (getPageExistsPredicate().test(validatee.getUid()))
		{
			getValidationErrorsProvider().getCurrentValidationErrors().add(
					newValidationErrorBuilder() //
							.field(AbstractPageModel.UID) //
							.errorCode(FIELD_ALREADY_EXIST) //
							.build());
		}

		if (getCloneContextSameAsActiveCatalogVersionPredicate().test(validatee))
		{
		    if (Strings.isBlank(validatee.getLabel())) {
                getValidationErrorsProvider().getCurrentValidationErrors().add(
                        newValidationErrorBuilder() //
                                .field(ContentPageModel.LABEL) //
                                .errorCode(FIELD_REQUIRED) //
                                .build());
            }
			else {
				final boolean existsPageWithLabel = getPrimaryPageWithLabelExistsPredicate().test(validatee.getLabel());
				if (validatee.getDefaultPage())
				{
					if (existsPageWithLabel)
					{
						getValidationErrorsProvider().getCurrentValidationErrors().add(
								newValidationErrorBuilder() //
										.field(ContentPageModel.LABEL) //
										.errorCode(DEFAULT_PAGE_LABEL_ALREADY_EXIST) //
										.errorArgs(new Object[]
										{ validatee.getLabel() }) //
										.build());
					}
				}
				else if (!existsPageWithLabel)
				{
					getValidationErrorsProvider().getCurrentValidationErrors().add(
							newValidationErrorBuilder() //
									.field(ContentPageModel.LABEL) //
									.errorCode(DEFAULT_PAGE_DOES_NOT_EXIST) //
									.errorArgs(new Object[]
									{ validatee.getLabel() }) //
									.build());
				}
			}
		}
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

	protected final Predicate<String> getPrimaryPageWithLabelExistsPredicate()
	{
		return primaryPageWithLabelExistsPredicate;
	}

	@Required
	public final void setPrimaryPageWithLabelExistsPredicate(final Predicate<String> primaryPageWithLabelExistsPredicate)
	{
		this.primaryPageWithLabelExistsPredicate = primaryPageWithLabelExistsPredicate;
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

	protected Predicate<Object> getCloneContextSameAsActiveCatalogVersionPredicate()
	{
		return cloneContextSameAsActiveCatalogVersionPredicate;
	}

	@Required
	public void setCloneContextSameAsActiveCatalogVersionPredicate(
			Predicate<Object> cloneContextSameAsActiveCatalogVersionPredicate)
	{
		this.cloneContextSameAsActiveCatalogVersionPredicate = cloneContextSameAsActiveCatalogVersionPredicate;
	}
}

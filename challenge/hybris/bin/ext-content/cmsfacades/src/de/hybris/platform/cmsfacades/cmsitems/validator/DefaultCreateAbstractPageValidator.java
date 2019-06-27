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
import de.hybris.platform.cmsfacades.cmsitems.predicates.CloneContextSameAsActiveCatalogVersionPredicate;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_ALREADY_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DEFAULT_PAGE_DOES_NOT_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_ALREADY_EXIST;


/**
 * Default implementation of the validator for {@link AbstractPageModel}
 */
public class DefaultCreateAbstractPageValidator implements Validator<AbstractPageModel>
{
	private Predicate<String> pageExistsPredicate;
	private Predicate<String> primaryPageWithLabelExistsPredicate;
	private Predicate<AbstractPageModel> pageCanOnlyHaveOnePrimaryPredicate;
	private PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry;
	private ValidationErrorsProvider validationErrorsProvider;
	private Predicate<Object> cloneContextSameAsActiveCatalogVersionPredicate;



	@Override
	public void validate(final AbstractPageModel validatee)
	{
		final PageVariationResolver<AbstractPageModel> pageVariationResolver = getPageVariationResolverTypeRegistry()
				.getPageVariationResolverType(validatee.getItemtype()).get().getResolver();
		final List<AbstractPageModel> defaultPages = pageVariationResolver.findPagesByType(validatee.getItemtype(), true);

		if (getPageExistsPredicate().test(validatee.getUid()))
		{
			getValidationErrorsProvider().getCurrentValidationErrors()
					.add(newValidationErrorBuilder() //
							.field(AbstractPageModel.UID) //
							.errorCode(FIELD_ALREADY_EXIST) //
							.build());
		}

		if (getPageCanOnlyHaveOnePrimaryPredicate().test(validatee) && //
				getCloneContextSameAsActiveCatalogVersionPredicate().test(validatee))
		{
			if (validatee.getDefaultPage())
			{
				if (!defaultPages.isEmpty())
				{
					getValidationErrorsProvider().getCurrentValidationErrors()
							.add(newValidationErrorBuilder() //
									.field(AbstractPageModel.TYPECODE) //
									.errorCode(DEFAULT_PAGE_ALREADY_EXIST) //
									.errorArgs(new Object[]
									{ validatee.getItemtype() }) //
									.build());
				}
			}
			else if (defaultPages.isEmpty())
			{
				getValidationErrorsProvider().getCurrentValidationErrors()
						.add(newValidationErrorBuilder() //
								.field(AbstractPageModel.TYPECODE) //
								.errorCode(DEFAULT_PAGE_DOES_NOT_EXIST) //
								.errorArgs(new Object[]
								{ validatee.getItemtype() }) //
								.build());
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

	protected final PageVariationResolverTypeRegistry getPageVariationResolverTypeRegistry()
	{
		return pageVariationResolverTypeRegistry;
	}

	@Required
	public final void setPageVariationResolverTypeRegistry(
			final PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry)
	{
		this.pageVariationResolverTypeRegistry = pageVariationResolverTypeRegistry;
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
	public void setPageCanOnlyHaveOnePrimaryPredicate(Predicate<AbstractPageModel> pageCanOnlyHaveOnePrimaryPredicate)
	{
		this.pageCanOnlyHaveOnePrimaryPredicate = pageCanOnlyHaveOnePrimaryPredicate;
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

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
package de.hybris.platform.cmsfacades.cmsitems.predicates;

import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.cmsitems.OriginalClonedItemProvider;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given page label maps to an existing primary page.
 * <p>
 * Returns <tt>TRUE</tt> if the page exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class PrimaryPageWithLabelExistsPredicate implements Predicate<String>
{
	private PageVariationResolver<ContentPageModel> pageVariationResolver;

	private OriginalClonedItemProvider originalClonedItemProvider;

	@Override
	public boolean test(final String label)
	{
		final ContentPageModel originalItemModel = (ContentPageModel) getOriginalClonedItemProvider().getCurrentItem();

		return getPageVariationResolver().findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE).stream() //
				.filter(defaultPage -> defaultPage.getLabel().equals(label)) //
				.filter(ContentPageModel::getDefaultPage) //
				.anyMatch(defaultPage -> originalItemModel == null || !defaultPage.getUid().equals(originalItemModel.getUid()));
	}

	protected PageVariationResolver<ContentPageModel> getPageVariationResolver()
	{
		return pageVariationResolver;
	}

	@Required
	public void setPageVariationResolver(final PageVariationResolver<ContentPageModel> pageVariationResolver)
	{
		this.pageVariationResolver = pageVariationResolver;
	}

	protected OriginalClonedItemProvider getOriginalClonedItemProvider()
	{
		return originalClonedItemProvider;
	}

	@Required
	public void setOriginalClonedItemProvider(final OriginalClonedItemProvider originalClonedItemProvider)
	{
		this.originalClonedItemProvider = originalClonedItemProvider;
	}
}

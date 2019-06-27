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

import static java.util.Objects.nonNull;

import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.cmsitems.OriginalClonedItemProvider;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if the label has been changed by comparing the given label to the label in
 * {@ContentPageModel}.
 * <p>
 * Returns <tt>TRUE</tt> if the page label has been modified; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class HasPageLabelChangedPredicate implements Predicate<String>
{
	private OriginalClonedItemProvider originalClonedItemProvider;

	@Override
	public boolean test(final String label)
	{
		final ContentPageModel contentPage = (ContentPageModel) getOriginalClonedItemProvider().getCurrentItem();

		if (nonNull(contentPage))
		{
			return !contentPage.getLabel().equals(label);
		}
		return false;
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

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

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;

import java.util.function.Predicate;

import static java.util.Arrays.asList;


/**
 * Predicate to test if a given page is of a type that allows having only one ACTIVE primary page of that type at a time.
 *
 * <p>
 * Returns <tt>TRUE</tt> if the page can only have one primary; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class PageCanOnlyHaveOnePrimaryPredicate implements Predicate<AbstractPageModel>
{
	@Override
	public boolean test(final AbstractPageModel pageModel)
	{
		return asList(ProductPageModel._TYPECODE, CategoryPageModel._TYPECODE).contains(pageModel.getItemtype());

	}


}

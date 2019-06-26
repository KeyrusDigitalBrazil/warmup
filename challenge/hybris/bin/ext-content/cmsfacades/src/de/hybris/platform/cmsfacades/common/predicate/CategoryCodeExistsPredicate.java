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
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.platform.category.CategoryService;
import org.springframework.beans.factory.annotation.Required;

import java.util.function.Predicate;

/**
 * Predicate to test if the category code exists.
 * <p>
 * Returns <tt>TRUE</tt> if the given category code exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class CategoryCodeExistsPredicate implements Predicate<String>
{
	private CategoryService categoryService;

	@Override
	@SuppressWarnings("squid:S1166")
	public boolean test(String categoryCode)
	{
		try
		{
			getCategoryService().getCategoryForCode(categoryCode);
			return true;
		}
		catch (RuntimeException e)
		{
			return false;
		}
	}

	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}
}

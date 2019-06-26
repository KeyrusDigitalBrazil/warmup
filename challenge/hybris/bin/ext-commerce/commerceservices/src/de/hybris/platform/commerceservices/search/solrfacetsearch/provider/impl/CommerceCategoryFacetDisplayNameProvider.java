/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FacetDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FacetValueDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class CommerceCategoryFacetDisplayNameProvider implements FacetDisplayNameProvider, FacetValueDisplayNameProvider
{
	private CategoryService categoryService;
	private final Map<String, Locale> localeCache = new HashMap<>();

	/**
	 * Gets the locale based on two or one part ISO code.
	 *
	 * @param isoCode
	 *           the iso code
	 *
	 * @return the locale
	 */
	protected Locale getLocale(final String isoCode)
	{
		Locale result;
		result = localeCache.get(isoCode);
		if (result == null)
		{
			final String[] splitted_code = isoCode.split("_");
			if (splitted_code.length == 1)
			{
				result = new Locale(splitted_code[0]);
			}
			else
			{
				result = new Locale(splitted_code[0], splitted_code[1]);
			}

			localeCache.put(isoCode, result);
		}
		return result;
	}

	@Override
	public String getDisplayName(final SearchQuery query, final IndexedProperty property, final String facetValue)
	{
		final Locale locale = getLocale(query.getLanguage());
		String categoryName = null;
		CategoryModel category = null;

		if (query.getCatalogVersions() != null)
		{
			for (final CatalogVersionModel catVersion : query.getCatalogVersions())
			{
				try
				{
					//search for the category in the specific catalog version first
					if (catVersion != null)
					{
						category = getCategoryService().getCategoryForCode(catVersion, facetValue);
						break;
					}
				}
				catch (final UnknownIdentifierException uie)
				{
					//do nothing, because we can still search in active session catalog versions
					continue;
				}
			}
		}

		//search in all active catalog versions in the session
		if (category == null)
		{
			category = getCategory(facetValue);
		}

		if (category != null)
		{
			categoryName = category.getName(locale);
		}
		return categoryName;
	}

	/**
	 * @deprecated Since 5.4.
	 */
	@Deprecated
	@Override
	public String getDisplayName(final SearchQuery query, final String name)
	{
		final Locale locale = getLocale(query.getLanguage());
		String categoryName = null;
		CategoryModel category = null;

		if (query.getCatalogVersions() != null)
		{
			for (final CatalogVersionModel catVersion : query.getCatalogVersions())
			{
				try
				{
					//search for the category in the specific catalog version first
					if (catVersion != null)
					{
						category = getCategoryService().getCategoryForCode(catVersion, name);
						break;
					}
				}
				catch (final UnknownIdentifierException uie)
				{
					//do nothing, because we can still search in active session catalog versions
					continue;
				}
			}
		}

		//search in all active catalog versions in the session
		if (category == null)
		{
			category = getCategory(name);
		}

		if (category != null)
		{
			categoryName = category.getName(locale);
		}
		return categoryName;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	protected CategoryModel getCategory(final String code)
	{
		CategoryModel category = null;
		try
		{
			category = getCategoryService().getCategoryForCode(code);
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.error(e.getMessage());
		}
		return category;
	}
}

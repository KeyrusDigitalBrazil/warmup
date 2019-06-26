/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.hybris.merchandising.service.MerchCatalogService;
import com.hybris.platform.merchandising.yaas.CategoryHierarchy;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import de.hybris.platform.site.BaseSiteService;

/**
 * DefaultMerchCatalogService is a default implementation of {@link MerchCatalogService}, used
 * to make catalog requests for Merch v2 support.
 *
 */
public class DefaultMerchCatalogService implements MerchCatalogService {
	protected BaseSiteService baseSiteService;

	protected AbstractUrlResolver<CategoryModel> categoryUrlResolver;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CategoryHierarchy> getCategories(final String baseSite, final String catalogId, final String catalogVersionId, final String baseCategoryUrl) {
		final BaseSiteModel baseSiteModel = baseSiteService.getBaseSiteForUID(baseSite);
		final List<CatalogModel> productCatalogs = baseSiteService.getProductCatalogs(baseSiteModel);
		final List<CategoryHierarchy> categories = new ArrayList<>();
		final String categoryUrl = StringUtils.isNotEmpty(baseCategoryUrl) ? baseCategoryUrl : "";
		productCatalogs.forEach(cm -> cm.getCatalogVersions()
			.stream().filter(CatalogVersionModel::getActive)
			.forEach(version ->
					version.getRootCategories()
					.forEach(category -> {
						final CategoryHierarchy rootCategory = new CategoryHierarchy();
						rootCategory.setId(category.getCode());
						rootCategory.setName(category.getName());
						rootCategory.setSubcategories(new ArrayList<>());
						rootCategory.setUrl(getUrl(category, categoryUrl));
						processSubCategories(rootCategory, category, true, categoryUrl);
						categories.add(rootCategory);
					}))
		);
		return categories;
	}


	/**
	 * processSubCategories is a method for taking a category model and recursively processing its sub categories.
	 * @param toPopulate the {@link CategoryHierarchy} we wish to populate.
	 * @param category the {@link CategoryModel} representing the hierarchy.
	 * @param root whether the category is a root category or not.
	 * @param categoryUrl the URL to prefix the category with.
	 */
	protected void processSubCategories(final CategoryHierarchy toPopulate, final CategoryModel category, final boolean root, final String categoryUrl)
	{
		if (root)
		{
			category.getCategories().forEach(subCategory -> processSubCategories(toPopulate, subCategory, false, categoryUrl));
		}
		else
		{
			final CategoryHierarchy hierarchy = new CategoryHierarchy();
			hierarchy.setId(category.getCode());
			hierarchy.setName(category.getName());
			hierarchy.setSubcategories(new ArrayList<>());
			hierarchy.setUrl(getUrl(category, categoryUrl));
			toPopulate.getSubcategories().add(hierarchy);
			category.getCategories().forEach(subCategory -> processSubCategories(hierarchy, subCategory, false, categoryUrl));
		}
	}

	/**
	 * getUrl resolves the URL for a provided {@link CategoryModel}.
	 * @param category the category model to retrieve the URL for.
	 * @return a String containing the URL.
	 */
	protected String getUrl(final CategoryModel category, final String baseCategoryUrl) {
		final String resolvedUrl = categoryUrlResolver.resolve(category);
		if(StringUtils.isNotEmpty(resolvedUrl)) {
			return baseCategoryUrl + resolvedUrl;
		}
		return resolvedUrl;
	}

	/**
	 * Gets the configured {@link BaseSiteService}, used to set the current site for the request.
	 * @return the injected base site service to use.
	 */
	protected BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	/**
	 * Sets the configured {@link BaseSiteService}, used to set the current site for the request.
	 * @param baseSiteService the injected base site service to use.
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

	/**
	 * Retrieves the configured URL resolver for {@link CategoryModel}.
	 * @return the configured resolver.
	 */
	public AbstractUrlResolver<CategoryModel> getCategoryUrlResolver() {
		return categoryUrlResolver;
	}

	/**
	 * Sets the configured URL resolver for {@link CategoryModel}.
	 * @param categoryUrlResolver the resolver to return.
	 */
	public void setCategoryUrlResolver(final AbstractUrlResolver<CategoryModel> categoryUrlResolver) {
		this.categoryUrlResolver = categoryUrlResolver;
	}
}

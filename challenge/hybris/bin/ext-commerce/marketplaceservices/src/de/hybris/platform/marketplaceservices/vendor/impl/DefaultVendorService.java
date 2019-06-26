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
package de.hybris.platform.marketplaceservices.vendor.impl;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.marketplaceservices.model.VendorUserModel;
import de.hybris.platform.marketplaceservices.strategies.IndexedVendorsLookupStrategy;
import de.hybris.platform.marketplaceservices.strategies.VendorActivationStrategy;
import de.hybris.platform.marketplaceservices.strategies.VendorCreationStrategy;
import de.hybris.platform.marketplaceservices.strategies.VendorDeactivationStrategy;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.marketplaceservices.vendor.daos.VendorDao;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.user.daos.UserDao;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation for {@link VendorService}.
 */
public class DefaultVendorService implements VendorService
{
	private VendorDao vendorDao;

	private VendorActivationStrategy vendorActivationStrategy;

	private VendorDeactivationStrategy vendorDeactivationStrategy;

	private VendorCreationStrategy vendorCreationStrategy;

	private CategoryService categoryService;

	private IndexedVendorsLookupStrategy indexedVendorsLookupStrategy;
	
	private UserDao userDao;

	@Override
	public Optional<VendorModel> getVendorByCode(final String code)
	{
		return getVendorDao().findVendorByCode(code);
	}
	
	@Override
	public Optional<VendorModel> getVendorByUserId(final String userId)
	{
		UserModel user = this.userDao.findUserByUID(userId);
		return (user instanceof VendorUserModel) ? Optional.ofNullable(((VendorUserModel) user).getVendor())
					: Optional.empty();
	}

	@Override
	public Set<VendorModel> getActiveVendors()
	{
		return new HashSet<>(getVendorDao().findActiveVendors());
	}

	@Override
	public void deactivateVendor(final VendorModel vendor)
	{
		Assert.notNull(vendor, "Parameter vendor cannot be null.");

		getVendorDeactivationStrategy().deactivateVendor(vendor);
	}

	@Override
	public void activateVendor(final VendorModel vendor)
	{
		Assert.notNull(vendor, "Parameter vendor cannot be null.");

		getVendorActivationStrategy().activateVendor(vendor);
	}

	@Override
	public Set<CatalogModel> getActiveCatalogs()
	{
		return new HashSet<>(getVendorDao().findActiveCatalogs());
	}

	@Override
	public Set<CatalogVersionModel> getActiveProductCatalogVersions()
	{
		return new HashSet<>(getVendorDao().findActiveCatalogVersions());
	}

	@Override
	public Optional<VendorModel> getVendorByProduct(final ProductModel product)
	{
		return getVendorDao().findVendorByProduct(product);
	}

	@Override
	public Optional<VendorModel> getVendorForConsignmentCode(final String consignmentCode)
	{
		return getVendorDao().findVendorByConsignmentCode(consignmentCode);
	}

	@Override
	public void createVendor(final VendorModel vendor, final boolean useCustomPage)
	{
		getVendorCreationStrategy().createVendor(vendor, useCustomPage);
	}

	@Override
	public Collection<CategoryModel> getVendorCategories(final String vendorCode)
	{
		final Collection<CategoryModel> allVendorCategories = new HashSet<>();
		getVendorDao().findVendorByCode(vendorCode).ifPresent(vendor -> {
			final Collection<CategoryModel> directCategories = vendor.getCategories();
			allVendorCategories.addAll(directCategories);
			allVendorCategories
					.addAll(directCategories.stream().map(category -> getCategoryService().getAllSubcategoriesForCategory(category))
							.flatMap(categories -> categories.stream()).collect(Collectors.toSet()));
		});
		return allVendorCategories;
	}

	@Override
	public SearchPageData<VendorModel> getIndexVendors(final PageableData pageableData)
	{
		return getIndexedVendorsLookupStrategy().getIndexVendors(pageableData);
	}

	protected VendorDao getVendorDao()
	{
		return vendorDao;
	}
	
	protected UserDao getUserDao()
	{
		return userDao;
	}
	
	@Required
	public void setUserDao(UserDao userDao)
	{
		this.userDao = userDao;
	}

	@Required
	public void setVendorDao(final VendorDao vendorDao)
	{
		this.vendorDao = vendorDao;
	}

	protected VendorActivationStrategy getVendorActivationStrategy()
	{
		return vendorActivationStrategy;
	}

	@Required
	public void setVendorActivationStrategy(final VendorActivationStrategy vendorActivationStrategy)
	{
		this.vendorActivationStrategy = vendorActivationStrategy;
	}

	protected VendorDeactivationStrategy getVendorDeactivationStrategy()
	{
		return vendorDeactivationStrategy;
	}

	@Required
	public void setVendorDeactivationStrategy(final VendorDeactivationStrategy vendorDeactivationStrategy)
	{
		this.vendorDeactivationStrategy = vendorDeactivationStrategy;
	}

	protected VendorCreationStrategy getVendorCreationStrategy()
	{
		return vendorCreationStrategy;
	}

	@Required
	public void setVendorCreationStrategy(final VendorCreationStrategy vendorCreationStrategy)
	{
		this.vendorCreationStrategy = vendorCreationStrategy;
	}


	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	protected IndexedVendorsLookupStrategy getIndexedVendorsLookupStrategy()
	{
		return indexedVendorsLookupStrategy;
	}

	@Required
	public void setIndexedVendorsLookupStrategy(final IndexedVendorsLookupStrategy indexedVendorsLookupStrategy)
	{
		this.indexedVendorsLookupStrategy = indexedVendorsLookupStrategy;
	}

}

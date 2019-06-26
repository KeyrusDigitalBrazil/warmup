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
package de.hybris.platform.marketplaceservices.vendor;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;


/**
 * Service with Vendor related methods
 */
public interface VendorService
{
	/**
	 * Get vendor for a given code
	 *
	 * @param vendorCode
	 *           vendor code
	 * @return VendorModel if vendor exist otherwise return empty option
	 */
	Optional<VendorModel> getVendorByCode(final String vendorCode);

	/**
	 * Get vendor for a given userId
	 *
	 * @param userId
	 *           user name
	 * @return VendorModel if vendor exist otherwise return empty option
	 */
	Optional<VendorModel> getVendorByUserId(final String userId);
	
	/**
	 * Get all active vendors
	 *
	 * @return all active vendors
	 */
	Set<VendorModel> getActiveVendors();

	/**
	 * Deactivate a specific vendor
	 *
	 * @param vendor
	 *           the specific vendor
	 */
	void deactivateVendor(final VendorModel vendor);

	/**
	 * Activate a specific vendor
	 *
	 * @param vendor
	 *           the specific vendor
	 */
	void activateVendor(final VendorModel vendor);

	/**
	 * Find all active catalog version including product catalog and classification.
	 *
	 * @return Set of CatalogModel
	 */
	Set<CatalogModel> getActiveCatalogs();

	/**
	 * Find all active product active catalogVersions
	 *
	 * @return Set of Active Catalog Version
	 */
	Set<CatalogVersionModel> getActiveProductCatalogVersions();

	/**
	 * Find vendor that the given product belongs to
	 *
	 * @param product
	 *           product to check
	 * @return An optional containing the specific vendor or an empty optional otherwise
	 */
	Optional<VendorModel> getVendorByProduct(final ProductModel product);

	/**
	 * Find vendor that the given consignment belongs to
	 *
	 * @param consignmentCode
	 *           code of the consignment
	 * @return An optional containing the specific vendor or an empty optional otherwise
	 */
	Optional<VendorModel> getVendorForConsignmentCode(final String consignmentCode);

	/**
	 * Initialize vendor data for create vendor.
	 *
	 * @param vendor
	 *           the target vendor model to save
	 * @param useCustomPage
	 *           if true will assign a vendor page to this vendor.
	 */
	void createVendor(final VendorModel vendor, final boolean useCustomPage);

	/**
	 * Find all categories and subcategories belongs to given vendor recursively
	 *
	 * @param vendorCode
	 *           code of vendor
	 * @return collection of category
	 */
	Collection<CategoryModel> getVendorCategories(final String vendorCode);

	/**
	 * Get expected vendors shown in index page
	 *
	 * @param pageableData
	 *           the pagination data
	 * @return all expected vendors
	 */
	SearchPageData<VendorModel> getIndexVendors(final PageableData pageableData);
}

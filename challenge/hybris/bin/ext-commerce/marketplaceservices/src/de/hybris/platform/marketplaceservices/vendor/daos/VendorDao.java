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
package de.hybris.platform.marketplaceservices.vendor.daos;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.List;
import java.util.Optional;



/**
 * Dao with Vendor related methods
 */
public interface VendorDao
{
	/**
	 * Find VendorModel by given code
	 *
	 * @param vendorCode
	 * @return VendorModel otherwise empty option
	 */
	Optional<VendorModel> findVendorByCode(String vendorCode);

	/**
	 * Find all active vendors
	 *
	 * @return the list of all active vendors
	 */
	List<VendorModel> findActiveVendors();

	/**
	 * Find vendor for given product
	 *
	 * @param product
	 *           product to check
	 * @return VendorModel otherwise empty option
	 */
	Optional<VendorModel> findVendorByProduct(ProductModel product);

	/**
	 * Find all catalogs belongs to active vendors
	 *
	 * @return list of all active catalogs
	 */
	List<CatalogModel> findActiveCatalogs();

	/**
	 * Final all active catalog versions belongs to active vendors
	 *
	 * @return list of all active catalog versions
	 */
	List<CatalogVersionModel> findActiveCatalogVersions();

	/**
	 * Find vendor for given consignment
	 *
	 * @param consignmentCode
	 *           code of the consignment
	 * @return VendorModel otherwise empty option
	 */
	Optional<VendorModel> findVendorByConsignmentCode(String consignmentCode);

	/**
	 * find consignmententries in WAITING status for a vendor
	 *
	 * @param vendorCode
	 *           the vendor's code
	 * @return list of consignmententries
	 */
	List<ConsignmentEntryModel> findPendingConsignmentEntryForVendor(String vendorCode);


	/**
	 * Find all active vendors
	 *
	 * @param pageableData
	 *           the pagination data
	 * @return paging result of all active vendors
	 */
	SearchPageData<VendorModel> findPagedActiveVendors(PageableData pageableData);
}

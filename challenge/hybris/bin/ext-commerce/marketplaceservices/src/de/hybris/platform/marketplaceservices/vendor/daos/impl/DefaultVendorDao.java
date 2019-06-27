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
package de.hybris.platform.marketplaceservices.vendor.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.marketplaceservices.vendor.daos.VendorDao;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link VendorDao}.
 */
public class DefaultVendorDao extends DefaultGenericDao<VendorModel> implements VendorDao
{
	protected static final String VENDOR_CODE = "code";
	protected static final String PRODUCT_CODE = "code";
	protected static final String CONSIGNMENT_CODE = "code";
	protected static final String CATEGORY_TO_VENDOR_RELATION = "Category2VendorRelation";
	protected static final String ORDER_BY_NAME_ASC = " ORDER BY {NAME} ASC";
	protected static final String ORDER_BY_NAME_DESC = " ORDER BY {NAME} DESC";

	protected static final String FIND_VENDOR_FOR_ID = "SELECT {" + VendorModel.PK + "} FROM {" + VendorModel._TYPECODE
			+ "} WHERE {" + VendorModel.CODE + "} = ?code";

	protected static final String FIND_ACTIVE_VENDORS = "SELECT {" + VendorModel.PK + "} FROM {" + VendorModel._TYPECODE
			+ "} WHERE {" + VendorModel.ACTIVE + "} = 1";

	protected static final String FIND_VENDOR_BY_PRODUCT_CODE = "SELECT {" + VendorModel.PK + "} FROM {" + VendorModel._TYPECODE
			+ "} WHERE {" + VendorModel.CATALOG + "} IN " + "({{ SELECT {" + ProductModel.CATALOG + "} FROM {"
			+ ProductModel._TYPECODE + "} WHERE {" + ProductModel.CODE + "}=?" + PRODUCT_CODE + " }})";

	protected static final String FIND_VENDOR_BY_CONSIGNMENT_CODE = "SELECT {v:" + VendorModel.PK + "} FROM {"
			+ ConsignmentModel._TYPECODE + " AS c JOIN " + WarehouseModel._TYPECODE + " AS w ON {c:" + ConsignmentModel.WAREHOUSE
			+ "} = {w:pk} JOIN " + VendorModel._TYPECODE + " AS v ON {v:pk} = {w:" + WarehouseModel.VENDOR + "}} WHERE {c:"
			+ ConsignmentModel.CODE + "} = ?" + CONSIGNMENT_CODE;

	protected static final String FIND_ACTIVE_CATALOGS = "SELECT {" + VendorModel.CATALOG + "} FROM {" + VendorModel._TYPECODE
			+ "} WHERE {" + VendorModel.ACTIVE + "} = 1 and {" + VendorModel.CATALOG + "} IS NOT NULL";

	protected static final String FIND_ACTIVE_CATALOGVERSIONS = "SELECT {a:" + CatalogModel.ACTIVECATALOGVERSION + "} FROM {"
			+ CatalogModel._TYPECODE + " AS a " + "JOIN " + VendorModel._TYPECODE + " AS b on {a:" + CatalogModel.PK + "} = {b:"
			+ VendorModel.CATALOG + "}} " + "where {b:" + VendorModel.ACTIVE + "}=1 and {b:" + VendorModel.CATALOG
			+ "} IS NOT NULL ";

	protected static final String FIND_PENDING_CONSIGNMENTENTRY_FOR_VENDOR = "SELECT {ce:" + ConsignmentEntryModel.PK + "} FROM {"
			+ VendorModel._TYPECODE + " AS v JOIN " + WarehouseModel._TYPECODE + " AS w ON {v:" + VendorModel.PK + "} = {w:"
			+ WarehouseModel.VENDOR + "} JOIN " + ConsignmentModel._TYPECODE + " AS c ON {c:" + ConsignmentModel.WAREHOUSE
			+ "} = {w:" + WarehouseModel.PK + "} JOIN " + ConsignmentStatus._TYPECODE + " AS cs ON {cs:pk} = {c:"
			+ ConsignmentModel.STATUS + "} JOIN " + ConsignmentEntryModel._TYPECODE + " AS ce ON {ce:"
			+ ConsignmentEntryModel.CONSIGNMENT + "} = {c:" + ConsignmentModel.PK + " }}  WHERE {cs:code} = 'WAITING' AND {v:"
			+ VendorModel.CODE + "} = ?" + VENDOR_CODE;

	private PagedFlexibleSearchService pagedFlexibleSearchService;

	public DefaultVendorDao()
	{
		super(VendorModel._TYPECODE);
	}

	@Override
	public Optional<VendorModel> findVendorByCode(final String vendorCode)
	{
		validateParameterNotNull(vendorCode, "Vendor code must not be null");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_VENDOR_FOR_ID);
		query.addQueryParameter(VENDOR_CODE, vendorCode);
		return findUnique(query);
	}

	@Override
	public List<VendorModel> findActiveVendors()
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ACTIVE_VENDORS);
		return getFlexibleSearchService().<VendorModel> search(query).getResult();
	}

	@Override
	public Optional<VendorModel> findVendorByProduct(final ProductModel product)
	{
		validateParameterNotNull(product, "Product must not be null");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_VENDOR_BY_PRODUCT_CODE);
		query.addQueryParameter(PRODUCT_CODE, product.getCode());
		return findUnique(query);
	}

	@Override
	public Optional<VendorModel> findVendorByConsignmentCode(final String consignmentCode)
	{
		validateParameterNotNull(consignmentCode, "Consignment code must not be null");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_VENDOR_BY_CONSIGNMENT_CODE);
		query.addQueryParameter(CONSIGNMENT_CODE, consignmentCode);
		return findUnique(query);
	}

	@Override
	public List<CatalogModel> findActiveCatalogs()
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ACTIVE_CATALOGS);
		return getFlexibleSearchService().<CatalogModel> search(query).getResult();
	}

	@Override
	public List<CatalogVersionModel> findActiveCatalogVersions()
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ACTIVE_CATALOGVERSIONS);
		return getFlexibleSearchService().<CatalogVersionModel> search(query).getResult();
	}

	@Override
	public List<ConsignmentEntryModel> findPendingConsignmentEntryForVendor(final String vendorCode)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_PENDING_CONSIGNMENTENTRY_FOR_VENDOR);
		query.addQueryParameter(VENDOR_CODE, vendorCode);
		return getFlexibleSearchService().<ConsignmentEntryModel> search(query).getResult();
	}

	@Override
	public SearchPageData<VendorModel> findPagedActiveVendors(final PageableData pageableData)
	{
		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData("byNameAsc", FIND_ACTIVE_VENDORS + ORDER_BY_NAME_ASC),
				createSortQueryData("byNameDesc", FIND_ACTIVE_VENDORS + ORDER_BY_NAME_DESC));
		final Map<String, Object> queryParams = new HashMap<>();
		return getPagedFlexibleSearchService().<VendorModel> search(sortQueries, "byNameAsc", queryParams, pageableData);
	}

	protected Optional<VendorModel> findUnique(final FlexibleSearchQuery query)
	{
		try
		{
			return Optional.ofNullable(getFlexibleSearchService().searchUnique(query));
		}
		catch (final ModelNotFoundException e)
		{
			return Optional.empty();
		}
	}

	protected SortQueryData createSortQueryData(final String sortCode, final String query)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(query);
		return result;
	}

	protected PagedFlexibleSearchService getPagedFlexibleSearchService()
	{
		return pagedFlexibleSearchService;
	}

	@Required
	public void setPagedFlexibleSearchService(final PagedFlexibleSearchService pagedFlexibleSearchService)
	{
		this.pagedFlexibleSearchService = pagedFlexibleSearchService;
	}

}

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
package de.hybris.platform.marketplaceservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.marketplaceservices.dao.MarketplaceProductDao;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.List;


/**
 * Default implementation for {@link MarketplaceProductDao}.
 */
public class DefaultMarketplaceProductDao extends AbstractItemDao implements MarketplaceProductDao
{

	protected static final String VENDOR_CODE = "code";

	protected static final String FIND_ALL_PRODUCT_BY_VENDOR = "select {A:" + ProductModel.PK + "} from {" + ProductModel._TYPECODE
			+ " as A JOIN " + VendorModel._TYPECODE + " AS B ON {A:" + ProductModel.CATALOG + "}={B:" + VendorModel.CATALOG
			+ "}} WHERE {A:" + ProductModel.SALEABLE + "}=true and {A:" + ProductModel.APPROVALSTATUS + "}=true " + "and {B:"
			+ VendorModel.CODE + "}=?" + VENDOR_CODE;

	@Override
	public List<ProductModel> findAllProductByVendor(final String vendorCode)
	{
		validateParameterNotNull(vendorCode, "Vendor code not be null!");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ALL_PRODUCT_BY_VENDOR);
		query.addQueryParameter(VENDOR_CODE, vendorCode);
		return getFlexibleSearchService().<ProductModel> search(query).getResult();
	}

}

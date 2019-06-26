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
package de.hybris.platform.marketplacefacades;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;


/**
 * A facade for product searching in vendor homepage.
 */
public interface VendorProductSearchFacade extends ProductSearchFacade<ProductData>
{

	/**
	 * get categories data from facet data for setting to vendor data
	 *
	 * @param vendorCode
	 *           the target vendor data to set categories
	 * @return the vendor data contains categories data
	 */
	VendorData getVendorCategories(String vendorCode);
}

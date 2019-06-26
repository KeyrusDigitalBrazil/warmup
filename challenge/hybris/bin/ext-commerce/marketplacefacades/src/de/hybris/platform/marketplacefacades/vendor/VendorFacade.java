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
package de.hybris.platform.marketplacefacades.vendor;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;

import java.util.Optional;


/**
 * Vendor facade interface. An Vendor Facade should provide access to a vendor's vendor users.
 */
public interface VendorFacade
{

	/**
	 * Get vendor data for a given code
	 *
	 * @param vendorCode
	 *           vendor code
	 * @return VendorData {@link de.hybris.platform.marketplacefacades.vendor.data.VendorData} if vendor exists otherwise
	 *         return empty option
	 */
	Optional<VendorData> getVendorByCode(final String vendorCode);

	/**
	 * Get paged vendors need to shown in index page
	 *
	 * @param pageableData
	 *           the pagination data
	 * @return the vendorData need to shown in index page
	 */
	SearchPageData<VendorData> getPagedIndexVendors(final PageableData pageableData);

}

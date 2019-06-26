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

import de.hybris.platform.commercefacades.product.data.VendorReviewData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


/**
 * Vendor review facade interface. An Vendor Facade should provide access to a vendor's vendor users.
 */
public interface CustomerVendorReviewFacade
{

	/**
	 * Add a vendor review
	 *
	 * @param orderCode
	 *           order code
	 * @param consignmentCode
	 *           consignment code
	 * @param reviewData
	 *           review data
	 */
	void postReview(String orderCode, String consignmentCode, VendorReviewData reviewData);

	/**
	 * Check whether a review for a consignment has been posted by current user
	 *
	 * @param consignmentCode
	 *           consignment code
	 * @return true if a review has been posted and false otherwise
	 */
	boolean postedReview(String consignmentCode);

	/**
	 * Get all approved paged reviews for a particular vendor
	 *
	 * @param vendorCode
	 *           code of vendor
	 * @param pageableData
	 *           the pagination data
	 * @return paging result of reviews
	 */
	SearchPageData<VendorReviewData> getPagedReviewsForVendor(String vendorCode, PageableData pageableData);
}

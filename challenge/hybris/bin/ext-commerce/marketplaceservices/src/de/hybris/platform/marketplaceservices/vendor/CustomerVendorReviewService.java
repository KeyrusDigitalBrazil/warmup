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

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.marketplaceservices.model.CustomerVendorReviewModel;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.Collection;


/**
 * Service with CustomerVendorReview related methods
 */
public interface CustomerVendorReviewService
{
	/**
	 * Find all approved reviews for a particular vendor
	 *
	 * @param vendor
	 *           the target vendor
	 *
	 * @return all approved reviews for this vendor
	 */
	Collection<CustomerVendorReviewModel> getReviewsForVendor(VendorModel vendor);

	/**
	 * Create a review
	 *
	 * @param vendorReview
	 *           vendor review model
	 * @return the created review
	 */
	CustomerVendorReviewModel createReview(final CustomerVendorReviewModel vendorReview);

	/**
	 * Check whether a review for a consignment has been posted by a user
	 *
	 * @param consignmentCode
	 *           consignment code
	 * @param user
	 *           user model
	 * @return true if a review has been posted and false otherwise
	 */
	boolean postedReview(String consignmentCode, UserModel user);

	/**
	 * Get all approved paged reviews for a particular vendor
	 *
	 * @param vendorCode
	 *           code of vendor
	 * @param language
	 *           current language
	 * @param pageableData
	 *           the pagination data
	 * @return paging result of reviews
	 */
	SearchPageData<CustomerVendorReviewModel> getPagedReviewsForVendor(String vendorCode, LanguageModel language,
			PageableData pageableData);

}

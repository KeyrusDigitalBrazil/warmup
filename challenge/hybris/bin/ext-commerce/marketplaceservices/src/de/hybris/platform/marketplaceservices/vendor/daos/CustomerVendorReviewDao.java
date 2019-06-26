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

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.marketplaceservices.model.CustomerVendorReviewModel;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.Collection;


/**
 * Dao with CustomerVendorReviewDao related methods
 */
public interface CustomerVendorReviewDao
{

	/**
	 * Find all approved reviews for a particular vendor
	 *
	 * @return all approved reviews for this vendor
	 */
	Collection<CustomerVendorReviewModel> findReviewsForVendor(VendorModel vendor);

	/**
	 * Check whether the review has been posted
	 *
	 * @param consignmentCode
	 *           consignment code
	 * @param user
	 *           User model
	 * @return true if a review has been posted and false otherwise
	 */
	boolean postedReview(String consignmentCode, UserModel user);

	/**
	 * Find all approved paged reviews for a particular vendor
	 *
	 * @param vendorCode
	 *           code of vendor
	 * @param language
	 *           current language
	 * @param pageableData
	 *           the pagination data
	 * @return paging result of reviews
	 */
	SearchPageData<CustomerVendorReviewModel> findPagedReviewsForVendor(String vendorCode, LanguageModel language,
			PageableData pageableData);

	/**
	 * Find all reviews for a particular user
	 * 
	 * @param user
	 *           the given user
	 * @return all reviews for this user
	 */
	Collection<CustomerVendorReviewModel> findReviewsByUser(UserModel user);

}

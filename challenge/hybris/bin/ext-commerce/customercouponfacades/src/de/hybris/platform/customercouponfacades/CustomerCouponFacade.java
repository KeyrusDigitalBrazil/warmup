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
package de.hybris.platform.customercouponfacades;

import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponSearchPageData;
import de.hybris.platform.customercouponfacades.emums.AssignCouponResult;

import java.util.List;


/**
 * Deals with customer coupon related DTOs using existing service
 */
public interface CustomerCouponFacade
{

	/**
	 * Gets paginated customer coupon data by pageableData
	 *
	 * @param pageableData
	 *           the data used for pagination
	 * @return the paginated customer coupon data
	 */
	de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponData> getPagedCouponsData(
			final PageableData pageableData);

	/**
	 * Assigns customer coupon to the current customer
	 *
	 * @param couponCode
	 *           the customer coupon code
	 * @return the assigning result
	 */
	AssignCouponResult grantCouponAccessForCurrentUser(final String couponCode);

	/**
	 * Gets customer coupon data of the current customer
	 * 
	 * @return the list of CustomerCouponData
	 */
	List<CustomerCouponData> getCouponsData();

	/**
	 * Saves customer coupon notification
	 *
	 * @param couponCode
	 *           the coupon code
	 */
	void saveCouponNotification(final String couponCode);

	/**
	 * Removes customer coupon notification
	 *
	 * @param couponCode
	 *           the coupon code
	 */
	void removeCouponNotificationByCode(final String couponCode);

	/**
	 * Gets assignable customer coupon data
	 *
	 * @param text
	 *           the text used for searching assignable customer coupons
	 * @return the list of search results
	 */
	List<CustomerCouponData> getAssignableCustomerCoupons(final String text);

	/**
	 * Gets assigned customer coupon data
	 *
	 * @param text
	 *           the text used for searching assigned customer coupons
	 * @return the list of search results
	 */
	List<CustomerCouponData> getAssignedCustomerCoupons(final String text);

	/**
	 * Releases the specific customer coupon of the current customer
	 *
	 * @param couponCode
	 *           the coupon code
	 * @throws VoucherOperationException
	 *            throw when release voucher failed
	 */
	void releaseCoupon(final String couponCode) throws VoucherOperationException;

	/**
	 * Gets customer coupon data by coupon id
	 *
	 * @param couponId
	 *           the coupon id
	 * @return the CustomerCouponData
	 */
	CustomerCouponData getCustomerCouponForCode(final String couponId);

	/**
	 * Checks if the specific customer coupon is owned by the current customer
	 *
	 * @param couponCode
	 *           the coupon code
	 * @return true if the coupon is owned by the current customer and false otherwise
	 */
	boolean isCouponOwnedByCurrentUser(String couponCode);

	/**
	 * Gets paginated customer coupon data by searchPageData
	 *
	 * @param searchPageData
	 *           the data used for pagination
	 * @return the paginated customer coupon data
	 */
	CustomerCouponSearchPageData getPaginatedCoupons(SearchPageData searchPageData);

	/**
	 * Gets valid customer coupon data by coupon code
	 *
	 * @param code
	 *           the coupon code
	 * @return the valid CustomerCouponData
	 */
	CustomerCouponData getValidCouponForCode(final String code);
}

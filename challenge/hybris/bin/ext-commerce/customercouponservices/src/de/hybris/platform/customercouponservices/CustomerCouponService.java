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
package de.hybris.platform.customercouponservices;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.couponservices.services.CouponService;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;

import java.util.List;
import java.util.Optional;


/**
 * Deals with customer coupon related Models using existing DAOs
 */
public interface CustomerCouponService extends CouponService
{
	/**
	 * Gets paginated customer coupon models for the customer by pageableData
	 *
	 * @param customer
	 *           the customer model
	 * @param pageableData
	 *           the data used for pagination
	 * @return the paginated customer coupon models
	 */
	de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponModel> getCustomerCouponsForCustomer(
			final CustomerModel customer, final PageableData pageableData);

	/**
	 * Gets promotion source rule by coupon code
	 *
	 * @param couponCode
	 *           the coupon code
	 * @return the list of PromotionSourceRuleModel
	 */
	List<PromotionSourceRuleModel> getPromotionSourceRuleForCouponCode(final String couponCode);

	/**
	 * Assigns the customer coupon to the current customer
	 *
	 * @param couponCode
	 *           the coupon code
	 * @param customer
	 *           the customer model
	 */
	void assignCouponToCustomer(final String couponCode, final CustomerModel customer);

	/**
	 * Gets promotion source rules by product code
	 *
	 * @param productCode
	 *           the product code
	 * @return the list of PromotionSourceRuleModel
	 *
	 */
	List<PromotionSourceRuleModel> getPromotionSourceRulesForProduct(final String productCode);

	/**
	 * Gets excluded promotion source rules by product code
	 *
	 * @param productCode
	 *           the product code
	 * @return the list of PromotionSourceRuleModel
	 *
	 */
	List<PromotionSourceRuleModel> getExclPromotionSourceRulesForProduct(final String productCode);

	/**
	 * Gets promotion source rules by category
	 *
	 * @param categoryCode
	 *           the category code
	 * @return the list of PromotionSourceRuleModel
	 *
	 */
	List<PromotionSourceRuleModel> getPromotionSourceRulesForCategory(final String categoryCode);

	/**
	 * Gets excluded promotion source rules by category
	 *
	 * @param categoryCode
	 *           the category code
	 * @return the list of PromotionSourceRuleModel
	 *
	 */
	List<PromotionSourceRuleModel> getExclPromotionSourceRulesForCategory(final String categoryCode);

	/**
	 * Gets customer coupon codes by promotion source rule
	 *
	 * @param code
	 *           the promotion source rule code
	 * @return the list of customer coupon codes
	 */
	List<String> getCouponCodeForPromotionSourceRule(final String code);

	/**
	 * Counts the number of products and categories mapped with promotion source rule
	 *
	 * @param code
	 *           the promotion source rule code
	 * @return the total number of products and categories mapped with promotion source rule
	 */
	int countProductOrCategoryForPromotionSourceRule(final String code);

	/**
	 * Gets valid customer coupon models by coupon code
	 *
	 * @param couponCode
	 *           the coupon code
	 * @return the optional of CustomerCouponModel
	 */
	Optional<CustomerCouponModel> getValidCustomerCouponByCode(final String couponCode);

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
	 * Gets coupon notification status by coupon code
	 *
	 * @param couponCode
	 *           the coupon code
	 * @return true if the current customer has subscribed to the coupon notification and false otherwise
	 */
	boolean getCouponNotificationStatus(final String couponCode);

	/**
	 * Removes customer coupon for the customer
	 *
	 * @param couponCode
	 *           the coupon code
	 * @param customer
	 *           the customer model
	 */
	void removeCouponForCustomer(final String couponCode, final CustomerModel customer);

	/**
	 * Gets effective customer coupons of the customer
	 *
	 * @param customer
	 *           the customer model
	 * @return the list of CustomerCouponModel
	 */
	List<CustomerCouponModel> getEffectiveCustomerCouponsForCustomer(final CustomerModel customer);

	/**
	 * Gets assignable customer coupons
	 *
	 * @param customer
	 *           the customer model
	 * @param text
	 *           the text used for searching assignable customer coupons
	 * @return the list of CustomerCouponModel
	 */
	List<CustomerCouponModel> getAssignableCustomerCoupons(final CustomerModel customer, final String text);

	/**
	 * Gets assigned customer coupons
	 *
	 * @param customer
	 *           the customer model
	 * @param text
	 *           the text used for searching assigned customer coupons
	 * @return the list of CustomerCouponModel
	 */
	List<CustomerCouponModel> getAssignedCustomerCouponsForCustomer(final CustomerModel customer, final String text);

	/**
	 * Gets customer coupon models by coupon code
	 *
	 * @param couponCode
	 *           the coupon code
	 * @return the optional of CustomerCouponModel, single and multi coupon will return null
	 */
	Optional<CustomerCouponModel> getCustomerCouponForCode(final String couponCode);

	/**
	 * Gets paginated customer coupon data by searchPageData
	 *
	 * @param customer
	 *           the customer model
	 * @param searchPageData
	 *           the data used for pagination
	 * @return the paginated customer coupon models
	 */
	SearchPageData<CustomerCouponModel> getPaginatedCouponsForCustomer(final CustomerModel customer,
			final SearchPageData searchPageData);

	/**
	 * Gets coupon notifications for the customer
	 * 
	 * @param customer
	 *           the customer model
	 * @return the list of CouponNotificationModel
	 */
	List<CouponNotificationModel> getCouponNotificationsForCustomer(CustomerModel customer);

	/**
	 * Gets promotionSourceRule for categories of product
	 *
	 * @param product
	 *           the product model
	 * @return the list of PromotionSourceRuleModel
	 */
	List<PromotionSourceRuleModel> getPromotionSourcesRuleForProductCategories(ProductModel product);
}

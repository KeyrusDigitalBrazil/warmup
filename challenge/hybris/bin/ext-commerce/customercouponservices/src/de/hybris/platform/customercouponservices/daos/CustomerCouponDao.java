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
package de.hybris.platform.customercouponservices.daos;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customercouponservices.model.CustomerCouponForPromotionSourceRuleModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.promotionengineservices.model.CatForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.ProductForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;

import java.util.List;
import java.util.Optional;


/**
 * Looks up items related to customer coupon
 */
public interface CustomerCouponDao
{

	/**
	 * Finds paginated customer coupons
	 *
	 * @param customer
	 *           the customer model
	 * @param pageableData
	 *           the data used for pagination
	 * @return the list of paginated CustomerCouponModel
	 */
	de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponModel> findCustomerCouponsByCustomer(
			CustomerModel customer, PageableData pageableData);

	/**
	 * Finds promotion source rules by rule code
	 *
	 * @param code
	 *           the rule code
	 * @return the optional of PromotionSourceRuleModel
	 */
	Optional<PromotionSourceRuleModel> findPromotionSourceRuleByCode(String code);

	/**
	 * Finds promotion source rules by product code
	 *
	 * @param productCode
	 *           the product code
	 * @return the list of PromotionSourceRuleModel
	 */
	List<PromotionSourceRuleModel> findPromotionSourceRuleByProduct(String productCode);

	/**
	 * Finds excluded promotion source rules by product code
	 *
	 * @param productCode
	 *           the product code
	 * @return the list of PromotionSourceRuleModel
	 */
	List<PromotionSourceRuleModel> findExclPromotionSourceRuleByProduct(final String productCode);

	/**
	 * Finds promotion source rules by category
	 *
	 * @param categoryCode
	 *           the category code
	 * @return the list of PromotionSourceRuleModel
	 */
	List<PromotionSourceRuleModel> findPromotionSourceRuleByCategory(String categoryCode);

	/**
	 * Finds excluded promotion source rules by category
	 *
	 * @param categoryCode
	 *           the category code
	 * @return the list of PromotionSourceRuleModel
	 */
	List<PromotionSourceRuleModel> findExclPromotionSourceRuleByCategory(String categoryCode);

	/**
	 * Finds customer coupons by promotion source rule
	 *
	 * @param code
	 *           the promotion source rule code
	 * @return the list of CustomerCouponModel
	 */
	List<CustomerCouponModel> findCustomerCouponByPromotionSourceRule(final String code);

	/**
	 * Finds promotion source rules by customer coupon code
	 *
	 * @param coupon
	 *           the coupon code
	 * @return the list of PromotionSourceRuleModel
	 */
	List<PromotionSourceRuleModel> findPromotionSourceRuleByCouponCode(final String code);

	/**
	 * Finds product for promotion source rules by code
	 *
	 * @param code
	 *           the promotion source rule code
	 * @return the list of ProductForPromotionSourceRuleModel
	 */
	List<ProductForPromotionSourceRuleModel> findProductForPromotionSourceRuleByPromotion(String code);

	/**
	 * Finds category for promotion source rules by promotion source rule
	 *
	 * @param code
	 *           the promotion source rule code
	 * @return the list of CatForPromotionSourceRuleModel
	 */
	List<CatForPromotionSourceRuleModel> findCategoryForPromotionSourceRuleByPromotion(String code);

	/**
	 * Finds customer coupons for the customer
	 *
	 * @param customer
	 *           the customer model
	 * @return the list of CustomerCouponModel
	 */
	List<CustomerCouponModel> findEffectiveCustomerCouponsByCustomer(CustomerModel customer);

	/**
	 * Finds customer coupon for promotion source rules by promotion source rule model
	 *
	 * @param rule
	 *           the promotion source rule model
	 * @return the list of CustomerCouponForPromotionSourceRuleModel
	 */
	List<CustomerCouponForPromotionSourceRuleModel> findAllCusCouponForSourceRules(PromotionSourceRuleModel rule);

	/**
	 * Finds customer coupon for promotion source rules by promotion source rule model
	 *
	 * @param rule
	 *           the promotion source rule model
	 * @param moduleName
	 *           the specific module name
	 * @return the list of CustomerCouponForPromotionSourceRuleModel
	 */
	List<CustomerCouponForPromotionSourceRuleModel> findAllCusCouponForSourceRules(PromotionSourceRuleModel rule,
			String moduleName);

	/**
	 * Checks if the customer coupon is available for the customer
	 *
	 * @param couponCode
	 *           the coupon code
	 * @param customer
	 *           the customer model
	 * @return true if the coupon is available for the customer and false otherwise
	 */
	boolean checkCustomerCouponAvailableForCustomer(String couponCode, CustomerModel customer);

	/**
	 * Counts the assigned customer coupons for the customer by coupon code
	 *
	 * @param couponCode
	 *           the coupon code
	 * @param customer
	 *           the customer model
	 * @return the total number of assigned coupons
	 */
	int countAssignedCouponForCustomer(final String couponCode, final CustomerModel customer);

	/**
	 * Finds assignable customer coupons for the customer by search text
	 *
	 * @param customer
	 *           the customer model
	 * @param text
	 *           the text used for searching assignable customer coupons
	 * @return the list of CustomerCouponModel
	 */
	List<CustomerCouponModel> findAssignableCoupons(CustomerModel customer, String text);

	/**
	 * Finds assigned customer coupons by the customer and search text
	 *
	 * @param customer
	 *           the customer model
	 * @param text
	 *           the text used for searching assigned customer coupons
	 * @return the list of CustomerCouponModel
	 */
	List<CustomerCouponModel> findAssignedCouponsByCustomer(CustomerModel customer, String text);

	/**
	 * Finds paginated customer coupons by the customer and searchPageData
	 *
	 * @param customer
	 *           the customer model
	 * @param searchPageData
	 *           the data used for pagination
	 * @return the paginated CustomerCouponModel
	 */
	SearchPageData<CustomerCouponModel> findPaginatedCouponsByCustomer(CustomerModel customer,
			final SearchPageData searchPageData);
}

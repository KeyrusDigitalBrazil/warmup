/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.assistedservicepromotionfacades;

import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;

import java.util.List;


/**
 * Interface defining methods needed for fetching prmos and coupons.
 */
public interface AssistedServicePromotionFacade
{
	/**
	 * returns Promotions
	 *
	 * @param promotionCodeLike "like" parameter for promotions query
	 * @return {List} of AbstractRuleModel
	 */
	List<AbstractRuleModel> getCSAPromotions(final String promotionCodeLike);

	/**
	 * returns Coupons
	 *
	 * @param couponCodeLike "like" parameter for coupons query
	 * @return {List} of AbstractCouponModel
	 */
	List<AbstractCouponModel> getCSACoupons(final String couponCodeLike);

	/**
	 * returns Promotions
	 *
	 * @param promotionCodeLike "like" parameter for promotions query
	 * @return {List} of AbstractRuleModel for current customer
	 */
	List<AbstractRuleModel> getCustomerPromotions(final String promotionCodeLike);
}

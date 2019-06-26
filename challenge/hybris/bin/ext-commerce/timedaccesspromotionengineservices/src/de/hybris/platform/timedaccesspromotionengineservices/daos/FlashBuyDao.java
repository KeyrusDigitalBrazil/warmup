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
package de.hybris.platform.timedaccesspromotionengineservices.daos;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotionengineservices.model.ProductForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;

import java.util.List;
import java.util.Optional;


/**
 * Looks up items related to {@link FlashBuyCouponModel} {@link PromotionSourceRuleModel} {@link ProductModel}
 * {@link ProductForPromotionSourceRuleModel}
 */
public interface FlashBuyDao
{

	/**
	 * Finds FlashBuyCoupon by given promotion code
	 *
	 * @param promotionCode
	 *           promotion code
	 * @return Optional<FlashBuyCouponModel> Optional of FlashBuyCouponModel
	 */
	Optional<FlashBuyCouponModel> findFlashBuyCouponByPromotionCode(String promotionCode);

	/**
	 * Finds promotion source rule by product
	 *
	 * @param productCode
	 *           product code
	 * @return Promotion souce rule model
	 */
	List<PromotionSourceRuleModel> findPromotionSourceRuleByProduct(String productCode);

	/**
	 * Finds product by given promotion
	 *
	 * @param promotion
	 *           the given promotion
	 * @return Optional<ProductModel> Optional of ProductModel
	 */
	Optional<ProductModel> findProductByPromotion(AbstractPromotionModel promotion);

	/**
	 * Finds ProductForPromotionRule by given promotion source rule
	 *
	 * @param rule
	 *           the promotion source rule
	 * @return list of ProductForPromotionSourceRuleModel
	 */
	List<ProductForPromotionSourceRuleModel> findProductForPromotionSourceRules(final PromotionSourceRuleModel rule);

	/**
	 * Finds all Products by given promotion source rule
	 *
	 * @param rule
	 *           promotion source rule
	 * @return List<ProductModel> list of ProductModel
	 */
	List<ProductModel> findAllProductsByPromotionSourceRule(final PromotionSourceRuleModel rule);

	/**
	 * Finds flash buy coupon by product
	 *
	 * @param product
	 *           product model
	 * @return List<FlashBuyCouponModel> list of FlashBuyCouponModel
	 */
	List<FlashBuyCouponModel> findFlashBuyCouponByProduct(final ProductModel product);

}

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
package de.hybris.platform.timedaccesspromotionengineservices;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotionengineservices.model.ProductForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;

import java.util.List;
import java.util.Optional;


/**
 * Deals with flash buy related models using existing DAOs
 */
public interface FlashBuyService
{

	/**
	 * Gets product by promotion
	 *
	 * @param promotion
	 *           the given promotion
	 * @return Optional<ProductModel> Optional of ProductModel
	 */
	Optional<ProductModel> getProductForPromotion(AbstractPromotionModel promotion);

	/**
	 * Gets promotion source rules by product
	 *
	 * @param productCode
	 *           the product code
	 * @return list of PromotionSourceRuleModel
	 */
	List<PromotionSourceRuleModel> getPromotionSourceRulesByProductCode(final String productCode);

	/**
	 * Gets flash buy coupon by promotion source rule
	 *
	 * @param code
	 *           the promotion code
	 * @return Optional<FlashBuyCouponModel> Optional of FlashBuyCouponModel
	 *
	 */
	Optional<FlashBuyCouponModel> getFlashBuyCouponByPromotionCode(final String code);


	/**
	 * Stops flash buy promotion by promotion
	 *
	 * @param promotionSourceRule
	 *           the promotionSourceRule model
	 */
	void undeployFlashBuyPromotion(PromotionSourceRuleModel promotionSourceRule);

	/**
	 * Finds Promotion by given promotion code
	 *
	 * @param promotionCode
	 *           the given promotion code
	 * @return AbstractPromotion Model
	 */
	AbstractPromotionModel getPromotionByCode(String promotionCode);

	/**
	 * Create or update cronjob for flash buy coupon to reset max order quantity
	 *
	 * @param coupon
	 *           the flash buy coupon model
	 */
	void createCronJobForFlashBuyCoupon(FlashBuyCouponModel coupon);

	/**
	 * Performs cronjob for flash buy directly
	 *
	 * @param coupon
	 *           the flash buy coupon model
	 */
	void performFlashBuyCronJob(FlashBuyCouponModel coupon);

	/**
	 * Gets productforpromotionsourcerule by promotionsourcerule
	 *
	 * @param promotionSourceRule
	 *           the promotion source rule
	 * @return List of ProductForPromotionSourceRuleModel
	 */
	List<ProductForPromotionSourceRuleModel> getProductForPromotionSourceRule(PromotionSourceRuleModel promotionSourceRule);

	/**
	 * Finds Product by product code
	 *
	 * @param productCode
	 *           the given productCode
	 * @return List<ProductModel> list of ProductModel
	 */
	List<ProductModel> getProductForCode(String productCode);


	/**
	 * Gets all products by promotionsourcerule
	 *
	 * @param rule
	 *           the promotion source rule
	 * @return List<ProductModel> list of ProductModel
	 */
	List<ProductModel> getAllProductsByPromotionSourceRule(final PromotionSourceRuleModel rule);

	/**
	 * Gets flash buy coupon by product
	 *
	 * @param product
	 *           the product model
	 * @return List<FlashBuyCouponModel> list of FlashBuysCouponModel
	 */
	List<FlashBuyCouponModel> getFlashBuyCouponByProduct(final ProductModel product);

	/**
	 * Deletes cronjobs for setting and resetting max order quantity and triggered by flash buy coupon
	 *
	 * @param coupon
	 *           the flash buy coupon model
	 */
	void deleteCronJobAndTrigger(final FlashBuyCouponModel coupon);

}

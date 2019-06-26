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
package de.hybris.platform.timedaccesspromotionengineservices.cronjob;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.promotionengineservices.model.ProductForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.timedaccesspromotionengineservices.FlashBuyService;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCronJobModel;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Sets value of max order quantity for flash buy product after flash buy expires or ends
 */
public class ResetMaxOrderQuantityJob extends AbstractJobPerformable<FlashBuyCronJobModel>
{
	private ProductDao productDao;
	private FlashBuyService flashBuyService;

	/**
	 * Executes cronjob reset max order quantity after flash buy expired finish
	 *
	 * @param cronJobModel
	 *           cronjob model
	 * @return PerformResult cronjob execute result
	 */
	@Override
	public PerformResult perform(final FlashBuyCronJobModel cronJobModel)
	{
		validateParameterNotNull(cronJobModel, "Parameter cronJobModel must not be null");
		final FlashBuyCouponModel coupon = cronJobModel.getFlashBuyCoupon();
		final PromotionSourceRuleModel sourceRule = coupon.getRule();
		if (sourceRule != null)
		{
			final List<ProductForPromotionSourceRuleModel> productForPromotionSourceRules = getFlashBuyService()
					.getProductForPromotionSourceRule(sourceRule);

			productForPromotionSourceRules.forEach(rule -> {
				final String productCode = rule.getProductCode();
				final List<ProductModel> products = productDao.findProductsByCode(productCode);
				products.forEach(p -> p.setMaxOrderQuantity(coupon.getOriginalMaxOrderQuantity()));
				modelService.saveAll(products);
			});

			getFlashBuyService().undeployFlashBuyPromotion(sourceRule);
		}

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}


	protected ProductDao getProductDao()
	{
		return productDao;
	}

	@Required
	public void setProductDao(final ProductDao productDao)
	{
		this.productDao = productDao;
	}

	protected FlashBuyService getFlashBuyService()
	{
		return flashBuyService;
	}

	@Required
	public void setFlashBuyService(final FlashBuyService flashBuyService)
	{
		this.flashBuyService = flashBuyService;
	}
}

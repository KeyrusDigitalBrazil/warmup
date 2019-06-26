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
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCronJobModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Sets value of max order quantity before flash buy start
 */
public class SetMaxOrderQuantityJob extends AbstractJobPerformable<FlashBuyCronJobModel>
{
	private ProductDao productDao;

	/**
	 * Executes cronjob set max order quantity for product
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
		final ProductModel product = coupon.getProduct();
		coupon.setOriginalMaxOrderQuantity(product.getMaxOrderQuantity());
		modelService.save(coupon);

		getProductDao().findProductsByCode(product.getCode()).forEach(p -> {
			p.setMaxOrderQuantity(coupon.getMaxProductQuantityPerOrder());
			modelService.save(p);
		});

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
}

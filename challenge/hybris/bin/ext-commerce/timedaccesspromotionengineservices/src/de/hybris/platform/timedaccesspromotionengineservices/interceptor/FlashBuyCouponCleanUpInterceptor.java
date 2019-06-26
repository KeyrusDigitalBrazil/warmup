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
package de.hybris.platform.timedaccesspromotionengineservices.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.timedaccesspromotionengineservices.FlashBuyService;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Deletes the cronjob after removing a flash buy coupon
 */
public class FlashBuyCouponCleanUpInterceptor implements RemoveInterceptor<FlashBuyCouponModel>
{

	private FlashBuyService flashBuyService;

	/**
	 * Deletes the cronjob after removing a flash buy coupon
	 *
	 * @param coupon
	 *           flash buy coupon
	 * @param ctx
	 *           the context
	 */
	@Override
	public void onRemove(final FlashBuyCouponModel coupon, final InterceptorContext ctx) throws InterceptorException
	{
		getFlashBuyService().deleteCronJobAndTrigger(coupon);
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

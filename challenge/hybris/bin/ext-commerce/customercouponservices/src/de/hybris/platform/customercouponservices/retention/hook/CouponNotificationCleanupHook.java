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

package de.hybris.platform.customercouponservices.retention.hook;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponservices.CustomerCouponService;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.retention.hook.ItemCleanupHook;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Cleans customer coupon notification when the retention framework removes customer data
 */
public class CouponNotificationCleanupHook implements ItemCleanupHook<CustomerModel>
{
	private ModelService modelService;
	private CustomerCouponService customerCouponService;
	@Override
	public void cleanupRelatedObjects(final CustomerModel customer)
	{
		validateParameterNotNullStandardMessage("customer", customer);
		final List<CouponNotificationModel> couponNotifications = customerCouponService.getCouponNotificationsForCustomer(customer);
		if (CollectionUtils.isNotEmpty(couponNotifications))
		{
			getModelService().removeAll(couponNotifications);
		}
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CustomerCouponService getCustomerCouponService()
	{
		return customerCouponService;
	}

	@Required
	public void setCustomerCouponService(final CustomerCouponService customerCouponService)
	{
		this.customerCouponService = customerCouponService;
	}

}

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
package de.hybris.platform.customercouponservices.interceptor;

import de.hybris.platform.couponservices.interceptor.AbstractCouponValidateInterceptor;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorMapping;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanPostProcessor;


/**
 * Makes sure {@link CustomerCouponValidateInterceptor} is initialized after {@link AbstractCouponValidateInterceptor}
 * and overrides it for validating customer coupon by using CustomerCouponValidateInterceptor
 */
public class CustomerCouponValidateProcessor implements BeanPostProcessor
{
	private ValidateInterceptor<AbstractCouponModel> couponValidateInterceptor;

	@Override
	public Object postProcessAfterInitialization(final Object arg0, final String arg1)
	{
		if (arg0 instanceof InterceptorMapping)
		{
			final InterceptorMapping mapping = (InterceptorMapping) arg0;
			if (AbstractCouponModel._TYPECODE.equals(mapping.getTypeCode()))
			{
				mapping.setInterceptor(couponValidateInterceptor);
			}
		}
		return arg0;
	}

	@Override
	public Object postProcessBeforeInitialization(final Object arg0, final String arg1)
	{
		return arg0;
	}

	protected ValidateInterceptor<AbstractCouponModel> getCouponValidateInterceptor()
	{
		return couponValidateInterceptor;
	}

	@Required
	public void setCouponValidateInterceptor(final ValidateInterceptor<AbstractCouponModel> couponValidateInterceptor)
	{
		this.couponValidateInterceptor = couponValidateInterceptor;
	}
}

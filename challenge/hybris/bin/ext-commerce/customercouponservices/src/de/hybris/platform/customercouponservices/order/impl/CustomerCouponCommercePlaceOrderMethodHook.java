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
package de.hybris.platform.customercouponservices.order.impl;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customercouponservices.CustomerCouponService;
import de.hybris.platform.customercouponservices.order.CustomerCouponsPlaceOrderStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.customercouponservices.constants.CustomercouponservicesConstants;


/**
 * Deals with customer coupon for the customer when placing order
 */
public class CustomerCouponCommercePlaceOrderMethodHook implements CommercePlaceOrderMethodHook,
		CustomerCouponsPlaceOrderStrategy
{

	private CustomerCouponService customerCouponService;
	private SessionService sessionService;
	
	private static final String CUSTOMER_COUPON = "%3AcustomerCouponCode%3A";
	private static final String RELEVSNCE = "%3Arelevance";
	private static final String TEXT = "&text=";
	@Override
	public void removeCouponsForCustomer(final UserModel currentUser, final OrderModel order)
	{
		final CustomerModel customer = (CustomerModel) currentUser;
		final Collection<String> appliedCoupons = order.getAppliedCouponCodes();

		if (CollectionUtils.isNotEmpty(appliedCoupons))
		{
			appliedCoupons.forEach(couponCode -> {
				getCustomerCouponService().removeCouponForCustomer(couponCode, customer);
				getCustomerCouponService().removeCouponNotificationByCode(couponCode);
			});
		}

	}
	
	@Override
	public void updateContinueUrl() 
	{
		final String url = getSessionService().getAttribute(CustomercouponservicesConstants.CONTINUE_URL);

		if (StringUtils.containsIgnoreCase(url, CUSTOMER_COUPON)) {
			final String couponParam = StringUtils.substringBetween(url, RELEVSNCE, TEXT);
			getSessionService().setAttribute(CustomercouponservicesConstants.CONTINUE_URL,
					StringUtils.replace(url, couponParam, StringUtils.EMPTY));
		}
	}
	
	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		final CartModel cartModel = parameter.getCart();
		final UserModel currentUser = cartModel.getUser();

		final OrderModel order = result.getOrder();
		removeCouponsForCustomer(currentUser, order);
		
		updateContinueUrl();
	}

	@Override
	public void beforePlaceOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		// not implemented

	}

	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		// not implemented

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

	protected SessionService getSessionService() {
		return sessionService;
	}
	
	@Required
	public void setSessionService(final SessionService sessionService) {
		this.sessionService = sessionService;
	}
	
}

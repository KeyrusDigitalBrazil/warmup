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
package de.hybris.platform.customercouponaddon.controllers.misc;

import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.customercouponaddon.controllers.CustomercouponaddonControllerConstants;
import de.hybris.platform.customercouponfacades.CustomerCouponFacade;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller for customer coupons in ASM
 */
@Controller
@Scope("tenant")
@RequestMapping("/asm")
public class CustomerCouponASMPageController
{

	private static final String COUPON_CODE_PATH_VARIABLE = "{code:.*}";
	private static final String COUPON_TYPE_AVAILABLE = "available";
	private static final String COUPON_ACTION_SEND = "send";
	private static final String COUPON_ACTION_REMOVE = "remove";


	@Resource(name = "customerCouponFacade")
	private CustomerCouponFacade customerCouponFacade;

	@RequestMapping(value = "/customer-coupons", method = RequestMethod.GET)
	public String loadCoupons(@RequestParam final String type, @RequestParam final String text, final Model model)
	{
		List<CustomerCouponData> customerCoupons = null;
		if (COUPON_TYPE_AVAILABLE.equalsIgnoreCase(type))
		{
			customerCoupons = getCustomerCouponFacade().getAssignableCustomerCoupons(text);
			model.addAttribute("action", COUPON_ACTION_SEND);
		}
		else
		{
			customerCoupons = getCustomerCouponFacade().getAssignedCustomerCoupons(text);
			model.addAttribute("action", COUPON_ACTION_REMOVE);
		}
		model.addAttribute("type", type);
		model.addAttribute("customerCoupons", customerCoupons);

		return CustomercouponaddonControllerConstants.Views.Fragments.Coupons.CustomerCouponSubPage;
	}

	@ResponseBody
	@RequestMapping(value = "/customer-coupon/" + COUPON_CODE_PATH_VARIABLE, method = RequestMethod.POST)
	public boolean handleCouponActions(@PathVariable("code") final String couponCode, @RequestParam("action") final String action,
			final Model model)
	{
		boolean flag = true;
		try
		{
			if (COUPON_ACTION_SEND.equalsIgnoreCase(action))
			{
				getCustomerCouponFacade().grantCouponAccessForCurrentUser(couponCode);
			}
			else
			{
				getCustomerCouponFacade().releaseCoupon(couponCode);
			}
		}
		catch (final VoucherOperationException e) //NOSONAR
		{
			flag = false;
		}

		return flag;
	}


	protected CustomerCouponFacade getCustomerCouponFacade()
	{
		return customerCouponFacade;
	}

}

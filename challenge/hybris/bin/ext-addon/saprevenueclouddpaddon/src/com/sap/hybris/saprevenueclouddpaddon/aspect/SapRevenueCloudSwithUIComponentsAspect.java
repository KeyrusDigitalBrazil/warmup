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
package com.sap.hybris.saprevenueclouddpaddon.aspect;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


/**
 * Responsible for loading UI components specific for the SAP Digital Payments and Subscription Products
 *
 */
@Component
@Aspect
public class SapRevenueCloudSwithUIComponentsAspect
{
	public static final Logger LOG = Logger.getLogger(SapRevenueCloudSwithUIComponentsAspect.class);
	public static final String SAP_REVENUE_CLOUD_DP_ADDON_PREFIX = "addon:/saprevenueclouddpaddon/";
	public static final String SAP_DIGITAL_PAYMENT_ADDON_PREFIX = "addon:/sapdigitalpaymentaddon/";
	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String FORWARD_PREFIX = "forward:";
	public static final String ADDON_PREFIX = "addon:";




	/**
	 * For sap digital payment subscriptions, add the add-on prefix to the UI component.
	 *
	 * @param pjp
	 * @return the UI component name
	 * @throws Throwable
	 */
	public String applyDPUIChanges(final ProceedingJoinPoint pjp) throws Throwable
	{

		String uiComponent = pjp.proceed().toString();
		if (uiComponent.contains(REDIRECT_PREFIX) || uiComponent.contains(FORWARD_PREFIX)
				|| uiComponent.contains(SAP_REVENUE_CLOUD_DP_ADDON_PREFIX))
		{
			return uiComponent;
		}
		uiComponent = StringUtils.replace(uiComponent, SAP_DIGITAL_PAYMENT_ADDON_PREFIX, SAP_REVENUE_CLOUD_DP_ADDON_PREFIX);
		return uiComponent;

	}


	@Around("execution(public String de.hybris.platform.sapdigitalpaymentaddon*.controllers.pages.checkout.*.enterStep(..))")
	public String enterDPStep(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		LOG.info("Applying UI changes for SAP Revenue Cloud Digital Payment addon");
		return applyDPUIChanges(joinPoint);
	}


}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.gigya.gigyaloginaddon.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;


@Aspect
public class GigyaSwitchUIComponentAspect
{

	public static final String GIGYALOGINADDON_ADDON_PREFIX = "addon:/gigyaloginaddon/";
	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String FORWARD_PREFIX = "forward:";
	public static final String ADDON_PREFIX = "addon:";

	public String applyUIChanges(final ProceedingJoinPoint pjp) throws Throwable
	{
		String uiComponent = pjp.proceed().toString();
		if (uiComponent.contains(REDIRECT_PREFIX) || uiComponent.contains(FORWARD_PREFIX) || uiComponent.contains(ADDON_PREFIX)
				|| uiComponent.contains(GIGYALOGINADDON_ADDON_PREFIX))
		{
			return uiComponent;
		}

		final StringBuilder prefix = new StringBuilder(GIGYALOGINADDON_ADDON_PREFIX);
		prefix.append(uiComponent);
		uiComponent = prefix.toString();
		return uiComponent;
	}

	@Around("execution(public String *..controllers.pages.*.doLogin(..))")
	public String doLogin(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		return applyUIChanges(joinPoint);
	}

	@Around("execution(public String *..controllers.pages.*.doCheckoutLogin(..))")
	public String doCheckoutLogin(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		return applyUIChanges(joinPoint);
	}

}

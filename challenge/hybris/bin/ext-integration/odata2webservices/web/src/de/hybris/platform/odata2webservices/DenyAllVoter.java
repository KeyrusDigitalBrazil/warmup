/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2webservices;

import java.util.Collection;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

/**
 * Denies access if the Spring security intercept-url's {@code access} attribute is DENY_ALL.
 */
public class DenyAllVoter implements AccessDecisionVoter<Object>
{
	@Override
	public boolean supports(final ConfigAttribute attribute)
	{
		return "DENY_ALL".equals(attribute.getAttribute());
	}

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return clazz.isAssignableFrom(FilterInvocation.class);
	}

	@Override
	public int vote(final Authentication authentication, final Object object, final Collection<ConfigAttribute> attributes)
	{
		return isDenyAll(attributes) ? ACCESS_DENIED : ACCESS_ABSTAIN;
	}

	private boolean isDenyAll(final Collection<ConfigAttribute> attributes)
	{
		return attributes.stream().anyMatch(this::supports);
	}
}

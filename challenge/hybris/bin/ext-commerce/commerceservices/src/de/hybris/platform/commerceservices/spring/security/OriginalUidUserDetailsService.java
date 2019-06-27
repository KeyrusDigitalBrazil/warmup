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
package de.hybris.platform.commerceservices.spring.security;

import de.hybris.platform.spring.security.CoreUserDetails;
import de.hybris.platform.spring.security.CoreUserDetailsService;


/**
 * accelerator specific implementation for providing user data access
 */
public class OriginalUidUserDetailsService extends CoreUserDetailsService
{
	@Override
	public CoreUserDetails loadUserByUsername(final String username)
	{
		return super.loadUserByUsername(username.toLowerCase());
	}
}

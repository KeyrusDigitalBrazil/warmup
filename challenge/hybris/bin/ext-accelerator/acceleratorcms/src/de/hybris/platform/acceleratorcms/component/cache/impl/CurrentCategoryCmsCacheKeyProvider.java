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
package de.hybris.platform.acceleratorcms.component.cache.impl;

import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


public class CurrentCategoryCmsCacheKeyProvider extends DefaultCmsCacheKeyProvider
{
	@Override
	public StringBuilder getKeyInternal(final HttpServletRequest request, final SimpleCMSComponentModel component)
	{
		final StringBuilder buffer = new StringBuilder(super.getKeyInternal(request, component));
		final String currentCategory = getRequestContextData(request).getCategory().getPk().getLongValueAsString();
		if (!StringUtils.isEmpty(currentCategory))
		{
			buffer.append(currentCategory);
		}
		return buffer;
	}
}

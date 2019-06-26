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
package de.hybris.platform.chinesepspalipaymock.utils.imported;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestDataValueProcessor;


/**
 * Deals with http request for preventing from CSRF attack
 */
public class CSRFRequestDataValueProcessor implements RequestDataValueProcessor
{

	@Override
	public Map<String, String> getExtraHiddenFields(final HttpServletRequest httpServletRequest)
	{
		final Map<String, String> extraHiddenFields = new HashMap<String, String>();
		final String sessionCsrfToken = CSRFTokenManager.getTokenForSession(httpServletRequest.getSession());
		extraHiddenFields.put(CSRFTokenManager.CSRF_PARAM_NAME, sessionCsrfToken);
		return extraHiddenFields;
	}

	@Override
	public String processUrl(final HttpServletRequest request, final String url)
	{
		return url;
	}


	@Override
	public String processFormFieldValue(final HttpServletRequest request, final String name, final String value, final String type)
	{
		return value;
	}

	@Override
	public String processAction(final HttpServletRequest request, final String action, final String httpMethod)
	{
		return action;
	}

}

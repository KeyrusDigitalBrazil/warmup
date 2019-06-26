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
package de.hybris.platform.cmswebservices.filter;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import de.hybris.platform.servicelayer.session.Session;


/**
 * 
 * Interface for adding additional header fields to the session
 * 
 */
public interface RestSessionFilterArgument
{

	/**
	 * Allows the definition of additional rest headers to the session
	 * 
	 * @param request
	 *           the http request object
	 * @param response
	 *           the http response object
	 * @param session
	 *           the local session object
	 */
	void addSessionArgument(HttpServletRequest request, ServletResponse response, Session session);

}

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
package de.hybris.platform.cmswebservices.namedquery;


import de.hybris.platform.cmswebservices.data.NamedQueryData;

import javax.servlet.http.HttpServletRequest;


/**
 * Interface to specify that a web component is searchable by named query.
 */
public interface NamedQuerySearchable
{

	/**
	 * Get the named query from the request.
	 *
	 * @param request
	 *           - the http servlet request
	 * @return the named query or <code>null</code> if the request does not contain a named query
	 */
	NamedQueryData getNamedQuery(HttpServletRequest request);
}

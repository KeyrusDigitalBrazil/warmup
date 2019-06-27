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
package de.hybris.platform.sap.productconfig.runtime.cps.session;

import java.util.List;

import javax.ws.rs.core.NewCookie;

import com.hybris.charon.RawResponse;


/**
 * Stores cookies and allows to access the cookies in string format for adding to a request later on
 */
public interface CPSResponseAttributeStrategy
{

	/**
	 * Store cookies for a given configuration ID
	 *
	 * @param configId
	 *           id of the runtime configuration
	 * @param cookies
	 *           List of the cookies to store
	 */
	void setCookies(String configId, List<NewCookie> cookies);

	/**
	 * Returns list of cookies per configuration ID, formatted as strings so that they can directly used to be sent along
	 * with the request header
	 *
	 * @param configId
	 *           id of the runtime configuration
	 * @return list of the cookies as string
	 *
	 */
	List<String> getCookiesAsString(String configId);

	/**
	 * Removes cookies for a given configuration ID
	 *
	 * @param configId
	 *           id of the runtime configuration
	 */
	void removeCookies(String configId);

	/**
	 * Store cookies as string for a given configuration ID
	 *
	 * @param configId
	 *           id of the runtime configuration
	 * @param cookieList
	 *           List of the cookies to store
	 */
	void setCookiesAsString(String configId, List<String> cookieList);

	/**
	 * Retrieves an eTag and saves response attributes
	 * 
	 * @param rawResponse
	 * 		raw response
	 * @param cfgId
	 * 			configuration id
	 * @return
	 */
	String retrieveETagAndSaveResponseAttributes(final RawResponse rawResponse, final String cfgId);

}

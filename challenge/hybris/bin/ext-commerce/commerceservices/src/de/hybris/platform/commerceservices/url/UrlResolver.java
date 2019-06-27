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
package de.hybris.platform.commerceservices.url;

/**
 * Interface used to resolve a URL path for a parametrized type
 * 
 * @param <T>
 *           the type of the source item to resolve into a URL.
 */
public interface UrlResolver<T>
{
	/**
	 * Resolve the url path for the source type.
	 * 
	 * @param source
	 *           the source type.
	 * @return the URL path
	 */
	String resolve(T source);
}

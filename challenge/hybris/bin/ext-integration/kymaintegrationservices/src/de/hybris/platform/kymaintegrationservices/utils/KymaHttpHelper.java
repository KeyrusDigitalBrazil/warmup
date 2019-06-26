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

package de.hybris.platform.kymaintegrationservices.utils;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


/**
 * Helper class for creating http communication with kyma.
 */
public class KymaHttpHelper
{
	private KymaHttpHelper()
	{
	}

	/**
	 * Makes some default http headers for communication with kyma
	 *
	 * @return default headers
	 */
	public static HttpHeaders getDefaultHeaders()
	{
		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.ALL));
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
}

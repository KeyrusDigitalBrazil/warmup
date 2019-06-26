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
package com.hybris.commons.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *  Factory creates instance of ResponseEntity with stub values.
 */
public class RestResponseFactory
{
	public static <T> ResponseEntity<T> newStubInstance()
	{
	
		final HttpStatus response = HttpStatus.OK;
		
		return new ResponseEntity<T>(response)
		{
			@Override
			public T getBody()
			{
				return null;
			}
		};
	}
}

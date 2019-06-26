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
package de.hybris.platform.outboundservices.client.impl;

/**
 * The UnsupportedRestTemplateException that occurs when the given consumed destination
 * is not supported by the RestTemplate factory or creator.
 */
public class UnsupportedRestTemplateException extends RuntimeException
{
	private static final String MESSAGE = "There is no RestTemplate supported for given consumed destination.";

	public UnsupportedRestTemplateException()
	{
		super(MESSAGE);
	}
}

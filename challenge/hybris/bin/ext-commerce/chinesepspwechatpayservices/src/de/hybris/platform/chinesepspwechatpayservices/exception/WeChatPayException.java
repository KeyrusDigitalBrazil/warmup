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
package de.hybris.platform.chinesepspwechatpayservices.exception;

/**
 * Custom exception class to handle exceptions while executing HTTP request
 */
public class WeChatPayException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public WeChatPayException()
	{
		super();
	}

	public WeChatPayException(String s)
	{
		super(s);
	}

	public WeChatPayException(Throwable throwable)
	{
		super(throwable);
	}

	public WeChatPayException(String s, Throwable throwable)
	{
		super(s, throwable);
	}

}

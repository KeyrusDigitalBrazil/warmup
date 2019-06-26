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
package de.hybris.platform.assistedserviceservices.exception;

/**
 * Parent exception type for the {@link AssistedServiceFacade}.
 */
public class AssistedServiceException extends Exception
{
	protected static final String ASM_ALERT_CART = "ASM_alert_cart";
	protected static final String ASM_ALERT_CUSTOMER = "ASM_alert_customer";
	protected static final String ASM_ALERT_CREDENTIALS = "ASM_alert_cred";

	public AssistedServiceException(final String message)
	{
		super(message);
	}

	public AssistedServiceException(final String message, final Throwable t)
	{
		super(message, t);
	}

	/**
	 * Returns message code from message properties.
	 */
	public String getMessageCode()
	{
		return getMessage();
	}

	/**
	 * Returns alert class for usage in storefront.
	 */
	public String getAlertClass()
	{
		return "";
	}
}

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
package de.hybris.platform.integration.cis.payment.constants;

@SuppressWarnings("PMD")
public class CispaymentConstants extends GeneratedCispaymentConstants
{
	public static final String EXTENSIONNAME = "cispayment";
	/**
	 * Error code which signifies a failed webservice call. The HYSTRIX fallback event was executed.
	 */
	public static final Integer HYSTRIX_FALLBACK_ERR_CODE = Integer.valueOf(50);
	/**
	 * Generic payment error.
	 */
	public static final Integer GENERAL_PAYMENT_ERR_CODE = Integer.valueOf(60);

	private CispaymentConstants()
	{
		//empty
	}
}

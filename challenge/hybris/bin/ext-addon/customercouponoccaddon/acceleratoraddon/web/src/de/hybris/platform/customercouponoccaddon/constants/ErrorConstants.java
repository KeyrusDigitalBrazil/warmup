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
package de.hybris.platform.customercouponoccaddon.constants;

/**
 * Customercouponoccaddon web error constants
 */
public class ErrorConstants
{

	public static final String NOT_EXIST_MESSAGE = "This customer coupon does not exist, or has expired, or is inactive.";
	public static final String NOT_OWNED_MESSAGE = "This customer coupon does not belong to you.";
	public static final String CLAIMED_MESSAGE = "You have already claimed this coupon.";

	public static final String NOT_EXIST_REASON = "notExist";

	private ErrorConstants()
	{
	}

}

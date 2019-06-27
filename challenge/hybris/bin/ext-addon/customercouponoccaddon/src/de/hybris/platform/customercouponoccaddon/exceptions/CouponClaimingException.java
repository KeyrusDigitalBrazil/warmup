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
package de.hybris.platform.customercouponoccaddon.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;


/**
 * CouponClaiming exception
 */
public class CouponClaimingException extends WebserviceException
{

	public static final String CLAIMED = "claimed";

	public CouponClaimingException(final String message, final String reason, final String subject)
	{
		super(message, reason, subject);
	}

	@Override
	public String getSubjectType()
	{
		return "CustomerCoupon";
	}

	@Override
	public String getType()
	{
		return "CouponClaimingError";
	}

}

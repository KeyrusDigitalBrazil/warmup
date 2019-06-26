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
package com.hybris.cis.client.shared.models;

/**
 * Indicates whether an address was validated, should be reviewed or was rejected.
 * 
 */
public enum CisDecision
{
	/**
	 * The request was accepted. (example for avs: address was recognized as valid and no or only irrelevant differences
	 * compared to the
	 * standardized address).
	 */
	ACCEPT,

	/**
	 * The request was accepted, but the response should be reviewed. (example for avs: address was recognized as valid, however
	 * there were
	 * differences compared to the standardized address).
	 */
	REVIEW,

	/** The request was rejected. (example avs: address is not valid) . */
	REJECT,

	/** A system error occurred. */
	ERROR;
}

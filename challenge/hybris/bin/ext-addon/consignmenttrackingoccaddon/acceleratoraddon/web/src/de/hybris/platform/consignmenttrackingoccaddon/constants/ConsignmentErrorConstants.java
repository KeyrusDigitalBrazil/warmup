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
 package de.hybris.platform.consignmenttrackingoccaddon.constants;

/**
 * define the constants for consignment tracking error information
 */
public class ConsignmentErrorConstants
{

	public static final String ORDER_NOT_FOUND_MESSAGE = "The order does not exist or belongs to another customer.";
	public static final String CONSIGNMENT_NOT_FOUND_MESSAGE = "No consignment found for the current order.";
	public static final String CONSIGNMENT_INCORRECT_MESSAGE = "The consignment code is incorrect.";

	public static final String ORDER_NOT_FOUND = "orderNotFound";
	public static final String CONSIGNMENT_NOT_FOUND = "consignmentNotFound";
	public static final String CONSIGNMENT_INCORRECT = "consignmentIncorrect";

	private ConsignmentErrorConstants()
	{

	}

}

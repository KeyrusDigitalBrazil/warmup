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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util;

/**
 * Container object for the GetAll read Parameters. <br>
 *
 * @version 1.0
 */
public class GetAllReadParameters
{

	/**
	 * Flag if incompletion log is requested.
	 */
	@SuppressWarnings("squid:S1444")
	public static boolean isIncompletionLogRequested = false;

	/**
	 * Freight condition type of the header.
	 */
	@SuppressWarnings("squid:S1444")
	public static String headerCondTypeFreight = "";

	/**
	 * Sub total including freight costs.
	 */
	public static final String subTotalItemFreight = "";

	/**
	 * Indicator if IPC price attributes have to be set.<br>
	 */
	@SuppressWarnings("squid:S1444")
	public static boolean setIpcPriceAttributes = false;

	/**
	 * The description of the shipping conditions.<br>
	 */
	public static final boolean shippingConditionsAsText = false;

	/**
	 * Private constructor
	 */
	private GetAllReadParameters()
	{

	}
}
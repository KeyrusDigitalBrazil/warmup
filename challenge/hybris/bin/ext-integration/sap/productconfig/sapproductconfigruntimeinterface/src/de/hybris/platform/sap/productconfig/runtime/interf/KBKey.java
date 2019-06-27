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
package de.hybris.platform.sap.productconfig.runtime.interf;

import java.util.Date;


/**
 * Key to identify a knowledge base (KB) for product configuration. This Object is immutable.
 * 
 */
public interface KBKey
{

	/**
	 * @return the product code
	 */
	String getProductCode();

	/**
	 * @return the knowledge base name
	 */
	String getKbName();

	/**
	 * @return the knowledge base logical system
	 */
	String getKbLogsys();

	/**
	 * @return the knowledge base version
	 */
	String getKbVersion();

	/**
	 * @return the date
	 */
	Date getDate();

}
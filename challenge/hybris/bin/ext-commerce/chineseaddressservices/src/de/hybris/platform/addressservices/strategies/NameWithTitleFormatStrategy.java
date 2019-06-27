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
package de.hybris.platform.addressservices.strategies;

public interface NameWithTitleFormatStrategy
{
	/**
	 * get full name with title name
	 *
	 * @param fullname
	 * @param title
	 * @return full name with title name
	 */
	String getFullnameWithTitle(String fullname, String title);

	/**
	 * get full name with title name
	 *
	 * @param firstname
	 * @param lastname
	 * @param title
	 * @return full name with title name
	 */
	String getFullnameWithTitle(String firstname, String lastname, String title);

	/**
	 * get full name with title name
	 *
	 * @param fullname
	 * @param title
	 * @param isocode
	 * @return full name with title name
	 */
	String getFullnameWithTitleForISOCode(String fullname, String title, String isocode);

	/**
	 *
	 * @param firstname
	 * @param lastname
	 * @param title
	 * @param isocode
	 * @return full name with title name
	 */
	String getFullnameWithTitleForISOCode(String firstname, String lastname, String title, String isocode);
}

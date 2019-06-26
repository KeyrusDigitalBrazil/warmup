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
package de.hybris.platform.xyformsservices.strategy;



/**
 * Strategy for getting a new form number based on some parameters.
 */
public interface GetVersionNumberStrategy
{
	/**
	 * Returns the next version number available for a form definition indentified by applicationId and formId
	 *
	 * @param applicationId
	 * @param formId
	 */
	public int execute(final String applicationId, final String formId);
}

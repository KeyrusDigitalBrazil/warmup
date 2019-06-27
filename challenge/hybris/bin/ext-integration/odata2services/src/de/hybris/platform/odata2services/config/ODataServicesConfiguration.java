/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.config;

public interface ODataServicesConfiguration
{
	/**
	 * Determines current setting of the maximum number of batches allowed in an inbound request body.
	 *
	 * @return number of batches that a single request may have.
	 */
	int getBatchLimit();

	/**
	 * Specifies new limit on number of batches in a single inbound request.
	 *
	 * @param maxNumber max number of batches a single inbound request may have.
	 */
	void setBatchLimit(int maxNumber);
	
	/**
	 * Determines current setting of the maximum number of items allowed per page.
	 *
	 * @return number of items that can be returned for a single page.
	 */
	int getMaxPageSize();

	/**
	 * @return the default page size for the extension
	 */
	int getDefaultPageSize();
}

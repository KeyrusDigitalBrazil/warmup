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
package de.hybris.platform.acceleratorservices.dataimport.batch;

/**
 * General task for execution in a batch pipeline.
 */
public interface HeaderTask
{
	/**
	 * Executes a task with a predefined {@link BatchHeader} identifying all relevant process information.
	 * 
	 * @param header
	 * @return the header
	 */
	BatchHeader execute(BatchHeader header) throws Exception;// NOSONAR

}

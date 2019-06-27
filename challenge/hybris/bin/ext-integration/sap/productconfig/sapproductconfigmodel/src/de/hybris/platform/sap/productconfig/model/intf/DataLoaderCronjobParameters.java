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
package de.hybris.platform.sap.productconfig.model.intf;

/**
 * Spring managed parameter container for dataload jobs
 */
public interface DataLoaderCronjobParameters
{

	/**
	 * @return Data Loader Start Job Bead Id
	 */
	String getDataloadStartJobBeanId();

	/**
	 * @param dataloadStartJobBeanId
	 *           Data Loader Start Job Bead Id
	 */
	void setDataloadStartJobBeanId(String dataloadStartJobBeanId);

	/**
	 * @return Data Loader Stop Job Bead Id
	 */
	String getDataloadStopJobBeanId();

	/**
	 * @param dataloadStopJobBeanId
	 *           Data Loader Stop Job Bead Id
	 */
	void setDataloadStopJobBeanId(String dataloadStopJobBeanId);

	/**
	 *
	 * @return Node Id for Start Job
	 */
	Integer retrieveNodeIdForStartJob();

	/**
	 * @return Node Id for Stop Job
	 */
	Integer retrieveNodeIdForStopJob();

}

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
package de.hybris.platform.sap.sapcarintegration.services;

/**
 * Standard interface to retrieve attributes related to CAR configuration
 */
public interface CarConfigurationService
{
	/**
	 * Returns the SapClient associated with the configuration of the CAR instance
	 * 
	 * @return String representation of SapClient
	 */
	String getSapClient();

	/**
	 * Returns the Root URL for the given CAR instance configuration
	 * 
	 * @return Root URL as a String
	 */
	String getRootUrl();

	/**
	 * Returns the Service name associated with the given Customer Activity Repository instance
	 * 
	 * @return String of Service Name
	 */
	String getServiceName();

	/**
	 * Returns the user name for the given CAR instance
	 * 
	 * @return User name as a String
	 */
	String getUsername();

	/**
	 * Returns the password of the CAR instance
	 * 
	 * @return Password String
	 */
	String getPassword();

	/**
	 * Returns the Transaction type associated with the customer activity instance
	 * 
	 * @return Transaction Type
	 */
	String getTransactionType();

	/**
	 * Provides the sales organization associated with the given activity
	 * 
	 * @return Sales Organization identity
	 */
	String getSalesOrganization();

	/**
	 * Returns Distribution Channel attributed to the customer activity
	 * 
	 * @return Distribution Channel String representation
	 */
	String getDistributionChannel();

	/**
	 * Get the Division tied with the customer activity instance
	 * 
	 * @return Division String
	 */
	String getDivision();

}

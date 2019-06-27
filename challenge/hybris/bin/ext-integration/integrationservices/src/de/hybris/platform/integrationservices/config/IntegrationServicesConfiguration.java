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
package de.hybris.platform.integrationservices.config;

public interface IntegrationServicesConfiguration
{
	/**
	 * Provides the value of the prefix used in the persisted media
	 *
	 * @return String with the value of the prefix
	 */
	String getMediaPersistenceMediaNamePrefix();

	/**
	 * Provides the value of the SAP passport system id
	 * @return String with the systemId value.
	 */
	String getSapPassportSystemId();

	/**
	 * Provides the value of the SAP passport service
	 * @return int with the service value.
	 */
	int getSapPassportServiceValue();

	/**
	 * Provides the value of the SAP passport user
	 * @return String with the user value.
	 */
	String getSapPassportUser();
}

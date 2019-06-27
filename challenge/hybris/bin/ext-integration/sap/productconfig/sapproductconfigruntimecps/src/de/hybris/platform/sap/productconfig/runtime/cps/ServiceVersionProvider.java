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
package de.hybris.platform.sap.productconfig.runtime.cps;

/**
 * Utility methods for handling the version of suffic of service URLs
 */
public interface ServiceVersionProvider
{

	/**
	 * Extracts the versions of the service from service URL resolved for the given service client name.<br>
	 * assumes that the service version is the last part of the URL, such as ...\v1
	 *
	 * @param clientName
	 *           name of the service client
	 * @return serviceVersion, as maintained in the service url
	 */
	String getVersion(String clientName);

}

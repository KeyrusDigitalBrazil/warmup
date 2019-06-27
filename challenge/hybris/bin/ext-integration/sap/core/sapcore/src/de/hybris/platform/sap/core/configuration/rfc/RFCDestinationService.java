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
package de.hybris.platform.sap.core.configuration.rfc;





/**
 * Interface to access SAP RFC destination data.
 */
public interface RFCDestinationService
{

	/**
	 * Returns the SAP RFC Destination for the given name.
	 * 
	 * @param destinationName
	 *           RFC Destination name
	 * @return list
	 */
	public RFCDestination getRFCDestination(String destinationName);

}

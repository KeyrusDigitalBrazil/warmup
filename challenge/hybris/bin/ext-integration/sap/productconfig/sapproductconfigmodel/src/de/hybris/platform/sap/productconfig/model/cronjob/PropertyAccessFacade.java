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
package de.hybris.platform.sap.productconfig.model.cronjob;


/**
 * Encapsulation of accesses to properties file for SSC DB attributes
 */
public interface PropertyAccessFacade
{

	/**
	 * @return true if the delta load has to be started automatically after the initial load
	 */
	boolean getStartDeltaloadAfterInitial();

}

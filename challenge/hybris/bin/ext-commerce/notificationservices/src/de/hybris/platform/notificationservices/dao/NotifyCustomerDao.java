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
package de.hybris.platform.notificationservices.dao;

import de.hybris.platform.core.model.user.CustomerModel;

import java.util.List;



/**
 * manipulate Customer lated to notification
 */
public interface NotifyCustomerDao
{

	/**
	 * find customers which are created before the install of notificationservices
	 */
	List<CustomerModel> findOldCustomer();

}
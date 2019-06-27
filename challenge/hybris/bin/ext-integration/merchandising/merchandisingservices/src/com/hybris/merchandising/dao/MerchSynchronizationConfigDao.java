/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.dao;

import java.util.Collection;

import com.hybris.merchandising.model.MerchSynchronizationConfigModel;

/**
 * The {@link MerchSynchronizationConfigModel} DAO.
 */
public interface MerchSynchronizationConfigDao
{
	/**
	 * Finds all merchandising synchronization configurations.
	 *
	 * @return list of merchandising synch configurations or empty list if no configuration is found
	 */
	Collection<MerchSynchronizationConfigModel> findAllMerchSynchronizationConfig();
}

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
package com.hybris.merchandising.dao.impl;

import java.util.Collection;

import com.hybris.merchandising.dao.MerchIndexingConfigDao;
import com.hybris.merchandising.dao.MerchSynchronizationConfigDao;
import com.hybris.merchandising.model.MerchIndexingConfigModel;
import com.hybris.merchandising.model.MerchSynchronizationConfigModel;

import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

/**
 * Default implementation of {@link MerchIndexingConfigDao}.
 */
public class DefaultMerchSynchronizationConfigDao extends DefaultGenericDao<MerchSynchronizationConfigModel> implements MerchSynchronizationConfigDao
{
	/**
	 * Creates DAO for {@link MerchIndexingConfigModel}.
	 */
	public DefaultMerchSynchronizationConfigDao()
	{
		super(MerchSynchronizationConfigModel._TYPECODE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<MerchSynchronizationConfigModel> findAllMerchSynchronizationConfig() {
		return find();
	}
}

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
 *
 */
package de.hybris.platform.warehousing.util.dao.impl;

import de.hybris.platform.warehousing.model.SourcingConfigModel;


public class SourcingConfigDaoImpl extends AbstractWarehousingDao<SourcingConfigModel>
{
	@Override
	protected String getQuery()
	{
		return "SELECT {pk} FROM {SourcingConfig} WHERE {code}=?code";
	}
}

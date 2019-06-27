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

import de.hybris.platform.warehousing.model.AllocationEventModel;


public class CancellationEventDaoImpl extends AbstractWarehousingDao<AllocationEventModel>
{
	@Override
	protected String getQuery()
	{
		return "SELECT {ca.pk} FROM {CancellationEvent as ca " //
				+ "JOIN ConsignmentEntry as e ON {ca.consignmentEntry} = {e.pk} " //
				+ "JOIN Consignment as c ON {e.consignment} = {c.pk}} " //
				+ "WHERE {c.code}=?code";
	}
}

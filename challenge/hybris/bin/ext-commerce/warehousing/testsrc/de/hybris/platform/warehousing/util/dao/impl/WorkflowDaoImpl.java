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

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.List;


public class WorkflowDaoImpl extends AbstractWarehousingDao<WorkflowModel>
{
	@Override
	protected String getQuery()
	{
		return "SELECT {pk} FROM {Workflow}";
}

	public List<Object> findAllWorkflows()
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(getQuery());
		return getFlexibleSearchService().search(query).getResult();
	}
}

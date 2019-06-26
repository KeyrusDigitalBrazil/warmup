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
package de.hybris.platform.b2b.dao.impl;

import de.hybris.platform.b2b.dao.B2BWorkflowDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.workflow.model.WorkflowItemAttachmentModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * Default implementation of the {@link B2BWorkflowDao}
 * 
 * @spring.bean b2bWorkflowDao
 */
public class DefaultB2BWorkflowDao extends DefaultGenericDao<WorkflowModel> implements B2BWorkflowDao
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BWorkflowDao.class);

	/**
	 * DefaultGenericDao is only usable when typecode is set.
	 */
	public DefaultB2BWorkflowDao()
	{
		super(WorkflowModel._TYPECODE);
	}

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #findWorkflowByOrder(OrderModel)}
	 */
	@Override
	@Deprecated
	public WorkflowModel findWorkflowForOrder(final OrderModel order)
	{
		return findWorkflowByOrder(order);
	}

	@Override
	public WorkflowModel findWorkflowByOrder(final OrderModel order)
	{
		if (order.getOrderProcess() != null)
		{
			final Map<String, Object> attr = new HashMap<String, Object>();
			attr.put("process", order.getOrderProcess());
			final StringBuilder sql = new StringBuilder();
			sql.append("SELECT {wia:workflow} from { ").append(WorkflowItemAttachmentModel._TYPECODE)
					.append(" as wia} WHERE {wia:item} in (?process)").append(" ORDER BY {wia:creationTime} DESC");

			final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
			query.getQueryParameters().putAll(attr);
			query.setResultClassList(Collections.singletonList(WorkflowModel.class));
			final SearchResult<WorkflowModel> result = this.getFlexibleSearchService().search(query);
			if (result.getCount() > 0)
			{
				final List<WorkflowModel> workflowList = result.getResult();
				if (workflowList.size() > 1)
				{
					LOG.warn(String.format("Found %s workflow for order %s", Integer.valueOf(workflowList.size()), order.getCode()));
				}
				// return the workflow that was created most recently.
				return workflowList.iterator().next();
			}
		}
		else
		{
			LOG.warn(String.format("Order %s does not have an associated OrderProcess", order.getCode()));
		}
		return null;
	}
}

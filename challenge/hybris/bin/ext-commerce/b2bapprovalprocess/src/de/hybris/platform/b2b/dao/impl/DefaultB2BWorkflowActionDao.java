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

import de.hybris.platform.b2b.dao.B2BWorflowActionDao;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.internal.dao.SortParameters;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of the {@link B2BWorflowActionDao}
 * 
 * @spring.bean b2bWorflowActionDao
 */
public class DefaultB2BWorkflowActionDao extends DefaultGenericDao<WorkflowActionModel> implements B2BWorflowActionDao
{

	public DefaultB2BWorkflowActionDao()
	{
		super(WorkflowActionModel._TYPECODE);
	}

	/**
	 * @deprecated Since 4.4.
	 */
	@Override
	@Deprecated
	public Collection<WorkflowActionModel> findActionByUserAndStatus(final WorkflowActionStatus status, final UserModel user)
	{
		final Map<String, Object> attr = new HashMap<String, Object>(2);
		attr.put("status", status);
		attr.put("user", user);
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT tbl.action FROM ( ").append("{{SELECT {actions:PK} action FROM { ")
				.append(WorkflowActionModel._TYPECODE)
				.append(" as actions} WHERE {actions:status}=?status AND {actions:principalAssigned}=?user }}").append(" UNION ALL ")
				.append("{{ SELECT {actions:PK} action FROM { ").append(WorkflowActionModel._TYPECODE)
				.append(" as actions JOIN PrincipalGroupRelation as rel ON {actions:principalAssigned}={rel:target}" + " } ")
				.append(" WHERE {actions:status}=?status AND {rel:source} = ?user }} ) tbl");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);

		final SearchResult<WorkflowActionModel> result = this.getFlexibleSearchService().search(query);
		final List<WorkflowActionModel> actions = result.getResult();
		return actions;
	}

	@Override
	public Collection<WorkflowActionModel> findWorkflowActionsByUserActionCodeAndStatus(final WorkflowActionStatus status,
			final String qualifier, final UserModel user)
	{
		final Map<String, Object> params = new HashMap<String, Object>(3);
		params.put(WorkflowActionModel.STATUS, status);
		params.put(WorkflowActionModel.PRINCIPALASSIGNED, user);
		params.put(WorkflowActionModel.QUALIFIER, qualifier);
		return find(params, SortParameters.singletonDescending(WorkflowActionModel.MODIFIEDTIME));
	}


	@Override
	public Collection<WorkflowActionModel> findWorkflowActionsByUser(final UserModel user)
	{
		final Map<String, Object> params = new HashMap<String, Object>(3);
		params.put(WorkflowActionModel.PRINCIPALASSIGNED, user);
		return find(params, SortParameters.singletonDescending(WorkflowActionModel.MODIFIEDTIME));
	}

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #findWorkflowActionByCode(String)}
	 */
	@Override
	@Deprecated
	public WorkflowActionModel getWorkflowActionByCode(final String code)
	{
		return findWorkflowActionByCode(code);
	}

	@Override
	public WorkflowActionModel findWorkflowActionByCode(final String code)
	{
		final List<WorkflowActionModel> workflowActionModels = this.find(Collections.singletonMap(WorkflowActionModel.CODE, code));
		return (workflowActionModels.iterator().hasNext() ? workflowActionModels.iterator().next() : null);
	}
}

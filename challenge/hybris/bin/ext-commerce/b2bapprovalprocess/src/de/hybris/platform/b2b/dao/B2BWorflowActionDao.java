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
package de.hybris.platform.b2b.dao;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import java.util.Collection;


/**
 * The Interface B2BWorflowActionDao.
 * 
 * @spring.bean b2bWorflowActionDao
 */
public interface B2BWorflowActionDao
{

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #findWorkflowActionByCode(String)}
	 */
	@Deprecated
	WorkflowActionModel getWorkflowActionByCode(final String code);

	/**
	 * Get the WorkFlowAction from its code.
	 * 
	 * @param code
	 *           the code of the WorkFlowAction
	 * @return WorkflowActionModel
	 * @deprecated Since 4.4. Use
	 *             {@link de.hybris.platform.workflow.WorkflowActionService#getActionForCode(de.hybris.platform.workflow.model.WorkflowModel, String)}
	 */
	@Deprecated
	WorkflowActionModel findWorkflowActionByCode(final String code);

	/**
	 * Finds a work flow action with a particular workflowaction status by user.
	 * 
	 * @param status
	 *           the status of the workflow
	 * @param user
	 *           the user
	 * @return WorkflowActionModel
	 * @deprecated Since 4.4. Use
	 *             {@link #findWorkflowActionsByUserActionCodeAndStatus(de.hybris.platform.workflow.enums.WorkflowActionStatus, String, de.hybris.platform.core.model.user.UserModel)}
	 */
	@Deprecated
	Collection<WorkflowActionModel> findActionByUserAndStatus(final WorkflowActionStatus status, final UserModel user);

	/**
	 * Finds a work flow action with a particular workflowaction status, user and template qualifier
	 */
	Collection<WorkflowActionModel> findWorkflowActionsByUserActionCodeAndStatus(final WorkflowActionStatus status,
			final String qualifier, final UserModel user);

	/**
	 * Find all the workflow actions for given user
	 */
	Collection<WorkflowActionModel> findWorkflowActionsByUser(UserModel user);

}

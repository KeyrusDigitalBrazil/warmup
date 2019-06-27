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
package de.hybris.platform.b2b.services;

import de.hybris.platform.b2b.enums.WorkflowTemplateType;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Collection;
import java.util.List;


/**
 * A integration service for workflow and process engine.
 *
 * @spring.bean b2bWorkflowIntegrationService
 */
public interface B2BWorkflowIntegrationService
{

	/**
	 * The Enum ACTIONCODES.
	 */
	public enum ACTIONCODES
	{
		APPROVAL, APPROVED, REJECTED, BACK_TO_PROCESSENGINE, END, START, ACCEPT_SALES_QUOTES, REJECT_SALES_QUOTES
	}

	/**
	 * The Enum DECISIONCODES.
	 */
	public enum DECISIONCODES
	{
		APPROVE, REJECT, CANCEL, CONFIRM
	}


	/**
	 * Gets all WorkFlowActions that are of {@link WorkflowActionType#START} type.
	 *
	 * @param workflow
	 *           the WorkflowModel
	 * @return a list of WorkflowActionModels
	 */
	Collection<WorkflowActionModel> getStartWorkflowActions(WorkflowModel workflow);

	/**
	 * Gets the WorkflowTemplateModel based on the code.
	 *
	 * @param code
	 *           the WorkflowActionType code
	 * @return WorkflowTemplateModel
	 * @deprecated Since 4.4. {@link de.hybris.platform.workflow.WorkflowTemplateService#getWorkflowTemplateForCode(String)}
	 */
	@Deprecated
	WorkflowTemplateModel getWorkflowTemplateForCode(String code);

	/**
	 * This will create a WorkflowTemplateModel if one does not exist
	 *
	 * @param users
	 *           the approvers that a work flow template will be created for
	 * @param code
	 *           the code of the template
	 * @param description
	 *           the short description for the template
	 * @param templateType
	 *           the strategy type of the template
	 * @return newly created WorkflowTemplateModel
	 */
	WorkflowTemplateModel createWorkflowTemplate(List<? extends UserModel> users, String code, String description,
			WorkflowTemplateType templateType);

	/**
	 * Generates a list of appended prefix_{@link UserModel#UID}
	 *
	 * @param prefix
	 *           the <code>String</code> to which the user id is appended to
	 * @param users
	 *           the list of users in which the template code will be generated for
	 * @return the template code of appended values
	 */
	String generateWorkflowTemplateCode(String prefix, List<? extends UserModel> users);

	/**
	 * @deprecated Since 4.4. Use
	 *             {@link de.hybris.platform.workflow.WorkflowService#createWorkflow(String, de.hybris.platform.workflow.model.WorkflowTemplateModel, java.util.List, de.hybris.platform.core.model.user.UserModel)}
	 *
	 * @param template
	 *           the workflow template
	 * @param attachments
	 *           the workflow attachments
	 * @return the workflow
	 */
	@Deprecated
	WorkflowModel createWorkflow(final WorkflowTemplateModel template, final List<? extends ItemModel> attachments);

	/**
	 * @param workflowModel
	 * @deprecated Since 4.4. Use
	 *             {@link de.hybris.platform.workflow.WorkflowProcessingService#startWorkflow(de.hybris.platform.workflow.model.WorkflowModel)}
	 */
	@Deprecated
	void startWorkflow(final WorkflowModel workflowModel);

	/**
	 * @deprecated As of hybris 4.4, replaced by
	 *             {@link #getWorkflowActionsForActionStatusAndUser(WorkflowActionStatus, String, UserModel)}
	 *
	 * @param status
	 *           the action status
	 * @param qualifier
	 *           the qualifier
	 * @param user
	 *           the assigned user
	 * @return the {@link Collection} of {WorkflowActionModel}
	 */
	@Deprecated
	Collection<WorkflowActionModel> findByActionStatusAndUser(final WorkflowActionStatus status, String qualifier,
			final UserModel user);

	/**
	 * Gets all WorkflowActions by user, workflowActionStatus (eg IN_PROGRESS) and qualifier (eg APPROVAL)
	 *
	 * @param status
	 *           the work flow action status
	 * @param qualifier
	 *           the qualifier (eg APPROVAL)
	 * @param user
	 *           the user
	 * @return WorkflowActionModel the set of work flow actions
	 */
	Collection<WorkflowActionModel> getWorkflowActionsForActionStatusAndUser(final WorkflowActionStatus status, String qualifier,
			final UserModel user);

	/**
	 * Gets the associated order to the WorkflowActionModel qualifier. The qualifier's attachment of type
	 * B2BApprovalProcessModel will have the associated order.
	 *
	 * @param workflowActionModel
	 *           the work flow action
	 * @return the order associated to the attachment
	 */
	OrderModel getOrderFromAction(final WorkflowActionModel workflowActionModel);

	/**
	 * @param workflowActionModel
	 * @deprecated Since 4.4. Use {@link #decideAction(de.hybris.platform.workflow.model.WorkflowActionModel, String)}
	 *             decideAction(action, DECISIONCODES.REJECT.name())
	 */
	@Deprecated
	void rejectWorkflowAction(final WorkflowActionModel workflowActionModel);

	/**
	 * @param workflowActionModel
	 * @deprecated Since 4.4. Use {@link #decideAction(de.hybris.platform.workflow.model.WorkflowActionModel, String)}
	 *             decideAction(action, DECISIONCODES.APPROVE.name())
	 */
	@Deprecated
	void approveWorkflowAction(final WorkflowActionModel workflowActionModel);

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #getActionForCode(String)}
	 *
	 * @param code
	 *           the action code
	 * @return the workflow action
	 */
	@Deprecated
	WorkflowActionModel getActionByCode(final String code);

	/**
	 * Get WorkflowAction for its code.
	 *
	 * @param code
	 *           the WorkflowAction's code
	 * @return WorkflowActionModel
	 */
	WorkflowActionModel getActionForCode(final String code);

	/**
	 * @deprecated As of hybris 4.4, replaced by {@link #getWorkflowForOrder(OrderModel)}
	 *
	 * @param order
	 *           the order for which to find the workflow
	 * @return the order {@link WorkflowModel}
	 */
	@Deprecated
	WorkflowModel findWorkflowForOrder(final OrderModel order);

	/**
	 * Get the Workflow for an Order.
	 *
	 * @param order
	 *           the order
	 * @return WorkflowModel
	 */
	WorkflowModel getWorkflowForOrder(final OrderModel order);

	/**
	 * Makes a decision on an action based on the qualifier (The qualifier is used to look up a decision with its code
	 * build from {@link WorkflowActionModel#CODE}_<param>decisionQualifier</param>)
	 *
	 * @param workflowActionModel
	 *           The action to make a decision upon
	 * @param decisionQualifier
	 *           a qualifier based on {@link DECISIONCODES} enumeration
	 */
	void decideAction(WorkflowActionModel workflowActionModel, String decisionQualifier);

	/**
	 * Gets a collection of workflow actions for a user
	 * 
	 * @param user
	 *           the user
	 * @return the {@link Collection} of {@link WorkflowActionModel}
	 */
	Collection<WorkflowActionModel> getWorkflowActionsForUser(UserModel user);
}

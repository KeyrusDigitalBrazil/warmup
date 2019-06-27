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
package de.hybris.platform.b2b.strategies.impl;

import de.hybris.platform.b2b.enums.WorkflowTemplateType;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.AutomatedWorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import java.util.List;
import org.apache.log4j.Logger;


public class OrderApprovalWorkflowTemplateStrategy extends AbstractWorkflowTemplateStrategy
{
	private static final Logger LOG = Logger.getLogger(OrderApprovalWorkflowTemplateStrategy.class);

	@Override
	public WorkflowTemplateModel createWorkflowTemplate(final List<? extends UserModel> users, final String code,
			final String description)
	{
		return (WorkflowTemplateModel) getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				final UserModel admin = getUserService().getAdminUser();
				getUserService().setCurrentUser(admin);

				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Createing WorkflowTemplate for code %s with description %s", code, description));
				}
				final WorkflowTemplateModel workflowTemplateModel = createBlankWorkflowTemplate(code, description, admin);
				final AutomatedWorkflowActionTemplateModel wakeUpProcessEngineAutomatedAction = createAutomatedWorkflowActionTemplate(
						code, B2BWorkflowIntegrationService.ACTIONCODES.BACK_TO_PROCESSENGINE.name(), WorkflowActionType.NORMAL, admin,
						workflowTemplateModel, null, "b2bAfterWorkflowAction");

				final AutomatedWorkflowActionTemplateModel afterApproveOrderWorkflowDecisionAction = createAutomatedWorkflowActionTemplate(
						code, B2BWorkflowIntegrationService.ACTIONCODES.APPROVED.name() + "_END", WorkflowActionType.NORMAL, admin,
						workflowTemplateModel, null, "afterApproveOrderWorkflowDecisionAction");
				final AutomatedWorkflowActionTemplateModel afterRejectOrderWorkflowDecisionAction = createAutomatedWorkflowActionTemplate(
						code, B2BWorkflowIntegrationService.ACTIONCODES.REJECTED.name() + "_END", WorkflowActionType.NORMAL, admin,
						workflowTemplateModel, null, "afterRejectOrderWorkflowDecisionAction");

				for (final UserModel approver : users)
				{
					final AutomatedWorkflowActionTemplateModel approveDecisionAutomatedAction = createAutomatedWorkflowActionTemplate(
							code, B2BWorkflowIntegrationService.ACTIONCODES.APPROVED.name(), WorkflowActionType.NORMAL, approver,
							workflowTemplateModel, null, "approveDecisionAutomatedAction");

					final AutomatedWorkflowActionTemplateModel rejectDecisionAutomatedAction = createAutomatedWorkflowActionTemplate(
							code, B2BWorkflowIntegrationService.ACTIONCODES.REJECTED.name(), WorkflowActionType.NORMAL, approver,
							workflowTemplateModel, null, "rejectDecisionAutomatedAction");

					final WorkflowActionTemplateModel action = createWorkflowActionTemplateModel(code,
							B2BWorkflowIntegrationService.ACTIONCODES.APPROVAL.name(), WorkflowActionType.START, approver,
							workflowTemplateModel);
					// the approve decision links
					createLink(action, approveDecisionAutomatedAction, B2BWorkflowIntegrationService.DECISIONCODES.APPROVE.name(),
							Boolean.TRUE);
					createLink(approveDecisionAutomatedAction, afterApproveOrderWorkflowDecisionAction,
							B2BWorkflowIntegrationService.DECISIONCODES.APPROVE.name() + "_END", Boolean.TRUE);
					createLink(afterApproveOrderWorkflowDecisionAction, wakeUpProcessEngineAutomatedAction,
							B2BWorkflowIntegrationService.ACTIONCODES.BACK_TO_PROCESSENGINE.name(), Boolean.FALSE);
					// reject decision links
					createLink(action, rejectDecisionAutomatedAction, B2BWorkflowIntegrationService.DECISIONCODES.REJECT.name(),
							Boolean.FALSE);
					createLink(rejectDecisionAutomatedAction, afterRejectOrderWorkflowDecisionAction,
							B2BWorkflowIntegrationService.DECISIONCODES.REJECT.name() + "_END", Boolean.FALSE);
					createLink(afterRejectOrderWorkflowDecisionAction, wakeUpProcessEngineAutomatedAction,
							B2BWorkflowIntegrationService.ACTIONCODES.BACK_TO_PROCESSENGINE.name(), Boolean.FALSE);
				}
				//end finishAction to end workflow
				final WorkflowActionTemplateModel finishAction = createWorkflowActionTemplateModel(code,
						B2BWorkflowIntegrationService.ACTIONCODES.END.name(), WorkflowActionType.END, admin, workflowTemplateModel);
				createLink(wakeUpProcessEngineAutomatedAction, finishAction, "WORKFLOW_FINISHED", Boolean.FALSE);

				return workflowTemplateModel;
			}
		});
	}

	@Override
	public String getWorkflowTemplateType()
	{
		return WorkflowTemplateType.ORDER_APPROVAL.getCode();
	}
}

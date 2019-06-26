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
import de.hybris.platform.b2b.process.approval.actions.AfterQuoteApprovalWorkflowDecisionAction;
import de.hybris.platform.b2b.process.approval.actions.AfterQuoteRejectionWorkflowDecisionAction;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.processengine.action.AfterWorkflowAction;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.AutomatedWorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import java.util.List;
import org.apache.log4j.Logger;


public class SalesQuotesWorkflowTemplateStrategy extends AbstractWorkflowTemplateStrategy
{
	private static final Logger LOG = Logger.getLogger(MerchantCheckWorkflowTemplateStrategy.class);

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

				final AutomatedWorkflowActionTemplateModel autoActionTemplate = createAutomatedWorkflowActionTemplate(code,
						B2BWorkflowIntegrationService.ACTIONCODES.BACK_TO_PROCESSENGINE.name(), WorkflowActionType.NORMAL, admin,
						workflowTemplateModel, AfterWorkflowAction.class, null);


				for (final UserModel approver : users)
				{

					final AutomatedWorkflowActionTemplateModel approveDecisionTemplate = createAutomatedWorkflowActionTemplate(code,
							B2BWorkflowIntegrationService.ACTIONCODES.ACCEPT_SALES_QUOTES.name(), WorkflowActionType.NORMAL, approver,
							workflowTemplateModel, AfterQuoteApprovalWorkflowDecisionAction.class, null);
					final AutomatedWorkflowActionTemplateModel rejectDecisionTemplate = createAutomatedWorkflowActionTemplate(code,
							B2BWorkflowIntegrationService.ACTIONCODES.REJECT_SALES_QUOTES.name(), WorkflowActionType.NORMAL, approver,
							workflowTemplateModel, AfterQuoteRejectionWorkflowDecisionAction.class, null);

					final WorkflowActionTemplateModel action = createWorkflowActionTemplateModel(code,
							B2BWorkflowIntegrationService.ACTIONCODES.APPROVAL.name(), WorkflowActionType.START, approver,
							workflowTemplateModel);

					createLink(action, approveDecisionTemplate, B2BWorkflowIntegrationService.DECISIONCODES.APPROVE.name(),
							Boolean.FALSE);

					createLink(approveDecisionTemplate, autoActionTemplate,
							B2BWorkflowIntegrationService.ACTIONCODES.BACK_TO_PROCESSENGINE.name(), Boolean.TRUE);


					createLink(action, rejectDecisionTemplate, B2BWorkflowIntegrationService.DECISIONCODES.REJECT.name(),
							Boolean.FALSE);
					createLink(rejectDecisionTemplate, autoActionTemplate,
							B2BWorkflowIntegrationService.ACTIONCODES.BACK_TO_PROCESSENGINE.name(), Boolean.FALSE);
				}
				//end finishAction to end workflow
				final WorkflowActionTemplateModel finishAction = createWorkflowActionTemplateModel(code,
						B2BWorkflowIntegrationService.ACTIONCODES.END.name(), WorkflowActionType.END, admin, workflowTemplateModel);
				createLink(autoActionTemplate, finishAction, "WORKFLOW_FINISHED", Boolean.FALSE);
				return workflowTemplateModel;
			}
		});
	}

	@Override
	public String getWorkflowTemplateType()
	{
		return WorkflowTemplateType.SALES_QUOTES.getCode();
	}
}

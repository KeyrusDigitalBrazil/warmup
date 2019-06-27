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
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import java.util.List;
import org.apache.log4j.Logger;


public class CreditAlertWorkflowTemplateStrategy extends AbstractWorkflowTemplateStrategy
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
				final UserModel user = users.iterator().next();
				getUserService().setCurrentUser(admin);


				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Createing WorkflowTemplate for code %s with description %s", code, description));
				}
				final WorkflowTemplateModel workflowTemplateModel = createBlankWorkflowTemplate(code, description, admin);

				final WorkflowActionTemplateModel startWorkflowAction = createWorkflowActionTemplateModel(code,
						B2BWorkflowIntegrationService.ACTIONCODES.START.name(), WorkflowActionType.START, user, workflowTemplateModel);

				//end finishAction to end workflow
				final WorkflowActionTemplateModel finishAction = createWorkflowActionTemplateModel(code,
						B2BWorkflowIntegrationService.ACTIONCODES.END.name(), WorkflowActionType.END, user, workflowTemplateModel);

				createLink(startWorkflowAction, finishAction, description, Boolean.FALSE);
				getModelService().save(workflowTemplateModel);
				return workflowTemplateModel;
			}
		});
	}

	@Override
	public String getWorkflowTemplateType()
	{
		return WorkflowTemplateType.CREDIT_LIMIT_ALERT.getCode();
	}
}

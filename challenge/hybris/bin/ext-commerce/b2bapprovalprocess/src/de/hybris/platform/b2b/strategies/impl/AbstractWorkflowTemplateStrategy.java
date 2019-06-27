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

import de.hybris.platform.b2b.strategies.WorkflowTemplateStrategy;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.AutomatedWorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowDecisionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public abstract class AbstractWorkflowTemplateStrategy implements WorkflowTemplateStrategy
{
	private UserService userService;
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(AbstractWorkflowTemplateStrategy.class);
	private WorkflowTemplateService workflowTemplateService;
	private ModelService modelService;
	private SessionService sessionService;

	@Override
	public abstract WorkflowTemplateModel createWorkflowTemplate(final List<? extends UserModel> users, final String code,
			final String description);

	/**
	 * @return A unique identifier of the stragegy implementation used for sellecting the stragegy to use in the service.
	 */
	public abstract String getWorkflowTemplateType();


	protected WorkflowTemplateModel createBlankWorkflowTemplate(final String code, final String description, final UserModel user)
	{
		final WorkflowTemplateModel workflowTemplateModel = getModelService().create(WorkflowTemplateModel.class);
		workflowTemplateModel.setCode(code);
		workflowTemplateModel.setName(code, Locale.ENGLISH);
		workflowTemplateModel.setDescription(description, Locale.ENGLISH);
		workflowTemplateModel.setOwner(user);
		getModelService().save(workflowTemplateModel);
		return workflowTemplateModel;
	}

	/**
	 * Creates an automated workflow cronjob by either joblass or springBean id
	 * 
	 * @param code
	 * @param qualifier
	 * @param actionType
	 * @param user
	 * @param workflowTemplateModel
	 * @param jobClass
	 *           class of the automated action which must implement
	 *           {@link de.hybris.platform.workflow.jalo.AutomatedWorkflowTemplateJob}
	 * @param jobHandlerBeanId
	 *           Spring bean ID of autmated action which implments
	 *           {@link de.hybris.platform.workflow.jobs.AutomatedWorkflowTemplateJob}
	 * @return A saved automated job model
	 */
	protected AutomatedWorkflowActionTemplateModel createAutomatedWorkflowActionTemplate(final String code,
			final String qualifier, final WorkflowActionType actionType, final UserModel user,
			final WorkflowTemplateModel workflowTemplateModel,
			final Class<? extends de.hybris.platform.workflow.jalo.AutomatedWorkflowTemplateJob> jobClass,
			final String jobHandlerBeanId)
	{
		final AutomatedWorkflowActionTemplateModel automatedWorkfow = getModelService().create(
				AutomatedWorkflowActionTemplateModel.class);
		automatedWorkfow.setActionType(actionType);
		automatedWorkfow.setPrincipalAssigned(user);
		automatedWorkfow.setWorkflow(workflowTemplateModel);
		if (jobClass != null)
		{
			automatedWorkfow.setJobClass(jobClass);
		}

		if (StringUtils.isNotBlank(jobHandlerBeanId))
		{
			automatedWorkfow.setJobHandler(jobHandlerBeanId);
		}
		automatedWorkfow.setCode(code);
		automatedWorkfow.setQualifier(qualifier);
		automatedWorkfow.setName(qualifier, Locale.ENGLISH);
		getModelService().save(automatedWorkfow);

		final List<WorkflowActionTemplateModel> actions = new ArrayList<WorkflowActionTemplateModel>(
				workflowTemplateModel.getActions());
		actions.add(automatedWorkfow);
		workflowTemplateModel.setActions(actions);
		this.getModelService().save(workflowTemplateModel);
		return automatedWorkfow;
	}

	protected WorkflowActionTemplateModel createWorkflowActionTemplateModel(final String code, final String qualifier,
			final WorkflowActionType actionType, final UserModel user, final WorkflowTemplateModel workflowTemplateModel)
	{
		final WorkflowActionTemplateModel actionTemplateModel = getModelService().create(WorkflowActionTemplateModel.class);
		actionTemplateModel.setActionType(actionType);
		actionTemplateModel.setPrincipalAssigned(user);
		actionTemplateModel.setWorkflow(workflowTemplateModel);
		actionTemplateModel.setCode(code);
		actionTemplateModel.setQualifier(qualifier);
		actionTemplateModel.setName(qualifier, Locale.ENGLISH);
		getModelService().save(actionTemplateModel);

		final List<WorkflowActionTemplateModel> actions = new ArrayList<WorkflowActionTemplateModel>(
				workflowTemplateModel.getActions());
		actions.add(actionTemplateModel);
		workflowTemplateModel.setActions(actions);
		this.getModelService().save(workflowTemplateModel);
		return actionTemplateModel;
	}


	protected void createLink(final WorkflowActionTemplateModel fromAction, final WorkflowActionTemplateModel toAction,
			final String qualifier, final Boolean isAndConnection)
	{
		final WorkflowDecisionTemplateModel workflowDecisionTemplate = getModelService()
				.create(WorkflowDecisionTemplateModel.class);
		workflowDecisionTemplate.setName(qualifier, Locale.ENGLISH);
		workflowDecisionTemplate.setCode(fromAction.getCode());
		workflowDecisionTemplate.setQualifier(qualifier);
		workflowDecisionTemplate.setActionTemplate(fromAction);
		workflowDecisionTemplate.setToTemplateActions(Collections.singletonList(toAction));
		getModelService().save(workflowDecisionTemplate);
		final Collection<WorkflowDecisionTemplateModel> decisionTemplates = new ArrayList<WorkflowDecisionTemplateModel>(
				fromAction.getDecisionTemplates());
		decisionTemplates.add(workflowDecisionTemplate);
		fromAction.setDecisionTemplates(decisionTemplates);
		this.getModelService().save(fromAction);

		setConnectionBetweenActionAndDecision(toAction, isAndConnection, workflowDecisionTemplate);
	}


	protected void setConnectionBetweenActionAndDecision(final WorkflowActionTemplateModel toAction,
			final Boolean isAndConnection, final WorkflowDecisionTemplateModel workflowDecisionTemplate)
	{

		if (isAndConnection.booleanValue())
		{
			getWorkflowTemplateService().setAndConnectionBetweenActionAndDecision(workflowDecisionTemplate, toAction);
		}
		else
		{
			getWorkflowTemplateService().setOrConnectionBetweenActionAndDecision(workflowDecisionTemplate, toAction);
		}
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected WorkflowTemplateService getWorkflowTemplateService()
	{
		return workflowTemplateService;
	}

	@Required
	public void setWorkflowTemplateService(final WorkflowTemplateService workflowTemplateService)
	{
		this.workflowTemplateService = workflowTemplateService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}

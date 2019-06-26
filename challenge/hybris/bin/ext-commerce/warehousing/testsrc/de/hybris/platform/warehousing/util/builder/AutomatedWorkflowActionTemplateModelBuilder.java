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
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.AutomatedWorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowDecisionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Collection;
import java.util.Locale;


public class AutomatedWorkflowActionTemplateModelBuilder
{
	private final AutomatedWorkflowActionTemplateModel model;

	private AutomatedWorkflowActionTemplateModelBuilder()
	{
		model = new AutomatedWorkflowActionTemplateModel();
	}

	private AutomatedWorkflowActionTemplateModel getModel()
	{
		return model;
	}

	public static AutomatedWorkflowActionTemplateModelBuilder aModel()
	{
		return new AutomatedWorkflowActionTemplateModelBuilder();
	}

	public AutomatedWorkflowActionTemplateModel build()
	{
		return getModel();
	}

	public AutomatedWorkflowActionTemplateModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public AutomatedWorkflowActionTemplateModelBuilder withName(final String name)
	{
		getModel().setName(name, Locale.ENGLISH);
		return this;
	}

	public AutomatedWorkflowActionTemplateModelBuilder withPrincipal(final PrincipalModel principal)
	{
		getModel().setPrincipalAssigned(principal);
		return this;
	}

	public AutomatedWorkflowActionTemplateModelBuilder withDecision(final Collection<WorkflowDecisionTemplateModel> decisions)
	{
		getModel().setDecisionTemplates(decisions);
		return this;
	}

	public AutomatedWorkflowActionTemplateModelBuilder withAction(final WorkflowActionType action)
	{
		getModel().setActionType(action);
		return this;
	}

	public AutomatedWorkflowActionTemplateModelBuilder withJobHandler(final String jobHandler)
	{
		getModel().setJobHandler(jobHandler);
		return this;
	}


	public AutomatedWorkflowActionTemplateModelBuilder withWorkflow(final WorkflowTemplateModel workflow)
	{
		getModel().setWorkflow(workflow);
		return this;
	}
}

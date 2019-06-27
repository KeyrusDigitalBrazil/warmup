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
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowDecisionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Collection;
import java.util.Locale;


public class WorkflowActionTemplateModelBuilder
{
	private final WorkflowActionTemplateModel model;

	private WorkflowActionTemplateModelBuilder()
	{
		model = new WorkflowActionTemplateModel();
	}

	private WorkflowActionTemplateModel getModel()
	{
		return model;
	}

	public static WorkflowActionTemplateModelBuilder aModel()
	{
		return new WorkflowActionTemplateModelBuilder();
	}

	public WorkflowActionTemplateModel build()
	{
		return getModel();
	}

	public WorkflowActionTemplateModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public WorkflowActionTemplateModelBuilder withName(final String name)
	{
		getModel().setName(name, Locale.ENGLISH);
		return this;
	}

	public WorkflowActionTemplateModelBuilder withPrincipal(final PrincipalModel principal)
	{
		getModel().setPrincipalAssigned(principal);
		return this;
	}

	public WorkflowActionTemplateModelBuilder withAction(final WorkflowActionType action)
	{
		getModel().setActionType(action);
		return this;
	}

	public WorkflowActionTemplateModelBuilder withWorkflow(final WorkflowTemplateModel workflow)
	{
		getModel().setWorkflow(workflow);
		return this;
	}

	public WorkflowActionTemplateModelBuilder withDecision(final Collection<WorkflowDecisionTemplateModel> decisions)
	{
		getModel().setDecisionTemplates(decisions);
		return this;
	}
}

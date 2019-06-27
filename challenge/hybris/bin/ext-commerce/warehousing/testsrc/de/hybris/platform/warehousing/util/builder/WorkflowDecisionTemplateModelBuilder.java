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

import de.hybris.platform.workflow.model.WorkflowDecisionTemplateModel;

import java.util.Locale;


public class WorkflowDecisionTemplateModelBuilder
{
	private final WorkflowDecisionTemplateModel model;

	private WorkflowDecisionTemplateModelBuilder()
	{
		model = new WorkflowDecisionTemplateModel();
	}

	private WorkflowDecisionTemplateModel getModel()
	{
		return model;
	}

	public static WorkflowDecisionTemplateModelBuilder aModel()
	{
		return new WorkflowDecisionTemplateModelBuilder();
	}

	public WorkflowDecisionTemplateModel build()
	{
		return getModel();
	}

	public WorkflowDecisionTemplateModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public WorkflowDecisionTemplateModelBuilder withName(final String name)
	{
		getModel().setName(name, Locale.ENGLISH);
		return this;
	}

}

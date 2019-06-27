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
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Collection;
import java.util.Locale;


public class WorkflowTemplateModelBuilder
{
	private final WorkflowTemplateModel model;

	private WorkflowTemplateModelBuilder()
	{
		model = new WorkflowTemplateModel();
	}

	private WorkflowTemplateModel getModel()
	{
		return model;
	}

	public static WorkflowTemplateModelBuilder aModel()
	{
		return new WorkflowTemplateModelBuilder();
	}

	public WorkflowTemplateModel build()
	{
		return getModel();
	}

	public WorkflowTemplateModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public WorkflowTemplateModelBuilder withName(final String name)
	{
		getModel().setName(name, Locale.ENGLISH);
		return this;
	}

	public WorkflowTemplateModelBuilder withPrincipals(final Collection<PrincipalModel> principals)
	{
		getModel().setVisibleForPrincipals(principals);
		return this;
	}



}

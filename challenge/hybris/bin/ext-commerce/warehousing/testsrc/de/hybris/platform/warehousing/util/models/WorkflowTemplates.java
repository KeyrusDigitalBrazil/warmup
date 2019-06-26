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
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.warehousing.util.builder.WorkflowTemplateModelBuilder;
import de.hybris.platform.warehousing.util.dao.WarehousingDao;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Required;


public class WorkflowTemplates extends AbstractItems<WorkflowTemplateModel>
{
	public static final String WORKFLOW_CONSIGNMENT_TEMPLATE_CODE = "ConsignmentTemplate";
	public static final String WORKFLOW_CONSIGNMENT_TEMPLATE_NAME = "Consignment Template";

	public static final String WORKFLOW_ASN_TEMPLATE_CODE = "AsnTemplate";
	public static final String WORKFLOW_ASN_TEMPLATE_NAME = "Asn Template";

	private WarehousingDao<WorkflowTemplateModel> workflowTemplateDao;
	private UserService userService;
	private UserGroups userGroups;
	private WorkflowActionTemplates workflowActionTemplates;
	private AutomatedWorkflowActionTemplates automatedWorkflowActionTemplates;

	public WorkflowTemplateModel ConsignmentTemplate()
	{
		return getOrSaveAndReturn(() -> getWorkflowTemplateDao().getByCode(WORKFLOW_CONSIGNMENT_TEMPLATE_CODE),
				() -> WorkflowTemplateModelBuilder.aModel().withCode(WORKFLOW_CONSIGNMENT_TEMPLATE_CODE)
						.withName(WORKFLOW_CONSIGNMENT_TEMPLATE_NAME).withPrincipals(
								Arrays.asList(getUserGroups().AdminGroup(), getUserGroups().WarehouseAdministratorGroup(),
										getUserGroups().WarehouseManagerGroup(), getUserGroups().WarehouseAgentGroup(),
										getUserGroups().CustomerGroup())).build());
	}


	public WorkflowTemplateModel AsnTemplate()
	{
		return getOrSaveAndReturn(() -> getWorkflowTemplateDao().getByCode(WORKFLOW_ASN_TEMPLATE_CODE),
				() -> WorkflowTemplateModelBuilder.aModel().withCode(WORKFLOW_ASN_TEMPLATE_CODE).withName(WORKFLOW_ASN_TEMPLATE_NAME)
						.withPrincipals(Arrays.asList(getUserGroups().AdminGroup(), getUserGroups().WarehouseAdministratorGroup()))
						.build());
	}

	protected WarehousingDao<WorkflowTemplateModel> getWorkflowTemplateDao()
	{
		return workflowTemplateDao;
	}

	@Required
	public void setWorkflowTemplateDao(final WarehousingDao<WorkflowTemplateModel> workflowTemplateDao)
	{
		this.workflowTemplateDao = workflowTemplateDao;
	}

	protected UserGroups getUserGroups()
	{
		return userGroups;
	}

	@Required
	public void setUserGroups(final UserGroups userGroups)
	{
		this.userGroups = userGroups;
	}

	protected WorkflowActionTemplates getWorkflowActionTemplates()
	{
		return workflowActionTemplates;
	}

	@Required
	public void setWorkflowActionTemplates(final WorkflowActionTemplates workflowActionTemplates)
	{
		this.workflowActionTemplates = workflowActionTemplates;
	}

	protected AutomatedWorkflowActionTemplates getAutomatedWorkflowActionTemplates()
	{
		return automatedWorkflowActionTemplates;
	}

	@Required
	public void setAutomatedWorkflowActionTemplates(final AutomatedWorkflowActionTemplates automatedWorkflowActionTemplates)
	{
		this.automatedWorkflowActionTemplates = automatedWorkflowActionTemplates;
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
}

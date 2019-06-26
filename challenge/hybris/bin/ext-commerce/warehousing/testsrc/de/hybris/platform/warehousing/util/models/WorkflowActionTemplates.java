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
import de.hybris.platform.warehousing.util.builder.WorkflowActionTemplateModelBuilder;
import de.hybris.platform.warehousing.util.dao.WarehousingDao;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Required;


public class WorkflowActionTemplates extends AbstractItems<WorkflowActionTemplateModel>
{
	//consignment wf
	public static final String PICKING_CODE = "NPR_Picking";
	public static final String PICKING_NAME = "Picking";
	public static final String PACKING_CODE = "NPR_Packing";
	public static final String PACKING_NAME = "Packing";
	public static final String SHIPPING_CODE = "NPR_Shipping";
	public static final String SHIPPING_NAME = "Shipping";
	public static final String PICKUP_CODE = "NPR_Pickup";
	public static final String PICKUP_NAME = "Pick up";
	public static final String END_CODE = "NPR_End";
	public static final String END_NAME = "End of the Workflow";
	//asn wf
	public static final String ASN_END_CODE = "ASN_End";
	public static final String ASN_END_NAME = "End of the Asn Workflow";

	private WarehousingDao<WorkflowActionTemplateModel> workflowActionTemplateDao;
	private WorkflowDecisionTemplates workflowDecisionTemplates;
	private WorkflowTemplates workflowTemplates;
	private UserService userService;

	public WorkflowActionTemplateModel Picking()
	{
		return getOrSaveAndReturn(() -> getWorkflowActionTemplateDao().getByCode(PICKING_CODE),
				() -> WorkflowActionTemplateModelBuilder.aModel().withCode(PICKING_CODE).withName(PICKING_NAME)
						.withWorkflow(getWorkflowTemplates().ConsignmentTemplate()).withAction(WorkflowActionType.START)
						.withPrincipal(getUserService().getAdminUser())
						.withDecision(Arrays.asList(getWorkflowDecisionTemplates().Picking())).build());
	}

	public WorkflowActionTemplateModel Packing()
	{
		return getOrSaveAndReturn(() -> getWorkflowActionTemplateDao().getByCode(PACKING_CODE),
				() -> WorkflowActionTemplateModelBuilder.aModel().withCode(PACKING_CODE).withName(PACKING_NAME)
						.withWorkflow(getWorkflowTemplates().ConsignmentTemplate()).withAction(WorkflowActionType.NORMAL)
						.withPrincipal(getUserService().getAdminUser())
						.withDecision(Arrays.asList(getWorkflowDecisionTemplates().Packing())).build());
	}

	public WorkflowActionTemplateModel Shipping()
	{
		return getOrSaveAndReturn(() -> getWorkflowActionTemplateDao().getByCode(SHIPPING_CODE),
				() -> WorkflowActionTemplateModelBuilder.aModel().withCode(SHIPPING_CODE).withName(SHIPPING_NAME)
						.withWorkflow(getWorkflowTemplates().ConsignmentTemplate()).withAction(WorkflowActionType.NORMAL)
						.withPrincipal(getUserService().getAdminUser())
						.withDecision(Arrays.asList(getWorkflowDecisionTemplates().Shipping())).build());
	}

	public WorkflowActionTemplateModel Pickup()
	{
		return getOrSaveAndReturn(() -> getWorkflowActionTemplateDao().getByCode(PICKUP_CODE),
				() -> WorkflowActionTemplateModelBuilder.aModel().withCode(PICKUP_CODE).withName(PICKUP_NAME)
						.withWorkflow(getWorkflowTemplates().ConsignmentTemplate()).withAction(WorkflowActionType.NORMAL)
						.withPrincipal(getUserService().getAdminUser())
						.withDecision(Arrays.asList(getWorkflowDecisionTemplates().Pickup())).build());
	}

	public WorkflowActionTemplateModel End()
	{
		return getOrSaveAndReturn(() -> getWorkflowActionTemplateDao().getByCode(END_CODE),
				() -> WorkflowActionTemplateModelBuilder.aModel().withCode(END_CODE).withName(END_NAME)
						.withPrincipal(getUserService().getAdminUser()).withWorkflow(getWorkflowTemplates().ConsignmentTemplate())
						.withAction(WorkflowActionType.END).build());
	}

	public WorkflowActionTemplateModel EndAsn()
	{
		return getOrSaveAndReturn(() -> getWorkflowActionTemplateDao().getByCode(ASN_END_CODE),
				() -> WorkflowActionTemplateModelBuilder.aModel().withCode(ASN_END_CODE).withName(ASN_END_NAME)
						.withPrincipal(getUserService().getAdminUser()).withWorkflow(getWorkflowTemplates().AsnTemplate())
						.withAction(WorkflowActionType.END).build());
	}

	protected WarehousingDao<WorkflowActionTemplateModel> getWorkflowActionTemplateDao()
	{
		return workflowActionTemplateDao;
	}

	@Required
	public void setWorkflowActionTemplateDao(final WarehousingDao<WorkflowActionTemplateModel> workflowActionTemplateDao)
	{
		this.workflowActionTemplateDao = workflowActionTemplateDao;
	}


	protected WorkflowDecisionTemplates getWorkflowDecisionTemplates()
	{
		return workflowDecisionTemplates;
	}

	@Required
	public void setWorkflowDecisionTemplates(final WorkflowDecisionTemplates workflowDecisionTemplates)
	{
		this.workflowDecisionTemplates = workflowDecisionTemplates;
	}

	protected WorkflowTemplates getWorkflowTemplates()
	{
		return workflowTemplates;
	}

	@Required
	public void setWorkflowTemplates(final WorkflowTemplates workflowTemplates)
	{
		this.workflowTemplates = workflowTemplates;
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

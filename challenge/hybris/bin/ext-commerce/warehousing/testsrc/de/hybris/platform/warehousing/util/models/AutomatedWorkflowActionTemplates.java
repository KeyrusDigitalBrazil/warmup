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
import de.hybris.platform.warehousing.util.builder.AutomatedWorkflowActionTemplateModelBuilder;
import de.hybris.platform.warehousing.util.dao.WarehousingDao;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.AutomatedWorkflowActionTemplateModel;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Required;


public class AutomatedWorkflowActionTemplates extends AbstractItems<AutomatedWorkflowActionTemplateModel>
{
	//consignment wf
	public static final String AUTO_PACK_CODE = "NPR_Automated_Packing";
	public static final String AUTO_PACK_NAME = "Automated Packing";
	public static final String AUTO_PACK_HANDLER = "taskAssignmentPackConsignmentAction";
	public static final String AUTO_SHIP_CODE = "NPR_Automated_Shipping";
	public static final String AUTO_SHIP_NAME = "Automated Shipping";
	public static final String AUTO_SHIP_HANDLER = "taskAssignmentShipConsignmentAction";
	public static final String AUTO_PICKUP_CODE = "NPR_Automated_Pickup";
	public static final String AUTO_PICKUP_NAME = "Automated Pick up";
	public static final String AUTO_PICKUP_HANDLER = "taskAssignmentPickupConsignmentAction";
	//asn wf
	public static final String AUTO_REALLOCATE_CONS_CODE = "ASN_Automated_ReallocateConsignments";
	public static final String AUTO_REALLOCATE_CONS_NAME = "Automated Reallocate Consignments";
	public static final String AUTO_REALLOCATE_HANDLER = "taskReallocateConsignmentsOnAsnCancelAction";

	public static final String AUTO_DELETE_CANCELEVENT_CODE = "ASN_Automated_DeleteCancellationEvents";
	public static final String AUTO_DELETE_CANCELEVENT_NAME = "Automated Delete CancellationEvents";
	public static final String AUTO_DELETE_CANCELLATION_EVENT_HANDLER = "taskDeleteCancellationEventsOnAsnCancelAction";

	public static final String AUTO_DELETE_STOCKS_CODE = "ASN_Automated_DeleteStockLevels";
	public static final String AUTO_DELETE_STOCKS_NAME = "Automated Delete StockLevels";
	public static final String AUTO_DELETE_STOCKLEVEL_HANDLER = "taskDeleteStockLevelsOnAsnCancelAction";


	private WarehousingDao<AutomatedWorkflowActionTemplateModel> automatedWorkflowActionTemplateDao;
	private WorkflowDecisionTemplates workflowDecisionTemplates;
	private WorkflowTemplates workflowTemplates;
	private UserService userService;

	public AutomatedWorkflowActionTemplateModel AutoPacking()
	{
		return getOrSaveAndReturn(() -> getAutomatedWorkflowActionTemplateDao().getByCode(AUTO_PACK_CODE),
				() -> AutomatedWorkflowActionTemplateModelBuilder.aModel().withCode(AUTO_PACK_CODE).withName(AUTO_PACK_NAME)
						.withJobHandler(AUTO_PACK_HANDLER).withAction(WorkflowActionType.NORMAL)
						.withWorkflow(getWorkflowTemplates().ConsignmentTemplate()).withPrincipal(getUserService().getAdminUser())
						.withDecision(Arrays.asList(getWorkflowDecisionTemplates().AutoPackingShipping(),
								getWorkflowDecisionTemplates().AutoPackingPickup())).build());
	}

	public AutomatedWorkflowActionTemplateModel AutoShipping()
	{
		return getOrSaveAndReturn(() -> getAutomatedWorkflowActionTemplateDao().getByCode(AUTO_SHIP_CODE),
				() -> AutomatedWorkflowActionTemplateModelBuilder.aModel().withCode(AUTO_SHIP_CODE).withName(AUTO_SHIP_NAME)
						.withJobHandler(AUTO_SHIP_HANDLER).withAction(WorkflowActionType.NORMAL)
						.withWorkflow(getWorkflowTemplates().ConsignmentTemplate()).withPrincipal(getUserService().getAdminUser())
						.withDecision(Arrays.asList(getWorkflowDecisionTemplates().AutoShipping())).build());
	}

	public AutomatedWorkflowActionTemplateModel AutoPickup()
	{
		return getOrSaveAndReturn(() -> getAutomatedWorkflowActionTemplateDao().getByCode(AUTO_PICKUP_CODE),
				() -> AutomatedWorkflowActionTemplateModelBuilder.aModel().withCode(AUTO_PICKUP_CODE).withName(AUTO_PICKUP_NAME)
						.withJobHandler(AUTO_PICKUP_HANDLER).withAction(WorkflowActionType.NORMAL)
						.withWorkflow(getWorkflowTemplates().ConsignmentTemplate()).withPrincipal(getUserService().getAdminUser())
						.withDecision(Arrays.asList(getWorkflowDecisionTemplates().AutoPickup())).build());
	}

	public AutomatedWorkflowActionTemplateModel AutoReallocateConsignment()
	{
		return getOrSaveAndReturn(() -> getAutomatedWorkflowActionTemplateDao().getByCode(AUTO_REALLOCATE_CONS_CODE),
				() -> AutomatedWorkflowActionTemplateModelBuilder.aModel().withCode(AUTO_REALLOCATE_CONS_CODE).withName(AUTO_REALLOCATE_CONS_NAME)
						.withJobHandler(AUTO_REALLOCATE_HANDLER).withAction(WorkflowActionType.START)
						.withWorkflow(getWorkflowTemplates().AsnTemplate()).withPrincipal(getUserService().getAdminUser())
						.withDecision(Arrays.asList(getWorkflowDecisionTemplates().AutoReallocateConsignments())).build());
	}


	public AutomatedWorkflowActionTemplateModel AutoDeleteCancellationEvent()
	{
		return getOrSaveAndReturn(() -> getAutomatedWorkflowActionTemplateDao().getByCode(AUTO_DELETE_CANCELEVENT_CODE),
				() -> AutomatedWorkflowActionTemplateModelBuilder.aModel().withCode(AUTO_DELETE_CANCELEVENT_CODE).withName(AUTO_DELETE_CANCELEVENT_NAME)
						.withJobHandler(AUTO_DELETE_CANCELLATION_EVENT_HANDLER).withAction(WorkflowActionType.NORMAL)
						.withWorkflow(getWorkflowTemplates().AsnTemplate()).withPrincipal(getUserService().getAdminUser())
						.withDecision(Arrays.asList(getWorkflowDecisionTemplates().AutoDeleteCancellationEvents())).build());
	}

	public AutomatedWorkflowActionTemplateModel AutoDeleteStockLevel()
	{
		return getOrSaveAndReturn(() -> getAutomatedWorkflowActionTemplateDao().getByCode(AUTO_DELETE_STOCKS_CODE),
				() -> AutomatedWorkflowActionTemplateModelBuilder.aModel().withCode(AUTO_DELETE_STOCKS_CODE).withName(AUTO_DELETE_STOCKS_NAME)
						.withJobHandler(AUTO_DELETE_STOCKLEVEL_HANDLER).withAction(WorkflowActionType.NORMAL)
						.withWorkflow(getWorkflowTemplates().AsnTemplate()).withPrincipal(getUserService().getAdminUser())
						.withDecision(Arrays.asList(getWorkflowDecisionTemplates().AutoDeleteStockLevels())).build());
	}

	protected WarehousingDao<AutomatedWorkflowActionTemplateModel> getAutomatedWorkflowActionTemplateDao()
	{
		return automatedWorkflowActionTemplateDao;
	}

	@Required
	public void setAutomatedWorkflowActionTemplateDao(
			final WarehousingDao<AutomatedWorkflowActionTemplateModel> automatedWorkflowActionTemplateDao)
	{
		this.automatedWorkflowActionTemplateDao = automatedWorkflowActionTemplateDao;
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

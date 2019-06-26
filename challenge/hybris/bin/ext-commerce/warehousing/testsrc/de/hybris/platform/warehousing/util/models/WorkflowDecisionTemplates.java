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

import de.hybris.platform.warehousing.util.builder.WorkflowDecisionTemplateModelBuilder;
import de.hybris.platform.warehousing.util.dao.WarehousingDao;
import de.hybris.platform.workflow.model.WorkflowDecisionTemplateModel;

import org.springframework.beans.factory.annotation.Required;


public class WorkflowDecisionTemplates extends AbstractItems<WorkflowDecisionTemplateModel>
{
	//consignment wf
	public static final String PICKING_CODE = "NPR001";
	public static final String PICKING_NAME = "Picked";
	public static final String PACKING_CODE = "NPR002";
	public static final String PACKING_NAME = "Packed";
	public static final String AUTO_PACK_SHIP_CODE = "NPR003";
	public static final String AUTO_PACK_SHIP_NAME = "Redirect to shipping";
	public static final String AUTO_PACK_PICKUP_CODE = "NPR004";
	public static final String AUTO_PACK_PICKUP_NAME = "Redirect to pick up";
	public static final String SHIPPING_CODE = "NPR005";
	public static final String SHIPPING_NAME = "Shipped";
	public static final String AUTO_SHIP_CODE = "NPR006";
	public static final String AUTO_SHIP_NAME = "Automated Shipping";
	public static final String PICKUP_CODE = "NPR007";
	public static final String PICKUP_NAME = "Picked up";
	public static final String AUTO_PICKUP_CODE = "NPR008";
	public static final String AUTO_PICKUP_NAME = "Automated Pick up";
	//asn wf
	public static final String AUTO_REALLOCATE_CONS_CODE = "ASN001";
	public static final String AUTO_REALLOCATE_CONS_NAME = "Reallocate Consignmets After ASN cancel";
	public static final String AUTO_DELETE_CANCELLATION_EVENT_CODE = "ASN002";
	public static final String AUTO_DELETE_CANCELLATION_EVENT_NAME = "Delete CancellationEvents After Asn Cancel";
	public static final String AUTO_DELETE_STOCKS_CODE = "ASN003";
	public static final String AUTO_DELETE_STOCKS_NAME = "Delete StockLevels After ASN cancel";


	private WarehousingDao<WorkflowDecisionTemplateModel> workflowDecisionTemplateDao;

	public WorkflowDecisionTemplateModel Picking()
	{
		return getOrSaveAndReturn(() -> getWorkflowDecisionTemplateDao().getByCode(PICKING_CODE),
				() -> WorkflowDecisionTemplateModelBuilder.aModel().withCode(PICKING_CODE).withName(PICKING_NAME).build());
	}

	public WorkflowDecisionTemplateModel Packing()
	{
		return getOrSaveAndReturn(() -> getWorkflowDecisionTemplateDao().getByCode(PACKING_CODE),
				() -> WorkflowDecisionTemplateModelBuilder.aModel().withCode(PACKING_CODE).withName(PACKING_NAME).build());
	}

	public WorkflowDecisionTemplateModel AutoPackingShipping()
	{
		return getOrSaveAndReturn(() -> getWorkflowDecisionTemplateDao().getByCode(AUTO_PACK_SHIP_CODE),
				() -> WorkflowDecisionTemplateModelBuilder.aModel().withCode(AUTO_PACK_SHIP_CODE).withName(AUTO_PACK_SHIP_NAME).build());
	}

	public WorkflowDecisionTemplateModel AutoPackingPickup()
	{
		return getOrSaveAndReturn(() -> getWorkflowDecisionTemplateDao().getByCode(AUTO_PACK_PICKUP_CODE),
				() -> WorkflowDecisionTemplateModelBuilder.aModel().withCode(AUTO_PACK_PICKUP_CODE).withName(AUTO_PACK_PICKUP_NAME).build());
	}

	public WorkflowDecisionTemplateModel Shipping()
	{
		return getOrSaveAndReturn(() -> getWorkflowDecisionTemplateDao().getByCode(SHIPPING_CODE),
				() -> WorkflowDecisionTemplateModelBuilder.aModel().withCode(SHIPPING_CODE).withName(SHIPPING_NAME).build());
	}

	public WorkflowDecisionTemplateModel AutoShipping()
	{
		return getOrSaveAndReturn(() -> getWorkflowDecisionTemplateDao().getByCode(AUTO_SHIP_CODE),
				() -> WorkflowDecisionTemplateModelBuilder.aModel().withCode(AUTO_SHIP_CODE).withName(AUTO_SHIP_NAME).build());
	}

	public WorkflowDecisionTemplateModel Pickup()
	{
		return getOrSaveAndReturn(() -> getWorkflowDecisionTemplateDao().getByCode(PICKUP_CODE),
				() -> WorkflowDecisionTemplateModelBuilder.aModel().withCode(PICKUP_CODE).withName(PICKUP_NAME).build());
	}

	public WorkflowDecisionTemplateModel AutoPickup()
	{
		return getOrSaveAndReturn(() -> getWorkflowDecisionTemplateDao().getByCode(AUTO_PICKUP_CODE),
				() -> WorkflowDecisionTemplateModelBuilder.aModel().withCode(AUTO_PICKUP_CODE).withName(AUTO_PICKUP_NAME).build());
	}

	public WorkflowDecisionTemplateModel AutoReallocateConsignments()
	{
		return getOrSaveAndReturn(() -> getWorkflowDecisionTemplateDao().getByCode(AUTO_REALLOCATE_CONS_CODE),
				() -> WorkflowDecisionTemplateModelBuilder.aModel().withName(AUTO_REALLOCATE_CONS_CODE).withName(AUTO_REALLOCATE_CONS_NAME).build());
	}

	public WorkflowDecisionTemplateModel AutoDeleteCancellationEvents()
	{
		return getOrSaveAndReturn(() -> getWorkflowDecisionTemplateDao().getByCode(AUTO_DELETE_CANCELLATION_EVENT_CODE),
				() -> WorkflowDecisionTemplateModelBuilder.aModel().withName(AUTO_DELETE_CANCELLATION_EVENT_CODE).withName(AUTO_DELETE_CANCELLATION_EVENT_NAME).build());
	}
	public WorkflowDecisionTemplateModel AutoDeleteStockLevels()
	{
		return getOrSaveAndReturn(() -> getWorkflowDecisionTemplateDao().getByCode(AUTO_DELETE_STOCKS_CODE),
				() -> WorkflowDecisionTemplateModelBuilder.aModel().withName(AUTO_DELETE_STOCKS_CODE).withName(AUTO_DELETE_STOCKS_NAME).build());
	}


	protected WarehousingDao<WorkflowDecisionTemplateModel> getWorkflowDecisionTemplateDao()
	{
		return workflowDecisionTemplateDao;
	}

	@Required
	public void setWorkflowDecisionTemplateDao(final WarehousingDao<WorkflowDecisionTemplateModel> workflowDecisionTemplateDao)
	{
		this.workflowDecisionTemplateDao = workflowDecisionTemplateDao;
	}

}

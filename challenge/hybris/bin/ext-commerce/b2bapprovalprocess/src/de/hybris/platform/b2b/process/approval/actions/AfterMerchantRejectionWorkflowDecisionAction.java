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
package de.hybris.platform.b2b.process.approval.actions;

import de.hybris.platform.b2b.event.MerchantRejectedEvent;
import de.hybris.platform.b2b.process.approval.jalo.B2BApprovalProcess;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.jalo.security.Principal;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.workflow.jalo.WorkflowAction;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;


public class AfterMerchantRejectionWorkflowDecisionAction extends AfterRejectGenericWorkflowDecisionAction
{
	@Override
	public void performAction(final WorkflowAction action)
	{
		super.performAction(action);
		final Principal principalAssigned = action.getPrincipalAssigned();
		final B2BApprovalProcess process = (B2BApprovalProcess) CollectionUtils.find(action.getAttachmentItems(),
				PredicateUtils.instanceofPredicate(B2BApprovalProcess.class));
		final OrderModel order = ((B2BApprovalProcessModel) this.getModelService().toModelLayer(process)).getOrder();
		final EventService eventService = (EventService) Registry.getApplicationContext().getBean("eventService");
		eventService.publishEvent(new MerchantRejectedEvent(order, getModelService().<PrincipalModel> toModelLayer(
				principalAssigned)));

	}
}

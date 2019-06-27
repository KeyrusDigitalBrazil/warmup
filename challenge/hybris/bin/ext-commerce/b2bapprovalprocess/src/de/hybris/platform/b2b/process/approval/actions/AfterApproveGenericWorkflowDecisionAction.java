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

import de.hybris.platform.b2b.process.approval.jalo.B2BApprovalProcess;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.jalo.security.Principal;
import de.hybris.platform.workflow.jalo.WorkflowAction;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;


/**
 * This automated action depends on Jalo functionality and is currently required for workflows managed via HMC.
 *
 * @deprecated Since 4.4. Will be migrated to service layer in version 4.5.
 */
@Deprecated
public class AfterApproveGenericWorkflowDecisionAction extends AbstractWorkflowAutomatedAction
{
	private static final Logger LOG = Logger.getLogger(AfterApproveGenericWorkflowDecisionAction.class);

	@Override
	public void performAction(final WorkflowAction action)
	{
		OrderModel order = null;
		try
		{
			final Principal principalAssigned = action.getPrincipalAssigned();
			final B2BApprovalProcess process = (B2BApprovalProcess) CollectionUtils.find(action.getAttachmentItems(),
					PredicateUtils.instanceofPredicate(B2BApprovalProcess.class));
			Assert.notNull(process, String.format("Process attachment missing for action %s", action.getCode()));
			order = ((B2BApprovalProcessModel) this.getModelService().toModelLayer(process)).getOrder();
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Executing action %s for process %s on order %s assigned to %s", action.getCode(),
						process.getCode(), order.getCode(), principalAssigned.getUID()));
			}
			order.setStatus(OrderStatus.APPROVED);
			getModelService().save(order);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			if (order != null) // NOSONAR
			{
				order.setStatus(OrderStatus.B2B_PROCESSING_ERROR);
				getModelService().save(order);
			}
		}
	}
}

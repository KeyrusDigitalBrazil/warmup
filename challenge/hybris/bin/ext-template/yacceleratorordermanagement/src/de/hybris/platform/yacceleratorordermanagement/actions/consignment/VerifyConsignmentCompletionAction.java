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
package de.hybris.platform.yacceleratorordermanagement.actions.consignment;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.warehousing.taskassignment.services.WarehousingConsignmentWorkflowService;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Checks the state of the consignment to performStrategy changes (e.g., setting the consignment status) according to the
 * consignment's state changes resulted from the actions performed on it.
 */
public class VerifyConsignmentCompletionAction extends AbstractAction<ConsignmentProcessModel>
{
	private static Logger LOGGER = LoggerFactory.getLogger(VerifyConsignmentCompletionAction.class);

	private WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService;

	protected enum Transition
	{
		OK, WAIT;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<>();

			for (final Transition transition : Transition.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}

	@Override
	public String execute(final ConsignmentProcessModel consignmentProcessModel) throws Exception
	{
		LOGGER.info("Process: {} in step {}", consignmentProcessModel.getCode(), getClass().getSimpleName());
		final ConsignmentModel consignment = consignmentProcessModel.getConsignment();

		//Terminating the current workflow for task assignment
		getWarehousingConsignmentWorkflowService().terminateConsignmentWorkflow(consignment);

		return consignment.getConsignmentEntries().stream()
				.anyMatch(consignmentEntry -> consignmentEntry.getQuantityPending().longValue() > 0) ?
				waitTransition(consignmentProcessModel) :
				okTransition(consignmentProcessModel);
	}

	/**
	 * Updates the {@link ConsignmentModel#STATUS} to {@link ConsignmentStatus#READY}.
	 * <p>Also, it assigns a new taskAssignment {@link WorkflowModel} to the {@link ConsignmentModel} and puts the {@link ConsignmentProcessModel} in waiting state</p>
	 *
	 * @param consignmentProcess
	 * 		the given {@link ConsignmentProcessModel}
	 * @return the {@link Transition#WAIT}
	 */
	protected String waitTransition(final ConsignmentProcessModel consignmentProcess)
	{
		updateConsignmentStatus(consignmentProcess, ConsignmentStatus.READY);
		getWarehousingConsignmentWorkflowService().startConsignmentWorkflow(consignmentProcess.getConsignment());
		return Transition.WAIT.toString();
	}

	/**
	 * Updates the {@link ConsignmentModel#STATUS} to {@link ConsignmentStatus#CANCELLED}
	 *
	 * @param consignmentProcess
	 * 		the given {@link ConsignmentProcessModel}
	 * @return the {@link Transition#OK}
	 */
	protected String okTransition(final ConsignmentProcessModel consignmentProcess)
	{
		updateConsignmentStatus(consignmentProcess, ConsignmentStatus.CANCELLED);
		return Transition.OK.toString();
	}

	/**
	 * Updates the {@link ConsignmentModel#STATUS} to given {@link ConsignmentStatus}
	 *
	 * @param consignmentProcessModel
	 * 		the given {@link ConsignmentProcessModel}
	 * @param status
	 * 		The {@link ConsignmentStatus} to be set on the {@link ConsignmentModel}
	 */
	protected void updateConsignmentStatus(final ConsignmentProcessModel consignmentProcessModel, final ConsignmentStatus status)
	{
		final ConsignmentModel consignment = consignmentProcessModel.getConsignment();
		consignment.setStatus(status);
		getModelService().save(consignment);
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	protected WarehousingConsignmentWorkflowService getWarehousingConsignmentWorkflowService()
	{
		return warehousingConsignmentWorkflowService;
	}

	@Required
	public void setWarehousingConsignmentWorkflowService(
			final WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService)
	{
		this.warehousingConsignmentWorkflowService = warehousingConsignmentWorkflowService;
	}
}

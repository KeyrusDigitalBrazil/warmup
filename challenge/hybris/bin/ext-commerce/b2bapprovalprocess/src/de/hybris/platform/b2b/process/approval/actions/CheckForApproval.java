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

import de.hybris.platform.b2b.enums.PermissionStatus;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BCommentService;
import de.hybris.platform.b2b.services.impl.DefaultB2BPermissionService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.task.RetryLaterException;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Checks if the order requires an approver.
 */
public class CheckForApproval extends AbstractB2BApproveOrderDecisionAction
{

	private static final Logger LOG = Logger.getLogger(CheckForApproval.class);
	private DefaultB2BPermissionService b2bPermissionService;
	private B2BCommentService<AbstractOrderModel> b2bCommentService;


	@Override
	public Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException, Exception
	{
		OrderModel order = null;
		try
		{
			order = getOrderForProcess(process);
			final B2BCustomerModel orderUser = (B2BCustomerModel) order.getUser();
			final Set<B2BPermissionResultModel> permissionResults = b2bPermissionService.evaluatePermissions(order, orderUser, null);

			// set only the results that need approval.
			order.setPermissionResults(permissionResults);
			order.setStatus(OrderStatus.PENDING_APPROVAL);
			Transition transition = Transition.OK;

			// NOK transition points to findApprovers action.
			for (final B2BPermissionResultModel b2bPermissionResultModel : order.getPermissionResults())
			{
				if (PermissionStatus.OPEN.equals(b2bPermissionResultModel.getStatus()))
				{
					transition = Transition.NOK;
				}
				else if (PermissionStatus.ERROR.equals(b2bPermissionResultModel.getStatus()))
				{
					// this can happen if budget was not found or any error occurred, like missing cost center.
					throw new IllegalStateException("Failed to evaluate permission::" + b2bPermissionResultModel.getNote());
				}
				else
				{
					b2bPermissionResultModel.setStatus(PermissionStatus.APPROVED);
				}

				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("PermissionResult %s|%s|%s ", b2bPermissionResultModel.getPermissionTypeCode(),
							b2bPermissionResultModel.getStatus(), b2bPermissionResultModel.getApprover().getUid()));
				}
			}

			if (transition.equals(Transition.NOK))
			{
				//Do nothing here as we will approve it in checkapproval process
				this.modelService.save(order);
			}
			//Do we have to save it here

			return transition;
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			this.handleError(order, e);

			return Transition.ERROR;
		}
	}

	public void handleError(final OrderModel order, final Exception e)
	{
		if (order != null)
		{
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);

			final B2BCommentModel comment = this.modelService.create(B2BCommentModel.class);
			comment.setComment(e.getMessage());
			this.getB2bCommentService().addComment(order, comment);
		}
		LOG.error(e.getMessage(), e);
	}

	public DefaultB2BPermissionService getB2bPermissionService()
	{
		return b2bPermissionService;
	}

	@Required
	public void setB2bPermissionService(final DefaultB2BPermissionService b2bPermissionService)
	{
		this.b2bPermissionService = b2bPermissionService;
	}


	public B2BCommentService<AbstractOrderModel> getB2bCommentService()
	{
		return b2bCommentService;
	}

	@Required
	public void setB2bCommentService(final B2BCommentService<AbstractOrderModel> b2bCommentService)
	{
		this.b2bCommentService = b2bCommentService;
	}


}

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
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BPermissionService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.task.RetryLaterException;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Locates eligible approvers for a b2b order.
 */
public class FindApprovers extends AbstractSimpleB2BApproveOrderDecisionAction
{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(FindApprovers.class);

	/** The b2b permission service. */
	private DefaultB2BPermissionService b2bPermissionService;

	private B2BPermissionResultHelperImpl permissionResultHelper;

	/**
	 * @param approvalProcess
	 * @return transition
	 **/
	@Override
	public Transition executeAction(final B2BApprovalProcessModel approvalProcess) throws RetryLaterException
	{
		OrderModel order = null;
		try
		{
			order = approvalProcess.getOrder();

			final Collection<B2BPermissionResultModel> openPermissionsForOrder = getPermissionResultHelper()
					.filterResultByPermissionStatus(order.getPermissionResults(), PermissionStatus.OPEN);

			final Set<B2BPermissionResultModel> permissionResults = b2bPermissionService.getApproversForOpenPermissions(order,
					(B2BCustomerModel) order.getUser(), openPermissionsForOrder);

			if (CollectionUtils.isNotEmpty(permissionResults))
			{
				order.setPermissionResults(permissionResults);
				this.modelService.save(order);
				return Transition.OK;
			}
			return Transition.NOK;
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			this.handleError(order, e);
		}
		return Transition.NOK;
	}

	protected void handleError(final OrderModel order, final Exception e)
	{

		if (order != null)
		{
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);
		}
		LOG.error(e.getMessage(), e);
	}

	/**
	 * Gets the b2b permission service.
	 *
	 * @return the b2bPermissionService
	 */
	public DefaultB2BPermissionService getB2bPermissionService()
	{
		return b2bPermissionService;
	}

	/**
	 * Sets the b2b permission service.
	 *
	 * @param b2bPermissionService
	 *           the b2bPermissionService to set
	 */
	@Required
	public void setB2bPermissionService(final DefaultB2BPermissionService b2bPermissionService)
	{
		this.b2bPermissionService = b2bPermissionService;
	}

	/**
	 * @return the permissionResultHelper
	 */
	public B2BPermissionResultHelperImpl getPermissionResultHelper()
	{
		return permissionResultHelper;
	}

	/**
	 * @param permissionResultHelper
	 *           the permissionResultHelper to set
	 */
	@Required
	public void setPermissionResultHelper(final B2BPermissionResultHelperImpl permissionResultHelper)
	{
		this.permissionResultHelper = permissionResultHelper;
	}
}

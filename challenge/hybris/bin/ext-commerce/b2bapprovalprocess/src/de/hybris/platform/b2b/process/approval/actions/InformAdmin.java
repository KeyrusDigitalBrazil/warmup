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

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.enums.PermissionStatus;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.task.RetryLaterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Informs an member of b2badmingroup of a parent unit to which a customer who placed an order belongs of order
 * rejection.
 */
public class InformAdmin extends AbstractSimpleB2BApproveOrderDecisionAction
{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(InformAdmin.class);

	/** The b2b unit service. */
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Override
	public Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		final OrderModel order = process.getOrder();
		try
		{

			final B2BCustomerModel customer = (B2BCustomerModel) order.getUser();
			final B2BUnitModel parent = b2bUnitService.getParent(customer);
			final List<B2BCustomerModel> b2bAdminGroupUsers = new ArrayList(b2bUnitService.getUsersOfUserGroup(parent,
					B2BConstants.B2BADMINGROUP, true));
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Found [" + b2bAdminGroupUsers.size() + "] admins for unit: " + parent.getUid());
			}
			// remove the user who placed the order.
			CollectionUtils.filter(b2bAdminGroupUsers, PredicateUtils.notPredicate(PredicateUtils.equalPredicate(customer)));

			if (CollectionUtils.isNotEmpty(b2bAdminGroupUsers))
			{
				order.setStatus(OrderStatus.ASSIGNED_TO_ADMIN);
				final Collection<B2BPermissionResultModel> b2bPermissionResults = order.getPermissionResults();
				for (final B2BPermissionResultModel b2bPermissionResultModel : b2bPermissionResults)
				{
					if (b2bPermissionResultModel.getStatus().equals(PermissionStatus.OPEN))
					{
						final B2BCustomerModel admin = b2bAdminGroupUsers.get(0);
						if (LOG.isDebugEnabled())
						{
							LOG.debug(String.format("Assigning approver of permission result %s for order %s to admin %s",
									b2bPermissionResultModel.getPermissionTypeCode(), order.getCode(), admin.getUid()));
						}
						b2bPermissionResultModel.setApprover(admin);
						modelService.save(b2bPermissionResultModel);
					}
				}
			}
			else
			{
				order.setStatus(OrderStatus.B2B_PROCESSING_ERROR);
				final Collection<B2BPermissionResultModel> b2bPermissionResults = order.getPermissionResults();
				for (final B2BPermissionResultModel b2bPermissionResultModel : b2bPermissionResults)
				{
					if (b2bPermissionResultModel.getStatus().equals(PermissionStatus.OPEN))
					{
						b2bPermissionResultModel.setApprover(null);
						modelService.save(b2bPermissionResultModel);
					}
				}
			}
			modelService.save(order);
			return Transition.OK;
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			handleError(order, e);
			return Transition.NOK;
		}
	}

	protected void handleError(final OrderModel order, final Exception e)
	{

		if (order != null)
		{
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);
		}
	}

	/**
	 * Gets the b2b unit service.
	 * 
	 * @return the b2bUnitService
	 */
	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * Sets the b2b unit service.
	 * 
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}
}

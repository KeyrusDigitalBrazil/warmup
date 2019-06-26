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
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.impl.DefaultB2BPermissionService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.task.RetryLaterException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class Find2POEApprovers extends AbstractSimpleB2BApproveOrderDecisionAction
{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(FindApprovers.class);

	/** The b2b permission service. */
	private DefaultB2BPermissionService b2bPermissionService;
	private B2BApproverService<B2BCustomerModel> b2bApproverService;
	private B2BPermissionResultHelperImpl permissionResultHelper;
	private UserService userService;
	private Set<String> userGroupIds;

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
			final B2BCustomerModel employee = (B2BCustomerModel) order.getUser();


			final Collection permissionResults = getPermissionResults(order, openPermissionsForOrder, employee);

			if (CollectionUtils.isNotEmpty(permissionResults) && permissionResults.size() == getUserGroupIds().size())
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

	protected Collection getPermissionResults(final OrderModel order,
			final Collection<B2BPermissionResultModel> openPermissionsForOrder, final B2BCustomerModel employee)
	{
		final Collection permissionResults = new ArrayList<B2BPermissionResultModel>();

		final List<B2BCustomerModel> allApprovers = getB2bApproverService().getAllActiveApprovers(employee);

		// filter by group
		final List<B2BCustomerModel> approversOfGroup = (List<B2BCustomerModel>) CollectionUtils.select(allApprovers,
				new Predicate()
				{
					@Override
					public boolean evaluate(final Object approver)
					{
						final boolean isInB2bApproverGroup = getUserService().isMemberOfGroup(((B2BCustomerModel) approver),
								getUserService().getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP));
						return isInB2bApproverGroup;
					}
				});

		approversOfGroup.remove(order.getUser());

		for (final B2BPermissionResultModel b2bPermissionResultModel : openPermissionsForOrder)
		{
			for (final B2BCustomerModel b2bCustomerModel : approversOfGroup)
			{
				final Collection match = CollectionUtils.select(
						getAllPermissions(b2bCustomerModel),
						new BeanPropertyValueEqualsPredicate(B2BPermissionModel.ITEMTYPE, b2bPermissionResultModel
								.getPermissionTypeCode(), true));

				if (CollectionUtils.isNotEmpty(match))
				{
					b2bPermissionResultModel.setStatus(PermissionStatus.PENDING_APPROVAL);
					b2bPermissionResultModel.setApprover(b2bCustomerModel);
					modelService.save(b2bPermissionResultModel);
					permissionResults.add(b2bPermissionResultModel);
					break;
				}
			}
		}

		return permissionResults;
	}


	protected Set<B2BPermissionModel> getAllPermissions(final B2BCustomerModel b2bCustomerModel)
	{
		final Set<B2BPermissionModel> allPermissions = new HashSet<B2BPermissionModel>();
		final Collection<B2BUserGroupModel> c = CollectionUtils.select(b2bCustomerModel.getAllGroups(),
				PredicateUtils.instanceofPredicate(B2BUserGroupModel.class));

		for (final B2BUserGroupModel b2bUserGroupModel : c)
		{
			allPermissions.addAll(b2bUserGroupModel.getPermissions());
		}
		allPermissions.addAll(b2bCustomerModel.getPermissions());
		return allPermissions;
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
	protected DefaultB2BPermissionService getB2bPermissionService()
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
	protected B2BPermissionResultHelperImpl getPermissionResultHelper()
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

	/**
	 * @return the b2bApproverService
	 */
	protected B2BApproverService<B2BCustomerModel> getB2bApproverService()
	{
		return b2bApproverService;
	}

	/**
	 * @param b2bApproverService
	 *           the b2bApproverService to set
	 */
	public void setB2bApproverService(final B2BApproverService<B2BCustomerModel> b2bApproverService)
	{
		this.b2bApproverService = b2bApproverService;
	}

	/**
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the userGroupIds
	 */
	protected Set<String> getUserGroupIds()
	{
		return userGroupIds;
	}

	/**
	 * @param userGroupIds
	 *           the userGroupIds to set
	 */
	public void setUserGroupIds(final Set<String> userGroupIds)
	{
		this.userGroupIds = userGroupIds;
	}
}

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
package de.hybris.platform.b2b.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.enums.PermissionStatus;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.PermissionEvaluateStrategy;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class B2B2POEPermissionEvaluationStrategy extends AbstractPermissionEvaluationStrategy<B2BPermissionModel> implements
		PermissionEvaluateStrategy<B2BPermissionResultModel, AbstractOrderModel, B2BCustomerModel>
{
	private static final Logger LOG = Logger.getLogger(B2B2POEPermissionEvaluationStrategy.class);
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private UserService userService;

	private String permissionTypeCode;
	private Class<? extends B2BPermissionModel> permissionType;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.b2b.strategies.EvaluateStrategy#evaluate(java.lang.Object)
	 */
	@Override
	public B2BPermissionResultModel evaluate(final AbstractOrderModel order, final B2BCustomerModel employee)
	{
		validateParameterNotNullStandardMessage("order", order);
		validateParameterNotNullStandardMessage("employee", employee);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Evaluating " + getPermissionTypeCode() + "Model for employee: " + employee.getUid());
		}

		PermissionStatus status = PermissionStatus.OPEN;
		B2BPermissionModel permissionToEvaluate = null;
		B2BPermissionResultModel result = null;

		if (!order.getUser().equals(employee))
		{
			try
			{
				final Set<B2BPermissionModel> b2bPermissions = getTypesToEvaluate(employee, order);
				permissionToEvaluate = getPermissionToEvaluate(b2bPermissions, getPermissionType());

				if (LOG.isDebugEnabled())
				{
					for (final B2BPermissionModel b2bPermissionModel : b2bPermissions)
					{
						LOG.debug(String.format("%s has permission %s", employee.getUid(), b2bPermissionModel.getCode()));
					}
				}

				if (b2bPermissions != null && b2bPermissions.contains(permissionToEvaluate))
				{
					status = PermissionStatus.PENDING_APPROVAL;
					result = this.getModelService().create(B2BPermissionResultModel.class);
					result.setApprover(employee);
					result.setPermission(permissionToEvaluate);
					result.setPermissionTypeCode(getPermissionTypeCode());
					result.setStatus(status);
					if (LOG.isDebugEnabled())
					{
						LOG.debug(String.format("PermissionResult %s|%s|%s ", result.getPermissionTypeCode(), result.getStatus(),
								result.getApprover().getUid()));
					}
				}
			}
			catch (final Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
		}
		else
		{
			if (OrderStatus.CREATED.equals(order.getStatus()))
			{
				status = PermissionStatus.OPEN;
				result = this.getModelService().create(B2BPermissionResultModel.class);
				result.setApprover(employee);
				result.setPermission(permissionToEvaluate);
				result.setPermissionTypeCode(getPermissionTypeCode());
				result.setStatus(status);
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("PermissionResult %s|%s|%s ", result.getPermissionTypeCode(), result.getStatus(), result
							.getApprover().getUid()));
				}
			}
			else
			{
				LOG.debug(String.format("User %s is buyer of order %s. He is not allowed to approve the order himself!",
						employee.getName(), order.getCode()));
			}
		}
		return result;
	}

	public B2BPermissionModel getPermissionToEvaluate(final Set<B2BPermissionModel> permissions,
			final Class<? extends B2BPermissionModel> type)
	{
		return (B2BPermissionModel) CollectionUtils.find(permissions, PredicateUtils.instanceofPredicate(type));
	}

	@Override
	public Set<B2BPermissionModel> getTypesToEvaluate(final B2BCustomerModel user, final AbstractOrderModel order)
	{
		// get 2poe approvers groups
		final Set<PrincipalGroupModel> groups = user.getGroups();
		final Set<B2BPermissionModel> permissions = new HashSet<B2BPermissionModel>();

		if (groups != null)
		{
			// get all permissions of that group
			for (final Iterator approverGroupsIter = groups.iterator(); approverGroupsIter.hasNext();)
			{
				final Object group = approverGroupsIter.next();
				if (group instanceof B2BUserGroupModel)
				{
					permissions.addAll(((B2BUserGroupModel) group).getPermissions());
				}
			}
		}
		return permissions;
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	@Required
	public void setPermissionTypeCode(final String permissionTypeCode)
	{
		this.permissionTypeCode = permissionTypeCode;
	}

	public String getPermissionTypeCode()
	{
		return permissionTypeCode;
	}

	@Required
	public void setPermissionType(final Class<? extends B2BPermissionModel> permissionType)
	{
		this.permissionType = permissionType;
	}

	@Override
	public Class<? extends B2BPermissionModel> getPermissionType()
	{
		return permissionType;
	}
}

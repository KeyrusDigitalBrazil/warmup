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

import de.hybris.platform.b2b.dao.B2BOrderDao;
import de.hybris.platform.b2b.enums.PermissionStatus;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdTimespanPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.strategies.PermissionEvaluateStrategy;
import de.hybris.platform.b2b.util.B2BDateUtils;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.util.StandardDateRange;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * A strategy for evaluating {@link B2BOrderThresholdTimespanPermissionModel}
 */
public class DefaultB2BOrderThresholdTimespanEvaluationStrategy
		extends AbstractB2BOrderThresholdPermissionEvaluationStrategy<B2BOrderThresholdTimespanPermissionModel>
		implements PermissionEvaluateStrategy<B2BPermissionResultModel, AbstractOrderModel, B2BCustomerModel>
{

	private static final Logger LOG = Logger.getLogger(DefaultB2BOrderThresholdTimespanEvaluationStrategy.class);

	private B2BOrderDao b2bOrderDao;
	private B2BDateUtils b2bDateUtils;

	@Override
	public B2BPermissionResultModel evaluate(final AbstractOrderModel order, final B2BCustomerModel employee)
	{
		final Set<B2BOrderThresholdTimespanPermissionModel> b2bGroupPermissions = this.getTypesToEvaluate(employee, order);
		if (LOG.isDebugEnabled())
		{
			for (final B2BPermissionModel b2bPermissionModel : b2bGroupPermissions)
			{
				LOG.debug(String.format("%s has permission %s", employee.getUid(), b2bPermissionModel.getCode()));
			}
		}

		final B2BOrderThresholdTimespanPermissionModel permissionToEvaluate = getPermissionToEvaluate(b2bGroupPermissions, order);
		PermissionStatus status = PermissionStatus.OPEN;

		if (permissionToEvaluate != null && permissionToEvaluate.getThreshold() != null)
		{
			final BigDecimal threshold = toMoney(permissionToEvaluate.getThreshold());
			final StandardDateRange dateRange = getB2bDateUtils().createDateRange(permissionToEvaluate.getRange());
			final List<OrderModel> approvedOrdersForDateRange = getB2bOrderDao().findOrdersApprovedByDateRange(order.getUser(),
					dateRange.getStart(), dateRange.getEnd());
			final BigDecimal totalForOrders = toMoney(order.getTotalPrice()).add(getOrderTotals(approvedOrdersForDateRange));
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Order total is: " + totalForOrders + " for date range: [" + dateRange.getStart() + " - "
						+ dateRange.getEnd() + "] and permissing threshold is: " + threshold);
			}

			if (threshold.compareTo(toMoney(AbstractPermissionEvaluationStrategy.NOLIMIT)) != 0
					&& threshold.compareTo(totalForOrders) > 0)
			{
				status = PermissionStatus.PENDING_APPROVAL;
			}
		}

		final B2BPermissionResultModel result = this.getModelService().create(B2BPermissionResultModel.class);
		result.setPermission(permissionToEvaluate);
		result.setApprover(employee);
		result.setPermissionTypeCode(B2BOrderThresholdTimespanPermissionModel._TYPECODE);
		result.setStatus(status);
		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("PermissionResult %s|%s|%s ", result.getPermissionTypeCode(), result.getStatus(),
					result.getApprover().getUid()));
		}
		return result;
	}

	@Override
	public Class<? extends B2BPermissionModel> getPermissionType()
	{
		return B2BOrderThresholdTimespanPermissionModel.class;
	}

	@Required
	public void setB2bOrderDao(final B2BOrderDao b2bOrderDao)
	{
		this.b2bOrderDao = b2bOrderDao;
	}

	protected B2BOrderDao getB2bOrderDao()
	{
		return b2bOrderDao;
	}

	protected B2BDateUtils getB2bDateUtils()
	{
		return b2bDateUtils;
	}

	@Required
	public void setB2bDateUtils(final B2BDateUtils b2bDateUtils)
	{
		this.b2bDateUtils = b2bDateUtils;
	}
}

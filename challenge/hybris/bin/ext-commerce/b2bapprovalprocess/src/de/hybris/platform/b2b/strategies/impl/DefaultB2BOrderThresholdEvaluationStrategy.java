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

import de.hybris.platform.b2b.enums.PermissionStatus;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.strategies.PermissionEvaluateStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * A strategy for evaluating {@link B2BOrderThresholdPermissionModel}
 */
public class DefaultB2BOrderThresholdEvaluationStrategy
		extends AbstractB2BOrderThresholdPermissionEvaluationStrategy<B2BOrderThresholdPermissionModel>
		implements PermissionEvaluateStrategy<B2BPermissionResultModel, AbstractOrderModel, B2BCustomerModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BOrderThresholdEvaluationStrategy.class);


	@Override
	public B2BPermissionResultModel evaluate(final AbstractOrderModel order, final B2BCustomerModel employee)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Evaluating B2BOrderThresholdPermissionModel for employee: " + employee.getUid());
		}

		B2BOrderThresholdPermissionModel permissionToEvaluate = null;
		PermissionStatus status = PermissionStatus.OPEN;

		try
		{
			final Set<B2BOrderThresholdPermissionModel> b2bGroupPermissions = this.getTypesToEvaluate(employee, order);
			if (LOG.isDebugEnabled())
			{
				for (final B2BPermissionModel b2bPermissionModel : b2bGroupPermissions)
				{
					LOG.debug(String.format("%s has permission %s", employee.getUid(), b2bPermissionModel.getCode()));
				}
			}

			permissionToEvaluate = getPermissionToEvaluate(b2bGroupPermissions, order);

			if (permissionToEvaluate != null && permissionToEvaluate.getThreshold() != null)
			{
				final BigDecimal threshold = toMoney(permissionToEvaluate.getThreshold());
				if (threshold.compareTo(toMoney(AbstractPermissionEvaluationStrategy.NOLIMIT)) != 0
						&& threshold.compareTo(toMoney(order.getTotalPrice())) > 0)
				{
					status = PermissionStatus.PENDING_APPROVAL;
				}
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Order total is: " + order.getTotalPrice().doubleValue() + " and permissing threshold is: "
							+ permissionToEvaluate.getThreshold());
				}
			}
		}
		catch (final Exception e)
		{
			//FIXME: handle or throw up the exception
			LOG.error(e.getMessage(), e);
		}

		final B2BPermissionResultModel result = this.getModelService().create(B2BPermissionResultModel.class);
		result.setApprover(employee);
		result.setPermission(permissionToEvaluate);
		result.setPermissionTypeCode(B2BOrderThresholdPermissionModel._TYPECODE);
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
		return B2BOrderThresholdPermissionModel.class;
	}

}

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
import de.hybris.platform.b2b.model.B2BBudgetExceededPermissionModel;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.services.B2BBudgetService;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.strategies.PermissionEvaluateStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.util.StandardDateRange;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * A strategy for evaluating {@link B2BBudgetExceededPermissionModel}
 */
public class DefaultB2BBudgetExceededEvaluationStrategy extends AbstractPermissionEvaluationStrategy<B2BPermissionModel>
		implements PermissionEvaluateStrategy<B2BPermissionResultModel, AbstractOrderModel, B2BCustomerModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BBudgetExceededEvaluationStrategy.class);
	private B2BCostCenterService b2bCostCenterService;
	private B2BBudgetService<B2BBudgetModel, B2BCustomerModel> b2BBudgetService;

	/**
	 * 1) Check if order would exceed budget of cost center 1.1) If it doesn't, fine, set permissionresult for this type
	 * to approved and exit 1.2) If it does, continue with 2 2) Check if user has BudgetExceeded permission 2.1) If he
	 * doesn't, continue with 3 2.2) If he does, fine, set permission result for this type to approved and exit 3) Set
	 * PermissionResult to Open and later processing has to find approver
	 */
	@Override
	public B2BPermissionResultModel evaluate(final AbstractOrderModel order, final B2BCustomerModel employee)
	{
		PermissionStatus status;
		B2BBudgetExceededPermissionModel permissionToEvaluate = null;
		String message = null;
		try
		{
			permissionToEvaluate = getPermissionToEvaluate(this.getTypesToEvaluate(employee, order), this.getPermissionType());
			//As long as the order user/approver has BudgetExceedePermission
			status = PermissionStatus.PENDING_APPROVAL;
			//If the Order user/approver doesn't have the BudgetExceeded Permission
			if (permissionToEvaluate == null)
			{
				//Check if the Order user and the current user is same for the very first time
				if (order.getUser().getUid().equals(employee.getUid()))
				{
					//If the order user doesn't have BudgetExceededPermission, check if the Budget for the cost center has exceeded the order cost
					if (checkBudgetExceeded(order))
					{
						status = PermissionStatus.OPEN;
					}
				}
				else
				{ //If the current user is an approver and doesn't have the BudgetExceededPermission , then set it to OPEN to find the next Approver who has BudgetExceededPermission
					status = PermissionStatus.OPEN;
				}
			}

		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			status = PermissionStatus.ERROR;
			message = e.getMessage();
		}

		final B2BPermissionResultModel result = this.getModelService().create(B2BPermissionResultModel.class);
		result.setApprover(employee);
		result.setPermission(permissionToEvaluate);
		result.setPermissionTypeCode(B2BBudgetExceededPermissionModel._TYPECODE);
		result.setStatus(status);
		result.setNote(message);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("PermissionResult %s|%s|%s ", result.getPermissionTypeCode(), result.getStatus(), result
					.getApprover().getUid()));
		}
		return result;
	}

	/**
	 * Check if the order has exceeded the active budget.
	 * 
	 * @param order
	 *           A b2b order
	 * @return true, if successful
	 */
	protected boolean checkBudgetExceeded(final AbstractOrderModel order)
	{
		// the employee has the permission assigned so check the budget.
		final Set<B2BCostCenterModel> b2bCostCenters = getB2bCostCenterService().getB2BCostCenters(order.getEntries());
		for (final B2BCostCenterModel b2bCostCenterModel : b2bCostCenters)
		{
			final Collection<B2BBudgetModel> currentBudgets = getB2BBudgetService().getCurrentBudgets(b2bCostCenterModel);
			if (currentBudgets.isEmpty())
			{
				throw new IllegalStateException(String.format("cost center '%s' does not have budget assigned that is current",
						b2bCostCenterModel.getCode()));
			}
			BigDecimal totalBudget = BigDecimal.ZERO;
			for (final B2BBudgetModel b2bBudgetModel : currentBudgets)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Active Budget code: %s amount: %s for cost center %s", b2bBudgetModel.getCode(),
							b2bBudgetModel.getBudget(), b2bCostCenterModel.getCode()));
				}
				totalBudget = totalBudget.add(b2bBudgetModel.getBudget());
			}

			final StandardDateRange budgetDateRange = currentBudgets.iterator().next().getDateRange();
			final double totalCost = getB2bCostCenterService().getTotalCost(b2bCostCenterModel, budgetDateRange.getStart(),
					budgetDateRange.getEnd()).doubleValue()
					+ getTotalOfEntriesWithCostCenter(b2bCostCenterModel, order.getEntries()).doubleValue();

			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Current order %s has total cost %s against budget %s valid for %s and cost center %s",
						order.getCode(), Double.valueOf(totalCost), totalBudget,
						budgetDateRange.getStart() + " - " + budgetDateRange.getEnd(), b2bCostCenterModel.getCode()));
			}

			if (totalCost > totalBudget.doubleValue())
			{
				return true;
			}
		}
		return false;
	}

	public B2BBudgetExceededPermissionModel getPermissionToEvaluate(final Set<B2BPermissionModel> permissions,
			final Class<? extends B2BPermissionModel> type)
	{
		return (B2BBudgetExceededPermissionModel) CollectionUtils.find(permissions, PredicateUtils.instanceofPredicate(type));
	}

	@Override
	public Class<? extends B2BPermissionModel> getPermissionType()
	{
		return B2BBudgetExceededPermissionModel.class;
	}

	@Required
	public void setB2bCostCenterService(final B2BCostCenterService b2bCostCenterService)
	{
		this.b2bCostCenterService = b2bCostCenterService;
	}

	@Required
	public void setB2BBudgetService(final B2BBudgetService<B2BBudgetModel, B2BCustomerModel> b2BBudgetService)
	{
		this.b2BBudgetService = b2BBudgetService;
	}

	protected B2BCostCenterService getB2bCostCenterService()
	{
		return b2bCostCenterService;
	}

	protected B2BBudgetService<B2BBudgetModel, B2BCustomerModel> getB2BBudgetService()
	{
		return b2BBudgetService;
	}
}

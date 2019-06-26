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

import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;


/**
 * Abstract evaluation strategy which provides methods helpful to all permission evaluations strategies.
 */

public abstract class AbstractPermissionEvaluationStrategy<P extends B2BPermissionModel> extends AbstractEvaluationStrategy<P>
{
	public static final Double NOLIMIT = Double.valueOf(-1D);
	private final MathContext MONEY_HALF_UP = new MathContext(16, RoundingMode.HALF_UP);
	private final BigDecimal ZERO = (new BigDecimal("0", MONEY_HALF_UP)).setScale(2);

	/**
	 * Builds a list of permissions associated to a {@link B2BCustomerModel} by collections associated permissions form
	 * {@link de.hybris.platform.b2b.model.B2BCustomerModel#getPermissionGroups()} and m
	 * {@link de.hybris.platform.b2b.model.B2BCustomerModel#getPermissions()}
	 *
	 * @param customer
	 *           A {@link B2BCustomerModel} who placed a b2b order
	 * @return A Collection of permissions.
	 * @deprecated Since 4.4. Use {@link #getTypesToEvaluate}
	 */
	@Deprecated
	public Set<P> getPermissions(final B2BCustomerModel customer)
	{
		final Collection<B2BUserGroupModel> permissionGroups = CollectionUtils.select(customer.getPermissionGroups(),
				PredicateUtils.instanceofPredicate(B2BUserGroupModel.class));

		final Set<P> b2bGroupPermissions = new HashSet<P>();
		b2bGroupPermissions.addAll((Collection<? extends P>) customer.getPermissions());
		for (final B2BUserGroupModel b2bUserGroupModel : permissionGroups)
		{
			b2bGroupPermissions.addAll((Collection<? extends P>) b2bUserGroupModel.getPermissions());
		}
		return b2bGroupPermissions;
	}


	@Override
	public Set<P> getTypesToEvaluate(final B2BCustomerModel user, final AbstractOrderModel order)
	{

		final Collection<B2BUserGroupModel> permissionGroups = CollectionUtils.select(user.getPermissionGroups(),
				PredicateUtils.instanceofPredicate(B2BUserGroupModel.class));
		final Collection<B2BUserGroupModel> permissionGroupsOfCustomer = CollectionUtils.select(user.getGroups(),
				PredicateUtils.instanceofPredicate(B2BUserGroupModel.class));

		final Collection<B2BUserGroupModel> permissionGroupsUnion = CollectionUtils.union(permissionGroups,
				permissionGroupsOfCustomer);

		final Set<P> b2bGroupPermissions = new HashSet<P>((Collection<? extends P>) user.getPermissions());
		for (final B2BUserGroupModel b2bUserGroupModel : permissionGroupsUnion)
		{
			if (CollectionUtils.isNotEmpty(b2bUserGroupModel.getPermissions()))
			{
				b2bGroupPermissions.addAll((Collection<? extends P>) b2bUserGroupModel.getPermissions());
			}
		}
		return getActivePermissions(b2bGroupPermissions);
	}

	protected Set<P> getActivePermissions(final Set<P> permissionSet)
	{
		if (permissionSet == null)
		{
			return null;
		}

		final Set<P> activePermissionSet = new HashSet<P>();
		for (final P permission : permissionSet)
		{
			if (permission.getActive() != null && permission.getActive().booleanValue())
			{
				activePermissionSet.add(permission);
			}
		}
		return activePermissionSet;
	}

	public abstract Class<? extends B2BPermissionModel> getPermissionType();

	protected BigDecimal getOrderTotal(final AbstractOrderModel order)
	{
		return this.toMoney(new Double(order.getTotalPrice().doubleValue()
				+ (order.getNet().booleanValue() ? order.getTotalTax().doubleValue() : 0)));
	}


	protected BigDecimal getOrderEntryTotal(final AbstractOrderEntryModel entry)
	{
		return this.toMoney(new Double(entry.getTotalPrice().doubleValue()
				+ (entry.getOrder().getNet().booleanValue() ? getTotalTax(entry).doubleValue() : 0)));
	}


	protected BigDecimal getOrderTotals(final List<OrderModel> orders)
	{
		BigDecimal orderTotal = this.ZERO;
		for (final OrderModel abstractOrderModel : orders)
		{
			orderTotal = orderTotal.add(getOrderTotal(abstractOrderModel));
		}
		return orderTotal;
	}

	protected BigDecimal getTotalTax(final AbstractOrderEntryModel orderEntry)
	{
		BigDecimal totalTax = this.ZERO;
		for (final TaxValue taxValue : orderEntry.getTaxValues())
		{
			totalTax = totalTax.add(BigDecimal.valueOf(taxValue.getAppliedValue()), this.MONEY_HALF_UP);
		}
		return totalTax;
	}

	protected BigDecimal getTotalOfEntriesWithCostCenter(final B2BCostCenterModel costCenter,
			final List<AbstractOrderEntryModel> entries)
	{
		BigDecimal total = this.ZERO;
		for (final AbstractOrderEntryModel abstractOrderEntryModel : entries)
		{
			if (abstractOrderEntryModel.getCostCenter().equals(costCenter))
			{
				total = total.add(getOrderEntryTotal(abstractOrderEntryModel));
			}
		}
		return total;
	}

	protected BigDecimal toMoney(final Double amt)
	{
		return amt == null ? ZERO : BigDecimal.valueOf(amt.doubleValue()).round(MONEY_HALF_UP).setScale(2, RoundingMode.HALF_UP);
	}

}

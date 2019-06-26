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

import de.hybris.platform.b2b.model.B2BOrderThresholdPermissionModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;


/**
 * Abstract evaluation strategy which provides methods helpful to all permission evaluations strategies.
 */
public abstract class AbstractB2BOrderThresholdPermissionEvaluationStrategy<P extends B2BOrderThresholdPermissionModel>
		extends AbstractPermissionEvaluationStrategy<P>
{
	/**
	 * Gets the permission to evaluate.
	 *
	 * @param permissions
	 *           the permissions
	 * @param order
	 *           the order
	 * @return the permission to evaluate
	 */
	public P getPermissionToEvaluate(final Set<P> permissions, final AbstractOrderModel order)
	{
		final Collection<P> allPermissionsByType = CollectionUtils.select(permissions,
				new BeanPropertyValueEqualsPredicate("class", getPermissionType()));

		// filter permissions based on order's Currency
		final List<P> permissionsForOrderCurrencyWithinRange = new ArrayList<>(CollectionUtils.select(allPermissionsByType,
				new BeanPropertyValueEqualsPredicate(OrderModel.CURRENCY, order.getCurrency())));

		if (CollectionUtils.isNotEmpty(permissionsForOrderCurrencyWithinRange))
		{
			// return the permission with the highest threshold
			Collections.sort(permissionsForOrderCurrencyWithinRange, new ThresholdComparator());
			return permissionsForOrderCurrencyWithinRange.get(permissionsForOrderCurrencyWithinRange.size() - 1);
		}

		return null;
	}

	/**
	 * Use this comparator to sort by permission threshold.
	 */
	protected static class ThresholdComparator implements Comparator<B2BOrderThresholdPermissionModel>, java.io.Serializable
	{
		@Override
		public int compare(final B2BOrderThresholdPermissionModel permission1, final B2BOrderThresholdPermissionModel permission2)
		{
			if (permission1 == null || permission2 == null)
			{
				throw new IllegalStateException("Permissions to compare may not be null");
			}
			if (permission1.getThreshold() == null || permission2.getThreshold() == null)
			{
				throw new IllegalStateException("Permission thresholds must not be null; given " + permission1.getCode()
						+ "'s Threshold is " + permission1.getThreshold() + "; " + permission2.getCode() + "'s Threshold is "
						+ permission2.getThreshold());
			}

			return Double.compare(permission1.getThreshold().doubleValue(), permission2.getThreshold().doubleValue());
		}
	}
}

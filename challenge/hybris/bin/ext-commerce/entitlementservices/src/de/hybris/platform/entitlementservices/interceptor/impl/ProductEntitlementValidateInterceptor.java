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
package de.hybris.platform.entitlementservices.interceptor.impl;

import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.util.localization.Localization;


/**
 * Interceptor to validate that
 * <ul>
 * <li>a {@link de.hybris.platform.entitlementservices.model.ProductEntitlementModel} is assigned
 * to a {@link de.hybris.platform.core.model.product.ProductModel} only once
 * <li>the {@link de.hybris.platform.entitlementservices.model.ProductEntitlementModel}'s parent objects are marked as modified
 * </ul>
 */
public class ProductEntitlementValidateInterceptor extends AbstractParentChildValidateInterceptor
{
	private static final String PATH_CONDITION_SEPARATOR = "/";
	private final Pattern geoPathPattern = Pattern.compile("[^/,]+(/[^/,]+){0,2}");

	@Override
	public void doValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		/*
		It would be nice to have more strict validation, that considers usage unit and time unit,
		but currently it breaks catalog synchronization. The reason is order of synchronized objects
		is not determined, so ProductEntitlement without a time unit could mean time unit has not been assigned,
		of time unit record is synchronized after the entitlement.
		 */
		if (model instanceof ProductEntitlementModel)
		{
			final ProductEntitlementModel entitlement = (ProductEntitlementModel) model;
			validateGeo(entitlement);
			validateMetered(entitlement);
			validatePath(entitlement);
			validateTime(entitlement);
		}
	}

	private void validateTime(final ProductEntitlementModel entitlement) throws InterceptorException
	{
		if (entitlement.getTimeUnitStart() != null && entitlement.getTimeUnitStart() < 1)
		{
			final String message = Localization.getLocalizedString(
					"entitlementservices.customvalidation.entitlements.starttime.toosmall");
			throw new InterceptorException(message);
		}
		if (entitlement.getTimeUnitDuration() != null)
		{
			if (entitlement.getTimeUnitDuration() < 0)
			{
				final String message = Localization.getLocalizedString(
						"entitlementservices.customvalidation.entitlements.duration.invalid");
				throw new InterceptorException(message);
			}
			if (entitlement.getTimeUnitStart() == null)
			{
				final String message = Localization.getLocalizedString(
						"entitlementservices.customvalidation.entitlements.starttime.required");
				throw new InterceptorException(message);
			}
		}
	}

	private void validateGeo(final ProductEntitlementModel entitlement) throws InterceptorException
	{
		final Collection<String> geoCondition = entitlement.getConditionGeo();
		if (CollectionUtils.isNotEmpty(geoCondition))
		{
			int index = 1;
			for (final String item : geoCondition)
			{
				if (!geoPathPattern.matcher(item).matches())
				{
					final Object[] args =
							{index};
					final String message = Localization.getLocalizedString(
							"entitlementservices.customvalidation.entitlements.geo.invalid", args);
					throw new InterceptorException(message);
				}
				index++;
			}
		}
	}

	private void validateMetered(final ProductEntitlementModel entitlement) throws InterceptorException
	{
		final Integer quantity = entitlement.getQuantity();

		if (quantity != null && quantity < -1)
		{
			final Object[] quantities = {quantity};
			final String message = Localization.getLocalizedString("entitlementservices.customvalidation.entitlements.negative",
					quantities);
			throw new InterceptorException(message);
		}
	}

	private void validatePath(final ProductEntitlementModel entitlement) throws InterceptorException
	{
		if (entitlement.getConditionPath() != null && entitlement.getConditionPath().endsWith(PATH_CONDITION_SEPARATOR))
		{
			final String message = Localization.getLocalizedString(
						"entitlementservices.customvalidation.entitlements.path.separator");
			throw new InterceptorException(message);
		}
	}
}

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
package de.hybris.platform.configurablebundleservices.order.hook;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.i18n.L10NService;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * A hook that limits amount of particular products in single bundle.
 */
public class BundleSelectionCriteriaAddToCartMethodHook implements CommerceAddToCartMethodHook
{
	private L10NService l10NService;
	private BundleTemplateService bundleTemplateService;

	@Override
	public void beforeAddToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		if (parameter.getBundleTemplate() == null)
		{
			return;
		}

		EntryGroup bundleEntryGroup = null;
		try
		{
			bundleEntryGroup = getBundleTemplateService().getBundleEntryGroup(parameter.getCart(), parameter.getEntryGroupNumbers());
		}
		catch (final IllegalArgumentException e)
		{
			throw new CommerceCartModificationException(e.getMessage(), e);
		}
		if (bundleEntryGroup == null)
		{
			return;
		}

		validateParameterNotNullStandardMessage("parameter.cart", parameter.getCart());

		final int maxItemsAllowed;
		final BundleSelectionCriteriaModel selectionCriteria = parameter.getBundleTemplate().getBundleSelectionCriteria();

		if (selectionCriteria instanceof PickNToMBundleSelectionCriteriaModel)
		{
			maxItemsAllowed = ((PickNToMBundleSelectionCriteriaModel) selectionCriteria).getM();
		}
		else if (selectionCriteria instanceof PickExactlyNBundleSelectionCriteriaModel)
		{
			maxItemsAllowed = ((PickExactlyNBundleSelectionCriteriaModel) selectionCriteria).getN();
		}
		else
		{
			return;
		}

		final long total = getTotalQuantityInBundle(parameter.getCart(), bundleEntryGroup.getGroupNumber());
		if (total >= maxItemsAllowed)
		{
			final String message = getL10NService().getLocalizedString("bundleservices.validation.selectioncriteriaexceeded",
					new Object[] {
							getBundleTemplateService().getBundleTemplateName(parameter.getBundleTemplate()),
							String.valueOf(maxItemsAllowed), String.valueOf(total)
					}
			);
			throw new CommerceCartModificationException(message);
		}
	}

	protected long getTotalQuantityInBundle(final CartModel cart, final Integer entryGroupNumber)
	{
		return cart.getEntries().stream()
				.filter(e -> e.getEntryGroupNumbers() != null)
				.filter(e -> e.getEntryGroupNumbers().contains(entryGroupNumber))
				.map(AbstractOrderEntryModel::getQuantity)
				.filter(Objects::nonNull)
				.mapToInt(Long::intValue)
				.sum();
	}

	@Override
	public void afterAddToCart(final CommerceCartParameter parameters, final CommerceCartModification result)
			throws CommerceCartModificationException
	{
		// no actions needed
	}

	protected L10NService getL10NService()
	{
		return l10NService;
	}

	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}

	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	@Required
	public void setBundleTemplateService(BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}
}

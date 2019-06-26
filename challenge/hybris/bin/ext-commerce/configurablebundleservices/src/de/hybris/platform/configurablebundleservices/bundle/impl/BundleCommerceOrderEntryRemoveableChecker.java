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

package de.hybris.platform.configurablebundleservices.bundle.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.configurablebundleservices.bundle.RemoveableChecker;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;

import java.util.List;
import java.util.stream.Collectors;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;


/**
 * Default Implementation for {@link RemoveableChecker}
 */
public class BundleCommerceOrderEntryRemoveableChecker implements RemoveableChecker<CartEntryModel>
{
	private BundleTemplateService bundleTemplateService;

	@Override
	public boolean canRemove(@Nonnull final CartEntryModel cartEntry)
	{
		validateParameterNotNullStandardMessage("cartEntry", cartEntry);

		final EntryGroup entryGroup = getBundleTemplateService().getBundleEntryGroup(cartEntry);

		if (entryGroup != null)
		{
			final BundleTemplateModel bundleTemplate = getBundleTemplateService().getBundleTemplateForCode(
					entryGroup.getExternalReferenceId());
			final BundleSelectionCriteriaModel selectionCriteria = bundleTemplate.getBundleSelectionCriteria();

			// handles PickExactlyN and PickNToM
			if (selectionCriteria instanceof PickExactlyNBundleSelectionCriteriaModel)
			{
				int minItemsRequired = ((PickExactlyNBundleSelectionCriteriaModel) selectionCriteria).getN();
				final List<AbstractOrderEntryModel> bundleEntries = findEntriesByGroupNumber(cartEntry.getOrder(),
						entryGroup.getGroupNumber());

				if (bundleEntries.size() <= minItemsRequired)
				{
					return false;
				}
			}
			else
			{
				return selectionCriteria == null;
			}
		}

		return true;
	}

	protected List<AbstractOrderEntryModel> findEntriesByGroupNumber(@Nonnull final CartModel order,
			@Nonnull final Integer groupNumber)
	{
		validateParameterNotNullStandardMessage("order.entries", order.getEntries());
		validateParameterNotNullStandardMessage("groupNumber", groupNumber);

		return order.getEntries().stream()
				.filter(entry -> entry.getEntryGroupNumbers().contains(groupNumber))
				.collect(Collectors.toList());
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

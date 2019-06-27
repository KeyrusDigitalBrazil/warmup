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

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


/**
 * Extended implementation of {@link AbstractBundleComponentEditableChecker}, which supports autopick components.
 */
public abstract class AutoPickBundleComponentEditableChecker<O extends AbstractOrderModel, E extends AbstractOrderEntryModel>
	extends DefaultAbstractBundleComponentEditableChecker<O, E>
{

	@Override
	public boolean canEdit(@Nonnull final O masterAbstractOrder, @Nullable final BundleTemplateModel bundleTemplate,
			final int bundleNo)
	{
		final boolean isAutoPick = isAutoPickComponent(bundleTemplate);

		if (isAutoPick)
		{
			return false;
		}
		else
		{
			return super.canEdit(masterAbstractOrder, bundleTemplate, bundleNo);
		}
	}

	@Override
	public boolean isAutoPickComponent(final BundleTemplateModel bundleTemplate)
	{
		return (bundleTemplate != null && bundleTemplate.getBundleSelectionCriteria() instanceof AutoPickBundleSelectionCriteriaModel);
	}

	@Override
	// NOSONAR
	public void checkIsComponentSelectionCriteriaMet(@Nonnull final O masterAbstractOrder, final BundleTemplateModel bundleTemplate,
			final int bundleNo) throws CommerceCartModificationException
	{
		if (bundleTemplate != null && bundleNo != ConfigurableBundleServicesConstants.NO_BUNDLE)
		{
			checkComponentIsLeaf(bundleTemplate);

			// check that the existing component's selection criteria are fulfilled
			final BundleSelectionCriteriaModel selCrit = bundleTemplate.getBundleSelectionCriteria();
			int minRequiredItems = 0;
			int maxAllowedItems = 0;

			if (selCrit == null)
			{
				return;
			}
			else if (selCrit instanceof AutoPickBundleSelectionCriteriaModel)
			{
				minRequiredItems = bundleTemplate.getProducts() == null ? 0 : bundleTemplate.getProducts().size();
				maxAllowedItems = minRequiredItems;
			}
			else if (selCrit instanceof PickNToMBundleSelectionCriteriaModel)
			{
				minRequiredItems = ((PickNToMBundleSelectionCriteriaModel) selCrit).getN().intValue();
				maxAllowedItems = ((PickNToMBundleSelectionCriteriaModel) selCrit).getM().intValue();
			}
			else if (selCrit instanceof PickExactlyNBundleSelectionCriteriaModel)
			{
				minRequiredItems = ((PickExactlyNBundleSelectionCriteriaModel) selCrit).getN().intValue();
				maxAllowedItems = minRequiredItems;
			}

			final List<E> entries = getOrderEntryDao().findEntriesByMasterCartAndBundleNoAndTemplate(masterAbstractOrder, bundleNo,
					bundleTemplate);

			if (entries.size() >= minRequiredItems && entries.size() <= maxAllowedItems)
			{
				return;
			}
			else
			{
				throw new CommerceCartModificationException("Selection criteria of component '" + bundleTemplate.getId()
						+ "' is not fulfilled (minQ='" + minRequiredItems + "', maxQ='" + maxAllowedItems + "', cartQ='"
						+ entries.size() + "'); masterOrder='" + masterAbstractOrder.getCode() + "'; bundleNo='" + bundleNo + "'");
			}
		}
	}
}

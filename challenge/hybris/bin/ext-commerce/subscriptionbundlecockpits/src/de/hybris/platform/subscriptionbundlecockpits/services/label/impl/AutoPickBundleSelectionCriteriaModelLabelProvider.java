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

package de.hybris.platform.subscriptionbundlecockpits.services.label.impl;

import de.hybris.platform.configurablebundlecockpits.services.label.impl.BundleSelectionCriteriaModelLabelProvider;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;

/**
 * Label provider implementation for {@link BundleSelectionCriteriaModel} and sub-types
 */
public class AutoPickBundleSelectionCriteriaModelLabelProvider extends BundleSelectionCriteriaModelLabelProvider
{
	@Override
	protected String getItemLabel(final BundleSelectionCriteriaModel selectionCriteria)
	{
		String label = "";
		if (selectionCriteria instanceof AutoPickBundleSelectionCriteriaModel)
		{
			label = getL10NService().getLocalizedString("cockpit.bundleselection.automaticpicked");
		}
		else
		{
			super.getItemLabel(selectionCriteria);
		}

		return label;
	}
}

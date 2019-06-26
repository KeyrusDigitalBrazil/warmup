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

package de.hybris.platform.configurablebundlefacades.converters.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 * Populator implementation for {@link BundleTemplateModel} as source and {@link BundleTemplateData} as target type.
 */
public class BundleTemplatePopulator<SOURCE extends BundleTemplateModel, TARGET extends BundleTemplateData> implements
		Populator<SOURCE, TARGET>
{
	private BundleTemplateService bundleTemplateService;

	@Override
	public void populate(final SOURCE source, final TARGET target)
	{
		validateParameterNotNullStandardMessage("target", target);
		validateParameterNotNullStandardMessage("source", source);

		target.setId(source.getId());
		target.setName(getFullPath(source));
		target.setVersion(source.getVersion());

		final BundleTemplateModel rootTemplate = bundleTemplateService.getRootBundleTemplate(source);
		target.setRootBundleTemplateName(rootTemplate.getName());

		if (CollectionUtils.isNotEmpty(source.getProducts()))
		{
			target.setType(source.getProducts().iterator().next().getClass().getSimpleName());
		}

		// maximum product selection for the given BundleTemplate.
		target.setMaxItemsAllowed(getMaxNoOfProductsAllowed(source));
	}

	/**
	 * Helper method to find maximum possible product selections for the given BundleTemplate based on Selection
	 * criteria.
	 *
	 * @param bundleTemplate
	 * @return Number of products allowed to the BundleTemplate
	 */
	protected int getMaxNoOfProductsAllowed(final BundleTemplateModel bundleTemplate)
	{
		int maxItemsAllowed = 0;

		final BundleSelectionCriteriaModel selectionCriteria = bundleTemplate.getBundleSelectionCriteria();

		if (selectionCriteria instanceof PickNToMBundleSelectionCriteriaModel)
		{
			maxItemsAllowed = ((PickNToMBundleSelectionCriteriaModel) selectionCriteria).getM();
		}
		else if (selectionCriteria instanceof PickExactlyNBundleSelectionCriteriaModel)
		{
			maxItemsAllowed = ((PickExactlyNBundleSelectionCriteriaModel) selectionCriteria).getN();
		}

		return maxItemsAllowed;
	}

	protected String getFullPath(final SOURCE component)
	{
		String result = component.getName();
		BundleTemplateModel parent = component.getParentTemplate();
		if (parent != null)
		{
			while (parent.getParentTemplate() != null)
			{
				result = parent.getName() + " - " + result;
				parent = parent.getParentTemplate();
			}
		}
		return result;
	}

	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}
}

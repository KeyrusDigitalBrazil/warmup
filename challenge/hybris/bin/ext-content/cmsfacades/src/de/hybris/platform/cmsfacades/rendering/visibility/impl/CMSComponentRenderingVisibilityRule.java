/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.rendering.visibility.impl;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;
import de.hybris.platform.cmsfacades.rendering.RestrictionContextProvider;
import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityRule;
import de.hybris.platform.core.model.ItemModel;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import java.util.function.Predicate;


/**
 * Rendering visibility rule for {@link AbstractCMSComponentModel}
 */
public class CMSComponentRenderingVisibilityRule implements RenderingVisibilityRule<AbstractCMSComponentModel>
{
	private CMSRestrictionService cmsRestrictionService;
	private RestrictionContextProvider restrictionContextProvider;

	@Override
	public Predicate<ItemModel> restrictedBy()
	{
		return itemModel -> AbstractCMSComponentModel.class.isAssignableFrom(itemModel.getClass());
	}

	@Override
	public boolean isVisible(AbstractCMSComponentModel component)
	{
		boolean allowed = true;
		RestrictionData restrictionData = getRestrictionContextProvider().getRestrictionInContext();

		if (Boolean.FALSE.equals(component.getVisible()))
		{
			allowed = false;
		}
		else if (!CollectionUtils.isEmpty(component.getRestrictions()) && restrictionData != null)
		{
			allowed = getCmsRestrictionService().evaluateCMSComponent(component, restrictionData);
		}

		return allowed;
	}

	protected CMSRestrictionService getCmsRestrictionService()
	{
		return cmsRestrictionService;
	}

	@Required
	public void setCmsRestrictionService(CMSRestrictionService cmsRestrictionService)
	{
		this.cmsRestrictionService = cmsRestrictionService;
	}

	protected RestrictionContextProvider getRestrictionContextProvider()
	{
		return restrictionContextProvider;
	}

	@Required
	public void setRestrictionContextProvider(RestrictionContextProvider restrictionContextProvider)
	{
		this.restrictionContextProvider = restrictionContextProvider;
	}
}

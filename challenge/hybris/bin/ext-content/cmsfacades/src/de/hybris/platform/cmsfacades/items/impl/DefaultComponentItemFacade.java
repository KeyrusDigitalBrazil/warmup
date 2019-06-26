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
package de.hybris.platform.cmsfacades.items.impl;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.items.ComponentItemFacade;
import de.hybris.platform.cmsfacades.rendering.ComponentRenderingService;
import de.hybris.platform.core.servicelayer.data.SearchPageData;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ComponentItemFacade}.
 */
public class DefaultComponentItemFacade implements ComponentItemFacade
{
	private ComponentRenderingService componentRenderingService;

	@Override
	public AbstractCMSComponentData getComponentById(final String componentId, final String categoryCode, final String productCode,
			final String catalogCode) throws CMSItemNotFoundException
	{
		return getComponentRenderingService().getComponentById(componentId, categoryCode, productCode, catalogCode);
	}

	@Override
	public SearchPageData<AbstractCMSComponentData> getComponentsByIds(final Collection<String> componentIds,
			final String categoryCode, final String productCode, final String catalogCode, final SearchPageData searchPageData)
	{
		return getComponentRenderingService().getComponentsByIds(componentIds, categoryCode, productCode, catalogCode,
				searchPageData);
	}

	protected ComponentRenderingService getComponentRenderingService()
	{
		return componentRenderingService;
	}

	@Required
	public void setComponentRenderingService(final ComponentRenderingService componentRenderingService)
	{
		this.componentRenderingService = componentRenderingService;
	}
}

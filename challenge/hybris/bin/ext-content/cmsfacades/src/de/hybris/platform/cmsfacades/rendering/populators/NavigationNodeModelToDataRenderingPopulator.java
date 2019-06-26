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
package de.hybris.platform.cmsfacades.rendering.populators;

import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.stream.Collectors;


/**
 * This populator will populate the {@link NavigationNodeData}'s base attributes with attributes from
 * {@link CMSNavigationNodeModel} for rendering purpose
 */
public class NavigationNodeModelToDataRenderingPopulator implements Populator<CMSNavigationNodeModel, NavigationNodeData>
{
	private Converter<CMSNavigationEntryModel, NavigationEntryData> navigationEntryModelToDataConverter;
	private RenderingVisibilityService renderingVisibilityService;

	@Override
	public void populate(final CMSNavigationNodeModel source, final NavigationNodeData target)
	{
		target.setUid(source.getUid());
		target.setName(source.getName());
		target.setLocalizedTitle(source.getTitle());

		final List<NavigationEntryData> navigationEntries = source.getEntries()
				.stream() //
				.filter(entry -> getRenderingVisibilityService().isVisible(entry.getItem()))
				.map(getNavigationEntryModelToDataConverter()::convert) //
				.collect(Collectors.toList());
		target.setEntries(navigationEntries);
	}

	protected Converter<CMSNavigationEntryModel, NavigationEntryData> getNavigationEntryModelToDataConverter()
	{
		return navigationEntryModelToDataConverter;
	}

	@Required
	public void setNavigationEntryModelToDataConverter(
			final Converter<CMSNavigationEntryModel, NavigationEntryData> navigationEntryModelToDataConverter)
	{
		this.navigationEntryModelToDataConverter = navigationEntryModelToDataConverter;
	}

	protected RenderingVisibilityService getRenderingVisibilityService()
	{
		return renderingVisibilityService;
	}

	@Required
	public void setRenderingVisibilityService(
			RenderingVisibilityService renderingVisibilityService)
	{
		this.renderingVisibilityService = renderingVisibilityService;
	}
}

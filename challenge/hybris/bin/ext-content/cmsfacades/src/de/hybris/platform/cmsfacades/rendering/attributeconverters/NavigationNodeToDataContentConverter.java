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
package de.hybris.platform.cmsfacades.rendering.attributeconverters;

import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityService;
import org.springframework.beans.factory.annotation.Required;


/**
 * Rendering Attribute Converter to convert {@link CMSNavigationNodeModel} to a {@link NavigationNodeData}
 * representation.
 */
public class NavigationNodeToDataContentConverter implements Converter<CMSNavigationNodeModel, NavigationNodeData>
{
	private de.hybris.platform.servicelayer.dto.converter.Converter<CMSNavigationNodeModel, NavigationNodeData> navigationNodeModelToDataRenderingConverter;
	private RenderingVisibilityService renderingVisibilityService;

	@Override
	public NavigationNodeData convert(final CMSNavigationNodeModel source)
	{
		if (Objects.isNull(source) || !getRenderingVisibilityService().isVisible(source))
		{
			return null;
		}

		final NavigationNodeData navigationNodeData = getNavigationNodeModelToDataRenderingConverter().convert(source);

		final List<NavigationNodeData> childrenNodes = //
				source.getChildren().stream().map(this::convert).collect(Collectors.toList());
		navigationNodeData.setChildren(childrenNodes);

		return navigationNodeData;
	}

	protected de.hybris.platform.servicelayer.dto.converter.Converter<CMSNavigationNodeModel, NavigationNodeData> getNavigationNodeModelToDataRenderingConverter()
	{
		return navigationNodeModelToDataRenderingConverter;
	}

	@Required
	public void setNavigationNodeModelToDataRenderingConverter(
			final de.hybris.platform.servicelayer.dto.converter.Converter<CMSNavigationNodeModel, NavigationNodeData> navigationNodeModelToDataRenderingConverter)
	{
		this.navigationNodeModelToDataRenderingConverter = navigationNodeModelToDataRenderingConverter;
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

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

import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityRule;
import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityService;
import de.hybris.platform.core.model.ItemModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Objects;


/**
 * Default implementation of {@link RenderingVisibilityService}.
 */
public class DefaultRenderingVisibilityService implements RenderingVisibilityService
{
	private List<RenderingVisibilityRule<ItemModel>> renderingVisibilityRules;

	@Override
	public boolean isVisible(ItemModel itemModel)
	{
		if (Objects.isNull(itemModel))
		{
			return false;
		}
		else
		{
			return getRenderingVisibilityRules()
					.stream()
					.filter(visibility -> visibility.restrictedBy().test(itemModel))
					.findFirst()
					.map(visibility -> visibility.isVisible(itemModel))
					.orElse(true);
		}
	}

	protected List<RenderingVisibilityRule<ItemModel>> getRenderingVisibilityRules()
	{
		return renderingVisibilityRules;
	}

	@Required
	public void setRenderingVisibilityRules(
			List<RenderingVisibilityRule<ItemModel>> renderingVisibilityRules)
	{
		this.renderingVisibilityRules = renderingVisibilityRules;
	}
}

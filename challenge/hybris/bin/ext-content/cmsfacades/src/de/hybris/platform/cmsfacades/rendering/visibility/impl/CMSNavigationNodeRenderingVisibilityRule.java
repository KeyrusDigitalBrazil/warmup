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

import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityRule;
import de.hybris.platform.core.model.ItemModel;

import java.util.function.Predicate;


/**
 * Rendering visibility rule for {@link CMSNavigationNodeModel}
 */
public class CMSNavigationNodeRenderingVisibilityRule implements RenderingVisibilityRule<CMSNavigationNodeModel>
{
	@Override
	public Predicate<ItemModel> restrictedBy()
	{
		return itemModel -> CMSNavigationNodeModel.class.isAssignableFrom(itemModel.getClass());
	}

	@Override
	public boolean isVisible(CMSNavigationNodeModel itemModel)
	{
		return itemModel.isVisible();
	}
}

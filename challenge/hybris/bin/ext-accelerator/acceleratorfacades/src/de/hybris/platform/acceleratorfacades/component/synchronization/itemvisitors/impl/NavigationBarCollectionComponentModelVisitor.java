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
package de.hybris.platform.acceleratorfacades.component.synchronization.itemvisitors.impl;

import de.hybris.platform.acceleratorcms.model.components.NavigationBarCollectionComponentModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.synchronization.itemvisitors.AbstractCMSComponentModelVisitor;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.Map;


/**
 * Concrete implementation of {@link AbstractCMSComponentModelVisitor} to visit items of the
 * {@link NavigationBarCollectionComponentModel} types.
 *
 * Collects the items from {@link AbstractCMSComponentModelVisitor#visit(AbstractCMSComponentModel, List, Map)} and
 * {@link NavigationBarCollectionComponentModel#getComponents()}.
 */
public class NavigationBarCollectionComponentModelVisitor
		extends AbstractCMSComponentModelVisitor<NavigationBarCollectionComponentModel>
{

	@Override
	public List<ItemModel> visit(final NavigationBarCollectionComponentModel source, final List<ItemModel> path,
			final Map<String, Object> ctx)
	{
		final List<ItemModel> collectedItems = super.visit(source, path, ctx);

		collectedItems.addAll(source.getComponents());

		return collectedItems;
	}

}

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
package de.hybris.platform.cmsfacades.synchronization.itemvisitors.impl;

import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cmsfacades.synchronization.itemvisitors.AbstractCMSComponentModelVisitor;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of {@link AbstractCMSComponentModelVisitor} to visit items of the {@link CMSLinkComponentModel} type.
 * 
 * Returns the items from {@link AbstractCMSComponentModelVisitor} along with the {@link CMSLinkComponentModel#getContentPage()} 
 * and {@link CMSLinkComponentModel#getProduct()}. 
 * 
 */
public class CMSLinkComponentModelVisitor extends AbstractCMSComponentModelVisitor<CMSLinkComponentModel>
{

	@Override
	public List<ItemModel> visit(CMSLinkComponentModel source, List<ItemModel> path, Map<String, Object> ctx)
	{
		final List<ItemModel> collectedItems = super.visit(source, path, ctx);
		collectedItems.add(source.getContentPage());
		return collectedItems;
	}

}

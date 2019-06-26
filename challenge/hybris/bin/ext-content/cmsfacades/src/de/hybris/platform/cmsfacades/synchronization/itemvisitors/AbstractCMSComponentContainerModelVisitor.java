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
package de.hybris.platform.cmsfacades.synchronization.itemvisitors;

import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.Map;

/**
 * Abstract class for visiting {@link AbstractCMSComponentContainerModel} models for the cms synchronization service to work properly.
 * In this implementation, it will collect items given by the {@link AbstractCMSComponentModelVisitor}
 * and for collecting all simple CMS components for the given component.
 *
 * @param <CMSCONTAINERTYPE> the media container type that extends {@link AbstractCMSComponentContainerModel}
 */
public abstract class AbstractCMSComponentContainerModelVisitor<CMSCONTAINERTYPE extends AbstractCMSComponentContainerModel> extends AbstractCMSComponentModelVisitor<CMSCONTAINERTYPE>
{

	@SuppressWarnings("deprecation")
	@Override
	public List<ItemModel> visit(CMSCONTAINERTYPE source, List<ItemModel> path, Map<String, Object> ctx)
	{
		final List<ItemModel> collectedItems = super.visit(source, path, ctx);
		collectedItems.addAll(source.getSimpleCMSComponents());
		collectedItems.addAll(source.getCurrentCMSComponents());
		return collectedItems;
	}

}

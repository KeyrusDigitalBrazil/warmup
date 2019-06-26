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

import static com.google.common.collect.Lists.newLinkedList;

import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.visitor.ItemVisitor;

import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of {@link ItemVisitor} to visit items of the {@link ContentSlotModel} types.
 *
 * Collects the items from {@link ContentSlotModel#getCmsComponents()} 
 */
public class ContentSlotModelVisitor implements ItemVisitor<ContentSlotModel>
{

	@Override
	public List<ItemModel> visit(ContentSlotModel source, List<ItemModel> path, Map<String, Object> ctx)
	{
		return newLinkedList(source.getCmsComponents());
	}

}

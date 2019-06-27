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

import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.visitor.ItemVisitor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Concrete implementation of {@link ItemVisitor} to visit items of the {@link ContentSlotForPageModel} types.
 * Collects the item from {@link ContentSlotForPageModel#getContentSlot()}
 */
public class ContentSlotForPageModelVisitor implements ItemVisitor<ContentSlotForPageModel>
{
	@Override public List<ItemModel> visit(ContentSlotForPageModel contentSlotForPageModel, List<ItemModel> list,
			Map<String, Object> map)
	{
		return Arrays.asList(contentSlotForPageModel.getContentSlot());
	}
}

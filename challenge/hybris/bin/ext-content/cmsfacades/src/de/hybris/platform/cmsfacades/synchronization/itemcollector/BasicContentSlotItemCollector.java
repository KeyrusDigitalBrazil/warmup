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
package de.hybris.platform.cmsfacades.synchronization.itemcollector;

import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cmsfacades.common.itemcollector.ItemCollector;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;

/**
 * Collects the direct cms components of a given {@link ContentSlotModel}.
 */
public class BasicContentSlotItemCollector implements ItemCollector<ContentSlotModel>
{
	@Override
	public List<? extends ItemModel> collect(final ContentSlotModel item)
	{
		return item.getCmsComponents();
	}
}

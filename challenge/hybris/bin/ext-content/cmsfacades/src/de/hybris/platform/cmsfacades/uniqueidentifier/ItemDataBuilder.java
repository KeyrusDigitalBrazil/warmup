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
package de.hybris.platform.cmsfacades.uniqueidentifier;

import de.hybris.platform.cmsfacades.data.ItemData;

/**
 * Builder class for {@link ItemData}
 */
public class ItemDataBuilder
{
	private String itemId;
	private String name;
	private String itemType;

	private ItemDataBuilder()
	{
	}

	public ItemData build()
	{
		final ItemData itemData = new ItemData();
		itemData.setItemId(this.itemId);
		itemData.setName(this.name);
		itemData.setItemType(this.itemType);
		return itemData;
	}

	public static ItemDataBuilder newItemDataBuilder()
	{
		return new ItemDataBuilder();
	}


	public ItemDataBuilder itemId(String itemId)
	{
		this.itemId = itemId;
		return this;
	}

	public ItemDataBuilder name(String name)
	{
		this.name = name;
		return this;
	}

	public ItemDataBuilder itemType(String itemType)
	{
		this.itemType = itemType;
		return this;
	}
}

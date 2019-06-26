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
package de.hybris.platform.cmsfacades.uniqueidentifier.functions;

import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.user.daos.UserGroupDao;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for conversion of {@link UserGroupModel}
 */
public class DefaultUserGroupModelUniqueIdentifierConverter implements UniqueIdentifierConverter<UserGroupModel>
{

	private UserGroupDao userGroupDao;

	private ObjectFactory<ItemData> itemDataDataFactory;

	@Override
	public String getItemType()
	{
		return UserGroupModel._TYPECODE;
	}

	@Override
	public ItemData convert(final UserGroupModel itemModel) throws IllegalArgumentException
	{
		if (itemModel == null)
		{
			throw new IllegalArgumentException("The argument itemModel is null");
		}
		final ItemData itemData = getItemDataDataFactory().getObject();
		itemData.setItemId(itemModel.getUid());
		itemData.setItemType(itemModel.getItemtype());
		itemData.setName(itemModel.getName());
		return itemData;
	}

	@Override
	public UserGroupModel convert(final ItemData itemData)
	{
		return getUserGroupDao().findUserGroupByUid(itemData.getItemId());
	}

	protected UserGroupDao getUserGroupDao()
	{
		return userGroupDao;
	}

	@Required
	public void setUserGroupDao(final UserGroupDao userGroupDao)
	{
		this.userGroupDao = userGroupDao;
	}

	protected ObjectFactory<ItemData> getItemDataDataFactory()
	{
		return itemDataDataFactory;
	}

	@Required
	public void setItemDataDataFactory(final ObjectFactory<ItemData> itemDataDataFactory)
	{
		this.itemDataDataFactory = itemDataDataFactory;
	}
}

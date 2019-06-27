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

import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for conversion of {@link CMSPageTypeModel}
 */
public class DefaultCMSPageTypeModelUniqueIdentifierConverter implements UniqueIdentifierConverter<CMSPageTypeModel> 
{
	private ObjectFactory<ItemData> itemDataDataFactory; 
	
	private CMSAdminPageService adminPageService;
	
	@Override
	public String getItemType()
	{
		return CMSPageTypeModel._TYPECODE;
	}

	@Override
	public ItemData convert(final CMSPageTypeModel pageTypeModel) 
	{
		final ItemData itemData = getItemDataDataFactory().getObject();
		itemData.setItemType(CMSPageTypeModel._TYPECODE);
		itemData.setName(pageTypeModel.getName());
		itemData.setItemId(pageTypeModel.getCode());
		return itemData;
	}

	@Override
	public CMSPageTypeModel convert(final ItemData itemData)
	{
		return getAdminPageService().getPageTypeByCode(itemData.getItemId())
				.orElseThrow(() -> new UnknownIdentifierException("Page Type not found for code [" + itemData.getItemId() + "]."));
	}

	protected CMSAdminPageService getAdminPageService()
	{
		return adminPageService;
	}
	
	@Required
	public void setAdminPageService(final CMSAdminPageService adminPageService)
	{
		this.adminPageService = adminPageService;
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



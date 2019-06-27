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
package de.hybris.platform.cmsfacades.version.populator;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.cmsfacades.data.CMSVersionData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a {@link CMSVersionData} instance from the {@link CMSVersionModel} source data model.
 */
public class CMSVersionDataPopulator implements Populator<CMSVersionModel, CMSVersionData>
{
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private CMSAdminItemService cmsAdminItemService;

	@Override
	public void populate(final CMSVersionModel source, final CMSVersionData target) throws ConversionException
	{
		try
		{
			final ItemModel itemModel = getCmsAdminItemService().findByUid(source.getItemUid());
			getUniqueItemIdentifierService().getItemData(itemModel).ifPresent(itemData -> target.setItemUUID(itemData.getItemId()));
		}
		catch (final CMSItemNotFoundException e)
		{
			throw new ConversionException("Failed to find item model with uid: " + source.getUid(), e);
		}

		target.setUid(source.getUid());
		target.setLabel(source.getLabel());
		target.setDescription(source.getDescription());
		target.setCreationtime(source.getCreationtime());
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected CMSAdminItemService getCmsAdminItemService()
	{
		return cmsAdminItemService;
	}

	@Required
	public void setCmsAdminItemService(final CMSAdminItemService cmsAdminItemService)
	{
		this.cmsAdminItemService = cmsAdminItemService;
	}
}

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

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for consuming the item Id, name and itemType from reading {@link MediaModel} class
 */
public class DefaultMediaModelUniqueIdentifierConverter implements UniqueIdentifierConverter<MediaModel>
{

	private ObjectFactory<ItemData> itemDataDataFactory;
	private MediaService mediaService;
	private CatalogVersionService catalogVersionService;
	
	@Override
	public String getItemType()
	{
		return MediaModel._TYPECODE;
	}

	@Override
	public ItemData convert(final MediaModel mediaModel) 
	{
		final ItemData itemData = getItemDataDataFactory().getObject();
		itemData.setItemId(getUniqueIdentifier(mediaModel));
		itemData.setItemType(MediaModel._TYPECODE);
		itemData.setName(mediaModel.getCode());
		return itemData;
	}

	@Override
	public MediaModel convert(final ItemData itemData)
	{
		final EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey
				.Builder(itemData.getItemId()).encoded().build();
		final CatalogVersionModel catalogVersion = getCatalogVersionService().getCatalogVersion(itemComposedKey.getCatalogId(),
				itemComposedKey.getCatalogVersion());
		return getMediaService().getMedia(catalogVersion, itemComposedKey.getItemId());
	}

	/**
	 * Returns the unique identifier using the encoded compose key class. See more details here {@link EncodedItemComposedKey}. 
	 *
	 * @param mediaModel the mediaitem model we want to extract the unique identifier.
	 * @return the encoded unique identifier. 
	 * @see EncodedItemComposedKey
	 */
	protected String getUniqueIdentifier(final MediaModel mediaModel)
	{
		EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
		itemComposedKey.setCatalogId(mediaModel.getCatalogVersion().getCatalog().getId());
		itemComposedKey.setCatalogVersion(mediaModel.getCatalogVersion().getVersion());
		itemComposedKey.setItemId(mediaModel.getCode());

		return itemComposedKey.toEncoded();
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

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}

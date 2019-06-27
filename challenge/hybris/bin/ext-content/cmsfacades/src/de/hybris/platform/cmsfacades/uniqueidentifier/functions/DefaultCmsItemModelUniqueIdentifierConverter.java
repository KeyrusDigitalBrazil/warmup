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

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for conversion of {@link CMSItemModel}
 */
public class DefaultCmsItemModelUniqueIdentifierConverter implements UniqueIdentifierConverter<CMSItemModel>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCmsItemModelUniqueIdentifierConverter.class);

	private ObjectFactory<ItemData> itemDataDataFactory;
	private CMSAdminItemService cmsAdminItemService;
	private CatalogVersionService catalogVersionService;
	private ObjectFactory<EncodedItemComposedKey> encodedItemComposedKeyObjectFactory;

	@Override
	public String getItemType()
	{
		return CMSItemModel._TYPECODE;
	}

	@Override
	public ItemData convert(final CMSItemModel cmsItemModel)
	{
		final ItemData itemData = getItemDataDataFactory().getObject();
		itemData.setItemId(getUniqueIdentifier(cmsItemModel));
		itemData.setItemType(cmsItemModel.getItemtype());
		itemData.setName(cmsItemModel.getUid());
		return itemData;
	}

	@Override
	public CMSItemModel convert(final ItemData itemData)
	{
		checkArgument(itemData != null, "itemData must not be null");
		checkArgument(StringUtils.isNoneBlank(itemData.getItemId()), "itemData.itemId must not be null or empty");

		// support for encoded composed key
		try
		{
			final EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey //
					.Builder(itemData.getItemId()) //
							.encoded() //
							.build();
			final CatalogVersionModel catalogVersion = //
					getCatalogVersionService().getCatalogVersion(itemComposedKey.getCatalogId(), itemComposedKey.getCatalogVersion());

			return findItemByComposedKey(itemComposedKey, catalogVersion);
		}
		catch (IllegalArgumentException | ConversionException e)
		{
			LOGGER.debug(format("Encoded CMSItemComposed key not used with uid %s", itemData.getItemId()), e);
		}

		// if there was an error, try again using simply the itemId.
		return findItemById(itemData);
	}

	protected CMSItemModel findItemByComposedKey(final EncodedItemComposedKey itemComposedKey,
			final CatalogVersionModel catalogVersion)
	{
		try
		{
			return getCmsAdminItemService().findByUid(itemComposedKey.getItemId(), catalogVersion);
		}
		catch (final CMSItemNotFoundException e)
		{
			throw new UnknownIdentifierException(format("Could not find ItemModel with uid [%s] on [%s]/[%s] catalog version.",
					itemComposedKey.getItemId(), itemComposedKey.getCatalogId(), itemComposedKey.getCatalogVersion()), e);
		}
	}

	protected CMSItemModel findItemById(final ItemData itemData)
	{
		try
		{
			return getCmsAdminItemService().findByUid(itemData.getItemId());
		}
		catch (final CMSItemNotFoundException e)
		{
			throw new UnknownIdentifierException(format("could not find ItemModel with uid %s", itemData.getItemId()), e);
		}
	}

	/**
	 * Returns the unique identifier using the encoded compose key class. See more details here
	 * {@link EncodedItemComposedKey}.
	 *
	 * @param cmsItemModel
	 *           the cms item model we want to extract the unique identifier.
	 * @return the encoded unique identifier.
	 * @see EncodedItemComposedKey
	 */
	protected String getUniqueIdentifier(final CMSItemModel cmsItemModel)
	{
		final EncodedItemComposedKey itemComposedKey = getEncodedItemComposedKeyObjectFactory().getObject();
		itemComposedKey.setCatalogId(cmsItemModel.getCatalogVersion().getCatalog().getId());
		itemComposedKey.setCatalogVersion(cmsItemModel.getCatalogVersion().getVersion());
		itemComposedKey.setItemId(cmsItemModel.getUid());

		return itemComposedKey.toEncoded();
	}

	@Required
	public void setCmsAdminItemService(final CMSAdminItemService cmsAdminItemService)
	{
		this.cmsAdminItemService = cmsAdminItemService;
	}

	protected CMSAdminItemService getCmsAdminItemService()
	{
		return cmsAdminItemService;
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

	protected ObjectFactory<ItemData> getItemDataDataFactory()
	{
		return itemDataDataFactory;
	}

	@Required
	public void setItemDataDataFactory(final ObjectFactory<ItemData> itemDataDataFactory)
	{
		this.itemDataDataFactory = itemDataDataFactory;
	}

	protected ObjectFactory<EncodedItemComposedKey> getEncodedItemComposedKeyObjectFactory()
	{
		return encodedItemComposedKeyObjectFactory;
	}

	@Required
	public void setEncodedItemComposedKeyObjectFactory(
			final ObjectFactory<EncodedItemComposedKey> encodedItemComposedKeyObjectFactory)
	{
		this.encodedItemComposedKeyObjectFactory = encodedItemComposedKeyObjectFactory;
	}


}



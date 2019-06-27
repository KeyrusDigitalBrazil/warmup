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
package de.hybris.platform.cmsfacades.media.populator;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate the {@link MediaData} from the {@link MediaModel}.
 */
public class BasicMediaPopulator implements Populator<MediaModel, MediaData>
{
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Override
	public void populate(final MediaModel source, final MediaData target) throws ConversionException
	{
		target.setAltText(source.getAltText());
		target.setCode(source.getCode());
		target.setDescription(source.getDescription());
		target.setDownloadUrl(source.getDownloadURL());
		target.setMime(source.getMime());
		target.setUrl(source.getURL());
		getUniqueItemIdentifierService().getItemData(source).ifPresent(itemData ->
		{
			target.setUuid(itemData.getItemId());
		});

		final CatalogVersionModel catalogVersion = source.getCatalogVersion();
		if (catalogVersion != null)
		{
			target.setCatalogVersion(catalogVersion.getVersion());

			final CatalogModel catalog = catalogVersion.getCatalog();
			if (catalog != null)
			{
				target.setCatalogId(catalog.getId());
			}
		}
	}

	@Required
	public void setUniqueItemIdentifierService(UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

}

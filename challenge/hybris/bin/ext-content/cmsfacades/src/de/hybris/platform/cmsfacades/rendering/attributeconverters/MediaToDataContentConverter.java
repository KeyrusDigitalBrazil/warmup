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
package de.hybris.platform.cmsfacades.rendering.attributeconverters;

import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.core.model.media.MediaModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.Objects;


/**
 * This converter is used to convert an attribute of type {@link MediaModel} to {@link MediaData}.
 */
public class MediaToDataContentConverter implements Converter<MediaModel, MediaData>
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private de.hybris.platform.servicelayer.dto.converter.Converter<MediaModel, MediaData> mediaModelConverter;

	// --------------------------------------------------------------------------
	// Public API
	// --------------------------------------------------------------------------
	@Override
	public MediaData convert(MediaModel source)
	{
		if(Objects.isNull(source))
		{
			return null;
		}

		MediaData convertedMedia = getMediaModelConverter().convert(source);
		convertedMedia.setUuid(null);
		convertedMedia.setCatalogId(null);
		convertedMedia.setCatalogVersion(null);

		return convertedMedia;
	}

	// --------------------------------------------------------------------------
	// Getters/Setters
	// --------------------------------------------------------------------------
	protected de.hybris.platform.servicelayer.dto.converter.Converter<MediaModel, MediaData> getMediaModelConverter()
	{
		return mediaModelConverter;
	}

	@Required
	public void setMediaModelConverter(
			de.hybris.platform.servicelayer.dto.converter.Converter<MediaModel, MediaData> mediaModelConverter)
	{
		this.mediaModelConverter = mediaModelConverter;
	}
}

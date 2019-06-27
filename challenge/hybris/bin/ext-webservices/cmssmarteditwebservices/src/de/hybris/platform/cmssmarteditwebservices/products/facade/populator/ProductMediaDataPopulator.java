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
package de.hybris.platform.cmssmarteditwebservices.products.facade.populator;

import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.data.ProductData;
import de.hybris.platform.cmsfacades.media.MediaFacade;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

/**
 * Basic class for populating media data for {@link de.hybris.platform.cmssmarteditwebservices.data.ProductData} from {@link ProductData} data.
 */
public class ProductMediaDataPopulator implements Populator<ProductData, de.hybris.platform.cmssmarteditwebservices.data.ProductData>
{

	private MediaFacade mediaFacade;

	@Override
	public void populate(final ProductData source,
			final de.hybris.platform.cmssmarteditwebservices.data.ProductData target) throws ConversionException
	{
		Optional.ofNullable(source.getThumbnailMediaCode())
				.ifPresent(thumbnailMediaCode -> setThumbnail(target, thumbnailMediaCode));
	}

	protected void setThumbnail(final de.hybris.platform.cmssmarteditwebservices.data.ProductData productData,
			final String mediaCode)
	{
		final MediaData mediaData = getMediaFacade().getMediaByCode(mediaCode);
		productData.setThumbnail(mediaData);
	}

	protected MediaFacade getMediaFacade()
	{
		return mediaFacade;
	}

	@Required
	public void setMediaFacade(final MediaFacade mediaFacade)
	{
		this.mediaFacade = mediaFacade;
	}
}

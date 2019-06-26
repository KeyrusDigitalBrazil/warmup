/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.marketplacefacades.vendor.converters.populator;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.VendorRatingData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.util.ServicesUtil;


/**
 *
 */
public class VendorPopulator implements Populator<VendorModel, VendorData>
{

	private Converter<MediaContainerModel, List<ImageData>> mediaContainerConverter;
	private UrlResolver<VendorModel> vendorUrlResolver;

	private static final double INITIAL_RATING = 0.0;
	private static final long INITIAL_REVIEW_COUNT = 0;

	@Override
	public void populate(final VendorModel source, final VendorData target)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("source", source);
		ServicesUtil.validateParameterNotNullStandardMessage("target", target);

		target.setCode(source.getCode());
		target.setName(source.getName());

		if (source.getLogo() != null)
		{
			target.setLogo(mediaContainerConverter.convert(source.getLogo()));
		}

		target.setRating(createRatingData(source));
		target.setUrl(getVendorUrlResolver().resolve(source));
	}

	protected VendorRatingData createRatingData(final VendorModel vendor)
	{
		final VendorRatingData ratingData = new VendorRatingData();
		if (vendor.getReviewCount() == null)
		{
			ratingData.setSatisfaction(INITIAL_RATING);
			ratingData.setDelivery(INITIAL_RATING);
			ratingData.setCommunication(INITIAL_RATING);
			ratingData.setAverage(INITIAL_RATING);
			ratingData.setReviewCount(INITIAL_REVIEW_COUNT);
		}
		else
		{
			ratingData.setSatisfaction(vendor.getSatisfactionRating());
			ratingData.setDelivery(vendor.getDeliveryRating());
			ratingData.setCommunication(vendor.getCommunicationRating());
			ratingData.setAverage(vendor.getAverageRating());
			ratingData.setReviewCount(vendor.getReviewCount());
		}
		return ratingData;
	}

	protected Converter<MediaContainerModel, List<ImageData>> getMediaContainerConverter()
	{
		return mediaContainerConverter;
	}

	@Required
	public void setMediaContainerConverter(final Converter<MediaContainerModel, List<ImageData>> mediaContainerConverter)
	{
		this.mediaContainerConverter = mediaContainerConverter;
	}

	protected UrlResolver<VendorModel> getVendorUrlResolver()
	{
		return vendorUrlResolver;
	}

	@Required
	public void setVendorUrlResolver(final UrlResolver<VendorModel> vendorUrlResolver)
	{
		this.vendorUrlResolver = vendorUrlResolver;
	}


}

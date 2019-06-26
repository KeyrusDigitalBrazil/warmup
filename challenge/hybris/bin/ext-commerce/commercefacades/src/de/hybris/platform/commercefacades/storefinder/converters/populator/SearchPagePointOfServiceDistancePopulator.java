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
package de.hybris.platform.commercefacades.storefinder.converters.populator;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


public class SearchPagePointOfServiceDistancePopulator<SOURCE extends StoreFinderSearchPageData<PointOfServiceDistanceData>, TARGET extends StoreFinderSearchPageData<PointOfServiceData>>
		implements Populator<SOURCE, TARGET>
{
	private Converter<PointOfServiceDistanceData, PointOfServiceData> pointOfServiceDistanceDataConverter;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setBoundEastLongitude(source.getBoundEastLongitude());
		target.setBoundNorthLatitude(source.getBoundNorthLatitude());
		target.setBoundSouthLatitude(source.getBoundSouthLatitude());
		target.setBoundWestLongitude(source.getBoundWestLongitude());
		target.setLocationText(source.getLocationText());
		target.setPagination(source.getPagination());
		target.setResults(Converters.convertAll(source.getResults(), getPointOfServiceDistanceDataConverter()));
		target.setSorts(source.getSorts());
		target.setSourceLatitude(source.getSourceLatitude());
		target.setSourceLongitude(source.getSourceLongitude());
	}

	@Required
	public void setPointOfServiceDistanceDataConverter(
			final Converter<PointOfServiceDistanceData, PointOfServiceData> pointOfServiceDistanceDataConverter)
	{
		this.pointOfServiceDistanceDataConverter = pointOfServiceDistanceDataConverter;
	}

	protected Converter<PointOfServiceDistanceData, PointOfServiceData> getPointOfServiceDistanceDataConverter()
	{
		return pointOfServiceDistanceDataConverter;
	}
}

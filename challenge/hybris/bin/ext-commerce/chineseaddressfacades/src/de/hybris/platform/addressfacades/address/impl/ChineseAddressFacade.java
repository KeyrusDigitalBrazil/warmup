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
package de.hybris.platform.addressfacades.address.impl;

import de.hybris.platform.addressfacades.address.AddressFacade;
import de.hybris.platform.addressfacades.data.CityData;
import de.hybris.platform.addressfacades.data.DistrictData;
import de.hybris.platform.addressservices.address.AddressService;
import de.hybris.platform.addressservices.model.CityModel;
import de.hybris.platform.addressservices.model.DistrictModel;
import de.hybris.platform.addressservices.strategies.PostcodeValidateStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

/**
 * 
 * Implementation of {@link AddressFacade}
 *
 */
public class ChineseAddressFacade implements AddressFacade
{
	private AddressService chineseAddressService;
	private Converter<CityModel, CityData> cityConverter;
	private Converter<DistrictModel, DistrictData> districtConverter;
	private PostcodeValidateStrategy postcodeValidateStrategy;

	@Override
	public CityData getCityForIsocode(final String isocode)
	{
		final CityModel cityModel = chineseAddressService.getCityForIsocode(isocode);
		if (cityModel != null)
		{
			return cityConverter.convert(cityModel);
		}
		return null;
	}

	@Override
	public DistrictData getDistrcitForIsocode(final String isocode)
	{
		final DistrictModel districtModel = chineseAddressService.getDistrictForIsocode(isocode);
		if (districtModel != null)
		{
			return districtConverter.convert(districtModel);
		}
		return null;
	}

	@Override
	public List<CityData> getCitiesForRegion(final String regionCode)
	{
		final List<CityModel> cityModels = chineseAddressService.getCitiesForRegion(regionCode);
		return cityModels.stream().map(cityModel -> cityConverter.convert(cityModel))
				.sorted((cityData1, cityData2) -> cityData1.getName().compareTo(cityData2.getName())).collect(Collectors.toList());
	}

	@Override
	public List<DistrictData> getDistrictsForCity(final String cityCode)
	{
		final List<DistrictModel> districtModels = chineseAddressService.getDistrictsForCity(cityCode);
		return districtModels.stream().map(districtModel -> districtConverter.convert(districtModel))
				.sorted((districtData1, districtData2) -> districtData1.getName().compareTo(districtData2.getName()))
				.collect(Collectors.toList());
	}

	@Override
	public boolean validatePostcode(final String postcode)
	{
		return getPostCodeValidateStrategy().validate(postcode);
	}


	protected AddressService getChineseAddressService()
	{
		return chineseAddressService;
	}

	@Required
	public void setChineseAddressService(final AddressService chineseAddressService)
	{
		this.chineseAddressService = chineseAddressService;
	}

	protected Converter<CityModel, CityData> getCityConverter()
	{
		return cityConverter;
	}

	@Required
	public void setCityConverter(final Converter<CityModel, CityData> cityConverter)
	{
		this.cityConverter = cityConverter;
	}

	protected Converter<DistrictModel, DistrictData> getDistrictConverter()
	{
		return districtConverter;
	}
	
	@Required
	public void setDistrictConverter(final Converter<DistrictModel, DistrictData> districtConverter)
	{
		this.districtConverter = districtConverter;
	}

	protected PostcodeValidateStrategy getPostCodeValidateStrategy()
	{
		return postcodeValidateStrategy;
	}

	@Required
	public void setPostcodeValidateStrategy(final PostcodeValidateStrategy postcodeValidateStrategy)
	{
		this.postcodeValidateStrategy = postcodeValidateStrategy;
	}

}

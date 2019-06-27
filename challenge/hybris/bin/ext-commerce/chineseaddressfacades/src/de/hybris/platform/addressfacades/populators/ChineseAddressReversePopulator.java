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
package de.hybris.platform.addressfacades.populators;

import de.hybris.platform.addressfacades.data.CityData;
import de.hybris.platform.addressfacades.data.DistrictData;
import de.hybris.platform.addressservices.address.AddressService;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


public class ChineseAddressReversePopulator extends AddressReversePopulator
{
	private AddressService chineseAddressService;

	@Override
	public void populate(final AddressData addressData, final AddressModel addressModel)
	{
		Assert.notNull(addressData, "Parameter addressData cannot be null.");
		Assert.notNull(addressModel, "Parameter addressModel cannot be null.");

		super.populate(addressData, addressModel);
		addressModel.setFullname(addressData.getFullname());
		addressModel.setCellphone(addressData.getCellphone());

		fillInFirstAndLastName(addressModel);

		final CityData city = addressData.getCity();
		if (city != null && StringUtils.isNotBlank(city.getCode()))
		{
			addressModel.setCity(chineseAddressService.getCityForIsocode(city.getCode()));
			//populate town with the city value, so that town is not displayed as "null" in backoffice
			addressModel.setTown(city.getName());
		}

		final DistrictData district = addressData.getDistrict();
		if (district != null && StringUtils.isNotBlank(district.getCode()))
		{
			addressModel.setCityDistrict(chineseAddressService.getDistrictForIsocode(district.getCode()));
		}
	}

	/**
	 * If first and last name are empty, try to copy full name to it, this is needed when guest checks out and creates
	 * new account based on address
	 *
	 * @param addressData
	 * @param addressModel
	 */
	protected void fillInFirstAndLastName(final AddressModel addressModel)
	{
		final String fullName = addressModel.getFullname();
		if (StringUtils.isEmpty(addressModel.getFirstname()) && StringUtils.isEmpty(addressModel.getLastname())
				&& StringUtils.isNotEmpty(fullName))
		{
			addressModel.setFirstname(fullName);
			addressModel.setLastname(StringUtils.EMPTY);
		}
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

}

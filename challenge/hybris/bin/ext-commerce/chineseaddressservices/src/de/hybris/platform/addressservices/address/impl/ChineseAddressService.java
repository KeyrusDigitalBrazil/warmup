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
package de.hybris.platform.addressservices.address.impl;

import de.hybris.platform.addressservices.address.AddressService;
import de.hybris.platform.addressservices.address.daos.AddressDao;
import de.hybris.platform.addressservices.model.CityModel;
import de.hybris.platform.addressservices.model.DistrictModel;
import de.hybris.platform.servicelayer.user.impl.DefaultAddressService;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * 
 * Implementation of {@link AddressService}
 *
 */
public class ChineseAddressService extends DefaultAddressService implements AddressService
{
	private transient AddressDao chineseAddressDao;

	@Override
	public List<CityModel> getCitiesForRegion(final String regionCode)
	{
		final List<CityModel> result = chineseAddressDao.getCitiesForRegion(regionCode);
		return result.isEmpty() ? Collections.emptyList() : result;
	}

	@Override
	public List<DistrictModel> getDistrictsForCity(final String cityCode)
	{
		final List<DistrictModel> result = chineseAddressDao.getDistrictsForCity(cityCode);
		return result.isEmpty() ? Collections.emptyList() : result;
	}

	@Override
	public CityModel getCityForIsocode(final String isocode)
	{
		return chineseAddressDao.getCityForIsocode(isocode);
	}

	@Override
	public DistrictModel getDistrictForIsocode(final String isocode)
	{
		return chineseAddressDao.getDistrictForIsocode(isocode);
	}

	protected AddressDao getChineseAddressDao()
	{
		return chineseAddressDao;
	}

	@Required
	public void setChineseAddressDao(final AddressDao chineseAddressDao)
	{
		this.chineseAddressDao = chineseAddressDao;
	}

}

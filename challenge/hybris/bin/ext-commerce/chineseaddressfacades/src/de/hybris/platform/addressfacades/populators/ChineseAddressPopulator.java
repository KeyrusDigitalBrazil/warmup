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
import de.hybris.platform.addressservices.strategies.NameWithTitleFormatStrategy;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


public class ChineseAddressPopulator implements Populator<AddressModel, AddressData>
{
	private I18NService i18NService;

	private NameWithTitleFormatStrategy nameWithTitleFormatStrategy;

	public I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	protected NameWithTitleFormatStrategy getNameWithTitleFormatStrategy()
	{
		return nameWithTitleFormatStrategy;
	}

	@Required
	public void setNameWithTitleFormatStrategy(final NameWithTitleFormatStrategy nameWithTitleFormatStrategy)
	{
		this.nameWithTitleFormatStrategy = nameWithTitleFormatStrategy;
	}

	@Override
	public void populate(final AddressModel source, final AddressData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCity(extractCity(source));
		target.setDistrict(extractDistrict(source));
		final String fullname = source.getFullname();
		if (StringUtils.isEmpty(fullname))
		{
			target.setFullnameWithTitle(getNameWithTitleFormatStrategy().getFullnameWithTitle(source.getFirstname(),
					source.getLastname(), extractTitleName(source.getTitle())));
		}
		else
		{
			target.setFullname(fullname);
			target.setFullnameWithTitle(getNameWithTitleFormatStrategy().getFullnameWithTitle(fullname,
					extractTitleName(source.getTitle())));
		}
		final String cellphone = source.getCellphone();
		if (StringUtils.isNotEmpty(cellphone))
		{
			target.setCellphone(cellphone);
		}
	}

	protected CityData extractCity(final AddressModel source)
	{
		final CityData city = new CityData();
		if (source.getCity() != null)
		{
			if (StringUtils.isNotEmpty(source.getCity().getIsocode()))
			{
				city.setCode(source.getCity().getIsocode());
			}
			if (StringUtils.isNotEmpty(source.getCity().getName()))
			{
				city.setName(source.getCity().getName());
			}
		}
		return city;
	}

	protected DistrictData extractDistrict(final AddressModel source)
	{
		final DistrictData district = new DistrictData();
		if (source.getCityDistrict() != null)
		{
			if (StringUtils.isNotEmpty(source.getCityDistrict().getIsocode()))
			{
				district.setCode(source.getCityDistrict().getIsocode());
			}
			if (StringUtils.isNotEmpty(source.getCityDistrict().getName()))
			{
				district.setName(source.getCityDistrict().getName());
			}
		}
		return district;
	}

	/**
	 * if address.getTitle() is null, return reverent by default
	 *
	 * @param title
	 * @return title name
	 */
	protected String extractTitleName(final TitleModel title)
	{
		if (title == null)
		{
			return StringUtils.EMPTY;
		}
		return title.getName(getI18NService().getCurrentLocale());
	}
}

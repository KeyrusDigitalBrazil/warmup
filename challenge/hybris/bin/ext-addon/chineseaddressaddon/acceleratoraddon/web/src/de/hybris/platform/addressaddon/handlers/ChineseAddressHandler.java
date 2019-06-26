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
package de.hybris.platform.addressaddon.handlers;

import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.addressaddon.forms.ChineseAddressForm;
import de.hybris.platform.addressaddon.forms.validation.ChineseAddressValidator;
import de.hybris.platform.addressfacades.address.AddressFacade;
import de.hybris.platform.addressfacades.data.CityData;
import de.hybris.platform.addressfacades.data.DistrictData;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegionData;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;


/**
 *
 */
@Component("chineseAddressHandler")
public class ChineseAddressHandler
{
	@Resource(name = "chineseAddressFacade")
	protected AddressFacade chineseAddressFacade;

	@Resource(name = "i18NFacade")
	protected I18NFacade i18NFacade;

	@Resource(name = "chineseAddressValidator")
	protected ChineseAddressValidator chineseAddressValidator;

	public void validate(final ChineseAddressForm addressForm, final BindingResult bindingResult)
	{
		chineseAddressValidator.validate(addressForm, bindingResult);
	}

	public AddressData prepareAddressData(final ChineseAddressForm addressForm)
	{
		final AddressData newAddress = new AddressData();
		BeanUtils.copyProperties(addressForm, newAddress, "billingAddress", "shippingAddress", "visibleInAddressBook",
				"defaultAddress");

		newAddress.setId(addressForm.getAddressId());
		newAddress.setBillingAddress(false);
		newAddress.setShippingAddress(true);
		newAddress.setVisibleInAddressBook(true);
		newAddress.setPostalCode(addressForm.getPostcode());
		newAddress.setCountry(i18NFacade.getCountryForIsocode(addressForm.getCountryIso()));
		newAddress.setRegion(i18NFacade.getRegion(addressForm.getCountryIso(), addressForm.getRegionIso()));
		newAddress.setDistrict(chineseAddressFacade.getDistrcitForIsocode(addressForm.getDistrictIso()));
		newAddress.setCity(chineseAddressFacade.getCityForIsocode(addressForm.getCityIso()));
		return newAddress;
	}

	public ChineseAddressForm setChineseAddressFormInModel(final Model model)
	{
		final AddressForm addressForm = (AddressForm) model.asMap().get("addressForm");
		final ChineseAddressForm chineseAddressForm = new ChineseAddressForm();
		BeanUtils.copyProperties(addressForm, chineseAddressForm);
		model.addAttribute("addressForm", chineseAddressForm);
		return chineseAddressForm;
	}

	public void prepareAddressForm(final Model model, final ChineseAddressForm addressForm)
	{
		final AddressData addressData = (AddressData) model.asMap().get("addressData");
		if (addressData != null)
		{
			addressForm.setCellphone(addressData.getCellphone());
			addressForm.setFullname(addressData.getFullname());

			final RegionData region = addressData.getRegion();
			if (region != null && StringUtils.isNotEmpty(region.getIsocode()))
			{
				model.addAttribute("cities", chineseAddressFacade.getCitiesForRegion(region.getIsocode()));
			}
			final CityData city = addressData.getCity();
			if (city != null && StringUtils.isNotEmpty(city.getCode()))
			{
				model.addAttribute("districts", chineseAddressFacade.getDistrictsForCity(city.getCode()));
				addressForm.setCityIso(city.getCode());
			}
			final DistrictData district = addressData.getDistrict();
			if (district != null && StringUtils.isNotEmpty(district.getCode()))
			{
				addressForm.setDistrictIso(addressData.getDistrict().getCode());
			}
		}
	}
}
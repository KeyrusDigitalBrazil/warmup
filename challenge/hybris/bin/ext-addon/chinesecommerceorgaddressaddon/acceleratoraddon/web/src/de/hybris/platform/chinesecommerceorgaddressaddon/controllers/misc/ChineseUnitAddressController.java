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
package de.hybris.platform.chinesecommerceorgaddressaddon.controllers.misc;

import de.hybris.platform.addressfacades.address.AddressFacade;
import de.hybris.platform.addressfacades.data.CityData;
import de.hybris.platform.addressfacades.data.DistrictData;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Handle address request for regions and cities
 */
@Controller
@Scope("tenant")
@RequestMapping("/my-company/organization-management/manage-units")
public class ChineseUnitAddressController
{
	@Resource(name = "chineseAddressFacade")
	private AddressFacade chineseAddressFacade;

	@RequestMapping(value = "/region/{code}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<CityData> getRegionAddressForm(@PathVariable("code") final String region)
	{
		return chineseAddressFacade.getCitiesForRegion(region);
	}

	@RequestMapping(value = "/city/{code}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<DistrictData> getCityAddressForm(@PathVariable("code") final String city)
	{
		return chineseAddressFacade.getDistrictsForCity(city);
	}
}

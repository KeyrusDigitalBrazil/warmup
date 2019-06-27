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
package de.hybris.platform.ycommercewebservices.v2.controller;

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commerceservices.enums.CountryType;
import de.hybris.platform.commercewebservicescommons.dto.user.CountryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.RegionDataList;
import de.hybris.platform.commercewebservicescommons.dto.user.RegionListWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.ycommercewebservices.user.data.CountryDataList;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping(value = "/{baseSiteId}/countries")
@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
@Api(tags = "Countries")
public class CountriesController extends BaseCommerceController
{
	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;

	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;

	@RequestMapping(method = RequestMethod.GET)
	@Cacheable(value = "countriesCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getCountries',#type,#fields)")
	@ResponseBody
	@ApiOperation(nickname = "getCountries", value = "Get a list of countries.", notes =
			"If the value of type equals to shipping, then return shipping countries. If the value of type equals to billing, then return billing countries."
					+ " If the value of type is not given, return all countries. The list is sorted alphabetically.")
	@ApiBaseSiteIdParam
	public CountryListWsDTO getCountries(
			@ApiParam(value = "The type of countries.", allowableValues = "SHIPPING,BILLING", required = false) @RequestParam(required = false) final String type,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (StringUtils.isNotBlank(type) && !CountryType.SHIPPING.toString().equalsIgnoreCase(type)
				&& !CountryType.BILLING.toString().equalsIgnoreCase(type))
		{
			throw new IllegalStateException(String.format("The value of country type : [%s] is invalid", type));
		}

		final CountryDataList dataList = new CountryDataList();
		dataList.setCountries(checkoutFacade.getCountries(StringUtils.isNotBlank(type) ? CountryType.valueOf(type) : null));
		return getDataMapper().map(dataList, CountryListWsDTO.class, fields);
	}

	@GetMapping("/{countyIsoCode}/regions")
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@Cacheable(value = "countriesCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,false,'getRegionsForCountry',#countyIsoCode,#fields)")
	@ApiOperation(nickname = "getCountryRegions", value = "Fetch the list of regions for the provided country.", notes = "Lists all regions.")
	@ApiBaseSiteIdParam
	public RegionListWsDTO getCountryRegions(
			@ApiParam(value = "An ISO code for a country", required = true) @PathVariable final String countyIsoCode,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final RegionDataList regionDataList = new RegionDataList();
		regionDataList.setRegions(i18NFacade.getRegionsForCountryIso(countyIsoCode.toUpperCase()));

		return getDataMapper().map(regionDataList, RegionListWsDTO.class, fields);
	}

}

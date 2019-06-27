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


import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.StoreFinderSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.ycommercewebservices.v2.helper.StoresHelper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
@Api(tags = "Stores")
public class StoresController extends BaseController
{
	private static final String DEFAULT_SEARCH_RADIUS_METRES = "100000.0";
	private static final String DEFAULT_ACCURACY = "0.0";

	@Resource(name = "storesHelper")
	private StoresHelper storesHelper;

	@RequestMapping(value = "/{baseSiteId}/stores", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getStoreLocations", value = "Get a list of store locations", notes = "Lists all store locations that are near the location specified in a query or based on latitude and longitude.")
	@ApiBaseSiteIdParam
	public StoreFinderSearchPageWsDTO getStoreLocations(
			@ApiParam(value = "Location in natural language i.e. city or country.") @RequestParam(required = false) final String query, //NOSONAR
			@ApiParam(value = "Coordinate that specifies the north-south position of a point on the Earth's surface.") @RequestParam(required = false) final Double latitude,
			@ApiParam(value = "Coordinate that specifies the east-west position of a point on the Earth's surface.") @RequestParam(required = false) final Double longitude,
			@ApiParam(value = "The current result page requested.") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "The number of results returned per page.") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Sorting method applied to the return results.") @RequestParam(required = false, defaultValue = "asc") final String sort,
			@ApiParam(value = "Radius in meters. Max value: 40075000.0 (Earth's perimeter).") @RequestParam(required = false, defaultValue = DEFAULT_SEARCH_RADIUS_METRES) final double radius,
			@ApiParam(value = "Accuracy in meters.") @RequestParam(required = false, defaultValue = DEFAULT_ACCURACY) final double accuracy,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			final HttpServletResponse response) throws RequestParameterException //NOSONAR
	{
		final StoreFinderSearchPageWsDTO result = storesHelper.locationSearch(query, latitude, longitude, currentPage, pageSize,
				sort, radius, accuracy, addPaginationField(fields));

		// X-Total-Count header
		setTotalCountHeader(response, result.getPagination());

		return result;
	}


	@RequestMapping(value = "/{baseSiteId}/stores", method = RequestMethod.HEAD)
	@ApiOperation(nickname = "countStoreLocations", value = "Get a header with the number of store locations.", notes =
			"In the response header, the \"x-total-count\" indicates the number of "
					+ "all store locations that are near the location specified in a query, or based on latitude and longitude.")
	@ApiBaseSiteIdParam
	public void countStoreLocations(
			@ApiParam(value = "Location in natural language i.e. city or country.") @RequestParam(required = false) final String query,
			@ApiParam(value = "Coordinate that specifies the north-south position of a point on the Earth's surface.") @RequestParam(required = false) final Double latitude,
			@ApiParam(value = "Coordinate that specifies the east-west position of a point on the Earth's surface.") @RequestParam(required = false) final Double longitude,
			@ApiParam(value = "Radius in meters. Max value: 40075000.0 (Earth's perimeter).") @RequestParam(required = false, defaultValue = DEFAULT_SEARCH_RADIUS_METRES) final double radius,
			@ApiParam(value = "Accuracy in meters.") @RequestParam(required = false, defaultValue = DEFAULT_ACCURACY) final double accuracy,
			final HttpServletResponse response) throws RequestParameterException //NOSONAR
	{
		final StoreFinderSearchPageData<PointOfServiceData> result = storesHelper.locationSearch(query, latitude, longitude, 0, 1,
				"asc", radius, accuracy);

		// X-Total-Count header
		setTotalCountHeader(response, result.getPagination());
	}


	@RequestMapping(value = "/{baseSiteId}/stores/{storeId}", method = RequestMethod.GET)
	@ApiOperation(nickname = "getStoreLocation", value = "Get a store location.", notes = "Returns store location based on its unique name.")
	@ApiBaseSiteIdParam
	@ResponseBody
	public PointOfServiceWsDTO getStoreLocation(
			@ApiParam(value = "Store identifier (currently store name)", required = true) @PathVariable final String storeId,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return storesHelper.locationDetails(storeId, fields);
	}
}

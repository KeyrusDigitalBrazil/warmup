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

import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.commercefacades.basestores.BaseStoreFacade;
import de.hybris.platform.commercewebservicescommons.dto.basestore.BaseStoreWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * Web Services Controller to expose the functionality of the {@link BaseStoreFacade}
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/basestores")
@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
@Api(tags = "Base Stores")
public class BaseStoresController extends BaseController
{
	@Resource(name = "baseStoreFacade")
	private BaseStoreFacade baseStoreFacade;

	@RequestMapping(value = "/{baseStoreUid}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getBaseStore", value = "Get a base store.", notes = "Returns details of a specific base store based on its identifier. The response contains detailed base store information.")
	@ApiBaseSiteIdParam
	public BaseStoreWsDTO getBaseStore(
			@ApiParam(value = "Base store name", required = true) @PathVariable final String baseStoreUid,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final BaseStoreData baseStoreData = baseStoreFacade.getBaseStoreByUid(baseStoreUid);

		return getDataMapper().map(baseStoreData, BaseStoreWsDTO.class, fields);
	}
}

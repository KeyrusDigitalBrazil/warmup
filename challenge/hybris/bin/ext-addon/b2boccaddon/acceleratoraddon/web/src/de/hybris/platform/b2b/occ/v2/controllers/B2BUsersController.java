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
package de.hybris.platform.b2b.occ.v2.controllers;

import de.hybris.platform.b2b.occ.security.SecuredAccessConstants;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;

import javax.annotation.Resource;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * Main Controller for Users
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/users")
@ApiVersion("v2")
@Api(tags = "B2B Users")
public class B2BUsersController
{
	@Resource(name = "b2bCustomerFacade")
	private CustomerFacade customerFacade;
	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;

	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	@RequestMappingOverride(priorityProperty = "b2bocc.B2BUsersController.getUser.priority")
	@ResponseBody
	@ApiOperation(value = "Get a B2B user profile", notes = "Returns a B2B user profile.")
	@ApiBaseSiteIdAndUserIdParam
	public UserWsDTO getUser(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = "DEFAULT") final String fields)
	{
		final CustomerData customerData = customerFacade.getCurrentCustomer();
		final UserWsDTO dto = dataMapper.map(customerData, UserWsDTO.class, fields);
		return dto;
	}
}

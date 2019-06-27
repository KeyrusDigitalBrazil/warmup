/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmswebservices.users.controller;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.users.UserFacade;
import de.hybris.platform.cmswebservices.dto.UserDataWsDTO;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;


/**
 * Controller to retrieve Users.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/users")
public class UserController
{
	@Resource
	private UserFacade cmsUserFacade;

	@Resource
	private DataMapper dataMapper;

	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get user by id", notes = "Get a user that matches the given id.")
	@ApiResponses({
			@ApiResponse(code = 400, message = "When the user was not found (CMSItemNotFoundException) or when there was a problem during conversion (ConversionException)."),
			@ApiResponse(code = 200, message = "UserDataWsDTO", response = UserDataWsDTO.class)
	})
	public UserDataWsDTO getUserDataById(
			@ApiParam(value = "The unique identifier of the user", required = true) @PathVariable final String userId) throws CMSItemNotFoundException
	{
		return getDataMapper().map(getCmsUserFacade().getUserById(userId), UserDataWsDTO.class);
	}

	protected UserFacade getCmsUserFacade()
	{
		return cmsUserFacade;
	}

	public void setCmsUserFacade(final UserFacade cmsUserFacade)
	{
		this.cmsUserFacade = cmsUserFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}
}

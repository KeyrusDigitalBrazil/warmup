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
package de.hybris.platform.smarteditwebservices.controllers;

import static de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants.API_VERSION;

import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.smarteditwebservices.configuration.facade.SmarteditConfigurationFacade;
import de.hybris.platform.smarteditwebservices.data.ConfigurationData;
import de.hybris.platform.smarteditwebservices.dto.ConfigurationDataListWsDto;
import de.hybris.platform.smarteditwebservices.security.IsAuthorizedAdmin;
import de.hybris.platform.smarteditwebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import javax.annotation.Resource;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * Controller to manage cms configuration data
 */
@Controller
@RequestMapping(API_VERSION + "/configurations")
@Api(tags = "configurations")
public class ConfigurationController
{
	private static Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);

	@Resource
	private SmarteditConfigurationFacade smarteditConfigurationFacade;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@IsAuthorizedCmsManager
	@ApiOperation(value = "Get All Configurations", notes = "Endpoint to retrieve all cms configuration data")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "Must be authenticated as an Admin or CMS Manager to access this resource")
	})
	public ConfigurationDataListWsDto loadAll()
	{
		final ConfigurationDataListWsDto configurations = new ConfigurationDataListWsDto();
		configurations.setConfigurations(getSmarteditConfigurationFacade().findAll());
		return configurations;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@IsAuthorizedAdmin
	@ApiOperation(value = "Save a Configuration", notes = "Endpoint to create cms configuration data")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "Must be authenticated as an Admin to access this resource")
	})
	/*
	 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exceptions) : The exception
	 * is already being handled appropriately.
	 */
	@SuppressWarnings("squid:S1166")
	public ConfigurationData save(
			@ApiParam(value = "Configuration data", required = true) @RequestBody final ConfigurationData data)
	{
		try
		{
			return getSmarteditConfigurationFacade().create(data);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@RequestMapping(value = "/{key:.+}", method = RequestMethod.GET)
	@ResponseBody
	@IsAuthorizedCmsManager
	@ApiOperation(value = "Find a Configuration by Key", notes = "Endpoint to retrieve cms configuration data that matches the given key value")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "Must be authenticated as an Admin or CMS Manager to access this resource")
	})
	public ConfigurationData findByKey(
			@ApiParam(value = "Configuration data identifier", required = true) @PathVariable("key") final String key)
	{
		return getSmarteditConfigurationFacade().findByUid(key);
	}

	@RequestMapping(value = "/{key:.+}", method = RequestMethod.PUT)
	@ResponseBody
	@IsAuthorizedAdmin
	@ApiOperation(value = "Update a Configuration", notes = "Endpoint to update cms configuration data")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Configuration data input is invalid"),
			@ApiResponse(code = 401, message = "Must be authenticated as an Admin to access this resource")
	})
	/*
	 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exceptions) : The exception
	 * is already being handled appropriately.
	 */
	@SuppressWarnings("squid:S1166")
	public ConfigurationData update(
			@ApiParam(value = "Configuration data", required = true) @RequestBody final ConfigurationData data,
			@ApiParam(value = "Configuration data identifier", required = true) @PathVariable("key") final String key)
	{
		try
		{
			return getSmarteditConfigurationFacade().update(key, data);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@RequestMapping(value = "/{key:.+}", method = RequestMethod.DELETE)
	@ResponseBody
	@IsAuthorizedAdmin
	@ApiOperation(value = "Remove a Configuration", notes = "Endpoint to remove cms configuration data that matches the given key")
	@ApiResponses(value = {
			@ApiResponse(code = 401, message = "Must be authenticated as an Admin to access this resource")
	})
	public void delete(@ApiParam(value = "Configuration data identifier", required = true) @PathVariable("key") final String key)
	{
		getSmarteditConfigurationFacade().delete(key);
	}

	public SmarteditConfigurationFacade getSmarteditConfigurationFacade()
	{
		return smarteditConfigurationFacade;
	}

	public void setSmarteditConfigurationFacade(final SmarteditConfigurationFacade smarteditConfigurationFacade)
	{
		this.smarteditConfigurationFacade = smarteditConfigurationFacade;
	}
}

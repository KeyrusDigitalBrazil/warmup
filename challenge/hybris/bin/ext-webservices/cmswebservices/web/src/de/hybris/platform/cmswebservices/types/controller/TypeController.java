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
package de.hybris.platform.cmswebservices.types.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cmsfacades.types.ComponentTypeFacade;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Controller to deal with component types.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/types")
public class TypeController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TypeController.class);

	@Resource
	private ComponentTypeFacade componentTypeFacade;
	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get all component types", notes = "Find all CMS component types.")
	@ApiResponses(
	{ @ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a list of ComponentTypeData, never", response = ComponentTypeListData.class) })
	public ComponentTypeListData getAllComponentTypes()
	{
		final List<ComponentTypeData> componentTypes = getDataMapper() //
				.mapAsList(getComponentTypeFacade().getAllComponentTypes(), ComponentTypeData.class, null);

		final ComponentTypeListData listDto = new ComponentTypeListData();
		listDto.setComponentTypes(componentTypes);
		return listDto;
	}

	@RequestMapping(method = RequestMethod.GET, params =
	{ "category" })
	@ResponseBody
	@ApiOperation(value = "Get all component types by category", notes = "Find all CMS component types filtered by a given category.")
	@ApiResponses(
	{ @ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a list of ComponentTypeData, never null", response = ComponentTypeListData.class) })
	public ComponentTypeListData getAllComponentTypesByCategory(
			@ApiParam(value = "The component type category of the types to be returned.", required = true)
			@RequestParam(value = "category")
			final String category)
	{
		final List<ComponentTypeData> componentTypes = getDataMapper()
				.mapAsList(getComponentTypeFacade().getAllComponentTypes(category), ComponentTypeData.class, null);

		final ComponentTypeListData listDto = new ComponentTypeListData();
		listDto.setComponentTypes(componentTypes);
		return listDto;
	}

	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get component type by code", notes = "Find a single CMS component types.")
	@ApiResponses(
	{ @ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a ComponentTypeData DTO", response = ComponentTypeData.class),
			@ApiResponse(code = 400, message = "When the code provided does not match any existing type(ComponentTypeNotFoundException).") })
	public ComponentTypeData getComponentTypeByCode(@ApiParam(value = "Component type code", required = true)
	@PathVariable
	final String code) throws ComponentTypeNotFoundException
	{
		return getDataMapper().map(getComponentTypeFacade().getComponentTypeByCode(code), ComponentTypeData.class);
	}

	@RequestMapping(method = RequestMethod.GET, params =
	{ "code", "mode" })
	@ResponseBody
	@ApiOperation(value = "Get component type by code and mode", notes = "Find a single CMS component type by by code and mode.")
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "When the code provided does not match any existing type (ComponentTypeNotFoundException)."),
			@ApiResponse(code = 200, message = "DTO which serves as a wrapper object that contains a ComponentTypeData DTO; or and empty list if the type and mode are not found.", response = ComponentTypeListData.class) })
	public ComponentTypeListData getComponentTypeByCodeAndMode(@RequestParam(value = "code")
	final String code, @RequestParam(value = "mode")
	final String mode) throws ComponentTypeNotFoundException
	{
		final List<ComponentTypeData> componentTypes = new ArrayList<>();
		final ComponentTypeListData componentTypeListData = new ComponentTypeListData();
		try
		{
			componentTypes.add(
					getDataMapper().map(getComponentTypeFacade().getComponentTypeByCodeAndMode(code, mode), ComponentTypeData.class));
		}
		catch (final ComponentTypeNotFoundException e)
		{
			LOGGER.debug("Component Type not found for type code = [" + code + "] and mode  = [" + mode + "]", e);
		}
		componentTypeListData.setComponentTypes(componentTypes);
		return componentTypeListData;
	}

	public ComponentTypeFacade getComponentTypeFacade()
	{
		return componentTypeFacade;
	}

	public void setComponentTypeFacade(final ComponentTypeFacade componentTypeFacade)
	{
		this.componentTypeFacade = componentTypeFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

}

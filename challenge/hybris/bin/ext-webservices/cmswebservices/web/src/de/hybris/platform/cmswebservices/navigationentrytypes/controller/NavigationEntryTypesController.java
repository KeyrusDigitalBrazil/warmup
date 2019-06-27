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
package de.hybris.platform.cmswebservices.navigationentrytypes.controller;

import de.hybris.platform.cmsfacades.navigationentrytypes.NavigationEntryTypesFacade;
import de.hybris.platform.cmswebservices.data.NavigationEntryTypeData;
import de.hybris.platform.cmswebservices.data.NavigationEntryTypeListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;


/**
 * Controller to get the supported Navigation Node Entry Types
 * @deprecated since 1811 - no longer needed
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/navigationentrytypes")
@Deprecated
public class NavigationEntryTypesController
{
	@Resource
	private NavigationEntryTypesFacade navigationEntryTypesFacade;
	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find all navigation entry types", notes = "Find all navigation entry types available.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "The navigation entry types supported", response = NavigationEntryTypeListData.class)
	})
	public NavigationEntryTypeListData findAllNavigationEntryTypes()
	{
		final List<NavigationEntryTypeData> navigationEntries = getDataMapper()
				.mapAsList(getNavigationEntryTypesFacade().getNavigationEntryTypes(), NavigationEntryTypeData.class, null);

		final NavigationEntryTypeListData navigationEntryTypeListData = new NavigationEntryTypeListData();
		navigationEntryTypeListData.setNavigationEntryTypes(navigationEntries);
		return navigationEntryTypeListData;
	}


	protected NavigationEntryTypesFacade getNavigationEntryTypesFacade()
	{
		return navigationEntryTypesFacade;
	}

	public void setNavigationEntryTypesFacade(final NavigationEntryTypesFacade navigationEntryTypesFacade)
	{
		this.navigationEntryTypesFacade = navigationEntryTypesFacade;
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

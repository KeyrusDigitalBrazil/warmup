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
package de.hybris.platform.cmswebservices.restrictions.controller;

import de.hybris.platform.cmsfacades.restrictions.RestrictionFacade;
import de.hybris.platform.cmswebservices.data.RestrictionTypeData;
import de.hybris.platform.cmswebservices.data.RestrictionTypeListData;
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
 * Controller to get restriction types.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/restrictiontypes")
public class RestrictionTypeController
{

	@Resource
	private RestrictionFacade restritionFacade;
	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find all restriction types", notes = "Find all restriction types")
	@ApiResponses({
			@ApiResponse(code = 200, message = "List of restriction types", response = RestrictionTypeListData.class)
	})
	public RestrictionTypeListData findAllRestrictionTypes()
	{
		final List<RestrictionTypeData> restrictionTypes = getDataMapper()
				.mapAsList(getRestritionFacade().findAllRestrictionTypes(), RestrictionTypeData.class, null);

		final RestrictionTypeListData restrictionTypeListData = new RestrictionTypeListData();
		restrictionTypeListData.setRestrictionTypes(restrictionTypes);
		return restrictionTypeListData;
	}

	protected RestrictionFacade getRestritionFacade()
	{
		return restritionFacade;
	}

	public void setRestritionFacade(final RestrictionFacade restritionFacade)
	{
		this.restritionFacade = restritionFacade;
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

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
import de.hybris.platform.b2bcommercefacades.company.B2BCostCenterFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2boccaddon.dto.company.B2BCostCenterListWsDTO;
import de.hybris.platform.b2boccaddon.dto.company.B2BCostCenterWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.access.annotation.Secured;
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
@ApiVersion("v2")
@Api(tags = "B2B Cost Centers")
public class B2BCostCentersController
{
	@Resource(name = "costCenterFacade")
	private B2BCostCenterFacade costCenterFacade;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;


	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@RequestMapping(value = "/{baseSiteId}/costcenters", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Returns a list of B2B cost centers.", notes = "Returns a list of B2B cost centers.")
	@ApiBaseSiteIdParam
	public B2BCostCenterListWsDTO getCostCenters(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final List<? extends B2BCostCenterData> costCenterData = costCenterFacade.getActiveCostCenters();

		final B2BCostCenterListWsDTO dto = new B2BCostCenterListWsDTO();
		dto.setCostCenters(dataMapper.mapAsList(costCenterData, B2BCostCenterWsDTO.class, fields));

		return dto;
	}

	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@RequestMapping(value = "/{baseSiteId}/costcenters/{costCenterId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Returns the specified B2B cost center.", notes = "Returns the specified B2B cost center.")
	@ApiBaseSiteIdParam
	public B2BCostCenterWsDTO getCostCenter(
			@ApiParam(value = "The cost center id.", required = true) @PathVariable final String costCenterId,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final B2BCostCenterData costCenterData = costCenterFacade.getCostCenterDataForCode(costCenterId);

		final B2BCostCenterWsDTO dto = dataMapper.map(costCenterData, B2BCostCenterWsDTO.class, fields);

		return dto;
	}
}

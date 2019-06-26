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
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2boccaddon.dto.order.B2BPaymentTypeListWsDTO;
import de.hybris.platform.b2boccaddon.dto.order.B2BPaymentTypeWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import java.util.List;

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


@Controller
@ApiVersion("v2")
@Api(tags = "B2B Miscs")
public class B2BMiscsController
{
	@Resource(name = "b2bCheckoutFacade")
	private CheckoutFacade checkoutFacade;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@RequestMapping(value = "/{baseSiteId}/paymenttypes", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Returns a list of the available payment types.", notes = "Returns a list of the available payment types in the B2B checkout process.")
	@ApiBaseSiteIdParam
	public B2BPaymentTypeListWsDTO getPaymentTypes(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final List<? extends B2BPaymentTypeData> paymentTypeDatas = checkoutFacade.getPaymentTypes();

		final B2BPaymentTypeListWsDTO dto = new B2BPaymentTypeListWsDTO();
		dto.setPaymentTypes(dataMapper.mapAsList(paymentTypeDatas, B2BPaymentTypeWsDTO.class, fields));

		return dto;
	}
}

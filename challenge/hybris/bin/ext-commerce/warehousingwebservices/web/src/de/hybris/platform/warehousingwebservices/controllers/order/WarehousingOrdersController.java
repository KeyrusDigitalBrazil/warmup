/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousingwebservices.controllers.order;

import de.hybris.platform.warehousingfacades.order.WarehousingConsignmentFacade;
import de.hybris.platform.warehousingfacades.order.WarehousingOrderFacade;
import de.hybris.platform.warehousingwebservices.controllers.WarehousingBaseController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * WebResource exposing {@link WarehousingConsignmentFacade}
 * http://host:port/warehousingwebservices/orders
 */
@Controller
@RequestMapping(value = "/orders")
@Api(value = "/orders", description = "Order's Operations")
public class WarehousingOrdersController extends WarehousingBaseController
{
	@Resource
	private WarehousingOrderFacade warehousingOrderFacade;

	/**
	 * Request to put order on hold
	 *
	 * @param code
	 * 		order's code to be put on hold
	 */
	@RequestMapping(value = "/{code}/on-hold", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Puts an order on hold")
	public void putOrderOnHold(@ApiParam(value = "Order code", required = true) @PathVariable @NotNull final String code)
	{
		warehousingOrderFacade.putOrderOnHold(code);
	}

	/**
	 * Request to re-Source an order
	 *
	 * @param code
	 * 		order's code to be resourced
	 */
	@RequestMapping(value = "/{code}/re-source", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Resources an order")
	public void reSource(@ApiParam(value = "Order code", required = true) @PathVariable @NotNull final String code)
	{
		warehousingOrderFacade.reSource(code);
	}
}

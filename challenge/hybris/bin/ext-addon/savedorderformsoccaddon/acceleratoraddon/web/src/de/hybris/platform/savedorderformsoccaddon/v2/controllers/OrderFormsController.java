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
package de.hybris.platform.savedorderformsoccaddon.v2.controllers;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.savedorderforms.api.orderform.OrderFormFacade;
import de.hybris.platform.savedorderforms.orderform.data.OrderFormData;
import de.hybris.platform.savedorderformsoccaddon.dto.OrderFormListWsDTO;
import de.hybris.platform.savedorderformsoccaddon.dto.OrderFormWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller
@RequestMapping(value = "/{baseSiteId}/orderforms")
@ApiVersion("v2")
public class OrderFormsController
{
	@Resource(name = "orderFormFacade")
	protected OrderFormFacade orderFormFacade;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	/**
	 * Gets a saved Order Form
	 *
	 * @param orderFormCode
	 *           the order form code to be fetched
	 * @return a representation of {@link de.hybris.platform.savedorderformsoccaddon.dto.OrderFormWsDTO}
	 */
	@ResponseBody
	@RequestMapping(value = "/{orderFormCode}", method = RequestMethod.GET)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	public OrderFormWsDTO getOrderFormForCode(@PathVariable final String orderFormCode,
			@RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final OrderFormData orderFormData = orderFormFacade.getOrderFormForCode(orderFormCode);

		final OrderFormWsDTO dto = dataMapper.map(orderFormData, OrderFormWsDTO.class, fields);

		return dto;
	}

	/**
	 * Gets a list of saved Order Form for the current user.
	 *
	 * @return a representation of {@link de.hybris.platform.savedorderformsoccaddon.dto.OrderFormListWsDTO}
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	public OrderFormListWsDTO getOrderFormsForCurrentUser(
			@RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final List<OrderFormData> orderFormDatas = orderFormFacade.getOrderFormsForCurrentUser();

		final OrderFormListWsDTO orderFormListWsDTO = new OrderFormListWsDTO();
		orderFormListWsDTO.setOrderForms(dataMapper.mapAsList(orderFormDatas, OrderFormWsDTO.class, fields));

		return orderFormListWsDTO;
	}

	/**
	 * Creates an Order Form
	 */
	@ResponseStatus(value = HttpStatus.CREATED)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public void createOrderForm(@RequestBody final OrderFormWsDTO orderFormWsDTO)
	{

		final OrderFormData orderFormData = dataMapper.map(orderFormWsDTO, OrderFormData.class,
				"code,description,currency,entries");

		orderFormFacade.createOrderForm(orderFormData);
	}

	/**
	 * Updates an order form.
	 *
	 * @param orderFormCode
	 *           the order form code to be updated
	 * @return a representation of {@link de.hybris.platform.savedorderformsoccaddon.dto.OrderFormWsDTO}
	 */
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{orderFormCode}", method = RequestMethod.PUT, consumes =
	{ MediaType.APPLICATION_JSON_VALUE })
	public OrderFormWsDTO updateOrderForm(@PathVariable final String orderFormCode,
			@RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields,
			@RequestBody(required = true) final OrderFormWsDTO orderFormWsDTO)
	{

		final OrderFormData orderFormData = dataMapper.map(orderFormWsDTO, OrderFormData.class, fields);

		final OrderFormData orderFormDataRet = orderFormFacade.updateOrderForm(orderFormCode, orderFormData);

		return dataMapper.map(orderFormDataRet, OrderFormWsDTO.class, fields);
	}

	/**
	 * Deletes an order form.
	 *
	 * @param orderFormCode
	 *           the order form code to be deleted
	 * @return a representation of {@link de.hybris.platform.savedorderformsoccaddon.dto.OrderFormWsDTO}
	 */
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@RequestMapping(value = "/{orderFormCode}", method = RequestMethod.DELETE)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	public void deleteOrderForm(@PathVariable final String orderFormCode)
	{

		orderFormFacade.removeOrderForm(orderFormCode);

	}

	/**
	 * Adds an order form to the cart.
	 *
	 * @param orderFormCode
	 *           the order form code to be updated
	 * @return a representation of {@link de.hybris.platform.savedorderformsoccaddon.dto.OrderFormWsDTO}
	 */
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{orderFormCode}/cart", method = RequestMethod.POST)
	public void orderForm(@PathVariable final String baseSiteId, @PathVariable final String orderFormCode,
			@RequestParam(defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
			throws CommerceCartModificationException
	{
		orderFormFacade.addOrderFormToCart(orderFormCode, "");
	}

}

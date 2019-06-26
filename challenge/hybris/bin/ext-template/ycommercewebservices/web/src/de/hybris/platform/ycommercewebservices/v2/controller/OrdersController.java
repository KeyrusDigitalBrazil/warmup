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
package de.hybris.platform.ycommercewebservices.v2.controller;

import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoriesData;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.PaymentAuthorizationException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.ycommercewebservices.exceptions.NoCheckoutCartException;
import de.hybris.platform.ycommercewebservices.strategies.OrderCodeIdentificationStrategy;
import de.hybris.platform.ycommercewebservices.v2.helper.OrdersHelper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;



/**
 * Web Service Controller for the ORDERS resource. Most methods check orders of the user. Methods require authentication
 * and are restricted to https channel.
 */


@Controller
@RequestMapping(value = "/{baseSiteId}")
@Api(tags = "Orders")
public class OrdersController extends BaseCommerceController
{
	private static final Logger LOG = Logger.getLogger(OrdersController.class);

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;
	@Resource(name = "orderCodeIdentificationStrategy")
	private OrderCodeIdentificationStrategy orderCodeIdentificationStrategy;
	@Resource(name = "cartLoaderStrategy")
	private CartLoaderStrategy cartLoaderStrategy;
	@Resource(name = "ordersHelper")
	private OrdersHelper ordersHelper;

	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/orders/{code}", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@Cacheable(value = "orderCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'getOrder',#code,#fields)")
	@ResponseBody
	@ApiOperation(nickname = "getOrder", value = "Get a order.", notes = "Returns details of a specific order based on the order GUID (Globally Unique Identifier) or the order CODE. The response contains detailed order information.", authorizations = {
			@Authorization(value = "oauth2_client_credentials") })
	@ApiBaseSiteIdParam
	public OrderWsDTO getOrder(
			@ApiParam(value = "Order GUID (Globally Unique Identifier) or order CODE", required = true) @PathVariable final String code,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final OrderData orderData;
		if (orderCodeIdentificationStrategy.isID(code))
		{
			orderData = orderFacade.getOrderDetailsForGUID(code);
		}
		else
		{
			orderData = orderFacade.getOrderDetailsForCodeWithoutUser(code);
		}

		return getDataMapper().map(orderData, OrderWsDTO.class, fields);
	}


	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/users/{userId}/orders/{code}", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@Cacheable(value = "orderCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,'getOrderForUserByCode',#code,#fields)")
	@ResponseBody
	@ApiOperation(nickname = "getUserOrders", value = "Get a order.", notes = "Returns specific order details based on a specific order code. The response contains detailed order information.")
	@ApiBaseSiteIdAndUserIdParam
	public OrderWsDTO getUserOrders(
			@ApiParam(value = "Order GUID (Globally Unique Identifier) or order CODE", required = true) @PathVariable final String code,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final OrderData orderData = orderFacade.getOrderDetailsForCode(code);
		return getDataMapper().map(orderData, OrderWsDTO.class, fields);
	}



	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@RequestMapping(value = "/users/{userId}/orders", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getUserOrderHistory", value = "Get order history for user.", notes = "Returns order history data for all orders placed by a specified user for a specified base store. The response can display the results across multiple pages, if required.")
	@ApiBaseSiteIdAndUserIdParam
	public OrderHistoryListWsDTO getUserOrderHistory(
			@ApiParam(value = "Filters only certain order statuses. For example, statuses=CANCELLED,CHECKED_VALID would only return orders with status CANCELLED or CHECKED_VALID.") @RequestParam(required = false) final String statuses,
			@ApiParam(value = "The current result page requested.") @RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "The number of results returned per page.") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Sorting method applied to the return results.") @RequestParam(required = false) final String sort,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
			final HttpServletResponse response)
	{
		validateStatusesEnumValue(statuses);

		final OrderHistoryListWsDTO orderHistoryList = ordersHelper.searchOrderHistory(statuses, currentPage, pageSize, sort,
				addPaginationField(fields));

		// X-Total-Count header
		setTotalCountHeader(response, orderHistoryList.getPagination());

		return orderHistoryList;
	}


	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/users/{userId}/orders", method = RequestMethod.HEAD)
	@ResponseBody
	@ApiOperation(nickname = "countUserOrders", value = "Get total number of orders.", notes = "In the response header, the \"x-total-count\" indicates the total number of orders placed by a specified user for a specified base store.")
	@ApiBaseSiteIdAndUserIdParam
	public void countUserOrders(
			@ApiParam(value = "Filters only certain order statuses. For example, statuses=CANCELLED,CHECKED_VALID would only return orders with status CANCELLED or CHECKED_VALID.") @RequestParam(required = false) final String statuses,
			final HttpServletResponse response)
	{
		final OrderHistoriesData orderHistoriesData = ordersHelper.searchOrderHistory(statuses, 0, 1, null);

		setTotalCountHeader(response, orderHistoriesData.getPagination());
	}


	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/users/{userId}/orders", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(nickname = "placeOrder", value = "Place a order.", notes = "Authorizes the cart and places the order. The response contains the new order data.")
	@ApiBaseSiteIdAndUserIdParam
	@SuppressWarnings("squid:S1160")
	public OrderWsDTO placeOrder(
			@ApiParam(value = "Cart code for logged in user, cart GUID for guest checkout", required = true) @RequestParam(required = true) final String cartId, //NOSONAR
			@ApiParam(value = "CCV security code.") @RequestParam(required = false) final String securityCode,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws PaymentAuthorizationException, InvalidCartException, WebserviceValidationException, NoCheckoutCartException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.info("placeOrder");
		}

		cartLoaderStrategy.loadCart(cartId);

		validateCartForPlaceOrder();

		//authorize
		if (!getCheckoutFacade().authorizePayment(securityCode))
		{
			throw new PaymentAuthorizationException();
		}

		//placeorder
		final OrderData orderData = getCheckoutFacade().placeOrder();
		return getDataMapper().map(orderData, OrderWsDTO.class, fields);
	}

}

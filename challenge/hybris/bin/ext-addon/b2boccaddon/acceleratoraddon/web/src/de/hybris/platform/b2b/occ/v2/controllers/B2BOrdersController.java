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

import static de.hybris.platform.util.localization.Localization.getLocalizedString;

import de.hybris.platform.b2b.occ.security.SecuredAccessConstants;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BAcceleratorCheckoutFacade;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.request.mapping.annotation.RequestMappingOverride;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.PaymentAuthorizationException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
@ApiVersion("v2")
@Api(tags = "B2B Orders")
public class B2BOrdersController
{
	private static final String CART_CHECKOUT_TERM_UNCHECKED = "cart.term.unchecked";

	@Resource(name = "userFacade")
	protected UserFacade userFacade;

	@Resource(name = "defaultB2BAcceleratorCheckoutFacade")
	private DefaultB2BAcceleratorCheckoutFacade b2bCheckoutFacade;

	@Resource(name = "b2bCartFacade")
	private CartFacade cartFacade;

	@Resource(name = "cartLoaderStrategy")
	private CartLoaderStrategy cartLoaderStrategy;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "b2BPlaceOrderCartValidator")
	private Validator placeOrderCartValidator;

	@Secured(
	{ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_GUEST,
			SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@RequestMapping(value = "/orders", method = RequestMethod.POST)
	@RequestMappingOverride(priorityProperty = "b2bocc.B2BOrdersController.placeOrder.priority")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiBaseSiteIdAndUserIdParam
	@ApiOperation(value = "Places a B2B Order.", notes = "Places a B2B Order. By default the payment type is ACCOUNT. Please set payment type to CARD if placing an order using credit card.")
	public OrderWsDTO placeOrder(
			@ApiParam(value = "Cart identifier: cart code for logged in user, cart guid for anonymous user, 'current' for the last modified cart", required = true) @RequestParam(required = true) final String cartId,
			@ApiParam(value = "Whether terms were accepted or not.", required = true) @RequestParam(required = true) final boolean termsChecked,
			@ApiParam(value = "Security code for credit card payments.", required = true) @RequestParam(required = false) final String securityCode,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
			throws InvalidCartException, PaymentAuthorizationException
	{

		if (!termsChecked)
		{
			throw new RequestParameterException(getLocalizedString(CART_CHECKOUT_TERM_UNCHECKED));
		}

		if (userFacade.isAnonymousUser())
		{
			throw new AccessDeniedException("Access is denied");
		}

		cartLoaderStrategy.loadCart(cartId);
		final CartData cartData = cartFacade.getCurrentCart();
		validateCart(cartData);
		validateAndAuthorizePayment(securityCode, cartData);

		return dataMapper.map(b2bCheckoutFacade.placeOrder(new PlaceOrderData()), OrderWsDTO.class, fields);
	}

	protected void validateAndAuthorizePayment(final String securityCode, final CartData cartData)
			throws PaymentAuthorizationException
	{
		if (CheckoutPaymentType.CARD.getCode().equals(cartData.getPaymentType().getCode()) && StringUtils.isBlank(securityCode)
				|| !b2bCheckoutFacade.authorizePayment(securityCode))
		{
			throw new PaymentAuthorizationException();
		}
	}

	protected void validateCart(final CartData cartData)
	{
		final Errors errors = new BeanPropertyBindingResult(cartData, "sessionCart");
		placeOrderCartValidator.validate(cartData, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}
}

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
package de.hybris.platform.acceleratorwebservicesaddon.controllers.v2;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorwebservicesaddon.exceptions.PaymentProviderException;
import de.hybris.platform.acceleratorwebservicesaddon.payment.facade.CommerceWebServicesPaymentFacade;
import de.hybris.platform.acceleratorwebservicesaddon.validator.SopPaymentDetailsValidator;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceDataList;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.payment.PaymentRequestWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.payment.SopPaymentDetailsWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceListWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@ApiVersion("v2")
@Api(tags = "Extended Carts")
public class ExtendedCartsController
{
	private final static Logger LOG = Logger.getLogger(ExtendedCartsController.class);

	@Resource(name = "acceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade acceleratorCheckoutFacade;
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;
	@Resource(name = "commerceWebServicesPaymentFacade")
	private CommerceWebServicesPaymentFacade commerceWebServicesPaymentFacade;
	@Resource(name = "userFacade")
	private UserFacade userFacade;
	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;
	@Resource(name = "sopPaymentDetailsValidator")
	private SopPaymentDetailsValidator sopPaymentDetailsValidator;

	@RequestMapping(value = "/{cartId}/consolidate", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getConsolidatedPickupLocations", value = "Get consolidated pickup options.", notes =
			"Returns a list of stores that have all the pick-up items in stock.\n\nNote, if there are no stores "
					+ "that have all the pick up items in stock, or all items are already set to the same pick up location, the response returns an empty list.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public PointOfServiceListWsDTO getConsolidatedPickupLocations(
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final PointOfServiceDataList pointOfServices = new PointOfServiceDataList();
		pointOfServices.setPointOfServices(acceleratorCheckoutFacade.getConsolidatedPickupOptions());
		return dataMapper.map(pointOfServices, PointOfServiceListWsDTO.class, fields);
	}

	@RequestMapping(value = "/{cartId}/consolidate", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(nickname = "createConsolidatedPickupLocation", value = "Handles the consolidating pickup locations.", notes =
			"Specifies one store location where all items will be picked up.\n\nNote, if any of the items are "
					+ "not available at the specified location, these items are removed from the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public CartModificationListWsDTO createConsolidatedPickupLocation(
			@ApiParam(value = "The name of the store where items will be picked up", required = true) @RequestParam(required = true) final String storeName,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
			throws CommerceCartModificationException
	{
		final CartModificationDataList modifications = new CartModificationDataList();
		modifications.setCartModificationList(acceleratorCheckoutFacade.consolidateCheckoutCart(storeName));
		final CartModificationListWsDTO result = dataMapper.map(modifications, CartModificationListWsDTO.class, fields);
		return result;
	}

	@RequestMapping(value = "/{cartId}/payment/sop/request", method = RequestMethod.GET)
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@ApiOperation(nickname = "getSopPaymentRequestDetails", value = "Get information needed for create subscription", notes =
			"Returns the necessary information for creating a subscription that contacts the "
					+ "payment provider directly. This information contains the payment provider URL and a list of parameters that are needed to create the subscription.")
	public PaymentRequestWsDTO getSopPaymentRequestDetails(
			@ApiParam(value = "The URL that the payment provider uses to return payment information. Possible values for responseUrl include the following: “orderPage_cancelResponseURL”, "
					+ "“orderPage_declineResponseURL”, and “orderPage_receiptResponseURL”.", required = true) @RequestParam(required = true) final String responseUrl,
			@ApiParam(value = "Define which url should be returned") @RequestParam(required = false, defaultValue = "false") final boolean extendedMerchantCallback,
			@ApiParam(value = "Base site identifier", required = true) @PathVariable final String baseSiteId,
			@ApiParam(value = "User identifier or one of the literals : 'current' for currently authenticated user, 'anonymous' for anonymous user", required = true) @PathVariable final String userId,
			@ApiParam(value = "Cart identifier: cart code for logged in user, cart guid for anonymous user, 'current' for the last modified cart", required = true) @PathVariable final String cartId,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final PaymentData paymentData = commerceWebServicesPaymentFacade.beginSopCreateSubscription(responseUrl,
				buildMerchantCallbackUrl(extendedMerchantCallback, baseSiteId, userId, cartId));
		final PaymentRequestWsDTO result = dataMapper.map(paymentData, PaymentRequestWsDTO.class, fields);
		return result;
	}

	/**
	 * Method build merchant callback url for given parameters
	 *
	 * @param extendedMerchantCallback
	 *           Define which url should be returned
	 * @param baseSiteId
	 *           Base site identifier
	 * @param userId
	 *           User identifier
	 * @param cartId
	 *           Cart identifier
	 * @return merchant callback url
	 */
	protected String buildMerchantCallbackUrl(final boolean extendedMerchantCallback, final String baseSiteId, final String userId,
			final String cartId)
	{
		if (extendedMerchantCallback)
		{
			return "/v2/" + baseSiteId + "/integration/users/" + userId + "/carts/" + cartId + "/payment/sop/response";
		}
		else
		{
			return "/v2/" + baseSiteId + "/integration/merchant_callback";
		}
	}

	@RequestMapping(value = "/{cartId}/payment/sop/response", method = RequestMethod.POST)
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@ApiOperation(nickname = "doHandleSopPaymentResponse", value = "Handles response from payment provider and create payment details", notes =
			"Handles the response from the payment provider and creates payment details."
			+ "\n\nNote, the “Try it out” button is not enabled for this method (always returns an error) because the Extended Carts Controller handles parameters differently, depending "
					+ "on which payment provider is used. For more information about this controller, please refer to the “acceleratorwebservicesaddon AddOn” documentation on help.hybris.com.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public PaymentDetailsWsDTO doHandleSopPaymentResponse(@ApiIgnore final HttpServletRequest request,
			@ApiIgnore final SopPaymentDetailsWsDTO sopPaymentDetails,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", required = true, allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = "DEFAULT") final String fields)
	{
		final Errors errors = validate(sopPaymentDetails, "SOP data", sopPaymentDetailsValidator);
		final PaymentSubscriptionResultData paymentSubscriptionResultData = commerceWebServicesPaymentFacade
				.completeSopCreateSubscription(getParameterMap(request), sopPaymentDetails.isSavePaymentInfo(), sopPaymentDetails.isDefaultPayment());

		final CCPaymentInfoData paymentInfoData = handlePaymentSubscriptionResultData(paymentSubscriptionResultData, errors);
		if (userFacade.getCCPaymentInfos(true).size() <= 1)
		{
			userFacade.setDefaultPaymentInfo(paymentInfoData);
		}
		checkoutFacade.setPaymentDetails(paymentInfoData.getId());

		return dataMapper.map(paymentInfoData, PaymentDetailsWsDTO.class, fields);
	}

	/**
	 * Method analyze payment subscription result data. If create subscription result is success it returns created payment
	 * info. Otherwise appropriate exception is thrown.
	 *
	 * @param paymentSubscriptionResultData
	 *           Data to analyze
	 * @param errors
	 *           Object storing validation errors. Can be null - then empty error object will be created
	 * @return payment info
	 */
	protected CCPaymentInfoData handlePaymentSubscriptionResultData(
			final PaymentSubscriptionResultData paymentSubscriptionResultData, Errors errors)
	{
		if (paymentSubscriptionResultData.isSuccess() && paymentSubscriptionResultData.getStoredCard() != null
				&& StringUtils.isNotBlank(paymentSubscriptionResultData.getStoredCard().getSubscriptionId()))
		{
			return paymentSubscriptionResultData.getStoredCard();
		}
		else if (paymentSubscriptionResultData.getErrors() != null && !paymentSubscriptionResultData.getErrors().isEmpty())
		{
			SopPaymentDetailsWsDTO sopPaymentDetailsWsDTO = null;
			if (errors == null)
			{
				sopPaymentDetailsWsDTO = new SopPaymentDetailsWsDTO();
				errors = new BeanPropertyBindingResult(sopPaymentDetailsWsDTO, "SOP data");
			}

			for (final PaymentErrorField paymentErrorField : paymentSubscriptionResultData.getErrors().values())
			{
				if (paymentErrorField.isMissing())
				{
					LOG.error("Missing: " + paymentErrorField.getName());
					errors.rejectValue(paymentErrorField.getName(), "field.required", "Please enter a value for this field");
				}
				if (paymentErrorField.isInvalid())
				{
					try
					{
						if (sopPaymentDetailsWsDTO != null)
						{
							PropertyUtils.setProperty(sopPaymentDetailsWsDTO, paymentErrorField.getName(), "invalid");
						}
					}
					catch (final Exception e)
					{
						LOG.error(e.getMessage(), e);
					}
					LOG.error("Invalid: " + paymentErrorField.getName());
					errors.rejectValue(paymentErrorField.getName(), "field.invalid", new Object[]
					{ paymentErrorField.getName() }, "This value is invalid for this field");
				}
			}
			throw new WebserviceValidationException(errors);
		}
		else if (paymentSubscriptionResultData.getDecision() != null
				&& "error".equalsIgnoreCase(paymentSubscriptionResultData.getDecision()))
		{
			LOG.error("Failed to create subscription. Error occurred while contacting external payment services.");
			throw new PaymentProviderException(
					"Failed to create subscription. Decision :" + paymentSubscriptionResultData.getDecision(),
					paymentSubscriptionResultData.getResultCode());
		}
		throw new PaymentProviderException(
				"Failed to create payment details. Decision :" + paymentSubscriptionResultData.getDecision(),
				paymentSubscriptionResultData.getResultCode());
	}

	@RequestMapping(value = "/{cartId}/payment/sop/response", method = RequestMethod.GET)
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@ApiOperation(nickname = "getSopPaymentResponse", value = "Get information about create subscription request results", notes =
			"Returns information related to creating subscription request results. "
			+ "If there is no response from the payment provider, a \"202 Accepted\" status is returned. If the subscription is created successfully, the payment details "
			+ "are returned. Otherwise, an error response is returned.\n\nNote, the “Try it out” button is not enabled for this method (always returns an error) because "
			+ "the Extended Carts Controller handles parameters differently, depending on which payment provider is used. For more information about this controller, please "
					+ "refer to the “acceleratorwebservicesaddon AddOn” documentation on help.hybris.com.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public PaymentDetailsWsDTO getSopPaymentResponse(@PathVariable final String cartId,
			@ApiParam(value = "Response configuration. This is the list of fields that should be returned in the response body.", allowableValues = "BASIC, DEFAULT, FULL") @RequestParam(required = false, defaultValue = "DEFAULT") final String fields,
			@ApiIgnore final HttpServletResponse response)
	{
		final PaymentSubscriptionResultData paymentSubscriptionResultData = commerceWebServicesPaymentFacade
				.getPaymentSubscriptionResult(cartId);
		if (paymentSubscriptionResultData == null) //still waiting for payment provider response
		{
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			return null;
		}

		final CCPaymentInfoData paymentInfoData = handlePaymentSubscriptionResultData(paymentSubscriptionResultData, null);
		return dataMapper.map(paymentInfoData, PaymentDetailsWsDTO.class, fields);
	}

	@RequestMapping(value = "/{cartId}/payment/sop/response", method = RequestMethod.DELETE)
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@ApiOperation(nickname = "removeSopPaymentResponse", value = "Deletes payment provider response related to cart.", notes = "Deletes the payment provider response related to the specified cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void removeSopPaymentResponse(@PathVariable final String cartId)
	{
		commerceWebServicesPaymentFacade.removePaymentSubscriptionResult(cartId);
	}

	protected Map<String, String> getParameterMap(final HttpServletRequest request)
	{
		final Map<String, String> map = new HashMap<>();
		final Enumeration myEnum = request.getParameterNames();
		while (myEnum.hasMoreElements())
		{
			final String paramName = (String) myEnum.nextElement();
			final String paramValue = request.getParameter(paramName);
			map.put(paramName, paramValue);
		}
		return map;
	}

	protected Errors validate(final Object object, final String objectName, final Validator validator)
	{
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
		return errors;
	}

}

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
package de.hybris.platform.cissapdigitalpayment.facade.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentAuthorizationService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.CardType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * SAP Digital Payments specific implementation of the {@link AcceleratorCheckoutFacade} interface extending
 * {@link DefaultAcceleratorCheckoutFacade} that allows to override the creation of payment subscription.
 */

public class DefaultSapDigitalPaymentAcceleratorCheckoutFacade extends DefaultAcceleratorCheckoutFacade
{

	private SapDigitalPaymentAuthorizationService sapDigitalPaymentAuthorizationService;

	private static final Logger LOG = Logger.getLogger(DefaultSapDigitalPaymentAcceleratorCheckoutFacade.class);

	@Override
	public CCPaymentInfoData createPaymentSubscription(final CCPaymentInfoData paymentInfoData)
	{
		validateParameterNotNullStandardMessage("paymentInfoData", paymentInfoData);
		final AddressData billingAddressData = paymentInfoData.getBillingAddress();
		validateParameterNotNullStandardMessage("billingAddress", billingAddressData);

		if (checkIfCurrentUserIsTheCartUser())
		{
			final CardInfo cardInfo = new CardInfo();
			cardInfo.setCardHolderFullName(paymentInfoData.getAccountHolderName());
			cardInfo.setCardNumber(paymentInfoData.getCardNumber());
			final CardType cardType = getCommerceCardTypeService().getCardTypeForCode(paymentInfoData.getCardType());
			cardInfo.setCardType(cardType == null ? null : cardType.getCode());
			cardInfo.setExpirationMonth(Integer.valueOf(paymentInfoData.getExpiryMonth()));
			cardInfo.setExpirationYear(Integer.valueOf(paymentInfoData.getExpiryYear()));
			cardInfo.setIssueNumber(paymentInfoData.getIssueNumber());
			//Adding payment token to cardInfo
			cardInfo.setCardToken(paymentInfoData.getSubscriptionId());

			final BillingInfo billingInfo = new BillingInfo();
			billingInfo.setCity(billingAddressData.getTown());
			billingInfo.setCountry(billingAddressData.getCountry() == null ? null : billingAddressData.getCountry().getIsocode());
			billingInfo.setFirstName(billingAddressData.getFirstName());
			billingInfo.setLastName(billingAddressData.getLastName());
			billingInfo.setEmail(billingAddressData.getEmail());
			billingInfo.setPhoneNumber(billingAddressData.getPhone());
			billingInfo.setPostalCode(billingAddressData.getPostalCode());
			billingInfo.setStreet1(billingAddressData.getLine1());
			billingInfo.setStreet2(billingAddressData.getLine2());

			final CreditCardPaymentInfoModel ccPaymentInfoModel = getCustomerAccountService().createPaymentSubscription(
					getCurrentUserForCheckout(), cardInfo, billingInfo, billingAddressData.getTitleCode(), getPaymentProvider(),
					paymentInfoData.isSaved());
			return ccPaymentInfoModel == null ? null : getCreditCardPaymentInfoConverter().convert(ccPaymentInfoModel);
		}
		return null;
	}

	/**
	 * Authorizes the payment. Delegates the request to SAP Digital payment authorization strategy
	 */
	@Override
	public boolean authorizePayment(final String securityCode)
	{
		LOG.info("Inside authorizePayment method from  DefaultSapDigitalPaymentAcceleratorCheckoutFacade");
		final CartModel cartModel = getCart();
		final CreditCardPaymentInfoModel creditCardPaymentInfoModel = cartModel == null ? null
				: (CreditCardPaymentInfoModel) cartModel.getPaymentInfo();
		if (checkIfCurrentUserIsTheCartUser() && creditCardPaymentInfoModel != null
				&& StringUtils.isNotBlank(creditCardPaymentInfoModel.getSubscriptionId()))
		{
			final CommerceCheckoutParameter parameter = createCommerceCheckoutParameter(cartModel, true);
			parameter.setSecurityCode(securityCode);
			parameter.setPaymentProvider(getPaymentProvider());
			//get the amount authorization strategy and perform authorization
			return getSapDigitalPaymentAuthorizationService().getAuthorisationStrategy().authorizePayment(parameter);
		}
		return false;
	}

	public SapDigitalPaymentAuthorizationService getSapDigitalPaymentAuthorizationService()
	{
		return sapDigitalPaymentAuthorizationService;
	}

	public void setSapDigitalPaymentAuthorizationService(
			final SapDigitalPaymentAuthorizationService sapDigitalPaymentAuthorizationService)
	{
		this.sapDigitalPaymentAuthorizationService = sapDigitalPaymentAuthorizationService;
	}




}

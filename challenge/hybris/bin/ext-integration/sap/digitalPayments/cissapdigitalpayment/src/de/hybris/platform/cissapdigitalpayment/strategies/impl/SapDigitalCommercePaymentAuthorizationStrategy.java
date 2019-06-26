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
package de.hybris.platform.cissapdigitalpayment.strategies.impl;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResultList;
import de.hybris.platform.cissapdigitalpayment.service.CisSapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentAuthorizationService;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentConfigurationStrategy;
import de.hybris.platform.commerceservices.order.CommercePaymentAuthorizationStrategy;
import de.hybris.platform.commerceservices.order.hook.AuthorizePaymentMethodHook;
import de.hybris.platform.commerceservices.order.impl.DefaultCommercePaymentAuthorizationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SAP Digital Payments specific implementation of {@link CommercePaymentAuthorizationStrategy}
 */
public class SapDigitalCommercePaymentAuthorizationStrategy extends DefaultCommercePaymentAuthorizationStrategy
		implements CommercePaymentAuthorizationStrategy
{

	private static final Logger LOG = LoggerFactory.getLogger(SapDigitalCommercePaymentAuthorizationStrategy.class);



	private I18NService i18nService;
	private CisSapDigitalPaymentService cisSapDigitalPaymentService;

	private List<AuthorizePaymentMethodHook> authorizePaymentHooks;
	private ConfigurationService configurationService;

	private SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy;
	private SapDigitalPaymentAuthorizationService sapDigitalPaymentAuthorizationService;

	/**
	 * Authorize the payment amount while placing the order. Calls the SAP DIgital payment client to authorize the
	 * payment, Creates an {@link PaymentTransactionEntryModel} from the authorization result. If the
	 * {@link PaymentTransactionModel}'s transaction status is ACCEPTED or REVIEW, set the payment and order information
	 * to the {@link PaymentTransactionModel}
	 *
	 * @param parameter
	 *           - {@link CommerceCheckoutParameter} containing the cart, payment provider, authorization amount.
	 *
	 * @return {@link PaymentTransactionEntryModel}
	 *
	 */
	@Override
	public PaymentTransactionEntryModel authorizePaymentAmount(final CommerceCheckoutParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();
		final BigDecimal amount = parameter.getAuthorizationAmount();

		//If the SAP digital payment configuration is missing, call the default authorizePaymentAmount method
		if (null == getSapDigitalPaymentConfigurationStrategy().getSapDigitalPaymentConfiguration())
		{
			return super.authorizePaymentAmount(parameter);
		}

		PaymentTransactionEntryModel transactionEntryModel = null;

		final PaymentInfoModel paymentInfo = cartModel.getPaymentInfo();
		if (paymentInfo instanceof CreditCardPaymentInfoModel)
		{

			final CreditCardPaymentInfoModel ccPaymentInfoModel = (CreditCardPaymentInfoModel) paymentInfo;

			final Currency currency = getI18nService().getBestMatchingJavaCurrency(cartModel.getCurrency().getIsocode());
			CisSapDigitalPaymentAuthorizationResultList authorizationResultList = null;

			try
			{

				final CisSapDigitalPaymentAuthorizationRequestList authorizationRequestList = getSapDigitalPaymentAuthorizationService()
						.createAuthorizePaymentRequest(ccPaymentInfoModel.getSubscriptionId(), amount, currency.getCurrencyCode());
				authorizationResultList = getCisSapDigitalPaymentService().authorizePayment(authorizationRequestList).toBlocking()
						.first();
				transactionEntryModel = getSapDigitalPaymentAuthorizationService()
						.processSapDigitalPaymentAuthorizationResult(parameter, authorizationResultList);
			}
			catch (final RuntimeException e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Error while fetching the authorization response from SAP Digital payment [%s]", e));
				}
				LOG.error(
						String.format("Error while fetching the authorization response from SAP Digital payment [%s]", e.getMessage()));
			}

		}
		return transactionEntryModel;
	}



	/**
	 * @return the i18nService
	 */
	@Override
	public I18NService getI18nService()
	{
		return i18nService;
	}

	/**
	 * @param i18nService
	 *           the i18nService to set
	 */
	@Override
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}



	/**
	 * @return the cisSapDigitalPaymentService
	 */
	public CisSapDigitalPaymentService getCisSapDigitalPaymentService()
	{
		return cisSapDigitalPaymentService;
	}

	/**
	 * @param cisSapDigitalPaymentService
	 *           the cisSapDigitalPaymentService to set
	 */
	public void setCisSapDigitalPaymentService(final CisSapDigitalPaymentService cisSapDigitalPaymentService)
	{
		this.cisSapDigitalPaymentService = cisSapDigitalPaymentService;
	}



	/**
	 * @return the authorizePaymentHooks
	 */
	@Override
	public List<AuthorizePaymentMethodHook> getAuthorizePaymentHooks()
	{
		return authorizePaymentHooks;
	}

	/**
	 * @param authorizePaymentHooks
	 *           the authorizePaymentHooks to set
	 */
	@Override
	public void setAuthorizePaymentHooks(final List<AuthorizePaymentMethodHook> authorizePaymentHooks)
	{
		this.authorizePaymentHooks = authorizePaymentHooks;
	}

	/**
	 * @return the configurationService
	 */
	@Override
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Override
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}


	/**
	 * @return the sapDigitalPaymentConfigurationStrategy
	 */
	public SapDigitalPaymentConfigurationStrategy getSapDigitalPaymentConfigurationStrategy()
	{
		return sapDigitalPaymentConfigurationStrategy;
	}

	/**
	 * @param sapDigitalPaymentConfigurationStrategy
	 *           the sapDigitalPaymentConfigurationStrategy to set
	 */
	public void setSapDigitalPaymentConfigurationStrategy(
			final SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy)
	{
		this.sapDigitalPaymentConfigurationStrategy = sapDigitalPaymentConfigurationStrategy;
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

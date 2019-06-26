/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.cissapdigitalpayment.service.impl;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationRequest;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResult;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCard;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentSource;
import de.hybris.platform.cissapdigitalpayment.enums.SapDigitalPaymentAuthTypeEnum;
import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentConfigurationModel;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentAuthorizationService;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentAuthorizationStrategy;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentConfigurationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Implementation for {@link SapDigitalPaymentAuthorizationService}
 */
public class DefaultSapDigitalPaymentAuthorizationService implements SapDigitalPaymentAuthorizationService
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapDigitalPaymentAuthorizationService.class);

	private SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy;
	private Map<SapDigitalPaymentAuthTypeEnum, SapDigitalPaymentAuthorizationStrategy> sapDigitalPaymentAuthorizationStrategyMap;
	private GenerateMerchantTransactionCodeStrategy generateMerchantTransactionCodeStrategy;
	private ModelService modelService;
	private SapDigitalPaymentService sapDigitalPaymentService;



	/**
	 * Determine the authorization strategy class
	 */
	@Override
	public SapDigitalPaymentAuthorizationStrategy getAuthorisationStrategy()
	{
		final SAPDigitalPaymentConfigurationModel sapDpConfig = getSapDigitalPaymentConfigurationStrategy()
				.getSapDigitalPaymentConfiguration();
		if (sapDpConfig.getPaymentAuthType() != null)
		{
			return sapDigitalPaymentAuthorizationStrategyMap.get(sapDpConfig.getPaymentAuthType());
		}
		return sapDigitalPaymentAuthorizationStrategyMap.get(SapDigitalPaymentAuthTypeEnum.DEFAULT);

	}


	/**
	 * Process the authorization result
	 */
	@Override
	public PaymentTransactionEntryModel processSapDigitalPaymentAuthorizationResult(final CommerceCheckoutParameter parameter,
			final CisSapDigitalPaymentAuthorizationResultList authorizationResultList)
	{
		CisSapDigitalPaymentAuthorizationResult authorizationResult = null;


		final CartModel cartModel = parameter.getCart();
		final String paymentProvider = parameter.getPaymentProvider();
		PaymentTransactionEntryModel transactionEntryModel = null;

		//Check if the response received is not empty. If not, take the first from the list
		if (null != authorizationResultList
				&& CollectionUtils.isNotEmpty(authorizationResultList.getCisSapDigitalPaymentAuthorizationResults()))
		{
			authorizationResult = authorizationResultList.getCisSapDigitalPaymentAuthorizationResults().stream().findFirst().get();
		}

		//Generate the merchant transaction code
		final String merchantTransactionCode = getGenerateMerchantTransactionCodeStrategy().generateCode(cartModel);

		try
		{
			transactionEntryModel = getSapDigitalPaymentService().authorize(merchantTransactionCode, paymentProvider,
					cartModel.getDeliveryAddress(), authorizationResult);

		}
		catch (final RuntimeException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Error while creating transaction entry from authorization response received [%s]", e));
			}
			LOG.error(
					String.format("Error while creating transaction entry from authorization response received [%s]", e.getMessage()));
		}
		if (transactionEntryModel != null)
		{
			final PaymentTransactionModel paymentTransaction = transactionEntryModel.getPaymentTransaction();

			//Save the transaction details if status is TransactionStatus.ACCEPTED

			if (TransactionStatus.ACCEPTED.name().equals(transactionEntryModel.getTransactionStatus())
					|| TransactionStatus.REVIEW.name().equals(transactionEntryModel.getTransactionStatus()))
			{
				paymentTransaction.setOrder(cartModel);
				paymentTransaction.setInfo(cartModel.getPaymentInfo());
				getModelService().saveAll(cartModel, paymentTransaction);
			}
			else
			{
				// TransactionStatus is error or reject remove the PaymentTransaction and TransactionEntry
				getModelService().removeAll(Arrays.asList(paymentTransaction, transactionEntryModel));
			}
		}
		return transactionEntryModel;

	}


	/**
	 * Creates the payment authorization request object. SAP Digital payment expects a list of payment authorizations,
	 * wrap the objects around a list and send.
	 *
	 * @param subscriptionId
	 *           - subscription ID
	 * @param amount
	 *           - authorization amount
	 * @param currencyCode
	 *           - currency code
	 *
	 * @return {@link CisSapDigitalPaymentAuthorizationRequestList}
	 */

	@Override
	public CisSapDigitalPaymentAuthorizationRequestList createAuthorizePaymentRequest(final String subscriptionId,
			final BigDecimal amount, final String currencyCode)
	{

		final CisSapDigitalPaymentAuthorizationRequestList authorizationRequestList = new CisSapDigitalPaymentAuthorizationRequestList();
		final CisSapDigitalPaymentAuthorizationRequest authorizationRequest = new CisSapDigitalPaymentAuthorizationRequest();
		final CisSapDigitalPaymentCard paymentCard = new CisSapDigitalPaymentCard();
		try
		{
			paymentCard.setPaytCardByDigitalPaymentSrvc(subscriptionId);
			final CisSapDigitalPaymentSource paymentSource = new CisSapDigitalPaymentSource();
			paymentSource.setCisSapDigitalPaymentCard(paymentCard);

			authorizationRequest.setCisSapDigitalPaymentSource(paymentSource);
			authorizationRequest.setAmountInAuthorizationCurrency(amount.toString());
			authorizationRequest.setAuthorizationCurrency(currencyCode);

			final List<CisSapDigitalPaymentAuthorizationRequest> authorizationRequests = new ArrayList<>();
			authorizationRequests.add(authorizationRequest);
			authorizationRequestList.setCisSapDigitalPaymentAuthorizationRequests(authorizationRequests);
		}
		catch (final RuntimeException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Error while creating the Payment authorization request [%s]", e));
			}
			LOG.error(String.format("Error while creating the Payment authorization request [%s]", e.getMessage()));
		}

		return authorizationRequestList;
	}


	public GenerateMerchantTransactionCodeStrategy getGenerateMerchantTransactionCodeStrategy()
	{
		return generateMerchantTransactionCodeStrategy;
	}


	public void setGenerateMerchantTransactionCodeStrategy(
			final GenerateMerchantTransactionCodeStrategy generateMerchantTransactionCodeStrategy)
	{
		this.generateMerchantTransactionCodeStrategy = generateMerchantTransactionCodeStrategy;
	}


	public ModelService getModelService()
	{
		return modelService;
	}


	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}



	public Map<SapDigitalPaymentAuthTypeEnum, SapDigitalPaymentAuthorizationStrategy> getSapDigitalPaymentAuthorizationStrategyMap()
	{
		return sapDigitalPaymentAuthorizationStrategyMap;
	}


	public void setSapDigitalPaymentAuthorizationStrategyMap(
			final Map<SapDigitalPaymentAuthTypeEnum, SapDigitalPaymentAuthorizationStrategy> sapDigitalPaymentAuthorizationStrategyMap)
	{
		this.sapDigitalPaymentAuthorizationStrategyMap = sapDigitalPaymentAuthorizationStrategyMap;
	}


	public SapDigitalPaymentService getSapDigitalPaymentService()
	{
		return sapDigitalPaymentService;
	}


	public void setSapDigitalPaymentService(final SapDigitalPaymentService sapDigitalPaymentService)
	{
		this.sapDigitalPaymentService = sapDigitalPaymentService;
	}


	public SapDigitalPaymentConfigurationStrategy getSapDigitalPaymentConfigurationStrategy()
	{
		return sapDigitalPaymentConfigurationStrategy;
	}


	public void setSapDigitalPaymentConfigurationStrategy(
			final SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy)
	{
		this.sapDigitalPaymentConfigurationStrategy = sapDigitalPaymentConfigurationStrategy;
	}



}

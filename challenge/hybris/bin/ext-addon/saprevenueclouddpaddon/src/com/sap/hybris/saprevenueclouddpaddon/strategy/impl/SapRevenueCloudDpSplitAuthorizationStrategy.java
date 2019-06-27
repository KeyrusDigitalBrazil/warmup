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
package com.sap.hybris.saprevenueclouddpaddon.strategy.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentAuthorizationService;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentAuthorizationStrategy;
import de.hybris.platform.commerceservices.order.CommercePaymentAuthorizationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSplitOrderService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * 
 * Implementation for authorization amount split between SAP Subscription Billing and S4HANA systems.
 *
 */
public class SapRevenueCloudDpSplitAuthorizationStrategy implements SapDigitalPaymentAuthorizationStrategy {
	
	private static final Logger LOG = LoggerFactory.getLogger(SapRevenueCloudDpSplitAuthorizationStrategy.class);
	
	private SapDigitalPaymentAuthorizationService sapDigitalPaymentAuthorizationService;
	private CommercePaymentAuthorizationStrategy commercePaymentAuthorizationStrategy;
	private SapRevenueCloudSplitOrderService sapRevenueCloudSplitOrderService;
	private ModelService modelService;
	

	@Override
	public boolean authorizePayment(CommerceCheckoutParameter parameter) {
		List<PaymentTransactionEntryModel> paymentTranactionEntryList = new ArrayList<>();
		//Map for target system and authorization amount
		LOG.info("Executing payment authorization split for SAP Digital payments");
		Map<String,BigDecimal> splitAuthorizedPaymentMap = sapRevenueCloudSplitOrderService.getAuthorizationAmountListFromCart(parameter.getCart());
		splitAuthorizedPaymentMap.entrySet().forEach(e -> {
			
			LOG.info(String.format("Setting the authorization amount [%s] for the target system [%s]  ", e.getValue(), e.getKey()));
			parameter.setAuthorizationAmount(e.getValue());
			
			//make actual amount authorization call 
			PaymentTransactionEntryModel transactionEntryModel = commercePaymentAuthorizationStrategy.authorizePaymentAmount(parameter);
			if(null != transactionEntryModel)
			{
				//Set the target system name to the payment transaction entry
				transactionEntryModel.getPaymentTransaction().setTransactionTarget(e.getKey());
				getModelService().save(transactionEntryModel);
				paymentTranactionEntryList.add(transactionEntryModel);
			}
		});
		if(CollectionUtils.isEmpty(paymentTranactionEntryList)) {
			return false;
		}
		return paymentTranactionEntryList.stream().allMatch(e -> TransactionStatus.ACCEPTED.name().equals(e.getTransactionStatus()));

	}
	


	public SapDigitalPaymentAuthorizationService getSapDigitalPaymentAuthorizationService() {
		return sapDigitalPaymentAuthorizationService;
	}



	public void setSapDigitalPaymentAuthorizationService(
			SapDigitalPaymentAuthorizationService sapDigitalPaymentAuthorizationService) {
		this.sapDigitalPaymentAuthorizationService = sapDigitalPaymentAuthorizationService;
	}



	public CommercePaymentAuthorizationStrategy getCommercePaymentAuthorizationStrategy() {
		return commercePaymentAuthorizationStrategy;
	}

	public void setCommercePaymentAuthorizationStrategy(
			CommercePaymentAuthorizationStrategy commercePaymentAuthorizationStrategy) {
		this.commercePaymentAuthorizationStrategy = commercePaymentAuthorizationStrategy;
	}

	public SapRevenueCloudSplitOrderService getSapRevenueCloudSplitOrderService() {
		return sapRevenueCloudSplitOrderService;
	}

	public void setSapRevenueCloudSplitOrderService(SapRevenueCloudSplitOrderService sapRevenueCloudSplitOrderService) {
		this.sapRevenueCloudSplitOrderService = sapRevenueCloudSplitOrderService;
	}



	public ModelService getModelService() {
		return modelService;
	}



	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}


}

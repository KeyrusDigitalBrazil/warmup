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
package de.hybris.platform.acceleratorservices.payment;

import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

import java.util.Map;

/**
 * Defines payment and payment subscription related services
 */
public interface PaymentService
{
	/**
	 * This method generates the Hosted Order Page request data which includes all data required to create a subscription
	 * (customer profile). The request data will be sent to the HOP/SOP URL in a POST method.
	 * 
	 * @param siteName
	 *           - The name of the current CMS site
	 * @param responseUrl
	 *           - The URL used by the CMS site to handle responses from the Hosted Order Page or Silent Order Post.
	 * @param merchantCallbackUrl
	 *           - The URL of the MerchantCallbackController used by the Provider to send validation responses.
	 * @param customerModel
	 *           - The Customer details.
	 * @param cardInfo
	 *           - If available will contain any existing credit card details used to pre-populate the HOP.
	 * @param paymentAddress
	 *           - If available contains the customer's billing address, used to pre-populate the HOP.
	 * @return a {@link PaymentData} object which contains all data required to create a subscription.
	 */
	PaymentData beginHopCreatePaymentSubscription(String siteName, String responseUrl, String merchantCallbackUrl,
			CustomerModel customerModel, CreditCardPaymentInfoModel cardInfo, AddressModel paymentAddress);

	/**
	 * This method generates the Hosted Order Page request data which includes all data required to create a subscription
	 * (customer profile). The request data will be sent to the HOP/SOP URL in a POST method.
	 * 
	 * @param siteName
	 *           - The name of the current CMS site
	 * @param responseUrl
	 *           - The URL used by the CMS site to handle responses from the Hosted Order Page or Silent Order Post.
	 * @param merchantCallbackUrl
	 *           - The URL of the MerchantCallbackController used by the Provider to send validation responses.
	 * @param customerModel
	 *           - The Customer details.
	 * @param cardInfo
	 *           - If available will contain any existing credit card details used to pre-populate the HOP.
	 * @param paymentAddress
	 *           - If available contains the customer's billing address, used to pre-populate the HOP.
	 * @return a {@link PaymentData} object which contains all data required to create a subscription.
	 */
	PaymentData beginSopCreatePaymentSubscription(String siteName, String responseUrl, String merchantCallbackUrl,
			CustomerModel customerModel, CreditCardPaymentInfoModel cardInfo, AddressModel paymentAddress);

	/**
	 * This method creates a subscription internally with the result parameters obtained from the provider's Hosted Order
	 * Page. Only basic customer information and a subscription id is stored internally. All customer information is
	 * stored on the provider's system including credit card number and security code. The subscription id is used for
	 * future lookup for obtaining the customer's credit card details.
	 * 
	 * @param customerModel
	 *           - The Customer details.
	 * @param saveInAccount
	 *           - Flag indicating if this new payment card will be stored in the customer's profile.
	 * @param parameters
	 *           - a Map of key-value paired Strings with the result data returned from the Hosted Order Page.
	 * @return a {@link PaymentSubscriptionResultItem} object which contains the newly created credit card.
	 */
	PaymentSubscriptionResultItem completeHopCreatePaymentSubscription(CustomerModel customerModel, boolean saveInAccount,
			Map<String, String> parameters);

	/**
	 * This method creates a subscription internally with the result parameters obtained from the provider's Silent Order
	 * Post. Only basic customer information and a subscription id is stored internally. All customer information is
	 * stored on the provider's system including credit card number and security code. The subscription id is used for
	 * future lookup for obtaining the customer's credit card details.
	 * 
	 * @param customerModel
	 *           - The Customer details.
	 * @param saveInAccount
	 *           - Flag indicating if this new payment card will be stored in the customer's profile.
	 * @param parameters
	 *           - a Map of key-value paired Strings with the result data returned from the Hosted Order Page.
	 * @return a {@link PaymentSubscriptionResultItem} object which contains the newly created credit card.
	 */
	PaymentSubscriptionResultItem completeSopCreatePaymentSubscription(CustomerModel customerModel, boolean saveInAccount,
			Map<String, String> parameters);

	/**
	 * This method attempts to validate the payment info with a given subscriptionId. Validation should only pass if both
	 * the payment info and the valid subscription exists for this subscriptionId.
	 * 
	 * @param parameters
	 *           - a Map of key-value paired Strings with the result data returned from the callback handler.
	 */
	void handleCreateSubscriptionCallback(Map<String, String> parameters);

	/**
	 * Uses the fraud call back strategy to handle fraud call back
	 * 
	 * @param parameters 
	 * 			a Map of key-value paired Strings with the result data returned from the callback handler
	 */
	void handleFraudUpdateCallback(Map<String, String> parameters);

	/**
	 * This method adds a new PaymentTransactionEntry of type REVIEW_DECISION to the order. It also send event to allow
	 * submitorder proccess to end waitForReviewDecision action.
	 * 
	 * @param reviewDecisionEntry
	 *           - payment transaction entry of REVIEW_DECISION type
	 * @param orderCode
	 *           - identifier of order for which review decision entry should be added
	 * 
	 */
	void setPaymentTransactionReviewResult(PaymentTransactionEntryModel reviewDecisionEntry, String orderCode);
}

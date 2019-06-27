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
package de.hybris.platform.integration.cis.subscription.service;

import de.hybris.platform.cissubscription.data.CisSubscriptionUpdateAction;

import java.util.Date;

import com.hybris.cis.api.subscription.model.CisPaymentMethodUpdateRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionChangeStateRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionCreateRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionPayNowRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionProfileRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionSessionFinalizeRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionSessionInitRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionUpdateRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionUpgradeRequest;
import org.springframework.http.ResponseEntity;


/**
 * Service called from the facade layer which delegates requests from the client to the CIS server
 */
public interface CisSubscriptionService
{
	/**
	 * The methods calls the CIS server in order to create a customer profile.
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param createAccountRequest
	 *           {@link CisSubscriptionProfileRequest}
	 * @return result object from the CIS server
	 *
	 * @since SBG API 5.2
	 */
	ResponseEntity createCustomerProfile(String cisClientRef, CisSubscriptionProfileRequest createAccountRequest);

	/**
	 * Updates a customer account by calling the SBG API method with updated profile data and a existing merchant account
	 * id
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param profileRequest
	 *           {@link CisSubscriptionProfileRequest}
	 * @return result object from the CIS server
	 */
	ResponseEntity updateProfile(final String cisClientRef, final CisSubscriptionProfileRequest profileRequest);


	/**
	 * This methods calls the CIS server so that informations about the order can be delegated to a billing provider to
	 * create a subscription
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param createSubscriptionRequest
	 *           {@link CisSubscriptionCreateRequest}
	 *
	 * @return result object from the CIS server
	 * @since SBG API 5.2
	 */
	ResponseEntity createSubscription(final String cisClientRef, final CisSubscriptionCreateRequest createSubscriptionRequest);


	/**
	 * This methods calls the CIS server in order to replace the payment method for an existing subscription with another
	 * (existing) payment method.
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param merchantSubscriptionId
	 *           id of the subscription to be updated {@link String}
	 * @param merchantPaymentMethodId
	 *           id of the substituting payment method {@link String}
	 * @param effectiveFrom
	 *           when the new payment method should be effective {@link String}
	 *
	 * @return result object from the CIS server
	 */
	ResponseEntity replacePaymentMethod(final String cisClientRef, final String merchantSubscriptionId,
			final String merchantPaymentMethodId, final String effectiveFrom);


	/**
	 * Calls the CIS Service to process the paynow prices of an order at the payment provider
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param payNowRequest
	 *           {@link CisSubscriptionPayNowRequest}
	 * @return result object from the CIS server
	 */
	ResponseEntity processPayNow(String cisClientRef, CisSubscriptionPayNowRequest payNowRequest);


	/**
	 * Calls the CIS Server in order to initialize the Web Session
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param initRequest
	 *           {@link CisSubscriptionSessionInitRequest}
	 * @return result object from the CIS server
	 */
	ResponseEntity initializeTransaction(final String cisClientRef, final CisSubscriptionSessionInitRequest initRequest);

	/**
	 * Calls the CIS Server in order to finalize the Web Session
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param finalizeRequest
	 *           {@link CisSubscriptionSessionFinalizeRequest}
	 * @return result object from the CIS server
	 */
	ResponseEntity finalizeTransaction(final String cisClientRef, final CisSubscriptionSessionFinalizeRequest finalizeRequest);

	/**
	 * Retrieves the URL for the silent order page
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @return url as String
	 */
	ResponseEntity hpfUrl(final String cisClientRef);


	/**
	 * This methods calls the CIS server in order to perform changes on an existing payment method.
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param updatePaymentRequest
	 *           {@link CisPaymentMethodUpdateRequest}
	 *
	 * @return result object from the CIS server
	 */
	ResponseEntity updatePaymentMethod(final String cisClientRef, final CisPaymentMethodUpdateRequest updatePaymentRequest);

	/**
	 * Cancels an existing subscription
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param merchantSubscriptionId
	 *           {@link String}
	 * @param effectiveFrom
	 *           {@link String}
	 * @return result object from the CIS server
	 * @since SBG API 5.2
	 */
	ResponseEntity cancelSubscription(final String cisClientRef, final String merchantSubscriptionId, final String effectiveFrom);

	/**
	 * @param cisClientRef
	 *           {@link String}
	 * @param subscriptionId
	 *           {@link String}
	 * @param force
	 *           boolean flag set in request
	 * @param updateAction
	 *           {@link CisSubscriptionUpdateAction}
	 * @return result object from the CIS server
	 */
	ResponseEntity updateSubscription(final String cisClientRef, final String subscriptionId, final boolean force,
			final CisSubscriptionUpdateAction updateAction);


	/**
	 * The methods calls the CIS server in order to retrieve a customer's profile data (including subscriptions).
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param merchantAccountId
	 *           {@link String}
	 * @return result object from the CIS server
	 */
	ResponseEntity getCustomerProfile(String cisClientRef, String merchantAccountId);

	/**
	 * This methods calls the CIS server in order to upgrade an existing subscription
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param subscriptionUpgradeRequest
	 *           {@link CisSubscriptionUpgradeRequest}
	 *
	 * @return result object from the CIS server
	 */
	ResponseEntity upgradeSubscription(final String cisClientRef, final CisSubscriptionUpgradeRequest subscriptionUpgradeRequest);

	/**
	 * This methods calls the CIS server in order to update an existing subscription
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param subscriptionUpdateRequest
	 *           {@link CisSubscriptionUpdateRequest}
	 *
	 * @return result object from the CIS server
	 */
	ResponseEntity updateSubscription(final String cisClientRef, final CisSubscriptionUpdateRequest subscriptionUpdateRequest);

	/**
	 * This methods calls the CIS server in order to change the status of an existing subscription
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param subscriptionChangeStateRequest
	 *           {@link CisSubscriptionChangeStateRequest}
	 *
	 * @return result object from the CIS server
	 */
	ResponseEntity changeSubscriptionState(final String cisClientRef,
			final CisSubscriptionChangeStateRequest subscriptionChangeStateRequest);

	/**
	 * Returns the billing activity list for the given subscription optionally filtered by date.
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param subscriptionId
	 *           {@link String}
	 * @param fromDate
	 *           {@link String}
	 * @param toDate
	 *           {@link String}
	 * @return result object from the CIS server
	 */
	ResponseEntity getBillingActivityList(final String cisClientRef, final String subscriptionId, final Date fromDate,
			final Date toDate);

	/**
	 * Returns the billing activity detail for the given id.
	 *
	 * @param cisClientRef
	 *           {@link String}
	 * @param billingActivityId
	 *           {@link String}
	 * @return result object from the CIS server
	 */
	ResponseEntity getBillingActivityDetail(final String cisClientRef, final String billingActivityId);

}

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
package de.hybris.platform.integration.cis.subscription.service.impl;

import de.hybris.platform.cissubscription.data.CisSubscriptionUpdateAction;
import de.hybris.platform.integration.cis.subscription.service.CisSubscriptionService;

import java.util.Date;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.api.subscription.model.CisChangePaymentMethodRequest;
import com.hybris.cis.api.subscription.model.CisPaymentMethodUpdateRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionCancelSubscriptionRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionChangeStateRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionCreateRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionOrderPostRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionPayNowRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionProfileRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionReplacePaymentMethodRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionSessionFinalizeRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionSessionInitRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionUpdateRequest;
import com.hybris.cis.api.subscription.model.CisSubscriptionUpgradeRequest;
import com.hybris.cis.client.rest.subscription.SubscriptionClient;
import org.springframework.http.ResponseEntity;


/**
 * Implementing class for delegating requests to the CIS server
 */
public class DefaultCisSubscriptionService implements CisSubscriptionService
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultCisSubscriptionService.class);

	private SubscriptionClient subscriptionClient;


	@Override
	public ResponseEntity createSubscription(final String cisClientRef, final CisSubscriptionCreateRequest createSubscriptionRequest)
	{
		return getSubscriptionClient().createSubscription(cisClientRef, createSubscriptionRequest);
	}

	@Override
	public ResponseEntity replacePaymentMethod(final String cisClientRef, final String merchantSubscriptionId,
			final String merchantPaymentMethodId, final String effectiveFrom)
	{
		return getSubscriptionClient().replaceSubscriptionPaymentMethod(cisClientRef, merchantSubscriptionId,
				merchantPaymentMethodId, effectiveFrom);
	}



	@Override
	public ResponseEntity processPayNow(final String cisClientRef, final CisSubscriptionPayNowRequest payNowRequest)
	{
		return getSubscriptionClient().processPayNow(cisClientRef, payNowRequest);
	}

	@Override
	public ResponseEntity initializeTransaction(final String cisClientRef, final CisSubscriptionSessionInitRequest initRequest)
	{
		return getSubscriptionClient().initializeTransaction(cisClientRef, initRequest);
	}

	@Override
	public ResponseEntity hpfUrl(final String cisClientRef)
	{
		return getSubscriptionClient().hpfUrl(cisClientRef);
	}

	@Override
	public ResponseEntity finalizeTransaction(final String cisClientRef, final CisSubscriptionSessionFinalizeRequest finalizeRequest)
	{
		return getSubscriptionClient().finalizeTransaction(cisClientRef, finalizeRequest);
	}

	@Override
	public ResponseEntity updateProfile(final String cisClientRef, final CisSubscriptionProfileRequest profileRequest)
	{
		return getSubscriptionClient().updateProfile(cisClientRef, profileRequest);
	}

	@Override
	public ResponseEntity updatePaymentMethod(final String cisClientRef, final CisPaymentMethodUpdateRequest updatePaymentRequest)
	{
		return getSubscriptionClient().updatePaymentMethod(cisClientRef, updatePaymentRequest);
	}

	@Override
	public ResponseEntity cancelSubscription(final String cisClientRef, final String merchantSubscriptionId,
			final String effectiveFrom)
	{
		return getSubscriptionClient().cancelSubscription(cisClientRef, merchantSubscriptionId, effectiveFrom);
	}

	@Override
	public ResponseEntity createCustomerProfile(final String cisClientRef, final CisSubscriptionProfileRequest createAccountRequest)
	{
		return getSubscriptionClient().createProfile(cisClientRef, createAccountRequest);
	}

	@Override
	public ResponseEntity getCustomerProfile(final String cisClientRef, final String merchantAccountId)
	{
		return getSubscriptionClient().getProfile(cisClientRef, merchantAccountId);
	}

	protected SubscriptionClient getSubscriptionClient()
	{
		return this.subscriptionClient;
	}

	@Required
	public void setSubscriptionClient(final SubscriptionClient subscriptionClient)
	{
		this.subscriptionClient = subscriptionClient;
	}

	@Override
	public ResponseEntity updateSubscription(final String cisClientRef, final String subscriptionId, final boolean force,
			final CisSubscriptionUpdateAction updateAction)
	{
		if (CisSubscriptionUpdateAction.CANCEL.equals(updateAction))
		{
			return cancelSubscription(cisClientRef, subscriptionId, force ? "NOW" : "on-next-payment");
		}
		throw new NotImplementedException(String.format("Update action %s not yet implemented", updateAction.name()));
	}

	@Override
	public ResponseEntity upgradeSubscription(final String cisClientRef,
			final CisSubscriptionUpgradeRequest subscriptionUpgradeRequest)
	{
		return getSubscriptionClient().upgradeSubscription(cisClientRef, subscriptionUpgradeRequest);
	}

	@Override
	public ResponseEntity updateSubscription(final String cisClientRef, final CisSubscriptionUpdateRequest subscriptionUpdateRequest)
	{
		return getSubscriptionClient().updateSubscription(cisClientRef, subscriptionUpdateRequest);
	}

	@Override
	public ResponseEntity changeSubscriptionState(final String cisClientRef,
			final CisSubscriptionChangeStateRequest subscriptionChangeStateRequest)
	{
		return getSubscriptionClient().changeSubscriptionState(cisClientRef, subscriptionChangeStateRequest);
	}


	@Override
	public ResponseEntity getBillingActivityList(final String cisClientRef, final String subscriptionId, final Date fromDate,
			final Date toDate)
	{
		return getSubscriptionClient().getBillingActivityList(cisClientRef, subscriptionId, fromDate, toDate);
	}


	@Override
	public ResponseEntity getBillingActivityDetail(final String cisClientRef, final String billingActivityId)
	{
		return getSubscriptionClient().getBillingActivityDetail(cisClientRef, billingActivityId);
	}
}

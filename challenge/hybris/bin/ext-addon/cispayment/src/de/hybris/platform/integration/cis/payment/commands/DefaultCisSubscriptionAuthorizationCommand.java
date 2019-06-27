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
package de.hybris.platform.integration.cis.payment.commands;

import javax.ws.rs.core.UriBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hybris.cis.client.payment.models.CisPaymentAuthorization;
import com.hybris.cis.client.payment.models.CisPaymentTransactionResult;
import com.hybris.cis.client.shared.exception.AbstractCisServiceException;
import com.hybris.cis.client.shared.exception.ServiceRequestException;
import com.hybris.cis.client.shared.exception.codes.ServiceExceptionDetail;
import com.hybris.cis.service.CisClientPaymentService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.integration.cis.payment.converter.CisAuthorizationResultConverter;
import de.hybris.platform.integration.cis.payment.converter.SubscriptionAuthorizationRequestConverter;
import de.hybris.platform.payment.commands.SubscriptionAuthorizationCommand;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation of SubscriptionAuthorizationCommand using CIS web services to call the payment provider.
 */
public class DefaultCisSubscriptionAuthorizationCommand implements SubscriptionAuthorizationCommand
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCisSubscriptionAuthorizationCommand.class);

	private CisClientPaymentService cisClientPaymentService;
	private SubscriptionAuthorizationRequestConverter subscriptionAuthorizationRequestConverter;
	private CisAuthorizationResultConverter cisAuthorizationResultConverter;

	@Override
	public AuthorizationResult perform(final SubscriptionAuthorizationRequest request)
	{
		try
		{
			final CisPaymentAuthorization cisRequest = getSubscriptionAuthorizationRequestConverter().convert(request);
			final UriBuilder uriBuilder = UriBuilder.fromUri(request.getSubscriptionID());
			final CisPaymentTransactionResult authorization = getCisClientPaymentService()
					.authorizeWithProfile(request.getMerchantTransactionCode(), Registry.getCurrentTenant().getTenantID(), uriBuilder.build(), cisRequest);
			return getCisAuthorizationResultConverter().convert(authorization);
		}
		catch (final ServiceRequestException e)
		{
			final Map<Integer, String> errors = extractErrorCodes(e);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Authorization errors " + errors);
			}
			return createAuthorizationResult(TransactionStatusDetails.AUTHORIZATION_REJECTED_BY_PSP,
					TransactionStatus.REJECTED);
		}
	}

	protected AuthorizationResult createAuthorizationResult(final TransactionStatusDetails transactionStatusDetails,
			final TransactionStatus transactionStatus)
	{
		final AuthorizationResult authorizationResult = new AuthorizationResult();
		authorizationResult.setTransactionStatusDetails(transactionStatusDetails);
		authorizationResult.setTransactionStatus(transactionStatus);
		return authorizationResult;
	}

	protected Map<Integer, String> extractErrorCodes(final AbstractCisServiceException failedExecutionException)
	{
		final List<ServiceExceptionDetail> errorCodes = failedExecutionException.getErrorCodes();
		final Map<Integer, String> errorCodeMap = new HashMap<Integer, String>(errorCodes.size());
		for (final ServiceExceptionDetail errorCode : errorCodes)
		{
			errorCodeMap.put(Integer.valueOf(errorCode.getCode()), errorCode.getMessage());
		}
		return errorCodeMap;
	}

	public CisClientPaymentService getCisClientPaymentService()
	{
		return cisClientPaymentService;
	}

	@Required
	public void setCisClientPaymentService(final CisClientPaymentService cisClientPaymentService)
	{
		this.cisClientPaymentService = cisClientPaymentService;
	}

	protected SubscriptionAuthorizationRequestConverter getSubscriptionAuthorizationRequestConverter()
	{
		return subscriptionAuthorizationRequestConverter;
	}

	@Required
	public void setSubscriptionAuthorizationRequestConverter(
			final SubscriptionAuthorizationRequestConverter subscriptionAuthorizationRequestConverter)
	{
		this.subscriptionAuthorizationRequestConverter = subscriptionAuthorizationRequestConverter;
	}

	protected CisAuthorizationResultConverter getCisAuthorizationResultConverter()
	{
		return cisAuthorizationResultConverter;
	}

	@Required
	public void setCisAuthorizationResultConverter(final CisAuthorizationResultConverter cisAuthorizationResultConverter)
	{
		this.cisAuthorizationResultConverter = cisAuthorizationResultConverter;
	}
}

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
package de.hybris.platform.integration.cis.payment.strategies.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.hybris.charon.RawResponse;
import com.hybris.cis.client.payment.models.CisExternalPaymentRequest;
import com.hybris.cis.client.payment.models.CisPaymentProfileResult;
import com.hybris.cis.client.shared.exception.AbstractCisServiceException;
import com.hybris.cis.client.shared.exception.codes.ServiceExceptionDetail;
import com.hybris.cis.service.CisClientPaymentService;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorservices.payment.enums.DecisionsEnum;
import de.hybris.platform.acceleratorservices.payment.strategies.PaymentResponseInterpretationStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.impl.AbstractPaymentResponseInterpretationStrategy;
import de.hybris.platform.core.Registry;
import de.hybris.platform.integration.cis.payment.constants.CispaymentConstants;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * CIS specific payment-profile-creation response interpretation strategy.
 */
public class CisPaymentResponseInterpretationStrategy extends AbstractPaymentResponseInterpretationStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(CisPaymentResponseInterpretationStrategy.class);

	private CisClientPaymentService cisClientPaymentService;
	private Converter<CisPaymentProfileResult, CreateSubscriptionResult> cisCreateSubscriptionResultConverter;
	private PaymentResponseInterpretationStrategy paymentResponseInterpretationStrategy;

	@Override
	public CreateSubscriptionResult interpretResponse(final Map<String, String> responseParams, final String clientRef,
			final Map<String, PaymentErrorField> errors)
	{

		responseParams.put("VerifyTransactionSignature()", "true");
		final CisExternalPaymentRequest externalPaymentRequest = new CisExternalPaymentRequest(responseParams);

		// there where errors in the payment form, skip the rest call.
		if ("REJECT".equalsIgnoreCase(responseParams.get("decision")))
		{
			return getPaymentResponseInterpretationStrategy().interpretResponse(responseParams, clientRef, errors);
		}
		else
		{
			try
			{
				final CreateSubscriptionResult createSubscriptionResult = new CreateSubscriptionResult();
				final RawResponse<CisPaymentProfileResult> rawResponse = getCisClientPaymentService()
						.addCustomerProfile(clientRef, Registry.getCurrentTenant().getTenantID(), StringUtils.EMPTY, externalPaymentRequest);
				CisPaymentProfileResult cisPaymentProfileResult = rawResponse.content().toBlocking().single();
				Optional<String> href = rawResponse.header("location");
				if (href.isPresent())
				{
					cisPaymentProfileResult.setHref(href.get());
				}
				return getCisCreateSubscriptionResultConverter().convert(cisPaymentProfileResult, createSubscriptionResult);
			}
			catch (final AbstractCisServiceException exception)
			{
				final CreateSubscriptionResult createSubscriptionResult = new CreateSubscriptionResult();
				createSubscriptionResult.setReasonCode(CispaymentConstants.GENERAL_PAYMENT_ERR_CODE);
				createSubscriptionResult.setDecision(DecisionsEnum.REJECT.name());
				final List<ServiceExceptionDetail> errorCodes = exception.getErrorCodes();
				final Map<Integer, String> errorCodeMap = new HashMap<Integer, String>(errorCodes.size());
				for (final ServiceExceptionDetail errorCode : errorCodes)
				{
					errorCodeMap.put(Integer.valueOf(errorCode.getCode()), errorCode.getMessage());
				}
				createSubscriptionResult.setErrors(errorCodeMap);
				return createSubscriptionResult;
			}
		}
	}

	protected CisClientPaymentService getCisClientPaymentService()
	{
		return cisClientPaymentService;
	}

	@Required
	public void setCisClientPaymentService(final CisClientPaymentService cisClientPaymentService)
	{
		this.cisClientPaymentService = cisClientPaymentService;
	}

	protected Converter<CisPaymentProfileResult, CreateSubscriptionResult> getCisCreateSubscriptionResultConverter()
	{
		return cisCreateSubscriptionResultConverter;
	}

	@Required
	public void setCisCreateSubscriptionResultConverter(
			final Converter<CisPaymentProfileResult, CreateSubscriptionResult> cisCreateSubscriptionResultConverter)
	{
		this.cisCreateSubscriptionResultConverter = cisCreateSubscriptionResultConverter;
	}

	protected PaymentResponseInterpretationStrategy getPaymentResponseInterpretationStrategy()
	{
		return paymentResponseInterpretationStrategy;
	}

	@Required
	public void setPaymentResponseInterpretationStrategy(
			final PaymentResponseInterpretationStrategy paymentResponseInterpretationStrategy)
	{
		this.paymentResponseInterpretationStrategy = paymentResponseInterpretationStrategy;
	}
}

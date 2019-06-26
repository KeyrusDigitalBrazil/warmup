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
package de.hybris.platform.integration.cis.payment.converter;

import java.util.Currency;
import java.util.Date;
import com.hybris.cis.client.payment.models.CisPaymentRequest;
import com.hybris.cis.client.payment.models.CisPaymentTransactionResult;
import com.hybris.cis.client.shared.models.CisDecision;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;


public class CisAuthorizationResultConverter
{
	public AuthorizationResult convert(final CisPaymentTransactionResult cisPaymentTransactionResult)
	{
		final AuthorizationResult authorizationResult = new AuthorizationResult();
		authorizationResult.setAuthorizationCode(cisPaymentTransactionResult.getId());
		authorizationResult.setAuthorizationTime(new Date());
		authorizationResult.setAvsStatus(null);//TODO
		CisPaymentRequest request = cisPaymentTransactionResult.getRequest();
		authorizationResult.setCurrency(Currency.getInstance(request.getCurrency()));
		authorizationResult.setCvnStatus(null);
		authorizationResult.setMerchantTransactionCode(cisPaymentTransactionResult.getClientAuthorizationId());
		authorizationResult.setPaymentProvider("cisCybersource");
		authorizationResult.setReconciliationId(null);
		authorizationResult.setRequestId(cisPaymentTransactionResult.getHref());
		authorizationResult.setRequestToken(null);
		authorizationResult.setTotalAmount(cisPaymentTransactionResult.getAmount());
		authorizationResult.setTransactionStatus(convertCisDecisionToTransactionStatus(cisPaymentTransactionResult.getDecision()));
		authorizationResult.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE);//TODO
		authorizationResult.setMerchantTransactionCode(cisPaymentTransactionResult.getClientAuthorizationId());
		return authorizationResult;
	}

	private TransactionStatus convertCisDecisionToTransactionStatus(final CisDecision cisDecision)
	{
		if (CisDecision.ACCEPT.equals(cisDecision))
		{
			return TransactionStatus.ACCEPTED;
		}
		else if (CisDecision.ERROR.equals(cisDecision))
		{
			return TransactionStatus.ERROR;
		}
		else if (CisDecision.REJECT.equals(cisDecision))
		{
			return TransactionStatus.REJECTED;
		}
		else if (CisDecision.REVIEW.equals(cisDecision))
		{
			return TransactionStatus.REVIEW;
		}
		else
		{
			throw new IllegalArgumentException("unknown cisDecision for authorization : " + cisDecision.toString());
		}
	}
}

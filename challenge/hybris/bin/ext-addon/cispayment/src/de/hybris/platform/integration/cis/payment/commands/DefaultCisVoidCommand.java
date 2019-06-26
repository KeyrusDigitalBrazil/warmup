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

import de.hybris.platform.payment.commands.VoidCommand;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import java.util.Date;


public class DefaultCisVoidCommand implements VoidCommand
{
	@Override
	public VoidResult perform(final VoidRequest voidRequest)
	{
		final VoidResult result = new VoidResult();
		result.setRequestTime(new Date());
		result.setTransactionStatus(TransactionStatus.REJECTED);
		result.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE);
		result.setMerchantTransactionCode(voidRequest.getMerchantTransactionCode());
		result.setRequestId(voidRequest.getRequestId());
		return result;
	}
}

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
package de.hybris.platform.payment.commands.impl;

import de.hybris.platform.payment.commands.StandaloneRefundCommand;
import de.hybris.platform.payment.commands.request.StandaloneRefundRequest;
import de.hybris.platform.payment.commands.result.RefundResult;

import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;

import java.util.Date;


/**
 * A mockup Implementation for {@link StandaloneRefundCommand}.
 */
public class StandaloneRefundMockCommand extends GenericMockCommand implements
		StandaloneRefundCommand<StandaloneRefundRequest>
{
	@Override
	public RefundResult perform(final StandaloneRefundRequest request)
	{
		final RefundResult result = new RefundResult();

        // Let's be as much polite as possible and return the requested data where it makes sense
        result.setCurrency( request.getCurrency() );
        result.setTotalAmount( request.getTotalAmount() );
        result.setRequestTime( new Date() );

        // And the most important
		result.setTransactionStatus( TransactionStatus.ACCEPTED );
		result.setTransactionStatusDetails( TransactionStatusDetails.SUCCESFULL );

        genericPerform( request, result );

		return result;
	}
}

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

import de.hybris.platform.payment.commands.DeleteSubscriptionCommand;
import de.hybris.platform.payment.commands.request.DeleteSubscriptionRequest;
import de.hybris.platform.payment.commands.result.SubscriptionResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;


/**
 * A mockup Implementation for {@link DeleteSubscriptionCommand}. The implementation allows to run tests of the payment
 * module without actually paying a dime for it.
 */
public class DeleteSubscriptionMockCommand extends GenericMockCommand implements DeleteSubscriptionCommand
{

	@Override
	public SubscriptionResult perform(final DeleteSubscriptionRequest request)
	{
		final SubscriptionResult result = new SubscriptionResult();

		result.setSubscriptionID(request.getSubscriptionID());

		// And the most important
		result.setTransactionStatus(TransactionStatus.ACCEPTED);
		result.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL);

		genericPerform(request, result);

		return result;
	}
}

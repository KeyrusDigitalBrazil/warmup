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
package de.hybris.platform.chinesepspalipayservices.dao;

import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;

import java.util.List;


/**
 * Looks up items related to {@link List<AlipayPaymentTransactionEntryModel>}
 */
public interface AlipayPaymentTransactionEntryDao
{

	/**
	 * Gets the list of AlipayPaymentTransactionEntries by the given PaymentTransactionEntry type, transaction status and
	 * AlipayPaymentTransaction
	 *
	 * @param capture
	 *           PaymentTransactionEntry type: CAPTURE,CANCEL and so on
	 * @param status
	 *           PaymentTransactionStatus: ACCEPTED,REJECTED and so on
	 * @param alipayPaymentTransaction
	 *           Alipay Payment Transaction
	 * @return list of AlipayPaymentTransactionEntries
	 */
	List<AlipayPaymentTransactionEntryModel> findPaymentTransactionEntryByTypeAndStatus(
			final PaymentTransactionType capture,
			final TransactionStatus status, final AlipayPaymentTransactionModel alipayPaymentTransaction);

}


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
package de.hybris.platform.chinesepspalipayservices.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.chinesepspalipayservices.dao.AlipayPaymentTransactionEntryDao;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Default implementation of {@link AlipayPaymentTransactionEntryDao}
 */
public class DefaultAlipayPaymentTransactionEntryDao extends DefaultGenericDao<AlipayPaymentTransactionEntryModel>
		implements AlipayPaymentTransactionEntryDao
{
	private static final Logger LOG = Logger.getLogger(DefaultAlipayPaymentTransactionDao.class);

	private static final String FIND_ENTRY_BY_TYPE_STATUS_QUERY = "SELECT {" + AlipayPaymentTransactionEntryModel.PK + "} FROM {"
			+ AlipayPaymentTransactionEntryModel._TYPECODE + "} " + "WHERE {" + AlipayPaymentTransactionEntryModel.TRANSACTIONSTATUS
			+ "}=?transactionstatus " + "AND {" + AlipayPaymentTransactionEntryModel.PAYMENTTRANSACTION + "}=?transaction " + "AND {"
			+ AlipayPaymentTransactionEntryModel.TYPE + "} in ({{ select {pk} from {" + PaymentTransactionType._TYPECODE
			+ "} where {code}=?type }}) ORDER BY {" + AlipayPaymentTransactionEntryModel.TIME + "} DESC";

	private static final String TRANSACTION_STATUS = "transactionstatus";
	private static final String TYPE = "type";
	private static final String PAYMENT_TRANSACTION = "transaction";

	public DefaultAlipayPaymentTransactionEntryDao()
	{
		super(AlipayPaymentTransactionEntryModel._TYPECODE);
	}


	@Override
	public List<AlipayPaymentTransactionEntryModel> findPaymentTransactionEntryByTypeAndStatus(final PaymentTransactionType capture,
			final TransactionStatus status, final AlipayPaymentTransactionModel alipayPaymentTransaction)
	{
		validateParameterNotNull(capture, "PaymentTransactionType cannot be null");
		validateParameterNotNull(status, "status cannot be null");
		validateParameterNotNull(alipayPaymentTransaction, "AlipayPaymentTransaction cannot be null");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_ENTRY_BY_TYPE_STATUS_QUERY);

		query.addQueryParameter(TRANSACTION_STATUS, status.name());
		query.addQueryParameter(TYPE, capture.name());
		query.addQueryParameter(PAYMENT_TRANSACTION, alipayPaymentTransaction.getPk().getLongValueAsString());

		final List<AlipayPaymentTransactionEntryModel> entries = getFlexibleSearchService()
				.<AlipayPaymentTransactionEntryModel> search(query).getResult();

		if (entries == null)
		{
			LOG.error("There is no Alipay payment transaction entry with type: " + capture + " and status: " + status);
			return Collections.emptyList();
		}

		return entries;
	}





}

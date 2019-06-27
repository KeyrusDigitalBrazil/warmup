/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapinvoiceaddon.document.dao.impl;

import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.sap.sapinvoiceaddon.document.dao.B2BInvoiceDao;
import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 */
public class B2BInvoiceDaoImpl extends DefaultGenericDao<B2BDocumentModel> implements B2BInvoiceDao
{
	public B2BInvoiceDaoImpl()
	{
		super("B2BDocument");
	}

	@Override
	public SapB2BDocumentModel findInvoiceByDocumentNumber(final String invoiceDocumentNumber)
	{

		final Map attr = new HashMap(1);
		attr.put("invoiceDocumentNumber", invoiceDocumentNumber);
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT {invdoc:pk} from { ").append("SapB2BDocument")
				.append(" as invdoc} WHERE {invdoc:documentNumber} = ?invoiceDocumentNumber ");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		final SearchResult result = getFlexibleSearchService().search(query);
		final List b2bDocuments = result.getResult();
		return ((b2bDocuments.isEmpty()) ? null : (SapB2BDocumentModel) b2bDocuments.get(0));
	}

}

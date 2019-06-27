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
package de.hybris.platform.accountsummaryaddon.document.dao;

import de.hybris.platform.servicelayer.search.SearchResult;

import de.hybris.platform.accountsummaryaddon.model.B2BDocumentPaymentInfoModel;


public interface B2BDocumentPaymentInfoDao
{

	/**
	 * Gets a list of document payments associated to a Document.
	 * 
	 * @param documentNumber
	 *           the document number identification.
	 * @return list of documentPaymentInfos
	 */
	public SearchResult<B2BDocumentPaymentInfoModel> getDocumentPaymentInfo(final String documentNumber);

}

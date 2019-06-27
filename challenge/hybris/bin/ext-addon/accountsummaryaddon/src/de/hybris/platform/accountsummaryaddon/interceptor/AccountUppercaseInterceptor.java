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
package de.hybris.platform.accountsummaryaddon.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;


/**
 * 
 * Interceptor that formats a document number to uppercase.
 * 
 */
public class AccountUppercaseInterceptor implements PrepareInterceptor
{


	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException
	{

		final B2BDocumentModel doc = (B2BDocumentModel) model;


		if (doc.getDocumentNumber() != null)
		{
			doc.setDocumentNumber(doc.getDocumentNumber().toUpperCase());
		}


	}

}

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
package de.hybris.platform.sap.sapcpicustomerexchange.service.impl;

import de.hybris.platform.sap.sapcpiadapter.clients.SapCpiCustomerClient;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiCustomer;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOAuthService;
import de.hybris.platform.sap.sapcpicustomerexchange.service.SapCpiCustomerService;

import org.springframework.beans.factory.annotation.Required;

import rx.Completable;

/**
 * DefaultSapCpiCustomerService
 */
public class DefaultSapCpiCustomerService implements SapCpiCustomerService
{

	private SapCpiCustomerClient sapCpiCustomerClient;
	private SapCpiOAuthService sapCpiOAuthService;


	@Override
	public Completable createCustomer(final SapCpiCustomer customer)
	{
		return getSapCpiOAuthService().getToken().flatMap(
				token -> getSapCpiCustomerClient().createCustomer(new StringBuilder("Bearer ").append(token).toString(), customer))
				.toCompletable();
	}

	protected SapCpiCustomerClient getSapCpiCustomerClient()
	{
		return sapCpiCustomerClient;
	}

	@Required
	public void setSapCpiCustomerClient(SapCpiCustomerClient sapCpiCustomerClient)
	{
		this.sapCpiCustomerClient = sapCpiCustomerClient;
	}

	protected SapCpiOAuthService getSapCpiOAuthService()
	{
		return sapCpiOAuthService;
	}

	@Required
	public void setSapCpiOAuthService(SapCpiOAuthService sapCpiOAuthService)
	{
		this.sapCpiOAuthService = sapCpiOAuthService;
	}

}

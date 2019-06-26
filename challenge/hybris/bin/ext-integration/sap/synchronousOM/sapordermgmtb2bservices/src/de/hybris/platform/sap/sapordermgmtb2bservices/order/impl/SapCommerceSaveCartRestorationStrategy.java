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
package de.hybris.platform.sap.sapordermgmtb2bservices.order.impl;

import de.hybris.platform.commerceservices.order.CommerceCartRestoration;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceSaveCartRestorationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.store.services.BaseStoreService;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class SapCommerceSaveCartRestorationStrategy extends DefaultCommerceSaveCartRestorationStrategy
{

	private BaseStoreService baseStoreService;



	protected boolean isSyncOrdermgmtEnabled()
	{
		return (getBaseStoreService().getCurrentBaseStore().getSAPConfiguration() != null)
				&& (getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().isSapordermgmt_enabled());


	}

	/**
	 * @return the baseStoreService
	 */
	@Override
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Override
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	@Override
	public CommerceCartRestoration restoreCart(final CommerceCartParameter parameters) throws CommerceCartRestorationException
	{

		if (isSyncOrdermgmtEnabled())
		{
			return new CommerceCartRestoration();
		}

		return super.restoreCart(parameters);


	}
}

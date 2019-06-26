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
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartRestorationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class SapCommerceCartRestorationStrategy extends DefaultCommerceCartRestorationStrategy
{


	private BaseStoreService baseStoreService;
	private BackendAvailabilityService backendAvailabilityService;

	/**
	 * @return the backendAvailabilityService
	 */
	public BackendAvailabilityService getBackendAvailabilityService()
	{
		return backendAvailabilityService;
	}

	/**
	 * @param backendAvailabilityService
	 *           the backendAvailabilityService to set
	 */
	@Required
	public void setBackendAvailabilityService(final BackendAvailabilityService backendAvailabilityService)
	{
		this.backendAvailabilityService = backendAvailabilityService;
	}

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
	
	/**
	 * @return Is Backend down?
	 */
	private boolean isBackendDown()
	{
		return backendAvailabilityService.isBackendDown();
	}

	@Override
	public CommerceCartRestoration restoreCart(final CommerceCartParameter parameter) throws CommerceCartRestorationException
	{
		if (isSyncOrdermgmtEnabled() && !isBackendDown())
		{
			return new CommerceCartRestoration();
		}
		return super.restoreCart(parameter);
	}

}

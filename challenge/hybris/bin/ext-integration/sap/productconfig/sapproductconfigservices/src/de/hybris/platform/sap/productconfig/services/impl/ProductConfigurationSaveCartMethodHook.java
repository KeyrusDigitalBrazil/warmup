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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.hook.CommerceSaveCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationSavedCartCleanUpStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * Hook to remove the product link from configuration to the product in the session cart. It can be enabled for a site
 * by appending the site uid to the property productconfig.services.commercesavecart.sessioncart.hook.enabled.{siteUid},
 * by default it is not enabled.
 */


public class ProductConfigurationSaveCartMethodHook implements CommerceSaveCartMethodHook
{
	private ConfigurationSavedCartCleanUpStrategy cleanUpStrategy;

	@Override
	public void beforeSaveCart(final CommerceSaveCartParameter parameters) throws CommerceSaveCartException
	{
		cleanUpStrategy.cleanUpCart();
	}

	@Override
	public void afterSaveCart(final CommerceSaveCartParameter parameters, final CommerceSaveCartResult saveCartResult)
			throws CommerceSaveCartException
	{
		// nothing to do
	}

	protected ConfigurationSavedCartCleanUpStrategy getCleanUpStrategy()
	{
		return cleanUpStrategy;
	}

	@Required
	public void setCleanUpStrategy(final ConfigurationSavedCartCleanUpStrategy cleanUpStrategy)
	{
		this.cleanUpStrategy = cleanUpStrategy;
	}

}

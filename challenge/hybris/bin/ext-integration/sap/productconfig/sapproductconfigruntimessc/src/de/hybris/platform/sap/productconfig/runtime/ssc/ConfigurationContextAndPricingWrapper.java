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
package de.hybris.platform.sap.productconfig.runtime.ssc;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

import java.util.Hashtable;
import java.util.Map;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingException;


/**
 * Defines pricing and context relevant interactions with SSC configuration an pricing engine.
 */
public interface ConfigurationContextAndPricingWrapper
{
	/**
	 * Prepares pricing context.
	 *
	 * @param session
	 *           SSC session
	 * @param configId
	 *           configuration Id
	 * @param kbKey
	 *           knowledge base data
	 * @throws InteractivePricingException
	 */
	void preparePricingContext(final IConfigSession session, final String configId, final KBKey kbKey)
			throws InteractivePricingException;

	/**
	 * Starts price calculation is configuration and pricing engine and put retrieved prices to the configuration model.
	 *
	 * @param session
	 *           SSC session
	 * @param configId
	 *           configuration Id
	 * @param configModel
	 *           configuration model
	 * @throws InteractivePricingException
	 */
	void processPrice(final IConfigSession session, String configId, final ConfigModel configModel)
			throws InteractivePricingException;

	/**
	 * Retrieves configuration context. We have {@link Hashtable} here instead of the preferred {@link Map} as SSC needs
	 * it for its map representing the configuration context
	 *
	 * @param kbKey
	 *           knowledge base data
	 * @return the configuration context
	 */
	Hashtable<String, String> retrieveConfigurationContext(KBKey kbKey);
}

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
package de.hybris.platform.sap.sapproductconfigsomservices.prodconf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * Handles product configuration for cart entries: Maintains the session attributes, converts an external representation
 * into a runtime object and vice versa.
 */
public interface ProductConfigurationSomService
{



	/**
	 * Sets ID of Product Configuration session into hybris session to enable UI for reconfiguration
	 * 
	 * @param itemKey
	 *           Key of cart item. Corresponds to the {@link BusinessObject#getHandle()} attribute of the BOL item, or to
	 *           the cart entry PK of the hybris cart in case the backend is down
	 * @param configId
	 *           ID of the configuration runtime object. The configurator is capable of accessing the session via this ID
	 */
	void setIntoSession(String itemKey, String configId);

	/**
	 * To check if key is in session
	 * 
	 * @param itemKey
	 *           Key of cart item. Corresponds to the {@link BusinessObject#getHandle()} attribute of the BOL item, or to
	 *           the cart entry PK of the hybris cart in case the backend is down
	 * @return Is a configuration is already available in the hybris session?
	 */
	boolean isInSession(String itemKey);

	/**
	 * To get external configuration.
	 * @param itemKey
	 *           Key of cart item. Corresponds to the {@link BusinessObject#getHandle()} attribute of the BOL item, or to
	 *           the cart entry PK of the hybris cart in case the backend is down
	 * @return The external configuration, supposed the configuration is available in the hybris session
	 */
	String getExternalConfiguration(String itemKey);

	/**
	 * Creates a configuration runtime representation from a product and an external configuration.
	 * 
	 * @param productCode
	 *           Product ID
	 * @param externalConfiguration
	 *           external configuration as XML string
	 * @return Configuration runtime representation
	 */
	ConfigModel getConfigModel(String productCode, String externalConfiguration);

	/**
	 * Returns the configuration total price, supposed the configuration ID is available in the hybris session.
	 * 
	 * @param itemKey
	 *           Key of cart item. Corresponds to the {@link BusinessObject#getHandle()} attribute of the BOL item, or to
	 *           the cart entry PK of the hybris cart in case the backend is down
	 * @return Total price
	 */
	Double getTotalPrice(String itemKey);

	/**
	 * Returns the configuration ID, supposed it is available in the hybris session
	 * 
	 * @param itemKey item key
	 * @return Configuration ID
	 */
	String getGetConfigId(String itemKey);

}

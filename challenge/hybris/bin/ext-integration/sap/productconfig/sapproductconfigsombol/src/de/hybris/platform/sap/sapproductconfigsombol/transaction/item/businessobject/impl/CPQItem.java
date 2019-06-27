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
package de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.Date;


/**
 * Represents the backend's view of the configurable items of a shopping basket.
 */
public interface CPQItem extends Item
{

	/**
	 * Return product configuration representation
	 *
	 * @return Product configuration
	 */
	ConfigModel getProductConfiguration();

	/**
	 * States that the configuration is dirty, i.e. needs to be sent to the backend
	 *
	 * @param productConfigurationDirty
	 */
	void setProductConfigurationDirty(boolean productConfigurationDirty);

	/**
	 * Sets product configuration
	 *
	 * @param configModel
	 */
	void setProductConfiguration(ConfigModel configModel);



	/**
	 * Do we need to send the product configuration to the back end?
	 *
	 * @return Configuration is dirty
	 */
	boolean isProductConfigurationDirty();



	/**
	 * @param kbDate
	 */
	void setKbDate(Date kbDate);

	/**
	 * @return Date of used for configuring the item
	 */
	Date getKbDate();

	/**
	 * Sets external representation of configuration
	 *
	 * @param externalConfiguration
	 */
	void setExternalConfiguration(Configuration externalConfiguration);

	/**
	 * @return External representation of product configuration
	 */
	Configuration getExternalConfiguration();


}

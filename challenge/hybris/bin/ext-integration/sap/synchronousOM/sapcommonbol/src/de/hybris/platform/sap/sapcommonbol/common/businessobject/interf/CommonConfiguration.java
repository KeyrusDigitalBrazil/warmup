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
package de.hybris.platform.sap.sapcommonbol.common.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DistChannelMapping;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DistChannelMappingKey;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DivisionMapping;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DivisionMappingKey;


/**
 * Provides common configuration settings of the application like e.g. the sales area. Only those attributes should be
 * here which are relevant for many modules.
 * 
 */
public interface CommonConfiguration extends BusinessObject
{

	/**
	 * @return the distribution channel
	 */
	String getDistributionChannel();

	/**
	 * Sets the distribution channel
	 * 
	 * @param arg
	 *           distribution channel
	 */
	void setDistributionChannel(String arg);

	/**
	 * Returns the division
	 * 
	 * @return division
	 */
	String getDivision();

	/**
	 * Sets the division
	 * 
	 * @param arg
	 *           division
	 */
	void setDivision(String arg);

	/**
	 * Returns the sales organisation
	 * 
	 * @return salesOrganisation
	 */
	String getSalesOrganisation();

	/**
	 * Sets the sales organisation
	 * 
	 * @param arg
	 *           sales organisation
	 */
	void setSalesOrganisation(String arg);

	/**
	 * Returns the currency
	 * 
	 * @return currency
	 */
	String getCurrency();

	/**
	 * Sets the currency
	 * 
	 * @param currency
	 */
	void setCurrency(String currency);

	/**
	 * Fetching WEC debug attribute. Note that this is not read from a configuration but set into this bean at runtime,
	 * depending on the context. <br>
	 * Available e.g. in sales context. <br>
	 * In the UI layer, one should derive this setting from the runtime instead.
	 * 
	 * @return WEC debug attribute
	 */
	boolean getWecDebug();

	/**
	 * Sets the WEC debug parameter from the UI layer. *
	 * 
	 * @param wecDebug
	 */
	void setWecDebug(boolean wecDebug);

	/**
	 * Gets the back end mapping for the sales org and division.<br>
	 * 
	 * @param key
	 *           the includes the sales org and division
	 * @return the mapping data, alternative division for condition, customer, document type
	 */
	DivisionMapping getDivisionMapping(DivisionMappingKey key);

	/**
	 * Gets the back end mapping for the sales org and distribution channel.<br>
	 * 
	 * @param key
	 *           the includes the sales org and distribution channel
	 * @return the mapping data, alternative division for condition, customer/material, document type
	 */
	DistChannelMapping getDistChannelMapping(DistChannelMappingKey key);

	/**
	 * Factory-method to create a mapping key.<br>
	 * 
	 * @param originalSalesOrg
	 *           sales organisation
	 * @param originalDistChannel
	 *           distribution channel
	 * @return key for distribution channel mapping
	 */
	DistChannelMappingKey getDistChannelMappingKey(String originalSalesOrg, String originalDistChannel);

	/**
	 * Factory-method to create a mapping key.<br>
	 * 
	 * @param originalSalesOrg
	 *           sales organisation
	 * @param originalDivison
	 *           division
	 * @return key for division mapping
	 */
	DivisionMappingKey getDivisionMappingKey(String originalSalesOrg, String originalDivison);
}

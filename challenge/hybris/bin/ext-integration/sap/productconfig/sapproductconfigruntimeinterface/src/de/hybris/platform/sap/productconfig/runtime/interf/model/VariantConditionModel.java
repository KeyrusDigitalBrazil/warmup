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
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import java.math.BigDecimal;


/**
 * Represents the variant condition model including key and factor.
 */
public interface VariantConditionModel
{
	/**
	 * @return the key
	 */
	String getKey();

	/**
	 * @param key
	 *           the key to set
	 */
	void setKey(String key);

	/**
	 * @return the factor
	 */
	BigDecimal getFactor();

	/**
	 * @param factor
	 *           the factor to set
	 */
	void setFactor(BigDecimal factor);

	/**
	 * Creates a copy of this VariantConditionModel object
	 * 
	 * @return copy of variant condition
	 */
	VariantConditionModel copy();
}

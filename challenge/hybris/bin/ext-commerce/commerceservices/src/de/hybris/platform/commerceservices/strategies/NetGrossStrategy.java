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
package de.hybris.platform.commerceservices.strategies;



/**
 * Interface for strategy, which allows for overriding the default behavior to retrieve the net/gross setting.
 * 
 * @spring.bean netGrossStrategy
 */
public interface NetGrossStrategy
{
	/**
	 * Method for retrieving the net/gross setting
	 * 
	 * @return the net/gross setting to be used for retrieving price information
	 */
	boolean isNet();
}

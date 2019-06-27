/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.service;

import java.util.List;

import com.hybris.merchandising.model.Strategy;


/**
 * StrategyService is a service for retrieving a list of configured {@link Strategy} entities to be rendered within
 * SmartEdit.
 */
public interface StrategyService
{
	/**
	 * getStrategies returns a list of {@link Strategy} objects for the configured tenant.
	 * 
	 * @param pageNumber - number of pages to include in the response
	 * @param pageSize - number of data in a single page
	 * @return List of {@link Strategy} - list of strategy
	 */
	List<Strategy> getStrategies(Integer pageNumber, Integer pageSize);

	/**
	 * getStrategy return the {@link Strategy} for the id
	 * 
	 * @param id - identifier of the strategy
	 * @return Strategy - a Strategy for the id provided
	 */
	Strategy getStrategy(String id);
}

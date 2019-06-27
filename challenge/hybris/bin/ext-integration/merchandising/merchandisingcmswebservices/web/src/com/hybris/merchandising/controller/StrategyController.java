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
package com.hybris.merchandising.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hybris.merchandising.dto.DropdownElement;
import com.hybris.merchandising.model.Strategy;
import com.hybris.merchandising.service.StrategyService;

import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;

/**
 * StrategyController is a simple REST controller exposing an end point to allow
 * us to retrieve the configured Strategies for a given tenant.
 */
@RestController
@IsAuthorizedCmsManager
public class StrategyController {
	@Autowired
	protected StrategyService strategyService;

	/**
	 * Retrieves a list of configured {@link Strategy} objects from Strategy
	 * service.
	 *
	 * @param currentPage - optional page number (e.g. 1).
	 * @param pageSize - optional page size (e.g. 10).
	 * @return a list of configured {@link Strategy}.
	 */
	@RequestMapping(value = "/v1/{siteId}/strategies", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, List<DropdownElement>> getStrategies(
			@RequestParam(value = "currentPage", defaultValue = "0", required = false) Integer currentPage,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize)
	{
		final Map<String, List<DropdownElement>> strategies = new HashMap<>();
		if(currentPage == null) {
			currentPage = Integer.valueOf(0);
		}
		if(pageSize == null) {
			pageSize = Integer.valueOf(10);
		}

		strategies.put("options", strategyService.getStrategies(currentPage + 1, pageSize)
				.stream()
				.filter(Objects::nonNull)
				.map(strategy -> new DropdownElement(strategy.getId(), strategy.getName()))
				.collect(Collectors.toList()));
		return strategies;
	}

	@RequestMapping(value = "/v1/{siteId}/strategies/{id}", method = RequestMethod.GET)
	@ResponseBody
	public DropdownElement getStrategy(@PathVariable final String id)
	{
		final Strategy strategy = strategyService.getStrategy(id);
		if(strategy !=null)
		{
			return new DropdownElement(strategy.getId(), strategy.getName());
		}
		return new DropdownElement("", "");
	}
}

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
package de.hybris.platform.acceleratorservices.process.strategies.impl;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * Default strategy to impersonate site and initialize session context from the process model. It resolves the correct
 * strategy to use based on the BusinessProcessModel type and delegates execution to it.
 */
public class DefaultProcessContextResolutionStrategy implements ProcessContextResolutionStrategy<BaseSiteModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultProcessContextResolutionStrategy.class);

	private Map<Class<?>, ProcessContextResolutionStrategy<BaseSiteModel>> processStrategyMap;

	@Override
	public void initializeContext(final BusinessProcessModel businessProcessModel)
	{
		final Optional<ProcessContextResolutionStrategy<BaseSiteModel>> strategy = getStrategy(businessProcessModel);
		if (strategy.isPresent())
		{
			strategy.get().initializeContext(businessProcessModel);
		}
		else
		{
			LOG.warn("Attempt to initialize process context for business process [{}] failed, no strategy found",
					businessProcessModel.getCode());
		}
	}

	@Override
	public CatalogVersionModel getContentCatalogVersion(final BusinessProcessModel businessProcessModel)
	{
		return getStrategy(businessProcessModel)
				.map(contextStrategy -> contextStrategy.getContentCatalogVersion(businessProcessModel)).orElse(null);
	}

	@Override
	public BaseSiteModel getCmsSite(final BusinessProcessModel businessProcessModel)
	{
		return getStrategy(businessProcessModel).map(contextStrategy -> contextStrategy.getCmsSite(businessProcessModel))
				.orElse(null);
	}

	protected Optional<ProcessContextResolutionStrategy<BaseSiteModel>> getStrategy(
			final BusinessProcessModel businessProcessModel)
	{
		final ProcessContextResolutionStrategy<BaseSiteModel> strategy = getProcessStrategyMap().get(businessProcessModel.getClass());
		return (strategy != null) ? Optional.of(strategy) :
				getProcessStrategyMap().entrySet()
						.stream()
						.filter(e -> e.getKey().isAssignableFrom(businessProcessModel.getClass()))
						.findFirst()
						.map(Map.Entry::getValue);
	}

	protected Map<Class<?>, ProcessContextResolutionStrategy<BaseSiteModel>> getProcessStrategyMap()
	{
		return processStrategyMap;
	}

	@Required
	public void setProcessStrategyMap(final Map<Class<?>, ProcessContextResolutionStrategy<BaseSiteModel>> processStrategyMap)
	{
		this.processStrategyMap = processStrategyMap;
	}

}

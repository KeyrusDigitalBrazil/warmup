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

import static junit.framework.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontProcessModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * Test class for DefaultProcessContextResolutionStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProcessContextResolutionStrategyTest
{
	@Mock
	private ProcessContextResolutionStrategy<BaseSiteModel> quoteContextStrategy;

	@Mock
	private ProcessContextResolutionStrategy<BaseSiteModel> orderContextStrategy;

	@Mock
	private ProcessContextResolutionStrategy<BaseSiteModel> storeFrontContextStrategy;

	@Mock
	private ProcessContextResolutionStrategy<BaseSiteModel> storeFrontCustomerContextStrategy;

	private StoreFrontCustomerProcessModel storeFrontCustomerProcessModel = new StoreFrontCustomerProcessModel();

	@Mock
	private QuoteProcessModel quoteProcessModel;

	@Mock
	private BusinessProcessModel businessProcessModel;

	@Mock
	private CatalogVersionModel catalogVersionModel;

	@Mock
	private BaseSiteModel baseSiteModel;

	private Map<Class<?>, ProcessContextResolutionStrategy<BaseSiteModel>> processStrategyMap;

	@Mock
	private ProcessContextResolutionStrategy<BaseSiteModel> defaultProcessContextStrategy;

	@InjectMocks
	private DefaultProcessContextResolutionStrategy contextResolutionStrategy = new DefaultProcessContextResolutionStrategy();

	@Before
	public void setUp() throws Exception
	{
		given(quoteProcessModel.getItemtype()).willReturn(QuoteProcessModel._TYPECODE);
		given(businessProcessModel.getItemtype()).willReturn(BusinessProcessModel._TYPECODE);
	}

	@Test
	public void initializeShouldNotFailWhenStrategyNotFound() throws Exception
	{
		contextResolutionStrategy.setProcessStrategyMap(new HashMap<>());

		contextResolutionStrategy.initializeContext(businessProcessModel);
	}

	@Test
	public void getContentCatalogVersionShouldNotFailWhenStrategyNotFound() throws Exception
	{
		contextResolutionStrategy.setProcessStrategyMap(new HashMap<>());

		Assertions.assertThat(contextResolutionStrategy.getContentCatalogVersion(businessProcessModel)).isNull();
	}

	@Test
	public void getCmsShouldNotFailWhenStrategyNotFound() throws Exception
	{
		contextResolutionStrategy.setProcessStrategyMap(new HashMap<>());

		Assertions.assertThat(contextResolutionStrategy.getCmsSite(businessProcessModel)).isNull();
	}

	@Test
	public void testShouldDelegateToMatchingStrategyFromTheMap() throws Exception
	{
		processStrategyMap = new HashMap<>();
		processStrategyMap.put(QuoteProcessModel.class, quoteContextStrategy);
		processStrategyMap.put(OrderProcessModel.class, orderContextStrategy);
		contextResolutionStrategy.setProcessStrategyMap(processStrategyMap);

		contextResolutionStrategy.initializeContext(quoteProcessModel);

		verify(quoteContextStrategy, atLeastOnce()).initializeContext(quoteProcessModel);
		verify(orderContextStrategy, never()).initializeContext(any());
	}

	@Test
	public void shouldReturnStrategyMappedToSupperTypeWhenExactTypeNotPresent() throws Exception
	{
		processStrategyMap = new HashMap<>();
		processStrategyMap.put(QuoteProcessModel.class, quoteContextStrategy);
		processStrategyMap.put(OrderProcessModel.class, orderContextStrategy);
		processStrategyMap.put(StoreFrontProcessModel.class, storeFrontContextStrategy);
		contextResolutionStrategy.setProcessStrategyMap(processStrategyMap);

		final Optional<ProcessContextResolutionStrategy<BaseSiteModel>> resultStrategy = contextResolutionStrategy
				.getStrategy(storeFrontCustomerProcessModel);

		assertSame(storeFrontContextStrategy, resultStrategy.get());
	}

	@Test
	public void shouldReturnStrategyMappedWhenPresentAvoidingSupperType() throws Exception
	{
		processStrategyMap = new HashMap<>();
		processStrategyMap.put(StoreFrontProcessModel.class, storeFrontContextStrategy);
		processStrategyMap.put(QuoteProcessModel.class, quoteContextStrategy);
		processStrategyMap.put(OrderProcessModel.class, orderContextStrategy);
		processStrategyMap.put(StoreFrontCustomerProcessModel.class, storeFrontCustomerContextStrategy);
		contextResolutionStrategy.setProcessStrategyMap(processStrategyMap);
		final Optional<ProcessContextResolutionStrategy<BaseSiteModel>> resultStrategy = contextResolutionStrategy
				.getStrategy(storeFrontCustomerProcessModel);
		assertSame(storeFrontCustomerContextStrategy, resultStrategy.get());
	}
}

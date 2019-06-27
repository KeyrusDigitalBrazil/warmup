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
package de.hybris.platform.adaptivesearch.strategies.impl;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchProfileModel;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileActivationMapping;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;


@UnitTest
public class DefaultAsSearchProfileRegistryTest
{
	private DefaultAsSearchProfileRegistry asSearchProfileRegistry;

	@Mock
	private ApplicationContext applicationContext;

	private DefaultAsSearchProfileMapping simpleSearchProfileMapping;
	private DefaultAsSearchProfileMapping categoryAwareSearchProfileMapping;
	private DefaultAsSearchProfileActivationMapping searchProfileActivationMapping1;
	private DefaultAsSearchProfileActivationMapping searchProfileActivationMapping2;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		asSearchProfileRegistry = new DefaultAsSearchProfileRegistry();
		asSearchProfileRegistry.setApplicationContext(applicationContext);

		asSearchProfileRegistry.afterPropertiesSet();

	}

	protected void initializeSearchProfileMappings()
	{
		final Map<String, DefaultAsSearchProfileMapping> beans = new HashMap<>();

		simpleSearchProfileMapping = new DefaultAsSearchProfileMapping();
		simpleSearchProfileMapping.setType(AsSimpleSearchProfileModel.class.getName());

		categoryAwareSearchProfileMapping = new DefaultAsSearchProfileMapping();
		categoryAwareSearchProfileMapping.setType(AsCategoryAwareSearchProfileModel.class.getName());

		beans.put("simpleSearchProfileMapping", simpleSearchProfileMapping);
		beans.put("categoryAwareSearchProfileMapping", categoryAwareSearchProfileMapping);

		when(applicationContext.getBeansOfType(DefaultAsSearchProfileMapping.class)).thenReturn(beans);

		asSearchProfileRegistry.afterPropertiesSet();
	}

	@Test
	public void getSimpleSearchProfileMapping()
	{
		//given
		initializeSearchProfileMappings();
		final AsSimpleSearchProfileModel searchProfile = new AsSimpleSearchProfileModel();

		//when
		final AsSearchProfileMapping mapping = asSearchProfileRegistry.getSearchProfileMapping(searchProfile);

		//then
		assertSame(mapping, simpleSearchProfileMapping);
	}

	@Test
	public void getCategoryAwareSearchProfileMapping()
	{
		//given
		initializeSearchProfileMappings();
		final AsCategoryAwareSearchProfileModel searchProfile = new AsCategoryAwareSearchProfileModel();

		//when
		final AsSearchProfileMapping mapping = asSearchProfileRegistry.getSearchProfileMapping(searchProfile);

		//then
		assertSame(mapping, categoryAwareSearchProfileMapping);
	}

	@Test
	public void getEmptySearchProfileMappings()
	{
		//when
		final Map<String, AsSearchProfileMapping> mappings = asSearchProfileRegistry.getSearchProfileMappings();

		//then
		assertNotNull(mappings);
		assertEquals(0, mappings.size());
	}

	@Test
	public void getSearchProfileMappings()
	{
		//given
		initializeSearchProfileMappings();

		//when
		final Map<String, AsSearchProfileMapping> mappings = asSearchProfileRegistry.getSearchProfileMappings();

		//then
		assertNotNull(mappings);
		assertEquals(2, mappings.size());
		assertThat(mappings.values(), hasItems(simpleSearchProfileMapping, categoryAwareSearchProfileMapping));
	}

	@Test
	public void getEmptySearchProfileActivationMappings()
	{
		//when
		final List<AsSearchProfileActivationMapping> mappings = asSearchProfileRegistry.getSearchProfileActivationMappings();

		//then
		assertNotNull(mappings);
		assertEquals(0, mappings.size());
	}

	@Test
	public void getSearchProfileActivationMappings1()
	{
		//given
		final Map<String, DefaultAsSearchProfileActivationMapping> beans = new HashMap<>();

		searchProfileActivationMapping1 = new DefaultAsSearchProfileActivationMapping();
		searchProfileActivationMapping1.setPriority(100);

		searchProfileActivationMapping2 = new DefaultAsSearchProfileActivationMapping();
		searchProfileActivationMapping2.setPriority(90);

		beans.put("searchProfileActivationMapping1", searchProfileActivationMapping1);
		beans.put("searchProfileActivationMapping2", searchProfileActivationMapping2);

		when(applicationContext.getBeansOfType(DefaultAsSearchProfileActivationMapping.class)).thenReturn(beans);

		asSearchProfileRegistry.afterPropertiesSet();

		//when
		final List<AsSearchProfileActivationMapping> mappings = asSearchProfileRegistry.getSearchProfileActivationMappings();

		//then
		assertNotNull(mappings);
		assertEquals(2, mappings.size());
		assertSame(mappings.get(0), searchProfileActivationMapping1);
		assertSame(mappings.get(1), searchProfileActivationMapping2);
	}

	@Test
	public void getSearchProfileActivationMappings2()
	{
		//given
		final Map<String, DefaultAsSearchProfileActivationMapping> beans = new HashMap<>();

		searchProfileActivationMapping1 = new DefaultAsSearchProfileActivationMapping();
		searchProfileActivationMapping1.setPriority(90);

		searchProfileActivationMapping2 = new DefaultAsSearchProfileActivationMapping();
		searchProfileActivationMapping2.setPriority(100);

		beans.put("searchProfileActivationMapping1", searchProfileActivationMapping1);
		beans.put("searchProfileActivationMapping2", searchProfileActivationMapping2);

		when(applicationContext.getBeansOfType(DefaultAsSearchProfileActivationMapping.class)).thenReturn(beans);

		asSearchProfileRegistry.afterPropertiesSet();

		//when
		final List<AsSearchProfileActivationMapping> mappings = asSearchProfileRegistry.getSearchProfileActivationMappings();

		//then
		assertNotNull(mappings);
		assertEquals(2, mappings.size());
		assertSame(mappings.get(0), searchProfileActivationMapping2);
		assertSame(mappings.get(1), searchProfileActivationMapping1);
	}
}

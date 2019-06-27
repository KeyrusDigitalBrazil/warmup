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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.data.AbstractAsConfiguration;
import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileResult;
import de.hybris.platform.adaptivesearch.enums.AsBoostItemsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsBoostRulesMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsFacetsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsSortsMergeMode;
import de.hybris.platform.adaptivesearch.util.ConfigurationUtils;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAsSearchProfileResultFactoryTest
{
	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private AbstractAsConfiguration asConfiguration;

	@Mock
	private AbstractAsConfiguration asReplacedConfiguration;

	@Mock
	private Object data;

	private DefaultAsSearchProfileResultFactory asSearchProfileResultFactory;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		when(configurationService.getConfiguration()).thenReturn(configuration);

		asSearchProfileResultFactory = new DefaultAsSearchProfileResultFactory();
		asSearchProfileResultFactory.setConfigurationService(configurationService);
	}

	@Test
	public void create()
	{
		// given
		when(configuration.getString(ConfigurationUtils.DEFAULT_FACETS_MERGE_MODE, AsFacetsMergeMode.ADD_AFTER.name()))
				.thenReturn(AsFacetsMergeMode.ADD_AFTER.name());
		when(configuration.getString(ConfigurationUtils.DEFAULT_BOOST_ITEMS_MERGE_MODE, AsBoostItemsMergeMode.ADD_AFTER.name()))
				.thenReturn(AsBoostItemsMergeMode.ADD_AFTER.name());
		when(configuration.getString(ConfigurationUtils.DEFAULT_BOOST_RULES_MERGE_MODE, AsBoostRulesMergeMode.ADD.name()))
				.thenReturn(AsBoostRulesMergeMode.ADD.name());
		when(configuration.getString(ConfigurationUtils.DEFAULT_SORTS_MERGE_MODE, AsSortsMergeMode.ADD_AFTER.name()))
				.thenReturn(AsSortsMergeMode.ADD_AFTER.name());

		// when
		final AsSearchProfileResult result = asSearchProfileResultFactory.createResult();

		// then
		assertNotNull(result);
		assertEquals(AsFacetsMergeMode.ADD_AFTER, result.getFacetsMergeMode());
		assertNotNull(result.getPromotedFacets());
		assertNotNull(result.getFacets());
		assertNotNull(result.getExcludedFacets());
		assertEquals(AsBoostItemsMergeMode.ADD_AFTER, result.getBoostItemsMergeMode());
		assertNotNull(result.getPromotedItems());
		assertNotNull(result.getExcludedItems());
		assertEquals(AsBoostRulesMergeMode.ADD, result.getBoostRulesMergeMode());
		assertNotNull(result.getBoostRules());
		assertEquals(AsSortsMergeMode.ADD_AFTER, result.getSortsMergeMode());
		assertNotNull(result.getPromotedSorts());
		assertNotNull(result.getSorts());
		assertNotNull(result.getExcludedSorts());
	}

	@Test
	public void createConfigurationHolder1()
	{
		// when
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder = asSearchProfileResultFactory
				.createConfigurationHolder(asConfiguration);

		// then
		assertEquals(asConfiguration, configurationHolder.getConfiguration());
		assertThat(configurationHolder.getReplacedConfigurations()).isNotNull().isEmpty();
		assertEquals(0, configurationHolder.getRank());
		assertNull(configurationHolder.getData());
	}

	@Test
	public void createConfigurationHolder2()
	{
		// when
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder = asSearchProfileResultFactory
				.createConfigurationHolder(asConfiguration, data);

		// then
		assertEquals(asConfiguration, configurationHolder.getConfiguration());
		assertThat(configurationHolder.getReplacedConfigurations()).isNotNull().isEmpty();
		assertEquals(0, configurationHolder.getRank());
		assertEquals(data, configurationHolder.getData());
	}

	@Test
	public void cloneConfigurationHolder1()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder = asSearchProfileResultFactory
				.createConfigurationHolder(asConfiguration);

		// when
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolderClone = asSearchProfileResultFactory
				.cloneConfigurationHolder(configurationHolder);

		// then
		assertNotSame(configurationHolder, configurationHolderClone);
		assertSame(configurationHolder.getConfiguration(), configurationHolderClone.getConfiguration());
		assertNotSame(configurationHolder.getReplacedConfigurations(), configurationHolderClone.getReplacedConfigurations());
		assertEquals(configurationHolder.getReplacedConfigurations(), configurationHolderClone.getReplacedConfigurations());
		assertEquals(configurationHolder.getRank(), configurationHolderClone.getRank());
		assertSame(configurationHolder.getData(), configurationHolderClone.getData());
	}

	@Test
	public void cloneConfigurationHolder2()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder = asSearchProfileResultFactory
				.createConfigurationHolder(asConfiguration, data);
		configurationHolder.getReplacedConfigurations().add(asReplacedConfiguration);
		configurationHolder.setRank(10);

		// when
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolderClone = asSearchProfileResultFactory
				.cloneConfigurationHolder(configurationHolder);

		// then
		assertNotSame(configurationHolder, configurationHolderClone);
		assertSame(configurationHolder.getConfiguration(), configurationHolderClone.getConfiguration());
		assertNotSame(configurationHolder.getReplacedConfigurations(), configurationHolderClone.getReplacedConfigurations());
		assertEquals(configurationHolder.getReplacedConfigurations(), configurationHolderClone.getReplacedConfigurations());
		assertEquals(configurationHolder.getRank(), configurationHolderClone.getRank());
		assertSame(configurationHolder.getData(), configurationHolderClone.getData());
	}
}
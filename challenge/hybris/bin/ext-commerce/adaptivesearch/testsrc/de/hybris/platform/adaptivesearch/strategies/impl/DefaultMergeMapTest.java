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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.data.AbstractAsConfiguration;
import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultMergeMapTest
{
	private static final String KEY1 = "1";
	private static final String KEY2 = "2";
	private static final String KEY3 = "3";
	private static final String KEY4 = "4";
	private static final String REPLACE_KEY = "replace";

	@Mock
	private AbstractAsConfiguration configuration1;

	@Mock
	private AbstractAsConfiguration configuration2;

	@Mock
	private AbstractAsConfiguration configuration3;

	@Mock
	private AbstractAsConfiguration configuration4;

	private DefaultMergeMap<String, AbstractAsConfiguration, AbstractAsConfiguration> mergeMap;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		mergeMap = new DefaultMergeMap<>();
	}

	@Test
	public void mergeBeforeSingleTime()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder = createConfigurationHolder(
				configuration1);

		// when
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> previousValue = mergeMap.mergeBefore(KEY1,
				configurationHolder);

		// then
		assertNull(previousValue);
		assertThat(mergeMap).containsEntry(KEY1, configurationHolder);
	}

	@Test
	public void mergeBeforeMultipleTimes()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder1 = createConfigurationHolder(
				configuration1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder2 = createConfigurationHolder(
				configuration2);

		// when
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> previousValue1 = mergeMap.mergeBefore(KEY1,
				configurationHolder1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> previousValue2 = mergeMap.mergeBefore(KEY2,
				configurationHolder2);

		final List<AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration>> orderedValues = mergeMap
				.orderedValues();

		// then
		assertNull(previousValue1);
		assertThat(mergeMap).containsEntry(KEY1, configurationHolder1);

		assertNull(previousValue2);
		assertThat(mergeMap).containsEntry(KEY2, configurationHolder2);

		assertThat(orderedValues).containsExactly(configurationHolder2, configurationHolder1);
	}

	@Test
	public void mergeBeforeWithReplace()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder1 = createConfigurationHolder(
				configuration1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder2 = createConfigurationHolder(
				configuration2);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder3 = createConfigurationHolder(
				configuration3);

		// when
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> previousValue1 = mergeMap.mergeBefore(KEY1,
				configurationHolder1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> previousValue2 = mergeMap
				.mergeBefore(REPLACE_KEY, configurationHolder2);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> previousValue3 = mergeMap
				.mergeBefore(REPLACE_KEY, configurationHolder3);

		final List<AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration>> orderedValues = mergeMap
				.orderedValues();

		// then
		assertNull(previousValue1);
		assertThat(mergeMap).containsEntry(KEY1, configurationHolder1);

		assertNull(previousValue2);
		assertSame(configurationHolder2, previousValue3);
		assertThat(mergeMap).containsEntry(REPLACE_KEY, configurationHolder3);

		assertThat(orderedValues).containsExactly(configurationHolder3, configurationHolder1);
	}

	@Test
	public void mergeBeforeMapSingleTime()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder1 = createConfigurationHolder(
				configuration1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder2 = createConfigurationHolder(
				configuration2);

		final DefaultMergeMap<String, AbstractAsConfiguration, AbstractAsConfiguration> sourceMergeMap = new DefaultMergeMap<>();
		sourceMergeMap.put(KEY1, configurationHolder1);
		sourceMergeMap.put(KEY2, configurationHolder2);

		// when
		mergeMap.mergeBefore(sourceMergeMap, (key, oldValue, value) -> {
			return value;
		});

		final List<AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration>> orderedValues = mergeMap
				.orderedValues();

		// then
		assertThat(mergeMap).containsEntry(KEY1, configurationHolder1);
		assertThat(mergeMap).containsEntry(KEY2, configurationHolder2);

		assertThat(orderedValues).containsExactly(configurationHolder1, configurationHolder2);
	}

	@Test
	public void mergeBeforeMapMultipleTimes()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder1 = createConfigurationHolder(
				configuration1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder2 = createConfigurationHolder(
				configuration2);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder3 = createConfigurationHolder(
				configuration3);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder4 = createConfigurationHolder(
				configuration4);

		final DefaultMergeMap<String, AbstractAsConfiguration, AbstractAsConfiguration> sourceMergeMap1 = new DefaultMergeMap<>();
		sourceMergeMap1.put(KEY1, configurationHolder1);
		sourceMergeMap1.put(KEY2, configurationHolder2);

		final DefaultMergeMap<String, AbstractAsConfiguration, AbstractAsConfiguration> sourceMergeMap2 = new DefaultMergeMap<>();
		sourceMergeMap2.put(KEY3, configurationHolder3);
		sourceMergeMap2.put(KEY4, configurationHolder4);

		// when
		mergeMap.mergeBefore(sourceMergeMap1, (key, oldValue, value) -> {
			return value;
		});

		mergeMap.mergeBefore(sourceMergeMap2, (key, oldValue, value) -> {
			return value;
		});

		final List<AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration>> orderedValues = mergeMap
				.orderedValues();

		// then
		assertThat(mergeMap).containsEntry(KEY1, configurationHolder1);
		assertThat(mergeMap).containsEntry(KEY2, configurationHolder2);
		assertThat(mergeMap).containsEntry(KEY3, configurationHolder3);
		assertThat(mergeMap).containsEntry(KEY4, configurationHolder4);

		assertThat(orderedValues).containsExactly(configurationHolder3, configurationHolder4, configurationHolder1,
				configurationHolder2);
	}

	@Test
	public void mergeBeforeMapReplaceItem()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder1 = createConfigurationHolder(
				configuration1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder2 = createConfigurationHolder(
				configuration2);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder3 = createConfigurationHolder(
				configuration3);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder4 = createConfigurationHolder(
				configuration4);

		final DefaultMergeMap<String, AbstractAsConfiguration, AbstractAsConfiguration> sourceMergeMap1 = new DefaultMergeMap<>();
		sourceMergeMap1.put(KEY1, configurationHolder1);
		sourceMergeMap1.put(REPLACE_KEY, configurationHolder2);

		final DefaultMergeMap<String, AbstractAsConfiguration, AbstractAsConfiguration> sourceMergeMap2 = new DefaultMergeMap<>();
		sourceMergeMap2.put(REPLACE_KEY, configurationHolder3);
		sourceMergeMap2.put(KEY4, configurationHolder4);

		// when
		mergeMap.mergeBefore(sourceMergeMap1, (key, oldValue, value) -> {
			return value;
		});

		mergeMap.mergeBefore(sourceMergeMap2, (key, oldValue, value) -> {
			return value;
		});

		final List<AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration>> orderedValues = mergeMap
				.orderedValues();

		// then
		assertThat(mergeMap).containsEntry(KEY1, configurationHolder1);
		assertThat(mergeMap).containsEntry(REPLACE_KEY, configurationHolder3);
		assertThat(mergeMap).containsEntry(KEY4, configurationHolder4);

		assertThat(orderedValues).containsExactly(configurationHolder3, configurationHolder4, configurationHolder1);
	}

	@Test
	public void mergeAfterSingleTime()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder = createConfigurationHolder(
				configuration1);

		// when
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> previousValue = mergeMap.mergeAfter(KEY1,
				configurationHolder);

		// then
		assertNull(previousValue);
		assertThat(mergeMap).containsEntry(KEY1, configurationHolder);
	}

	@Test
	public void mergeAfterMultipleTimes()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder1 = createConfigurationHolder(
				configuration1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder2 = createConfigurationHolder(
				configuration2);

		// when
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> previousValue1 = mergeMap.mergeAfter(KEY1,
				configurationHolder1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> previousValue2 = mergeMap.mergeAfter(KEY2,
				configurationHolder2);

		final List<AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration>> orderedValues = mergeMap
				.orderedValues();

		// then
		assertNull(previousValue1);
		assertThat(mergeMap).containsEntry(KEY1, configurationHolder1);

		assertNull(previousValue2);
		assertThat(mergeMap).containsEntry(KEY2, configurationHolder2);

		assertThat(orderedValues).containsExactly(configurationHolder1, configurationHolder2);
	}

	@Test
	public void mergeAfterWithReplace()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder1 = createConfigurationHolder(
				configuration1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder2 = createConfigurationHolder(
				configuration2);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder3 = createConfigurationHolder(
				configuration3);

		// when
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> previousValue1 = mergeMap.mergeAfter(KEY1,
				configurationHolder1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> previousValue2 = mergeMap
				.mergeAfter(REPLACE_KEY, configurationHolder2);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> previousValue3 = mergeMap
				.mergeAfter(REPLACE_KEY, configurationHolder3);

		final List<AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration>> orderedValues = mergeMap
				.orderedValues();

		// then
		assertNull(previousValue1);
		assertThat(mergeMap).containsEntry(KEY1, configurationHolder1);

		assertNull(previousValue2);
		assertSame(configurationHolder2, previousValue3);
		assertThat(mergeMap).containsEntry(REPLACE_KEY, configurationHolder3);

		assertThat(orderedValues).containsExactly(configurationHolder1, configurationHolder3);
	}

	@Test
	public void mergeAfterMapSingleTime()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder1 = createConfigurationHolder(
				configuration1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder2 = createConfigurationHolder(
				configuration2);

		final DefaultMergeMap<String, AbstractAsConfiguration, AbstractAsConfiguration> sourceMergeMap = new DefaultMergeMap<>();
		sourceMergeMap.put(KEY1, configurationHolder1);
		sourceMergeMap.put(KEY2, configurationHolder2);

		// when
		mergeMap.mergeAfter(sourceMergeMap, (key, oldValue, value) -> {
			return value;
		});

		final List<AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration>> orderedValues = mergeMap
				.orderedValues();

		// then
		assertThat(mergeMap).containsEntry(KEY1, configurationHolder1);
		assertThat(mergeMap).containsEntry(KEY2, configurationHolder2);

		assertThat(orderedValues).containsExactly(configurationHolder1, configurationHolder2);
	}

	@Test
	public void mergeAfterMapMultipleTimes()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder1 = createConfigurationHolder(
				configuration1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder2 = createConfigurationHolder(
				configuration2);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder3 = createConfigurationHolder(
				configuration3);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder4 = createConfigurationHolder(
				configuration4);

		final DefaultMergeMap<String, AbstractAsConfiguration, AbstractAsConfiguration> sourceMergeMap1 = new DefaultMergeMap<>();
		sourceMergeMap1.put(KEY1, configurationHolder1);
		sourceMergeMap1.put(KEY2, configurationHolder2);

		final DefaultMergeMap<String, AbstractAsConfiguration, AbstractAsConfiguration> sourceMergeMap2 = new DefaultMergeMap<>();
		sourceMergeMap2.put(KEY3, configurationHolder3);
		sourceMergeMap2.put(KEY4, configurationHolder4);

		// when
		mergeMap.mergeAfter(sourceMergeMap1, (key, oldValue, value) -> {
			return value;
		});

		mergeMap.mergeAfter(sourceMergeMap2, (key, oldValue, value) -> {
			return value;
		});

		final List<AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration>> orderedValues = mergeMap
				.orderedValues();

		// then
		assertThat(mergeMap).containsEntry(KEY1, configurationHolder1);
		assertThat(mergeMap).containsEntry(KEY2, configurationHolder2);
		assertThat(mergeMap).containsEntry(KEY3, configurationHolder3);
		assertThat(mergeMap).containsEntry(KEY4, configurationHolder4);

		assertThat(orderedValues).containsExactly(configurationHolder1, configurationHolder2, configurationHolder3,
				configurationHolder4);
	}

	@Test
	public void mergeAfterMapReplaceItem()
	{
		// given
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder1 = createConfigurationHolder(
				configuration1);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder2 = createConfigurationHolder(
				configuration2);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder3 = createConfigurationHolder(
				configuration3);
		final AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration> configurationHolder4 = createConfigurationHolder(
				configuration4);

		final DefaultMergeMap<String, AbstractAsConfiguration, AbstractAsConfiguration> sourceMergeMap1 = new DefaultMergeMap<>();
		sourceMergeMap1.put(KEY1, configurationHolder1);
		sourceMergeMap1.put(REPLACE_KEY, configurationHolder2);

		final DefaultMergeMap<String, AbstractAsConfiguration, AbstractAsConfiguration> sourceMergeMap2 = new DefaultMergeMap<>();
		sourceMergeMap2.put(REPLACE_KEY, configurationHolder3);
		sourceMergeMap2.put(KEY4, configurationHolder4);

		// when
		mergeMap.mergeAfter(sourceMergeMap1, (key, oldValue, value) -> {
			return value;
		});

		mergeMap.mergeAfter(sourceMergeMap2, (key, oldValue, value) -> {
			return value;
		});

		final List<AsConfigurationHolder<AbstractAsConfiguration, AbstractAsConfiguration>> orderedValues = mergeMap
				.orderedValues();

		// then
		assertThat(mergeMap).containsEntry(KEY1, configurationHolder1);
		assertThat(mergeMap).containsEntry(REPLACE_KEY, configurationHolder3);
		assertThat(mergeMap).containsEntry(KEY4, configurationHolder4);

		assertThat(orderedValues).containsExactly(configurationHolder1, configurationHolder3, configurationHolder4);
	}

	public <T, R> AsConfigurationHolder<T, R> createConfigurationHolder(final T configuration)
	{
		final AsConfigurationHolder<T, R> configurationHolder = new AsConfigurationHolder<>();
		configurationHolder.setConfiguration(configuration);
		configurationHolder.setRank(0);

		return configurationHolder;
	}
}

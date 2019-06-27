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
/**
 *
 */
package de.hybris.platform.personalizationservices.strategies.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.enums.CxCatalogLookupType;
import de.hybris.platform.personalizationservices.service.impl.DefaultCxCatalogService;
import de.hybris.platform.personalizationservices.strategies.CxCatalogLookupStrategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultCxCatalogServiceTest
{
	protected static final String CATALOG1 = "qwer";
	protected static final String CATALOG2 = "asdf";
	protected static final String CATALOG3 = "zxcv";
	protected static final String CATALOG4 = "yuio";
	protected static final String CATALOG5 = "hjkl";

	private final DefaultCxCatalogService service = new DefaultCxCatalogService();
	private final Map<String, CatalogVersionModel> cache = new HashMap<>();

	protected final CxCatalogLookupStrategy s1 = buildStrategy(CATALOG1, CATALOG2);
	protected final CxCatalogLookupStrategy s2 = buildStrategy(CATALOG3, CATALOG4);
	protected final CxCatalogLookupStrategy s3 = buildStrategy(CATALOG4, CATALOG5);

	protected CxConfigurationService configService;


	@Before
	public void setup()
	{
		configService = mock(CxConfigurationService.class);
		service.setCxConfigurationService(configService);
		setLookupType(CxCatalogLookupType.ALL_CATALOGS);
	}

	protected List<CxCatalogLookupStrategy> buildStrategies(final CxCatalogLookupStrategy... strategies)
	{
		return Arrays.asList(strategies);
	}

	protected CxCatalogLookupStrategy buildStrategy(final String... catalogs)
	{
		return buildStrategy(CxCatalogLookupType.ALL_CATALOGS, catalogs);
	}

	protected CxCatalogLookupStrategy buildStrategy(final CxCatalogLookupType type, final String... catalogs)
	{
		final List<CatalogVersionModel> result = Stream.of(catalogs).map(id -> {
			final CatalogVersionModel cv = new CatalogVersionModel();
			cv.setVersion(id);
			cache.putIfAbsent(id, cv);
			return cache.get(id);
		}).collect(Collectors.toList());
		final CxCatalogLookupStrategy strategy = mock(CxCatalogLookupStrategy.class);
		when(strategy.getCatalogVersionsForCalculation()).thenReturn(result);
		when(strategy.getType()).thenReturn(type);
		return strategy;
	}

	protected void setLookupType(final CxCatalogLookupType type)
	{
		when(configService.getCatalogLookupType()).thenReturn(type);
	}

	@Test
	public void testSingleStrategy()
	{
		//given
		service.setCxCatalogLookupStrategies(buildStrategies(s1));

		//when
		final List<CatalogVersionModel> catalogVersionsForCalculation = service.getConfiguredCatalogVersions();

		//then
		Assert.assertNotNull(catalogVersionsForCalculation);
		assertEquals(catalogVersionsForCalculation, CATALOG1, CATALOG2);
	}

	@Test
	public void testMultipleStrategy()
	{
		//given
		service.setCxCatalogLookupStrategies(buildStrategies(s1, s2));

		//when
		final List<CatalogVersionModel> catalogVersionsForCalculation = service.getConfiguredCatalogVersions();

		//then
		Assert.assertNotNull(catalogVersionsForCalculation);
		assertEquals(catalogVersionsForCalculation, CATALOG1, CATALOG2, CATALOG3, CATALOG4);
	}

	@Test
	public void testOverlapingStrategy()
	{
		//given
		service.setCxCatalogLookupStrategies(buildStrategies(s1, s2, s3));

		//when
		final List<CatalogVersionModel> catalogVersionsForCalculation = service.getConfiguredCatalogVersions();

		//then
		Assert.assertNotNull(catalogVersionsForCalculation);
		assertEquals(catalogVersionsForCalculation, CATALOG1, CATALOG2, CATALOG3, CATALOG4, CATALOG5);
	}

	protected void assertEquals(final List<CatalogVersionModel> actual, final String... expected)
	{
		final List<String> expectedList = Arrays.asList(expected);
		final List<String> actualList = actual.stream().map(CatalogVersionModel::getVersion).collect(Collectors.toList());
		Assert.assertEquals(expectedList, actualList);
	}
}

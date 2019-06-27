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
package de.hybris.platform.cmsfacades.pages.service.impl;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.CatalogPageData;
import de.hybris.platform.cmsfacades.data.CategoryPageData;
import de.hybris.platform.cmsfacades.pages.service.PageTypeMapping;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultPageTypeMappingRegistryTest
{
	private static final String INVALID = "invalid";
	private static final String TEST_TYPE_CODE = "testTypeCode";
	private static final String TYPE_CODE = "aTypeCode";

	private final Set<PageTypeMapping> allMappings = new HashSet<>();
	private final DefaultPageTypeMappingRegistry registry = new DefaultPageTypeMappingRegistry();

	private PageTypeMapping mapping1;
	private PageTypeMapping mapping2;

	@Before
	public void setUp() throws Exception
	{
		mapping1 = new DefaultPageTypeMapping();
		mapping1.setTypecode(TEST_TYPE_CODE);
		mapping1.setTypedata(CatalogPageData.class);

		mapping2 = new DefaultPageTypeMapping();
		mapping2.setTypecode(TYPE_CODE);
		mapping2.setTypedata(CategoryPageData.class);

		allMappings.add(mapping1);
		allMappings.add(mapping2);
		registry.setAllPageTypeMappings(allMappings);
		registry.afterPropertiesSet();
	}

	@Test
	public void shouldPopulateMappingsInAfterPropertiesSet()
	{
		final Collection<PageTypeMapping> result = registry.getPageTypeMappings().values();
		assertThat(result.size(), is(2));
		assertThat(result, containsInAnyOrder(mapping1, mapping2));
	}

	@Test
	public void shouldFindPageTypeMapping()
	{
		final Optional<PageTypeMapping> result = registry.getPageTypeMapping(TEST_TYPE_CODE);
		assertThat(result.isPresent(), is(true));
		assertEquals(CatalogPageData.class, result.get().getTypedata());
	}

	@Test
	public void shouldNotFindPageTypeMapping()
	{
		final Optional<PageTypeMapping> result = registry.getPageTypeMapping(INVALID);
		assertThat(result.isPresent(), is(false));
	}

}

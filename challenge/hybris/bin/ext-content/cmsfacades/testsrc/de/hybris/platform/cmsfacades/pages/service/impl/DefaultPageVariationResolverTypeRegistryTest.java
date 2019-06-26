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
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPageVariationResolverTypeRegistryTest
{
	private static final String TYPE_CODE1 = "typeCode-1";
	private static final String TYPE_CODE2 = "typeCode-2";

	private final Set<PageVariationResolverType> allResolverTypes = new HashSet<>();
	private final DefaultPageVariationResolverTypeRegistry registry = new DefaultPageVariationResolverTypeRegistry();

	private DefaultPageVariationResolverType resolverType1;
	private DefaultPageVariationResolverType resolverType2;

	@Mock
	private PageVariationResolver<AbstractPageModel> resolver1;
	@Mock
	private PageVariationResolver<AbstractPageModel> resolver2;

	@Before
	public void setUp() throws Exception
	{
		resolverType1 = new DefaultPageVariationResolverType();
		resolverType1.setTypecode(TYPE_CODE1);
		resolverType1.setResolver(resolver1);

		resolverType2 = new DefaultPageVariationResolverType();
		resolverType2.setTypecode(AbstractPageModel._TYPECODE);
		resolverType2.setResolver(resolver2);

		allResolverTypes.add(resolverType1);
		allResolverTypes.add(resolverType2);
		registry.setAllPageVariationResolverTypes(allResolverTypes);
		registry.afterPropertiesSet();
	}

	@Test
	public void shouldPopulateResolverTypesInAfterPropertiesSet()
	{
		final Collection<PageVariationResolverType> result = registry.getResolversByType().values();
		assertThat(result.size(), is(2));
		assertThat(result, containsInAnyOrder(resolverType1, resolverType2));
	}

	@Test
	public void shouldFindPageVariationResolverType()
	{
		final Optional<PageVariationResolverType> result = registry.getPageVariationResolverType(TYPE_CODE1);
		assertThat(result.isPresent(), is(true));
		assertThat(result.get().getResolver(), is(resolver1));
	}

	@Test
	public void shouldFindDefaultPageVariationResolverType()
	{
		final Optional<PageVariationResolverType> result = registry.getPageVariationResolverType(TYPE_CODE2);
		assertThat(result.isPresent(), is(true));
		assertThat(result.get().getResolver(), is(resolver2));
	}

}

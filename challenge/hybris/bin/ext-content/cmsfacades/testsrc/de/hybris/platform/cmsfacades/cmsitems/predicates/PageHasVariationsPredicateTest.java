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
package de.hybris.platform.cmsfacades.cmsitems.predicates;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverType;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PageHasVariationsPredicateTest
{

	@InjectMocks
	private PageHasVariationsPredicate pageHasVariationsPredicate;

	@Mock
	private PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry;

	@Mock
	private PageVariationResolverType pageVariationResolverType;

	@Mock
	private PageVariationResolver<AbstractPageModel> pageVariationResolver;

	@Mock
	private AbstractPageModel defaultPageModel;

	@Mock
	private AbstractPageModel variationPageModel;

	@Before
	public void setup()
	{
		when(defaultPageModel.getItemtype()).thenReturn(AbstractPageModel._TYPECODE);
		when(pageVariationResolverTypeRegistry.getPageVariationResolverType(AbstractPageModel._TYPECODE)).thenReturn(Optional.of(pageVariationResolverType));
		when(pageVariationResolverType.getResolver()).thenReturn(pageVariationResolver);
	}

	@Test
	public void testPageHasVariations()
	{
		when(pageVariationResolver.findVariationPages(defaultPageModel)).thenReturn(Collections.singletonList(variationPageModel));

		assertThat(pageHasVariationsPredicate.test(defaultPageModel), is(true));
	}

	@Test
	public void testPageHasNoVariations()
	{
		when(pageVariationResolver.findVariationPages(defaultPageModel)).thenReturn(Collections.emptyList());

		assertThat(pageHasVariationsPredicate.test(defaultPageModel), is(false));
	}
}

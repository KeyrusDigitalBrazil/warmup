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
package de.hybris.platform.cms2.cloning.strategy.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.cloning.service.CMSItemDeepCloningService;
import de.hybris.platform.cms2.cloning.service.CMSModelCloningContextFactory;
import de.hybris.platform.cms2.cloning.service.impl.CMSModelCloningContext;
import de.hybris.platform.cms2.cloning.service.predicate.CMSItemCloneablePredicate;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ComponentCloningStrategyTest
{
	@Mock
	private CMSItemDeepCloningService cmsItemDeepCloningService;
	@Mock
	private SessionSearchRestrictionsDisabler cmsSessionSearchRestrictionsDisabler;
	@Mock
	private CMSModelCloningContextFactory cmsModelCloningContextFactory;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CMSItemCloneablePredicate cmsItemCloneablePredicate;

	@InjectMocks
	private ComponentCloningStrategy strategy;

	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private CMSModelCloningContext modelCloningContext;
	@Mock
	private CMSLinkComponentModel linkComponent;

	@Before
	public void setUp()
	{
		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final Supplier<?> supplier = (Supplier<?>) args[0];
			return supplier.get();
		}).when(cmsSessionSearchRestrictionsDisabler).execute(any());
	}

	@Test
	public void shouldCloneComponent() throws CMSItemNotFoundException
	{
		when(cmsItemCloneablePredicate.test(linkComponent)).thenReturn(true);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Collections.singletonList(catalogVersionModel));
		when(cmsModelCloningContextFactory.createCloningContextWithCatalogVersionPredicates(catalogVersionModel))
				.thenReturn(modelCloningContext);

		strategy.clone(linkComponent, Optional.empty(), Optional.empty());

		verify(cmsItemDeepCloningService).deepCloneComponent(linkComponent, modelCloningContext);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCloneWhenCatalogVersionIsNull() throws CMSItemNotFoundException
	{
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Collections.emptyList());

		strategy.clone(linkComponent, Optional.empty(), Optional.empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCloneWhenComponentNonCloneable() throws CMSItemNotFoundException
	{
		when(cmsItemCloneablePredicate.test(linkComponent)).thenReturn(false);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Collections.singletonList(catalogVersionModel));
		when(cmsModelCloningContextFactory.createCloningContextWithCatalogVersionPredicates(catalogVersionModel))
				.thenReturn(modelCloningContext);

		strategy.clone(linkComponent, Optional.empty(), Optional.empty());
	}
}

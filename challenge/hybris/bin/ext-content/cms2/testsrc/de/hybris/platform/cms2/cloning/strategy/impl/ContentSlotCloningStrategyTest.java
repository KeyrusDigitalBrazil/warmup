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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.cloning.service.CMSItemCloningService;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ContentSlotCloningStrategyTest
{
	private static final String CONTENT_SLOT_POSITION = "contentSlotPosition";
	private static final boolean CONTENT_SLOT_ACTIVE = true;
	private static final Date CONTENT_SLOT_ACTIVE_FROM = new Date();
	private static final Date CONTENT_SLOT_ACTIVE_UNTIL = new Date();

	@Spy
	@InjectMocks
	private ContentSlotCloningStrategy strategy;

	@Mock
	private SessionSearchRestrictionsDisabler cmsSessionSearchRestrictionsDisabler;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CMSItemCloningService cmsItemCloningService;
	@Mock
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	@Mock
	private ModelService modelService;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private AbstractPageModel sourcePageModel;
	@Mock
	private ContentSlotModel sourceContentSlotModel;
	@Mock
	private ContentSlotForPageModel newContentSlotForPageModel;
	@Mock
	private AbstractCMSComponentModel cloneableCmsComponentModel;
	@Mock
	private AbstractCMSComponentModel nonCloneableCmsComponentModel;

	@Before
	public void setUp()
	{
		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final Supplier<?> supplier = (Supplier<?>) args[0];
			return supplier.get();
		}).when(cmsSessionSearchRestrictionsDisabler).execute(any());

		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Collections.singletonList(catalogVersionModel));

		when(cmsAdminContentSlotService.getContentSlotPosition(sourcePageModel, sourceContentSlotModel))
				.thenReturn(CONTENT_SLOT_POSITION);

		when(modelService.create(ContentSlotForPageModel.class)).thenReturn(newContentSlotForPageModel);

		when(sourceContentSlotModel.getActive()).thenReturn(CONTENT_SLOT_ACTIVE);
		when(sourceContentSlotModel.getActiveFrom()).thenReturn(CONTENT_SLOT_ACTIVE_FROM);
		when(sourceContentSlotModel.getActiveUntil()).thenReturn(CONTENT_SLOT_ACTIVE_UNTIL);

		when(sourceContentSlotModel.getCmsComponents()).thenReturn(Arrays.asList(cloneableCmsComponentModel, nonCloneableCmsComponentModel));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCloneWhenTemplateIsEmpty() throws CMSItemNotFoundException
	{
		strategy.clone(sourceContentSlotModel, Optional.empty(), Optional.empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCloneWhenContextIsEmpty() throws CMSItemNotFoundException
	{
		strategy.clone(sourceContentSlotModel, Optional.of(new ContentSlotModel()), Optional.empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCloneWhenPageNotInContext() throws CMSItemNotFoundException
	{
		strategy.clone(sourceContentSlotModel, Optional.of(new ContentSlotModel()), Optional.of(new HashMap<>()));
	}

	@Test
	public void shouldCloneComponents() throws CMSItemNotFoundException
	{
		final Map<String, Object> context = new HashMap<>();
		context.put(Cms2Constants.PAGE_CONTEXT_KEY, sourcePageModel);
		context.put(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, true);

		final ContentSlotModel clonedContentSlotModel = new ContentSlotModel();
		strategy.clone(sourceContentSlotModel, Optional.of(clonedContentSlotModel), Optional.of(context));

		verify(cmsSessionSearchRestrictionsDisabler).execute(any());
		verify(newContentSlotForPageModel).setPosition(CONTENT_SLOT_POSITION);

		assertThat(clonedContentSlotModel.getActive(), equalTo(CONTENT_SLOT_ACTIVE));
		assertThat(clonedContentSlotModel.getActiveFrom(), equalTo(CONTENT_SLOT_ACTIVE_FROM));
		assertThat(clonedContentSlotModel.getActiveUntil(), equalTo(CONTENT_SLOT_ACTIVE_UNTIL));
		assertThat(clonedContentSlotModel.getCatalogVersion(), equalTo(catalogVersionModel));
	}

	@Test
	public void shouldCloneAndExcludeComponents() throws CMSItemNotFoundException
	{
		final Map<String, Object> context = new HashMap<>();
		context.put(Cms2Constants.PAGE_CONTEXT_KEY, sourcePageModel);
		context.put(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, false);

		final ContentSlotModel clonedContentSlotModel = new ContentSlotModel();
		strategy.clone(sourceContentSlotModel, Optional.of(clonedContentSlotModel), Optional.of(context));

		verify(cmsItemCloningService, never()).cloneContentSlotComponents(sourceContentSlotModel, clonedContentSlotModel, catalogVersionModel);

		assertThat(clonedContentSlotModel.getCmsComponents().isEmpty(), is(true));
	}
}

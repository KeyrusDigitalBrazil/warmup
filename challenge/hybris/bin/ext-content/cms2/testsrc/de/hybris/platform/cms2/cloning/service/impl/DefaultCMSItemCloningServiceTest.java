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
package de.hybris.platform.cms2.cloning.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.cloning.service.CMSItemDeepCloningService;
import de.hybris.platform.cms2.cloning.service.CMSModelCloningContextFactory;
import de.hybris.platform.cms2.cloning.service.predicate.CMSItemCloneablePredicate;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSItemCloningServiceTest
{
	private static final String ORIGINAL_COMPONENT_NAME = "component name";
	private static final String CLONED_COMPONENT_NAME = "cloned component name";
	private static final String PAGE_SLOT1_NAME = "ps-1";
	private static final String PAGE_SLOT2_NAME = "ps-1";
	private static final String OTHER_PAGE_SLOT1_NAME = "other-ps-1";
	private static final String OTHER_PAGE_SLOT2_NAME = "other-ps-2";

	@Spy
	@InjectMocks
	private DefaultCMSItemCloningService cmsItemCloningService;

	@Mock
	private CMSModelCloningContextFactory cmsModelCloningContextFactory;

	@Mock
	private CMSItemCloneablePredicate cmsItemCloneablePredicate;

	@Mock
	private CMSItemDeepCloningService cmsItemDeepCloningService;

	@Mock
	private CatalogVersionModel catalogVersionModel;

	@Mock
	private CMSModelCloningContext cmsModelCloningContext;

	@Mock
	private AbstractCMSComponentModel cloneableCMSComponentModel;

	@Mock
	private AbstractCMSComponentModel nonCloneableCMSComponentModel;

	@Mock
	private AbstractCMSComponentModel clonedCMSComponentModel;

	@Mock
	private ContentSlotModel contentSlotModel;

	@Mock
	private ContentSlotData pageSlot1Data;

	@Mock
	private ContentSlotData pageSlot2Data;

	@Mock
	private ContentSlotModel pageSlot1Model;

	@Mock
	private ContentSlotModel pageSlot2Model;

	@Mock
	private ContentSlotModel otherPageSlot1Model;

	@Mock
	private ContentSlotModel otherPageSlot2Model;

	@Mock
	private AbstractPageModel page;

	@Mock
	private CMSPageService pageService;

	@Before
	public void setUp()
	{
		when(cmsModelCloningContextFactory.createCloningContextWithCatalogVersionPredicates(catalogVersionModel)).thenReturn(cmsModelCloningContext);

		when(cmsItemCloneablePredicate.test(cloneableCMSComponentModel)).thenReturn(true);
		when(cmsItemCloneablePredicate.test(nonCloneableCMSComponentModel)).thenReturn(false);

		when(cmsItemDeepCloningService.deepCloneComponent(cloneableCMSComponentModel, cmsModelCloningContext)).thenReturn(clonedCMSComponentModel);
		when(cmsItemDeepCloningService.generateCloneComponentName(ORIGINAL_COMPONENT_NAME)).thenReturn(CLONED_COMPONENT_NAME);

		when(cloneableCMSComponentModel.getName()).thenReturn(ORIGINAL_COMPONENT_NAME);
		when(cloneableCMSComponentModel.getCatalogVersion()).thenReturn(catalogVersionModel);
		when(contentSlotModel.getCmsComponents()).thenReturn(Arrays.asList(cloneableCMSComponentModel, nonCloneableCMSComponentModel));

		// Set up components
		when(cloneableCMSComponentModel.getSlots()).thenReturn(Arrays.asList(otherPageSlot1Model, pageSlot1Model, otherPageSlot2Model, pageSlot2Model));
		when(clonedCMSComponentModel.getSlots()).thenReturn(Arrays.asList(otherPageSlot1Model, pageSlot1Model, otherPageSlot2Model, pageSlot2Model));

		// Set up slots
		when(pageSlot1Model.getUid()).thenReturn(PAGE_SLOT1_NAME);
		when(pageSlot2Model.getUid()).thenReturn(PAGE_SLOT2_NAME);
		when(pageSlot1Data.getUid()).thenReturn(PAGE_SLOT1_NAME);
		when(pageSlot2Data.getUid()).thenReturn(PAGE_SLOT2_NAME);
		when(otherPageSlot1Model.getUid()).thenReturn(OTHER_PAGE_SLOT1_NAME);
		when(otherPageSlot2Model.getUid()).thenReturn(OTHER_PAGE_SLOT2_NAME);

		// Set up page
		when(pageService.getContentSlotsForPage(page)).thenReturn(Arrays.asList(pageSlot2Data, pageSlot1Data));
	}

	@Test
	public void shouldCloneContentSlotComponents()
	{
		final ContentSlotModel clonedContentSlotModel = new ContentSlotModel();
		cmsItemCloningService.cloneContentSlotComponents(contentSlotModel, clonedContentSlotModel, catalogVersionModel);

		verify(cmsItemDeepCloningService, times(1)).deepCloneComponent(cloneableCMSComponentModel, cmsModelCloningContext);
		verify(cmsItemDeepCloningService, never()).deepCloneComponent(nonCloneableCMSComponentModel, cmsModelCloningContext);

		assertThat(clonedContentSlotModel.getCmsComponents(), hasSize(1));
		assertThat(clonedContentSlotModel.getCmsComponents().get(0), equalTo(clonedCMSComponentModel));
	}

	@Test
	public void shouldSetNewNameToClonedContentSlotComponents()
	{
		// GIVEN
		final ContentSlotModel clonedContentSlotModel = new ContentSlotModel();

		// WHEN
		cmsItemCloningService.cloneContentSlotComponents(contentSlotModel, clonedContentSlotModel, catalogVersionModel);

		// THEN
		verify(clonedCMSComponentModel).setName(CLONED_COMPONENT_NAME);
		assertThat(clonedContentSlotModel.getCmsComponents().size(), equalTo(1));
	}

	@Test
	public void shouldReturnFalseForNullOrEmptyContext()
	{
		assertThat(cmsItemCloningService.shouldCloneComponents(null), is(false));
		assertThat(cmsItemCloningService.shouldCloneComponents(new HashMap<>()), is(false));
	}

	@Test
	public void shouldReturnFalseForInvalidContext()
	{
		assertThat(cmsItemCloningService.shouldCloneComponents(Maps.newHashMap(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, "blah")), is(false));
		assertThat(cmsItemCloningService.shouldCloneComponents(Maps.newHashMap(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, "")), is(false));
		assertThat(cmsItemCloningService.shouldCloneComponents(Maps.newHashMap(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, null)), is(false));
	}

	@Test
	public void shouldReturnValueFromContext()
	{
		assertThat(cmsItemCloningService.shouldCloneComponents(Maps.newHashMap(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, true)), is(true));
		assertThat(cmsItemCloningService.shouldCloneComponents(Maps.newHashMap(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, "true")), is(true));
		assertThat(cmsItemCloningService.shouldCloneComponents(Maps.newHashMap(Cms2Constants.SHOULD_CLONE_COMPONENTS_CONTEXT_KEY, "TRUE")), is(true));
	}

	@Test
	public void givenCloneableElement_WhenCloneComponentInCatalogVersionIsCalled_ThenItProperlyClonesTheComponent()
	{
		// WHEN
		final Optional<AbstractCMSComponentModel> clonedComponent =
				cmsItemCloningService.cloneComponentInCatalogVersion(cloneableCMSComponentModel, catalogVersionModel);

		// THEN
		verify(cmsItemCloneablePredicate).test(cloneableCMSComponentModel);
		verify(cmsItemDeepCloningService).deepCloneComponent(cloneableCMSComponentModel, cmsModelCloningContext);
		verify(cmsItemDeepCloningService).generateCloneComponentName(ORIGINAL_COMPONENT_NAME);
		assertTrue(clonedComponent.isPresent());
		verify(clonedComponent.get()).setName(CLONED_COMPONENT_NAME);
		assertThat(clonedComponent, is(Optional.of(clonedCMSComponentModel)));
	}

	@Test
	public void givenNonCloneableElement_WhenCloneComponentInCatalogVersionIsCalled_ThenItReturnsAnEmptyOptional()
	{
		// WHEN
		final Optional<AbstractCMSComponentModel> clonedComponent =
				cmsItemCloningService.cloneComponentInCatalogVersion(nonCloneableCMSComponentModel, catalogVersionModel);

		// THEN
		verify(cmsItemCloneablePredicate).test(nonCloneableCMSComponentModel);
		verify(cmsItemDeepCloningService, never()).deepCloneComponent(nonCloneableCMSComponentModel, cmsModelCloningContext);
		assertFalse(clonedComponent.isPresent());
	}

	@Test
	public void givenCloneableElement_WhenCloneComponentIsCalled_ThenItDelegatesToCloneComponentInCatalogVersion()
	{
		// WHEN
		cmsItemCloningService.cloneComponent(cloneableCMSComponentModel);

		// THEN
		verify(cmsItemCloningService).cloneComponentInCatalogVersion(cloneableCMSComponentModel, catalogVersionModel);
	}

	@Test
	public void givenNonCloneableElement_WhenCloneComponentIsCalled_ThenItDelegatesToCloneComponentInCatalogVersion()
	{
		// GIVEN
		when(nonCloneableCMSComponentModel.getCatalogVersion()).thenReturn(catalogVersionModel);

		// WHEN
		cmsItemCloningService.cloneComponent(nonCloneableCMSComponentModel);

		// THEN
		verify(cmsItemCloningService).cloneComponentInCatalogVersion(nonCloneableCMSComponentModel, catalogVersionModel);
	}
}

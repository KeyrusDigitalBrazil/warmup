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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.cloning.service.CMSItemCloningService;
import de.hybris.platform.cms2.cloning.service.CMSItemDeepCloningService;
import de.hybris.platform.cms2.cloning.service.CMSModelCloningContextFactory;
import de.hybris.platform.cms2.cloning.service.impl.CMSModelCloningContext;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.impl.DefaultCMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.impl.DefaultCMSAdminPageService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.MockSessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PageCloningStrategyTest
{
	private static final String CONTENT_SLOT_NAME = "contentSlotName";
	private static final String CONTENT_SLOT_POSITION = "contentSlotPosition";
	private static final boolean CONTENT_SLOT_ACTIVE = true;
	private static final Date CONTENT_SLOT_ACTIVE_FROM = new Date();
	private static final Date CONTENT_SLOT_ACTIVE_UNTIL = new Date();

	private static final String CMS_COMPONENT_NAME = "cmsComponentName";
	private static final String CMS_COMPONENT_TYPE_CODE = "cmsComponentTypeCode";

	private static final String CLONED_PREVIEW_IMAGE_CODE = "clonedPreviewImageCode";

	@Spy
	@InjectMocks
	private PageCloningStrategy strategy;

	@Mock
	private AbstractPageModel sourcePageModel;
	@Mock
	private DefaultCMSAdminPageService adminPageService;
	@Mock
	private DefaultCMSAdminContentSlotService contentSlotService;
	@Mock
	private ModelService modelService;
	@Mock
	private CMSItemCloningService cmsItemCloningService;
	@Mock
	private CMSItemDeepCloningService cmsItemDeepCloningService;
	@Mock
	private CMSModelCloningContextFactory cmsModelCloningContextFactory;
	@Mock
	private SessionSearchRestrictionsDisabler cmsSessionSearchRestrictionsDisabler;
	@Mock
	private PlatformTransactionManager transactionManager;
	@Mock
	private AbstractPageModel identicalPrimaryPage;
	@Mock
	private MediaModel sourcePreviewImage;
	@Mock
	private MediaModel clonedPreviewImage;
	@Mock
	private CatalogVersionModel targetCatalogVersion;
	@Mock
	private CMSModelCloningContext modelCloningContext;

	@Mock
	private ContentPageModel contentPage;
	@Mock
	private AbstractPageModel clonedPage;

	private final MockSessionService sessionService = new MockSessionService();

	@Before
	public void setUp()
	{
		strategy.setSessionService(sessionService);

		when(sourcePageModel.getPreviewImage()).thenReturn(sourcePreviewImage);
		when(cmsItemDeepCloningService.deepCloneComponent(sourcePreviewImage, modelCloningContext)).thenReturn(clonedPreviewImage);
		when(cmsItemDeepCloningService.generateCloneItemUid()).thenReturn(CLONED_PREVIEW_IMAGE_CODE);
		when(cmsModelCloningContextFactory.createCloningContextWithCatalogVersionPredicates(any())).thenReturn(modelCloningContext);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCloneWhenTemplateIsNull() throws CMSItemNotFoundException
	{
		// Act
		strategy.clone(sourcePageModel, null, Optional.empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCloneWhenTemplateIsEmpty() throws CMSItemNotFoundException
	{
		// Act
		strategy.clone(sourcePageModel, Optional.empty(), Optional.empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCloneWhenContextIsNull() throws CMSItemNotFoundException
	{
		// Act
		strategy.clone(sourcePageModel, Optional.of(contentPage), null);
	}

	@Test
	public void shouldClone() throws CMSItemNotFoundException
	{
		strategy.clone(sourcePageModel, Optional.of(contentPage), Optional.empty());

		verify(transactionManager).getTransaction(any());
		verify(transactionManager).commit(any());
		verify(cmsSessionSearchRestrictionsDisabler).execute(any());
	}

	@Test
	public void shouldClonePageAndRemoveIdenticalPrimaryPage()
	{
		// Setup
		when(adminPageService.getIdenticalPrimaryPageModel(contentPage)).thenReturn(identicalPrimaryPage);
		when(contentPage.getDefaultPage()).thenReturn(true);

		// Act
		final Supplier<AbstractPageModel> supplier = strategy.clonePage(sourcePageModel, contentPage, null);
		supplier.get();

		// Assert
		verify(contentPage).setOriginalPage(sourcePageModel);

		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(contentPage).setLocalizedPages(captor.capture());
		assertEquals(0, captor.getValue().size());

		verify(contentPage).setPreviewImage(clonedPreviewImage);
		verify(identicalPrimaryPage).setPageStatus(CmsPageStatus.DELETED);
		verify(modelService).saveAll();
	}

	@Test
	public void shouldClonePreviewImage()
	{
		// Act
		final Optional<MediaModel> cloned = strategy.clonePreviewImage(sourcePageModel, targetCatalogVersion);

		// Assert
		//		verify(clonedPreviewImage).setCatalogVersion(targetCatalogVersion);
		//		verify(clonedPreviewImage).setCode(CLONED_PREVIEW_IMAGE_CODE);
		assertEquals(cloned.get(), clonedPreviewImage);
	}

	@Test
	public void shouldNotCloneUndefinedPreviewImage()
	{
		// Setup
		when(sourcePageModel.getPreviewImage()).thenReturn(null);

		// Act
		final Optional<MediaModel> cloned = strategy.clonePreviewImage(sourcePageModel, targetCatalogVersion);

		// Assert
		assertFalse(cloned.isPresent());
	}

	@Test
	public void shouldCloneContentSlotAndCloneComponents()
	{
		// Setup
		final ContentSlotModel sourceContentSlotModel = generateMockContentSlotModel();

		final ContentSlotModel expectedClonedContentSlotModel = new ContentSlotModel();
		expectedClonedContentSlotModel.setCatalogVersion(targetCatalogVersion);
		when(contentSlotService.createContentSlot(clonedPage, null, CONTENT_SLOT_NAME, CONTENT_SLOT_POSITION, CONTENT_SLOT_ACTIVE,
				CONTENT_SLOT_ACTIVE_FROM, CONTENT_SLOT_ACTIVE_UNTIL)).thenReturn(expectedClonedContentSlotModel);
		when(cmsItemDeepCloningService.deepCloneComponent(any(), any())).thenReturn(mock(AbstractCMSComponentModel.class));

		// Act
		final ContentSlotModel actualClonedContentSlotModel = strategy.cloneAndAddContentSlot(clonedPage, sourceContentSlotModel,
				true);

		// Assert
		assertEquals(expectedClonedContentSlotModel, actualClonedContentSlotModel);

		verify(cmsItemCloningService).cloneContentSlotComponents(sourceContentSlotModel, expectedClonedContentSlotModel, targetCatalogVersion);
	}

	/**
	 * Generates a content slot model mock.
	 *
	 * @return the mocked content slot model
	 */
	protected ContentSlotModel generateMockContentSlotModel()
	{
		final ContentSlotModel contentSlotModel = mock(ContentSlotModel.class);

		when(contentSlotModel.getName()).thenReturn(CONTENT_SLOT_NAME);
		when(contentSlotModel.getCurrentPosition()).thenReturn(CONTENT_SLOT_POSITION);
		when(contentSlotModel.getActive()).thenReturn(CONTENT_SLOT_ACTIVE);
		when(contentSlotModel.getActiveFrom()).thenReturn(CONTENT_SLOT_ACTIVE_FROM);
		when(contentSlotModel.getActiveUntil()).thenReturn(CONTENT_SLOT_ACTIVE_UNTIL);
		when(contentSlotModel.getCatalogVersion()).thenReturn(targetCatalogVersion);

		final List<AbstractCMSComponentModel> cmsComponentModels = Arrays.asList(generateMockCmsComponentModel(),
				generateMockCmsComponentModel());
		when(contentSlotModel.getCmsComponents()).thenReturn(cmsComponentModels);

		return contentSlotModel;
	}

	/**
	 * Generates a CMS component model mock.
	 *
	 * @return the mocked CMS component model
	 */
	protected AbstractCMSComponentModel generateMockCmsComponentModel()
	{
		final AbstractCMSComponentModel cmsComponentModel = mock(AbstractCMSComponentModel.class);
		final List<ContentSlotModel> slots = new ArrayList<>();

		when(cmsComponentModel.getName()).thenReturn(CMS_COMPONENT_NAME);
		when(cmsComponentModel.getTypeCode()).thenReturn(CMS_COMPONENT_TYPE_CODE);
		when(cmsComponentModel.getSlots()).thenReturn(slots);

		return cmsComponentModel;
	}
}

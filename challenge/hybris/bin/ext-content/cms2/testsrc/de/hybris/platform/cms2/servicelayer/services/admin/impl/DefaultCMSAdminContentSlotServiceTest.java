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
package de.hybris.platform.cms2.servicelayer.services.admin.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cms2.jalo.contents.components.AbstractCMSComponent;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;
import de.hybris.platform.cms2.servicelayer.daos.CMSContentSlotDao;
import de.hybris.platform.cms2.servicelayer.data.CMSDataFactory;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSAdminContentSlotServiceTest
{

	private static final int MIN_CONTENT_SLOT_RELATIONS = 1;
	private static final int MAX_CONTENT_SLOT_RELATIONS = 5;

	private static final String CONTENT_SLOT_POSITION = "contentSlotPosition";

	@Captor
	private ArgumentCaptor<ContentSlotForPageModel> savedContentSlotForPageCaptor;

	@Mock
	private KeyGenerator keyGenerator;

	@Mock
	private ModelService modelService;

	@Mock
	private PermissionCRUDService permissionCRUDService;

	@InjectMocks
	@Spy
	private DefaultCMSAdminContentSlotService cmsAdminContentSlotService;

	@Mock
	private DefaultCMSAdminComponentService cmsAdminComponentService;

	@Mock
	private CMSContentSlotDao cmsContentSlotDao;

	@Mock
	private CMSDataFactory cmsDataFactory;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private AbstractPageModel page;

	@Mock
	private PageTemplateModel template;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private SessionService sessionService;

	@Mock
	private CMSAdminSiteService cmsAdminSiteService;

	@Mock
	private CatalogLevelService catalogLevelService;

	@Mock
	private CMSSiteModel cmsSite;

	@Mock
	private ContentCatalogModel contentCatalog;

	@Mock
	private Date from;

	@Mock
	private Date to;

	@Mock
	private AbstractCMSComponentModel componentModel;

	private final ContentSlotModel contentSlotModel = new ContentSlotModel();

	private final ContentSlotForPageModel contentSlotForPageModel = new ContentSlotForPageModel();

	/**
	 * Generates a list of {@value MIN_CONTENT_SLOT_RELATIONS} to {@value MAX_CONTENT_SLOT_RELATIONS} content slot
	 * relation model mocks for the page.
	 *
	 * @return the list content slot relation model mocks for the page
	 */
	protected List<ContentSlotForPageModel> generateContentSlotForPageModelMocks()
	{
		final List<ContentSlotForPageModel> contentSlotForPageModels = new ArrayList<>();

		final int contentSlotRelationCount = ThreadLocalRandom.current().nextInt(MIN_CONTENT_SLOT_RELATIONS,
				MAX_CONTENT_SLOT_RELATIONS);

		for (int i = 0; i < contentSlotRelationCount; i++)
		{
			final ContentSlotForPageModel contentSlotForPageModel = new ContentSlotForPageModel();
			contentSlotForPageModel.setPosition(CONTENT_SLOT_POSITION + i);

			final ContentSlotData contentSlotData = mock(ContentSlotData.class);
			when(contentSlotData.getPosition()).thenReturn(CONTENT_SLOT_POSITION + i);

			when(cmsDataFactory.createContentSlotData(contentSlotForPageModel)).thenReturn(contentSlotData);

			contentSlotForPageModels.add(contentSlotForPageModel);
		}

		return contentSlotForPageModels;
	}

	/**
	 * Generates a list of {@value MIN_CONTENT_SLOT_RELATIONS} to {@value MAX_CONTENT_SLOT_RELATIONS} content slot
	 * relation model mocks for the page template.
	 *
	 * @param withPositions
	 *           true if the template slots should have a position parameter to test the filtering out of custom content
	 *           slots on the page
	 * @return the list content slot relation model mocks for the page template
	 */
	protected List<ContentSlotForTemplateModel> generateContentSlotForTemplateModelMocks(final boolean isOverlapExpected)
	{
		final List<ContentSlotForTemplateModel> contentSlotForTemplateModels = new ArrayList<>();

		final int contentSlotRelationCount = ThreadLocalRandom.current().nextInt(MIN_CONTENT_SLOT_RELATIONS,
				MAX_CONTENT_SLOT_RELATIONS);

		for (int i = 0; i < contentSlotRelationCount; i++)
		{
			final ContentSlotForTemplateModel contentSlotForTemplateModel = mock(ContentSlotForTemplateModel.class);
			final String position = isOverlapExpected ? CONTENT_SLOT_POSITION + i : "templateSlotPosition" + i;
			when(contentSlotForTemplateModel.getPosition()).thenReturn(position);

			final ContentSlotData contentSlotData = mock(ContentSlotData.class);
			when(contentSlotData.getPosition()).thenReturn(CONTENT_SLOT_POSITION + i);

			when(cmsDataFactory.createContentSlotData(page, contentSlotForTemplateModel)).thenReturn(contentSlotData);

			contentSlotForTemplateModels.add(contentSlotForTemplateModel);
		}

		return contentSlotForTemplateModels;
	}

	/**
	 * Calculates how many content slots should be returned, based on the number of content slots on the page, the
	 * template and if an overlap of slots (custom page slots replacing template slots) is expected.
	 *
	 * @param contentSlotsForPageCount
	 *           the number of content slots on the page
	 * @param contentSlotsForTemplateCount
	 *           the number of content slots on the template
	 * @param isOverlapExpected
	 *           true if there are custom content slots on the page that override the slot on the template, false
	 *           otherwise
	 * @return
	 */
	protected int calculateNumberExpectedContentSlotsForPage(final int contentSlotsForPageCount,
			final int contentSlotsForTemplateCount, final boolean isOverlapExpected)
	{
		int expectedContentSlotsForPageCount;

		if (isOverlapExpected)
		{
			expectedContentSlotsForPageCount = contentSlotsForTemplateCount > contentSlotsForPageCount ? contentSlotsForTemplateCount
					: contentSlotsForPageCount;
		}
		else
		{
			expectedContentSlotsForPageCount = contentSlotsForPageCount + contentSlotsForTemplateCount;
		}

		return expectedContentSlotsForPageCount;
	}

	@Before
	public void setUp()
	{
		when(keyGenerator.generate()).thenReturn("generatedKey");

		when(page.getUid()).thenReturn("mypage$uid");
		when(page.getCatalogVersion()).thenReturn(catalogVersion);
		when(page.getMasterTemplate()).thenReturn(template);

		when(cmsDataFactory.createContentSlotData(any(ContentSlotForPageModel.class))).thenReturn(mock(ContentSlotData.class));
		when(cmsDataFactory.createContentSlotData(any(AbstractPageModel.class), any(ContentSlotForTemplateModel.class)))
				.thenReturn(mock(ContentSlotData.class));

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getBoolean(anyString())).thenReturn(Boolean.FALSE);

		when(cmsAdminSiteService.getActiveSite()).thenReturn(cmsSite);
		when(cmsSite.getContentCatalogs()).thenReturn(Arrays.asList(contentCatalog));
		when(catalogLevelService.getLevelCatalogVersions(any(), any())).thenReturn(Arrays.asList(catalogVersion));

		when(modelService.create(ContentSlotModel.class)).thenReturn(contentSlotModel);
		when(modelService.create(ContentSlotForPageModel.class)).thenReturn(contentSlotForPageModel);
	}

	@Test
	public void willCreateAndAssignSlotsToThePositionsNotBusyWithSharedSlots()
	{
		final ContentSlotModel returnValue = cmsAdminContentSlotService.createContentSlot(page, null, "name1", "position1", true,
				from, to);

		assertThat(returnValue, is(contentSlotModel));

		assertThat(contentSlotModel,
				allOf(hasProperty("uid", is("position1Slot-mypage-uid")), hasProperty("name", is("name1")),
						hasProperty("active", is(true)), hasProperty("activeFrom", is(from)), hasProperty("activeUntil", is(to)),
						hasProperty("catalogVersion", is(catalogVersion))));

		assertThat(contentSlotForPageModel,
				allOf(hasProperty("uid", is("contentSlotForPage-generatedKey")), hasProperty("catalogVersion", is(catalogVersion)),
						hasProperty("position", is("position1")), hasProperty("page", is(page)),
						hasProperty("contentSlot", is(contentSlotModel))));

	}

	@Test
	public void willReturnOnlyContentSlotsForPage()
	{
		// Setup
		final List<ContentSlotForPageModel> expectedContentSlotsForPage = generateContentSlotForPageModelMocks();
		when(cmsContentSlotDao.findAllContentSlotRelationsByPage(any(AbstractPageModel.class)))
				.thenReturn(expectedContentSlotsForPage);

		// Act
		final Collection<ContentSlotData> actualContentSlotsForPage = cmsAdminContentSlotService.getContentSlotsForPage(page,
				false);

		// Assert
		assertEquals(expectedContentSlotsForPage.size(), actualContentSlotsForPage.size());

		verify(cmsContentSlotDao, never()).findAllContentSlotRelationsByPageTemplate(template);
	}

	@Test
	public void willReturnContentSlotsForPageAndForMasterTemplate()
	{
		// Setup
		final List<ContentSlotForPageModel> expectedContentSlotsForPage = generateContentSlotForPageModelMocks();
		when(cmsContentSlotDao.findAllContentSlotRelationsByPage(any(AbstractPageModel.class)))
				.thenReturn(expectedContentSlotsForPage);

		final List<ContentSlotForTemplateModel> expectedContentSlotsForTemplate = generateContentSlotForTemplateModelMocks(false);
		when(cmsContentSlotDao.findAllContentSlotRelationsByPageTemplate(any(PageTemplateModel.class)))
				.thenReturn(expectedContentSlotsForTemplate);

		final int expectedContentSlotsForPageCount = calculateNumberExpectedContentSlotsForPage(expectedContentSlotsForPage.size(),
				expectedContentSlotsForTemplate.size(), false);
		// Act
		final Collection<ContentSlotData> actualContentSlotsForPage = cmsAdminContentSlotService.getContentSlotsForPage(page, true);

		// Assert
		assertEquals(expectedContentSlotsForPageCount, actualContentSlotsForPage.size());
	}

	@Test
	public void willReturnCustomContentSlotsInTemplateSlotPositions()
	{
		// Setup
		final List<ContentSlotForPageModel> expectedContentSlotsForPage = generateContentSlotForPageModelMocks();
		when(cmsContentSlotDao.findAllContentSlotRelationsByPage(any(AbstractPageModel.class)))
				.thenReturn(expectedContentSlotsForPage);

		final List<ContentSlotForTemplateModel> expectedContentSlotsForTemplate = generateContentSlotForTemplateModelMocks(true);
		when(cmsContentSlotDao.findAllContentSlotRelationsByPageTemplate(any(PageTemplateModel.class)))
				.thenReturn(expectedContentSlotsForTemplate);

		final int expectedContentSlotsForPageCount = calculateNumberExpectedContentSlotsForPage(expectedContentSlotsForPage.size(),
				expectedContentSlotsForTemplate.size(), true);

		// Act
		final Collection<ContentSlotData> actualContentSlotsForPage = cmsAdminContentSlotService.getContentSlotsForPage(page, true);

		// Assert
		assertEquals(expectedContentSlotsForPageCount, actualContentSlotsForPage.size());
	}

	@Test
	public void willReturnCustomContentSlotsInTemplateSlotPositionsAndSetOverrideToTrue()
	{
		// Setup
		final List<ContentSlotForPageModel> expectedContentSlotsForPage = new ArrayList<ContentSlotForPageModel>();
		final ContentSlotForPageModel contentSlotForPageModel = mock(ContentSlotForPageModel.class);
		when(contentSlotForPageModel.getPosition()).thenReturn(CONTENT_SLOT_POSITION);

		final ContentSlotData contentSlotData = mock(ContentSlotData.class);
		when(contentSlotData.getPosition()).thenReturn(CONTENT_SLOT_POSITION);

		when(cmsDataFactory.createContentSlotData(contentSlotForPageModel)).thenReturn(contentSlotData);

		expectedContentSlotsForPage.add(contentSlotForPageModel);


		when(cmsContentSlotDao.findAllContentSlotRelationsByPage(any(AbstractPageModel.class)))
				.thenReturn(expectedContentSlotsForPage);

		final List<ContentSlotForTemplateModel> expectedContentSlotsForTemplate = new ArrayList<ContentSlotForTemplateModel>();
		final ContentSlotForTemplateModel contentSlotForTemplateModel = mock(ContentSlotForTemplateModel.class);
		when(contentSlotForTemplateModel.getPosition()).thenReturn(CONTENT_SLOT_POSITION);

		expectedContentSlotsForTemplate.add(contentSlotForTemplateModel);

		when(cmsContentSlotDao.findAllContentSlotRelationsByPageTemplate(any(PageTemplateModel.class)))
				.thenReturn(expectedContentSlotsForTemplate);

		// Act
		final Collection<ContentSlotData> actualContentSlotsForPage = cmsAdminContentSlotService.getContentSlotsForPage(page, true);

		// Assert
		assertEquals(1, actualContentSlotsForPage.size());
		verify(contentSlotData).setIsOverrideSlot(true);
	}

	@Test
	public void willReturnPositionOfContentSlotFromPage()
	{
		final ContentSlotForPageModel contentSlotForPageModel = mock(ContentSlotForPageModel.class);
		when(contentSlotForPageModel.getPosition()).thenReturn(CONTENT_SLOT_POSITION);
		when(cmsContentSlotDao.findContentSlotRelationsByPageAndContentSlot(page, contentSlotModel, catalogVersion)).thenReturn(Collections.singletonList(contentSlotForPageModel));

		verify(cmsContentSlotDao, never()).findContentSlotRelationsByPageTemplateAndContentSlot(template, contentSlotModel, catalogVersion);

		assertThat(cmsAdminContentSlotService.getContentSlotPosition(page, contentSlotModel), equalTo(CONTENT_SLOT_POSITION));
	}

	@Test
	public void willReturnPositionOfContentSlotFromPageTemplate()
	{
		final ContentSlotForTemplateModel contentSlotForTemplateModel = mock(ContentSlotForTemplateModel.class);
		when(contentSlotForTemplateModel.getPosition()).thenReturn(CONTENT_SLOT_POSITION);
		when(cmsContentSlotDao.findContentSlotRelationsByPageAndContentSlot(page, contentSlotModel, catalogVersion)).thenReturn(Collections.emptyList());
		when(cmsContentSlotDao.findContentSlotRelationsByPageTemplateAndContentSlot(template, contentSlotModel, catalogVersion)).thenReturn(Collections.singletonList(contentSlotForTemplateModel));

		assertThat(cmsAdminContentSlotService.getContentSlotPosition(page, contentSlotModel), equalTo(CONTENT_SLOT_POSITION));
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowTypePermissionExceptionWhenAddComponentToSlotAndIfSlotDoesNotHaveChangePermission()
	{
		// GIVEN
		when(permissionCRUDService.canChangeType(contentSlotModel.getItemtype())).thenReturn(false);
		doThrow(new TypePermissionException("invalid")).when(cmsAdminContentSlotService).throwTypePermissionException(
				PermissionsConstants.CHANGE, contentSlotModel.getItemtype());

		// WHEN
		cmsAdminContentSlotService.addCMSComponentToContentSlot(componentModel, contentSlotModel, 1);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowTypePermissionExceptionWhenUpdateComponentPositionInSlotAndIfSlotDoesNotHaveChangePermission()
	{
		// GIVEN
		when(permissionCRUDService.canChangeType(contentSlotModel.getItemtype())).thenReturn(false);
		doThrow(new TypePermissionException("invalid")).when(cmsAdminContentSlotService).throwTypePermissionException(
				PermissionsConstants.CHANGE, contentSlotModel.getItemtype());

		// WHEN
		cmsAdminContentSlotService.updatePositionCMSComponentInContentSlot(componentModel, contentSlotModel, 1);
	}

}

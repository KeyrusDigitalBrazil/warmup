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
package com.hybris.backoffice.config.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.MockSessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.daos.BackofficeConfigurationDao;
import com.hybris.backoffice.model.BackofficeConfigurationMediaModel;
import com.hybris.cockpitng.core.Widget;
import com.hybris.cockpitng.core.persistence.impl.jaxb.Widgets;
import com.hybris.cockpitng.core.persistence.packaging.CockpitClassLoader;
import com.hybris.cockpitng.core.persistence.packaging.WidgetLibUtils;
import com.hybris.cockpitng.core.spring.CockpitApplicationContext;
import com.hybris.cockpitng.modules.CockpitModuleConnector;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeWidgetPersistenceServiceUnitTest
{
	private static final String WIDGET_CONFIG_MEDIA = BackofficeWidgetPersistenceService.WIDGET_CONFIG_MEDIA;
	private static final String SECURED_FOLDER = "securedFolder";

	@Mock
	private ModelService modelService;
	@Mock
	private MediaService mediaService;
	@Spy
	@InjectMocks
	private BackofficeWidgetPersistenceServiceStub service;
	@Mock
	private WidgetLibUtils widgetLibUtils;
	@Mock
	private CockpitModuleConnector cockpitModuleConnector;
	@Mock
	private BackofficeConfigurationDao configurationDao;
	@Mock
	private SearchRestrictionService searchRestrictionService;
	@Mock(answer = Answers.CALLS_REAL_METHODS)
	private MockSessionService sessionService;
	@InjectMocks
	@Spy
	private DefaultBackofficeConfigurationMediaHelper backofficeConfigurationMediaHelper;
	@Mock
	private MediaStorageConfigService mediaStorageConfigService;
	@Mock
	private UserService userService;


	@Before
	public void setUp()
	{
		service.setBackofficeConfigurationMediaHelper(backofficeConfigurationMediaHelper);
	}

	@Test
	public void loadWidgetTree()
	{
		final MediaModel mediaModel = mock(MediaModel.class);
		final InputStream inputStream = mock(InputStream.class);
		final Widget widget = mock(Widget.class);
		doReturn(mediaModel).when(service).getOrCreateWidgetsConfigMedia();
		when(mediaService.getStreamFromMedia(mediaModel)).thenReturn(inputStream);
		doReturn(widget).when(service).loadWidgetTree("testId", inputStream);
		service.loadWidgetTree("testId");
		verify(service).getOrCreateWidgetsConfigMedia();
		verify(mediaService).getStreamFromMedia(mediaModel);
	}

	@Test
	public void testStoreWidgetTree()
	{
		final MediaModel mediaModel = mock(MediaModel.class);
		final InputStream inputStream = mock(InputStream.class);
		final Widget widget = mock(Widget.class);
		final Widgets widgets = mock(Widgets.class);
		doReturn(mediaModel).when(service).getOrCreateWidgetsConfigMedia();
		doReturn(Boolean.TRUE).when(service).isWidgetsConfigStoredInMedia();
		doReturn(widgets).when(service).loadWidgets(inputStream);
		when(mediaService.getStreamFromMedia(mediaModel)).thenReturn(inputStream);
		doNothing().when(service).storeWidgetTree(any(), any(), any());
		service.storeWidgetTree(widget);
		verify(service).isStoringEnabled();
		verify(service).isWidgetsConfigStoredInMedia();
		verify(service).loadWidgets(inputStream);
		verify(service).storeWidgetTree(any(), any(), any());
		verify(mediaService).setDataForMedia(any(), any());
	}

	@Test
	public void testDeleteWidgetTree()
	{
		final MediaModel mediaModel = mock(MediaModel.class);
		final InputStream inputStream = mock(InputStream.class);
		final Widget widget = mock(Widget.class);
		final Widgets widgets = mock(Widgets.class);
		doReturn(mediaModel).when(service).getOrCreateWidgetsConfigMedia();
		doReturn(widgets).when(service).loadWidgets(inputStream);
		when(mediaService.getStreamFromMedia(mediaModel)).thenReturn(inputStream);
		doNothing().when(service).storeWidgetTree(any(), any(), any());
		service.deleteWidgetTree(widget);
		verify(service).loadWidgets(inputStream);
		verify(service).deleteWidgetTreeRecursive(widgets, widget);
		verify(service).deleteOrphanedConnections(widgets);
		verify(service).storeWidgets(any(), any());
		verify(mediaService).setDataForMedia(any(), any());
	}

	@Test
	public void testResetToDefaults()
	{
		final MediaModel media = mock(MediaModel.class);
		doReturn(null).when(mediaService).getMedia(WIDGET_CONFIG_MEDIA);
		doReturn(media).when(modelService).create(BackofficeConfigurationMediaModel.class);
		doNothing().when(service).putDefaultWidgetsConfig(media);

		when(widgetLibUtils.libDirAbsolutePath()).thenReturn(StringUtils.EMPTY);
		when(cockpitModuleConnector.getCockpitModuleUrls()).thenReturn(Collections.emptyList());

		final CockpitApplicationContext applicationContext = mock(CockpitApplicationContext.class);
		final CockpitClassLoader cockpitClassLoader = mock(CockpitClassLoader.class);
		when(applicationContext.getClassLoader()).thenReturn(cockpitClassLoader);
		service.setApplicationContext(applicationContext);

		service.resetToDefaults();
		verify(backofficeConfigurationMediaHelper).findWidgetsConfigMedia(any());
		verify(backofficeConfigurationMediaHelper).createWidgetsConfigMedia(any(), any());
		verify(mediaService).removeDataFromMediaQuietly(media);
		verify(service).putDefaultWidgetsConfig(media);
	}

	@Test
	public void testIsWidgetsConfigStoredInMedia()
	{
		final MediaModel media = mock(MediaModel.class);
		media.setCode(WIDGET_CONFIG_MEDIA);
		when(mediaService.getMedia(WIDGET_CONFIG_MEDIA)).thenReturn(media);
		when(Boolean.valueOf(mediaService.hasData(media))).thenReturn(Boolean.FALSE);

		assertThat(service.isWidgetsConfigStoredInMedia()).isFalse();
		when(Boolean.valueOf(mediaService.hasData(media))).thenReturn(Boolean.TRUE);
		assertThat(service.isWidgetsConfigStoredInMedia()).isTrue();
	}

	@Test
	public void testPutDefaultWidgetsConfig()
	{
		final MediaModel media = mock(MediaModel.class);
		final Widgets widgets = mock(Widgets.class);
		final InputStream inputStream = mock(InputStream.class);
		doReturn(inputStream).when(service).getDefaultWidgetsConfigInputStream();
		doReturn(widgets).when(service).loadWidgets(inputStream);
		service.putDefaultWidgetsConfig(media);
		verify(service).loadWidgets(inputStream);
		verify(service).applyImports(new File("."), widgets, new HashSet<>());
		verify(service).applyExtensions(widgets);
		verify(service).storeWidgets(any(Widgets.class), any(OutputStream.class));
		verify(mediaService).setDataForMedia(any(MediaModel.class), any(byte[].class));
	}

	@Test
	public void loadWidgetTreeTest()
	{
		// assign
		final String widgetId = "widget";
		final Widget widget = Mockito.mock(Widget.class);
		final MediaModel media = Mockito.mock(MediaModel.class);
		final InputStream input = Mockito.mock(InputStream.class);
		final MediaFolderModel mediaFolder = mock(MediaFolderModel.class);

		doReturn(SECURED_FOLDER).when(mediaFolder).getQualifier();
		doReturn(media).when(mediaService).getMedia(WIDGET_CONFIG_MEDIA);
		doReturn(input).when(mediaService).getStreamFromMedia(media);
		doReturn(widget).when(service).loadWidgetTree(widgetId, input);
		doReturn(mediaFolder).when(media).getFolder();
		doReturn(Collections.singletonList(SECURED_FOLDER)).when(mediaStorageConfigService).getSecuredFolders();

		// act
		final Widget result = service.loadWidgetTree(widgetId);

		// assert
		assertThat(result).isEqualTo(widget);
		verify(service).loadWidgetTree(widgetId);
		verify(service).loadWidgetTree(widgetId, input);
	}



	/**
	 * Class to stub protected methods from super class
	 */
	public static class BackofficeWidgetPersistenceServiceStub extends BackofficeWidgetPersistenceService
	{
		/**
		 * @deprecated since 6.5
		 */
		@Deprecated
		@Override
		protected boolean applyImports(final File currentPath, final Widgets widgets, final Set<String> alreadyImportedResources)
		{
			return true;
		}

		@Override
		protected void applyExtensions(final Widgets widgets)
		{
			// do nothing
		}

		@Override
		protected Widgets loadWidgets(final InputStream inputStream)
		{
			return new Widgets();
		}

		@Override
		protected void storeWidgets(final Widgets widgets, final OutputStream outputStream)
		{
			// do nothing
		}

		@Override
		protected void deleteWidgetTreeRecursive(final Widgets widgets, final Widget node)
		{
			// do nothing
		}

		@Override
		protected void deleteOrphanedConnections(final Widgets widgets)
		{
			// do nothing
		}
	}
}

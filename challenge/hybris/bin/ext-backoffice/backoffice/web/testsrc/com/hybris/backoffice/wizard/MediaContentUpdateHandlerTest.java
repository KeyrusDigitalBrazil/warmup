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
package com.hybris.backoffice.wizard;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.editor.defaultfileupload.FileUploadResult;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


@RunWith(MockitoJUnitRunner.class)
public class MediaContentUpdateHandlerTest
{
	public static final String NEW_MEDIA = "newMedia";
	public static final String MEDIA_CONTENT = "mediaContent";
	@InjectMocks
	private MediaContentUpdateHandler handler;
	@Mock
	private MediaService mediaService;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private FlowActionHandlerAdapter adapter;
	@Mock
	private CustomType customType;
	@Mock
	private MediaModel newMedia;
	@Mock
	private FileUploadResult mediaContent;
	private HashMap<String, String> params;

	@Before
	public void setUp()
	{
		final WidgetInstanceManager widgetInstanceManager = CockpitTestUtil.mockWidgetInstanceManager();

		params = new HashMap<>();
		params.put(MediaContentUpdateHandler.MEDIA_CONTENT_PROPERTY, MEDIA_CONTENT);
		params.put(MediaContentUpdateHandler.MEDIA_PROPERTY, NEW_MEDIA);

		when(adapter.getWidgetInstanceManager()).thenReturn(widgetInstanceManager);
		widgetInstanceManager.getModel().setValue(NEW_MEDIA, newMedia);
		widgetInstanceManager.getModel().setValue(MEDIA_CONTENT, mediaContent);
	}

	@Test
	public void shouldNotFinishWhenMediaIsNotPersisted()
	{
		when(objectFacade.isModified(newMedia)).thenReturn(true);

		handler.perform(customType, adapter, params);

		verify(adapter, never()).done();
	}

	@Test
	public void shouldFinishWhenMediaIsPersisted()
	{
		when(objectFacade.isModified(newMedia)).thenReturn(false);

		handler.perform(customType, adapter, params);

		verify(adapter).done();
	}

	@Test
	public void shouldSetMetaData()
	{
		when(objectFacade.isModified(newMedia)).thenReturn(false);
		when(mediaContent.getName()).thenReturn("product-image.gif");
		when(mediaContent.getContentType()).thenReturn("image/gif");
		when(mediaContent.getData()).thenReturn(new byte[1]);

		handler.perform(customType, adapter, params);

		verify(newMedia).setRealFileName(mediaContent.getName());
		verify(newMedia).setMime(mediaContent.getContentType());
		verify(mediaService).setDataForMedia(newMedia, mediaContent.getData());
	}

}

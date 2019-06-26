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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.core.expression.ExpressionResolver;
import com.hybris.cockpitng.core.expression.ExpressionResolverFactory;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectCreationException;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectSavingException;
import com.hybris.cockpitng.editor.defaultfileupload.FileUploadResult;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


@RunWith(MockitoJUnitRunner.class)
public class MediaReferenceCreateHandlerTest
{
	@InjectMocks
	@Spy
	private MediaReferenceCreateHandler handler;

	@Mock
	private MediaService mediaService;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private ExpressionResolverFactory expressionResolverFactory;
	@Mock
	private FlowActionHandlerAdapter flowActionHandlerAdapter;
	private WidgetInstanceManager wim;
	@Mock
	private FileUploadResult mediaContent;
	@Mock
	private ExpressionResolver resolver;
	@Mock
	private TypeService typeService;
	@Mock
	private NotificationService notificationService;

	@Before
	public void setUp()
	{
		doNothing().when(handler).beginTransaction();
		doNothing().when(handler).rollbackTransaction();
		doNothing().when(handler).commitTransaction();
		wim = CockpitTestUtil.mockWidgetInstanceManager();
		when(expressionResolverFactory.createResolver()).thenReturn(resolver);
		when(flowActionHandlerAdapter.getWidgetInstanceManager()).thenReturn(wim);
		when(mediaContent.getData()).thenReturn(new byte[0]);
		when(mediaContent.getName()).thenReturn("uploadedMedia");
	}

	@Test
	public void shouldSaveUploadedContentOnProduct() throws ObjectCreationException, ObjectSavingException
	{
		//given
		final HashMap<String, String> params = new HashMap<>();
		params.put(MediaReferenceCreateHandler.PARAM_SAVE_PARENT_OBJECT, "true");
		final ProductModel product = mock(ProductModel.class);
		wim.getModel().put("newProduct", product);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_PROPERTY, "newProduct.picture");
		wim.getModel().put("newProduct.code", "testProductCode");
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_CODE_EXP, "newProduct.code");

		wim.getModel().put("productPicture", mediaContent);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_CONTENT_PROPERTY, "productPicture");

		final CatalogVersionModel cv = mock(CatalogVersionModel.class);
		wim.getModel().setValue("newProduct.catalogVersion", cv);
		params.put(MediaReferenceCreateHandler.PARAM_CATALOG_VERSION, "newProduct.catalogVersion");

		final MediaFormatModel format = mock(MediaFormatModel.class);
		wim.getModel().setValue("mediaFormat", format);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_FORMAT, "mediaFormat");

		final MediaFolderModel folder = mock(MediaFolderModel.class);
		wim.getModel().setValue("mediaFolder", folder);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_FOLDER, "mediaFolder");

		final MediaContainerModel container = mock(MediaContainerModel.class);
		wim.getModel().setValue("mediaContainer", container);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_CONTAINER, "mediaContainer");

		final MediaModel media = mock(MediaModel.class);
		when(objectFacade.create(MediaModel._TYPECODE)).thenReturn(media);

		//when
		handler.perform(null, flowActionHandlerAdapter, params);

		//then
		verify(handler).beginTransaction();
		verify(handler).commitTransaction();
		verify(flowActionHandlerAdapter).done();
		verify(mediaService).setDataForMedia(media, mediaContent.getData());
		verify(resolver).setValue(product, "picture", media);
		verify(objectFacade).save(product);
		verify(media).setMediaFormat(format);
		verify(media).setFolder(folder);
		verify(media).setMediaContainer(container);
		verify(media).setCode("testProductCode");
	}

	@Test
	public void shouldNotCallDoneWhenObjectIsModified() throws ObjectSavingException
	{
		//given
		final HashMap<String, String> params = new HashMap<>();
		params.put(MediaReferenceCreateHandler.PARAM_SAVE_PARENT_OBJECT, "true");
		final ProductModel product = mock(ProductModel.class);
		wim.getModel().put("newProduct", product);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_PROPERTY, "newProduct.picture");
		wim.getModel().put("newProduct.code", "testProductCode");
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_CODE_EXP, "newProduct.code");

		wim.getModel().put("productPicture", mediaContent);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_CONTENT_PROPERTY, "productPicture");

		when(objectFacade.isModified(product)).thenReturn(true);

		//when
		handler.perform(null, flowActionHandlerAdapter, params);

		//then
		verify(handler, never()).beginTransaction();
		verify(handler, never()).commitTransaction();
		verify(flowActionHandlerAdapter, never()).done();
		verify(objectFacade, never()).save(product);
	}

	@Test
	public void shouldRollbackOnSetMediaDataError() throws ObjectSavingException, ObjectCreationException
	{
		//given
		final HashMap<String, String> params = new HashMap<>();
		params.put(MediaReferenceCreateHandler.PARAM_SAVE_PARENT_OBJECT, "true");
		final ProductModel product = mock(ProductModel.class);
		wim.getModel().put("newProduct", product);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_PROPERTY, "newProduct.picture");
		wim.getModel().put("newProduct.code", "testProductCode");
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_CODE_EXP, "newProduct.code");

		wim.getModel().put("productPicture", mediaContent);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_CONTENT_PROPERTY, "productPicture");

		final CatalogVersionModel cv = mock(CatalogVersionModel.class);
		wim.getModel().setValue("newProduct.catalogVersion", cv);
		params.put(MediaReferenceCreateHandler.PARAM_CATALOG_VERSION, "newProduct.catalogVersion");


		final MediaModel media = mock(MediaModel.class);
		when(objectFacade.create(MediaModel._TYPECODE)).thenReturn(media);

		doThrow(new IllegalArgumentException()).when(mediaService).setDataForMedia(any(), any());

		//when
		handler.perform(null, flowActionHandlerAdapter, params);

		//then
		verify(handler).beginTransaction();
		verify(handler).rollbackTransaction();
		verify(handler).notifyCreateMediaFailed(mediaContent.getName());
		verify(notificationService).notifyUser("mediaReferenceHandler", "createMediaReference", NotificationEvent.Level.FAILURE,
				mediaContent.getName());
		verify(flowActionHandlerAdapter).done();
	}

	@Test
	public void shouldRollbackOnSaveReferenceError() throws ObjectSavingException, ObjectCreationException
	{
		//given
		final HashMap<String, String> params = new HashMap<>();
		params.put(MediaReferenceCreateHandler.PARAM_SAVE_PARENT_OBJECT, "true");
		final ProductModel product = mock(ProductModel.class);
		wim.getModel().put("newProduct", product);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_PROPERTY, "newProduct.picture");
		wim.getModel().put("newProduct.code", "testProductCode");
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_CODE_EXP, "newProduct.code");

		wim.getModel().put("productPicture", mediaContent);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_CONTENT_PROPERTY, "productPicture");

		final CatalogVersionModel cv = mock(CatalogVersionModel.class);
		wim.getModel().setValue("newProduct.catalogVersion", cv);
		params.put(MediaReferenceCreateHandler.PARAM_CATALOG_VERSION, "newProduct.catalogVersion");


		final MediaModel media = mock(MediaModel.class);
		when(objectFacade.create(MediaModel._TYPECODE)).thenReturn(media);

		doThrow(new IllegalArgumentException()).when(objectFacade).save(product);

		//when
		handler.perform(null, flowActionHandlerAdapter, params);

		//then
		verify(handler).beginTransaction();
		verify(handler).rollbackTransaction();
		verify(handler).notifyCreateMediaFailed(mediaContent.getName());
		verify(notificationService).notifyUser("mediaReferenceHandler", "createMediaReference", NotificationEvent.Level.FAILURE,
				mediaContent.getName());
		verify(flowActionHandlerAdapter).done();
	}

	@Test
	public void shouldNotRequireCatalogVersionForCatalogUnawareMedia() throws ObjectCreationException, ObjectSavingException
	{
		//given
		final HashMap<String, String> params = new HashMap<>();
		params.put(MediaReferenceCreateHandler.PARAM_SAVE_PARENT_OBJECT, "true");
		final ProductModel product = mock(ProductModel.class);
		wim.getModel().put("newProduct", product);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_PROPERTY, "newProduct.picture");
		wim.getModel().put("newProduct.code", "testProductCode");
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_CODE_EXP, "newProduct.code");

		wim.getModel().put("productPicture", mediaContent);
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_CONTENT_PROPERTY, "productPicture");


		final String catalogUnawareMediaSubType = "CatalogUnawareMediaSubType";
		params.put(MediaReferenceCreateHandler.PARAM_MEDIA_TYPE, catalogUnawareMediaSubType);
		when(typeService.isAssignableFrom(CatalogUnawareMediaModel._TYPECODE, catalogUnawareMediaSubType)).thenReturn(true);
		when(typeService.isAssignableFrom(MediaModel._TYPECODE, catalogUnawareMediaSubType)).thenReturn(true);

		final MediaModel media = mock(MediaModel.class);
		when(objectFacade.create(catalogUnawareMediaSubType)).thenReturn(media);

		//when
		handler.perform(null, flowActionHandlerAdapter, params);

		//then
		verify(handler).beginTransaction();
		verify(handler).commitTransaction();
		verify(flowActionHandlerAdapter).done();
		verify(mediaService).setDataForMedia(media, mediaContent.getData());
		verify(resolver).setValue(product, "picture", media);
		verify(objectFacade).save(product);
		verify(media).setCode("testProductCode");
	}


}

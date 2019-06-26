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
package de.hybris.platform.acceleratorservices.document.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.document.context.AbstractDocumentContext;
import de.hybris.platform.acceleratorservices.document.factory.DocumentContextFactory;
import de.hybris.platform.acceleratorservices.document.service.DocumentPageService;
import de.hybris.platform.acceleratorservices.document.strategy.DocumentCatalogFetchStrategy;
import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageTemplateModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDocumentGenerationServiceTest
{
	private static final String FRONTEND_TEMPLATENAME = "frontendTemplateName";

	@InjectMocks
	private DefaultDocumentGenerationService documentGenerationService = new DefaultDocumentGenerationService();

	@Mock
	private RendererService rendererService;
	@Mock
	private MediaService mediaService;
	@Mock
	private GuidKeyGenerator guidKeyGenerator;
	@Mock
	private DocumentContextFactory<BusinessProcessModel> documentContextFactory;
	@Mock
	private DocumentCatalogFetchStrategy documentCatalogFetchStrategy;
	@Mock
	private ModelService modelService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private DocumentPageService documentPageService;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private DocumentPageModel documentPageModel;

	@Test
	public void testGenerate()
	{
		//Given
		final MediaModel mediaModel = mock(MediaModel.class);
		final DocumentPageModel documentPageModel = mock(DocumentPageModel.class);
		final ConsignmentProcessModel businessProcessModel = mock(ConsignmentProcessModel.class);
		final DocumentPageTemplateModel documentPageTemplateModel = mock(DocumentPageTemplateModel.class);
		final RendererTemplateModel renderTemplate = mock(RendererTemplateModel.class);
		final AbstractDocumentContext documentContext = mock(AbstractDocumentContext.class);
		when(documentPageModel.getMasterTemplate()).thenReturn(documentPageTemplateModel);
		when(documentPageTemplateModel.getHtmlTemplate()).thenReturn(renderTemplate);
		when(documentContextFactory.create(businessProcessModel, documentPageModel, renderTemplate)).thenReturn(documentContext);
		when(renderTemplate.getContextClass()).thenReturn("TestDocumentContext");
		when(guidKeyGenerator.generate()).thenReturn("TestDocumentUID");
		when(modelService.create(MediaModel.class)).thenReturn(mediaModel);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Collections.singleton(catalogVersion));
		when(documentPageService.findDocumentPageByTemplateName(anyString(), anyCollection())).thenReturn(documentPageModel);

		//When
		MediaModel result = null;
		result = documentGenerationService.generate(FRONTEND_TEMPLATENAME, businessProcessModel);

		//Then
		verify(rendererService, times(1)).render(any(RendererTemplateModel.class), any(Map.class), any(StringWriter.class));
		assertEquals(mediaModel, result);
	}
}

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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.document.context.AbstractHybrisVelocityContext;
import de.hybris.platform.acceleratorservices.document.factory.DocumentContextFactory;
import de.hybris.platform.acceleratorservices.document.service.DocumentGenerationService;
import de.hybris.platform.acceleratorservices.document.service.DocumentPageService;
import de.hybris.platform.acceleratorservices.document.strategy.DocumentCatalogFetchStrategy;
import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageTemplateModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Service to generate Document from velocity template.
 */
public class DefaultDocumentGenerationService implements DocumentGenerationService
{
	public static final String DOCUMENT_BODY_ENCODING = "UTF-8";

	private String mimeType;
	private String documentMediaFolderName;
	private ModelService modelService;
	private RendererService rendererService;
	private MediaService mediaService;
	private GuidKeyGenerator guidKeyGenerator;
	private DocumentContextFactory<BusinessProcessModel> documentContextFactory;
	private DocumentCatalogFetchStrategy documentCatalogFetchStrategy;

	private CatalogVersionService catalogVersionService;
	private DocumentPageService documentPageService;


	private static final Logger LOG = Logger.getLogger(DefaultDocumentGenerationService.class);

	@Override
	public MediaModel generate(final String frontendTemplateName, final BusinessProcessModel businessProcessModel)
	{
		validateParameterNotNull(frontendTemplateName, "Template code cannot be null");
		validateParameterNotNull(businessProcessModel, "BusinessProcessModel cannot be null");
		final Collection<CatalogVersionModel> contentCatalogVersions = getCatalogVersionService().getSessionCatalogVersions();
		Assert.isTrue(CollectionUtils.isNotEmpty(contentCatalogVersions), "Catalog Version cannot be empty");

		final DocumentPageModel documentPageModel = getDocumentPageService().findDocumentPageByTemplateName(frontendTemplateName,
				contentCatalogVersions);
		validateParameterNotNull(documentPageModel, "DocumentPageModel cannot be null");
		Assert.isInstanceOf(DocumentPageTemplateModel.class, documentPageModel.getMasterTemplate(),
				"MasterTemplate associated with DocumentPageModel should be EmailPageTemplate");

		final DocumentPageTemplateModel documentPageTemplateModel = (DocumentPageTemplateModel) documentPageModel
				.getMasterTemplate();
		final RendererTemplateModel documentRendererTemplate = documentPageTemplateModel.getHtmlTemplate();
		validateParameterNotNull(documentRendererTemplate, "Render template cannot be null");

		MediaModel mediaModel = null;
		//This call creates the context to be used for rendering document template.
		final AbstractHybrisVelocityContext<BusinessProcessModel> documentContext = getDocumentContextFactory()
				.create(businessProcessModel, documentPageModel, documentRendererTemplate);

		if (documentContext == null)
		{
			LOG.error("Failed to create context for businessProcess [" + businessProcessModel + "]");
			throw new IllegalStateException("Failed to create context for businessProcess [" + businessProcessModel + "]");
		}
		else
		{
			final StringWriter document = new StringWriter();
			getRendererService().render(documentRendererTemplate, documentContext, document);

			mediaModel = createMedia("documentMedia-" + getGuidKeyGenerator().generate().toString(), document.toString(),
					businessProcessModel);
		}
		return mediaModel;
	}

	/**
	 * Method creates MediaModel object for storing document
	 *
	 * @param documentBody
	 *           - content of document
	 * @return created MediaModel object
	 */
	protected MediaModel createMedia(final String documentMediaName, final String documentBody,
			final BusinessProcessModel businessProcessModel)
	{
		validateParameterNotNull("Document Body cannot be null", documentBody);

		final MediaModel documentMedia = getModelService().create(MediaModel.class);
		documentMedia.setCode(documentMediaName);
		documentMedia.setMime(getMimeType());
		documentMedia.setRealFileName(documentMediaName);
		documentMedia.setCatalogVersion(getDocumentCatalogFetchStrategy().fetch(businessProcessModel));
		getModelService().save(documentMedia);

		final MediaFolderModel mediaFolderModel = getDocumentMediaFolder();
		InputStream dataStream = null;
		try
		{
			try
			{
				dataStream = new ByteArrayInputStream(documentBody.getBytes(DOCUMENT_BODY_ENCODING));
			}
			catch (final UnsupportedEncodingException e)
			{
				dataStream = new ByteArrayInputStream(documentBody.getBytes());
				LOG.warn("document content - UnsupportedEncodingException", e);
			}
			getMediaService().setStreamForMedia(documentMedia, dataStream, documentMediaName, getMimeType(), mediaFolderModel);
		}
		finally
		{
			if (dataStream != null)
			{
				try
				{
					dataStream.close();
				}
				catch (final IOException e)
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug(e);
					}
				}
			}
		}
		return documentMedia;
	}

	/**
	 * Gets the {@link MediaFolderModel} to save the generated Media
	 *
	 * @return the {@link MediaFolderModel}
	 */
	protected MediaFolderModel getDocumentMediaFolder()
	{
		return getMediaService().getFolder(getDocumentMediaFolderName());
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected RendererService getRendererService()
	{
		return rendererService;
	}

	@Required
	public void setRendererService(final RendererService rendererService)
	{
		this.rendererService = rendererService;
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected DocumentContextFactory<BusinessProcessModel> getDocumentContextFactory()
	{
		return documentContextFactory;
	}

	@Required
	public void setDocumentContextFactory(final DocumentContextFactory<BusinessProcessModel> documentContextFactory)
	{
		this.documentContextFactory = documentContextFactory;
	}

	protected DocumentPageService getDocumentPageService()
	{
		return documentPageService;
	}

	@Required
	public void setDocumentPageService(final DocumentPageService documentPageService)
	{
		this.documentPageService = documentPageService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	@Required
	public void setDocumentMediaFolderName(final String documentMediaFolderName)
	{
		this.documentMediaFolderName = documentMediaFolderName;
	}

	protected String getDocumentMediaFolderName()
	{
		return documentMediaFolderName;
	}

	protected GuidKeyGenerator getGuidKeyGenerator()
	{
		return guidKeyGenerator;
	}

	@Required
	public void setGuidKeyGenerator(final GuidKeyGenerator guidKeyGenerator)
	{
		this.guidKeyGenerator = guidKeyGenerator;
	}

	protected DocumentCatalogFetchStrategy getDocumentCatalogFetchStrategy()
	{
		return documentCatalogFetchStrategy;
	}

	@Required
	public void setDocumentCatalogFetchStrategy(final DocumentCatalogFetchStrategy documentCatalogFetchStrategy)
	{
		this.documentCatalogFetchStrategy = documentCatalogFetchStrategy;
	}

	@Required
	public void setMimeType(final String mimeType)
	{
		this.mimeType = mimeType;
	}

	protected String getMimeType()
	{
		return mimeType;
	}
}

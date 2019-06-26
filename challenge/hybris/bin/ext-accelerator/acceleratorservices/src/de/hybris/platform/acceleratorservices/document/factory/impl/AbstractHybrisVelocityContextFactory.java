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
package de.hybris.platform.acceleratorservices.document.factory.impl;

import de.hybris.platform.acceleratorservices.document.context.AbstractHybrisVelocityContext;
import de.hybris.platform.acceleratorservices.process.strategies.EmailTemplateTranslationStrategy;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.commons.renderer.daos.RendererTemplateDao;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * Default factory used to create the velocity context for rendering document
 */
public abstract class AbstractHybrisVelocityContextFactory
{
	private EmailTemplateTranslationStrategy emailTemplateTranslationStrategy;
	private CMSPageService cmsPageService;
	private CMSComponentService cmsComponentService;
	private TypeService typeService;
	private ModelService modelService;
	private RendererService rendererService;
	private ProcessContextResolutionStrategy<CMSSiteModel> contextResolutionStrategy;
	private RendererTemplateDao rendererTemplateDao;
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	private static final Logger LOG = LoggerFactory.getLogger(AbstractHybrisVelocityContextFactory.class);

	public void renderCMSSlotsIntoContext(final AbstractHybrisVelocityContext<BusinessProcessModel> context,
			final AbstractPageModel pageModel, final BusinessProcessModel businessProcessModel)
	{
		final Map<String, String> cmsSlotContents = new HashMap<>();

		getCmsPageService().getContentSlotsForPage(pageModel).forEach(contentSlotData ->
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Starting to prodess Content Slot: " + contentSlotData.getName() + "...");
			}

			final String contentPosition = contentSlotData.getPosition();
			final String renderedComponent = renderComponents(contentSlotData.getContentSlot(), context, businessProcessModel);
			cmsSlotContents.put(contentPosition, renderedComponent);

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Content Slot Position: " + contentPosition);
				LOG.debug("Renedered Component: " + renderedComponent);

				LOG.debug("Finished Processing Content Slot: " + contentSlotData.getName());
			}
		});

		context.setCmsSlotContents(cmsSlotContents);
	}

	protected String renderComponents(final ContentSlotModel contentSlotModel,
			final AbstractHybrisVelocityContext<BusinessProcessModel> context, final BusinessProcessModel businessProcessModel)
	{
		final StringWriter text = new StringWriter();

		contentSlotModel.getCmsComponents().forEach(component ->
		{
			final ComposedTypeModel componentType = getTypeService().getComposedTypeForClass(component.getClass());
			if (Boolean.TRUE.equals(component.getVisible())
					&& !getCmsComponentService().isComponentContainer(componentType.getCode()))
			{
				final String renderTemplateCode = resolveRendererTemplateForComponent(component, businessProcessModel);
				final List<RendererTemplateModel> results = getRendererTemplateDao().findRendererTemplatesByCode(renderTemplateCode);
				final RendererTemplateModel renderTemplateModel = results.isEmpty() ? null : results.get(0);
				final BaseSiteModel site = getContextResolutionStrategy().getCmsSite(businessProcessModel);
				if (renderTemplateModel != null)
				{
					renderTemplate(context, text, component, renderTemplateCode, renderTemplateModel, site);
				}
				else
				{
					// Component won't get rendered in the documents.
					final String siteName = site == null ? null : site.getUid();

					LOG.error("Couldn't find render template for component [" + component.getUid() + "] of type ["
							+ componentType.getCode() + "] in slot [" + contentSlotModel.getUid() + "] for site [" + siteName
							+ "] during process [" + businessProcessModel + "]. Tried code [" + renderTemplateCode + "]");
				}
			}
		});

		return text.toString();
	}

	protected String resolveRendererTemplateForComponent(final AbstractCMSComponentModel component,final BusinessProcessModel businessProcessModel)
	{
		final BaseSiteModel site = getContextResolutionStrategy().getCmsSite(businessProcessModel);
		final ComposedTypeModel componentType = getTypeService().getComposedTypeForClass(component.getClass());

		return (site != null ? site.getUid() : "") + "-" + componentType.getCode() + "-template";
	}

	protected void renderTemplate(final AbstractHybrisVelocityContext<BusinessProcessModel> context, final StringWriter text,
			final AbstractCMSComponentModel component, final String renderTemplateCode, final RendererTemplateModel renderTemplate,
			final BaseSiteModel site)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Using Render Template Code: " + renderTemplateCode);
		}

		final Map<String, Object> componentContext = new HashMap<>();
		componentContext.put("parentContext", context);
		processProperties(component, componentContext);
		//insert services for usage at jsp/vm page
		componentContext.put("urlResolutionService", getSiteBaseUrlResolutionService());
		//insert cms site
		componentContext.put("site", site);

		getRendererService().render(renderTemplate, componentContext, text);
	}

	protected void processProperties(final AbstractCMSComponentModel component, final Map<String, Object> componentContext)
	{
		for (final String property : getCmsComponentService().getReadableEditorProperties(component))
		{
			try
			{
				final Object value = getModelService().getAttributeValue(component, property);
				componentContext.put(property, value);
			}
			catch (final AttributeNotSupportedException ignore)
			{
				// ignore
			}
		}
	}

	protected void appendTokensToBuffer(final AbstractHybrisVelocityContext<BusinessProcessModel> context,
			final Map.Entry<String, String> entry, final StringBuilder buffer)
	{
		final StringTokenizer tokenizer = new StringTokenizer(entry.getValue(), "{}");
		while (tokenizer.hasMoreElements())
		{
			final String token = tokenizer.nextToken();
			if (context.containsKey(token))
			{
				final Object tokenValue = context.get(token);
				if (tokenValue != null)
				{
					buffer.append(tokenValue.toString());
				}
			}
			else
			{
				buffer.append(token);
			}
		}
	}

	protected ApplicationContext getApplicationContext()
	{
		return Registry.getApplicationContext();
	}

	protected CMSPageService getCmsPageService()
	{
		return cmsPageService;
	}

	@Required
	public void setCmsPageService(final CMSPageService cmsPageService)
	{
		this.cmsPageService = cmsPageService;
	}


	protected CMSComponentService getCmsComponentService()
	{
		return cmsComponentService;
	}

	@Required
	public void setCmsComponentService(final CMSComponentService cmsComponentService)
	{
		this.cmsComponentService = cmsComponentService;
	}

	protected ProcessContextResolutionStrategy<CMSSiteModel> getContextResolutionStrategy()
	{
		return contextResolutionStrategy;
	}

	@Required
	public void setContextResolutionStrategy(final ProcessContextResolutionStrategy<CMSSiteModel> contextResolutionStrategy)
	{
		this.contextResolutionStrategy = contextResolutionStrategy;
	}

	protected SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	@Required
	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
	}

	protected RendererTemplateDao getRendererTemplateDao()
	{
		return rendererTemplateDao;
	}

	@Required
	public void setRendererTemplateDao(final RendererTemplateDao rendererTemplateDao)
	{
		this.rendererTemplateDao = rendererTemplateDao;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
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

	protected EmailTemplateTranslationStrategy getEmailTemplateTranslationStrategy()
	{
		return emailTemplateTranslationStrategy;
	}

	@Required
	public void setEmailTemplateTranslationStrategy(final EmailTemplateTranslationStrategy emailTemplateTranslationStrategy)
	{
		this.emailTemplateTranslationStrategy = emailTemplateTranslationStrategy;
	}

}

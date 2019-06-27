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
package de.hybris.platform.acceleratorservices.process.email.context.impl;

import de.hybris.platform.acceleratorservices.document.factory.impl.AbstractHybrisVelocityContextFactory;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.acceleratorservices.process.email.context.EmailContextFactory;
import de.hybris.platform.acceleratorservices.process.strategies.EmailTemplateTranslationStrategy;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
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

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;


/**
 * Default factory used to create the velocity context for rendering emails
 */
public class DefaultEmailContextFactory extends AbstractHybrisVelocityContextFactory implements EmailContextFactory<BusinessProcessModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultEmailContextFactory.class);

	private Map<String, String> emailContextVariables;
	private EmailTemplateTranslationStrategy emailTemplateTranslationStrategy;

	@Override
	public AbstractEmailContext<BusinessProcessModel> create(final BusinessProcessModel businessProcessModel,
			final EmailPageModel emailPageModel, final RendererTemplateModel renderTemplate)
	{
		final AbstractEmailContext<BusinessProcessModel> emailContext = resolveEmailContext(renderTemplate);
		emailContext.init(businessProcessModel, emailPageModel);
		renderCMSSlotsIntoEmailContext(emailContext, emailPageModel, businessProcessModel);

		// parse and populate the variable at the end
		parseVariablesIntoEmailContext(emailContext);

		final String languageIso = emailContext.getEmailLanguage() == null ? null : emailContext.getEmailLanguage().getIsocode();
		//Render translated messages from the email message resource bundles into the email context.
		emailContext.setMessages(getEmailTemplateTranslationStrategy().translateMessagesForTemplate(renderTemplate, languageIso));

		return emailContext;
	}

	protected <T extends AbstractEmailContext<BusinessProcessModel>> T resolveEmailContext(
			final RendererTemplateModel renderTemplate)
	{
		try
		{
			final Class<T> contextClass = (Class<T>) Class.forName(renderTemplate.getContextClass());
			final Map<String, T> emailContexts = getApplicationContext().getBeansOfType(contextClass);
			if (MapUtils.isNotEmpty(emailContexts))
			{
				return emailContexts.entrySet().iterator().next().getValue();
			}
			else
			{
				throw new IllegalStateException("Cannot find bean in application context for context class [" + contextClass + "]");
			}
		}
		catch (final ClassNotFoundException e)
		{
			LOG.error("failed to create email context", e);
			throw new IllegalStateException("Cannot find email context class", e);
		}
	}

	protected ApplicationContext getApplicationContext()
	{
		return super.getApplicationContext();
	}

	protected void renderCMSSlotsIntoEmailContext(final AbstractEmailContext<BusinessProcessModel> emailContext,
			final EmailPageModel emailPageModel, final BusinessProcessModel businessProcessModel)
	{
		super.renderCMSSlotsIntoContext(emailContext,emailPageModel,businessProcessModel);
	}

	protected String renderComponents(final ContentSlotModel contentSlotModel,
			final AbstractEmailContext<BusinessProcessModel> emailContext, final BusinessProcessModel businessProcessModel)
	{
		return super.renderComponents(contentSlotModel,emailContext,businessProcessModel);
	}

	protected void renderTemplate(final AbstractEmailContext<BusinessProcessModel> emailContext, final StringWriter text,
			final AbstractCMSComponentModel component, final String renderTemplateCode, final RendererTemplateModel renderTemplate,
			final BaseSiteModel site)
	{
		super.renderTemplate(emailContext,text,component,renderTemplateCode,renderTemplate,site);
	}

	protected void processProperties(final AbstractCMSComponentModel component, final Map<String, Object> componentContext)
	{
		super.processProperties(component,componentContext);
	}

	protected String resolveRendererTemplateForComponent(final AbstractCMSComponentModel component,
			final BusinessProcessModel businessProcessModel)
	{
		return super.resolveRendererTemplateForComponent(component,businessProcessModel);
	}

	protected void parseVariablesIntoEmailContext(final AbstractEmailContext<BusinessProcessModel> emailContext)
	{
		final Map<String, String> variables = getEmailContextVariables();
		if (variables != null)
		{
			for (final Map.Entry<String, String> entry : variables.entrySet())
			{
				final StringBuilder buffer = new StringBuilder();

				appendTokensToBuffer(emailContext, entry, buffer);

				emailContext.put(entry.getKey(), buffer.toString());
			}
		}
	}

	protected Map<String, String> getEmailContextVariables()
	{
		return emailContextVariables;
	}

	public void setEmailContextVariables(final Map<String, String> emailContextVariables)
	{
		this.emailContextVariables = emailContextVariables;
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

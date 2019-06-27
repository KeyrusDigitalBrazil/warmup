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
package de.hybris.platform.b2b.mail.impl;

import de.hybris.platform.b2b.mail.TemplateRenderer;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.security.InvalidParameterException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Required;


/**
 * Renders velocity templates defined by {@link RendererTemplateModel}'s.
 * 
 * @deprecated Since 4.4. Use {@link de.hybris.platform.commons.renderer.impl.VelocityTemplateRenderer}
 */
@Deprecated
public class VelocityTemplateRenderer implements TemplateRenderer
{
	private static final Logger LOG = Logger.getLogger(VelocityTemplateRenderer.class.getName());
	private MediaService mediaService;
	private CommonI18NService commonI18NService;
	private String encoding;

	/**
	 * Evaluates the content of the templates media via a velocity engine. I If an error occurs it is logged and the
	 * method terminates.
	 * 
	 * @param template
	 *           the template
	 * @param context
	 *           the context
	 * @param output
	 *           the output
	 */
	@Override
	public void render(final RendererTemplateModel template, final Object context, final Writer output)
	{
		InputStream is = null;
		InputStreamReader reader = null;
		try
		{
			final Class c = Thread.currentThread().getContextClassLoader().loadClass(template.getContextClass());
			if (!c.isAssignableFrom(context.getClass()))
			{
				throw new InvalidParameterException("The context class [" + context.getClass().getName()
						+ "] is not correctly defined.");
			}

			final MediaModel m = template.getContent();
			if (m == null)
			{
				throw new IllegalStateException("No content found for template " + template.getCode() + " and language "
						+ this.getCommonI18NService().getCurrentLanguage().getIsocode());
			}
			is = this.getMediaService().getStreamFromMedia(m);
			reader = new InputStreamReader(is, getEncoding());
			final VelocityContext ctx = new VelocityContext();
			ctx.put("ctx", context);
			Velocity.evaluate(ctx, output, template.getCode(), reader);
			output.flush();

		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		finally
		{
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(is);
		}
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

	protected String getEncoding()
	{
		return encoding;
	}

	@Required
	public void setEncoding(final String encoding)
	{
		this.encoding = encoding;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

}

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
package de.hybris.platform.acceleratorservices.process.strategies.impl;

import de.hybris.platform.acceleratorservices.process.strategies.EmailTemplateTranslationStrategy;
import de.hybris.platform.acceleratorservices.util.collections.ParameterizedHashMap;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.exceptions.RendererException;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;


/**
 *
 */
public class DefaultEmailTemplateTranslationStrategy implements EmailTemplateTranslationStrategy
{
	private CommonI18NService commonI18NService;
	private MediaService mediaService;
	private String defaultLanguageIso;

	@Override
	public Map<String, Object> translateMessagesForTemplate(final RendererTemplateModel renderTemplate, String languageIso)
	{
		final String properLanguageIso = languageIso == null ? defaultLanguageIso : languageIso;

		//Get the location of the properties file
		final List<String> propertiesRootPaths = getPropertiesRootPath(renderTemplate, properLanguageIso);

		//Load property file into context
		final Map<String, Object> messages = new ParameterizedHashMap<>();
		for (final String path : propertiesRootPaths)
		{
			//Load property file
			final Map<?, ?> properties = loadPropertyfile(path);

			//Add contents to message map in the context
			for (final Entry entry : properties.entrySet())
			{
				messages.put(String.valueOf(entry.getKey()), entry.getValue());
			}
			//Done
		}
		//Done
		return messages;
	}

	protected List<String> getPropertiesRootPath(final RendererTemplateModel renderTemplate, final String languageIso)
	{
		final MediaModel content = renderTemplate.getContent();
		final List<String> messageSources = new ArrayList<>();
		if (content != null)
		{
			BufferedReader reader = null;
			try
			{
				reader = new BufferedReader(new InputStreamReader(mediaService.getStreamFromMedia(content), "UTF-8"));
				final String line = reader.readLine();
				return buildMessageSources(languageIso, reader, line);
			}
			catch (final IOException e)
			{
				throw new RendererException("Problem during rendering", e);
			}
			finally
			{
				IOUtils.closeQuietly(reader);
			}
		}
		return messageSources;
	}

	protected List<String> buildMessageSources(final String languageIso, final BufferedReader reader, final String line)
			throws IOException
	{
		final List<String> messageSources = new ArrayList<>();
		String lineToProcess = line;

		while (StringUtils.isNotEmpty(lineToProcess))
		{
			String messageSource = null;

			if (lineToProcess.trim().startsWith("<"))
			{
				break;
			}
			else if (lineToProcess.contains("## messageSource="))
			{
				messageSource = StringUtils.substring(lineToProcess, lineToProcess.indexOf("## messageSource=") + 17);
			}
			else if (lineToProcess.contains("##messageSource="))
			{
				messageSource = StringUtils.substring(lineToProcess, lineToProcess.indexOf("##messageSource=") + 16);
			}

			if (StringUtils.isNotEmpty(messageSource))
			{
				if (messageSource.contains("$lang")) //NOSONAR
				{
					messageSource = messageSource.replace("$lang", languageIso);
				}
				messageSources.add(messageSource);
			}
			lineToProcess = reader.readLine();
		}
		return messageSources;
	}

	protected Map loadPropertyfile(final String path)
	{
		final Properties properties = new Properties();
		Reader reader = null;
		try
		{
			final Resource propertyResource = getApplicationContext().getResource(path);
			if (propertyResource != null && propertyResource.exists() && propertyResource.isReadable())
			{
				reader = new InputStreamReader(new BOMInputStream(propertyResource.getInputStream()), "UTF-8");
				properties.load(reader);
			}
		}
		catch (final IOException e)
		{
			throw new RendererException("Problem during rendering", e);
		}
		finally
		{
			IOUtils.closeQuietly(reader);
		}

		return properties;
	}

	protected ApplicationContext getApplicationContext()
	{
		return Registry.getApplicationContext();
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

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	/**
	 * Default language which is used if languageIso parameter is null
	 *
	 * @return default language
	 */
	public String getDefaultLanguageIso()
	{
		return defaultLanguageIso;
	}

	/**
	 * Set default language which is used if languageIso parameter is null
	 *
	 * @param defaultLanguageIso
	 */
	public void setDefaultLanguageIso(final String defaultLanguageIso)
	{
		this.defaultLanguageIso = defaultLanguageIso;
	}

}

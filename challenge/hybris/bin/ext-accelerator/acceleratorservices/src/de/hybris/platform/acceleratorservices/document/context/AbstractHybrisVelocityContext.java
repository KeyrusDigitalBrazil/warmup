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
package de.hybris.platform.acceleratorservices.document.context;

import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;


/**
 * The abstract hybris velocity context.
 */
public abstract class AbstractHybrisVelocityContext<T extends BusinessProcessModel> extends VelocityContext
{
	public static final String BASE_SITE = "baseSite";
	public static final String BASE_URL = "baseUrl";
	public static final String BASE_THEME_URL = "baseThemeUrl";
	public static final String SECURE_BASE_URL = "secureBaseUrl";
	public static final String MEDIA_BASE_URL = "mediaBaseUrl";
	public static final String MEDIA_SECURE_BASE_URL = "mediaSecureBaseUrl";
	public static final String THEME = "theme";

	private Map<String, String> cmsSlotContents;
	private Map<String, Object> messages;
	private String urlEncodingAttributes;
	private BaseSiteModel baseSite;

	private UrlEncoderService urlEncoderService;
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	private static final Logger LOG = LoggerFactory.getLogger(AbstractHybrisVelocityContext.class);

	public void init(final T businessProcessModel, final AbstractPageModel abstractPageModel)
	{
		if (baseSite == null)
		{
			LOG.error("Failed to lookup Site for BusinessProcess [" + businessProcessModel + "]");
		}
		else
		{
			put(BASE_SITE, baseSite);
			setUrlEncodingAttributes(getUrlEncoderService().getUrlEncodingPatternForEmail(businessProcessModel));
			// Lookup the site specific URLs
			put(BASE_URL, getSiteBaseUrlResolutionService().getWebsiteUrlForSite(baseSite, getUrlEncodingAttributes(), false, ""));
			put(BASE_THEME_URL, getSiteBaseUrlResolutionService().getWebsiteUrlForSite(baseSite, false, ""));
			put(SECURE_BASE_URL, getSiteBaseUrlResolutionService().getWebsiteUrlForSite(baseSite, getUrlEncodingAttributes(), true, ""));
			put(MEDIA_BASE_URL, getSiteBaseUrlResolutionService().getMediaUrlForSite(baseSite, false));
			put(MEDIA_SECURE_BASE_URL, getSiteBaseUrlResolutionService().getMediaUrlForSite(baseSite, true));

			put(THEME, baseSite.getTheme() != null ? baseSite.getTheme().getCode() : null);
		}
	}

	/**
	 * Retrieves a specific localized messageId from the template
	 *
	 * @param messageId
	 * @return the localized messageId
	 */
	public String getMessage(final String messageId)
	{
		return messages.get(messageId).toString();
	}

	public Map<String, Object> getMessages()
	{
		return messages;
	}

	public void setMessages(final Map<String, Object> messages)
	{
		this.messages = messages;
	}

	public void setBaseSite(final BaseSiteModel baseSite)
	{
		this.baseSite = baseSite;
	}

	public BaseSiteModel getBaseSite()
	{
		return (BaseSiteModel) get(BASE_SITE);
	}

	public String getBaseUrl()
	{
		return (String) get(BASE_URL);
	}

	public String getBaseThemeUrl()
	{
		return (String) get(BASE_THEME_URL);
	}

	public String getSecureBaseUrl()
	{
		return (String) get(SECURE_BASE_URL);
	}

	public String getMediaBaseUrl()
	{
		return (String) get(MEDIA_BASE_URL);
	}

	public String getMediaSecureBaseUrl()
	{
		return (String) get(MEDIA_SECURE_BASE_URL);
	}

	public String getTheme()
	{
		return (String) get(THEME);
	}

	public Map<String, String> getCmsSlotContents()
	{
		return cmsSlotContents;
	}

	public void setCmsSlotContents(final Map<String, String> cmsSlotContents)
	{
		this.cmsSlotContents = cmsSlotContents;
	}

	protected String getUrlEncodingAttributes()
	{
		return urlEncodingAttributes;
	}

	public void setUrlEncodingAttributes(final String urlEncodingAttributes)
	{
		this.urlEncodingAttributes = urlEncodingAttributes;
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

	protected UrlEncoderService getUrlEncoderService()
	{
		return urlEncoderService;
	}

	@Required
	public void setUrlEncoderService(final UrlEncoderService urlEncoderService)
	{
		this.urlEncoderService = urlEncoderService;
	}

}


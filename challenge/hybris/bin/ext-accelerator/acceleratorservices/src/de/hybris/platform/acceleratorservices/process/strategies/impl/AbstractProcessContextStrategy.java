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

import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.c2l.LocalizableItem;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.site.BaseSiteService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default strategy to impersonate site and initialize session context from the process model.
 */
public abstract class AbstractProcessContextStrategy implements ProcessContextResolutionStrategy
{
	protected static final String BUSINESS_PROCESS_MUST_NOT_BE_NULL_MSG = "businessProcess must not be null";
	private static final Logger LOG = LoggerFactory.getLogger(AbstractProcessContextStrategy.class);
	private CatalogVersionService catalogVersionService;

	private CMSSiteService cmsSiteService;
	private CommonI18NService commonI18NService;
	private CommerceCommonI18NService commerceCommonI18NService;
	private SessionService sessionService;
	private boolean enableLanguageFallback;
	private BaseSiteService baseSiteService;

	@Override
	public void initializeContext(final BusinessProcessModel businessProcess)
	{
		ServicesUtil.validateParameterNotNull(businessProcess, BUSINESS_PROCESS_MUST_NOT_BE_NULL_MSG);

		LOG.debug("Initializing context for business process [{}]", businessProcess.getCode());

		final BaseSiteModel baseSite = getCmsSite(businessProcess);

		if (baseSite == null)
		{
			LOG.warn("Failed to lookup BaseSite for BusinessProcess [{}]. Unable to setup site in session.",
					businessProcess.getCode());
		}
		else
		{
			LOG.debug("Initializing context with site [{}]", baseSite.getName());

			setSite(baseSite);

			setCurrency(businessProcess);

			setLanguage(businessProcess);

			setFallbacks();
		}

	}

	@Override
	public CatalogVersionModel getContentCatalogVersion(final BusinessProcessModel businessProcess)
	{
		ServicesUtil.validateParameterNotNull(businessProcess, BUSINESS_PROCESS_MUST_NOT_BE_NULL_MSG);

		final BaseSiteModel baseSite = getCmsSite(businessProcess);

		if (baseSite instanceof CMSSiteModel)
		{
			final List<ContentCatalogModel> contentCatalogs = ((CMSSiteModel) baseSite).getContentCatalogs();
			if (!contentCatalogs.isEmpty())
			{
				return getCatalogVersionService().getSessionCatalogVersionForCatalog(contentCatalogs.get(0).getId()); // Shouldn't be more than one
			}
		}

		return null;
	}

	/**
	 * Setup the site in the current session, either a CMS Site or a Base Site
	 *
	 * @param baseSite
	 *           the BaseSiteModel to set in session
	 */
	protected void setSite(final BaseSiteModel baseSite)
	{
		if (baseSite instanceof CMSSiteModel)
		{
			try
			{
				getCmsSiteService().setCurrentSiteAndCatalogVersions((CMSSiteModel) baseSite, true);
			}
			catch (final CMSItemNotFoundException e)
			{
				LOG.warn("Failed to set current site and catalog version in session from baseSite", e);
			}
		}
		else
		{
			getBaseSiteService().setCurrentBaseSite(baseSite, true);
		}
	}

	protected void setCurrency(final BusinessProcessModel businessProcess)
	{
		LOG.debug("Setting context currency for businessProcess [{}] ", businessProcess.getCode());

		CurrencyModel contextCurrency = computeCurrency(businessProcess);

		if (!isValidCurrency(contextCurrency))
		{
			contextCurrency = getCommerceCommonI18NService().getDefaultCurrency();
		}

		getCommonI18NService().setCurrentCurrency(contextCurrency);
	}

	protected void setLanguage(final BusinessProcessModel businessProcess)
	{
		LOG.debug("Setting context language for businessProcess [{}]", businessProcess.getCode());

		LanguageModel contextLanguage = computeLanguage(businessProcess);

		if (!isValidLanguage(contextLanguage))
		{
			contextLanguage = getCommerceCommonI18NService().getDefaultLanguage();
		}

		getCommonI18NService().setCurrentLanguage(contextLanguage);
	}

	protected void setFallbacks()
	{
		getSessionService().setAttribute(LocalizableItem.LANGUAGE_FALLBACK_ENABLED, Boolean.valueOf(isEnableLanguageFallback()));
		getSessionService().setAttribute(AbstractItemModel.LANGUAGE_FALLBACK_ENABLED_SERVICE_LAYER,
				Boolean.valueOf(isEnableLanguageFallback()));
	}

	protected CurrencyModel computeCurrency(final BusinessProcessModel businessProcess)
	{
		final CustomerModel customer = getCustomer(businessProcess);

		final CurrencyModel currency = getCurrency(customer);

		LOG.debug("Context Currency for business process [{}] is [{}]", businessProcess, currency);

		return currency;
	}

	protected LanguageModel computeLanguage(final BusinessProcessModel businessProcess)
	{
		final CustomerModel customer = getCustomer(businessProcess);

		final LanguageModel language = getLanguage(customer);

		LOG.debug("Context Language for business process [{}] is [{}]", businessProcess, language);

		return language;
	}

	protected CurrencyModel getCurrency(final CustomerModel customer)
	{
		return customer == null ? null : customer.getSessionCurrency();
	}

	protected boolean isValidCurrency(final CurrencyModel currency)
	{
		return currency != null && getCommerceCommonI18NService().getAllCurrencies().contains(currency);
	}

	protected LanguageModel getLanguage(final CustomerModel customer)
	{
		return customer == null ? null : customer.getSessionLanguage();
	}

	protected boolean isValidLanguage(final LanguageModel language)
	{
		return language != null && getCommerceCommonI18NService().getAllLanguages().contains(language);
	}

	protected abstract CustomerModel getCustomer(final BusinessProcessModel businessProcess);

	protected CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
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

	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected boolean isEnableLanguageFallback()
	{
		return enableLanguageFallback;
	}

	// Optional
	public void setEnableLanguageFallback(final boolean enableLanguageFallback)
	{
		this.enableLanguageFallback = enableLanguageFallback;
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

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

}

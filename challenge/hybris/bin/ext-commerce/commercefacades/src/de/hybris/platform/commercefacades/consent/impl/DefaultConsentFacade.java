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
package de.hybris.platform.commercefacades.consent.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.consent.ConsentFacade;
import de.hybris.platform.commercefacades.consent.data.ConsentTemplateData;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ConsentFacade}.
 */
public class DefaultConsentFacade implements ConsentFacade
{
	private UserService userService;
	private CommerceConsentService commerceConsentService;
	private BaseSiteService baseSiteService;

	private Converter<ConsentTemplateModel, ConsentTemplateData> consentTemplateConverter;

	@Override
	public ConsentTemplateData getLatestConsentTemplate(final String consentTemplateId)
	{
		validateParameterNotNull(consentTemplateId, "Parameter consentTemplateId must not be null");

		final BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();
		return getConsentTemplateConverter()
				.convert(getCommerceConsentService().getLatestConsentTemplate(consentTemplateId, baseSite));
	}

	@Override
	public List<ConsentTemplateData> getConsentTemplatesWithConsents()
	{
		final BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();
		final List<ConsentTemplateModel> consentTemplates = getCommerceConsentService().getConsentTemplates(baseSite);
		return consentTemplates.stream().map(consent -> getConsentTemplateConverter().convert(consent))
				.collect(Collectors.toList());
	}

	@Override
	public void giveConsent(final String consentTemplateId, final Integer consentTemplateVersion)
	{
		validateParameterNotNull(consentTemplateId, "Parameter consentTemplateId must not be null");
		validateParameterNotNull(consentTemplateVersion, "Parameter consentTemplateVersion must not be null");

		final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
		final BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();

		final ConsentTemplateModel consentTemplate = getCommerceConsentService().getConsentTemplate(consentTemplateId,
				consentTemplateVersion, baseSite);
		getCommerceConsentService().giveConsent(customer, consentTemplate);
	}

	@Override
	public void withdrawConsent(final String consentCode)
	{
		validateParameterNotNull(consentCode, "Parameter consentCode must not be null");

		final ConsentModel consent = getCommerceConsentService().getConsent(consentCode);

		getCommerceConsentService().withdrawConsent(consent);
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CommerceConsentService getCommerceConsentService()
	{
		return commerceConsentService;
	}

	@Required
	public void setCommerceConsentService(final CommerceConsentService commerceConsentService)
	{
		this.commerceConsentService = commerceConsentService;
	}

	protected Converter<ConsentTemplateModel, ConsentTemplateData> getConsentTemplateConverter()
	{
		return consentTemplateConverter;
	}

	@Required
	public void setConsentTemplateConverter(final Converter<ConsentTemplateModel, ConsentTemplateData> consentTemplateConverter)
	{
		this.consentTemplateConverter = consentTemplateConverter;
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

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.consent.service.impl;

import de.hybris.platform.commercefacades.consent.ConsentFacade;
import de.hybris.platform.commercefacades.consent.data.ConsentTemplateData;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.consent.YmktConsentService;


/**
 * User consent management integration with {@link ConsentFacade} and {@link ConsentModel} of
 * {@link YmktConsentService}.
 */
public class DefaultYmktConsentService implements YmktConsentService
{
	protected static final String CONSENT_GIVEN = "GIVEN";

	private static final Logger LOG = LoggerFactory.getLogger(DefaultYmktConsentService.class);

	protected static final String USER_CONSENTS = "user-consents";

	protected ConfigurationService configurationService;
	protected ConsentFacade consentFacade;
	protected FlexibleSearchService flexibleSearchService;
	protected SessionService sessionService;
	protected UserService userService;

	/**
	 * Read and return consent value from cookie for the current anonymous user's session given a consent template ID.
	 * 
	 * @param consentID
	 *           String configuration property containing consent template ID
	 * @return true if current anonymous user has given consent, false otherwise
	 */
	protected boolean getAnonymousUserConsent(final String consentID)
	{
		final Optional<String> consentTemplateId = this.getConsentTemplateID(consentID);
		final Map<String, String> userConsents = this.sessionService.getAttribute(USER_CONSENTS);
		final Map<String, String> consentsMap = Optional.ofNullable(userConsents).orElse(Collections.emptyMap());
		final String consentState = consentsMap.get(consentTemplateId.orElse(""));
		final boolean hasConsent = CONSENT_GIVEN.equalsIgnoreCase(consentState);

		LOG.debug("consentID={}, consentTemplateId={}, userConsents={}, consentState={}, hasConsent={}", //
				consentID, consentTemplateId, userConsents, consentState, Boolean.valueOf(hasConsent));

		return hasConsent;
	}

	/**
	 * Look-up consent template ID from provided configuration property
	 * 
	 * @param consentID
	 *           String configuration property containing consent template ID
	 * @return String consent template ID
	 */
	protected Optional<String> getConsentTemplateID(final String consentID)
	{
		return Optional.ofNullable(this.configurationService.getConfiguration().getString(consentID, null));
	}

	/**
	 * Read and return consent value for the current logged-in user given a consent template ID.
	 * 
	 * @param consentID
	 *           String configuration property containing consent template ID
	 * @return true if current logged in user has given consent, false otherwise
	 */
	protected boolean getRegisteredUserConsent(final String consentID)
	{
		return this.getConsentTemplateID(consentID) //
				.map(this.consentFacade::getLatestConsentTemplate) //
				.map(ConsentTemplateData::getConsentData) //
				.map(c -> Boolean.valueOf(Objects.isNull(c.getConsentWithdrawnDate()))) //
				.orElse(Boolean.FALSE).booleanValue();
	}

	@Override
	public boolean getUserConsent(final String consentID)
	{
		return isAnonymousUser() ? getAnonymousUserConsent(consentID) : getRegisteredUserConsent(consentID);
	}

	@Override
	public boolean getUserConsent(String customerId, String consentId)
	{
		final String consentTemplateId = this.getConsentTemplateID(consentId).orElse("");

		final Map<String, String> queryParams = new HashMap<>();
		queryParams.put("consentId", consentTemplateId);
		queryParams.put("customerID", customerId);

		final SearchResult<ConsentModel> search = this.flexibleSearchService.search(
				"SELECT {co.pk} FROM {Consent AS co} " + //
						"WHERE {co.consentWithdrawnDate} IS NULL " + //
						"AND {co.consentTemplate} IN ({{SELECT {ct.pk} FROM {ConsentTemplate AS ct} WHERE {ct.id} = ?consentId}}) " + //
						"AND {co.customer} IN ({{SELECT {cu.pk} FROM {Customer AS cu} WHERE {cu.customerID} = ?customerID}})",
				queryParams);
		return search.getCount() > 0;
	}

	/**
	 * Checks if current user is logged in or browsing anonymously
	 * 
	 * @return boolean is the user anonymous
	 */
	private boolean isAnonymousUser()
	{
		return Optional.ofNullable(this.userService.getCurrentUser()) //
				.map(this.userService::isAnonymousUser) //
				.orElse(Boolean.TRUE).booleanValue();
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	@Required
	public void setConsentFacade(final ConsentFacade consentFacade)
	{
		this.consentFacade = consentFacade;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}

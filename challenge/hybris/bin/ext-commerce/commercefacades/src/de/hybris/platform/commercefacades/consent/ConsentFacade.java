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
package de.hybris.platform.commercefacades.consent;

import java.util.List;

import de.hybris.platform.commercefacades.consent.data.ConsentTemplateData;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;


/**
 * Facade interface providing an API for performing various consent operations.
 */
public interface ConsentFacade
{
	/**
	 * Gets the latest consent template for the specified <code>consentTemplateId</code> and the current base site.
	 *
	 * @param consentTemplateId
	 *           the id of the consent template to retrieve the consent for. Should be a valid id for a
	 *           {@link de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel}.
	 * @return the active consent
	 */
	ConsentTemplateData getLatestConsentTemplate(final String consentTemplateId);

	/**
	 * Gets the list of consent templates available for the current base site (in their latest version) along with
	 * current user consents embedded in <code>ConsentTemplateData.consentData</code>.
	 *
	 * @return a list of active consent templates
	 */
	List<ConsentTemplateData> getConsentTemplatesWithConsents();

	/**
	 * Gives consent for the specified <code>consentTemplateId</code> and <code>consentTemplateVersion</code>, the
	 * current base site and current user. If no Consent is found for the specified Consent Template, or if a Consent
	 * exists but is withdrawn (i.e. the withdrawal date is not null), a new Consent is created with the current date set
	 * to <code>consentGivenDate</code> attribute.
	 *
	 * @see de.hybris.platform.commerceservices.consent.CommerceConsentService#giveConsent(CustomerModel,
	 *      ConsentTemplateModel)
	 * @param consentTemplateId
	 *           the id of the consent template to give the consent for. Should be a valid id for a
	 *           {@link ConsentTemplateModel}.
	 * @param consentTemplateVersion
	 *           the version of the consent template to give the consent for
	 * @throws de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
	 *            if no ConsentTemplate with the specified <code>consentTemplateId</code> and
	 *            <code>consentTemplateVersion</code> was found
	 * @throws de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException
	 *            if multiple ConsentTemplate entities were found for the the specified <code>consentTemplateId</code>
	 *            and <code>consentTemplateVersion</code>
	 * @throws de.hybris.platform.commerceservices.consent.exceptions.CommerceConsentGivenException
	 * 			  if a consent was already given
	 */
	void giveConsent(final String consentTemplateId, Integer consentTemplateVersion);

	/**
	 * Withdraws consent for the specified <code>consentCode</code>. If the given Consent has been withdrawn already,
	 * nothing happens
	 *
	 * @see de.hybris.platform.commerceservices.consent.CommerceConsentService#withdrawConsent(de.hybris.platform.commerceservices.model.consent.ConsentModel)
	 * @param consentCode
	 *           the code of the user consent to withdraw
	 * @throws de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
	 *            if no Consent with the specified <code>consentCode</code> was found
	 */
	void withdrawConsent(final String consentCode);
}




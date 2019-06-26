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
package de.hybris.platform.commerceservices.consent;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.exceptions.CommerceConsentGivenException;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.List;


/**
 * Handles activities relating to {@link ConsentModel} and {@link ConsentTemplateModel}.
 */
public interface CommerceConsentService
{
	/**
	 * Finds all the latest versions of consent templates for the given <code>baseSite</code>.
	 *
	 * @param baseSite
	 *           the baseSite to retrieve the consent template for
	 * @return available consent templates
	 */
	List<ConsentTemplateModel> getConsentTemplates(final BaseSiteModel baseSite);

	/**
	 * Finds the consent template for the supplied <code>consentTemplateId</code>, <code>consentTemplateVersion</code> and
	 * <code>baseSite</code>.
	 *
	 * @param consentTemplateId
	 *           the id of the consent template
	 * @param consentTemplateVersion
	 *           the version of the consent template
	 * @param baseSite
	 *           the baseSite to retrieve the consent template for
	 * @return the consent template
	 */
	ConsentTemplateModel getConsentTemplate(final String consentTemplateId, final Integer consentTemplateVersion,
			final BaseSiteModel baseSite);

	/**
	 * Finds the latest version of a <code>ConsentTemplate</code> for the given <code>consentTemplateId</code> and
	 * <code>baseSite</code>.
	 *
	 * @param consentTemplateId
	 *           the id of the consent template
	 * @param baseSite
	 *           the baseSite to retrieve the consent template for.
	 * @return the consent template
	 */
	ConsentTemplateModel getLatestConsentTemplate(final String consentTemplateId, final BaseSiteModel baseSite);

	/**
	 * Finds the consent for the specified <code>consentCode</code>
	 *
	 * @param consentCode
	 *           the consent code to get the consent for
	 * @return the consent
	 */
	ConsentModel getConsent(final String consentCode);

	/**
	 * Finds the latest consent for the specified customer and consent template.
	 *
	 * @param customer
	 *           the customer to get the consent for
	 * @param consentTemplate
	 *           the consent template to get the consent for
	 * @return the consent if found, otherwise null
	 */
	ConsentModel getActiveConsent(final CustomerModel customer, final ConsentTemplateModel consentTemplate);

	/**
	 * Checks if a customer has a consent that has not been withdrawn for the specified consent template.
	 *
	 * @param customer
	 *           the customer to check the consent for
	 * @param consentTemplate
	 *           the consent template to check the consent for
	 * @return true, if the customer has a consent that has not been withdrawn
	 */
	boolean hasEffectivelyActiveConsent(CustomerModel customer, ConsentTemplateModel consentTemplate);

	/**
	 * Gives consent for the specified customer and consent template. Creates a new <code>Consent</code> and sets the
	 * consentGivenDate. <br/>
	 * <br/>
	 *
	 * 1. Doesn't create a new consent if the consent is already given and active and throws {@link CommerceConsentGivenException}
	 *
	 * @param customer
	 *           the customer to give the consent for
	 * @param consentTemplate
	 *           the consent template to give the consent for
	 */
	void giveConsent(final CustomerModel customer, final ConsentTemplateModel consentTemplate);

	/**
	 * Withdraws consent for the specified ConsentModel by updating the consentWithdrawnDate.<br/>
	 * <br/>
	 * 1. Doesn't withdraw the consent if its already withdrawn, i.e, If <code>Consent.consentWithdrawnDate</code> is
	 * already set calling this method will not overwrite the existing value.
	 *
	 * @param consent
	 *           the consent to withdraw the consent for
	 */
	void withdrawConsent(final ConsentModel consent);
}

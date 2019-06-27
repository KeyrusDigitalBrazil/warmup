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
package de.hybris.platform.commerceservices.consent.dao;

import java.util.List;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;


/**
 * Data Access Object for looking up items related to the consent templates.
 */
public interface ConsentTemplateDao extends GenericDao<ConsentTemplateModel>
{
	/**
	 * Finds the latest version of a consent template for a specified id and base site.
	 *
	 * @param consentTemplateId
	 *           the id of the consent template
	 * @param baseSite
	 *           the base site to get the consent template for
	 * @return the consent template
	 */
	ConsentTemplateModel findLatestConsentTemplateByIdAndSite(String consentTemplateId, BaseSiteModel baseSite);

	/**
	 * Finds consent template for specified id, version and base site.
	 *
	 * @param consentTemplateId
	 *           the id of the consent template
	 * @param consentTemplateVersion
	 *           the version of the consent template
	 * @param baseSite
	 *           the base site to get the consent template for
	 * @return the consent template
	 */
	ConsentTemplateModel findConsentTemplateByIdAndVersionAndSite(String consentTemplateId, Integer consentTemplateVersion,
			BaseSiteModel baseSite);

	/**
	 * Finds available consent templates (in the latest version) for a specified base site.
	 *
	 * @param baseSite
	 *           the base site to get the consent templates for
	 * @return available consent templates
	 */
	List<ConsentTemplateModel> findConsentTemplatesBySite(final BaseSiteModel baseSite);
}

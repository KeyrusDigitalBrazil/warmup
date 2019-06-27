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
package de.hybris.platform.acceleratorservices.email.dao;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;


/**
 * Data Access for looking up the CMS email page for a template name.
 */
public interface EmailPageDao extends Dao
{
	/**
	 * Retrieves EmailPage given its frontend template name.
	 * 
	 * @param frontendTemplateName
	 *           the frontend template name
	 * @param catalogVersion
	 *           the catalog version
	 * @return EmailPage object if found, null otherwise
	 */
	EmailPageModel findEmailPageByFrontendTemplateName(String frontendTemplateName, CatalogVersionModel catalogVersion);
}

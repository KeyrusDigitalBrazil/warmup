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
package de.hybris.platform.acceleratorservices.document.dao;

import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.Collection;


/**
 * Data Access for looking up the Document page for a template name.
 */
public interface DocumentPageDao extends Dao
{
	/**
	 * Retrieves {@link DocumentPageModel} given its frontend template name.
	 *
	 * @param frontendTemplateName
	 * 		the frontend template name
	 * @return {@link DocumentPageModel} object if found, null otherwise
	 */
	DocumentPageModel findDocumentPageByTemplateName(final String frontendTemplateName, final Collection<CatalogVersionModel> catalogVersions);
}

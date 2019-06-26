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
package de.hybris.platform.acceleratorservices.document.service;

import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;

import java.util.Collection;


/**
 * Service for looking up the CMS Document Page for a template name.
 */
public interface DocumentPageService
{
	/**
	 * Retrieves {@link DocumentPageModel} given its template name.
	 *
	 * @param templateName
	 * 		template name to retrieve the {@link DocumentPageModel}
	 * @param catalogVersions
	 * 		collection of {@link CatalogVersionModel}
	 * @return DocumentPage object if found, null otherwise
	 */
	DocumentPageModel findDocumentPageByTemplateName(final String templateName, final Collection<CatalogVersionModel> catalogVersions);
}

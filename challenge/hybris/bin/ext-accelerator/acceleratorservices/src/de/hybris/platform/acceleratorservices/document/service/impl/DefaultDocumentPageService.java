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
package de.hybris.platform.acceleratorservices.document.service.impl;

import de.hybris.platform.acceleratorservices.document.dao.DocumentPageDao;
import de.hybris.platform.acceleratorservices.document.service.DocumentPageService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;


/**
 * Default implementation for {@link DocumentPageService}
 */
public class DefaultDocumentPageService implements DocumentPageService
{
	private DocumentPageDao documentPageDao;

	@Override
	public DocumentPageModel findDocumentPageByTemplateName(final String templateName, final Collection<CatalogVersionModel> catalogVersions)
	{

		return getDocumentPageDao().findDocumentPageByTemplateName(templateName, catalogVersions);
	}

	protected DocumentPageDao getDocumentPageDao()
	{
		return documentPageDao;
	}

	@Required
	public void setDocumentPageDao(final DocumentPageDao documentPageDao)
	{
		this.documentPageDao = documentPageDao;
	}
}

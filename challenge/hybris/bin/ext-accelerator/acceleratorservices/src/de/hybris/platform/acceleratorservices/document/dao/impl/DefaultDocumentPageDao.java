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
package de.hybris.platform.acceleratorservices.document.dao.impl;

import de.hybris.platform.acceleratorservices.document.dao.DocumentPageDao;
import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageTemplateModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.FlexibleSearchUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Default Data Access for looking up the Document page for a template name.
 */
public class DefaultDocumentPageDao extends AbstractItemDao implements DocumentPageDao
{
	protected static final String CATALOG_VERSIONS_QUERY_PARAM = "catalogVersions";

	protected static final String QUERY =
			"SELECT {" + DocumentPageModel.PK + "} " + "FROM {" + DocumentPageModel._TYPECODE + " AS p " + "JOIN "
					+ DocumentPageTemplateModel._TYPECODE + " AS t ON {p:" + DocumentPageModel.MASTERTEMPLATE + "}={t:"
					+ DocumentPageTemplateModel.PK
					+ "}} " + "WHERE {t:" + DocumentPageTemplateModel.UID + "}=?templateUid AND ";

	@Override
	public DocumentPageModel findDocumentPageByTemplateName(final String frontendTemplateName,
			final Collection<CatalogVersionModel> catalogVersions)
	{
		validateParameterNotNull(frontendTemplateName, "Template name code cannot be null");
		Assert.isTrue(CollectionUtils.isNotEmpty(catalogVersions), "No Catalog versions found to find the document page");

		final StringBuilder queryBuilder = new StringBuilder();
		final Map<String, Object> queryParameters = new HashMap<String, Object>();

		queryBuilder.append(QUERY);
		queryBuilder.append(//
				FlexibleSearchUtils.buildOracleCompatibleCollectionStatement(
						"{" + DocumentPageModel.CATALOGVERSION + "} in (?" + CATALOG_VERSIONS_QUERY_PARAM + ")",
						CATALOG_VERSIONS_QUERY_PARAM, "OR", catalogVersions, queryParameters));

		queryParameters.put("templateUid", frontendTemplateName);

		final SearchResult<DocumentPageModel> result = search(queryBuilder.toString(), queryParameters);
		return result.getCount() > 0 ? result.getResult().iterator().next() : null;
	}
}

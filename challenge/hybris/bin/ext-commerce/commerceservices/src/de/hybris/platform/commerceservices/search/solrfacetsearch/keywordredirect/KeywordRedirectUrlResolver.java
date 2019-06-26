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
package de.hybris.platform.commerceservices.search.solrfacetsearch.keywordredirect;

import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.solrfacetsearch.model.redirect.SolrAbstractKeywordRedirectModel;


/**
 * URL Resolver - constructs an actual URL from a Solr Keyword redirect model referencing a target of a particular type
 * (e.g. Product, Page, ...)
 */
public interface KeywordRedirectUrlResolver<T extends SolrAbstractKeywordRedirectModel> extends UrlResolver<T>
{
	//Empty
}

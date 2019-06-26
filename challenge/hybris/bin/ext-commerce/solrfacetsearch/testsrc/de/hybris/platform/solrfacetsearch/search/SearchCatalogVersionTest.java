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
package de.hybris.platform.solrfacetsearch.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.solrfacetsearch.integration.AbstractSearchQueryTest;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;


public class SearchCatalogVersionTest extends AbstractSearchQueryTest
{
	private static final String LANGUAGE_CODE = "isocode";

	private static final String LANGUAGE_CODE_EN = "en";
	private static final String LANGUAGE_CODE_DE = "de";
	private static final String LANGUAGE_CODE_FR = "fr";

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/SearchCatalogVersionTest.impex");
	}

	@Test
	public void searchNonCatalogVersionAwareItems() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery();

		// then
		assertEquals(3, searchResult.getNumberOfResults());

		final List<String> languageCodes = searchResult.getDocuments().stream().map(document -> (String) document.getFields().get(LANGUAGE_CODE)).collect(Collectors.toList());

		assertTrue(languageCodes.contains(LANGUAGE_CODE_EN));
		assertTrue(languageCodes.contains(LANGUAGE_CODE_DE));
		assertTrue(languageCodes.contains(LANGUAGE_CODE_FR));
	}
}

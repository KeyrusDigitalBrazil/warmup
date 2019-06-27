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

import static org.junit.Assert.assertTrue;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.enums.KeywordRedirectMatchType;
import de.hybris.platform.solrfacetsearch.handler.KeywordRedirectHandler;
import de.hybris.platform.solrfacetsearch.handler.impl.DefaultContainsKeywordRedirectHandler;
import de.hybris.platform.solrfacetsearch.handler.impl.DefaultEndsWithKeywordRedirectHandler;
import de.hybris.platform.solrfacetsearch.handler.impl.DefaultExactKeywordRedirectHandler;
import de.hybris.platform.solrfacetsearch.handler.impl.DefaultRegexKeywordRedirectHandler;
import de.hybris.platform.solrfacetsearch.handler.impl.DefaultStartsWithKeywordRedirectHandler;
import de.hybris.platform.solrfacetsearch.model.redirect.SolrAbstractKeywordRedirectModel;
import de.hybris.platform.solrfacetsearch.model.redirect.SolrFacetSearchKeywordRedirectModel;
import de.hybris.platform.solrfacetsearch.search.impl.DefaultSolrKeywordRedirectService;
import de.hybris.platform.solrfacetsearch.search.impl.SolrSearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@UnitTest
public class SolrKeywordRedirectTest
{
	@Mock
	private SolrSearchResult searchResult;

	@Mock
	private SearchQuery searchQuery;

	@Mock
	private FacetSearchConfig facetSearchConfig;

	@Mock
	private SolrFacetSearchKeywordDao solrFacetSearchKeywordDao;

	@Mock
	private I18NService i18nService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private KeywordRedirectSorter keywordRedirectSorter;

	@InjectMocks
	private final DefaultContainsKeywordRedirectHandler containsKeywordRedirectHandler = new DefaultContainsKeywordRedirectHandler();

	@InjectMocks
	private final DefaultEndsWithKeywordRedirectHandler endsWithKeywordRedirectHandler = new DefaultEndsWithKeywordRedirectHandler();

	@InjectMocks
	private final DefaultExactKeywordRedirectHandler exactKeywordRedirectHandler = new DefaultExactKeywordRedirectHandler();

	@InjectMocks
	private final DefaultRegexKeywordRedirectHandler regexKeywordRedirectHandler = new DefaultRegexKeywordRedirectHandler();

	@InjectMocks
	private final DefaultStartsWithKeywordRedirectHandler startsWithKeywordRedirectHandler = new DefaultStartsWithKeywordRedirectHandler();

	@InjectMocks
	private final DefaultSolrKeywordRedirectService solrKeywordRedirectService = new DefaultSolrKeywordRedirectService();

	private final LanguageModel lang = new LanguageModel();

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		searchResult = new SolrSearchResult();
		searchResult.setSearchQuery(searchQuery);
		lang.setIsocode("de");

		when(i18nService.getCurrentLocale()).thenReturn(Locale.GERMAN);
		when(commonI18NService.getCurrentLanguage()).thenReturn(lang);
		when(searchQuery.getFacetSearchConfig()).thenReturn(facetSearchConfig);

		final HashMap<KeywordRedirectMatchType, KeywordRedirectHandler> map = new HashMap<KeywordRedirectMatchType, KeywordRedirectHandler>();
		map.put(KeywordRedirectMatchType.CONTAINS, containsKeywordRedirectHandler);
		map.put(KeywordRedirectMatchType.ENDS_WITH, endsWithKeywordRedirectHandler);
		map.put(KeywordRedirectMatchType.EXACT, exactKeywordRedirectHandler);
		map.put(KeywordRedirectMatchType.REGEX, regexKeywordRedirectHandler);
		map.put(KeywordRedirectMatchType.STARTS_WITH, startsWithKeywordRedirectHandler);

		solrKeywordRedirectService.setRedirectHandlers(map);
		when(keywordRedirectSorter.sort(Mockito.<List<SolrFacetSearchKeywordRedirectModel>> any())).thenAnswer(new Answer()
		{
			@Override
			public List<KeywordRedirectValue> answer(final InvocationOnMock invocation)
			{
				final Object[] args = invocation.getArguments();
				return (List<KeywordRedirectValue>) args[0];
			}
		});
	}

	@Test
	public void testProcess()
	{
		solrKeywordRedirectService.attachKeywordRedirect(searchResult);
		assertThat(searchResult.getKeywordRedirects()).isEmpty();

		defaultTestSingleMatch(KeywordRedirectMatchType.EXACT, "fullMatch", "fullMatch");
		defaultTestSingleMatch(KeywordRedirectMatchType.STARTS_WITH, "startsWith", "startsWithWithSomethingAfter");
		defaultTestSingleMatch(KeywordRedirectMatchType.ENDS_WITH, "startsWith", "endsWithstartsWith");
		defaultTestSingleMatch(KeywordRedirectMatchType.CONTAINS, "contains", "start contains end");
		defaultTestSingleMatch(KeywordRedirectMatchType.REGEX, "a[bc]{2,3}.+e", "abcc-e");
	}

	private void defaultTestSingleMatch(final KeywordRedirectMatchType matchMode, final String matchWord,
			final String fullQueryText)
	{
		searchResult.setKeywordRedirects(Collections.<KeywordRedirectValue> emptyList());
		final List<SolrFacetSearchKeywordRedirectModel> matches = new ArrayList<SolrFacetSearchKeywordRedirectModel>();
		final SolrFacetSearchKeywordRedirectModel redirect = new SolrFacetSearchKeywordRedirectModel();
		final SolrAbstractKeywordRedirectModel redirectValue = prepareRedirect(redirect, matchWord, matchMode);
		final SolrFacetSearchKeywordRedirectModel redirectNoMatch = new SolrFacetSearchKeywordRedirectModel();
		prepareRedirect(redirectNoMatch, "noMatchMatch", matchMode);
		matches.add(redirect);
		matches.add(redirectNoMatch);
		when(solrFacetSearchKeywordDao.findKeywords(searchQuery.getFacetSearchConfig().getName(), lang.getIsocode()))
				.thenReturn(matches);
		//when(solrKeywordRedirectService.findKeywordRedirects(searchQuery)).thenReturn(matches);
		when(searchQuery.getUserQuery()).thenReturn(fullQueryText);
		solrKeywordRedirectService.attachKeywordRedirect(searchResult);
		assertThat(searchResult.getKeywordRedirects().size()).isEqualTo(1);
		for (final KeywordRedirectValue value : searchResult.getKeywordRedirects())
		{
			assertTrue(value.getRedirect() == redirectValue);
		}
	}

	private SolrAbstractKeywordRedirectModel prepareRedirect(final SolrFacetSearchKeywordRedirectModel redirect,
			final String keyword, final KeywordRedirectMatchType match)
	{
		final SolrAbstractKeywordRedirectModel redirectValue = mock(SolrAbstractKeywordRedirectModel.class);
		redirect.setMatchType(match);
		redirect.setKeyword(keyword.trim());
		redirect.setIgnoreCase(Boolean.TRUE);
		redirect.setRedirect(redirectValue);
		return redirectValue;
	}

}

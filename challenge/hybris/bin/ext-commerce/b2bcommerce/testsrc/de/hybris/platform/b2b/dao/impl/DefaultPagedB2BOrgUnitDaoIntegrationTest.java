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
package de.hybris.platform.b2b.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.daos.impl.DefaultOrgUnitDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.util.CommerceSearchUtils;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.test.context.ContextConfiguration;


/**
 * DefaultB2BOrgUnitDao integration test for overridden method {@link DefaultOrgUnitDao
 * <T>#findQuotesForEmployee(de.hybris.platform.core.model.user.EmployeeModel, java.util.Set,
 * de.hybris.platform.commerceservices.search.pagedata.PageableData)}
 *
 */
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:b2bcommerce/test/b2bcommerce-test-spring.xml" })
public class DefaultPagedB2BOrgUnitDaoIntegrationTest extends BaseCommerceBaseTest
{
	private static final String USA_EMPLOYEE = "usaEmployee";
	private static final String NA_EMPLOYEE = "northAmericaEmployee";
	private static final String CANADA_EMPLOYEE = "canadaEmployee";
	private static final String CA_UNIT_UID = "canada";
	private static final String MT_UNIT_UID = "montreal";
	private static final String CAL_UNIT_UID = "california";
	private static final String PRONTO_SERVICE_UNIT_UID = "Pronto Services";
	private static final String RUSTIC_UNIT_UID = "Rustic";
	private static final String RUSTIC_RETAIL_UNIT_UID = "Rustic Retail";
	private static final String RUSTIC_SERVICE_UNIT_UID = "Rustic Services";
	private static final String[] unitUids =
	{ CA_UNIT_UID, MT_UNIT_UID, CAL_UNIT_UID };

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Resource
	private DefaultB2BOrgUnitDao<OrgUnitModel> defaultB2BOrgUnitDao;

	@Before
	public void setUp() throws Exception
	{
		ServicelayerTest.createCoreData();
		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		importCsv("/b2bcommerce/test/usergroups.impex", "UTF-8");

		i18NService.setCurrentLocale(Locale.ENGLISH);
	}

	@Test
	public void shouldGetQuotesForUSAEmployee()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_EMPLOYEE));
		final EmployeeModel usaEmployee = (EmployeeModel) userService.getUserForUID(USA_EMPLOYEE);

		final SearchPageData<QuoteModel> quoteSearchResult = defaultB2BOrgUnitDao.findQuotesForEmployee(usaEmployee,
				getSellerQuoteStates(), createPageableData(0, 10));

		Assert.assertNotNull("Search page data is null", quoteSearchResult);
		Assert.assertNotNull("Search results are null", quoteSearchResult.getResults());

		// For 'usaEmployee', we're expecting to get 1 quote from the test data:
		// - testQuote4, version 2
		Assert.assertEquals("Unexpected number of results", 1, quoteSearchResult.getResults().size());
		assertResultContains(quoteSearchResult.getResults(), "testQuote4", Integer.valueOf(2), "customer.q@rustic-hw.com");
	}

	@Test
	public void shouldGetQuotesForNAEmployee()
	{
		userService.setCurrentUser(userService.getUserForUID(NA_EMPLOYEE));
		final EmployeeModel NAEmployee = (EmployeeModel) userService.getUserForUID(NA_EMPLOYEE);


		final SearchPageData<QuoteModel> quoteSearchResult = defaultB2BOrgUnitDao.findQuotesForEmployee(NAEmployee,
				getSellerQuoteStates(), createPageableData(0, 10));

		Assert.assertNotNull("Search page data is null", quoteSearchResult);
		Assert.assertNotNull("Search results are null", quoteSearchResult.getResults());

		// For 'northAmericaEmployee', we're expecting to get 3 quotes from the test data:
		// - testQuote1, version 2
		// - testQuote2, version 2
		// - testQuote3, version 2
		Assert.assertEquals("Unexpected number of results", 3, quoteSearchResult.getResults().size());
		assertResultContains(quoteSearchResult.getResults(), "testQuote1", Integer.valueOf(2), "customer.k@rustic-hw.com");
		assertResultContains(quoteSearchResult.getResults(), "testQuote2", Integer.valueOf(2), "customer.k@rustic-hw.com");
		assertResultContains(quoteSearchResult.getResults(), "testQuote3", Integer.valueOf(2), "customer.k@rustic-hw.com");
	}

	@Test
	public void shouldGetQuotesForCanadaEmployee()
	{
		userService.setCurrentUser(userService.getUserForUID(CANADA_EMPLOYEE));
		final EmployeeModel canadaEmployee = (EmployeeModel) userService.getUserForUID(CANADA_EMPLOYEE);

		final SearchPageData<QuoteModel> quoteSearchResult = defaultB2BOrgUnitDao.findQuotesForEmployee(canadaEmployee,
				getSellerQuoteStates(), createPageableData(0, 10));

		Assert.assertNotNull("Search page data is null", quoteSearchResult);
		Assert.assertNotNull("Search results are null", quoteSearchResult.getResults());

		// For 'canadaEmployee', we're expecting to get 0 quotes
		Assert.assertEquals("Unexpected number of results", 0, quoteSearchResult.getResults().size());
	}

	@Test
	public void shouldFindMembersOfType()
	{
		final Set<String> b2bUnitUids = Sets.newSet(PRONTO_SERVICE_UNIT_UID, RUSTIC_UNIT_UID, RUSTIC_RETAIL_UNIT_UID,
				RUSTIC_SERVICE_UNIT_UID);
		final SearchPageData<B2BUnitModel> unitsPageData = defaultB2BOrgUnitDao.findMembersOfType(B2BUnitModel.class,
				CommerceSearchUtils.getAllOnOnePagePageableData(), unitUids);
		Assert.assertNotNull("unitsPageData", unitsPageData);
		Assert.assertNotNull("unitsPageData", unitsPageData.getResults());
		Assert.assertEquals("unitsPageData size", b2bUnitUids.size(), unitsPageData.getResults().size());
		final Set<String> uidSet = unitsPageData.getResults().stream().map(OrgUnitModel::getUid).collect(Collectors.toSet());
		for (final String uid : b2bUnitUids)
		{
			Assert.assertTrue("uid is wrong:" + uid, uidSet.contains(uid));
		}
	}

	protected void assertResultContains(final List<QuoteModel> result, final String quoteCode, final Integer quoteVersion,
			final String userUid)
	{
		for (final QuoteModel quote : result)
		{
			if (quoteCode.equals(quote.getCode()) && quoteVersion.equals(quote.getVersion())
					&& userUid.equals(quote.getUser().getUid()))
			{
				return;
			}
		}
		Assert.fail(String.format("Result does not contain expected quote: [code: %s, version: %s, user: %s]", quoteCode,
				quoteVersion, userUid));
	}

	protected PageableData createPageableData(final int currentPage, final int pageSize)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(currentPage);
		pageableData.setPageSize(pageSize);
		return pageableData;
	}

	protected Set<QuoteState> getSellerQuoteStates()
	{
		final Set<QuoteState> sellerStates = new HashSet<>();
		sellerStates.add(QuoteState.SELLER_DRAFT);
		sellerStates.add(QuoteState.SELLER_REQUEST);
		sellerStates.add(QuoteState.SELLER_SUBMITTED);
		return sellerStates;
	}
}

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
package de.hybris.platform.commerceservices.organization.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:commerceservices/quote-spring-test.xml" })
public class DefaultOrgUnitQuoteServiceIntegrationTest extends BaseCommerceBaseTest
{

	private static final String UNASSIGNED_EMPLOYEE = "unassignedEmployee";
	private static final String CANADA_EMPLOYEE = "canadaEmployee";
	private static final String USA_EMPLOYEE = "usaEmployee";
	private static final String NA_EMPLOYEE = "northAmericaEmployee";

	private static final String CREATE_GROUPS_KEY = "commerceservices.organization.rights.create.groups";
	private static final String EDIT_GROUPS_KEY = "commerceservices.organization.rights.edit.groups";
	private static final String EDIT_PARENT_GROUPS_KEY = "commerceservices.organization.rights.edit.parent.groups";
	private static final String VIEW_GROUPS_KEY = "commerceservices.organization.rights.view.groups";

	@Resource(name = "testOrgUnitQuoteService")
	private DefaultOrgUnitQuoteService testOrgUnitQuoteService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private String createGroupsBackup;
	private String editGroupsBackup;
	private String editParentGroupsBackup;
	private String viewGroupsBackup;

	@Before
	public void setUp() throws Exception
	{
		ServicelayerTest.createCoreData();
		importCsv("/impex/essentialdata_usergroups.impex", "UTF-8");
		importCsv("/commerceservices/test/orgUnitTestData.impex", "UTF-8");

		i18NService.setCurrentLocale(Locale.ENGLISH);

		// Temporarily change organization related authorization properties

		createGroupsBackup = Config.getString(CREATE_GROUPS_KEY, null);
		editGroupsBackup = Config.getString(EDIT_GROUPS_KEY, null);
		editParentGroupsBackup = Config.getString(EDIT_PARENT_GROUPS_KEY, null);
		viewGroupsBackup = Config.getString(VIEW_GROUPS_KEY, null);

		Config.setParameter(CREATE_GROUPS_KEY, "orgadmingroup");
		Config.setParameter(EDIT_GROUPS_KEY, "orgadmingroup,orgmanagergroup");
		Config.setParameter(EDIT_PARENT_GROUPS_KEY, "orgadmingroup");
		Config.setParameter(VIEW_GROUPS_KEY, "orgemployeegroup");
	}

	@After
	public void cleanUp() throws Exception
	{
		Config.setParameter(CREATE_GROUPS_KEY, createGroupsBackup);
		Config.setParameter(EDIT_GROUPS_KEY, editGroupsBackup);
		Config.setParameter(EDIT_PARENT_GROUPS_KEY, editParentGroupsBackup);
		Config.setParameter(VIEW_GROUPS_KEY, viewGroupsBackup);
	}

	@Test
	public void shouldGetQuotesForEmployee()
	{
		userService.setCurrentUser(userService.getUserForUID(USA_EMPLOYEE));

		SearchPageData<QuoteModel> quoteSearchResult = testOrgUnitQuoteService
				.getQuotesForEmployee((EmployeeModel) userService.getUserForUID(USA_EMPLOYEE), createPageableData(0, 10));

		Assert.assertNotNull("Search page data is null", quoteSearchResult);
		Assert.assertNotNull("Search results are null", quoteSearchResult.getResults());

		// For an employee of the 'usa' unit we're expecting to get 1 quote from the test data:
		// - testQuote4, version 3
		Assert.assertEquals("Unexpected number of results", 1, quoteSearchResult.getResults().size());
		Assert.assertEquals("Unexpected quote code", "testQuote4", quoteSearchResult.getResults().get(0).getCode());
		Assert.assertEquals("Unexpected quote version", Integer.valueOf(3), quoteSearchResult.getResults().get(0).getVersion());
		Assert.assertEquals("Unexpected quote user", "customer2", quoteSearchResult.getResults().get(0).getUser().getUid());

		userService.setCurrentUser(userService.getUserForUID(NA_EMPLOYEE));

		quoteSearchResult = testOrgUnitQuoteService.getQuotesForEmployee((EmployeeModel) userService.getUserForUID(NA_EMPLOYEE),
				createPageableData(0, 10));

		Assert.assertNotNull("Search page data is null", quoteSearchResult);
		Assert.assertNotNull("Search results are null", quoteSearchResult.getResults());

		// For an employee of the 'northAmerica' unit we're expecting to get 3 quotes from the test data:
		// - testQuote1, version 4
		// - testQuote2, version 3
		// - testQuote3, version 4
		assertResultContains(quoteSearchResult.getResults(), "testQuote1", Integer.valueOf(6), "customer1");
		assertResultContains(quoteSearchResult.getResults(), "testQuote2", Integer.valueOf(3), "customer1");
		assertResultContains(quoteSearchResult.getResults(), "testQuote3", Integer.valueOf(4), "customer1");

		userService.setCurrentUser(userService.getUserForUID(CANADA_EMPLOYEE));

		quoteSearchResult = testOrgUnitQuoteService.getQuotesForEmployee((EmployeeModel) userService.getUserForUID(CANADA_EMPLOYEE),
				createPageableData(0, 10));

		Assert.assertNotNull("Search page data is null", quoteSearchResult);
		Assert.assertNotNull("Search results are null", quoteSearchResult.getResults());

		// For an employee of the 'canada' unit we're expecting to get 0 quotes
		Assert.assertEquals("Unexpected number of results", 0, quoteSearchResult.getResults().size());
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

	@Test
	public void shouldNotGetQuotesForEmployeeEmployeeNull()
	{
		thrown.expect(IllegalArgumentException.class);
		testOrgUnitQuoteService.getQuotesForEmployee(null, createPageableData(0, 10));
	}

	@Test
	public void shouldNotGetQuotesForEmployeePageableDataNull()
	{
		thrown.expect(IllegalArgumentException.class);
		testOrgUnitQuoteService.getQuotesForEmployee((EmployeeModel) userService.getUserForUID(USA_EMPLOYEE), null);
	}

	@Test
	public void shouldNotGetQuotesForEmployeeInsufficientRole()
	{
		thrown.expect(IllegalStateException.class);
		userService.setCurrentUser(userService.getUserForUID(UNASSIGNED_EMPLOYEE));

		testOrgUnitQuoteService.getQuotesForEmployee((EmployeeModel) userService.getUserForUID(UNASSIGNED_EMPLOYEE),
				createPageableData(0, 10));
	}

	protected PageableData createPageableData(final int currentPage, final int pageSize)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(currentPage);
		pageableData.setPageSize(pageSize);
		return pageableData;
	}

}

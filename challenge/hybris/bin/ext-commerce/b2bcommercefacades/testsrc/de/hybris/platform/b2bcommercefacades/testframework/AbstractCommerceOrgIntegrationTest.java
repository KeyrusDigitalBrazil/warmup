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
package de.hybris.platform.b2bcommercefacades.testframework;

import de.hybris.platform.b2b.dao.impl.B2BDaoTestUtils;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.test.context.ContextConfiguration;


/**
 * Base test class for commerce organization integration tests. <br>
 * <br>
 * The {@link #setUp()} method loads core data, essential data (e.g. search restrictions for commerce organization
 * related types) as well as commerce organization related sample data. Finally it sets a B2B admin as the current
 * session user.
 */
@Ignore
@ContextConfiguration(locations =
{ "classpath:b2bcommerce/test/b2bcommerce-test-spring.xml" })
public abstract class AbstractCommerceOrgIntegrationTest extends BaseCommerceBaseTest
{
	@Resource
	private UserService userService;

	@Resource
	private I18NService i18NService;

	@Resource
	private B2BDaoTestUtils b2BDaoTestUtils;

	@Before
	public void setUp() throws Exception
	{
		ServicelayerTest.createCoreData();
		importCsv("/impex/essentialdata_2_b2bcommerce.impex", "UTF-8");
		if (StringUtils.isNotEmpty(getTestDataPath()))
		{
			importCsv(getTestDataPath(), "UTF-8");
		}

		i18NService.setCurrentLocale(Locale.ENGLISH);
		setCurrentUser("DC Admin");
	}

	protected void setCurrentUser(final String userId)
	{
		final UserModel user = getUserService().getUserForUID(userId);
		getUserService().setCurrentUser(user);
	}

	/**
	 * Asserts that
	 * <ul>
	 * <li>the given {@link SearchPageData} is not null,</li>
	 * <li>that the list of results it holds is not null and</li>
	 * <li>that the list of results has the expected size.</li>
	 * </ul>
	 *
	 * @param expectedNumberOfResults
	 *           the expected number of search results
	 * @param searchPageData
	 *           the {@link SearchPageData} object to execute assertions on
	 */
	protected void assertSearchPageData(final int expectedNumberOfResults, final SearchPageData<?> searchPageData)
	{
		b2BDaoTestUtils.assertResultsSize(expectedNumberOfResults, searchPageData);
	}

	protected UserService getUserService()
	{
		return userService;
	}

	protected abstract String getTestDataPath();
}

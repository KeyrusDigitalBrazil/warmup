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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.ycommercewebservicestest.setup.TestSetupUtils
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.access.AccessRightsTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.access.OAuth2Test
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.address.AddressTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.CartDeliveryTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.CartEntriesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.CartMergeTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.CartPromotionsTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.CartResourceTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.CartVouchersTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.GuestsTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.OrderPlacementTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.SavedCartFullScenarioTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.SavedCartTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.catalogs.CatalogsResourceTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.customergroups.CustomerGroupsTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.errors.ErrorTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.export.ExportTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.filters.CartMatchingFilterTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.filters.UserMatchingFilterTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.flows.AddressBookFlow
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.flows.CartFlowTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.general.StateTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.misc.CardTypesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.misc.CurrenciesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.misc.DeliveryCountriesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.misc.LanguagesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.misc.LocalizationRequestTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.misc.TitlesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.orders.OrdersTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.paymentdetails.PaymentsTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.products.ProductResourceTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.products.ProductsStockTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.promotions.PromotionsTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.stores.StoresTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.UserAccountTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.UserOrdersTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.UsersResourceTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.general.HeaderTests
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.consents.ConsentResourcesTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.countries.CountryResourcesTest

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.slf4j.LoggerFactory


@RunWith(Suite.class)
@Suite.SuiteClasses([
	AccessRightsTest, OAuth2Test, StateTest, CartDeliveryTest, CartMergeTest, CartEntriesTest, CartPromotionsTest,
	CartResourceTest, CartVouchersTest, GuestsTest, OrderPlacementTest, CatalogsResourceTest, CustomerGroupsTest, ErrorTest, ExportTest,
	AddressBookFlow, CartFlowTest, CardTypesTest, CurrenciesTest, DeliveryCountriesTest, LanguagesTest, LocalizationRequestTest, TitlesTest,
	OrdersTest, ProductResourceTest, ProductsStockTest, PromotionsTest, SavedCartTest ,SavedCartFullScenarioTest, StoresTest, UserAccountTest,
	AddressTest, UserOrdersTest, PaymentsTest, UsersResourceTest, CartMatchingFilterTest, UserMatchingFilterTest, HeaderTests, 
	ConsentResourcesTest, CountryResourcesTest])
@IntegrationTest
class AllSpockTests {

	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AllSpockTests.class)

	@BeforeClass
	public static void setUpClass() {
		TestSetupUtils.loadData();
		TestSetupUtils.startServer();
	}

	@AfterClass
	public static void tearDown(){
		TestSetupUtils.stopServer();
		TestSetupUtils.cleanData();
	}

	@Test
	public static void testing() {
		//dummy test class
	}
}

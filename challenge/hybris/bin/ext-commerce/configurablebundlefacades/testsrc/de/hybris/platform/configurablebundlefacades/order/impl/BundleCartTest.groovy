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
 
package de.hybris.platform.configurablebundlefacades.order.impl


import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.configurablebundlefacades.AbstractConfigurableBundleFacadesSpockTest
import de.hybris.platform.order.InvalidCartException

import org.junit.Test

/**
 * Port of old ATDD-framework tests to Spock framework
 */
@IntegrationTest
class BundleCartTest extends AbstractConfigurableBundleFacadesSpockTest {

	// test data values
	def CUSTOMER_ID = "bundle"

	// is run before every test
	def setup() {
		importCsv("/configurablebundleservices/test/nestedBundleTemplates.impex", "utf-8")
		importCsv("/configurablebundleservices/test/cartRegistration.impex", "utf-8")
		prepareSession("testSite")
	}



	// Add to cart tests

	@Test
	def "new bundle adds product entry"() {
		// Test_NewBundleAddsProductEntry

		when: "we add a product for a new bundle to the cart"
		def entry = startNewBundle("ProductComponent1", "PRODUCT01", 1)

		then: "number of child carts and cart totals are correct "
		entry.product.code == "PRODUCT01"
		!entry.entryGroupNumbers.isEmpty()

		when: "we add a product for a new bundle to the cart"
		def cart = getCartDTO()

		then:
		!cart.rootGroups.isEmpty()
		cart.rootGroups[0].groupType.code == "CONFIGURABLEBUNDLE"
		cart.rootGroups[0].externalReferenceId == "ParentPackage"
		!cart.rootGroups[0].children.isEmpty()
	}

	@Test
	def "validate bundle"() {
		// Test_ValidateBundle

		when:
		login(CUSTOMER_ID)
		startNewBundle("ProductComponent1", "PRODUCT01", 1)
		def cart = getCartDTO()
		def group = findEntryGroupByRefInOrder(cart, "PremiumComponent2")
		group.erroneous == true
		validateSessionCart()

		then: "exception is expected"
		InvalidCartException e = thrown()
		e.message.startsWith("Cart is not valid")

	}

}
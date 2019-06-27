package de.hybris.platform.commercefacades.order.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercefacades.groovy.AbstractCommerceFacadesSpockTest

import org.junit.Test

@IntegrationTest
class CheckoutFacadeTest  extends AbstractCommerceFacadesSpockTest{

	def CUSTOMER_ID = "testuser@checkout.com"

	def setup() {
		importCsv("/commercefacades/test/testCheckout.csv", "utf-8")
	}


	@Test
	def "Checkout a Cart Assignment"() {
		// Adds PRODUCT01 to Cart and then proceeds with the checkout.

		given: "a session with a logged in user"
		createCustomerWithHybrisApi(CUSTOMER_ID, "pwd", "mr", "Test", "User");
		login(CUSTOMER_ID)

		when: "we add a product to a cart and perform a checkout"
		updateUserDetails()
		addProductToCartOnce("PRODUCT01")
		def order = doCheckout()

		then: "order should not be null"
		order != null
	}
}

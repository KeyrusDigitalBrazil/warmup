/**
 *
 */
package de.hybris.platform.subscriptionfacades

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commerceservices.order.CommerceSaveCartException

import org.junit.Test

/**
 * Port of old ATDD-framework tests to Spock framework
 */
@IntegrationTest
class CloneSubscriptionCartTest extends AbstractSubscriptionFacadesSpockTest {

	def CUSTOMER_ID = "testuser@saved-carts.com"
	def SAVE_CART_NAME_1 = "Saved Cart 1"
	def SAVE_CART_DESCRIPTION_1 = "This is a saved cart."
	def CLONED_CART_NAME = "clonedCartName"
	def CLONED_CART_DESCRIPTION = "clonedCartDescription"

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testCloneSubscriptionCart.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "attempt to clone NOT a master cart should raise an exception"() {
		// Clone_Child_Subscription_Cart_FAIL
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()
		addProductToCartOnce("SP1")
		def savedCartData = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		def masterCart = getCartForCodeAndUser(savedCartData.code, CUSTOMER_ID)

		when:
		cloneSavedCart(masterCart.children[0].code)

		then: "exception is expected"
		CommerceSaveCartException e = thrown()
		e.message.endsWith("Only master carts can be cloned.")

	}

	@Test
	def "saves a customer cart with a subscription product which has entries in the master cart and the monthly cart and then clone it"() {
		// Clone_Saved_Subscription_Cart
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()

		when:
		addProductToCartOnce("SP1")
		addProductToCartOnce("SP1")
		addProductToCartOnce("SP2")
		addProductToCartOnce("SP2")
		addProductToCartOnce("SP2")

		then:
		verifyNumberOfChildCarts(2)

		when:
		def savedCartData = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		def savedCart = getCartForCodeAndUser(savedCartData.code, CUSTOMER_ID)
		def savedCarts = getListOfSavedCarts("")

		then:
		savedCarts.size == 1

		when:
		def clonedCartData = cloneSavedCart(savedCartData.code, CLONED_CART_NAME, CLONED_CART_DESCRIPTION)
		def clonedCart = getCartForCodeAndUser(clonedCartData.code, CUSTOMER_ID)
		savedCarts = getListOfSavedCarts("")

		then:
		savedCarts.size == 2
		verifyCartClone(savedCartData.code, clonedCartData.code)
		verifyClonedChildCarts(savedCartData.code, clonedCartData.code)

	}

}
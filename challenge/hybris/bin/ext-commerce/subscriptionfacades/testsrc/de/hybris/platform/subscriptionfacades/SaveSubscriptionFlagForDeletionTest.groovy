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
class SaveSubscriptionFlagForDeletionTest extends AbstractSubscriptionFacadesSpockTest {

	def CUSTOMER_ID = "testuser@saved-carts.com"
	def SAVE_CART_NAME_1 = "Saved Cart 1"
	def SAVE_CART_DESCRIPTION_1 = "This is a saved cart"

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testSaveSubscriptionFlagForDeletion.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "flag a cart with a subscription product for deletion"() {
		// Test_Flag_For_Deletion_Subscription_Cart_For_Subscription_Product
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()
		addProductToCartOnce("SP1")
		verifyNumberOfChildCarts(1)

		when:
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		def monthlyCart = getCartDataForBillingTimeAndMasterCart("monthly", savedCart)

		then:
		with(savedCart) {
			name == SAVE_CART_NAME_1
			description == SAVE_CART_DESCRIPTION_1
		}
		with(monthlyCart) {
			saveTime == savedCart.saveTime
			expirationTime == savedCart.expirationTime
		}

		when:
		def flaggedCart = flagForDeletion(savedCart)
		monthlyCart = getCartDataForBillingTimeAndMasterCart("monthly", savedCart)

		then:
		with(flaggedCart) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime == null
		}
		with(monthlyCart) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime == null
		}


	}

	@Test
	def "flag a child cart for deletion exception"() {
		// Test_Flag_For_Deletion_Child_Cart
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCartDTO()
		addProductToCartOnce("SP1")
		verifyNumberOfChildCarts(1)

		when:
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		def monthlyCart = getCartDataForBillingTimeAndMasterCart("monthly", sessionCart)

		then:
		with(savedCart) {
			name == SAVE_CART_NAME_1
			description == SAVE_CART_DESCRIPTION_1
		}
		with(monthlyCart) {
			saveTime == savedCart.saveTime
			expirationTime == savedCart.expirationTime
		}

		when:
		flagForDeletion(monthlyCart)

		then:
		CommerceSaveCartException e = thrown()
		e.message.endsWith("is a child cart. Only master carts can be saved.")
	}

}
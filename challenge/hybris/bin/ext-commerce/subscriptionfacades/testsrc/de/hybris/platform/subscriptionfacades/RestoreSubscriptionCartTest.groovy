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
class RestoreSubscriptionCartTest extends AbstractSubscriptionFacadesSpockTest {

	def CUSTOMER_ID = "testuser@saved-carts.com"
	def CUSTOMER_ID_2 = "testuser2@saved-carts.com"
	def SAVE_CART_NAME_1 = "Saved Cart 1"
	def SAVE_CART_DESCRIPTION_1 = "This is a saved cart No. 1"
	def SAVE_CART_NAME_2 = "Saved Cart 2"
	def SAVE_CART_DESCRIPTION_2 = "This is a saved cart No. 2"
	def EXPIRATION_DAYS = 30

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testRestoreSubscriptionCart.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "restore a master cart  with onetime charge oncancellation and no child cart"() {
		// Test_Restore_Subscription_Cart_RestoreMasterCart_OneTimeCharge_Oncancellation_NoChildCart
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()

		when:
		addProductToCartOnce("SP1")

		then:
		verifyNumberOfChildCarts(0)
		with(sessionCart) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime == null
		}

		when:
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		restoreSavedCartWithCode(savedCart.code)
		sessionCart = getCurrentSessionCart()

		then:
		verifyNumberOfChildCarts(0)
		with(sessionCart) {
			name == savedCart.name
			description == savedCart.description
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}

	}

	@Test
	def "restore a master cart with onetime charge paynow and no child cart"() {
		// Test_Restore_Subscription_Cart_RestoreMasterCart_OneTimeCharge_Paynow_NoChildCart
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()

		when:
		addProductToCartOnce("SP2")

		then:
		verifyNumberOfChildCarts(0)
		with(sessionCart) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime == null
		}

		when:
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		restoreSavedCartWithCode(savedCart.code)
		sessionCart = getCurrentSessionCart()

		then:
		verifyNumberOfChildCarts(0)
		with(sessionCart) {
			name == savedCart.name
			description == savedCart.description
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}

	}

	@Test
	def "restore a master cart with onetime charge onfirstbill and one child cart"() {
		// Test_Restore_Subscription_Cart_RestoreMasterCart_OneTimeCharge_Onfirstbill_1xChildCart
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()

		when:
		addProductToCartOnce("SP3")

		then:
		verifyNumberOfChildCarts(1)
		with(sessionCart) {
			children.size() == 1
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime == null
		}

		when:
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		restoreSavedCartWithCode(savedCart.code)
		sessionCart = getCurrentSessionCart()

		then:
		verifyNumberOfChildCarts(1)
		with(sessionCart) {
			children.size() == 1
			name == savedCart.name
			description == savedCart.description
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}

	}

	@Test
	def "restore a master cart with recurring charge and one child cart"() {
		// Test_Restore_Subscription_Cart_RestoreMasterCart_RecurringCharge_1xChildCart
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()

		when:
		addProductToCartOnce("SP4")

		then:
		verifyNumberOfChildCarts(1)
		with(sessionCart) {
			children.size() == 1
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime == null
		}

		when:
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		restoreSavedCartWithCode(savedCart.code)
		sessionCart = getCurrentSessionCart()

		then:
		verifyNumberOfChildCarts(1)
		with(sessionCart) {
			children.size() == 1
			name == savedCart.name
			description == savedCart.description
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}

	}

	@Test
	def "restore a master cart with onetime charge onfirstbill and recurring charge and two child cart"() {
		// Test_Restore_Subscription_Cart_RestoreMasterCart_OneTimeCharge_Onfirstbill_And_RecurringCharge_2xChildCart
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()

		when:
		addProductToCartOnce("SP5")

		then:
		verifyNumberOfChildCarts(2)
		with(sessionCart) {
			children.size() == 2
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime == null
		}

		when:
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		restoreSavedCartWithCode(savedCart.code)
		sessionCart = getCurrentSessionCart()

		then:
		verifyNumberOfChildCarts(2)
		with(sessionCart) {
			children.size() == 2
			name == savedCart.name
			description == savedCart.description
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}

	}

	@Test
	def "restore a master cart with multiple carts"() {
		// Test_Restore_Subscription_Cart_RestoreMasterCart_MultipleCarts
		given: "logged in customer"
		login(CUSTOMER_ID)

		when:
		def sessionCart = getCurrentSessionCart()
		addProductToCartOnce("SP6")
		def savedCart1 = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		sessionCart = removeAndCreateNewSessionCart()
		addProductToCartOnce("SP7")
		def savedCart2 = saveCartWithNameAndDescription(SAVE_CART_NAME_2, SAVE_CART_DESCRIPTION_2)

		then:
		with(sessionCart) {
			children.size() == 1
			name == SAVE_CART_NAME_2
			description == SAVE_CART_DESCRIPTION_2
			saveTime != null
			savedBy.uid == CUSTOMER_ID
			expirationTime == saveTime + EXPIRATION_DAYS
		}

		when:
		restoreSavedCartWithCode(savedCart1.code)
		sessionCart = getCurrentSessionCart()

		then:
		with(sessionCart) {
			children.size() == 2
			name == SAVE_CART_NAME_1
			description == SAVE_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}

		when:
		restoreSavedCartWithCode(savedCart2.code)
		sessionCart = getCurrentSessionCart()

		then:
		with(sessionCart) {
			children.size() == 1
			name == SAVE_CART_NAME_2
			description == SAVE_CART_DESCRIPTION_2
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}
	}
	@Test
	def "restore a master cart with multiple customers"() {
		// Test_Restore_Subscription_Cart_RestoreMasterCart_MultipleCustomers
		given: "logged in customer"
		login(CUSTOMER_ID)

		when:
		def sessionCart = getCurrentSessionCart()
		addProductToCartOnce("SP8")
		def savedCart1 = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		login(CUSTOMER_ID_2)
		sessionCart = removeAndCreateNewSessionCart()
		addProductToCartOnce("SP8")
		def savedCart2 = saveCartWithNameAndDescription(SAVE_CART_NAME_2, SAVE_CART_DESCRIPTION_2)

		then:
		with(sessionCart) {
			children.size() == 2
			name == SAVE_CART_NAME_2
			description == SAVE_CART_DESCRIPTION_2
			saveTime != null
			savedBy.uid == CUSTOMER_ID_2
			expirationTime != null
		}

		when:
		restoreSavedCartWithCode(savedCart1.code)
		sessionCart = getCurrentSessionCart()

		then:
		with(sessionCart) {
			children.size() == 2
			name == SAVE_CART_NAME_1
			description == SAVE_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}

		when:
		restoreSavedCartWithCode(savedCart2.code)
		sessionCart = getCurrentSessionCart()

		then:
		with(sessionCart) {
			children.size() == 2
			name == SAVE_CART_NAME_2
			description == SAVE_CART_DESCRIPTION_2
			savedBy.uid == CUSTOMER_ID_2
			expirationTime != null
		}
	}

	@Test
	def "restore a child cart with onetime charge on first bill exception"() {
		// Test_Restore_Subscription_Cart_RestoreChildCart_OneTimeCharge_Onfirstbill_Exception
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()
		addProductToCartOnce("SP9")
		verifyNumberOfChildCarts(1)
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		def onfirstbillCart = getCartDataForBillingTimeAndMasterCart("onfirstbill", savedCart)

		when:
		restoreSavedCartWithCode(onfirstbillCart.code)

		then:
		CommerceSaveCartException e = thrown()
		e.message.endsWith("is a child cart. Only master carts can be restored.")

	}

	@Test
	def "restore a child cart with recurring charge exception"() {
		// Test_Restore_Subscription_Cart_RestoreChildCart_RecurringCharge_Exception
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()
		addProductToCartOnce("SP10")
		verifyNumberOfChildCarts(1)
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		def monthlyCart = getCartDataForBillingTimeAndMasterCart("monthly", savedCart)

		when:
		restoreSavedCartWithCode(monthlyCart.code)

		then:
		CommerceSaveCartException e = thrown()
		e.message.endsWith("is a child cart. Only master carts can be restored.")

	}


}
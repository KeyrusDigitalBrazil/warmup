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
class SaveSubscriptionCartTest extends AbstractSubscriptionFacadesSpockTest {

	def CUSTOMER_ID = "testuser@saved-carts.com"
	def SAVE_CART_NAME_1 = "Saved Cart 1"
	def SAVE_CART_DESCRIPTION_1 = "This is a saved cart No. 1"

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testSaveSubscriptionCart.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "saves a cart with a subscription product with onetime charge oncancellation no child cart"() {
		// Test_Save_Subscription_Cart_SaveMasterCart_OneTimeCharge_Oncancellation_NoChildCart
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()
		addProductToCartOnce("SP1")
		verifyNumberOfChildCarts(0)

		when:
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)

		then:
		verifyNumberOfChildCarts(0)
		with(savedCart) {
			name == SAVE_CART_NAME_1
			description == SAVE_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}

	}

	@Test
	def "saves a cart with a subscription product with onetime charge paynow no child cart"() {
		// Test_Save_Subscription_Cart_SaveMasterCart_OneTimeCharge_Paynow_NoChildCart
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()
		addProductToCartOnce("SP2")
		verifyNumberOfChildCarts(0)

		when:
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)

		then:
		verifyNumberOfChildCarts(0)
		with(savedCart) {
			name == SAVE_CART_NAME_1
			description == SAVE_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}

	}

	@Test
	def "saves a cart with a subscription product with onetime charge onfirstbill one child cart"() {
		// Test_Save_Subscription_Cart_SaveMasterCart_OneTimeCharge_Onfirstbill_1xChildCart
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()
		addProductToCartOnce("SP3")
		verifyNumberOfChildCarts(1)

		when:
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		def onFirstBillCart = getCartDataForBillingTimeAndMasterCart("onfirstbill", savedCart)

		then:
		verifyNumberOfChildCarts(1)
		with(savedCart) {
			name == SAVE_CART_NAME_1
			description == SAVE_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}
		with(onFirstBillCart) {
			saveTime == savedCart.saveTime
			expirationTime == savedCart.expirationTime
		}
	}

	@Test
	def "saves a cart with a subscription product with recurring charge  one child cart"() {
		// Test_Save_Subscription_Cart_SaveMasterCart_OneTimeCharge_Onfirstbill_1xChildCart
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()
		addProductToCartOnce("SP4")
		verifyNumberOfChildCarts(1)

		when:
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		def monthlyCart = getCartDataForBillingTimeAndMasterCart("monthly", savedCart)

		then:
		verifyNumberOfChildCarts(1)
		with(savedCart) {
			name == SAVE_CART_NAME_1
			description == SAVE_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}
		with(monthlyCart) {
			saveTime == savedCart.saveTime
			expirationTime == savedCart.expirationTime
		}
	}

	@Test
	def "saves a cart with a subscription product with onetime charge onfirstbill recurring charge two child carts"() {
		// Test_Save_Subscription_Cart_SaveMasterCart_OneTimeCharge_Onfirstbill_And_RecurringCharge_2xChildCart
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()
		addProductToCartOnce("SP5")
		verifyNumberOfChildCarts(2)

		when:
		def savedCart = saveCartWithNameAndDescription(SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		def onFirstBillCart = getCartDataForBillingTimeAndMasterCart("onfirstbill", savedCart)
		def monthlyCart = getCartDataForBillingTimeAndMasterCart("monthly", savedCart)

		then:
		verifyNumberOfChildCarts(2)
		with(savedCart) {
			name == SAVE_CART_NAME_1
			description == SAVE_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}
		with(onFirstBillCart) {
			saveTime == savedCart.saveTime
			expirationTime == savedCart.expirationTime
		}
		with(monthlyCart) {
			saveTime == savedCart.saveTime
			expirationTime == savedCart.expirationTime
		}
	}
	@Test
	def "saves a cart with a subscription product by cafrt code"() {
		// Test_Save_Subscription_Cart_SaveMasterCart_ByCartCode
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCurrentSessionCart()
		addProductToCartOnce("SP6")
		verifyNumberOfChildCarts(1)

		when:
		def savedCart = saveGivenCartWithNameAndDescription(sessionCart.code, SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)
		def monthlyCart = getCartDataForBillingTimeAndMasterCart("monthly", savedCart)

		then:
		verifyNumberOfChildCarts(1)
		with(savedCart) {
			name == SAVE_CART_NAME_1
			description == SAVE_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID
			expirationTime != null
		}
		with(monthlyCart) {
			saveTime == savedCart.saveTime
			expirationTime == savedCart.expirationTime
		}
	}

	@Test
	def "saves a cart with a subscription product save child cart with onetime charge onfirstbill exception"() {
		// Test_Save_Subscription_Cart_SaveChildCart_OneTimeCharge_Onfirstbill_Exception
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCartDTO()
		addProductToCartOnce("SP7")
		verifyNumberOfChildCarts(1)
		def onFirstBillCart = getCartDataForBillingTimeAndMasterCart("onfirstbill", sessionCart)

		when:
		saveGivenCartWithNameAndDescription(onFirstBillCart.code, SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)

		then:
		CommerceSaveCartException e = thrown()
		e.message.endsWith("is a child cart. Only master carts can be saved.")
	}

	@Test
	def "saves a cart with a subscription product save child cart with recurring charge exception"() {
		// Test_Save_Subscription_Cart_SaveChildCart_RecurringCharge_Exception
		given: "logged in customer"
		login(CUSTOMER_ID)
		def sessionCart = getCartDTO()
		addProductToCartOnce("SP8")
		verifyNumberOfChildCarts(1)
		def monthlyCart = getCartDataForBillingTimeAndMasterCart("monthly", sessionCart)

		when:
		saveGivenCartWithNameAndDescription(monthlyCart.code, SAVE_CART_NAME_1, SAVE_CART_DESCRIPTION_1)

		then:
		CommerceSaveCartException e = thrown()
		e.message.endsWith("is a child cart. Only master carts can be saved.")
	}

}
package de.hybris.platform.commercefacades.order.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercefacades.groovy.AbstractCommerceFacadesSpockTest
import de.hybris.platform.commerceservices.order.CommerceSaveCartException

import org.junit.Test

@IntegrationTest
class SaveCartCloneTest  extends AbstractCommerceFacadesSpockTest{

	def CUSTOMER_ID_1 = "testuser1@saved-carts.com"
	def CUSTOMER_ID_2 = "testuser2@saved-carts.com"

	def PRODUCT_CODE_1 = "P1"
	def PRODUCT_CODE_2 = "P2"

	def EXPIRATION_DAYS = 30

	def setup() {
		importCsv("/commercefacades/test/testCommerceServices.csv", "utf-8")
	}

	@Test
	def "Test Clone Empty Saved Cart"() {

		given: "a session with a logged in user"
		def sessionCart = getCurrentSessionCart()
		createCustomer(CUSTOMER_ID_1);
		login(CUSTOMER_ID_1)

		when: "we save the current session cart"
		def savedCart = saveCartWithNameAndDescription("EmptyCart", "An Empty Cart")

		then:
		with (savedCart) {
			code == sessionCart.code
			saveTime != null
			expirationTime != null
		}

		when: "we clone the cart"
		def clonedCart = cloneSavedCart(savedCart.code)

		then: "the clone does not have the same code as the original saved cart, is identical to the original saved cart, and has been saved"
		with (clonedCart) {
			code != savedCart.code
			name == "Copy of " + savedCart.name
			description == savedCart.description
			entries.size() == savedCart.entries.size()
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime == saveTime + EXPIRATION_DAYS
		}
	}

	@Test
	def "Test Clone Saved Cart With Products"() {

		given: "a session with a logged in user and products on their cart"
		def sessionCart = getCurrentSessionCart()
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)
		createProduct(PRODUCT_CODE_1, "pieces", "USD", "100")
		addProductToCartOnce(PRODUCT_CODE_1)
		createProduct(PRODUCT_CODE_2, "pieces", "USD", "50")
		addProductToCart(PRODUCT_CODE_2, 2)

		when: "we save the current session cart"
		def savedCart = saveCartWithNameAndDescription("TestCart", "A Test Cart")

		then:
		with (savedCart) {
			code == sessionCart.code
			saveTime != null
			expirationTime != null
			entries[0].product.code == PRODUCT_CODE_1
			entries[0].quantity == 1
			entries[1].product.code == PRODUCT_CODE_2
			entries[1].quantity == 2
		}

		when: "we clone the cart"
		def cloneName = "Copy of " + savedCart.name
		def clonedCart = cloneSavedCart(savedCart.code, cloneName, savedCart.description)

		then:
		verifyCartClone(savedCart.code, clonedCart.code)
		with (clonedCart) {
			savedBy.uid == savedCart.savedBy.uid
			name == cloneName
			description == savedCart.description
			saveTime != null
			expirationTime == saveTime + EXPIRATION_DAYS
		}
	}

	@Test
	def "Test Clone Not Saved Cart Fail"() {
		given: "a session with a logged in user"
		def sessionCart = getCurrentSessionCart()
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when:"we try to clone a cart that has not been saved"
		cloneSavedCart(sessionCart.code)

		then:
		CommerceSaveCartException e = thrown()
		e.message == "Cannot find a saved cart for code ["+sessionCart.code+"]"
	}

	@Test
	def "Test Clone Cart Flagged For Deletion Fail"() {
		given: "a session with a logged in user"
		def sessionCart = getCurrentSessionCart()
		createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when: "we save the current session cart"
		def savedCart = saveCartWithNameAndDescription("EmptyCart", "An Empty Cart")

		then:
		with (savedCart) {
			code == sessionCart.code
			saveTime != null
			expirationTime != null
		}

		when: "we clone the cart"
		def clonedCart = cloneSavedCart(savedCart.code)

		then: "the clone does not have the same code as the original saved cart, is identical to the original saved cart, and has been saved"
		clonedCart.code != savedCart.code


		when:"we flag the cart for deletion and try to clone it"
		flagCartForDeletion(savedCart.code)
		cloneSavedCart(savedCart.code)

		then:
		CommerceSaveCartException e = thrown()
		e.message == "Cannot find a saved cart for code ["+savedCart.code+"]"
	}

	@Test
	def "Test Clone Saved Cart Of Different User Fail"() {

		given: "a session with a logged in user"
		createCustomer(CUSTOMER_ID_1)
		createCustomer(CUSTOMER_ID_2)
		def sessionCart1 = getCurrentSessionCart()
		login(CUSTOMER_ID_1)

		when: "we save the current session cart"
		def savedCart = saveCartWithNameAndDescription("EmptyCart", "An Empty Cart")

		then:
		with (savedCart) {
			code == sessionCart1.code
			saveTime != null
			expirationTime != null
		}


		when:"we try to clone a cart belonging to another csutomer"
		def sessionCart2 = removeAndCreateNewSessionCart()
		login(CUSTOMER_ID_2)
		cloneSavedCart(savedCart.code)

		then:
		CommerceSaveCartException e = thrown()
		e.message == "Cannot find a saved cart for code ["+savedCart.code+"]"
	}
}

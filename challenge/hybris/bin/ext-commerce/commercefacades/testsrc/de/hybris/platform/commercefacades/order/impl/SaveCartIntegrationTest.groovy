package de.hybris.platform.commercefacades.order.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercefacades.groovy.AbstractCommerceFacadesSpockTest

import spock.lang.Ignore
import org.junit.Test


@IntegrationTest
class SaveCartIntegrationTest  extends AbstractCommerceFacadesSpockTest{

	def CUSTOMER_ID_1 = "testuser1@saved-carts.com"
	def CUSTOMER_ID_2 = "testuser2@saved-carts.com"
	def SAVED_CART_NAME_1 = "Saved Cart 1"
	def SAVED_CART_DESCRIPTION_1 = "This is a saved cart"
	def SAVED_CART_NAME_2 = "Saved Cart 2"
	def SAVED_CART_DESCRIPTION_2 = "This is another saved cart"
	def SAVED_CART_NAME_3 = "CartNameNo3"
	def SAVED_CART_DESCRIPTION_3 = "CartNo3Description"
	def EXPIRATION_DAYS = 30

	def setup() {
		importCsv("/commercefacades/test/testCommerceServices.csv", "utf-8")
	}

	@Ignore
	@Test
	def "Test Save Multiple Carts CustomerSessionCart Then Restore And Update"() {
		// Save 2 Carts and then restore one and update it

		given: "a logged in customer and session cart"
		def sessionCart = getCurrentSessionCart()
		createCustomer(CUSTOMER_ID_1);
		login(CUSTOMER_ID_1)

		when: "we save the cart"
		def savedCart = saveCartWithNameAndDescription(SAVED_CART_NAME_1, SAVED_CART_DESCRIPTION_1)

		then:
		with (savedCart) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime == saveTime + EXPIRATION_DAYS
		}

		when: "we save another cart"
		removeAndCreateNewSessionCart()
		def savedCart2 = saveCartWithNameAndDescription(SAVED_CART_NAME_2, SAVED_CART_DESCRIPTION_2)

		then:
		with (savedCart2) {
			name == SAVED_CART_NAME_2
			description == SAVED_CART_DESCRIPTION_2
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime == saveTime + EXPIRATION_DAYS
		}

		when: "we restore the first cart"
		restoreSavedCartWithCode(savedCart.code)
		def restoredCart = getCurrentSessionCart()

		then:
		with (restoredCart) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime == saveTime + EXPIRATION_DAYS
		}

		when: "we update the restored cart"
		def updatedCart = saveGivenCartWithNameAndDescription(restoredCart.code, "New Name", "New Description")

		then:
		with (updatedCart) {
			name == "New Name"
			description == "New Description"
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime == saveTime + EXPIRATION_DAYS
		}

	}

	@Test
	def "Test Save Multiple Carts CustomerSessionCart Then Retrieve And Update"() {
		// Save 2 carts then retrieve one and update it without providing any name/description

		given: "a logged in customer and session cart"
		def sessionCart = getCurrentSessionCart()
		createCustomer(CUSTOMER_ID_1);
		login(CUSTOMER_ID_1)

		when: "we save the cart"
		def savedCart = saveCartWithNameAndDescription(SAVED_CART_NAME_1, SAVED_CART_DESCRIPTION_1)

		then:
		with (savedCart) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime == saveTime + EXPIRATION_DAYS
		}

		when: "we save another cart"
		removeAndCreateNewSessionCart()
		def savedCart2 = saveCartWithNameAndDescription(SAVED_CART_NAME_2, SAVED_CART_DESCRIPTION_2)

		then:
		with (savedCart2) {
			name == SAVED_CART_NAME_2
			description == SAVED_CART_DESCRIPTION_2
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime == saveTime + EXPIRATION_DAYS
		}

		when: "we restore the first cart"
		def retrievedCart = retrieveSavedCartWithCode(savedCart.code)

		then:
		with (retrievedCart) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime == saveTime + EXPIRATION_DAYS
		}

		when: "we update the retreived cart"
		def updatedCart = saveGivenCartWithNameAndDescription(retrievedCart.code, null, null)

		then:
		with (updatedCart) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime == saveTime + EXPIRATION_DAYS
		}

	}

	@Test
	def "Test Save Multiple Carts CustomerSessionCart Then Flag For Deletion"() {
		// Save 2 Carts then get list of saved carts and then flag a cart for deletion and get list of saved carts again

		given: "a logged in customer and session cart"
		def sessionCart = getCurrentSessionCart()
		createCustomer(CUSTOMER_ID_1);
		login(CUSTOMER_ID_1)

		when: "we save the cart"
		def savedCart = saveCartWithNameAndDescription(SAVED_CART_NAME_1, SAVED_CART_DESCRIPTION_1)

		then:
		with (savedCart) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime == saveTime + EXPIRATION_DAYS
		}

		when: "we save another cart"
		removeAndCreateNewSessionCart()
		def savedCart2 = saveCartWithNameAndDescription(SAVED_CART_NAME_2, SAVED_CART_DESCRIPTION_2)

		then:
		with (savedCart2) {
			name == SAVED_CART_NAME_2
			description == SAVED_CART_DESCRIPTION_2
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			expirationTime == saveTime + EXPIRATION_DAYS
		}

		when: "we restore the list of carts"
		def carts1 = getListOfSavedCarts("")

		then:
		carts1.size() == 2

		when: "we update the retreived cart"
		def flaggedCart = flagCartForDeletion(savedCart2.code)

		then:
		with (flaggedCart) {
			name == null
			description == null
			savedBy == null
			saveTime == null
			expirationTime == null
		}

		when: "we restore the list of carts"
		def carts2 = getListOfSavedCarts("")

		then:
		carts2.size() == 1


	}

}

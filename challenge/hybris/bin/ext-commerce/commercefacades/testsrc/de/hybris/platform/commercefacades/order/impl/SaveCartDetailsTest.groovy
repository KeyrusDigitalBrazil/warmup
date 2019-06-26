package de.hybris.platform.commercefacades.order.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercefacades.groovy.AbstractCommerceFacadesSpockTest
import de.hybris.platform.commerceservices.order.CommerceSaveCartException

import spock.lang.Ignore
import org.junit.Test

@IntegrationTest
class SaveCartDetailsTest  extends AbstractCommerceFacadesSpockTest{

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

	@Test
	def "Test Save Cart Details Retrieve CartDetails WrongCode"() {

		when:
		retrieveSavedCartWithCode("wrongCode")

		then:
		CommerceSaveCartException e = thrown()
		e.message == "Cannot find a cart for code [wrongCode]"
	}

	@Test
	def "Test Save Cart Details Retrieve CartDetails EmptyCode"() {

		when:
		retrieveSavedCartWithCode("")

		then:
		CommerceSaveCartException e = thrown()
		e.message == "Cart code cannot be empty"
	}

	@Test
	def "Test Save Cart Details Retrieve CartDetails SingleCart"() {

		given: "a customer"
		createCustomerWithHybrisApi(CUSTOMER_ID_1, "pwd", "mr", "Test", "User");

		when: "we retrieve the session cart"
		def sessionCart = getCurrentSessionCart()

		then:
		with (sessionCart) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime == null
		}

		when: "login, save the session cart and retreive the cart by code"
		login(CUSTOMER_ID_1)
		def savedCart = saveCartWithNameAndDescription(SAVED_CART_NAME_1, SAVED_CART_DESCRIPTION_1)
		def retrievedCart = retrieveSavedCartWithCode(savedCart.code)

		then:
		with (retrievedCart) {
			name == savedCart.name
			description == savedCart.description
			saveTime != null
			saveTime == savedCart.saveTime
			savedBy.uid == CUSTOMER_ID_1
			expirationTime == saveTime + EXPIRATION_DAYS
			expirationTime == savedCart.expirationTime
		}
	}

	@Ignore
	@Test
	def "Test Save Cart Details Retrieve CartDetails MultipleCarts"() {

		given: "a logged in customer and the session cart"
		def sessionCart = getCurrentSessionCart()
		createCustomerWithHybrisApi(CUSTOMER_ID_1, "pwd", "mr", "Test", "User");
		login(CUSTOMER_ID_1)

		when: "save some carts for that customer and then retrive them"
		def savedCart1 = saveCartWithNameAndDescription(SAVED_CART_NAME_1, SAVED_CART_DESCRIPTION_1)
		removeAndCreateNewSessionCart()
		def savedCart2 = saveCartWithNameAndDescription(SAVED_CART_NAME_2, SAVED_CART_DESCRIPTION_2)
		removeAndCreateNewSessionCart()
		def savedCart3 = saveCartWithNameAndDescription(SAVED_CART_NAME_3, SAVED_CART_DESCRIPTION_3)
		removeAndCreateNewSessionCart()
		def savedCart4 = saveCartWithNameAndDescription("", "")
		def retrievedCart1 = retrieveSavedCartWithCode(savedCart1.code)
		def retrievedCart2 = retrieveSavedCartWithCode(savedCart2.code)
		def retrievedCart3 = retrieveSavedCartWithCode(savedCart3.code)
		def retrievedCart4 = retrieveSavedCartWithCode(savedCart4.code)

		then:
		with (retrievedCart1) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			saveTime == savedCart1.saveTime
			expirationTime == saveTime + EXPIRATION_DAYS
			expirationTime == savedCart1.expirationTime
		}
		with (retrievedCart2) {
			name == SAVED_CART_NAME_2
			description == SAVED_CART_DESCRIPTION_2
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			saveTime == savedCart2.saveTime
			expirationTime == saveTime + EXPIRATION_DAYS
			expirationTime == savedCart2.expirationTime
		}
		with (retrievedCart3) {
			name == SAVED_CART_NAME_3
			description == SAVED_CART_DESCRIPTION_3
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			saveTime == savedCart3.saveTime
			expirationTime == saveTime + EXPIRATION_DAYS
			expirationTime == savedCart3.expirationTime
		}
		with (retrievedCart4) {
			name.matches("\\d{8}")
			description == "-"
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			saveTime == savedCart4.saveTime
			expirationTime == saveTime + EXPIRATION_DAYS
			expirationTime == savedCart4.expirationTime
		}
	}

	@Test
	def "Test Save Cart Details RetrieveCartDetails MultipleCustomers"() {

		given: "two customers"
		createCustomerWithHybrisApi(CUSTOMER_ID_1, "pwd", "mr", "Test", "User1");
		createCustomerWithHybrisApi(CUSTOMER_ID_2, "pwd", "mr", "Test", "User2");


		when: "save a cart for each customer and then retrive them"
		def sessionCart1 = getCurrentSessionCart()
		login(CUSTOMER_ID_1)
		def savedCart1 = saveCartWithNameAndDescription(SAVED_CART_NAME_1, SAVED_CART_DESCRIPTION_1)

		login(CUSTOMER_ID_2)
		def sessionCart2 = removeAndCreateNewSessionCart()
		def savedCart2 = saveCartWithNameAndDescription(SAVED_CART_NAME_2, SAVED_CART_DESCRIPTION_2)

		def retrievedCart1 = retrieveSavedCartWithCode(savedCart1.code)
		def retrievedCart2 = retrieveSavedCartWithCode(savedCart2.code)

		then:
		with (retrievedCart1) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			savedBy.uid == CUSTOMER_ID_1
			saveTime != null
			saveTime == savedCart1.saveTime
			expirationTime == saveTime + EXPIRATION_DAYS
			expirationTime == savedCart1.expirationTime
		}
		with (retrievedCart2) {
			name == SAVED_CART_NAME_2
			description == SAVED_CART_DESCRIPTION_2
			savedBy.uid == CUSTOMER_ID_2
			saveTime != null
			saveTime == savedCart2.saveTime
			expirationTime == saveTime + EXPIRATION_DAYS
			expirationTime == savedCart2.expirationTime
		}
	}
}

package de.hybris.platform.commercefacades.order.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercefacades.groovy.AbstractCommerceFacadesSpockTest

import org.junit.Test

@IntegrationTest
class SaveCartListTest  extends AbstractCommerceFacadesSpockTest{

	def BASE_SITE_ID = "UnsaveCartTestSite"
	def CUSTOMER_ID_1 = "testuser2@savecartlist.com"
	def CUSTOMER_ID_2 = "testuser2@savecartlist.com"

	def SAVED_CART_NAME_1 = "Test cart 1 name"
	def SAVED_CART_DESCRIPTION_1 = "Test cart 1 description"
	def SAVED_CART_NAME_2 = "Test cart 2 name"
	def SAVED_CART_DESCRIPTION_2 = "Test cart 2 description"

	def setup() {
		importCsv("/commercefacades/test/testCommerceServices.csv", "utf-8")
	}

	@Test
	def "Get Empty List"() {

		when: "get the list of saved carts for the current customer"
		def savedCartList = getSavedCartsForCurrentUser()

		then: "the list returned is empty"
		savedCartList.size() == 0
	}

	@Test
	def "Get Empty List for logged in customer"() {

		given: "a session with a logged in customer"
		def customer = createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when: "get the list of saved carts for the current customer"
		def savedCartList = getSavedCartsForCurrentUser()

		then: "the list returned is empty"
		savedCartList.size() == 0
	}

	@Test
	def "Get a list of saved carts with a single cart"() {

		given: "a loged in user"
		def customer = createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		when: "we save the current session cart"
		def savedCart = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)
		def cartList = getSavedCartsForCurrentUser()

		then: "the cart has saved cart attribute values"
		with(savedCart) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			saveTime != null
			savedBy != null
			expirationTime != null
		}
		cartList.size() == 1
	}

	@Test
	def "Get list with multiple carts"() {

		given: "a logged in customer with two saved carts"
		def customer = createCustomer(CUSTOMER_ID_1)
		login(CUSTOMER_ID_1)

		def saveCartResultData1 = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)
		removeAndCreateNewSessionCart()
		def saveCartResultData2 = saveCartWithNameAndDescription(SAVED_CART_NAME_2,SAVED_CART_DESCRIPTION_2)

		when: "we get the list of saved carts for the current customer"
		def savedCartList = getSavedCartsForCurrentUser()
		def cart1 = getSavedCartFromList(savedCartList, saveCartResultData1.code)
		def cart2 = getSavedCartFromList(savedCartList, saveCartResultData2.code)

		then: "there are 2 carts found and cart DTO attributes are populated"
		savedCartList.size() == 2
		with(cart1) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			saveTime != null
			savedBy != null
			expirationTime != null
		}
		with(cart2) {
			name == SAVED_CART_NAME_2
			description == SAVED_CART_DESCRIPTION_2
			saveTime != null
			savedBy != null
			expirationTime != null
		}
	}

	// Test Get_List_And_Flag_Cart_For_Deletion already covered in SaveCartUnsaveTest

}

package de.hybris.platform.commercefacades.order.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercefacades.groovy.AbstractCommerceFacadesSpockTest
import de.hybris.platform.commerceservices.order.CommerceSaveCartException

import org.junit.Test

@IntegrationTest
class SaveCartUnsaveTest  extends AbstractCommerceFacadesSpockTest{

	def BASE_SITE_ID = "testSite"
	def CUSTOMER_ID = "testuser@savedcarts.com"
	def SAVED_CART_NAME_1 = "Test unsave cart 1 name"
	def SAVED_CART_DESCRIPTION_1 = "Test unsave cart description"
	def SAVED_CART_NAME_2 = "Test unsave cart 2 name"
	def SAVED_CART_DESCRIPTION_2 = "Test unsave cart 2 description"
	def EXP_DAYS = 30

	def setup() {
		importCsv("/commercefacades/test/testCommerceServices.csv", "utf-8")
	}

	@Test
	def "Test Flag Cart For Deletion For Customer With Existing Saved Cart"() {

		given: "a customer"
		def customer = createCustomer(CUSTOMER_ID)

		when: "we log that customer in"
		login(CUSTOMER_ID)

		then: "the cart has no details"
		with(getCurrentSessionCart()) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime == null
		}

		when: "we save the current cart"
		def saveCartData1 = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)
		def savedCartList = getSavedCartsForCurrentUser()

		then: "the cart has saved cart attribute values"
		with(getCurrentSessionCart()) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			saveTime != null
			savedBy != CUSTOMER_ID
			expirationTime == saveTime + EXP_DAYS
		}
		resultCartDataMatchesSessionCartData(saveCartData1) == true
		savedCartList.size() == 1

		when: "we flag the saved cart for deletion"
		def flaggedCart = flagForDeletion(saveCartData1)

		then: "the saved cart attribues are nulled out"
		with(flaggedCart) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime ==  null
		}
	}

	@Test
	def "Test Save Multiple Carts CustomerSessionCart Then Retrive And Update"() {

		given: "a logged in customer with two saved carts"
		def customer = createCustomer(CUSTOMER_ID)
		login(CUSTOMER_ID)
		def saveCartData1 = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)
		removeAndCreateNewSessionCart()
		def saveCartResultData2 = saveCartWithNameAndDescription(SAVED_CART_NAME_2,SAVED_CART_DESCRIPTION_2)

		when: "we get the list of saved carts for the current customer"
		def savedCartList = getSavedCartsForCurrentUser()

		then: "there are 2 carts found"
		savedCartList.size() == 2

		when: "we flag the first saved cart for deletion"
		def flaggedCart1 = flagForDeletion(saveCartData1)

		then: "saved attributes are nulled out"
		with(flaggedCart1) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime ==  null
		}

		when: "we get the second cart from the list"
		def cart2 = getSavedCartFromList(savedCartList, saveCartResultData2.code)

		then: "the saved cart attributes are populated"
		with(cart2) {
			name == SAVED_CART_NAME_2
			description == SAVED_CART_DESCRIPTION_2
			saveTime != null
			savedBy != CUSTOMER_ID
			expirationTime == saveTime + EXP_DAYS
		}
	}

	@Test
	def "Flag an already flagged cart for deletion"() {

		given: "a customer"
		def customer = createCustomer(CUSTOMER_ID)

		when: "we log that customer in"
		login(CUSTOMER_ID)

		then: "the cart has no details"
		with(getCurrentSessionCart()) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime == null
		}

		when: "we save the current cart"
		def saveCartData1 = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)
		def savedCartList = getSavedCartsForCurrentUser()

		then: "the cart has saved cart attribute values and the number of saved carts is one"
		with(getCurrentSessionCart()) {
			name == SAVED_CART_NAME_1
			description == SAVED_CART_DESCRIPTION_1
			saveTime != null
			savedBy != CUSTOMER_ID
			expirationTime == saveTime + EXP_DAYS
		}
		resultCartDataMatchesSessionCartData(saveCartData1) == true
		savedCartList.size() == 1

		when: "we flag the saved cart for deletion"
		def flaggedCart = flagForDeletion(saveCartData1)

		then: "the saved attributes are nulled out"
		with(flaggedCart) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime ==  null
		}

		when: "we flag the saved cart for deletion a second time"
		flaggedCart = flagForDeletion(saveCartData1)

		then: "the saved attributes are still nulled out"
		with(flaggedCart) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime ==  null
		}
	}

	@Test
	def "Flag a non-existing cart for deletion"() {

		given: "a looged in customer with a saved cart"
		def customer = createCustomer(CUSTOMER_ID)
		login(CUSTOMER_ID)
		def saveCartData1 = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)

		when: "flag a saved cart for deletion using an incorrect code"
		def flaggedCart = flagCartForDeletion("*")

		then: "no cart is found"
		CommerceSaveCartException e = thrown()
		e.message == "Cannot find a cart for code [*]"
	}

	@Test
	def "Flag multiple saved carts for same customer for deletion"() {

		given: "a logged in customer with two saved carts"
		def customer = createCustomer(CUSTOMER_ID)
		login(CUSTOMER_ID)
		def saveCartData1 = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)
		removeAndCreateNewSessionCart()
		def saveCartData2 = saveCartWithNameAndDescription(SAVED_CART_NAME_2,SAVED_CART_DESCRIPTION_2)

		when: "we get the list of saved carts for the current customer"
		def savedCartList = getSavedCartsForCurrentUser()

		then: "there are 2 carts found"
		savedCartList.size() == 2

		when: "we flag the first saved cart for deletion"
		def flaggedCart1 = flagForDeletion(saveCartData1)

		then: "the saved cart attributes are nulled out"
		with(flaggedCart1) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime ==  null
		}

		when: "we get the list of carts again"
		savedCartList = getSavedCartsForCurrentUser()
		def cart2 = getSavedCartFromList(savedCartList, saveCartData2.code)

		then: "the list size is one and saved cart attributes are populated"
		savedCartList.size() == 1
		with(cart2) {
			name == SAVED_CART_NAME_2
			description == SAVED_CART_DESCRIPTION_2
			saveTime != null
			savedBy != CUSTOMER_ID
			expirationTime == saveTime + EXP_DAYS
		}

		when: "we flag the second saved cart for deletion"
		def flaggedCart2 = flagForDeletion(saveCartData2)

		then: "the saved attributes are nulled out"
		with(flaggedCart2) {
			name == null
			description == null
			saveTime == null
			savedBy == null
			expirationTime ==  null
		}
	}
}

package de.hybris.platform.commercefacades.order.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercefacades.groovy.AbstractCommerceFacadesSpockTest
import de.hybris.platform.commercefacades.order.data.CartData

import org.junit.Test

@IntegrationTest
class SaveCartUnsaveDaoTest  extends AbstractCommerceFacadesSpockTest{

	def BASE_SITE_ID = "UnsaveCartTestSite"
	def CUSTOMER_ID = "unsavecart@test.com"
	def SAVED_CART_NAME_1 = "Test unsave cart 1 name"
	def SAVED_CART_DESCRIPTION_1 = "Test unsave cart description"
	def SAVED_CART_NAME_2 = "Test unsave cart 2 name"
	def SAVED_CART_DESCRIPTION_2 = "Test unsave cart 2 description"
	def EXP_DAYS = 30

	def setup() {
		importCsv("/commercefacades/test/testCommerceServices.csv", "utf-8")
	}

	@Test
	def "Test Unsave Dao For Expired Saved Cart"() {

		given: "a session with a logged in customer"
		def customer = createCustomer(CUSTOMER_ID)
		login(CUSTOMER_ID)

		when: "we save two carts with  a valid name and description"
		CartData saveCartData1 = saveCartWithNameAndDescription(SAVED_CART_NAME_1,SAVED_CART_DESCRIPTION_1)
		removeAndCreateNewSessionCart()
		CartData saveCartData2 = saveCartWithNameAndDescription(SAVED_CART_NAME_2,SAVED_CART_DESCRIPTION_2)

		then: "the two saved carts have 30 day expiration terms, and no carts have expired"
		saveCartData1.expirationTime >= saveCartData1.saveTime + EXP_DAYS
		saveCartData2.expirationTime >= saveCartData2.saveTime + EXP_DAYS
		getSavedCartsToRemove().size() == 0

		when: "we change expiration dates of saved cart so they have expired"
		decreaseSavedCartsExpirationDate()

		then: "then two carts have expired"
		getSavedCartsToRemove().size() == 2
	}
}

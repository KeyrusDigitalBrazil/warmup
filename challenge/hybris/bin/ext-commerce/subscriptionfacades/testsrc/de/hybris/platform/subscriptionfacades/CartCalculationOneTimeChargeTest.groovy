/**
 *
 */
package de.hybris.platform.subscriptionfacades


import de.hybris.bootstrap.annotations.IntegrationTest

import org.junit.Test

/**
 * Port of old ATDD-framework tests to Spock framework
 */
@IntegrationTest
class CartCalculationOneTimeChargeTest extends AbstractSubscriptionFacadesSpockTest {

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testOneTimeCharge.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "cart calculation onetime charge on first bill one product"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_OneProductx1
		when:
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime charge on first bill one productx2"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_OneProductx2
		when:
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 20.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime charge on first bill two products two price plans"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_TwoProducts_TwoPricePlans
		when:
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P2")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 21.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime charge on first bill two products one price plans"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_TwoProducts_TwoPricePlans
		when:
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P2")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 21.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime charge oncancellaion one product"() {
		// Test_Cart_Calculation_OneTime_Charge_Oncancellation_OneProductx1
		when:
		addProductToCartOnce("P3")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime charge oncancellaion two products"() {
		// Test_Cart_Calculation_OneTime_Charge_Oncancellation_OneProductx1
		when:
		addProductToCartOnce("P3")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P4")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotal(0.00)

	}


	@Test
	def "cart calculation onetime charge paynow one product"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_OneProductx1
		when:
		addProductToCartOnce("P5")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("paynow", 10.00)
		verifyCartTotal(10.00)

	}

	@Test
	def "cart calculation onetime charge paynow one productx2"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_OneProductx2
		when:
		addProductToCartOnce("P5")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("paynow", 10.00)
		verifyCartTotal(10.00)

		when:
		addProductToCartOnce("P6")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("paynow", 21.00)
		verifyCartTotal(21.00)

	}

	@Test
	def "cart calculation onetime charge on first bill paynow one product one plan"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_Paynow_OneProduct_OnePlan
		when:
		addProductToCartOnce("P7")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("paynow", 5.00)
		verifyCartTotal(5.00)

	}

	@Test
	def "cart calculation onetime charge on oncancellation paynow one product one plan"() {
		// Test_Cart_Calculation_OneTime_Charge_Oncancellation_Paynow_OneProduct_OnePlan
		when:
		addProductToCartOnce("P8")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("paynow", 5.00)
		verifyCartTotal(5.00)

	}

	@Test
	def "cart calculation onetime charge on onfirstbill oncancellation paynow one product one plan"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_Oncancellation_Paynow_OneProduct_OnePlan
		when:
		addProductToCartOnce("P9")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotal(7.00)

	}

	@Test
	def "cart calculation onetime charge on onfirstbill oncancellation paynow multiple product multiple plan"() {
		// Test_Cart_Calculation_OneTime_Charge_Multi_Onfirstbill_Oncancellation_Paynow_MultipleProducts_MultiplePlans
		when:
		addProductToCartOnce("P10")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 1.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P11")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 3.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P12")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 5.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P13")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 5.00)
		verifyCartTotalForBillingEvent("paynow", 4.00)
		verifyCartTotal(4.00)

		when:
		addProductToCartOnce("P14")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 13.00)
		verifyCartTotalForBillingEvent("paynow", 10.00)
		verifyCartTotal(10.00)

		when:
		addProductToCartOnce("P15")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 20.00)
		verifyCartTotalForBillingEvent("paynow", 16.00)
		verifyCartTotal(16.00)

		when:
		addProductToCartOnce("P15")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 27.00)
		verifyCartTotalForBillingEvent("paynow", 22.00)
		verifyCartTotal(22.00)

	}

	@Test
	def "cart calculation onetime charge billingEvent1Test one product"() {
		// Test_Cart_Calculation_OneTime_Charge_BillingEvent1Test_OneProductx1
		when:
		addProductToCartOnce("P16")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("billingEvent1Test", 10.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime charge onfirstbill billingEvent1Test one product"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_BillingEvent1Test_OneProduct
		when:
		addProductToCartOnce("P17")
		addProductToCartOnce("P17")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 20.00)
		verifyCartTotalForBillingEvent("billingEvent1Test", 14.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime charge onfirstbill billingEvent1Test two products"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_BillingEvent1Test_TwoProducts
		when:
		addProductToCartOnce("P18")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P19")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("billingEvent1Test", 7.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime charge onfirstbill one product customer specific price plan"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_OneProduct_CustomerSpecificPricePlan
		given:
		login("vipCustomer")

		when:
		addProductToCartOnce("P20")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 5.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime charge onfirstbill one product curr specific price plan"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_OneProduct_CurrencySpecificPricePlan
		given:
		login("euroCustomer")

		when:
		addProductToCartOnce("P21")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 7.49)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime charge onfirstbill removing one product"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_Removing_OneProductx1

		when:
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P1")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotal(0.00)


		when: "we try to remove a product that is no longer in the cart"
		removeProductFromCart("P1")

		then: "nothing happens, no exception thrown"
		// nothing expected
	}

	@Test
	def "cart calculation onetime charge onfirstbill removing one productx2"() {
		// Test_Cart_Calculation_OneTime_Charge_Onfirstbill_Removing_OneProductx2

		when:
		addProductToCartOnce("P1")
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 20.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P1")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotal(0.00)


		when: "we try to remove a product that is no longer in the cart"
		removeProductFromCart("P1")

		then: "nothing happens, no exception thrown"
		// nothing expected
	}

	@Test
	def "cart calculation onetime charge oncancellation removing one product"() {
		// Test_Cart_Calculation_OneTime_Charge_Oncancellation_Removing_OneProductx1

		when:
		addProductToCartOnce("P3")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P3")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotal(0.00)


		when: "we try to remove a product that is no longer in the cart"
		removeProductFromCart("P3")

		then: "nothing happens, no exception thrown"
		// nothing expected
	}

	@Test
	def "cart calculation onetime charge paynow removing one product"() {
		// Test_Cart_Calculation_OneTime_Charge_Paynow_Removing_OneProductx1

		when:
		addProductToCartOnce("P5")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotal(10.00)

		when:
		removeProductFromCart("P5")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotal(0.00)


		when: "we try to remove a product that is no longer in the cart"
		removeProductFromCart("P5")

		then: "nothing happens, no exception thrown"
		// nothing expected
	}

	@Test
	def "cart calculation onetime charge onfirstbill removing complex"() {
		// Test_Cart_Calculation_OneTime_Charge_Removing_Complex

		when:
		addProductToCartOnce("P22")
		addProductToCartOnce("P23")
		addProductToCartOnce("P24")
		addProductToCartOnce("P25")
		addProductToCartOnce("P26")
		addProductToCartOnce("P27")
		addProductToCartOnce("P27")
		addProductToCartOnce("P28")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 27.00)
		verifyCartTotalForBillingEvent("paynow", 22.00)
		verifyCartTotal(22.00)

		when:
		removeProductFromCart("P22")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 26.00)
		verifyCartTotalForBillingEvent("paynow", 22.00)
		verifyCartTotal(22.00)

		when:
		removeProductFromCart("P27")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 19.00)
		verifyCartTotalForBillingEvent("paynow", 16.00)
		verifyCartTotal(16.00)

		when:
		removeProductFromCart("P24")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 17.00)
		verifyCartTotalForBillingEvent("paynow", 16.00)
		verifyCartTotal(16.00)

		when:
		removeProductFromCart("P23")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 15.00)
		verifyCartTotalForBillingEvent("paynow", 16.00)
		verifyCartTotal(16.00)

		when:
		removeProductFromCart("P25")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 15.00)
		verifyCartTotalForBillingEvent("paynow", 12.00)
		verifyCartTotal(12.00)

		when:
		removeProductFromCart("P26")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 7.00)
		verifyCartTotalForBillingEvent("paynow", 6.00)
		verifyCartTotal(6.00)

		when:
		removeProductFromCart("P27")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("onfirstbill", 0.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P28")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("onfirstbill", 0.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P22")
		removeProductFromCart("P23")
		removeProductFromCart("P24")
		removeProductFromCart("P25")
		removeProductFromCart("P26")
		removeProductFromCart("P27")
		removeProductFromCart("P28")

		then: "nothing happens, no exception thrown"
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("onfirstbill", 0.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotal(0.00)

	}
	@Test
	def "cart calculation onetime charge onfirstbill removing adding removing complex"() {
		// Test_Cart_Calculation_OneTime_Charge_RemovingAddingRemoving_Complex

		when:
		addProductToCartOnce("P22")
		addProductToCartOnce("P23")
		addProductToCartOnce("P24")
		addProductToCartOnce("P25")
		addProductToCartOnce("P26")
		addProductToCartOnce("P27")
		addProductToCartOnce("P27")
		addProductToCartOnce("P28")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 27.00)
		verifyCartTotalForBillingEvent("paynow", 22.00)
		verifyCartTotal(22.00)

		when:
		removeProductFromCart("P22")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 26.00)
		verifyCartTotalForBillingEvent("paynow", 22.00)
		verifyCartTotal(22.00)

		when:
		removeProductFromCart("P27")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 19.00)
		verifyCartTotalForBillingEvent("paynow", 16.00)
		verifyCartTotal(16.00)

		when:
		addProductToCartOnce("P22")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 20.00)
		verifyCartTotalForBillingEvent("paynow", 16.00)
		verifyCartTotal(16.00)

		when:
		removeProductFromCart("P22")
		removeProductFromCart("P24")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 17.00)
		verifyCartTotalForBillingEvent("paynow", 16.00)
		verifyCartTotal(16.00)

		when:
		addProductToCartOnce("P24")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 19.00)
		verifyCartTotalForBillingEvent("paynow", 16.00)
		verifyCartTotal(16.00)

		when:
		removeProductFromCart("P23")
		removeProductFromCart("P24")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 15.00)
		verifyCartTotalForBillingEvent("paynow", 16.00)
		verifyCartTotal(16.00)

		when:
		removeProductFromCart("P25")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 15.00)
		verifyCartTotalForBillingEvent("paynow", 12.00)
		verifyCartTotal(12.00)

		when:
		removeProductFromCart("P25")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 15.00)
		verifyCartTotalForBillingEvent("paynow", 12.00)
		verifyCartTotal(12.00)

		when:
		addProductToCartOnce("P28")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 15.00)
		verifyCartTotalForBillingEvent("paynow", 12.00)
		verifyCartTotal(12.00)

		when:
		removeProductFromCart("P26")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 7.00)
		verifyCartTotalForBillingEvent("paynow", 6.00)
		verifyCartTotal(6.00)

		when:
		addProductToCartOnce("P25")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 7.00)
		verifyCartTotalForBillingEvent("paynow", 10.00)
		verifyCartTotal(10.00)

		when:
		removeProductFromCart("P27")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("onfirstbill", 0.00)
		verifyCartTotalForBillingEvent("paynow", 4.00)
		verifyCartTotal(4.00)

		when:
		removeProductFromCart("P25")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("onfirstbill", 0.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P28")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("onfirstbill", 0.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P22")
		removeProductFromCart("P23")
		removeProductFromCart("P24")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("onfirstbill", 0.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P24")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 2.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P24")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("onfirstbill", 0.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P25")
		removeProductFromCart("P26")
		removeProductFromCart("P27")
		removeProductFromCart("P28")

		then: "nothing happens, no exception thrown"
		// nothing expected
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("onfirstbill", 0.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotal(0.00)

	}
}
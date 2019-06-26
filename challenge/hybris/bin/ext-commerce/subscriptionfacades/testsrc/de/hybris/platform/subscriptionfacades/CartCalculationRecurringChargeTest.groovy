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
class CartCalculationRecurringChargeTest extends AbstractSubscriptionFacadesSpockTest {

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testRecurringCharge.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "cart calculation recurring charge cycle from 0 one product"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom0To_OneProduct
		when:
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.00)
		getDiscountPrice("monthly", 0.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 one product"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To_OneProduct
		when:
		addProductToCartOnce("P2")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 0.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 1 from 2 one product"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To1_From2To_OneProduct
		when:
		addProductToCartOnce("P3")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 1 from 2 to 2 from 3 to 3 one product"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To1_From2To2_From3To4_OneProduct
		when:
		addProductToCartOnce("P4")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 30.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 1 from 2 to 2 from 3 to 4 one product"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To1_From2To2_From3To4_OneProduct
		when:
		addProductToCartOnce("P5")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 30.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 3 from 4 to 6  one product"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To3_From4To6_OneProduct
		when:
		addProductToCartOnce("P6")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 20.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 3 from 4 to 4  one product"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To3_From4To4_OneProduct
		when:
		addProductToCartOnce("P7")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 20.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 3 from 4 to 5  one product"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To3_From4To5_OneProduct
		when:
		addProductToCartOnce("P8")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 30.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 two products"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To_TwoProducts
		when:
		addProductToCartOnce("P9")
		addProductToCartOnce("P10")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 40.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 1 from 2 two products"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To1_From2To_TwoProducts
		when:
		addProductToCartOnce("P11")
		addProductToCartOnce("P12")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 42.98)
		getDiscountPrice("monthly", 21.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 1 from 2 to 2 from 3 to 3 two products"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To1_From2To2_From3To3_TwoProducts
		when:
		addProductToCartOnce("P13")
		addProductToCartOnce("P14")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 42.98)
		getDiscountPrice("monthly", 62.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 1 from 2 to 2 from 3 to 4 two products"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To1_From2To2_From3To4_TwoProducts
		when:
		addProductToCartOnce("P15")
		addProductToCartOnce("P16")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 42.98)
		getDiscountPrice("monthly", 62.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 3 from 4 to 6 two products"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To3_From4To6_TwoProducts
		when:
		addProductToCartOnce("P17")
		addProductToCartOnce("P18")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 42.98)
		getDiscountPrice("monthly", 41.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 3 from 4 to 4 two products"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To3_From4To4_TwoProducts
		when:
		addProductToCartOnce("P19")
		addProductToCartOnce("P20")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 42.98)
		getDiscountPrice("monthly", 41.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 3 from 4 to 4 from 5 two products"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To3_From4To4_From5To_TwoProducts
		when:
		addProductToCartOnce("P21")
		addProductToCartOnce("P22")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 42.98)
		getDiscountPrice("monthly", 62.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge cycle from 1 to 1 from 2 to 4 one productx2"() {
		// Test_Cart_Calculation_Recurring_Charge_CycleFrom1To1_From2To_OneProductx2
		when:
		addProductToCartOnce("P23")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(0.00)

		addProductToCartOnce("P23")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 41.98)
		getDiscountPrice("monthly", 20.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge different cycle ranges three products"() {
		// Test_Cart_Calculation_Recurring_Charge_DifferentCycleRanges_ThreeProducts
		when:
		addProductToCartOnce("P24")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 30.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P25")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 42.98)
		getDiscountPrice("monthly", 51.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P26")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 68.97)
		getDiscountPrice("monthly", 83.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P24")
		addProductToCartOnce("P25")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 111.95)
		getDiscountPrice("monthly", 134.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P24")
		addProductToCartOnce("P26")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 158.93)
		getDiscountPrice("monthly", 196.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge removing one product"() {
		// Test_Cart_Calculation_Recurring_Charge_Removing_OneProductx1
		when:
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.00)
		getDiscountPrice("monthly", 0.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P1")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("monthly", 0.00)
		verifyCartTotal(0.00)

		when: "we remove a product that is no longer in the cart"
		removeProductFromCart("P1")

		then: "nothing happens, no excepotion to handle"
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("monthly", 0.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge removing one productx2"() {
		// Test_Cart_Calculation_Recurring_Charge_Removing_OneProductx2
		when:
		addProductToCartOnce("P1")
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 40.00)
		getDiscountPrice("monthly", 0.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P1")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("monthly", 0.00)
		verifyCartTotal(0.00)

		when: "we remove a product that is no longer in the cart"
		removeProductFromCart("P1")

		then: "nothing happens, no excepotion to handle"
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("monthly", 0.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge removing adding removing one productx1"() {
		// Test_Cart_Calculation_Recurring_Charge_RemovingAddingRemoving_OneProductx1
		when:
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.00)
		getDiscountPrice("monthly", 0.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P1")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("monthly", 0.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.00)
		getDiscountPrice("monthly", 0.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P1")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("monthly", 0.00)
		verifyCartTotal(0.00)

		when: "we remove a product that is no longer in the cart"
		removeProductFromCart("P1")

		then: "nothing happens, no excepotion to handle"
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("monthly", 0.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation recurring charge removing complex"() {
		// Test_Cart_Calculation_Recurring_Charge_Removing_Complex
		when:
		addProductToCartOnce("P27")
		addProductToCartOnce("P27")
		addProductToCartOnce("P27")
		addProductToCartOnce("P28")
		addProductToCartOnce("P28")
		addProductToCartOnce("P29")
		addProductToCartOnce("P29")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 158.93)
		getDiscountPrice("monthly", 196.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P28")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 136.94)
		getDiscountPrice("monthly", 175.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P29")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 110.95)
		getDiscountPrice("monthly", 143.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P28")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 88.96)
		getDiscountPrice("monthly", 122.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P27")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 67.97)
		getDiscountPrice("monthly", 92.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P29")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 41.98)
		getDiscountPrice("monthly", 60.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P27")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 30.00)
		verifyCartTotal(0.00)

		when:
		removeProductFromCart("P27")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("monthly", 0.00)
		verifyCartTotal(0.00)

		when:"we remove a product that is no longer in the cart"
		removeProductFromCart("P27")
		removeProductFromCart("P28")
		removeProductFromCart("P29")

		then:"nothing happens, no excepotion to handle"
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("monthly", 0.00)
		verifyCartTotal(0.00)
	}

}
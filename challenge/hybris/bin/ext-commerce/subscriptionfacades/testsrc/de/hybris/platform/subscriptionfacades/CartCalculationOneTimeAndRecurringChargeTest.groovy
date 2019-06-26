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
class CartCalculationOneTimeAndRecurringChargeTest extends AbstractSubscriptionFacadesSpockTest {

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testOneTimeRecurringCharge.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "cart calculation onetime and recurring charge on first bill cycle from 1 one product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To_OneProduct
		when:
		addProductToCartOnce("P1")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 0.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime and recurring charge on first bill cycle from 1 to 1 from 2 one product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To1_From2To_OneProduct
		when:
		addProductToCartOnce("P2")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime and recurring charge on first bill cycle from 1 to 3 from 4 to 4 and from 5 one product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To3_From4To4_From5To_OneProduct
		when:
		addProductToCartOnce("P3")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 30.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime and recurring charge on first bill cycle from 1 to 1 from 2 one productx2"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To1_From2To_OneProductx2
		when:
		addProductToCartOnce("P2")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P2")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 20.00)
		verifyCartTotalForBillingEvent("monthly", 41.98)
		getDiscountPrice("monthly", 20.00)
		verifyCartTotal(0.00)

	}

	@Test
	def "cart calculation onetime and recurring charge on first bill cycle from 1 to 1 from 2 one productx3"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To1_From2To_OneProductx3
		when:
		addProductToCartOnce("P4")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P2")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 20.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P2")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 30.00)
		verifyCartTotalForBillingEvent("monthly", 41.98)
		getDiscountPrice("monthly", 20.00)
		verifyCartTotal(0.00)


	}

	@Test
	def "cart calculation onetime and recurring charge on first bill cycle from 1 to 1 from 2 two product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To1_From2To_TwoProducts
		when:
		addProductToCartOnce("P2")
		addProductToCartOnce("P5")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 25.00)
		verifyCartTotalForBillingEvent("monthly", 42.98)
		getDiscountPrice("monthly", 21.00)
		verifyCartTotal(0.00)
	}

	@Test
	def "cart calculation onetime and recurring charge on cancellatin cycle from 1 one product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To_OneProduct
		when:
		addProductToCartOnce("P6")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 0.00)
		verifyCartTotal(0.00)
	}

	@Test
	def "cart calculation onetime and recurring charge on cancellatin cycle from 1 to 1 from 2 one product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To1_From2To_OneProduct
		when:
		addProductToCartOnce("P7")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(0.00)
	}

	@Test
	def "cart calculation onetime and recurring charge on cancellatin cycle from 1 to 1 from 2 one productx2"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To1_From2To_OneProductx2
		when:
		addProductToCartOnce("P7")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P7")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 41.98)
		getDiscountPrice("monthly", 20.00)
		verifyCartTotal(0.00)
	}

	@Test
	def "cart calculation onetime and recurring charge on cancellatin cycle from 1 to 1 from 2 one productx3 "() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To1_From2To_OneProductx3
		when:
		addProductToCartOnce("P8")

		then:
		verifyNumberOfChildCarts(0)

		when:
		addProductToCartOnce("P7")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P7")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 41.98)
		getDiscountPrice("monthly", 20.00)
		verifyCartTotal(0.00)
	}

	@Test
	def "cart calculation onetime and recurring charge on cancellation cycle from 1 to 1 from 2 two product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To1_From2To_TwoProducts
		when:
		addProductToCartOnce("P7")
		addProductToCartOnce("P9")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("monthly", 42.98)
		getDiscountPrice("monthly", 21.00)
		verifyCartTotal(0.00)
	}

	@Test
	def "cart calculation onetime and recurring charge pay now cycle from 1 one product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To_OneProduct
		when:
		addProductToCartOnce("P10")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("paynow", 10.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 0.00)
		verifyCartTotal(10.00)

	}

	@Test
	def "cart calculation onetime and recurring charge pay now cycle from 1 to 1 from 2 one product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To1_From2To_OneProduct
		when:
		addProductToCartOnce("P11")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("paynow", 10.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(10.00)

	}

	@Test
	def "cart calculation onetime and recurring charge pay now cycle from 1 to 3 from 4 to 4 from 5 one product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To3_From4To4_From5To_OneProduct
		when:
		addProductToCartOnce("P12")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("paynow", 10.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 30.00)
		verifyCartTotal(10.00)

	}

	@Test
	def "cart calculation onetime and recurring charge paynow cycle from 1 to 1 from 2 one productx2"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To1_From2To_OneProductx2
		when:
		addProductToCartOnce("P11")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("paynow", 10.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(10.00)

		when:
		addProductToCartOnce("P11")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("paynow", 20.00)
		verifyCartTotalForBillingEvent("monthly", 41.98)
		getDiscountPrice("monthly", 20.00)
		verifyCartTotal(20.00)

	}

	@Test
	def "cart calculation onetime and recurring charge pay now cycle from 1 to 1 from 2 one productx3"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To1_From2To_OneProductx3
		when:
		addProductToCartOnce("P13")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("paynow", 10.00)
		verifyCartTotal(10.00)

		when:
		addProductToCartOnce("P11")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("paynow", 20.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(20.00)

		when:
		addProductToCartOnce("P11")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("paynow", 30.00)
		verifyCartTotalForBillingEvent("monthly", 41.98)
		getDiscountPrice("monthly", 20.00)
		verifyCartTotal(30.00)


	}

	@Test
	def "cart calculation onetime and recurring charge paynow cycle from 1 to 1 from 2 two product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To1_From2To_TwoProducts
		when:
		addProductToCartOnce("P11")
		addProductToCartOnce("P14")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("paynow", 25.00)
		verifyCartTotalForBillingEvent("monthly", 42.98)
		getDiscountPrice("monthly", 21.00)
		verifyCartTotal(25.00)
	}

	@Test
	def "cart calculation onetime and recurring charge onfirstbill oncancellation paynow cycle from 1 to 1 from 2 one product one plan"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_Oncancellation_Paynow_CycleFrom1To1_From2To_OneProduct_OnePlan
		when:
		addProductToCartOnce("P15")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(7.00)
	}

	@Test
	def "cart calculation onetime and recurring charge onfirstbill oncancellation paynow cycle from 1 to 1 from 2 multiple products multiple plans"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Multi_Onfirstbill_Oncancellation_Paynow_CycleFrom1To1_From2To_MultipleProducts_MultiplePlans
		when:
		addProductToCartOnce("P16")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 1.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P17")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 3.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotalForBillingEvent("monthly", 42.98)
		getDiscountPrice("monthly", 21.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P18")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 5.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotalForBillingEvent("monthly", 66.97)
		getDiscountPrice("monthly", 32.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P19")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 5.00)
		verifyCartTotalForBillingEvent("paynow", 4.00)
		verifyCartTotalForBillingEvent("monthly", 92.96)
		getDiscountPrice("monthly", 43.00)
		verifyCartTotal(4.00)

		when:
		addProductToCartOnce("P20")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 13.00)
		verifyCartTotalForBillingEvent("paynow", 10.00)
		verifyCartTotalForBillingEvent("monthly", 120.95)
		getDiscountPrice("monthly", 54.00)
		verifyCartTotal(10.00)

		when:
		addProductToCartOnce("P21")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 20.00)
		verifyCartTotalForBillingEvent("paynow", 16.00)
		verifyCartTotalForBillingEvent("monthly", 150.94)
		getDiscountPrice("monthly", 74.00)
		verifyCartTotal(16.00)

		when:
		addProductToCartOnce("P21")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 27.00)
		verifyCartTotalForBillingEvent("paynow", 22.00)
		verifyCartTotalForBillingEvent("monthly", 180.93)
		getDiscountPrice("monthly", 94.00)
		verifyCartTotal(22.00)


	}

	@Test
	def "cart calculation onetime and recurring charge billingeventteat1 cycle from 1 to 1 from 2 one product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_BillingEvent1Test_CycleFrom1To1_From2To_OneProduct
		when:
		addProductToCartOnce("P22")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("billingEvent1Test", 10.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotalForBillingEvent("monthly", 21.99)
		getDiscountPrice("monthly", 11.00)
		verifyCartTotal(0.00)
	}

	@Test
	def "cart calculation onetime and recurring charge billingeventteat1 cycle from 1 to 1 from 2 two product"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_BillingEvent1Test_CycleFrom1To1_From2To_TwoProduct
		when:
		addProductToCartOnce("P2")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)
		getDiscountPrice("monthly", 10.00)
		verifyCartTotal(0.00)

		when:
		addProductToCartOnce("P22")

		then:
		verifyNumberOfChildCarts(3)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("billingEvent1Test", 10.00)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotalForBillingEvent("monthly", 42.98)
		getDiscountPrice("monthly", 21.00)
		verifyCartTotal(0.00)
	}

	@Test
	def "cart calculation onetime and recurring charge removing complex"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_Removing_Complex
		when:
		addProductToCartOnce("P23")
		addProductToCartOnce("P24")
		addProductToCartOnce("P25")
		addProductToCartOnce("P26")

		then:
		verifyNumberOfChildCarts(3)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("billingEvent1Test", 17.00)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotalForBillingEvent("monthly", 89.96)
		getDiscountPrice("monthly", 46.00)
		verifyCartTotal(7.00)

		when:
		removeProductFromCart("P24")

		then:
		verifyNumberOfChildCarts(3)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("billingEvent1Test", 17.00)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotalForBillingEvent("monthly", 67.97)
		getDiscountPrice("monthly", 35.00)
		verifyCartTotal(7.00)

		when:
		removeProductFromCart("P23")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("billingEvent1Test", 17.00)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotalForBillingEvent("monthly", 46.98)
		getDiscountPrice("monthly", 25.00)
		verifyCartTotal(7.00)

		when:
		removeProductFromCart("P26")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotalForBillingEvent("monthly", 22.99)
		getDiscountPrice("monthly", 12.00)
		verifyCartTotal(7.00)

		when:
		removeProductFromCart("P25")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotal(0.00)

		when: "we try to remove a product that is no longer in the cart"
		removeProductFromCart("P23")

		then: "exception is expected"
		//		Exception e1 = thrown()

		when: "we try to remove oproiduct that is no longer in the cart"
		removeProductFromCart("P24")

		then: "exception is expected"
		//		Exception e2 = thrown()

		when: "we try to remove oproiduct that is no longer in the cart"
		removeProductFromCart("P25")

		then: "exception is expected"
		//		Exception e3 = thrown()

		when: "we try to remove oproiduct that is no longer in the cart"
		removeProductFromCart("P26")

		then: "exception is expected"
		//		Exception e4 = thrown()

	}

	@Test
	def "cart calculation onetime and recurring charge removing adding removing complex"() {
		// Test_Cart_Calculation_OneTime_And_Recurring_Charge_RemovingAddingRemoving_Complex
		when:
		addProductToCartOnce("P23")
		addProductToCartOnce("P24")
		addProductToCartOnce("P25")
		addProductToCartOnce("P26")

		then:
		verifyNumberOfChildCarts(3)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("billingEvent1Test", 17.00)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotalForBillingEvent("monthly", 89.96)
		getDiscountPrice("monthly", 46.00)
		verifyCartTotal(7.00)

		when:
		removeProductFromCart("P24")

		then:
		verifyNumberOfChildCarts(3)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("billingEvent1Test", 17.00)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotalForBillingEvent("monthly", 67.97)
		getDiscountPrice("monthly", 35.00)
		verifyCartTotal(7.00)

		when:
		addProductToCartOnce("P24")

		then:
		verifyNumberOfChildCarts(3)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("billingEvent1Test", 17.00)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotalForBillingEvent("monthly", 89.96)
		getDiscountPrice("monthly", 46.00)
		verifyCartTotal(7.00)

		when:
		removeProductFromCart("P23")
		removeProductFromCart("P24")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("billingEvent1Test", 17.00)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotalForBillingEvent("monthly", 46.98)
		getDiscountPrice("monthly", 25.00)
		verifyCartTotal(7.00)

		when:
		removeProductFromCart("P24")

		then: "exception is expected"
		//		Exception e1 = thrown()

		when: "we try to remove a product that is no longer in the cart"
		removeProductFromCart("P26")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotalForBillingEvent("monthly", 22.99)
		getDiscountPrice("monthly", 12.00)
		verifyCartTotal(7.00)

		when: "we try to remove a product that is no longer in the cart"
		removeProductFromCart("P24")

		then: "exception is expected"
		//		Exception e2 = thrown()

		when:
		addProductToCartOnce("P26")

		then:
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("billingEvent1Test", 17.00)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotalForBillingEvent("monthly", 46.98)
		getDiscountPrice("monthly", 25.00)
		verifyCartTotal(7.00)

		when:
		removeProductFromCart("P26")
		addProductToCartOnce("P25")
		removeProductFromCart("P25")

		then:
		verifyNumberOfChildCarts(1)
		verifyCartTotalForBillingEvent("paynow", 7.00)
		verifyCartTotalForBillingEvent("monthly", 22.99)
		getDiscountPrice("monthly", 12.00)
		verifyCartTotal(7.00)

		when:
		removeProductFromCart("P25")

		then:
		verifyNumberOfChildCarts(0)
		verifyCartTotalForBillingEvent("paynow", 0.00)
		verifyCartTotal(0.00)

		when: "we try to remove a product that is no longer in the cart"
		removeProductFromCart("P23")

		then: "exception is expected"
		//		Exception e3 = thrown()

		when: "we try to remove a product that is no longer in the cart"
		removeProductFromCart("P24")

		then: "exception is expected"
		//		Exception e4 = thrown()

		when: "we try to remove a product that is no longer in the cart"
		removeProductFromCart("P25")

		then: "exception is expected"
		//		Exception e5 = thrown()

		when: "we try to remove a product that is no longer in the cart"
		removeProductFromCart("P26")

		then: "exception is expected"
		//		Exception e6 = thrown()

	}

}
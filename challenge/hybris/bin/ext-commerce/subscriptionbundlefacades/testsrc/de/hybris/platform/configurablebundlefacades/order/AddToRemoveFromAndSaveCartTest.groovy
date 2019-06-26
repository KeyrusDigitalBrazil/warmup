/**
 *
 */
package de.hybris.platform.configurablebundlefacades.order


import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.configurablebundlefacades.AbstractSubscriptionBundleFacadesSpockTest
import de.hybris.platform.order.CartService

import javax.annotation.Resource

import org.junit.Test

/**
 * Port of old ATDD-framework tests to Spock framework
 */
@IntegrationTest
class AddToRemoveFromAndSaveCartTest extends AbstractSubscriptionBundleFacadesSpockTest {

	// test data values
	def CUSTOMER_ID = "testuser1@saved-carts.com"

	@Resource
	private BundleCartFacade bundleCartFacade;

	@Resource
	private CartService cartService;

	// is run before every test
	def setup() {
		importCsv("/subscriptionbundlefacades/test/testBundleCartFacade.csv", "utf-8")
		prepareSession("testSite")
	}

	// Save Cart Tests

	@Test
	def "Save a cart that has a product bundle"() {

		given: "a session with a logged in user"
		def sessionCart = getCurrentSessionCart()
		createCustomer(CUSTOMER_ID);
		login(CUSTOMER_ID)

		when: "we add a product for a bundle component to the cart"
		bundleCartFacade.startBundle("ProductComponent1", "PRODUCT01", 1)

		then: "the cart total is as per sample data"
		verifyCartTotal(600.00)

		when: "we save the cart"
		def savedCart = saveCartWithNameAndDescription("TestCart", "Test")

		then: "the saved cart has the same code as the session cart"
		savedCart.code == sessionCart.code

		when: "we clone the cart"
		def clonedCart = cloneSavedCart(savedCart.code)

		then: "the clone does not have the same code as the original saved cart, is identical to the original saved cart, and last modified entries are empty"
		clonedCart.code != savedCart.code
		verifyCartClone(savedCart.code, clonedCart.code)
		verifyLastModifiedEntriesIsEmpty(clonedCart.code)

	}


	// Add to cart tests

	@Test
	def "Add a single product to a new bundle"() {
		// Test_Add_To_Cart_SingleBundleNew_SingleProduct

		given:
		verifyCartTotal(0.0)

		when: "we add a product for a bundle component to the cart"
		bundleCartFacade.startBundle("ProductComponent1", "PRODUCT01", 1)

		then: "number of child carts and cart totals are correct "
		verifyCartTotal(600.00)
		verifyCartTotalForBillingEvent("paynow", 600.00)
		verifyNumberOfChildCarts(0)

	}

	@Test
	def "Add a single product to a existing bundle"() {
		// Test_Add_To_Cart_SingleBundleExisting_SingleProduct

		given:
		verifyCartTotal(0.0)

		when: "we add a product for a bundle component to the cart"
		addProductToNewBundle("PRODUCT01", "ProductComponent1")
		addProductToExistingBundle("PREMIUM01", "PremiumComponent2", 1)

		then: "cart total is correct"
		verifyCartTotal(1310)
	}


	@Test
	def "Add two products to a new bundle"() {
		// Test_Add_To_Cart_SingleBundleNew_TwoProducts: (for example one as Device and other as service Plan)

		given:
		verifyCartTotal(0.0)

		when: "we add a product for a bundle component to the cart"
		addTwoProductsToNewBundle("PRODUCT01", "ProductComponent1", "PREMIUM01", "PremiumComponent2")

		then: "cart total is correct"
		verifyCartTotal(1310)
	}

	@Test
	def "Add two products to an existing bundle"() {
		// Test_Add_To_Cart_SingleBundleExisting_TwoProducts: (for example one as Device and other as service Plan)

		given:
		verifyCartTotal(0.0)

		when: "we add a product for a bundle component to the cart"
		addProductToNewBundle("PRODUCT01", "ProductComponent1")
		addTwoProductsToNewBundle("PRODUCT02", "ProductComponent1", "PREMIUM01", "PremiumComponent2")

		then: "cart total is correct"
		verifyCartTotal(1960.00)
	}

	@Test
	def "Add two products to the bundle with billing event"() {
		// Test_BundleRule_With_BillingEvent: (SUBSCRIPTIONPRODUCT01 with pay now price 100 and one time price 10, SUBSCRIPTIONPREMIUM01 with pay now price 0, one time charge 0 and price rule to reduce SUBSCRIPTIONPRODUCT01 one time charge to 7)

		given:
		verifyCartTotal(0.0)

		when: "we add a subscription product for a bundle component to the cart"
		addProductToNewBundle("SUBSCRIPTIONPRODUCT01", "ProductComponent1")

		then: "cart total and billing event is correct"
		verifyCartTotal(100.00)
		verifyCartTotalForBillingEvent("onetimeprice", 10)

		when: "we add another subscription product to the cart"
		addProductToExistingBundle("SUBSCRIPTIONPREMIUM01", "PremiumComponent2", 1)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(100.00)
		verifyCartTotalForBillingEvent("onetimeprice", 7)
		getDiscountPrice("onetimeprice", 3)

	}

	@Test
	def "Add two products again to the bundle with billing event"() {
		// Test_BundleRule_With_BillingEvent: (SUBSCRIPTIONPRODUCT01 with pay now price 100 and one time price 10, SUBSCRIPTIONPREMIUM03 with pay now price 210, one time charge 0 and price rule to reduce SUBSCRIPTIONPRODUCT01 one time charge to 4)

		given:
		verifyCartTotal(0.0)

		when: "we add a subscription product for a bundle component to the cart"
		addProductToNewBundle("SUBSCRIPTIONPRODUCT01", "ProductComponent1")

		then: "cart total and billing event is correct"
		verifyCartTotal(100.00)
		verifyCartTotalForBillingEvent("onetimeprice", 10)

		when: "we add another subscription product to the cart"
		addProductToExistingBundle("SUBSCRIPTIONPREMIUM03", "PremiumComponent2", 1)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(310)
		verifyCartTotalForBillingEvent("onetimeprice", 4)
		getDiscountPrice("onetimeprice", 6)

	}

	@Test
	def "Apply a price rule to one product"() {
		// Test_BundleRule_With_BillingEvent_TwoProducts_For_Apply_PriceRule_To_One_Product: Adding two products to the bundle with billing event. (SUBSCRIPTIONPRODUCT01 with pay now price 100 and one time price 10, SUBSCRIPTIONPRODUCT02 with pay now price 150 and one time price 15, SUBSCRIPTIONPREMIUM01 with pay now price 0, one time charge 0 and price rule to reduce SUBSCRIPTIONPRODUCT01 one time charge to 7)

		given:
		verifyCartTotal(0.0)

		when: "we add a subscription product for a bundle component to the cart"
		addProductToNewBundle("SUBSCRIPTIONPRODUCT01", "ProductComponent1")

		then: "cart total and billing event is correct"
		verifyCartTotal(100)
		verifyCartTotalForBillingEvent("onetimeprice", 10)

		when: "we add another subscription product to the cart"
		addProductToExistingBundle("SUBSCRIPTIONPRODUCT02", "ProductComponent1", 1)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(250)
		verifyCartTotalForBillingEvent("onetimeprice", 25)

		when: "we add another subscription product to the cart"
		addProductToExistingBundle("SUBSCRIPTIONPREMIUM01", "PremiumComponent2", 1)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(250)
		verifyCartTotalForBillingEvent("onetimeprice", 22)
		getDiscountPrice("onetimeprice", 3)

	}

	@Test
	def "Apply a price rule to two products"() {
		// Test_BundleRule_With_BillingEvent_TwoProducts_For_Apply_PriceRule_To_Two_Product: Adding two products to the bundle with billing event. (SUBSCRIPTIONPRODUCT01 with pay now price 100 and one time price 10, SUBSCRIPTIONPRODUCT02 with pay now price 150 and one time price 15, SUBSCRIPTIONPREMIUM02 with pay now price 0, one time charge 0 and price rule to reduce SUBSCRIPTIONPRODUCT01 and SUBSCRIPTIONPRODUCT02 one time charge to 9)

		given:
		verifyCartTotal(0.0)

		when: "we add a subscription product for a bundle component to the cart"
		addProductToNewBundle("SUBSCRIPTIONPRODUCT01", "ProductComponent1")

		then: "cart total and billing event is correct"
		verifyCartTotal(100)
		verifyCartTotalForBillingEvent("onetimeprice", 10)

		when: "we add another subscription product to the cart"
		addProductToNewBundle("SUBSCRIPTIONPRODUCT02", "ProductComponent1")

		then:  "cart total and billing event is still correct"
		verifyCartTotal(250)
		verifyCartTotalForBillingEvent("onetimeprice", 25)

		when: "we add another subscription product to the cart"
		addProductToExistingBundle("SUBSCRIPTIONPREMIUM02", "PremiumComponent2", 1)
		addProductToExistingBundle("SUBSCRIPTIONPREMIUM02", "PremiumComponent2", 2)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(250)
		verifyCartTotalForBillingEvent("onetimeprice", 18)
		getDiscountPrice("onetimeprice", 7)

	}

	@Test
	def "Recurring rule win without billingEvent"() {
		// Test_BundleRule_For_Recurring_Rule_Win_Without_BillingEvent: Adding two products to the bundle without billing event.

		given:
		verifyCartTotal(0.0)

		when: "we add a subscription product for a bundle component to the cart"
		addProductToNewBundle("SUBSCRIPTIONPRODUCT03", "ProductComponent1")

		then: "cart total and billing event is correct"
		verifyCartTotal(170)
		verifyCartTotalForBillingEvent("monthly", 17)

		when: "we add another subscription product to the cart"
		addProductToExistingBundle("SUBSCRIPTIONPREMIUM04", "PremiumComponent2", 1)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(170)
		verifyCartTotalForBillingEvent("monthly", 7)

		getDiscountPrice("monthly", 10)
		getDiscountPrice("paynow", 0)

	}

	@Test
	def "Recurring plan win without billingEvent"() {
		// Test_BundleRule_For_Recurring_Plan_Win_Without_BillingEvent: Adding two products to the bundle without billing event.

		given:
		verifyCartTotal(0.0)

		when: "we add a subscription product for a bundle component to the cart"
		addProductToNewBundle("SUBSCRIPTIONPRODUCT03", "ProductComponent1")

		then: "cart total and billing event is correct"
		verifyCartTotal(170)
		verifyCartTotalForBillingEvent("monthly", 17)

		when: "we add another subscription product to the cart"
		addProductToExistingBundle("SUBSCRIPTIONPREMIUM05", "PremiumComponent2", 1)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(360)
		verifyCartTotalForBillingEvent("monthly", 17)

		getDiscountPrice("monthly", 0)
		getDiscountPrice("paynow", 0)

	}

	@Test
	def "Mixed bundle rule for one time price and recurring"() {
		// Test_Mixed_BundleRule_For_OneTimePrice_And_Recurring

		given:
		verifyCartTotal(0.0)

		when: "we add a subscription product for a bundle component to the cart"
		addProductToNewBundle("SUBSCRIPTIONPRODUCT01", "ProductComponent1")

		then: "cart total and billing event is correct"
		verifyCartTotal(100)
		verifyCartTotalForBillingEvent("onetimeprice", 10)

		when: "we add another subscription product to the cart"
		addProductToExistingBundle("SUBSCRIPTIONPREMIUM03", "PremiumComponent2", 1)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(310)
		verifyCartTotalForBillingEvent("onetimeprice", 4)
		getDiscountPrice("onetimeprice",6)

		when: "we add another subscription product to the cart"
		addProductToNewBundle("SUBSCRIPTIONPRODUCT03", "ProductComponent1")

		then:  "cart total and billing event is still correct"
		verifyCartTotal(480)
		verifyCartTotalForBillingEvent("monthly", 17)

		when: "we add another subscription product to the cart"
		addProductToExistingBundle("SUBSCRIPTIONPREMIUM04", "PremiumComponent2", 2)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(480)
		verifyCartTotalForBillingEvent("monthly", 7)

		getDiscountPrice("paynow", 0)
		getDiscountPrice("monthly", 10)


	}

	@Test
	def "Add two products to a new bundle which has empty billing event."() {
		// Test_Add_To_Cart_SingleBundleExisting_TwoProducts_With_Rule_But_Empty_BillingEvent: Adding two products (for example one as Device and other as service Plan) to a new bundle, which is empty billing event.

		when: "we add a product for a bundle component to the cart"
		addProductToNewBundle("PRODUCT06", "ProductComponent1")

		then: "cart total and billing event is correct"
		verifyCartTotal(600)

		when: "we add another  product to the cart"
		addProductToExistingBundle("PRODUCT05", "ProductComponent1", 1)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(1250)

		when: "we add another  product to the cart"
		addProductToExistingBundle("PREMIUM06", "PremiumComponent2", 1)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(1510)
		getDiscountPrice("paynow", 500)

	}

	@Test
	def "Add one products to a new bundle which has empty billing event."() {
		// Test_Add_To_Cart_SingleBundleExisting_OneProduct_With_Rule_Empty_BillingEvent: Adding one product (for example one as Device and other as service Plan) to a new bundle, which is empty billing event.

		when: "we add a product for a bundle component to the cart"
		addProductToNewBundle("PRODUCT06", "ProductComponent1")

		then: "cart total and billing event is correct"
		verifyCartTotal(600)

		when: "we add another  product to the cart"
		addProductToExistingBundle("PREMIUM06", "PremiumComponent2", 1)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(860)
		getDiscountPrice("paynow", 500)

	}

	@Test
	def "Plan with billing event misconfiguration plan no one time price"() {
		// Test_BundleRule_With_BillingEvent_Misconfiguration_Plan_No_OneTimePrice: Adding two products to the bundle with billing event.

		given:
		verifyCartTotal(0.0)

		when: "we add a product for a bundle component to the cart"
		addProductToNewBundle("SUBSCRIPTIONPRODUCT04", "ProductComponent1")

		then: "cart total and billing event is correct"
		verifyCartTotal(125)

		when: "we add another  product to the cart"
		addProductToExistingBundle("SUBSCRIPTIONPREMIUM06", "PremiumComponent2", 1)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(125)
		verifyNumberOfChildCarts(0)

	}

	@Test
	def "Rule with billing event misconfiguration plan no one time price"() {
		// Test_BundleRule_With_BillingEvent_Misconfiguration_Rule_No_OneTimePrice: Adding two products to the bundle with billing event.

		given:
		verifyCartTotal(0.0)

		when: "we add a product for a bundle component to the cart"
		addProductToNewBundle("SUBSCRIPTIONPRODUCT01", "ProductComponent1")

		then: "cart total and billing event is correct"
		verifyCartTotal(100)
		verifyCartTotalForBillingEvent("onetimeprice", 10)


		when: "we add another  product to the cart"
		addProductToExistingBundle("SUBSCRIPTIONPREMIUM07", "PremiumComponent2", 1)

		then:  "cart total and billing event is still correct"
		verifyCartTotal(100)
		verifyCartTotalForBillingEvent("onetimeprice", 10)
		verifyNumberOfChildCarts(1)

	}

	// Remove from Cart Tests

	@Test
	def "Delete all the entries from the given bundle"() {
		// Test_Remove_From_Cart_SingleBundle_SingleProduct

		when: "we add a product for a bundle component to the cart"
		addProductToNewBundle("PRODUCT01", "ProductComponent1")

		then: "cart total and billing event is correct"
		verifyCartTotal(600)

		when: "we remove product from the cart"
		deleteCartBundle(1)

		then:  "cart total is zero"
		verifyCartTotal(0)

	}

}
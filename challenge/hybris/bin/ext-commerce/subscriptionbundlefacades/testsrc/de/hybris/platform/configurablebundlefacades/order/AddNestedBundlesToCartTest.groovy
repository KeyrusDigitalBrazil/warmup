/**
 *
 */
package de.hybris.platform.configurablebundlefacades.order


import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commerceservices.order.CommerceCartModificationException
import de.hybris.platform.configurablebundlefacades.AbstractSubscriptionBundleFacadesSpockTest
import de.hybris.platform.order.CartService
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException

import javax.annotation.Resource

import org.junit.Test

/**
 * Port of old ATDD-framework tests to Spock framework
 */
@IntegrationTest
class AddNestedBundlesToCartTest extends AbstractSubscriptionBundleFacadesSpockTest {

	// test data values
	def CUSTOMER_ID = "testuser1@saved-carts.com"

	@Resource
	private BundleCartFacade bundleCartFacade;

	@Resource
	private CartService cartService;

	// is run before every test
	def setup() {
		importCsv("/configurablebundleservices/test/nestedBundleTemplates.impex", "utf-8")
		prepareSession("testSite")
	}


	// Add nested bundle to cart tests

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
	def "Standalone product can not be added with a bundle"() {
		// Test_AddAStandaloneProductAsABundled

		when: "we add a standalone product to a bundle component in the cart"
		addProductToNewBundle("STANDALONE01", "ProductComponent1")

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Product 'STANDALONE01' is not in the product list of component (bundle template) #ProductComponent1"
	}

	@Test
	def "Product from another bundle can not be added with the bundle"() {
		// Test_AddAProductOfAnotherBundle

		when: "we add a standalone product to a bundle component in the cart"
		addProductToNewBundle("PRODUCT01", "PremiumComponent2")

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Product 'PRODUCT01' is not in the product list of component (bundle template) #PremiumComponent2"
	}

	@Test
	def "Products with soldIndividually true can be sold out of bundle"() {
		// Test_IndividualProductCanBeSoldSeparately

		given:
		verifyCartTotal(0.0)

		when: "we add a product with soldIndividually true to the cart"
		addProductToCartOnce("PRODUCT05")

		then: "cart totals are correct "
		verifyCartTotal(650.00)
	}

	@Test
	def "Not individual products can not be sold separately"() {
		// Test_NotIndividualProductCanNotBeSoldSeparately

		when: "we add a standalone product to a bundle component in the cart"
		addProductToCartOnce("PRODUCT06")

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "The given product 'PRODUCT06' can not be sold individually."
	}

	@Test
	def "Not individual products can be sold as a part of bundle"() {
		// Test_NotIndividualProductCanBeSoldInABundle

		given:
		verifyCartTotal(0.0)

		when: "we add a product for a bundle component to the cart"
		bundleCartFacade.startBundle("ProductComponent1", "PRODUCT06", 1)

		then: "cart totals are correct "
		verifyCartTotal(600.00)
	}

	@Test
	def "Adding the same product to the same bundle"() {
		// Test_Add_To_Cart_TheSameProduct_to_TheSameBundle

		given:
		verifyCartTotal(0.0)

		when: "we add a product for a bundle component to the cart"
		addProductToNewBundle("PRODUCT01", "ProductComponent1")

		then: "cart totals are correct "
		verifyCartTotal(600.00)

		when:"we add the same product to the bundle component in the cart"
		addProductToExistingBundle("PRODUCT01", "ProductComponent1", 1)

		then: "cart total is correct"
		verifyCartTotal(1200)
	}

	@Test
	def "Pick N to M criterion should limit maximum number of products being added to single bundle"() {
		// Test_PickNtoMOverflow
		given:
		addProductToNewBundle("PRODUCT01", "OptionalComponent")

		when:
		addProductToExistingBundle("PRODUCT02", "OptionalComponent", 1)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Upper limit of possible product selections for component #OptionalComponent reached; items allowed: 1; items selected: 1"
	}

	@Test
	def "Pick exactly N criterion should limit maximum number of products being added to single bundle"() {
		// Test_PickMOverflow
		given:
		addProductToNewBundle("PRODUCT01", "ProductComponent1")
		addProductToExistingBundle("PRODUCT02", "ProductComponent1", 1)
		addProductToExistingBundle("PRODUCT03", "ProductComponent1", 1)

		when:
		addProductToExistingBundle("PRODUCT04", "ProductComponent1", 1)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Upper limit of possible product selections for component #ProductComponent1 reached; items allowed: 3; items selected: 3"
	}


	@Test
	def "two items of same product cannot be added  to a new bundle component"() {
		// Test_Add_To_Cart_NewBundle_CheckGroup

		when:
		addQuantityOfProductsToExistingBundle(2, "PRODUCT01", "ProductComponent1", -1)
		addQuantityOfProductsToExistingBundle(2, "SHARED01", "PremiumComponent2", 1)
		def cart = getCartDTO()
		def group = findEntryGroupByRefInOrder(cart, "ParentPackage")

		then:
		group.erroneous == false

	}

	@Test
	def "multiple items of same product cannot be added  to a new bundle component"() {
		// Test_Add_To_Cart_NewBundle_CheckGroup

		when:
		addQuantityOfProductsToExistingBundle(10, "PRODUCT01", "ProductComponent1", -1)
		def cart = getCartDTO()
		def group = findEntryGroupByRefInOrder(cart, "ProductComponent1")

		then:
		group.erroneous == true

	}

	@Test
	def "multiple items of same product cannot be added  to existing bundle component"() {
		// Test_Add_To_Cart_ExistingBundle_CheckQuantity

		when:
		addProductToNewBundle("PRODUCT01", "OptionalComponent")

		then:
		verifyCartTotal(600)

		when:
		addProductToExistingBundle("PRODUCT02", "ProductComponent1", 1)

		then:
		verifyCartTotal(1250)

		when:
		addQuantityOfProductsToExistingBundle(30, "PRODUCT02", "ProductComponent1", 1)

		then:
		def cart = getCartDTO()
		def group = findEntryGroupByRefInOrder(cart, "ProductComponent1")

		then:
		group.erroneous == true

	}

	@Test
	def "only one piece of product can be added to a new  bundle"() {
		// Test_Add_To_Cart_NewBundle_CheckQuantity_One

		when:
		addQuantityOfProductsToExistingBundle(1, "PRODUCT01", "ProductComponent1", -1)

		then:
		verifyCartTotal(600)

	}

	@Test
	def "Only one piece of product can be added to an existing bundle"() {
		// Test_Add_To_Cart_ExistingBundle_CheckQuantity_One

		given:
		addQuantityOfProductsToExistingBundle(1, "PRODUCT01", "ProductComponent1", -1)

		when:
		addQuantityOfProductsToExistingBundle(1, "PRODUCT02", "ProductComponent1", 1)

		then:
		verifyCartTotal(1250)

	}

	@Test
	def "Product with quantity 0 cannot be added to a new bundle component"() {
		// Test_Add_To_Cart_NewBundle_AddQuantity_Zero

		when:
		addQuantityOfProductsToExistingBundle(0, "PRODUCT02", "ProductComponent1", -1)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Quantity must not be less than one"
	}

	@Test
	def "Product with quantity 0 cannot be added to existing bundle component"() {
		// Test_Add_To_Cart_ExistingBundle_AddQuantity_Zero

		given:
		addProductToNewBundle("PRODUCT01", "ProductComponent1")

		when:
		addQuantityOfProductsToExistingBundle(0, "PRODUCT02", "ProductComponent1", 1)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Quantity must not be less than one"
	}

	@Test
	def "Check that giving bundle index of -1 generates new bundle"() {
		// Test_NewBundleIsGenerated
		// The test is indirect: single bundle is created and filled with products
		//	up to the limit, so adding more products is impossible.
		//	Then one more product is added to bundle '-1'. Successful result means new bundle was created


		given:
		addProductToNewBundle("PRODUCT01", "ProductComponent1")
		addProductToExistingBundle("PRODUCT02", "ProductComponent1", 1)

		when:
		addProductToExistingBundle("PRODUCT03", "ProductComponent1", 1)

		then:
		verifyCartTotal(2100)

		when:
		addProductToExistingBundle("PRODUCT04", "ProductComponent1", 1)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Upper limit of possible product selections for component #ProductComponent1 reached; items allowed: 3; items selected: 3"

		when:
		addProductToExistingBundle("PRODUCT04", "ProductComponent1", -1)

		then:
		verifyCartTotal(3050)

	}

	@Test
	def "Allowed values of bunde index are -1 0 1"() {
		// Test_BundleIndex_MustBeGreaterThan_Minus2

		when:
		addProductToExistingBundle("PRODUCT01", "ProductComponent1", -2)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "The bundleNo must not be lower then '-1', given bundleNo: -2"
	}

	@Test
	def "Bundle index -1 means 'put product into a new bundle"() {
		// Test_BundleIndexMinus1_means_NewBundle
		given:
		verifyCartTotal(0)

		when:
		addProductToExistingBundle("PRODUCT01", "ProductComponent1", -1)

		then:
		verifyCartTotal(600)
	}

	@Test
	def "If bundle index is positive, product should be added to the given bundle"() {
		// Test_ExistingBundleIndex_ShoudlAddToCart

		given:
		addProductToNewBundle("PRODUCT01", "ProductComponent1")

		when:
		addProductToExistingBundle("PRODUCT02", "ProductComponent1", 1)

		then:
		verifyCartTotal(1250)
	}

	@Test
	def "If bundle index is positive then the given bundle must exist"() {
		// Test_NonExistingBundleIndex_ShouldFail

		when:
		addProductToExistingBundle("PRODUCT01", "ProductComponent1", 100)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Can't determine parentBundleTemplateModel"
	}

	@Test
	def "Adding the same product to different bundles is allowed"() {
		// Test_Add_To_Cart_TheSameProduct_to_DifferentBundles

		given:
		verifyCartTotal(0)

		when:
		addProductToNewBundle("SHARED01", "ProductComponent1")

		then:
		verifyCartTotal(99)

		when:
		addProductToExistingBundle("SHARED01", "PremiumComponent2", 1)

		then:
		verifyCartTotal(198)
	}

	@Test
	def "Replace a bundle product in cart"() {
		// Test_ReplaceProductInBundle

		when:
		addProductToNewBundle("PRODUCT01", "ProductComponent1")

		then:
		verifyCartTotal(600)

		when:
		replaceBundleProduct("ProductComponent1", "PRODUCT02", 1)

		then:
		verifyCartTotal(650)
	}

	@Test
	def "Try to replace product giving incorrect index of bundle"() {
		// Test_ReplaceProductInBundle_IncorrectBundleNo

		when:
		addProductToNewBundle("PRODUCT01", "ProductComponent1")

		then:
		verifyCartTotal(600)

		when:
		replaceBundleProduct("ProductComponent1", "PRODUCT02", 2)

		then: "exception is expected"
		CommerceCartModificationException e = thrown()
		e.message == "Can't determine parentBundleTemplateModel"
	}

	@Test
	def "Replace in new bundle works as addition"() {
		// Test_ReplaceProductInNewBundle

		given:
		addProductToNewBundle("PRODUCT01", "ProductComponent1")

		when:
		replaceBundleProduct("ProductComponent1", "PRODUCT02", -1)

		then:
		verifyCartTotal(1250)
	}

	@Test
	def "Try to replace product giving negative index of bundle"() {
		// Test_ReplaceProductInBundle_NegativeBundleNo

		when:
		addProductToNewBundle("PRODUCT01", "ProductComponent1")

		then:
		verifyCartTotal(600)

		when:
		replaceBundleProduct("ProductComponent1", "PRODUCT02", -2)

		then: "exception is expected"
		CommerceCartModificationException e = thrown()
		e.message == "The bundleNo must not be lower then '-1', given bundleNo: -2"
	}

	@Test
	def "Try to replace product giving incorrect component id"() {
		// Test_ReplaceProductInBundle_IncorrectComponentId

		given:
		addProductToNewBundle("PRODUCT01", "ProductComponent1")
		verifyCartTotal(600)

		when:
		replaceBundleProduct("PremiumComponent2", "PRODUCT02", 1)

		then: "exception is expected"
		CommerceCartModificationException e = thrown()
		e.message == "Product 'PRODUCT02' is not in the product list of component (bundle template) #PremiumComponent2"
	}

	@Test
	def "Try to replace product giving unknown id of component"() {
		// Test_ReplaceProductInBundle_NonExistingComponentId

		given:
		addProductToNewBundle("PRODUCT01", "ProductComponent1")
		verifyCartTotal(600)

		when:
		replaceBundleProduct("PremiumComponent99", "PRODUCT02", 1)

		then: "exception is expected"
		ModelNotFoundException e = thrown()
		e.message == "No result for the given query"
	}

	// Test_ChangeComponent_ComponentIdOmitted is only testing the test keyword mechanism and is obsolete

	@Test
	def "Replace existing bundle in cart with another component id and another product"() {
		// Test_ChangeComponent_AnotherProduct

		given:
		addProductToNewBundle("PRODUCT05", "ProductComponent1")
		addProductToNewBundle("PREMIUM01", "PremiumComponent2")
		verifyCartTotal(1360)

		when:
		replaceBundleProduct("ProductComponent1", "PRODUCT01", 1)

		then:
		verifyCartTotal(1310)
	}

	@Test
	def "Try to change bundle product to another component"() {
		// Test_ChangeComponent_AnotherBundle The original product should remain, as it does not belong to the component provided.

		given:
		addProductToNewBundle("PRODUCT05", "ProductComponent1")

		when:
		replaceBundleProduct("OptionalComponent", "PRODUCT01", 1)

		then:
		verifyCartTotal(1250)
	}

	@Test
	def "Try to change bundle product to a component from another bundle"() {
		// Test_ChangeComponent_AnotherBundle The original product should remain, as it does not belong to the component provided.

		given:
		addProductToNewBundle("PRODUCT05", "ProductComponent1")

		when:
		replaceBundleProduct("SecondComponent1", "PRODUCT01", 1)

		then: "exception is expected"
		IllegalArgumentException e = thrown()
		e.message == "Bundle #1 does not contain component 'SecondComponent1'"
	}

	@Test
	def "Adding dependent component to empty cart is not allowed"() {
		// Test_Add_To_Cart_EmptyCart_NoRequiredComponent_ShouldFail

		given:
		verifyCartTotal(0)

		when:
		addProductToNewBundle("PRODUCT01","DependentComponent")

		then: "exception is expected"
		AssertionError e = thrown()
		e.message.startsWith("Component 'DependentComponent' cannot be modified as its selection dependency to component one of its components is not fulfilled;")
	}

	@Test
	def "Adding dependent component without required component to existing bundle is not allowed"() {
		// Test_Add_To_Cart_ExistingBundle_NoRequiredComponent_ShouldFail

		given:
		verifyCartTotal(0)
		addProductToNewBundle("SHARED01", "AnotherComponent")
		verifyCartTotal(99)

		when:
		addProductToExistingBundle("PRODUCT01", "DependentComponent", 1)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message.startsWith("Component 'DependentComponent' cannot be modified as its selection dependency to component one of its components is not fulfilled;")
	}

	@Test
	def "Adding required component to one bundle and dependent component to another is not allowed"() {
		// Test_Add_To_Cart_DifferentBundles_ShouldFail

		given:
		verifyCartTotal(0)
		addProductToNewBundle("PRODUCT01", "RequiredComponent")
		verifyCartTotal(600)

		when:
		addProductToNewBundle("PRODUCT02", "DependentComponent")

		then: "exception is expected"
		AssertionError e = thrown()
		e.message.startsWith("Component 'DependentComponent' cannot be modified as its selection dependency to component one of its components is not fulfilled;")
	}

	@Test
	def "Adding required and dependent components to an existing bundle in a cart"() {
		// Test_Add_To_Cart_ExistingBundle_RequiredComponent

		given:
		verifyCartTotal(0)
		addProductToNewBundle("PRODUCT01", "AnotherComponent")
		verifyCartTotal(600)

		when:
		addProductToNewBundle("PRODUCT01", "RequiredComponent")

		then:
		verifyCartTotal(1200)

		when:
		addProductToExistingBundle("PRODUCT02", "DependentComponent", 2)

		then:
		verifyCartTotal(1850)
	}

	@Test
	def "Adding conditional and target products from one component is not allowed by a disable rule"() {
		// Test_Add_To_Cart_DisableRules_ConditionalAndTargetProductsFromOneComponent

		given:
		verifyCartTotal(0)
		addProductToNewBundle("PRODUCT03", "ThirdComponent1")
		verifyCartTotal(850)

		when:
		addProductToExistingBundle("PRODUCT01", "ThirdComponent1", 1)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Product PRODUCT01 cannot be added as disable rule 'ThirdPackage_DisableProduct01WhenProduct03' of component #ThirdComponent1 is violated"
	}

	@Test
	def "Adding conditional and target products from 2 different components is not allowed by a disable rule"() {
		// Test_Add_To_Cart_DisableRules_ConditionalAndTargetProductsFromDifferentComponents

		given:
		verifyCartTotal(0)
		addProductToNewBundle("PRODUCT04", "ThirdComponent2")
		verifyCartTotal(950)

		when:
		addProductToExistingBundle("PRODUCT01", "ThirdComponent1", 1)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Product PRODUCT01 cannot be added as disable rule 'ThirdPackage_DisableProduct01WhenProduct04' of component #ThirdComponent1 is violated"
	}

	@Test
	def "Adding a target product to bundle component after any conditional products in another component of the same bundle is not allowed by a disable rule"() {
		// Test_Add_To_Cart_DisableRules_TargetProduct_AnyConditionalProductsInAnotherComponent

		given:
		verifyCartTotal(0)
		addProductToNewBundle("PREMIUM01", "ThirdComponent3")
		verifyCartTotal(710)

		when:
		addProductToExistingBundle("PRODUCT01", "ThirdComponent1", 1)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Product PRODUCT01 cannot be added as disable rule 'ThirdPackage_DisableProduct01WithAnyPremiumProduct' of component #ThirdComponent1 is violated"
	}

	@Test
	def "Adding a target product to bundle component with all conditional products in another component of the same bundle is not allowed by a disable rule"() {
		// Test_Add_To_Cart_DisableRules_TargetProduct_AllConditionalProductsInAnotherComponent

		given:
		verifyCartTotal(0)
		addProductToNewBundle("PREMIUM01", "ThirdComponent3")
		addProductToExistingBundle("PREMIUM02", "ThirdComponent3", 1)
		addProductToExistingBundle("PREMIUM03", "ThirdComponent3", 1)
		addProductToExistingBundle("PREMIUM04", "ThirdComponent3", 1)
		addProductToExistingBundle("PREMIUM05", "ThirdComponent3", 1)
		addProductToExistingBundle("PREMIUM06", "ThirdComponent3", 1)
		verifyCartTotal(4410)

		when:
		addProductToExistingBundle("PRODUCT02", "ThirdComponent1", 1)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Product PRODUCT02 cannot be added as disable rule 'ThirdPackage_DisableProduct02WithAllPremiumProducts' of component #ThirdComponent1 is violated"
	}


	@Test
	def "Adding products without disable rules is allowed"() {
		// Test_Add_To_Cart_NoDisableRules

		given:
		verifyCartTotal(0)
		addProductToNewBundle("PRODUCT03", "ThirdComponent1")
		verifyCartTotal(850)

		when:
		addProductToExistingBundle("PRODUCT02", "ThirdComponent1", 1)

		then: "cart total is correct"
		verifyCartTotal(1500)
	}

	@Test
	def "Adding products with configured ChangePriceRule and DisableRule is not allowed by by the disable rule"() {
		// Test_Add_To_Cart_DisableRules_ChangePriceRuleForTheSameProducts

		given:
		verifyCartTotal(0)
		addProductToNewBundle("PRODUCT01", "ThirdComponent1")
		verifyCartTotal(600)

		when:
		addProductToExistingBundle("SHARED01", "ThirdComponent5", 1)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Product SHARED01 cannot be added as disable rule 'ThirdPackage_DisableSHARED01WhenProduct01' of component #ThirdComponent5 is violated"
	}

	@Test
	def "Adding target product if conditional products weren't configured for disable rule is allowed"() {
		// Test_Add_To_Cart_DisableRules_NoConditionalProducts

		given:
		verifyCartTotal(0)
		addProductToNewBundle("PRODUCT01", "ThirdComponent1")
		verifyCartTotal(600)

		when:
		addProductToExistingBundle("SHARED02", "ThirdComponent6", 1)

		then: "cart total is correct"
		verifyCartTotal(699)
	}

	@Test
	def "Adding bundle if target products weren't configured for disable rule is allowed"() {
		// Test_Add_To_Cart_DisableRules_NoTargetProducts

		given:
		verifyCartTotal(0)
		addProductToNewBundle("SHARED01", "ThirdComponent5")
		verifyCartTotal(99)

		when:
		addProductToExistingBundle("SHARED02", "ThirdComponent7", 1)

		then: "cart total is correct"
		verifyCartTotal(198)
	}

	@Test
	def "Adding the conditional and target products to different bundles is allowed"() {
		// Test_Add_To_Cart_DisableRules_ConditionalAndTargetProductsFromDifferentBundles

		given:
		verifyCartTotal(0)
		addProductToNewBundle("PRODUCT03", "ThirdComponent1")
		verifyCartTotal(850)

		when:
		addProductToNewBundle("PRODUCT01", "ThirdComponent1")

		then: "cart total is correct"
		verifyCartTotal(1450)
	}

	@Test
	def "Adding a conditional product to a bundle and a target product as a standalone product is allowed"() {
		// Test_Add_To_Cart_DisableRules_ConditionalAndTargetProductsFromDifferentBundles

		given:
		verifyCartTotal(0)
		addProductToNewBundle("PRODUCT03", "ThirdComponent1")
		verifyCartTotal(850)

		when:
		addProductToCartOnce("PRODUCT01")

		then: "cart total is correct"
		verifyCartTotal(1450)
	}

	@Test
	def "Adding a conditional product as a standalone product and a target product to a bundle is allowed."() {
		// Test_Add_To_Cart_DisableRules_ConditionalProductIsStandalone_TargetProductIsInBundled

		given:
		verifyCartTotal(0)
		addProductToCartOnce("PRODUCT03")
		verifyCartTotal(850)

		when:
		addProductToNewBundle("PRODUCT01", "ThirdComponent1")

		then: "cart total is correct"
		verifyCartTotal(1450)
	}

	@Test
	def "Adding a conditional product to the bundle after a target product is not allowed by disable rule."() {
		// Test_Add_To_Cart_DisableRules_TargetIsAlreadyInCart

		given:
		verifyCartTotal(0)
		addProductToNewBundle("PRODUCT01", "ThirdComponent1")
		verifyCartTotal(600)

		when:
		addProductToExistingBundle("PRODUCT03", "ThirdComponent1", 1)

		then: "exception is expected"
		AssertionError e = thrown()
		e.message == "Product PRODUCT03 cannot be added as disable rule 'ThirdPackage_DisableProduct01WhenProduct03' of component #ThirdComponent1 is violated"
	}

}
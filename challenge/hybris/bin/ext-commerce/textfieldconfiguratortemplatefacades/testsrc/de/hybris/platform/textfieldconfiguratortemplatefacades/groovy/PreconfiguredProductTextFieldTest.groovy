package de.hybris.platform.textfieldconfiguratortemplatefacades.groovy

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercefacades.groovy.AbstractCommerceFacadesSpockTest
import de.hybris.platform.commerceservices.url.impl.DefaulCustomPageUrlResolver;
import de.hybris.platform.jalo.order.CartEntry
import de.hybris.platform.order.InvalidCartException

import org.junit.Test

@IntegrationTest
class PreconfiguredProductTextFieldTest extends AbstractCommerceFacadesSpockTest{

	def CUSTOMER_ID = "testuser@saved-carts.com"
	
	def setup() {
		importCsv("/textfieldconfiguratortemplatefacades/testdata/testCheckout.csv", "utf-8");
		importCsv("/textfieldconfiguratortemplatefacades/testdata/basicData.csv", "utf-8");
		given: "a session with a logged in user"
		createCustomerWithHybrisApi(CUSTOMER_ID, "pwd", "mr", "Test", "User");
		login(CUSTOMER_ID);
		updateUserDetails();
	}

	@Test
	def "Test Preconfigured Product Text Field"() {
		when: "add product P1 to cart"
		addProductToCartOnce("P1");
		def cart = getCartDTO();
		then: "verify cart"
		verifyListSizeEquals(cart.entries,1);
		verifyCartTotal(120);
		
		when:"do checkout for current customer"
		def order = doCheckout()
		def entry = getEntryByNumber(order,0);
		def configuration = getConfigurationForOrderEntry(entry,"fontSize");
		then: "verify configuration"
		with(configuration){
			configurationValue.equals("12");
		}
	}
	
	@Test
	def "Test Change Configuration Product Text Field"() {
		when: "add product P1 to cart"
		addProductToCartOnce("P1");
		def cart = getCartDTO();
		def cartEntry = getEntryByNumber(cart,0);
		def cartConfiguration = getConfigurationForOrderEntry(cartEntry,"fontSize");
		then: "verify cart configuration"
		with(cartConfiguration){
			configurationValue.equals("12");
		}
	
		when:"update configuration info and checkout"
		updateConfigurationInfo(cartEntry,"fontSize","20");
		def order = doCheckout();
		def orderEntry = getEntryByNumber(order,0);
		def orderConfiguration = getConfigurationForOrderEntry(orderEntry,"fontSize");
		then: "verify order configuration"
		with(orderConfiguration){
			configurationValue.equals("20");
		}
	}
	
	@Test
	def "Test Preconfigured Out Of Stock Product Text Field"(){
		when: "add product P2 to cart"
		addProductToCartOnce("P2");
		def cart = getCartDTO();
		then: "verify cart"
		verifyListSizeEquals(cart.entries,0);
	}
	
	@Test
	def "Test Preconfigured Empty Configuration Product Text Field"(){
		when: "add product P1 to cart"
		addProductToCartOnce("P1");
		def cart = getCartDTO();
		def cartEntry = getEntryByNumber(cart,0);
		updateConfigurationInfo(cartEntry,"fontSize","");
		then: "verify cart entry"
		verifyListSizeEquals(cart.entries,1);
		verifyCartTotal(120);
		
		when:"verify cart"
		validateSessionCart();
		then:"expect error"
		InvalidCartException e = thrown();
		e.message == "Cart is not valid:\n";
		
	}
	
	@Test
	def "Test Preconfigured Out Of Stock Back In Stock Product Text Field"(){
		when: "add product P2 to cart"
		addProductToCartOnce("P2");
		def cart = getCartDTO();
		then: "verify cart"
		verifyListSizeEquals(cart.entries,0);
		
		when: "update stock level and add product P2 to cart"
		importCsv("/textfieldconfiguratortemplatefacades/testdata/updatedData.csv", "utf-8");
		addProductToCartOnce("P2");
		cart = getCartDTO();
		then: "verify cart"
		verifyListSizeEquals(cart.entries,1);
		verifyCartTotal(120);
		
		when:"do checkout for current customer"
		def order = doCheckout();
		def orderEntry = getEntryByNumber(order,0);
		def orderConfiguration = getConfigurationForOrderEntry(orderEntry,"fontSize");
		then:"verify cart"
		order != null;
		orderConfiguration != null;
		with(orderEntry)
		{
			quantity == 1;
		}
		with(orderConfiguration){
			configurationValue.equals("12");
		}
	}
	
	@Test
	def "Test Change Default Configuration Product Text Field"(){
		when: "add product P1 to cart"
		addProductToCartOnce("P1");
		def cart = getCartDTO();
		def cartEntry = getEntryByNumber(cart,0);
		def cartConfiguration = getConfigurationForOrderEntry(cartEntry,"fontSize");
		then: "verify cart configuration"
		with(cartConfiguration){
			configurationValue.equals("12");
		}
		
		when:"change default configuration"
		importCsv("/textfieldconfiguratortemplatefacades/testdata/updatedData.csv", "utf-8");
		cart = getCartDTO();
		cartEntry = getEntryByNumber(cart,0);
		cartConfiguration = getConfigurationForOrderEntry(cartEntry,"fontSize");
		then: "verify cart configuration"
		with(cartConfiguration){
			configurationValue.equals("12");
		}
	}
	
	@Test
	def "Test Add Product Two Times Same Configuration"(){
		when: "add product P1 to cart twice"
		addProductToCartOnce("P1");
		addProductToCartOnce("P1");
		def cart = getCartDTO();
		then: "verify cart"
		verifyListSizeEquals(cart.entries,2);
		
		when:"get configuration"
		def cartEntry1 = getEntryByNumber(cart,0);
		def cartConfiguration1 = getConfigurationForOrderEntry(cartEntry1,"fontSize");
		def cartEntry2 = getEntryByNumber(cart,1);
		def cartConfiguration2 = getConfigurationForOrderEntry(cartEntry2,"fontSize");
		then: "verify cart configuration"
		with(cartConfiguration1){
			configurationValue.equals("12");
		}
		with(cartConfiguration2){
			configurationValue.equals("12");
		}
	} 
	
	@Test
	def "Test_Add_Product_Two_Times_Different_Configurations"(){
		when: "add product P1 to cart"
		addProductToCartOnce("P1");
		def cart = getCartDTO();
		then: "verify cart"
		verifyListSizeEquals(cart.entries,1);
		
		when:"update configuration info and add a same product"
		def cartEntry = getEntryByNumber(cart,0);
		updateConfigurationInfo(cartEntry,"fontSize","20");
		addProductToCartOnce("P1");
		cart = getCartDTO();
		def cartEntry1 = getEntryByNumber(cart,0);
		def cartConfiguration1 = getConfigurationForOrderEntry(cartEntry1,"fontSize");
		def cartEntry2 = getEntryByNumber(cart,1);
		def cartConfiguration2 = getConfigurationForOrderEntry(cartEntry2,"fontSize");
		then: "verify cart configuration"
		verifyListSizeEquals(cart.entries,2);
		with(cartConfiguration1){
			configurationValue.equals("20");
		}
		with(cartConfiguration2){
			configurationValue.equals("12");
		}
	}
	
	@Test
	def "Test Add Product Two Times Update With Same Configuration"(){
		when: "add product P1 to cart"
		addProductToCartOnce("P1");
		def cart = getCartDTO();
		then: "verify cart"
		verifyListSizeEquals(cart.entries,1);
		
		when:"update configuration info and add a same product"
		def cartEntry = getEntryByNumber(cart,0);
		updateConfigurationInfo(cartEntry,"fontSize","20");
		addProductToCartOnce("P1");
		cart = getCartDTO();
		cartEntry = getEntryByNumber(cart,0);
		updateConfigurationInfo(cartEntry,"fontSize","12");
		def cartEntry1 = getEntryByNumber(cart,0);
		def cartConfiguration1 = getConfigurationForOrderEntry(cartEntry1,"fontSize");
		def cartEntry2 = getEntryByNumber(cart,1);
		def cartConfiguration2 = getConfigurationForOrderEntry(cartEntry2,"fontSize");
		then: "verify cart configuration"
		verifyListSizeEquals(cart.entries,2);
		with(cartConfiguration1){
			configurationValue.equals("12");
		}
		with(cartConfiguration2){
			configurationValue.equals("12");
		}
	}
	
	@Test
	def "Test Add Different Products Same Configuration"(){
		when: "add product P1 and P3 to cart"
		addProductToCartOnce("P1");
		addProductToCartOnce("P3");
		def cart = getCartDTO();
		then: "verify cart and cart entries"
		verifyListSizeEquals(cart.entries,2);
		def cartEntry1 = getEntryByNumber(cart,0);
		def cartConfiguration1 = getConfigurationForOrderEntry(cartEntry1,"fontSize");
		def cartEntry2 = getEntryByNumber(cart,1);
		def cartConfiguration2 = getConfigurationForOrderEntry(cartEntry2,"fontSize");
		with(cartEntry1){
			product.code.equals("P1");
		}
		with(cartConfiguration1){
			configurationValue.equals("12");
		}
		with(cartEntry2){
			product.code.equals("P3");
		}
		with(cartConfiguration2){
			configurationValue.equals("12");
		}
	}
	
	@Test
	def "Test Add Product With Configuration And Quantity"(){
		when: "add 3 P1 to cart"
		addProductToCart("P1",3);
		def cart = getCartDTO();
		then: "verify cart"
		verifyListSizeEquals(cart.entries,1);
		verifyCartTotal(360);

		when:"do checkout for current customer"
		def order = doCheckout()
		def entry = getEntryByNumber(order,0);
		def configuration = getConfigurationForOrderEntry(entry,"fontSize");
		then: "verify cart"
		with(configuration){
			configurationValue.equals("12");
		}
		with(entry)
		{
			quantity == 3;
		}
	}
	
	@Test
	def "Test Remove Product With Configuration"(){
		when: "add product P1 to cart"
		addProductToCartOnce("P1");
		def cart = getCartDTO();
		then: "verify cart"
		verifyListSizeEquals(cart.entries,1);
		
		when:"delete cart entry"
		deleteCartEntry(0);
		cart = getCartDTO();
		then: "verify cart"
		verifyListSizeEquals(cart.entries,0);
	}
	
	@Test
	def "Test Add Configured And Standart Product Remove Configured"(){
		when: "add product P1 and P3 to cart"
		addProductToCartOnce("P1");
		addProductToCartOnce("P3");
		def cart = getCartDTO();
		def cartEntry1 = getEntryByNumber(cart,0);
		def cartConfiguration1 = getConfigurationForOrderEntry(cartEntry1,"fontSize");
		def cartEntry2 = getEntryByNumber(cart,1);
		def cartConfiguration2 = getConfigurationForOrderEntry(cartEntry2,"fontSize");
		then: "verify cart"
		verifyListSizeEquals(cart.entries,2);
		with(cartEntry1){
			product.code.equals("P1");
		}
		with(cartConfiguration1){
			configurationValue.equals("12");
		}
		with(cartEntry2){
			product.code.equals("P3");
		}
		with(cartConfiguration2){
			configurationValue.equals("12");
		}
		
		when:"delete cart entry"
		deleteCartEntry(0);
		cart = getCartDTO();
		cartEntry1 = getEntryByNumber(cart,0);
		then: "verify cart"
		verifyListSizeEquals(cart.entries,1);
		with(cartEntry1){
			product.code.equals("P3");
		}
	}
	
	@Test
	def "Test Add Two Differently Configured Products Remove One"(){
		when: "add product P1 to cart, update configuration, add another one and delete entry 0"
		addProductToCartOnce("P1");
		def cart = getCartDTO();
		def cartEntry = getEntryByNumber(cart,0);
		updateConfigurationInfo(cartEntry,"fontSize","20");
		addProductToCartOnce("P1");
		deleteCartEntry(0);
		cart = getCartDTO();
		cartEntry = getEntryByNumber(cart,0);
		def cartConfiguration = getConfigurationForOrderEntry(cartEntry,"fontSize");
		then: "verify cart configuration"
		verifyListSizeEquals(cart.entries,1);
		with(cartConfiguration){
			configurationValue.equals("12");
		}
	}
	
	@Test
	def "Test Add Two Products Same Configuration Remove One"(){
		when: "add product P1 to cart twice"
		addProductToCartOnce("P1");
		addProductToCartOnce("P1");
		def cart = getCartDTO();
		then: "verify cart"
		verifyListSizeEquals(cart.entries,2);
		
		when:"delete cart entry"
		deleteCartEntry(0);
		cart = getCartDTO();
		def cartEntry = getEntryByNumber(cart,0);
		def cartConfiguration = getConfigurationForOrderEntry(cartEntry,"fontSize");
		then: "verify cart"
		verifyListSizeEquals(cart.entries,1);
		with(cartConfiguration){
			configurationValue.equals("12");
		}
	}
}

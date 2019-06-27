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
class CurrencySwitchTest extends AbstractSubscriptionFacadesSpockTest {

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testCurrencySwitch.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "Switch currency one subscription product"() {
		// Test_Switch_Currency_One_SubscriptionProduct
		when:
		setCartCurrencyTo("USD")
		addProductToCartOnce("P1")

		then:
		verifyCartTotal(5.75)
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 10.00)
		verifyCartTotalForBillingEvent("monthly", 20.99)

		when:
		setCartCurrencyTo("EUR")

		then:
		verifyCartTotal(20.50)
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 21.00)
		verifyCartTotalForBillingEvent("monthly", 31.99)

	}

	@Test
	def "Switch currency three subscription products"() {
		// Test_Switch_Currency_Three_SubscriptionProducts
		when:
		setCartCurrencyTo("USD")
		addProductToCartOnce("P1")
		addProductToCartOnce("P1")
		addProductToCartOnce("P1")

		then:
		verifyCartTotal(17.25)
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 30.00)
		verifyCartTotalForBillingEvent("monthly", 62.97)

		when:
		setCartCurrencyTo("EUR")

		then:
		verifyCartTotal(61.50)
		verifyNumberOfChildCarts(2)
		verifyCartTotalForBillingEvent("onfirstbill", 63.00)
		verifyCartTotalForBillingEvent("monthly", 95.97)

	}

	@Test
	def "Switch currency xml"() {
		// Test_Switch_Currency_Change_Xml
		when:
		setCartCurrencyTo("USD")
		addProductToCartOnce("P1")

		then: "generated XML contents are correct"
		verifyProductXml('P1', "product/price[@class='subscriptionPricePlan']",
				'''<price class="subscriptionPricePlan">
                 <currencyIso>USD</currencyIso>
                 <oneTimeChargeEntries>
                   <oneTimeChargeEntry>
                     <price>
                       <currencyIso>USD</currencyIso>
                       <priceType>BUY</priceType>
                       <value>5.75</value>
                       <formattedValue>$5.75</formattedValue>
                     </price>
                     <billingTime>
                       <description>Pay Now</description>
                       <orderNumber>1</orderNumber>
                       <name>Pay on Checkout</name>
                       <nameInOrder>Paid on order</nameInOrder>
                       <code>paynow</code>
                     </billingTime>
                   </oneTimeChargeEntry>
                   <oneTimeChargeEntry>
                     <price>
                       <currencyIso>USD</currencyIso>
                       <priceType>BUY</priceType>
                       <value>10.0</value>
                       <formattedValue>$10.00</formattedValue>
                     </price>
                     <billingTime>
                       <description>On First Bill</description>
                       <orderNumber>2</orderNumber>
                       <name>On first bill</name>
                       <code>onfirstbill</code>
                     </billingTime>
                   </oneTimeChargeEntry>
                 </oneTimeChargeEntries>
                 <name>PP1_USD</name>
                 <usageCharges class="empty-list"/>
                 <recurringChargeEntries>
                   <recurringChargeEntry>
                     <price>
                       <currencyIso>USD</currencyIso>
                       <priceType>BUY</priceType>
                       <value>20.99</value>
                       <formattedValue>$20.99</formattedValue>
                     </price>
                     <cycleStart>1</cycleStart>
                     <cycleEnd>-1</cycleEnd>
                   </recurringChargeEntry>
                 </recurringChargeEntries>
               </price>''')

		when:
		setCartCurrencyTo("EUR")

		then: "generated XML contents are correct"
		verifyProductXml('P1', "product/price[@class='subscriptionPricePlan']",
				'''<price class="subscriptionPricePlan">
                       <currencyIso>EUR</currencyIso>
                       <oneTimeChargeEntries>
                         <oneTimeChargeEntry>
                           <price>
                             <currencyIso>EUR</currencyIso>
                             <priceType>BUY</priceType>
                             <value>20.5</value>
                             <formattedValue>E20.50</formattedValue>
                           </price>
                           <billingTime>
                             <description>Pay Now</description>
                             <orderNumber>1</orderNumber>
                             <name>Pay on Checkout</name>
                             <nameInOrder>Paid on order</nameInOrder>
                             <code>paynow</code>
                           </billingTime>
                         </oneTimeChargeEntry>
                         <oneTimeChargeEntry>
                           <price>
                             <currencyIso>EUR</currencyIso>
                             <priceType>BUY</priceType>
                             <value>21.0</value>
                             <formattedValue>E21.00</formattedValue>
                           </price>
                           <billingTime>
                             <description>On First Bill</description>
                             <orderNumber>2</orderNumber>
                             <name>On first bill</name>
                             <code>onfirstbill</code>
                           </billingTime>
                         </oneTimeChargeEntry>
                       </oneTimeChargeEntries>
                       <name>PP1_EUR</name>
                       <usageCharges class="empty-list"/>
                       <recurringChargeEntries>
                         <recurringChargeEntry>
                           <price>
                             <currencyIso>EUR</currencyIso>
                             <priceType>BUY</priceType>
                             <value>31.99</value>
                             <formattedValue>E31.99</formattedValue>
                           </price>
                           <cycleStart>1</cycleStart>
                           <cycleEnd>-1</cycleEnd>
                         </recurringChargeEntry>
                       </recurringChargeEntries>
                     </price>''')

	}

}
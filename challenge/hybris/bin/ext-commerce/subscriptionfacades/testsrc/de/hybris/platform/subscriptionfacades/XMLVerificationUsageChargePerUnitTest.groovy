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
class XMLVerificationUsageChargePerUnitTest extends AbstractSubscriptionFacadesSpockTest {

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testXMLVerificationUsageChargePerUnit.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "XML Verification Usage Charge PerUnit Overage Video"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_Overage_Video
		when:
		addProductToCartOnce("P1")

		then: "generated XML contents are correct"
		verifyProductXml('P1',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC1']",
				'''<perUnitUsageCharge>
               	<usageChargeEntries>
               		<overageUsageChargeEntry>
               			<price>
               				<currencyIso>USD</currencyIso>
               				<priceType>BUY</priceType>
               				<value>15.0</value>
               				<formattedValue>$15.00</formattedValue>
               			</price>
               		</overageUsageChargeEntry>
               	</usageChargeEntries>
               	<usageUnit>
               		<id>video</id>
               		<namePlural>Videos</namePlural>
               		<name>Video</name>
               		<accumulative>false</accumulative>
               	</usageUnit>
               	<name>PUC1</name>
               	<usageChargeType>
               		<code>each_respective_tier</code>
               	</usageChargeType>
               </perUnitUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit TierFrom1To1 Video"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_TierFrom1To1_Video
		when:
		addProductToCartOnce("P2")

		then: "generated XML contents are correct"
		verifyProductXml('P2',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC2']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>11.0</value>
			              <formattedValue>$11.00</formattedValue>
			            </price>
			            <tierEnd>1</tierEnd>
			            <tierStart>1</tierStart>
			          </tierUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>video</id>
			          <namePlural>Videos</namePlural>
			          <name>Video</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC2</name>
			        <usageChargeType>
			          <code>each_respective_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit TierFrom1To2 Video"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_TierFrom1To2_Video
		when:
		addProductToCartOnce("P3")

		then: "generated XML contents are correct"
		verifyProductXml('P3',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC3']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>11.0</value>
			              <formattedValue>$11.00</formattedValue>
			            </price>
			            <tierEnd>2</tierEnd>
			            <tierStart>1</tierStart>
			          </tierUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>video</id>
			          <namePlural>Videos</namePlural>
			          <name>Video</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC3</name>
			        <usageChargeType>
			          <code>each_respective_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit TierFrom1To2 From3to3 Video"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_TierFrom1To2_From3To3_Video
		when:
		addProductToCartOnce("P4")

		then: "generated XML contents are correct"
		verifyProductXml('P4',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC4']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>11.0</value>
			              <formattedValue>$11.00</formattedValue>
			            </price>
			            <tierEnd>2</tierEnd>
			            <tierStart>1</tierStart>
			          </tierUsageChargeEntry>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>12.0</value>
			              <formattedValue>$12.00</formattedValue>
			            </price>
			            <tierEnd>3</tierEnd>
			            <tierStart>3</tierStart>
			          </tierUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>video</id>
			          <namePlural>Videos</namePlural>
			          <name>Video</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC4</name>
			        <usageChargeType>
			          <code>each_respective_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit TierFrom1To2 From3to4 Video"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_TierFrom1To2_From3To4_Video
		when:
		addProductToCartOnce("P5")

		then: "generated XML contents are correct"
		verifyProductXml('P5',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC5']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>11.0</value>
			              <formattedValue>$11.00</formattedValue>
			            </price>
			            <tierEnd>2</tierEnd>
			            <tierStart>1</tierStart>
			          </tierUsageChargeEntry>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>12.0</value>
			              <formattedValue>$12.00</formattedValue>
			            </price>
			            <tierEnd>4</tierEnd>
			            <tierStart>3</tierStart>
			          </tierUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>video</id>
			          <namePlural>Videos</namePlural>
			          <name>Video</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC5</name>
			        <usageChargeType>
			          <code>each_respective_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit overage TierFrom1To2 From3to3 Video"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_Overage_TierFrom1To2_From3To3_Video
		when:
		addProductToCartOnce("P6")

		then: "generated XML contents are correct"
		verifyProductXml('P6',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC6']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>12.0</value>
			              <formattedValue>$12.00</formattedValue>
			            </price>
			            <tierEnd>2</tierEnd>
			            <tierStart>1</tierStart>
			          </tierUsageChargeEntry>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>13.0</value>
			              <formattedValue>$13.00</formattedValue>
			            </price>
			            <tierEnd>3</tierEnd>
			            <tierStart>3</tierStart>
			          </tierUsageChargeEntry>
			          <overageUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>11.0</value>
			              <formattedValue>$11.00</formattedValue>
			            </price>
			          </overageUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>video</id>
			          <namePlural>Videos</namePlural>
			          <name>Video</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC6</name>
			        <usageChargeType>
			          <code>each_respective_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit overage video minute one type"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_Overage_Video_Minute_1Type
		when:
		addProductToCartOnce("P7")

		then: "generated XML contents are correct"
		verifyProductXml('P7',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC71']",
				'''<perUnitUsageCharge>
   		        <usageChargeEntries>
   		          <overageUsageChargeEntry>
   		            <price>
   		              <currencyIso>USD</currencyIso>
   		              <priceType>BUY</priceType>
   		              <value>15.0</value>
   		              <formattedValue>$15.00</formattedValue>
   		            </price>
   		          </overageUsageChargeEntry>
   		        </usageChargeEntries>
   		        <usageUnit>
   		          <id>video</id>
   		          <namePlural>Videos</namePlural>
   		          <name>Video</name>
   		          <accumulative>false</accumulative>
   		        </usageUnit>
   		        <name>PUC71</name>
   		        <usageChargeType>
   		          <code>each_respective_tier</code>
   		        </usageChargeType>
      			</perUnitUsageCharge>''')
		verifyProductXml('P7',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC72']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <overageUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>25.0</value>
			              <formattedValue>$25.00</formattedValue>
			            </price>
			          </overageUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>minute</id>
			          <namePlural>Minutes</namePlural>
			          <name>Minute</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC72</name>
			        <usageChargeType>
			          <code>each_respective_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit overage video minute two types"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_Overage_Video_Minute_2Types
		when:
		addProductToCartOnce("P8")

		then: "generated XML contents are correct"
		verifyProductXml('P8',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC81']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <overageUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>15.0</value>
			              <formattedValue>$15.00</formattedValue>
			            </price>
			          </overageUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>video</id>
			          <namePlural>Videos</namePlural>
			          <name>Video</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC81</name>
			        <usageChargeType>
			          <code>each_respective_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
		verifyProductXml('P8',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC82']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <overageUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>25.0</value>
			              <formattedValue>$25.00</formattedValue>
			            </price>
			          </overageUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>minute</id>
			          <namePlural>Minutes</namePlural>
			          <name>Minute</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC82</name>
			        <usageChargeType>
			          <code>highest_applicable_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit TierFrom1To2 From3To4 video minute one type"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_TierFrom1To2_From3To4_Video_Minute_1Type
		when:
		addProductToCartOnce("P9")

		then: "generated XML contents are correct"
		verifyProductXml('P9',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC91']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>11.0</value>
			              <formattedValue>$11.00</formattedValue>
			            </price>
			            <tierEnd>2</tierEnd>
			            <tierStart>1</tierStart>
			          </tierUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>video</id>
			          <namePlural>Videos</namePlural>
			          <name>Video</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC91</name>
			        <usageChargeType>
			          <code>each_respective_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
		verifyProductXml('P9',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC92']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>12.0</value>
			              <formattedValue>$12.00</formattedValue>
			            </price>
			            <tierEnd>4</tierEnd>
			            <tierStart>3</tierStart>
			          </tierUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>minute</id>
			          <namePlural>Minutes</namePlural>
			          <name>Minute</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC92</name>
			        <usageChargeType>
			          <code>each_respective_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit TierFrom1To2 From3To4 video minute two type"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_TierFrom1To2_From3To4_Video_Minute_2Types
		when:
		addProductToCartOnce("P10")

		then: "generated XML contents are correct"
		verifyProductXml('P10',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC101']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>11.0</value>
			              <formattedValue>$11.00</formattedValue>
			            </price>
			            <tierEnd>2</tierEnd>
			            <tierStart>1</tierStart>
			          </tierUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>video</id>
			          <namePlural>Videos</namePlural>
			          <name>Video</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC101</name>
			        <usageChargeType>
			          <code>each_respective_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
		verifyProductXml('P10',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC102']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>12.0</value>
			              <formattedValue>$12.00</formattedValue>
			            </price>
			            <tierEnd>4</tierEnd>
			            <tierStart>3</tierStart>
			          </tierUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>minute</id>
			          <namePlural>Minutes</namePlural>
			          <name>Minute</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC102</name>
			        <usageChargeType>
			          <code>highest_applicable_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit Overage video TierFrom1To2 minute one type"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_Overage_Video_TierFrom1To2_Minute_1Type
		when:
		addProductToCartOnce("P11")

		then: "generated XML contents are correct"
		verifyProductXml('P11',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC111']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <overageUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>11.0</value>
			              <formattedValue>$11.00</formattedValue>
			            </price>
			          </overageUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>video</id>
			          <namePlural>Videos</namePlural>
			          <name>Video</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC111</name>
			        <usageChargeType>
			          <code>each_respective_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
		verifyProductXml('P11',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC112']",
				'''<perUnitUsageCharge>
   		        <usageChargeEntries>
   		          <tierUsageChargeEntry>
   		            <price>
   		              <currencyIso>USD</currencyIso>
   		              <priceType>BUY</priceType>
   		              <value>12.0</value>
   		              <formattedValue>$12.00</formattedValue>
   		            </price>
   		            <tierEnd>4</tierEnd>
   		            <tierStart>3</tierStart>
   		          </tierUsageChargeEntry>
   		        </usageChargeEntries>
   		        <usageUnit>
   		          <id>minute</id>
   		          <namePlural>Minutes</namePlural>
   		          <name>Minute</name>
   		          <accumulative>false</accumulative>
   		        </usageUnit>
   		        <name>PUC112</name>
   		        <usageChargeType>
   		          <code>each_respective_tier</code>
   		        </usageChargeType>
   		      </perUnitUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit Overage video TierFrom1To2 minute two types"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_Overage_Video_TierFrom1To2_Minute_2Type
		when:
		addProductToCartOnce("P12")

		then: "generated XML contents are correct"
		verifyProductXml('P12',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC121']",
				'''<perUnitUsageCharge>
   		        <usageChargeEntries>
   		          <overageUsageChargeEntry>
   		            <price>
   		              <currencyIso>USD</currencyIso>
   		              <priceType>BUY</priceType>
   		              <value>11.0</value>
   		              <formattedValue>$11.00</formattedValue>
   		            </price>
   		          </overageUsageChargeEntry>
   		        </usageChargeEntries>
   		        <usageUnit>
   		          <id>video</id>
   		          <namePlural>Videos</namePlural>
   		          <name>Video</name>
   		          <accumulative>false</accumulative>
   		        </usageUnit>
   		        <name>PUC121</name>
   		        <usageChargeType>
   		          <code>each_respective_tier</code>
   		        </usageChargeType>
   		      </perUnitUsageCharge>''')
		verifyProductXml('P12',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC122']",
				'''<perUnitUsageCharge>
			        <usageChargeEntries>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>12.0</value>
			              <formattedValue>$12.00</formattedValue>
			            </price>
			            <tierEnd>4</tierEnd>
			            <tierStart>3</tierStart>
			          </tierUsageChargeEntry>
			        </usageChargeEntries>
			        <usageUnit>
			          <id>minute</id>
			          <namePlural>Minutes</namePlural>
			          <name>Minute</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>PUC122</name>
			        <usageChargeType>
			          <code>highest_applicable_tier</code>
			        </usageChargeType>
			      </perUnitUsageCharge>''')
	}

}
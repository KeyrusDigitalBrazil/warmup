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
class XMLVerificationUsageChargeVolume extends AbstractSubscriptionFacadesSpockTest {

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testXMLVerificationUsageChargeVolume.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "XML Verification Usage Charge Volume Overage Video"() {
		// Test_XML_Verification_Usage_Charge_Volume_Overage_Video
		when:
		addProductToCartOnce("P1")

		then: "generated XML contents are correct"
		verifyProductXml('P1',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC1']",
				'''<volumeUsageCharge>
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
				    <name>VC1</name>
				  </volumeUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge Volume TierFrom1To1 Video"() {
		// Test_XML_Verification_Usage_Charge_Volume_TierFrom1To1_Video
		when:
		addProductToCartOnce("P2")

		then: "generated XML contents are correct"
		verifyProductXml('P2',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC2']",
				'''<volumeUsageCharge>
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
			        <name>VC2</name>
			      </volumeUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge Volume TierFrom1To2 Video"() {
		// Test_XML_Verification_Usage_Charge_Volume_TierFrom1To2_Video
		when:
		addProductToCartOnce("P3")

		then: "generated XML contents are correct"
		verifyProductXml('P3',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC3']",
				'''<volumeUsageCharge>
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
			        <name>VC3</name>
			      </volumeUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge Volume TierFrom1To2 From3to3 Video"() {
		// Test_XML_Verification_Usage_Charge_Volume_TierFrom1To2_From3To3_Video
		when:
		addProductToCartOnce("P4")

		then: "generated XML contents are correct"
		verifyProductXml('P4',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC4']",
				'''<volumeUsageCharge>
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
				    <name>VC4</name>
				  </volumeUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge Volume TierFrom1To2 From3to4 Video"() {
		// Test_XML_Verification_Usage_Charge_Volume_TierFrom1To2_From3To4_Video
		when:
		addProductToCartOnce("P5")

		then: "generated XML contents are correct"
		verifyProductXml('P5',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC5']",
				'''<volumeUsageCharge>
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
			        <name>VC5</name>
			      </volumeUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge Volume Overage TierFrom1To2 From3to3 Video"() {
		// Test_XML_Verification_Usage_Charge_Volume_Overage_TierFrom1To2_From3To3_Video
		when:
		addProductToCartOnce("P6")

		then: "generated XML contents are correct"
		verifyProductXml('P6',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC6']",
				'''<volumeUsageCharge>
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
			        <name>VC6</name>
			      </volumeUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge Volume Overage video minute"() {
		// Test_XML_Verification_Usage_Charge_Volume_Overage_Video_Minute
		when:
		addProductToCartOnce("P7")

		then: "generated XML contents are correct"
		verifyProductXml('P7',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC71']",
				'''<volumeUsageCharge>
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
			        <name>VC71</name>
			      </volumeUsageCharge>''')
		verifyProductXml('P7',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC72']",
				'''<volumeUsageCharge>
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
   		        <name>VC72</name>
   		      </volumeUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge Volume TierFrom1To2 From3To4 video minute"() {
		// Test_XML_Verification_Usage_Charge_Volume_TierFrom1To2_From3To4_Video_Minute
		when:
		addProductToCartOnce("P8")

		then: "generated XML contents are correct"
		verifyProductXml('P8',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC81']",
				'''<volumeUsageCharge>
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
      	        <name>VC81</name>
      	      </volumeUsageCharge>''')
		verifyProductXml('P8',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC82']",
				'''<volumeUsageCharge>
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
			        <name>VC82</name>
			      </volumeUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge Volume Overage video TierFrom1To2  minute"() {
		// Test_XML_Verification_Usage_Charge_Volume_Overage_Video_TierFrom1To2_Minute
		when:
		addProductToCartOnce("P9")

		then: "generated XML contents are correct"
		verifyProductXml('P9',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC91']",
				'''<volumeUsageCharge>
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
   		        <name>VC91</name>
   		      </volumeUsageCharge>''')
		verifyProductXml('P9',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC92']",
				'''<volumeUsageCharge>
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
			        <name>VC92</name>
			      </volumeUsageCharge>''')
	}

}
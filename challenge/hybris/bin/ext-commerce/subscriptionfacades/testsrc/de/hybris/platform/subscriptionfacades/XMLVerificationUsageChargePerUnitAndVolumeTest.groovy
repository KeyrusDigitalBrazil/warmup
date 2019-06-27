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
class XMLVerificationUsageChargePerUnitAndVolumeTest extends AbstractSubscriptionFacadesSpockTest {

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testXMLVerificationUsageChargePerUnitAndVolume.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "XML Verification Usage Charge PerUnit Overage Video Volume Overage Minute"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_Overage_Video_Volume_Overage_Minute
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
		verifyProductXml('P1',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC1']",
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
			        <name>VC1</name>
			      </volumeUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit Overage Video Volume TierFrom1To2 Minute"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_Overage_Video_Volume_TierFrom1To2_Minute
		when:
		addProductToCartOnce("P2")

		then: "generated XML contents are correct"
		verifyProductXml('P2',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC2']",
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
               	<name>PUC2</name>
               	<usageChargeType>
               		<code>each_respective_tier</code>
               	</usageChargeType>
               </perUnitUsageCharge>''')
		verifyProductXml('P2',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC2']",
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
               	</usageChargeEntries>
               	<usageUnit>
               		<id>minute</id>
               		<namePlural>Minutes</namePlural>
               		<name>Minute</name>
               		<accumulative>false</accumulative>
               	</usageUnit>
               	<name>VC2</name>
               </volumeUsageCharge>''')
	}

	@Test
	def "XML Verification Usage Charge PerUnit TierFrom1To2 Video Volume Overage Minute"() {
		// Test_XML_Verification_Usage_Charge_PerUnit_TierFrom1To2_Video_Volume_Overage_Minute
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
			              <value>12.0</value>
			              <formattedValue>$12.00</formattedValue>
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
		verifyProductXml('P3',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC3']",
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
			          <id>minute</id>
			          <namePlural>Minutes</namePlural>
			          <name>Minute</name>
			          <accumulative>false</accumulative>
			        </usageUnit>
			        <name>VC3</name>
			      </volumeUsageCharge>''')
	}


}
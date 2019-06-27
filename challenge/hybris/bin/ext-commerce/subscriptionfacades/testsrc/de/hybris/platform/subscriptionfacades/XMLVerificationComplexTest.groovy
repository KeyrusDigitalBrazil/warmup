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
class XMLVerificationComplexTest extends AbstractSubscriptionFacadesSpockTest {

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testXMLVerificationComplex.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "XML Verification Complex Currency USD"() {
		// Test_XML_Verification_Complex_Currency_USD
		when:
		addProductToCartOnce("P1")

		then: "generated XML contents are correct"
		verifyProductXml('P1', 'product/subscriptionTerm',
				'''<subscriptionTerm>
               	<termOfServiceFrequency>
               		<code>monthly</code>
               	</termOfServiceFrequency>
               	<name>T1</name>
               	<termOfServiceRenewal>
               		<code>auto_renewing</code>
               	</termOfServiceRenewal>
               	<billingPlan>
               		<billingCycleType>
               			<code>subscription_start</code>
               		</billingCycleType>
               		<name>Monthly Plan</name>
               		<billingTime>
               			<description>Monthly</description>
               			<orderNumber>5</orderNumber>
               			<name>Monthly</name>
               			<code>monthly</code>
               		</billingTime>
               	</billingPlan>
               	<cancellable>true</cancellable>
               	<termOfServiceNumber>10</termOfServiceNumber>
               </subscriptionTerm>''')
		verifyProductXml('P1',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC1']",
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
            			<tierEnd>6</tierEnd>
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
            	<name>PUC1</name>
            	<usageChargeType>
            		<code>each_respective_tier</code>
            	</usageChargeType>
            </perUnitUsageCharge>''')
		verifyProductXml('P1',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC1']",
				'''<volumeUsageCharge>
			        <usageChargeEntries>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>26.0</value>
			              <formattedValue>$26.00</formattedValue>
			            </price>
			            <tierEnd>5</tierEnd>
			            <tierStart>1</tierStart>
			          </tierUsageChargeEntry>
			          <tierUsageChargeEntry>
			            <price>
			              <currencyIso>USD</currencyIso>
			              <priceType>BUY</priceType>
			              <value>27.0</value>
			              <formattedValue>$27.00</formattedValue>
			            </price>
			            <tierEnd>10</tierEnd>
			            <tierStart>6</tierStart>
			          </tierUsageChargeEntry>
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
			      </volumeUsageCharge>
               ''')
	}

	@Test
	def "XML Verification Complex Currency EUR"() {
		// Test_XML_Verification_Complex_Currency_EUR
		when:
		addProductToCartOnce("P2")

		then: "generated XML contents are correct"
		verifyProductXml('P2', 'product/subscriptionTerm',
				'''<subscriptionTerm>
				    <termOfServiceFrequency>
				      <code>monthly</code>
				    </termOfServiceFrequency>
				    <name>T2</name>
				    <termOfServiceRenewal>
				      <code>non_renewing</code>
				    </termOfServiceRenewal>
				    <billingPlan>
				      <billingCycleType>
				        <code>subscription_start</code>
				      </billingCycleType>
				      <name>Monthly Plan</name>
				      <billingTime>
				        <description>Monthly</description>
				        <orderNumber>5</orderNumber>
				        <name>Monthly</name>
				        <code>monthly</code>
				      </billingTime>
				    </billingPlan>
				    <cancellable>true</cancellable>
				    <termOfServiceNumber>11</termOfServiceNumber>
				  </subscriptionTerm>
				''')
		verifyProductXml('P2',
				"product/price[@class='subscriptionPricePlan']/usageCharges/perUnitUsageCharge[name/text()='PUC2']",
				'''<perUnitUsageCharge>
				        <usageChargeEntries>
				          <tierUsageChargeEntry>
				            <price>
				              <currencyIso>EUR</currencyIso>
				              <priceType>BUY</priceType>
				              <value>2.0</value>
				              <formattedValue>E2.00</formattedValue>
				            </price>
				            <tierEnd>2</tierEnd>
				            <tierStart>1</tierStart>
				          </tierUsageChargeEntry>
				          <tierUsageChargeEntry>
				            <price>
				              <currencyIso>EUR</currencyIso>
				              <priceType>BUY</priceType>
				              <value>3.0</value>
				              <formattedValue>E3.00</formattedValue>
				            </price>
				            <tierEnd>4</tierEnd>
				            <tierStart>3</tierStart>
				          </tierUsageChargeEntry>
				          <overageUsageChargeEntry>
				            <price>
				              <currencyIso>EUR</currencyIso>
				              <priceType>BUY</priceType>
				              <value>1.0</value>
				              <formattedValue>E1.00</formattedValue>
				            </price>
				          </overageUsageChargeEntry>
				        </usageChargeEntries>
				        <usageUnit>
				          <id>usageUnitTest1</id>
				          <namePlural>usageUnitTest1</namePlural>
				          <name>usageUnitTest1</name>
				          <accumulative>false</accumulative>
				        </usageUnit>
				        <name>PUC2</name>
				        <usageChargeType>
				          <code>highest_applicable_tier</code>
				        </usageChargeType>
				      </perUnitUsageCharge>''')
		verifyProductXml('P2',
				"product/price[@class='subscriptionPricePlan']/usageCharges/volumeUsageCharge[name/text()='VC2']",
				'''<volumeUsageCharge>
				        <usageChargeEntries>
				          <tierUsageChargeEntry>
				            <price>
				              <currencyIso>EUR</currencyIso>
				              <priceType>BUY</priceType>
				              <value>6.0</value>
				              <formattedValue>E6.00</formattedValue>
				            </price>
				            <tierEnd>7</tierEnd>
				            <tierStart>5</tierStart>
				          </tierUsageChargeEntry>
				          <tierUsageChargeEntry>
				            <price>
				              <currencyIso>EUR</currencyIso>
				              <priceType>BUY</priceType>
				              <value>7.0</value>
				              <formattedValue>E7.00</formattedValue>
				            </price>
				            <tierEnd>11</tierEnd>
				            <tierStart>8</tierStart>
				          </tierUsageChargeEntry>
				          <overageUsageChargeEntry>
				            <price>
				              <currencyIso>EUR</currencyIso>
				              <priceType>BUY</priceType>
				              <value>5.0</value>
				              <formattedValue>E5.00</formattedValue>
				            </price>
				          </overageUsageChargeEntry>
				        </usageChargeEntries>
				        <usageUnit>
				          <id>textMessage</id>
				          <namePlural>Text Message</namePlural>
				          <name>Text Message</name>
				          <accumulative>false</accumulative>
				        </usageUnit>
				        <name>VC2</name>
				      </volumeUsageCharge>
               ''')
	}
}
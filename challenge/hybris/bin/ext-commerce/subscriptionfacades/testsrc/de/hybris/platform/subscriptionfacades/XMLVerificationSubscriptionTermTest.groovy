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
class XMLVerificationSubscriptionTermTest extends AbstractSubscriptionFacadesSpockTest {

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testXMLVerificationSubscriptionTerm.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "Test XML Verification Subscription Term AutoRenewing Monthly"() {
		// Test_XML_Verification_Subscription_Term_AutoRenewing_Monthly
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
	}
	@Test
	def "Test XML Verification Subscription Term AutoRenewing Daily"() {
		// Test_XML_Verification_Subscription_Term_AutoRenewing_Daily
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
               	<termOfServiceNumber>11</termOfServiceNumber>
               </subscriptionTerm>''')
	}

	@Test
	def "Test XML Verification Subscription Term AutoRenewing Weekly"() {
		// Test_XML_Verification_Subscription_Term_AutoRenewing_Weekly
		when:
		addProductToCartOnce("P3")

		then: "generated XML contents are correct"
		verifyProductXml('P3', 'product/subscriptionTerm',
				'''<subscriptionTerm>
               	<termOfServiceFrequency>
               		<code>monthly</code>
               	</termOfServiceFrequency>
               	<name>T3</name>
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
               	<termOfServiceNumber>12</termOfServiceNumber>
               </subscriptionTerm>''')
	}

	@Test
	def "Test XML Verification Subscription Term NonRenewing Daily"() {
		// Test_XML_Verification_Subscription_Term_NonRenewing_Daily
		when:
		addProductToCartOnce("P4")

		then: "generated XML contents are correct"
		verifyProductXml('P4', 'product/subscriptionTerm',
				'''<subscriptionTerm>
               	<termOfServiceFrequency>
               		<code>monthly</code>
               	</termOfServiceFrequency>
               	<name>T4</name>
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
               </subscriptionTerm>''')
	}

	@Test
	def "Test XML Verification Subscription Term NonRenewing Weekly"() {
		// Test_XML_Verification_Subscription_Term_NonRenewing_Weeky
		when:
		addProductToCartOnce("P5")

		then: "generated XML contents are correct"
		verifyProductXml('P5', 'product/subscriptionTerm',
				'''<subscriptionTerm>
               	<termOfServiceFrequency>
               		<code>monthly</code>
               	</termOfServiceFrequency>
               	<name>T5</name>
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
               	<termOfServiceNumber>12</termOfServiceNumber>
               </subscriptionTerm>''')
	}

	@Test
	def "Test XML Verification Subscription Term RenewsOnce Daily"() {
		// Test_XML_Verification_Subscription_Term_RenewsOnce_Daily
		when:
		addProductToCartOnce("P6")

		then: "generated XML contents are correct"
		verifyProductXml('P6', 'product/subscriptionTerm',
				'''<subscriptionTerm>
               	<termOfServiceFrequency>
               		<code>monthly</code>
               	</termOfServiceFrequency>
               	<name>T6</name>
               	<termOfServiceRenewal>
               		<code>renews_once</code>
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
               </subscriptionTerm>''')
	}

	@Test
	def "Test XML Verification Subscription Term RenewsOnce Weekly"() {
		// Test_XML_Verification_Subscription_Term_RenewsOnce_Weekly
		when:
		addProductToCartOnce("P7")

		then: "generated XML contents are correct"
		verifyProductXml('P7', 'product/subscriptionTerm',
				'''<subscriptionTerm>
               	<termOfServiceFrequency>
               		<code>monthly</code>
               	</termOfServiceFrequency>
               	<name>T7</name>
               	<termOfServiceRenewal>
               		<code>renews_once</code>
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
               	<termOfServiceNumber>12</termOfServiceNumber>
               </subscriptionTerm>''')
	}

}
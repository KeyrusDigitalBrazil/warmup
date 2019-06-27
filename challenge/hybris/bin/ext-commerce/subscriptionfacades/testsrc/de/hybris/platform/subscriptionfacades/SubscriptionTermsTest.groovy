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
class SubscriptionTermsTest extends AbstractSubscriptionFacadesSpockTest {

	// is run before every test
	def setup() {
		importCsv("/subscriptionfacades/tests/testCommerceCart.csv", "utf-8")
		importCsv("/subscriptionfacades/tests/testSubscriptionTerms.impex", "utf-8")
		prepareSession("testSite")
		prepareCurrency("USD")
	}

	// is run after every test
	def cleanup() {
		resetSystemAttributes()
		resetCurrency()
	}

	@Test
	def "Test Subscription Terms Default For Cancellable"() {
		// Test_Subscription_Terms_Default_For_Cancellable
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
               	<!-- billing plan is defined in resources/impex/testCommerceCart.csv -->
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
               	<!-- cancellable=true is defined as the default in resources/impex-templates/subscription-term/subscriptionterm-insert.impex.vm -->
               	<cancellable>true</cancellable>
               	<termOfServiceNumber>10</termOfServiceNumber>
               </subscriptionTerm>''')
	}

	@Test
	def "Test Subscription Terms Not Cancellable"() {
		// Test_Subscription_Terms_Not_Cancellable
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
               	<!-- billing plan is defined in resources/impex/testCommerceCart.csv -->
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
               	<!-- cancellable=true is defined as the default in resources/impex-templates/subscription-term/subscriptionterm-insert.impex.vm -->
               	<cancellable>false</cancellable>
               	<termOfServiceNumber>10</termOfServiceNumber>
               </subscriptionTerm>''')
	}

	@Test
	def "Test Subscription Terms Custom Billing Plan"() {
		// Test_Subscription_Terms_Custom_BillingPlan
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
               	<!-- billing plan is defined in resources/impex/testCommerceCart.csv -->
               	<billingPlan>
               		<billingCycleDay>3</billingCycleDay>
               		<billingCycleType>
               			<code>day_of_month</code>
               		</billingCycleType>
               		<name>BP1_MONTHLY</name>
               		<billingTime>
               			<description>Monthly</description>
               			<orderNumber>5</orderNumber>
               			<name>Monthly</name>
               			<code>monthly</code>
               		</billingTime>
               	</billingPlan>
               	<!-- cancellable=true is defined as the default in resources/impex-templates/subscription-term/subscriptionterm-insert.impex.vm -->
               	<cancellable>false</cancellable>
               	<termOfServiceNumber>10</termOfServiceNumber>
               </subscriptionTerm>''')
	}


}
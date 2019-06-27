/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.consents
import de.hybris.bootstrap.annotations.ManualTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.AbstractUserTest

import org.springframework.http.HttpHeaders;
import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.*


@Unroll
@ManualTest
class ConsentResourcesTest extends AbstractUserTest {

	private static final String CUSTOMER_USERNAME = 'democustomer1@hybris.com'
	private static final String CUSTOMER_PASSWORD = 'PAss1234!'
	private static final String CUSTOMER_FIRST_NAME = 'Demo'
	private static final String CUSTOMER_LAST_NAME = 'Consent Customer1'
	
 	def "Fetch the list of consent templates for anonymous user: #format"(){
 		given: "a trusted client"
 		authorizeClient(restClient)

 		when: "anonymous (not logged in) user wants to fetch the list of consent templates"
 		HttpResponseDecorator response = restClient.get(
 				path: getBasePathWithSite() + '/users/anonymous/consenttemplates',
 				contentType: format,
 				requestContentType: URLENC
 				)

 		then: "he retrieves the list of consent templates without consent infomation"
 		with(response){
 			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
 			status == SC_OK
 			data.consentTemplates.size()==2
 			data.consentTemplates[0].id == 'CONSENT_TEMPLATE_2'
 			data.consentTemplates[0].version == 2
 			data.consentTemplates[0].name == 'Newsletter Subscription Consent 2'
 			data.consentTemplates[0].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
 			isEmpty(data.consentTemplates[0].consent)
 			data.consentTemplates[1].id == 'CONSENT_TEMPLATE_3'
 			data.consentTemplates[1].version == 0
 			data.consentTemplates[1].name == 'Newsletter Subscription Consent 3'
 			data.consentTemplates[1].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
 			isEmpty(data.consentTemplates[1].consent)
 		}

 		where:
 		format <<[XML, JSON]
 	}

 	def "Fetch the list of consent templates for login user who has not given any consent yet: #format"(){
 		given: "a trusted client"
 		authorizeTrustedClient(restClient)
 		def customer = registerAndAuthorizeCustomer(restClient, format)

 		when: "logged in user wants to fetch the list of consent templates"
 		HttpResponseDecorator response = restClient.get(
 				path: getBasePathWithSite() + '/users/' + customer.id + '/consenttemplates',
 				contentType: format,
 				requestContentType: URLENC
 				)

 		then: "he retrieves the list of consent templates without consent infomation"
 		with(response){
 			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
 			status == SC_OK
			data.consentTemplates.size()==5
 			data.consentTemplates[0].id == 'CONSENT_TEMPLATE_1'
 			data.consentTemplates[0].version == 2
 			data.consentTemplates[0].name == 'Newsletter Subscription Consent 1'
 			data.consentTemplates[0].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
 			isEmpty(data.consentTemplates[0].consent)
 			data.consentTemplates[1].id == 'CONSENT_TEMPLATE_2'
 			data.consentTemplates[1].version == 2
 			data.consentTemplates[1].name == 'Newsletter Subscription Consent 2'
 			data.consentTemplates[1].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
 			isEmpty(data.consentTemplates[1].consent)
 			data.consentTemplates[2].id == 'CONSENT_TEMPLATE_3'
 			data.consentTemplates[2].version == 0
 			data.consentTemplates[2].name == 'Newsletter Subscription Consent 3'
 			data.consentTemplates[2].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
 			isEmpty(data.consentTemplates[2].consent)
			data.consentTemplates[3].id == 'CONSENT_TEMPLATE_4'
			data.consentTemplates[3].version == 0
			data.consentTemplates[3].name == 'Newsletter Subscription Consent 4'
			data.consentTemplates[3].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
			isEmpty(data.consentTemplates[3].consent)
			data.consentTemplates[4].id == 'CONSENT_TEMPLATE_5'
			data.consentTemplates[4].version == 0
			data.consentTemplates[4].name == 'Newsletter Subscription Consent 5'
			data.consentTemplates[4].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
			isEmpty(data.consentTemplates[4].consent)
 		}

 		where:
 		format <<[XML, JSON]
 	}
 	
 	def "Fetch the exposed consent template for anonymous user: #format"(){
 		given: "a trusted client"
 		authorizeClient(restClient)

 		when: "anonymous (not logged in) user wants to fetch the consent template"
 		HttpResponseDecorator response = restClient.get(
 				path: getBasePathWithSite() + '/users/anonymous/consenttemplates/CONSENT_TEMPLATE_2',
 				contentType: format,
 				requestContentType: URLENC
 				)

 		then: "he retrieves the consent template without consent infomation"
 		with(response){
 			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
 			status == SC_OK
 			data.id == 'CONSENT_TEMPLATE_2'
 			data.version == 2
 			data.name == 'Newsletter Subscription Consent 2'
 			data.description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
 			isEmpty(data.consent)
 		}

 		where:
 		format <<[XML, JSON]
 	}
 	
 	def "Fetch the not exposed consent template for anonymous user: #format"(){
 		given: "a trusted client"
 		authorizeClient(restClient)

 		when: "anonymous (not logged in) user wants to fetch the consent template"
 		HttpResponseDecorator response = restClient.get(
 				path: getBasePathWithSite() + '/users/anonymous/consenttemplates/CONSENT_TEMPLATE_1',
 				contentType: format,
 				requestContentType: URLENC
 				)

 		then: "he gets not found error"
 		with(response){
 			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
 			status == SC_NOT_FOUND
 			data.errors[0].type == 'NotFoundError'
 			data.errors[0].message == "This consent template is not exposed to anonymous user"
 		}

 		where:
 		format <<[XML, JSON]
 	}
 	
 	def "Fetch the consent template for login user who has not given any consent yet: #format"(){
 		given: "a trusted client"
 		authorizeTrustedClient(restClient)
 		def customer = registerAndAuthorizeCustomer(restClient, format)

 		when: "anonymous (not logged in) user wants to fetch the consent template"
 		HttpResponseDecorator response = restClient.get(
 				path: getBasePathWithSite() + '/users/' + customer.id + '/consenttemplates/CONSENT_TEMPLATE_1',
 				contentType: format,
 				requestContentType: URLENC
 				)

 		then: "he retrieves the consent template without consent infomation"
 		with(response){
 			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
 			status == SC_OK
 			data.id == 'CONSENT_TEMPLATE_1'
 			data.version == 2
 			data.name == 'Newsletter Subscription Consent 1'
 			data.description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
 			isEmpty(data.consent)
 		}

 		where:
 		format <<[XML, JSON]
 	}
 	
 	def "Fetch the non-exist consent template for anonymous user: #format"(){
 		given: "a trusted client"
 		authorizeClient(restClient)

 		when: "anonymous (not logged in) user wants to fetch the non-exist consent template"
 		HttpResponseDecorator response = restClient.get(
 				path: getBasePathWithSite() + '/users/anonymous/consenttemplates/no_exist',
 				contentType: format,
 				requestContentType: URLENC
 				)

 		then: "he gets error information"
 		with(response){
 			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
 			status == SC_BAD_REQUEST
 			data.errors[0].type == 'UnknownIdentifierError'
 			data.errors[0].message == "No result for the given query"
 		}

 		where:
 		format <<[XML, JSON]
 	}

 	def "Can give a consent for logged in user: #format"(){
 		given: "a trusted client"
 		authorizeTrustedClient(restClient)
 		def customer = registerAndAuthorizeCustomer(restClient, format)
 		def pathPrefix = getBasePathWithSite() + '/users/' + customer.id

 		when: "logged in user wants to give a consent"
 		HttpResponseDecorator response = restClient.post(
 				path: pathPrefix + '/consents',
 				contentType: format,
 				requestContentType: URLENC,
 				query: ['consentTemplateId': "CONSENT_TEMPLATE_1", 'consentTemplateVersion' : 2]
 		)

 		then: "he retrieves a consent template with a given date"
 		with(response){
 			status == SC_CREATED
 			data.id == 'CONSENT_TEMPLATE_1'
 			data.version == 2
 			data.name == 'Newsletter Subscription Consent 1'
 			data.description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
 			isNotEmpty(data.currentConsent)
 			isNotEmpty(data.currentConsent.code)
 			isNotEmpty(data.currentConsent.consentGivenDate)
 			isEmpty(data.currentConsent.consentWithdrawnDate)
 			response.containsHeader(HttpHeaders.LOCATION)
            response.getFirstHeader(HttpHeaders.LOCATION).getValue().endsWith(pathPrefix + '/consenttemplates/CONSENT_TEMPLATE_1')
 		}
 		
 		where:
 		format <<[XML, JSON]
 	}

 	def "Can't give a consent when the consent was already given: #format"(){
 		given: "a trusted client"
 		authorizeTrustedClient(restClient)
 		def customer = registerAndAuthorizeCustomer(restClient, format)

 		when: "logged in user wants to give a consent when a consent was already given"
 		restClient.post(
 				path: getBasePathWithSite() + '/users/' + customer.id + '/consents',
 				contentType: URLENC,
 				requestContentType: URLENC,
 				query: ['consentTemplateId': "CONSENT_TEMPLATE_1", 'consentTemplateVersion' : 2]
 		)

 		HttpResponseDecorator response = restClient.post(
 				path: getBasePathWithSite() + '/users/' + customer.id + '/consents',
 				contentType: format,
 				requestContentType: URLENC,
 				query: ['consentTemplateId': "CONSENT_TEMPLATE_1", 'consentTemplateVersion' : 2]
 		)
 		then: "he gets an error"
 		with(response){
 			status == SC_CONFLICT
 		}
 		
 		where:
 		format <<[XML, JSON]
 	}
 	def "Anonymous user can't give a consent: #format"(){
 		given: "a trusted client"
 		authorizeClient(restClient)
 		when: "anonymous (not logged in) user wants to give a consent"
 		HttpResponseDecorator response = restClient.post(
 				path: getBasePathWithSite() + '/users/anonymous/consents',
 				contentType: format,
 				requestContentType: URLENC,
 				query: ['consentTemplateId': "CONSENT_TEMPLATE_1", 'consentTemplateVersion' : 2]
 		)
 		then: "he gets an error"
 		with(response){
 			status == SC_FORBIDDEN
 		}
 		
		where:
		format <<[XML, JSON]
	}

	/**
	 * This method creates new mock customer with the given username and password.
	 *
	 * @param username the mock username
	 * @param password the mock password
	 * @return created mock customer
	 */
	private getCustomer(String username, String password) {
		return [
			'id': username,
			'password': password
		]
	}

	/**
	 * This method enables a mock customer to give consent to use the consent for the withdraw consent test case.
	 *
	 * @param username the mock username
	 * @param password the mock password
	 * @return created mock customer
	 */
	private giveConsent(customer, String consentTemplateId, Integer version)
	{
	authorizeClient(restClient)
	authorizeCustomer(restClient, customer)
    		def response = restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consents',
				contentType: JSON,
				requestContentType: URLENC,
 				query: ['consentTemplateId': consentTemplateId, 'consentTemplateVersion' : version]
				)

		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_CREATED
		}

		return response.data
	}

	def "Withdraw consent using the given consent code for a non anonymous user: #format"(){
		given: "a trusted client"
		authorizeClient(restClient)
		def customer = getCustomer(CUSTOMER_USERNAME, CUSTOMER_PASSWORD)
		def consent = giveConsent(customer, "CONSENT_TEMPLATE_3", 0)
		authorizeCustomer(restClient, customer)

		when: "non anonymous user wants to withdraw consent using the consent code"
		HttpResponseDecorator response = restClient.delete(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consents/' + consent.currentConsent.code,
				contentType: format,
				requestContentType: URLENC
				)
				
		then: "user withdraws consent successfully for the given consent code with xml and json response"
		with(response){
			status == SC_OK
		}

		where:
		format <<[XML, JSON]
	}

	def "Withdraw consent using the given consent code for an anonymous user: #format"(){
		given: "a trusted client"
		authorizeClient(restClient)

		when: "anonymous user wants to withdraw consent using the consent code"
		HttpResponseDecorator response = restClient.delete(
				path: getBasePathWithSite() + '/users/anonymous/consents/consent0',
				contentType: format,
				requestContentType: URLENC
				)

		then: "anonymous user cannot withdraw consent"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_UNAUTHORIZED
			data.errors[0].type == 'AccessDeniedError'
		}

 		where:
 		format <<[XML, JSON]
 	}
	
	def "Withdraw a consent that is already withdrawn using the given consent code for a non anonymous user: #format"(){
		given: "a trusted client"
		authorizeClient(restClient)
		def customer = getCustomer("democustomer2@hybris.com", CUSTOMER_PASSWORD)

		authorizeCustomer(restClient, customer)

		when: "non anonymous user tries to withdraw a consent that is already withdrawn using the consent code"
		HttpResponseDecorator response = restClient.delete(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consents/consent3',
				contentType: format,
				requestContentType: URLENC
				)

		then: "Consent is already withdrawn"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_BAD_REQUEST
			data.errors[0].type == 'ConsentWithdrawnError'
		}

		where:
		format <<[XML, JSON]
	}

	def "Withdraw a consent not associated with a non anonymous user: #format"(){
		given: "a trusted client"
		authorizeClient(restClient)
		def customer = getCustomer(CUSTOMER_USERNAME, CUSTOMER_PASSWORD)

		authorizeCustomer(restClient, customer)

		when: "non anonymous user tries to withdraw a consent that is not associated"
		HttpResponseDecorator response = restClient.delete(
				path: getBasePathWithSite() + '/users/' + customer.id +'/consents/consent2',
				contentType: format,
				requestContentType: URLENC
				)

		then: "Consent does not exist error"
		with(response){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_UNAUTHORIZED
			data.errors[0].type == 'AccessDeniedError'
		}

		where:
		format <<[XML, JSON]
	}

	def "Fetch the consent template for login user who has give some consents: #format"() {
		given: "a trusted client who give a consent"
		authorizeTrustedClient(restClient)
		def customer = registerAndAuthorizeCustomer(restClient, format)

		restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consents',
				contentType: format,
				requestContentType: URLENC,
 				query: ['consentTemplateId': "CONSENT_TEMPLATE_1", 'consentTemplateVersion' : 2]
				)

		restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consents',
				contentType: format,
				requestContentType: URLENC,
 				query: ['consentTemplateId': "CONSENT_TEMPLATE_3", 'consentTemplateVersion' : 0]
				)

		when: "he retrieves the list of consent templates with consent infomation"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consenttemplates/',
				contentType: format,
				requestContentType: URLENC
				)

		then: "he retrieves the list of consent templates with consent infomation(given)"
		with(response){
			status == SC_OK
			data.consentTemplates.size()==5
			data.consentTemplates[0].id == 'CONSENT_TEMPLATE_1'
			data.consentTemplates[0].version == 2
			data.consentTemplates[0].name == 'Newsletter Subscription Consent 1'
			data.consentTemplates[0].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
			isNotEmpty(data.consentTemplates[0].currentConsent)
			isNotEmpty(data.consentTemplates[0].currentConsent.code)
			isNotEmpty(data.consentTemplates[0].currentConsent.consentGivenDate)
			isEmpty(data.consentTemplates[0].currentConsent.consentWithdrawnDate)// make sure the consent wasn't withdrawn

			data.consentTemplates[1].id == 'CONSENT_TEMPLATE_2'
			data.consentTemplates[1].version == 2
			data.consentTemplates[1].name == 'Newsletter Subscription Consent 2'
			data.consentTemplates[1].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
			isEmpty(data.consentTemplates[1].currentConsent)

			data.consentTemplates[2].id == 'CONSENT_TEMPLATE_3'
			data.consentTemplates[2].version == 0
			data.consentTemplates[2].name == 'Newsletter Subscription Consent 3'
			data.consentTemplates[2].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
			isNotEmpty(data.consentTemplates[2].currentConsent)
			isNotEmpty(data.consentTemplates[2].currentConsent.code)
			isNotEmpty(data.consentTemplates[2].currentConsent.consentGivenDate)
			isEmpty(data.consentTemplates[2].currentConsent.consentWithdrawnDate)// make sure the consent wasn't withdrawn

			data.consentTemplates[3].id == 'CONSENT_TEMPLATE_4'
			data.consentTemplates[3].version == 0
			data.consentTemplates[3].name == 'Newsletter Subscription Consent 4'
			data.consentTemplates[3].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
			isEmpty(data.consentTemplates[3].consent)

			data.consentTemplates[4].id == 'CONSENT_TEMPLATE_5'
			data.consentTemplates[4].version == 0
			data.consentTemplates[4].name == 'Newsletter Subscription Consent 5'
			data.consentTemplates[4].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
			isEmpty(data.consentTemplates[4].consent)

		}

		where:
		format <<[XML, JSON]
	}

	def "Fetch the consent template for login user who has withdraw a consent: #format"() {
		given: "a trusted client who give a consent and witdraw it after"
		authorizeTrustedClient(restClient)
		def customer = registerAndAuthorizeCustomer(restClient, format)

		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consents',
				contentType: format,
				requestContentType: URLENC,
 				query: ['consentTemplateId': "CONSENT_TEMPLATE_1", 'consentTemplateVersion' : 2]
				)

		restClient.post(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consents',
				contentType: format,
				requestContentType: URLENC,
 				query: ['consentTemplateId': "CONSENT_TEMPLATE_3", 'consentTemplateVersion' : 0]
				)
		def consentCode = response.data.currentConsent.code

		restClient.delete(
				path: getBasePathWithSite() + '/users/' + customer.id +'/consents/' + consentCode,
				contentType: format,
				requestContentType: URLENC
				)

		when: "he retrieves the consent templates with consent infomation"
		response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consenttemplates/',
				contentType: format,
				requestContentType: URLENC
				)

		then: "he retrieves the list of consent templates with consent infomation(given and withdraw)"
		with(response){
			status == SC_OK
			data.consentTemplates.size()==5
			data.consentTemplates[0].id == 'CONSENT_TEMPLATE_1'
			data.consentTemplates[0].version == 2
			data.consentTemplates[0].name == 'Newsletter Subscription Consent 1'
			data.consentTemplates[0].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
			isNotEmpty(data.consentTemplates[0].currentConsent)
			isNotEmpty(data.consentTemplates[0].currentConsent.code)
			isNotEmpty(data.consentTemplates[0].currentConsent.consentGivenDate)
			isNotEmpty(data.consentTemplates[0].currentConsent.consentWithdrawnDate)

			data.consentTemplates[1].id == 'CONSENT_TEMPLATE_2'
			data.consentTemplates[1].version == 2
			data.consentTemplates[1].name == 'Newsletter Subscription Consent 2'
			data.consentTemplates[1].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
			isEmpty(data.consentTemplates[1].currentConsent)

			data.consentTemplates[2].id == 'CONSENT_TEMPLATE_3'
			data.consentTemplates[2].version == 0
			data.consentTemplates[2].name == 'Newsletter Subscription Consent 3'
			data.consentTemplates[2].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
			isNotEmpty(data.consentTemplates[2].currentConsent)
			isNotEmpty(data.consentTemplates[2].currentConsent.code)
			isNotEmpty(data.consentTemplates[2].currentConsent.consentGivenDate)
			isEmpty(data.consentTemplates[2].currentConsent.consentWithdrawnDate)

			data.consentTemplates[3].id == 'CONSENT_TEMPLATE_4'
			data.consentTemplates[3].version == 0
			data.consentTemplates[3].name == 'Newsletter Subscription Consent 4'
			data.consentTemplates[3].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
			isEmpty(data.consentTemplates[3].consent)

			data.consentTemplates[4].id == 'CONSENT_TEMPLATE_5'
			data.consentTemplates[4].version == 0
			data.consentTemplates[4].name == 'Newsletter Subscription Consent 5'
			data.consentTemplates[4].description == 'I approve to use my personal data for receiving e-mail newsletters for Marketing campaigns.'
			isEmpty(data.consentTemplates[4].consent)
		}

		where:
		format <<[XML, JSON]
	}
	
	/**
	 * This method enables a mock customer to withdraw consent
	 *
	 * @param username the mock username
	 * @param password the mock password
	 * @return created mock customer
	 */
	private withdrawConsent(customer,consentCode)
	{
	authorizeClient(restClient)
	authorizeCustomer(restClient, customer)
	
    		HttpResponseDecorator response = restClient.delete(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consents/' + consentCode,
				contentType: JSON,
				requestContentType: URLENC
				)
		with(response){
		if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
		}
		
		return response.data
	}
	
	def "Give a user consent and fetch the user consent through the get user consent api : #format"() {
	         given: "a trusted client"
		authorizeClient(restClient)
		def customer = getCustomer("democustomer3@hybris.com", CUSTOMER_PASSWORD)
		def consent = giveConsent(customer,"CONSENT_TEMPLATE_4", 0)
		
		authorizeCustomer(restClient, customer)
		
		when: "non anonymous user retrieves user consent details for a given consent using the giveConsent api"
		HttpResponseDecorator getConsentResponse = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consenttemplates/' + consent.id,
				contentType: format,
				requestContentType: URLENC
				)
				// consent is withdrawn to avoid 409 response for test cases
				withdrawConsent(customer,consent.currentConsent.code)

		then: "user retrieves given consent details with xml and json response"
		with(getConsentResponse){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			data.id == consent.id
			data.version == consent.version
			data.name == consent.name
			data.description == consent.description
			isNotEmpty(data.currentConsent)
			isNotEmpty(data.currentConsent.consentGivenDate)
		}
		
		where:
		format <<[XML,JSON]                                                             
	                                                                                           
	  }
	  
	   def "Withdraw a user consent and fetch the withdrawn user consent details through the get user consent api : #format"() {
	         given: "a trusted client"
		authorizeClient(restClient)
		def customer = getCustomer("democustomer3@hybris.com", CUSTOMER_PASSWORD)
		def givenConsent = giveConsent(customer,"CONSENT_TEMPLATE_4", 0)
		
		withdrawConsent(customer,givenConsent.currentConsent.code)
		
		authorizeCustomer(restClient, customer)
		
		when: "non anonymous user retrieves user consent details for a consent withdrawn using the giveConsent api"
		HttpResponseDecorator getConsentResponse = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consenttemplates/' + givenConsent.id,
				contentType: format,
				requestContentType: URLENC
				)

		then: "user retrieves withdrawn consent successfully for the given consent code with xml and json response"
		with(getConsentResponse){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			data.id == givenConsent.id
			data.version == givenConsent.version
			data.name == givenConsent.name
			data.description == givenConsent.description
			isNotEmpty(data.currentConsent)
			data.currentConsent.code == givenConsent.currentConsent.code
			isNotEmpty(data.currentConsent.consentGivenDate)
			isNotEmpty(data.currentConsent.consentWithdrawnDate)
		}
		
		where:
		format <<[XML,JSON]                                                                                                                                                
	  }
	  
	  def "Give consent to a withdrawn user consent and fetch the user consent details through the get user consent api : #format"() {
	         given: "a trusted client"
		authorizeClient(restClient)
		def customer = getCustomer("democustomer3@hybris.com", CUSTOMER_PASSWORD)
		def givenConsent = giveConsent(customer,"CONSENT_TEMPLATE_4", 0)
		withdrawConsent(customer,givenConsent.currentConsent.code)
		
		def giveSameConsent = giveConsent(customer, givenConsent.id, givenConsent.version)
		
		authorizeCustomer(restClient, customer)
		
		when: "non anonymous user retrieves user consent details for a consent given after the same consent was withdrawn using the giveConsent api"
		HttpResponseDecorator getConsentResponse = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/consenttemplates/' + giveSameConsent.id,
				contentType: format,
				requestContentType: URLENC
				)
				
				// consent is withdrawn to avoid 409 response for test cases
		withdrawConsent(customer,giveSameConsent.currentConsent.code)

		then: "user retrieves consent successfully for the given consent code with xml and json response"
		with(getConsentResponse){
			if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
			status == SC_OK
			data.id == giveSameConsent.id
			data.version == giveSameConsent.version
			data.name == giveSameConsent.name
			data.description == giveSameConsent.description
			isNotEmpty(data.currentConsent)
			data.currentConsent.code == giveSameConsent.currentConsent.code
			isNotEmpty(data.currentConsent.consentGivenDate)
			isEmpty(data.currentConsent.consentWithdrawnDate)
		}
		
		where:
		format <<[XML,JSON]                                                                                                                                          
	  }

	def "A customer tries to get consents of another customer using the customer's OAuth token: #format"(){
		given: "trusted clients"

		authorizeClient(restClient)
		def customer1 = registerCustomer(restClient, format)
		def userToken1 = getOAuth2TokenUsingPassword(restClient, getClientId(), getClientSecret(), customer1.id, customer1.password,)

		def customer2 = registerCustomer(restClient, format)
		// use a token from a different user
		restClient.getHeaders().put('Authorization', ' Bearer ' + userToken1.access_token)

		when: "a customer2 want's to use a token from another user to retrieve his consents"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer2.id + '/consenttemplates',
				contentType: format,
				requestContentType: URLENC
		)

		then: "he receives 403 forbidden error"
		with(response){
			status == SC_FORBIDDEN
			data.errors[0].type == "ForbiddenError"
		}
		where:
		format <<[XML, JSON]
	}

	def "A customer tries to get consents of another customer: #format"() {
		given: "trusted clients"

		authorizeClient(restClient)
		def customer1 = registerCustomer(restClient, format)

		// register another customer, and try to access the data from customer1
		registerAndAuthorizeCustomer(restClient, format)

		when: "a customer wants to retrieve consents of another customer"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + customer1.id + '/consenttemplates',
				contentType: format,
				requestContentType: URLENC
		)

		then: "he receives 403 forbidden error"
		with(response) {
			status == SC_FORBIDDEN
			data.errors[0].type == "ForbiddenError"
		}
		where:
		format << [XML, JSON]
	}
}

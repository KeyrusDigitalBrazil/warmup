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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.access

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.Method.DELETE
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.HEAD
import static groovyx.net.http.Method.PATCH
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_FORBIDDEN
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

import de.hybris.bootstrap.annotations.ManualTest

import spock.lang.Ignore
import spock.lang.Shared
import groovyx.net.http.RESTClient

@Ignore
@ManualTest
class AccessRightsTest extends AbstractSpockAccessRightsTest {

	@Shared
	private customer
	@Shared
	private customerCart
	@Shared
	private anonymousCart
	@Shared
	private customerAddress

	def setupSpec() {
		RESTClient restClient = createRestClient()
		authorizeTrustedClient(restClient)

		customer = registerCustomer(restClient)
		anonymousCart = createAnonymousCart(restClient)
		customerCart = createCustomerCart(restClient, customer)
		customerAddress = createCustomerAddress(restClient, customer)

		restClient.shutdown()
	}


	def createCustomerCart(RESTClient client, customer, format = JSON) {
		customerCart = returningWith(client.post(
				path: basePathWithSite + '/users/' + customer.id + '/carts',
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: URLENC), {
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_OK
				}).data
	}

	def createAnonymousCart(RESTClient client, format = JSON) {
		anonymousCart = returningWith(client.post(
				path: basePathWithSite + '/users/anonymous/carts',
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: URLENC), {
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_OK
				}).data
	}

	def createCustomerAddress(RESTClient client, customer, format = JSON) {
		customerAddress = client.post(
				path: basePathWithSite + '/users/' + customer.id + '/addresses',
				body: [
					'titleCode': CUSTOMER_TITLE_CODE,
					'firstName': CUSTOMER_FIRST_NAME,
					'lastName': CUSTOMER_LAST_NAME,
					'line1': CUSTOMER_ADDRESS_LINE1,
					'line2': CUSTOMER_ADDRESS_LINE2,
					'postalCode': CUSTOMER_ADDRESS_POSTAL_CODE,
					'town': CUSTOMER_ADDRESS_TOWN,
					'country.isocode': CUSTOMER_ADDRESS_COUNTRY_ISO_CODE,
					'fields': FIELD_SET_LEVEL_FULL
				],
				contentType: format,
				requestContentType: URLENC)
	}

	@Override
	protected void authorizeCustomer(RESTClient client) {
		super.authorizeCustomer(client, customer)
	}

	@Override
	protected void authorizeGuest(RESTClient client) {
		authorizeClient(client)
	}

	@Override
	protected getRequests() {
		[
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/cardtypes',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/catalogs',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/catalogs/wsTestProductCatalog',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/catalogs/wsTestProductCatalog/Online/',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/catalogs/wsTestProductCatalog/Online/categories/brands',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/currencies',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/customergroups',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [POST],
				requestArgs: [
					path: getBasePathWithSite() + '/customergroups',
					body: [
						'groupId' : System.currentTimeMillis() + "_customerGroup"
					],
					contentType : JSON,
					requestContentType : URLENC
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/customergroups/regulargroup',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [PUT],
				requestArgs: [
					path: getBasePathWithSite() + '/customergroups/regulargroup/members',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [PATCH],
				requestArgs: [
					path: getBasePathWithSite() + '/customergroups/regulargroup/members',
					body:
					[
						members: System.currentTimeMillis()
					],
					contentType : JSON,
					requestContentType : URLENC
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [DELETE],
				requestArgs: [
					path: getBasePathWithSite() + '/customergroups/regulargroup/members/' + System.currentTimeMillis(),
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/deliverycountries',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/export/products',
					query: ['pageSize': '1']
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/feeds/orders/statusfeed',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [POST],
				requestArgs: [
					path: getBasePathWithSite() + '/forgottenpasswordtokens',
					body: [
						'userId' : customer.id
					],
					contentType : JSON,
					requestContentType : URLENC
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/languages',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/orders/123123',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/products/expressupdate',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET, POST, HEAD],
				requestArgs: [
					path: getBasePathWithSite() + '/products/search',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/products/suggestions',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/products/2053226/references',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/products/2053226/reviews',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [POST],
				requestArgs: [
					path: getBasePathWithSite() + '/products/2053226/reviews',
					body: [
						'rating' : '5',
						'alias' : System.currentTimeMillis() + "_alias",
						'comment' : System.currentTimeMillis() + "_comment",
						'headline' : System.currentTimeMillis() + "_headline"
					],
					contentType : JSON,
					requestContentType : URLENC
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET, HEAD],
				requestArgs: [
					path: getBasePathWithSite() + '/products/2053226/stock',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/products/2053226/stock/WS-Nakano',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/promotions',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/promotions/WS_ProductBundle',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET, HEAD],
				requestArgs: [
					path: getBasePathWithSite() + '/stores',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/stores/8796093054999',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.ANONYMOUS,
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/titles',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
					AbstractSpockAccessRightsTest.Roles.GUEST
				],
				methods: [POST],
				requestArgs: [
					path: getBasePathWithSite() + '/users',
					body: [
						'firstName' : System.currentTimeMillis() + "_firstName",
						'lastName' : System.currentTimeMillis() + "_lastName",
						'password' : System.currentTimeMillis() + "_password",
						'login' : System.currentTimeMillis() + "_login",
						'titleCode' : System.currentTimeMillis() + "_titleCode"
					],
					contentType : JSON,
					requestContentType : URLENC
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET, PUT, PATCH],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id,
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [DELETE],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + System.currentTimeMillis(),
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/addresses',
				]
			],
			[	//users/{userId}/addresses/
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
				],
				methods: [POST],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/addresses',
					body: [
						'firstName' : System.currentTimeMillis() + "_firstName",
						'lastName' : System.currentTimeMillis() + "_lastName",
						'town' : System.currentTimeMillis() + "_town",
						'postalCode' : System.currentTimeMillis() + "_postalCode",
						'line1' : System.currentTimeMillis() + "_line1",
						'line2' : System.currentTimeMillis() + "_line2",
						'titleCode' : System.currentTimeMillis() + "_titleCode",
						'country.isocode' : System.currentTimeMillis() + "PL"
					],
					contentType : JSON,
					requestContentType : URLENC
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP,
				],
				methods: [POST],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/addresses/verification',
					body: [
						'firstName' : System.currentTimeMillis() + "_firstName",
						'lastName' : System.currentTimeMillis() + "_lastName",
						'town' : System.currentTimeMillis() + "_password",
						'login' : System.currentTimeMillis() + "_login",
						'titleCode' : System.currentTimeMillis() + "_titleCode"
					],
					contentType : JSON,
					requestContentType : URLENC
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET, PUT, PATCH, DELETE],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/addresses/123123',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET, POST],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET, DELETE],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code,
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [POST, PUT, DELETE],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/addresses/delivery/',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [POST],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/clonesavedcart/',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET, PUT, DELETE],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/deliverymode',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/deliverymodes',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [PUT],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/email',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET, POST],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/entries',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET, PUT, PATCH, DELETE],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/entries/123123',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [PATCH],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/flagForDeletion',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [POST, PUT],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/paymentdetails',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET, POST],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/promotions',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET, DELETE],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/promotions/123123123213',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [PATCH],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/restoresavedcart',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [PATCH],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/save',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/savedcart',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET, POST],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/vouchers',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [DELETE],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + customerCart.code + '/vouchers/12213231',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/customergroups',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [PUT],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/login',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET, POST, HEAD],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/orders',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/orders/1231251',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [PUT],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/password',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/paymentdetails',
				]
			],
			[
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERGROUP,
					AbstractSpockAccessRightsTest.Roles.CUSTOMERMANAGERGROUP
				],
				methods: [GET, PUT, PATCH, DELETE],
				requestArgs: [
					path: getBasePathWithSite() + '/users/' + customer.id + '/paymentdetails/1259812589052',
				]
			],
			[	//vouchers/{code}
				rolesAllowed: [
					AbstractSpockAccessRightsTest.Roles.TRUSTED_CLIENT
				],
				methods: [GET],
				requestArgs: [
					path: getBasePathWithSite() + '/vouchers/001/',
				]
			]
		]
	}
}

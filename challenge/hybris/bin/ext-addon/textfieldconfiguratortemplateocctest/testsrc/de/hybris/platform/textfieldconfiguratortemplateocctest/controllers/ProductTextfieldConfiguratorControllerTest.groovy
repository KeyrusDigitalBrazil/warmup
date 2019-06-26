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
package de.hybris.platform.textfieldconfiguratortemplateocctest.controllers

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest

import spock.lang.Unroll

@Unroll
class ProductTextfieldConfiguratorControllerTest extends AbstractCartTest {
    private final static String PRODUCT_CODE = "AK_CAMERA_KIT"

    def "Expected #configSize text field configurations for product #productCode in format #format"() {
        when: "creates a new configuration"
        def response = restClient.get(
                path: getBasePathWithSite() + '/products/' + productCode + '/configurator/textfield',
                contentType: format
        )

        then: "it gets a new text field configuration"
        with(response) {
            status == SC_OK
            data.configurationInfos.size() == configSize
        }

        where:
        productCode  | configSize | format
        PRODUCT_CODE | 3          | JSON
        PRODUCT_CODE | 3          | XML
    }

    def "Product not found - format #format"() {
        when: "creates a new configuration"
        def response = restClient.get(
                path: getBasePathWithSite() + '/products/4711/configurator/textfield',
                contentType: format
        )

        then: "product code not found"
        with(response) { status == SC_BAD_REQUEST }

        where:
        format << [XML, JSON]
    }

    def "Add product #productCode with qty #qty to cart"() {
        given: "anonymous user with cart"
        def customer = ['id': 'anonymous']
        def cart = createAnonymousCart(restClient, responseFormat)

        when: "customer decides to add product to cart"
        def response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.guid + '/entries/configurator/textfield',
                query: ['fields': FIELD_SET_LEVEL_FULL],
                body: postBody,
                contentType: responseFormat,
                requestContentType: requestFormat
        )

        then: "a new entry is added to the cart"
        with(response) {
            status == SC_OK
            data.statusCode == 'success'
            data.quantityAdded == 2
            data.entry.configurationInfos.size() == 1
            data.entry.configurationInfos[0].configurationValue == 'Hans'
        }

        where:
        productCode  | qty | requestFormat | responseFormat | postBody
        PRODUCT_CODE | 2   | JSON          | JSON           | "{\"configurationInfos\": [ {\"configurationLabel\": \"Font Type\", \"configurationValue\": \"Hans\", \"configuratorType\": \"TEXTFIELD\", \"status\": \"SUCCESS\" }], \"product\" : {\"code\" : \"" + productCode + "\"},\"quantity\" : " + qty + "}"
    }

    def "Get configuration for cart entry"() {
        given: "anonymous user with cart"
        def customer = ['id': 'anonymous']
        def cart = createAnonymousCart(restClient, JSON)

        and: "cart with added textfield conifguration"
        def cartEntry = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.guid + '/entries/configurator/textfield',
                query: ['fields': FIELD_SET_LEVEL_FULL],
                body: "{\"product\" : {\"code\" : \"" + PRODUCT_CODE + "\"},\"quantity\" : 2, \"configurationInfos\": [{\"configurationLabel\": \"Font Type\", \"configurationValue\": \"Hans\", \"configuratorType\": \"TEXTFIELD\", \"status\": \"SUCCESS\" }]}",
                contentType: JSON,
                requestContentType: JSON).data

        when: "customer want configuration of a cart entry"
        def response = restClient.get(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.guid + '/entries/' + cartEntry.entry.entryNumber + '/configurator/textfield',
                contentType: JSON,
        )

        then: "the configuration infos of the cart entry"
        with(response) {
            status == SC_OK
            data.configurationInfos.size() == 1
            data.configurationInfos[0].configurationValue == 'Hans'
        }

        when: "customer want to update configuration of a cart entry"
        def updateReponse = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.guid + '/entries/' + cartEntry.entry.entryNumber + '/configurator/textfield',
                body: "{\"product\" : {\"code\" : \"" + PRODUCT_CODE + "\"},\"quantity\" : 2, \"configurationInfos\": [{\"configurationLabel\": \"Font Type\", \"configurationValue\": \"Max\", \"configuratorType\": \"TEXTFIELD\", \"status\": \"SUCCESS\" }]}",
                contentType: JSON,
                requestContentType: JSON)

        then: "the configuration info was updated in the cart entry"
        with(updateReponse) {
            status == SC_OK
            data.quantityAdded == 0
            data.quantity == 2
            data.entry.configurationInfos.size() == 1
            data.entry.configurationInfos[0].configurationValue == 'Max'
        }
    }

}

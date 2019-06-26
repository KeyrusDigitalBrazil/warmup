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
package de.hybris.platform.personalizationcmsweb.compatibility

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.oauth2.constants.OAuth2Constants
import de.hybris.platform.personalizationwebservices.BaseWebServiceTest
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer
import groovy.json.JsonSlurper
import org.junit.Before
import org.junit.Test

import javax.ws.rs.client.Entity

import static javax.ws.rs.core.MediaType.APPLICATION_JSON
import static javax.ws.rs.core.MediaType.APPLICATION_XML
import static javax.ws.rs.core.Response.Status.CREATED
import static javax.ws.rs.core.Response.Status.OK

/**
 * Compatibility test for 6.1 format. If this test breaks, it means that you might have broken the backward
 * compatility of this webservice format with 6.1 version.
 */
@IntegrationTest
@NeedsEmbeddedServer(webExtensions = [
        PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME
])
class PersonalizationWebservicesCompatibilityTest extends BaseWebServiceTest {

    static final String BASE_FILE_PATH = "/personalizationcmsweb/test/"
    static final String CUSTOMIZATION_ENDPOINT = "catalogs/testCatalog/catalogVersions/Online/customizations"
    static final String QUERY_ENDPOINT = "query/cxReplaceComponentWithContainer"

    @Before
    void setup() {
        importData(new ClasspathImpExResource(BASE_FILE_PATH + "personalizationcmsweb_testdata.impex", "utf-8"));
        importData(new ClasspathImpExResource(BASE_FILE_PATH + "webcontext_testdata.impex", "utf-8"));
    }

    @Test
    void "replace components on page: json"() {
        "replace components on page"("json", APPLICATION_JSON)
    }

    @Test
    void "replace components on page: xml"() {
        "replace components on page"("xml", APPLICATION_XML)
    }

    def "replace components on page"(ext, format) {
        given: "predefined request and response"
        def request = loadText(BASE_FILE_PATH + "wstests/replaceComponent-request." + ext)
        def expected = loadObject(BASE_FILE_PATH + "wstests/replaceComponent-response." + ext, format)

        when: "actual request is made"
        def response = getWsSecuredRequestBuilderForCmsManager() //
                .path(VERSION)//
                .path(QUERY_ENDPOINT)//
                .build().accept(format) //
                .post(Entity.entity(request, format));
        def actual = parse(response, format)

        then: "request was made "
        assert response.status == OK.statusCode
        assert expected.uid != null
        assert expected.sourceId != null

        when: "random field is normalized"
        actual.uid = expected.uid
        actual.sourceId = expected.sourceId

        then: "actual response is the same as expected"
        assertObject(expected, actual, format)
    }


    @Test
    void "create cms action: json"() {
        "create cms action"("json", APPLICATION_JSON)
    }

    @Test
    void "create cms action: xml"() {
        "create cms action"("xml", APPLICATION_XML)
    }

    def "create cms action"(ext, format) {
        given: "predefined request and response"
        def request = loadText(BASE_FILE_PATH + "wstests/createAction-request." + ext)
        def expected = loadObject(BASE_FILE_PATH + "wstests/createAction-response." + ext, format)

        when: "actual request is made"
        def response = getWsSecuredRequestBuilderForCmsManager() //
                .path(VERSION) //
                .path(CUSTOMIZATION_ENDPOINT) //
                .path(CUSTOMIZATION) //
                .path(VARIATION_ENDPOINT) //
                .path(VARIATION) //
                .path(ACTION_ENDPOINT)//
                .build().accept(format) //
                .post(Entity.entity(request, format));
        def actual = parse(response, format)

        then: "request was made"
        assert response.status == CREATED.statusCode

        then: "actual response is the same as expected"
        assertObject(expected, actual, format)
    }

    def loadText(name) {
        this.getClass().getResource(name).text
    }

    def loadObject(name, format) {
        stringParse(loadText(name), format)
    }

    def parse(response, format) {
        def text = response.readEntity(String.class)
        stringParse(text, format)
    }

    def stringParse(text, format) {
        switch (format) {
            case APPLICATION_JSON:
                return new JsonSlurper().parseText(text);
            case APPLICATION_XML:
                return new XmlSlurper().parseText(text);
            default:
                return null;
        }
    }

    def assertObject(expected, actual, format) {
        assert expected
        assert actual

        switch (format) {
            case APPLICATION_JSON:
                expected.each {
                    assert actual[it.key] == it.value
                }

                break;

            case APPLICATION_XML:
                assert expected.name() == actual.name() // makes sure the name of the root element is the same

                def expectedMap = expected.children().collectEntries { [(it.name()): it] }
                def actualMap = actual.children().collectEntries { [(it.name()): it] }

                expectedMap.each {
                    assert actualMap[it.key] == it.value
                }

                break;

            default:
                assert false;
        }
    }
}

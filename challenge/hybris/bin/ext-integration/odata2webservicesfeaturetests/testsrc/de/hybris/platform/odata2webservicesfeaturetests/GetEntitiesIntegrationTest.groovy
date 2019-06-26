/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2webservicesfeaturetests

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.inboundservices.util.InboundMonitoringRule
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.integrationservices.util.XmlObject
import de.hybris.platform.odata2webservices.constants.Odata2webservicesConstants
import de.hybris.platform.odata2webservicesfeaturetests.ws.BasicAuthRequestBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer
import org.junit.Rule
import org.junit.Test

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@NeedsEmbeddedServer(webExtensions = Odata2webservicesConstants.EXTENSIONNAME)
@IntegrationTest
class GetEntitiesIntegrationTest extends ServicelayerSpockSpecification {

    @Rule
    InboundMonitoringRule monitoring = InboundMonitoringRule.disabled()

    def setup() {
        importCsv("/impex/essentialdata-odata2services.impex", "UTF-8") // For the integrationadmingroup (from odata2services)
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Employee; UID[unique = true]; groups(uid); @password[translator = de.hybris.platform.impex.jalo.translators.UserPasswordTranslator]',
                '; tester ; integrationadmingroup; *:retset',
                'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
                '; InboundProduct; INBOUND',
                'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
                '; InboundProduct  ; Product         ; Product',
                '; InboundProduct  ; Unit			 ; Unit',
                '; InboundProduct  ; Catalog         ; Catalog',
                '; InboundProduct  ; CatalogVersion  ; CatalogVersion',
                'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
                '; InboundProduct:Unit           ; code            ; Unit:code               ;',
                '; InboundProduct:Unit           ; name            ; Unit:name               ;',
                '; InboundProduct:Unit           ; unitType        ; Unit:unitType           ;',
                '; InboundProduct:Catalog        ; id              ; Catalog:id              ;',
                '; InboundProduct:CatalogVersion ; catalog         ; CatalogVersion:catalog  ; InboundProduct:Catalog',
                '; InboundProduct:CatalogVersion ; version         ; CatalogVersion:version  ;',
                '; InboundProduct:Product        ; code            ; Product:code            ;',
                '; InboundProduct:Product        ; unit            ; Product:unit            ; InboundProduct:Unit',
                '; InboundProduct:Product        ; catalogVersion  ; Product:catalogVersion  ; InboundProduct:CatalogVersion')
    }

    @Test
    def "get all integration object items for an integration object as json"() {
        when:
        def response = basicAuthRequest("InboundProduct")
                .build()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get()

        then:
        response.status == 200
        def json = extractBody(response)
        def entities = json.getCollection("\$.d.EntitySets[*]")
        entities.containsAll(["Products", "Units", "CatalogVersions", "Catalogs"])
    }

    @Test
    def "get all integration object items for an integration object as xml"() {
        when:
        def response = basicAuthRequest("InboundProduct")
                .build()
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get()

        then:
        response.status == 200
        def xml = getXml(response)
        def entities = xml.get("/service/workspace")
        entities.contains("Units")
        entities.contains("Products")
        entities.contains("CatalogVersions")
        entities.contains("Catalogs")
    }

    @Test
    def "get all integration object items for nonexistent integration object returns 404"() {
        when:
        def response = basicAuthRequest("NonExistentProduct")
                .build()
                .accept(MediaType.APPLICATION_XML_TYPE)
                .get()

        then:
        response.status == 404
    }

    BasicAuthRequestBuilder basicAuthRequest(final String path)
    {
        new BasicAuthRequestBuilder()
                .extensionName(Odata2webservicesConstants.EXTENSIONNAME)
                .credentials('tester', 'retset') // defined inside setup()
                .path(path)
    }

    private XmlObject getXml(final Response response)
    {
        return XmlObject.createFrom(response.getEntity());
    }

    JsonObject extractBody(final Response response)
    {
        JsonObject.createFrom((InputStream) response.getEntity())
    }
}

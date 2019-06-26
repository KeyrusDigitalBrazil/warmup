/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
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
class GetCrossFeatureIntegrationTest extends ServicelayerSpockSpecification {
    @Rule
    InboundMonitoringRule monitoring = InboundMonitoringRule.disabled()

    def setup() {
        importCsv("/impex/essentialdata-odata2services.impex", "UTF-8") // For the integrationadmingroup (from odata2services)
        IntegrationTestUtil.importImpEx(
                '$catalog = Default',
                '$version = Staged',
                '$catalogVersion = $catalog:$version',
                'INSERT_UPDATE Employee; UID[unique = true]; groups(uid); @password[translator = de.hybris.platform.impex.jalo.translators.UserPasswordTranslator]',
                '; tester ; integrationadmingroup; *:retset',
                'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
                '; CrossFeature; INBOUND',
                'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
                '; CrossFeature  ; Catalog         ; Catalog',
                '; CrossFeature  ; CatalogVersion  ; CatalogVersion',
                '; CrossFeature  ; Product         ; Product',
                '; CrossFeature  ; Category        ; Category',
                'INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]',
                '; CrossFeature:Catalog        ; id              ; Catalog:id              ;',
                '; CrossFeature:CatalogVersion ; catalog         ; CatalogVersion:catalog  ; CrossFeature:Catalog',
                '; CrossFeature:CatalogVersion ; version         ; CatalogVersion:version  ;',
                '; CrossFeature:Product        ; code            ; Product:code            ;',
                '; CrossFeature:Product        ; catalogVersion  ; Product:catalogVersion  ; CrossFeature:CatalogVersion',
                '; CrossFeature:Product        ; supercategories ; Product:supercategories ; CrossFeature:Category',
                '; CrossFeature:Category       ; code            ; Category:code           ;',
                '; CrossFeature:Category       ; products        ; Category:products       ; CrossFeature:Product',
                'INSERT_UPDATE Catalog; id[unique = true]; name[lang = en]; defaultCatalog;',
                '; $catalog ; $catalog ; true',
                'INSERT_UPDATE CatalogVersion; catalog(id)[unique = true]; version[unique = true]; active;',
                '; $catalog ; $version ; true',
                'INSERT_UPDATE Category; code[unique = true];',
                '; category1',
                '; category2',
                '; category3',
                'INSERT_UPDATE Product; code[unique = true]; catalogVersion(catalog(id), version); supercategories(code)',
                '; pr1-1   ; $catalogVersion; category1',
                '; pr2-1   ; $catalogVersion; category1',
                '; pr3-2   ; $catalogVersion; category2',
                '; pr4-2   ; $catalogVersion; category2',
                '; pr5-2_3 ; $catalogVersion; category2, category3',
                '; pr6     ; $catalogVersion;')
    }

    @Test
    def "request with \$expand, \$top, \$skip, and \$inlinecount"() {
        when:
        def response = basicAuthRequest()
                .path('Products')
                .queryParam('$expand', 'supercategories')
                .queryParam('$top', 10)
                .queryParam('$skip', 1)
                .queryParam('$inlinecount', 'allpages')
                .build()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get()

        then:
        response.status == 200
        def json = extractBody response
        json.getCollection("\$.d.results").size() == 5
        json.getString('d.__count') == '6'
        json.getCollectionOfObjects('d.results[?(@.code == "pr2-1")].supercategories.results[*].code') == ['category1']
        json.getCollectionOfObjects('d.results[?(@.code == "pr3-2")].supercategories.results[*].code') == ['category2']
        json.getCollectionOfObjects('d.results[?(@.code == "pr4-2")].supercategories.results[*].code') == ['category2']
        json.getCollectionOfObjects('d.results[?(@.code == "pr5-2_3")].supercategories.results[*].code') == ['category2', 'category3']
        json.getCollectionOfObjects('d.results[?(@.code == "pr6")].supercategories.results[*]') == []
        json.getCollectionOfObjects("d.results[*].supercategories.__deferred").isEmpty()
    }

    BasicAuthRequestBuilder basicAuthRequest()
    {
        new BasicAuthRequestBuilder()
                .extensionName(Odata2webservicesConstants.EXTENSIONNAME)
                .credentials('tester', 'retset') // defined inside setup()
                .path('CrossFeature')
    }

    JsonObject extractBody(final Response response)
    {
        JsonObject.createFrom((InputStream) response.getEntity())
    }
}

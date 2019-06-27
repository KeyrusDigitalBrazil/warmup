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
package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.model.CatalogModel
import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonBuilder
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import de.hybris.platform.servicelayer.model.ModelService
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.junit.Test
import org.springframework.http.MediaType
import spock.lang.Unroll

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists
import static de.hybris.platform.integrationservices.util.JsonBuilder.json
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.createContext

@IntegrationTest
class LocalizedAttributesPersistenceIntegrationTest extends ServicelayerTransactionalSpockSpecification {
    static final String SERVICE = "MyProduct"
    static final String PRODUCTS = "Products"
    static final String PRODUCT_CODE = "orange_product"
    static final String ORIG_EN_PRODUCT_NAME = "UPhone 11"
    static final String ORIG_EN_PRODUCT_DESCRIPTION = "Latest version of the UPhone"
    static final String NEW_EN_PRODUCT_NAME = "UPhone XI"
    static final String NEW_EN_PRODUCT_DESCRIPTION = "The newest and fastest UPhone"
    static final String NEW_DE_PRODUCT_NAME = "UPhone XI.de"
    static final String NEW_DE_PRODUCT_DESCRIPTION = "Das neueste und schnellste UPhone"
    static final String CATALOG_ID = "Default"
    static final String CATALOG_VERSION_VERSION = "Staged"
    static final String NEW_EN_CATALOG_VERSION_CATEGORY_SYSTEM_NAME = "New tech of the year"

    static final String[] impEx = [
            "INSERT_UPDATE IntegrationObject; code[unique = true];",
            "; MyProduct",
            "INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique=true]; code[unique = true]; type(code)",
            "; MyProduct ; Product        ; Product",
            "; MyProduct ; Catalog        ; Catalog",
            "; MyProduct ; CatalogVersion ; CatalogVersion",
            "INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
            "; MyProduct:Product        ; code               ; Product:code                        ;",
            "; MyProduct:Product        ; name               ; Product:name                        ;",
            "; MyProduct:Product        ; description        ; Product:description                 ;",
            "; MyProduct:Product        ; catalogVersion     ; Product:catalogVersion              ; MyProduct:CatalogVersion",
            "; MyProduct:Catalog        ; id                 ; Catalog:id                          ;",
            "; MyProduct:CatalogVersion ; catalog            ; CatalogVersion:catalog              ; MyProduct:Catalog",
            "; MyProduct:CatalogVersion ; version            ; CatalogVersion:version              ;",
            "; MyProduct:CatalogVersion ; categorySystemName ; CatalogVersion:categorySystemName   ;",
            "INSERT_UPDATE Catalog;id[unique=true];name[lang=en];defaultCatalog;",
            ";Default;Default;true",
            "INSERT_UPDATE CatalogVersion; catalog(id)[unique=true]; version[unique=true];active;",
            ";Default;Staged;true",
            "INSERT_UPDATE Language;isocode[unique=true];name[lang=de];name[lang=en]",
            ";de;Deutsch;German"
    ]

    @Resource(name = "defaultODataFacade")
    ODataFacade facade

    @Resource
    ModelService modelService

    def setup() {
        IntegrationTestUtil.importImpEx(impEx)
    }

    @Test
    @Unroll
    def "localizedAttributes are persisted with content language '#contentLanguage' and accept language '#acceptLanguage'"() {
        given: "A request with localized attributes in the body"
        def request = postRequest()
                .withContentLanguage(contentLanguage)
                .withAcceptLanguage(acceptLanguage)
                .withBody(product()
                        .withField("name", ORIG_EN_PRODUCT_NAME)
                        .withField("description", ORIG_EN_PRODUCT_DESCRIPTION)
                        .withLocalizedAttributes(
                                [language: "en", name: NEW_EN_PRODUCT_NAME, description: NEW_EN_PRODUCT_DESCRIPTION],
                                [language: "de", name: NEW_DE_PRODUCT_NAME, description: NEW_DE_PRODUCT_DESCRIPTION]))

        when: "The request is processed"
        def oDataResponse = facade.handlePost(createContext(request))

        then: "Response body is unchanged. This is a bug that needs to be fixed in STOUT-1042."
        ODataAssertions.assertThat(oDataResponse)
                .hasStatus(HttpStatusCodes.CREATED)
                .jsonBody()
                .hasPathWithValue("d.name", bodyValues["name"])
                .hasPathWithValue("d.description", bodyValues["description"])

        and: "Localized attributes are persisted at the item level."
        ProductModel productModel = retrieveProduct()
        NEW_EN_PRODUCT_NAME == productModel.getProperty("name", Locale.ENGLISH)
        NEW_EN_PRODUCT_DESCRIPTION == productModel.getProperty("description", Locale.ENGLISH)
        NEW_DE_PRODUCT_NAME == productModel.getProperty("name", Locale.GERMAN)
        NEW_DE_PRODUCT_DESCRIPTION == productModel.getProperty("description", Locale.GERMAN)

        and: "Localized attributes are persisted at a nested level."
        CatalogVersionModel catalogVersionModel = retrieveCatalogVersion()
        NEW_EN_CATALOG_VERSION_CATEGORY_SYSTEM_NAME == catalogVersionModel.getProperty("categorySystemName", Locale.ENGLISH)

        where:
        contentLanguage | acceptLanguage | bodyValues
        Locale.ENGLISH  | Locale.ENGLISH | [name: NEW_EN_PRODUCT_NAME, description: NEW_EN_PRODUCT_DESCRIPTION]
        Locale.ENGLISH  | Locale.GERMAN  | [name: NEW_DE_PRODUCT_NAME, description: NEW_DE_PRODUCT_DESCRIPTION]
    }

    @Test
    @Unroll
    def "#localizedAttribute is persisted with '#description' description"() {
        given:
        def request = postRequest()
                .withContentLanguage(Locale.ENGLISH)
                .withBody(product().withLocalizedAttributes(localizedAttribute))
                .build()

        when:
        facade.handlePost(createContext(request))

        then:
        ProductModel productModel = retrieveProduct()
        NEW_DE_PRODUCT_NAME == productModel.getProperty("name", Locale.GERMAN)
        description == productModel.getProperty("description", Locale.GERMAN)

        where:
        localizedAttribute                                           | description
        [language: "de", name: NEW_DE_PRODUCT_NAME, description: ""] | ""
        [language: "de", name: NEW_DE_PRODUCT_NAME]                  | null
    }

    @Test
    def "exception is thrown if the language is not provided in the localizedAttribute map entry"() {
        given:
        def request = postRequest()
                .withContentLanguage(Locale.ENGLISH)
                .withBody(product().withLocalizedAttributes([name: NEW_EN_PRODUCT_NAME, description: NEW_EN_PRODUCT_DESCRIPTION]))

        when:
        def oDataResponse = facade.handlePost(createContext(request))

        then:
        ODataAssertions.assertThat(oDataResponse)
                .hasStatus(HttpStatusCodes.BAD_REQUEST)
                .jsonBody()
                .hasPathWithValue("error.code", "missing_language")
    }

    @Test
    def "Content-Language is returned when Accept-Language is omitted"() {
        given:
        def request = postRequest()
                .withContentLanguage(Locale.GERMAN)
                .withBody(product().withLocalizedAttributes(
                        [language: 'en', name: NEW_EN_PRODUCT_NAME, description: NEW_EN_PRODUCT_DESCRIPTION],
                        [language: 'de', name: NEW_DE_PRODUCT_NAME, description: NEW_DE_PRODUCT_DESCRIPTION]))

        when:
        def oDataResponse = facade.handlePost(createContext(request))

        then:
        ODataAssertions.assertThat(oDataResponse).jsonBody()
                .hasPathWithValue("d.name", NEW_DE_PRODUCT_NAME)
                .hasPathWithValue("d.description", NEW_DE_PRODUCT_DESCRIPTION)
    }

    ProductModel retrieveProduct() {
        ProductModel testModel = new ProductModel()
        testModel.setCode(PRODUCT_CODE)
        assertModelExists(testModel)
    }

    CatalogModel retrieveCatalog() {
        CatalogModel testModel = new CatalogModel()
        testModel.setId(CATALOG_ID)
        assertModelExists(testModel)
    }

    CatalogVersionModel retrieveCatalogVersion() {
        CatalogVersionModel testModel = new CatalogVersionModel()
        testModel.setCatalog(retrieveCatalog())
        testModel.setVersion(CATALOG_VERSION_VERSION)
        assertModelExists(testModel)
    }

    JsonBuilder product() {
        json()
                .withCode(PRODUCT_CODE)
                .withField("catalogVersion", catalogVersion())
    }

    JsonBuilder catalogVersion() {
        json()
                .withField("version", CATALOG_VERSION_VERSION)
                .withField("catalog", catalog())
                .withLocalizedAttributes([language: "en", categorySystemName: NEW_EN_CATALOG_VERSION_CATEGORY_SYSTEM_NAME])
    }

    JsonBuilder catalog() {
        json().withId(CATALOG_ID)
    }

    ODataRequestBuilder postRequest() {
        ODataFacadeTestUtils.postRequestBuilder(SERVICE, PRODUCTS, MediaType.APPLICATION_JSON_VALUE)
    }
}

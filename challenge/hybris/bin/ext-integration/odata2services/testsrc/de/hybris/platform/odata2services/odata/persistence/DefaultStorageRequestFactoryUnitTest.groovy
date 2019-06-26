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
package de.hybris.platform.odata2services.odata.persistence

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.processor.ServiceNameExtractor
import org.apache.olingo.odata2.api.commons.HttpHeaders
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.edm.EdmEntityType
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.exception.ODataException
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.uri.PathInfo
import org.junit.Test
import spock.lang.Specification

@UnitTest
class DefaultStorageRequestFactoryUnitTest extends Specification {
	def SERVICE_NAME = "InboundProduct"
	def INTEGRATION_KEY_VALUE = "asdf|fdsa|asdf"
	def CONTENT_TYPE = "application/json"
	def PRE_PERSIST_HOOK = "Pre-Persist-Hook";
	def POST_PERSIST_HOOK = "Post-Persist-Hook";
	def LOCALE = Locale.ENGLISH

	def localeExtractor = Mock(ODataContextLanguageExtractor)

	def serviceNameExtractor = Mock(ServiceNameExtractor) {
		extract(_ as ODataContext, _ as String) >> SERVICE_NAME
	}

	def modelEntityService = Mock(ModelEntityService) {
		addIntegrationKeyToODataEntry(_ as EdmEntitySet, _ as ODataEntry) >> INTEGRATION_KEY_VALUE
	}

	def edmEntitySet = Mock(EdmEntitySet) {
		getEntityType() >> Mock(EdmEntityType)
	}

	def oDataEntry = Mock(ODataEntry)

	private DefaultStorageRequestFactory factory = new DefaultStorageRequestFactory()

	def setup() {
		factory.setLocaleExtractor(localeExtractor)
		factory.setServiceNameExtractor(serviceNameExtractor)
		factory.setModelEntityService(modelEntityService)
	}

	@Test
	def "handles ODataException thrown from the ODataContext"() {
		given:
		def oDataContext = Mock(ODataContext)
				{
					getPathInfo() >> {
						throw new ODataException()
					}
				}
		localeExtractor.extractFrom(oDataContext, HttpHeaders.CONTENT_LANGUAGE) >> LOCALE
		localeExtractor.getAcceptLanguage(_ as ODataContext) >> Optional.empty()


		when:
		factory.create(oDataContext, CONTENT_TYPE, edmEntitySet, oDataEntry)

		then:
		def e = thrown ODataContextProcessingException
		e.getCause() instanceof ODataException
	}

	@Test
	def "sets accept locale to content locale when accept locale header is not provided"() {
		given:
		def oDataContext = oDataContext()
		localeExtractor.extractFrom(oDataContext, HttpHeaders.CONTENT_LANGUAGE) >> LOCALE
		localeExtractor.getAcceptLanguage(_ as ODataContext) >> Optional.empty()

		when:
		def storageRequest = factory.create(oDataContext, CONTENT_TYPE, edmEntitySet, oDataEntry)

		then:
		storageRequest.getAcceptLocale() == LOCALE
	}

	@Test
	def "creates a storage request successfully with expected fields"() {
		given:
		def oDataContext = oDataContext()
		oDataContext.getRequestHeader(PRE_PERSIST_HOOK) >> "prePersistHookName"
		oDataContext.getRequestHeader(POST_PERSIST_HOOK) >> "postPersistHookName"
		localeExtractor.extractFrom(oDataContext, HttpHeaders.CONTENT_LANGUAGE) >> Locale.ENGLISH
		localeExtractor.getAcceptLanguage(_ as ODataContext) >> Optional.of(new Locale("de"))
		localeExtractor.extractFrom(oDataContext, HttpHeaders.ACCEPT_LANGUAGE) >> Locale.GERMAN

		when:
		def storageRequest = factory.create(oDataContext, CONTENT_TYPE, edmEntitySet, oDataEntry)

		then:
		storageRequest.getIntegrationKey() == INTEGRATION_KEY_VALUE
		storageRequest.getEntitySet() == edmEntitySet
		storageRequest.getODataEntry() == oDataEntry
		storageRequest.getAcceptLocale() == Locale.GERMAN
		storageRequest.getContentLocale() == Locale.ENGLISH
		storageRequest.getPrePersistHook() == "prePersistHookName"
		storageRequest.getPostPersistHook() == "postPersistHookName"
		storageRequest.getIntegrationObjectCode() == SERVICE_NAME
		new URI("https://localhost:9002/odata2webservices/InboundProduct") == storageRequest.getServiceRoot()
		storageRequest.getContentType() == CONTENT_TYPE
		new URI("https://localhost:9002/odata2webservices/InboundProduct/Products") == storageRequest.getRequestUri()
	}

	private ODataContext oDataContext() {
		Mock(ODataContext) {
			getPathInfo() >> Mock(PathInfo) {
				getServiceRoot() >> new URI("https://localhost:9002/odata2webservices/InboundProduct")
				getRequestUri() >>  new URI("https://localhost:9002/odata2webservices/InboundProduct/Products")
			}
		}
	}
}

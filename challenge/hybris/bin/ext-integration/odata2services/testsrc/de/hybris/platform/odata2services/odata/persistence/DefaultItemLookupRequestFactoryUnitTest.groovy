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
import de.hybris.platform.odata2services.config.ODataServicesConfiguration
import de.hybris.platform.odata2services.odata.integrationkey.IntegrationKeyToODataEntryGenerator
import de.hybris.platform.odata2services.odata.persistence.exception.InvalidIntegrationKeyException
import de.hybris.platform.odata2services.odata.processor.ServiceNameExtractor
import org.apache.olingo.odata2.api.commons.HttpHeaders
import org.apache.olingo.odata2.api.commons.InlineCount
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.edm.EdmEntityType
import org.apache.olingo.odata2.api.edm.EdmException
import org.apache.olingo.odata2.api.edm.EdmProperty
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.uri.KeyPredicate
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment
import org.apache.olingo.odata2.api.uri.PathInfo
import org.apache.olingo.odata2.api.uri.UriInfo
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class DefaultItemLookupRequestFactoryUnitTest extends Specification {
	def INTEGRATION_OBJECT = "thisServiceName"
	def INTEGRATION_KEY_VALUE = "asdf|fdsa|asdf"

	def integrationKeyToODataEntryGenerator = Mock(IntegrationKeyToODataEntryGenerator)
	def localeExtractor = Mock(ODataContextLanguageExtractor) {
		extractFrom(_ as ODataContext, HttpHeaders.ACCEPT_LANGUAGE) >> Locale.ENGLISH
	}
	private ODataServicesConfiguration oDataServicesConfiguration = Mock(ODataServicesConfiguration) {
		getMaxPageSize() >> 3
		getDefaultPageSize() >> 2
	}
	def serviceNameExtractor = Mock(ServiceNameExtractor) {
		extract(_ as ODataContext, _ as String) >> "Inbound"
	}
	private static final String EXPECTED_PAGING_ERROR_CODE = "invalid_query_parameter"

	private DefaultItemLookupRequestFactory factory = new DefaultItemLookupRequestFactory()

	def setup() {
		factory.setIntegrationKeyToODataEntryGenerator(integrationKeyToODataEntryGenerator)
		factory.setLocaleExtractor(localeExtractor)
		factory.setServiceNameExtractor(serviceNameExtractor)
		factory.setODataServicesConfiguration(oDataServicesConfiguration)
	}

	@Test
	@Unroll
	def "lookup request \$skip option when at most 1 skip #skipValue present in uriInfo"() {
		given:
		def uriInfo = uriInfoWithoutIntegrationKey()
		uriInfo.getSkip() >> skipValue
		uriInfo.getSkipToken() >> skipTokenValue
		uriInfo.getTop() >> 1

		when:
		def lookupRequest = factory.create(uriInfo, oDataContext(), "")

		then:
		lookupRequest.skip == expected

		where:
		skipValue | skipTokenValue | expected
		1         | null           | 1
		null      | null           | 0
		null      | "1"            | 1
	}

	@Test
	@Unroll
	def "error thrown when invalid \$skipToken=#skipToken provided"() {
		given:
		def uriInfo = uriInfoWithoutIntegrationKey()
		uriInfo.getSkip() >> 1
		uriInfo.getTop() >> 1
		uriInfo.getSkipToken() >> skipToken

		when:
		factory.create(uriInfo, oDataContext(), "")

		then:
		def exception = thrown(InvalidDataException)
		exception.getCode() == EXPECTED_PAGING_ERROR_CODE

		where:
		skipToken << ["1.1", "notAnInt", "-1"]
	}

	@Test
	@Unroll
	def "lookup request #condition_desc count option when \$inlineCount with #inlineCntValue present"() {
		given:
		def uriInfo = uriInfoWithoutIntegrationKey()
		uriInfo.getInlineCount() >> inlineCntValue

		when:
		def lookupRequest = factory.create(uriInfo, oDataContext(), "")

		then:
		lookupRequest.count == expected
		!lookupRequest.countOnly

		where:
		inlineCntValue       | condition_desc | expected
		InlineCount.ALLPAGES | 'has'          | true
		InlineCount.NONE     | 'has no'       | false
	}

	@Test
	@Unroll
	def "lookup request #res_description count option when \$count #res_condition"() {
		given:
		def uriInfo = uriInfoWithoutIntegrationKey()
		uriInfo.isCount() >> countCondition


		when:
		def lookupRequest = factory.create(uriInfo, oDataContext(), "")

		then:
		lookupRequest.count == expected
		lookupRequest.countOnly == expected

		where:
		res_condition | res_description | countCondition | expected
		'present'     | 'has'           | true           | true
		'absent'      | 'has no'        | false          | false
	}

	@Test
	@Unroll
	def "lookup request \$top option when topValue = #topValue present in uriInfo"() {
		given:
		def uriInfo = uriInfoWithoutIntegrationKey()
		uriInfo.getSkip() >> 1
		uriInfo.getSkipToken() >> null
		uriInfo.getTop() >> topValue

		when:
		def lookupRequest = factory.create(uriInfo, oDataContext(), "")

		then:
		lookupRequest.top == expected

		where:
		topValue | expected
		3        | 3
		4        | 3
		null     | 2
	}

	@Test
	@Unroll
	def "lookup request \$expand option when expandValue = #expandValue present in uriInfo"() {
		given:
		def uriInfo = uriInfoWithoutIntegrationKey()
		uriInfo.getExpand() >> expandValue

		when:
		def lookupRequest = factory.create(uriInfo, oDataContext(), "")

		then:
		lookupRequest.expand == expandValue

		where:
		expandValue << [[], [[null]], [[Mock(NavigationPropertySegment)]]]
	}

	@Test
	def "create with integration key present"() {
		given:
		def uriInfo = uriInfoWithIntegrationKey()
		def context = oDataContext()

		when:
		def lookupRequest = factory.create(uriInfo, context, "")

		then:
		lookupRequest != null
		lookupRequest.integrationKey == INTEGRATION_KEY_VALUE
		lookupRequest.integrationObjectCode == INTEGRATION_OBJECT
		1 * integrationKeyToODataEntryGenerator.generate(_ as EdmEntitySet, INTEGRATION_KEY_VALUE) >> Mock(ODataEntry)
		1 * serviceNameExtractor.extract(context, INTEGRATION_KEY_VALUE) >> INTEGRATION_OBJECT
	}


	@Test
	def "create with integration key not present"() {
		given:
		def uriInfo = uriInfoWithoutIntegrationKey()
		def context = oDataContext()

		when:
		def lookupRequest = factory.create(uriInfo, context, "")

		then:
		lookupRequest != null
		lookupRequest.integrationKey == null
		0 * integrationKeyToODataEntryGenerator.generate(_ as EdmEntitySet, _ as String)
		0 * serviceNameExtractor.extract(context, _ as String)
	}

	@Test
	def "create with incorrect integration key property name"() {
		given:
		def uriInfo = uriInfoWithIncorrectIntegrationKeyPropertyName()

		when:
		factory.create(uriInfo, oDataContext(), "")

		then:
		def exception = thrown(InvalidIntegrationKeyException)
		exception.getCode() == "invalid_key"
	}


	@Test
	def "create with integration key when key converter throws exception"() {
		given:
		def uriInfo = uriInfoWithIntegrationKey()
		integrationKeyToODataEntryGenerator.generate(_ as EdmEntitySet, INTEGRATION_KEY_VALUE) >> {
			throw new EdmException(EdmException.PROVIDERPROBLEM)
		}

		when:
		factory.create(uriInfo, oDataContext(), "")

		then:
		def exception = thrown(InternalProcessingException)
		exception.getCode() == "internal_error"
	}

	@Test
	def "createFrom creates new request with given parameters"() {
		given:
		def entry = Mock(ODataEntry)
		def entityType = Mock(EdmEntityType)
		def entitySet = Mock(EdmEntitySet) {
			getEntityType() >> entityType
		}
		def request = Mock(ItemLookupRequest) {
			getEntitySet() >> Mock(EdmEntitySet) { getEntityType() >> Mock(EdmEntityType)}
			getODataEntry() >> Mock(ODataEntry)
			getAcceptLocale() >> Locale.ENGLISH
		}

		when:
		def newRequest = factory.createFrom(request, entitySet, entry)

		then:
		newRequest.entitySet == entitySet
		newRequest.entitySet.entityType == entityType
		newRequest.getODataEntry() == entry
	}

	private UriInfo uriInfoWithoutIntegrationKey() {
		Mock(UriInfo) {
			getKeyPredicates() >> []
			getStartEntitySet() >> Mock(EdmEntitySet) {
				getEntityType() >> Mock(EdmEntityType)
			}
		}
	}

	private UriInfo uriInfoWithIncorrectIntegrationKeyPropertyName() {
		def property = Mock(EdmProperty) {
			getName() >> "wrongName"
		}
		def keyPredicate = Mock(KeyPredicate) {
			getProperty() >> (EdmProperty) property
			getLiteral() >> INTEGRATION_KEY_VALUE
		}
		Mock(UriInfo) {
			getKeyPredicates() >> [keyPredicate]
			getStartEntitySet() >> Mock(EdmEntitySet) {
				getEntityType() >> Mock(EdmEntityType)
			}
		}
	}

	private UriInfo uriInfoWithIntegrationKey() {
		def property = Mock(EdmProperty) {
			getName() >> "integrationKey"
		}
		def keyPredicate = Mock(KeyPredicate) {
			getProperty() >> (EdmProperty) property
			getLiteral() >> INTEGRATION_KEY_VALUE
		}
		Mock(UriInfo) {
			getKeyPredicates() >> [keyPredicate]
			getStartEntitySet() >> Mock(EdmEntitySet) {
				getEntityType() >> Mock(EdmEntityType)
			}
		}
	}

	private ODataContext oDataContext() {
		Mock(ODataContext) {
			getPathInfo() >> Mock(PathInfo) {
				getServiceRoot() >> new URI("https://localhost:9002/odata2webservices/InboundProduct")
			}
		}
	}
}

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
package de.hybris.platform.odata2services.odata.persistence.populator.processor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.core.model.type.CollectionTypeModel
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.odata2services.odata.persistence.StorageRequest
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.type.TypeService
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.edm.EdmEntityType
import org.apache.olingo.odata2.api.edm.EdmMultiplicity
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.ep.feed.ODataFeed
import org.apache.olingo.odata2.core.edm.provider.EdmNavigationPropertyImplProv
import org.apache.olingo.odata2.core.ep.entry.EntryMetadataImpl
import org.apache.olingo.odata2.core.ep.entry.MediaMetadataImpl
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl
import org.apache.olingo.odata2.core.uri.ExpandSelectTreeNodeImpl
import org.assertj.core.util.Maps
import org.junit.Test
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.PRIMITIVE_ENTITY_PROPERTY_NAME
import static de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest.itemConversionRequestBuilder
import static de.hybris.platform.odata2services.odata.persistence.populator.processor.PropertyProcessorTestUtils.typeAttributeDescriptor

@UnitTest
class PrimitiveCollectionPropertyProcessorUnitTest extends Specification {
	def ENTITY_NAME = "entityName"

	def TEST_PROPERTY = "testProperty"
	def INTEGRATION_OBJECT_CODE = "TestIntegrationObject"
	def TEST_TYPE = "TestType"


	def LOCALE = Locale.ENGLISH
	def properties = new HashMap<String, Object>()
	def processor = Spy(PrimitiveCollectionPropertyProcessor)

	def itemModel = itemModel()

	def itemModel() {
		return Stub(ItemModel) {
			getItemtype() >> TEST_TYPE
		}
	}

	def setup() {
		processor.setIntegrationObjectService(integrationObjectService)
		processor.setModelService(modelService)
		processor.setTypeService(typeService)
	}


	def attributeDescriptor = Mock(AttributeDescriptorModel) {
		getAttributeType() >> Mock(CollectionTypeModel)
		getQualifier() >> TEST_PROPERTY
		getLocalized() >> false
	}

	def entity = Mock(EdmNavigationPropertyImplProv) {
		getToRole() >> "String"
		getMultiplicity() >> EdmMultiplicity.MANY
	}

	def edmEntityType = Mock(EdmEntityType) {
		getPropertyNames() >> Collections.emptyList()
		getNavigationPropertyNames() >> Arrays.asList(TEST_PROPERTY)
		getProperty(TEST_PROPERTY) >> entity
		getName() >> ENTITY_NAME
	}

	def entitySet = Mock(EdmEntitySet) {
		getEntityType() >> edmEntityType
	}

	def integrationObjectService = Mock(IntegrationObjectService) {
		findItemAttributeName(_ as String, _ as String, _ as String) >> TEST_PROPERTY
	}

	def modelService = Mock(ModelService) {
		isNew(itemModel) >> Boolean.TRUE
	}

	def typeService = Mock(TypeService) {
		getAttributeDescriptor(_ as String, _ as String) >> attributeDescriptor
	}

	def storageRequest = Mock(StorageRequest) {
		getEntityType() >> edmEntityType
		getIntegrationObjectCode() >> INTEGRATION_OBJECT_CODE
		getAcceptLocale() >> LOCALE
		getODataEntry() >> Mock(ODataEntry) {
			getProperties() >> properties
		}
	}

	@Test
	def "Processor supported when the attribute value is a collection of primitives"() {
		given:
		final TypeAttributeDescriptor attributeDescriptor = typeAttributeDescriptor(true, true)

		expect:
		processor.isPropertySupported(Optional.of(attributeDescriptor), TEST_PROPERTY)
	}

	@Test
	def "Processor not supported when the attribute is a collection of non primitives"() {
		given:
		final TypeAttributeDescriptor attributeDescriptor = typeAttributeDescriptor(true, false)

		expect:
		!processor.isPropertySupported(Optional.of(attributeDescriptor), TEST_PROPERTY)
	}

	@Test
	def "Processor not supported when the attribute is a single primitive"() {
		given:
		final TypeAttributeDescriptor attributeDescriptor = typeAttributeDescriptor(false, true)

		expect:
		!processor.isPropertySupported(Optional.of(attributeDescriptor), TEST_PROPERTY)
	}

	@Test
	def "Processor not supported when the attribute is a single entity"() {
		given:
		final TypeAttributeDescriptor attributeDescriptor = typeAttributeDescriptor(false, false)

		expect:
		!processor.isPropertySupported(Optional.of(attributeDescriptor), TEST_PROPERTY)
	}

	@Test
	def "Process item with no previously existing values"() {
		given:
		givenIsPropertySupported()
		def urlPatternStringList = Arrays.asList("/this/url", "/another/url")
		properties.put(TEST_PROPERTY, createUrlPatternFeed(urlPatternStringList))
		modelService.getAttributeValue(itemModel, TEST_PROPERTY) >> null

		when:
		processor.processItem(itemModel, storageRequest)

		then:
		1 * modelService.setAttributeValue(itemModel, TEST_PROPERTY, urlPatternStringList)
	}

	@Test
	def "Process item with previously existing values and no duplicates"() {
		given:
		givenIsPropertySupported()
		def existingUrlPatternStringList = Arrays.asList("/this/url", "/another/url")
		def newUrlPatternStringList = Arrays.asList("/new/url", "/more/url")
		properties.put(TEST_PROPERTY, createUrlPatternFeed(newUrlPatternStringList))
		modelService.getAttributeValue(itemModel, TEST_PROPERTY) >> existingUrlPatternStringList

		def expectedCollection = Stream.concat(existingUrlPatternStringList.stream(), newUrlPatternStringList.stream()).collect(Collectors.toList())

		when:
		processor.processItem(itemModel, storageRequest)

		then:
		1 * modelService.setAttributeValue(itemModel, TEST_PROPERTY, expectedCollection)
	}

	@Test
	def "Process item with previously existing values and duplicates"() {
		given:
		givenIsPropertySupported()
		def existingUrlPatternStringList = Arrays.asList("/this/url", "/another/url")
		properties.put(TEST_PROPERTY, createUrlPatternFeed(existingUrlPatternStringList))
		modelService.getAttributeValue(itemModel, TEST_PROPERTY) >> existingUrlPatternStringList

		when:
		processor.processItem(itemModel, storageRequest)

		then:
		1 * modelService.setAttributeValue(itemModel, TEST_PROPERTY, existingUrlPatternStringList)
	}

	@Test
	def "Process entity returns collection of primitives as feed with a list of ODataEntry"() {
		given:
		givenIsPropertySupported()
		def entry = new ODataEntryImpl([:], new MediaMetadataImpl(), new EntryMetadataImpl(),
				new ExpandSelectTreeNodeImpl())
		def existingUrlPatternStringList = ["/this/url", "/another/url"]
		modelService.getAttributeValue(itemModel, TEST_PROPERTY) >> existingUrlPatternStringList

		when:
		processor.processEntity(entry, getConversionRequest())

		then:
		entry.getProperties().containsKey(TEST_PROPERTY)
		((ODataFeed) entry.getProperties().get(TEST_PROPERTY)).getEntries().get(0).getProperties().get("value") == "/this/url"
		((ODataFeed) entry.getProperties().get(TEST_PROPERTY)).getEntries().get(1).getProperties().get("value") == "/another/url"
	}

	def givenIsPropertySupported() {

		final TypeAttributeDescriptor attributeDescriptor = typeAttributeDescriptor(true, true)
		processor.findTypeDescriptorAttributeForItem(_, _) >> Optional.of(attributeDescriptor)
	}

	def createUrlPatternFeed(List<Object> urlPatterns) {
		def urlPatternEntries = new ArrayList<>()
		urlPatterns.each { u -> urlPatternEntries.add(createEntry(u)) }
		def feed = Mock(ODataFeed) {
			getEntries() >> urlPatternEntries
		}
		feed
	}

	def createEntry(Object o) {
		def entry = Mock(ODataEntry) {
			getProperties() >> Maps.newHashMap(PRIMITIVE_ENTITY_PROPERTY_NAME, o)
		}
		entry
	}

	def getConversionRequest() {
		itemConversionRequestBuilder().withEntitySet(entitySet)
				.withAcceptLocale(LOCALE)
				.withItemModel(itemModel)
				.withIntegrationObject(itemModel.getItemtype())
				.build()
	}
}

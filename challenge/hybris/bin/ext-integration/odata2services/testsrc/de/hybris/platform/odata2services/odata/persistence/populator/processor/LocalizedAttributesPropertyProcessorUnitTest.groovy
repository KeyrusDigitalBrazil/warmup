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
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.odata2services.odata.persistence.*
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.type.TypeService
import org.apache.olingo.odata2.api.edm.EdmEntityType
import org.apache.olingo.odata2.api.edm.EdmException
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.ep.feed.ODataFeed
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment
import org.apache.olingo.odata2.api.uri.NavigationSegment
import org.apache.olingo.odata2.core.edm.provider.EdmNavigationPropertyImplProv
import org.apache.olingo.odata2.core.edm.provider.EdmSimplePropertyImplProv
import org.apache.olingo.odata2.core.ep.entry.EntryMetadataImpl
import org.apache.olingo.odata2.core.ep.entry.MediaMetadataImpl
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl
import org.apache.olingo.odata2.core.uri.ExpandSelectTreeNodeImpl
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class LocalizedAttributesPropertyProcessorUnitTest extends Specification {
	private static final String LOCALIZED_ATTRIBUTES = "localizedAttributes"

	def oDataEntry = Stub(ODataEntryImpl)
	def attributeDescriptor = Stub(AttributeDescriptorModel)
	def entityType = Stub(EdmEntityType) {
		getProperty(_ as String) >> Stub(EdmNavigationPropertyImplProv)
	}
	def item = Stub(ItemModel)

	def modelService = Mock(ModelService)
	def typeService = Stub(TypeService) {
		getAttributeDescriptor(_, _) >> attributeDescriptor
	}
	def integrationObjectService = Stub(IntegrationObjectService) {
		findItemAttributeName(_, _, _) >> "attributeName"
	}
	def oDataLocalizationService = Stub(ODataLocalizationService) {
		getLocaleForLanguage('en') >> Locale.ENGLISH
		getLocaleForLanguage('de') >> Locale.GERMAN
		getSupportedLocales() >> [Locale.ENGLISH, Locale.GERMAN]
	}

	def localizedAttributesPropertyProcessor = Spy(LocalizedAttributesPropertyProcessor)

	def setup() {
		localizedAttributesPropertyProcessor.setModelService(modelService)
		localizedAttributesPropertyProcessor.setIntegrationObjectService(integrationObjectService)
		localizedAttributesPropertyProcessor.setoDataLocalizationService(oDataLocalizationService)
		localizedAttributesPropertyProcessor.setTypeService(typeService)
	}

	@Test
	@Unroll
	def "isPropertySupported is #expected when propertyName=#propertyName"() {
		def optionalTypeDescriptorDoesNotMatter = null

		when:
		def isSupported = localizedAttributesPropertyProcessor.isPropertySupported(optionalTypeDescriptorDoesNotMatter, propertyName)

		then:
		isSupported == expected

		where:
		propertyName                | expected
		"NOTlocalizedAttributes"    | false
		"localizedAttributes"       | true
	}

	@Test
	def "processItem throws exception when localizedAttribute entry has no language key"() {
		given:
		givenTypeDescriptorAttributeNotFound()
		oDataEntry.getProperties() >> oDataFeed(entry(name: 'English Product'))

		when:
		localizedAttributesPropertyProcessor.processItem(item, storageRequest(oDataEntry))

		then:
		thrown MissingLanguageException
	}

	@Test
	def "processItem throws exception when ISO language code is invalid"() {
		given:
		givenTypeDescriptorAttributeNotFound()
		oDataEntry.getProperties() >> oDataFeed(entry(language: 'zz'))
		oDataLocalizationService.getLocaleForLanguage("zz" as String) >> { throw new LanguageNotSupportedException("", null) }

		when:
		localizedAttributesPropertyProcessor.processItem(item, storageRequest(oDataEntry))

		then:
		thrown LanguageNotSupportedException
	}

	@Test
	def "processItem ignores properties with non ODataFeed values"() {
		given:
		givenTypeDescriptorAttributeNotFound()
		oDataEntry.getProperties() >> [localizedAttributes: 'NOT AN instanceof ODataFeed']

		when:
		localizedAttributesPropertyProcessor.processItem(item, storageRequest(oDataEntry))

		then:
		0 * modelService.setAttributeValue(_, _ as String, _)
	}

	@Test
	def "processItem ignores properties with names other than 'localizedAttributes'"() {
		def feed = oDataFeed(entry(language: "en", name: "English Product"))

		given:
		givenTypeDescriptorAttributeNotFound()
		oDataEntry.getProperties() >> [localizedAttribute: feed, name: feed, localizedProperties: feed, (null): feed, (''): feed]

		when:
		localizedAttributesPropertyProcessor.processItem(item, storageRequest(oDataEntry))

		then:
		0 * modelService.setAttributeValue(_, _ as String, _)
	}

	@Test
	@Unroll
	def "processItem ignores property when localizable=#isLocalizable and settable=#isSettable"() {
		given:
		givenTypeDescriptorAttributeNotFound()
		def oDataEntry = oDataEntryWithLocalizations(language: "en", name: "English Product")
		modelService.isNew(item) >> isSettable
		attributeDescriptor.getWritable() >> isSettable
		attributeDescriptor.getLocalized() >> isLocalizable

		when:
		localizedAttributesPropertyProcessor.processItem(item, storageRequest(oDataEntry))

		then:
		0 * modelService.setAttributeValue(_, _ as String, _)

		where:
		isSettable | isLocalizable
		true       | false
		false      | true
	}

	@Test
	def "processItem ignores properties when localizable check throws exception"() {
		given:
		givenTypeDescriptorAttributeNotFound()
		def oDataEntry = oDataEntryWithLocalizations(language: "en", name: "English Product")
		attributeDescriptor.getLocalized() >> { throw new EdmException(null) }

		when:
		localizedAttributesPropertyProcessor.processItem(item, storageRequest(oDataEntry))

		then:
		0 * modelService.setAttributeValue(_, _ as String, _)
	}

	@Test
	def "processItem success for 1 settable property with language"() {
		given:
		givenTypeDescriptorAttributeNotFound()
		def oDataEntry = oDataEntryWithLocalizations(language: "en", name: "English Product")
		modelService.isNew(_) >> true
		attributeDescriptor.getLocalized() >> true

		when:
		localizedAttributesPropertyProcessor.processItem(item, storageRequest(oDataEntry))

		then:
		1 * modelService.setAttributeValue(item, "name", _) >> { arguments ->
			assert arguments[2] == [(Locale.ENGLISH): "English Product"]
		}
	}

	@Test
	def "processItem success for 2 language entries with english name as empty string"() {
		given:
		givenTypeDescriptorAttributeNotFound()
		def oDataEntry = oDataEntryWithLocalizations(
				[language: "en", name: ""],
				[language: "de", description: "German Description"])
		modelService.isNew(_) >> true
		attributeDescriptor.getLocalized() >> true

		when:
		localizedAttributesPropertyProcessor.processItem(item, storageRequest(oDataEntry))

		then:
		1 * modelService.setAttributeValue(item, "name", _) >> { arguments ->
			assert arguments[2] == [(Locale.ENGLISH): ""]
		}

		1 * modelService.setAttributeValue(item, "description", _) >> { arguments ->
			assert arguments[2] == [(Locale.GERMAN): "German Description"]
		}
	}

	@Test
	def "processEntity ignores items with null localizedAttributes"() {
		setup:
		entityType.getProperty(_) >> null

		when:
		localizedAttributesPropertyProcessor.processEntity(oDataEntry, conversionRequest())

		then:
		oDataEntry.properties.isEmpty()
	}

	@Test
	def "processEntity ignores items if localizedAttributes not an instanceof EdmNavigationProperty"() {
		setup:
		entityType.getProperty(_) >> Stub(EdmSimplePropertyImplProv)

		when:
		localizedAttributesPropertyProcessor.processEntity(oDataEntry, conversionRequest())

		then:
		oDataEntry.properties.isEmpty()
	}

	@Test
	def "processEntity() ignores non-localizable property"() {
		setup:
		entityType.getPropertyNames() >> ["id"]
		modelService.getAttributeValues(item, "id", _) >> [:]
		oDataEntry.getProperties() >> [:]

		when:
		localizedAttributesPropertyProcessor.processEntity(oDataEntry, conversionRequest())

		then:
		def feed = oDataEntry.properties[LOCALIZED_ATTRIBUTES]
		feed.entries.size() == 0
	}

	@Test
	@Unroll
	def "processEntity localizable property '#name' with value #value"() {
		setup:
		entityType.getPropertyNames() >> [name]
		modelService.getAttributeValues(item, name, _) >> [(Locale.ENGLISH): value]
		oDataEntry.getProperties() >> [:]

		when:
		localizedAttributesPropertyProcessor.processEntity(oDataEntry, conversionRequest())

		then:
		def feed = oDataEntry.properties[LOCALIZED_ATTRIBUTES]
		feed.entries.size() == 1

		where:
		name 			| value
		"stringProp" 	| "stringValue"
		"booleanProp" 	| Boolean.TRUE
		"byteProp"		| Byte.MAX_VALUE
		"characterProp" | Character.MAX_VALUE
		"doubleProp"	| Double.MAX_VALUE
		"floatProp"		| Float.MAX_VALUE
		"integerProp" 	| Integer.MAX_VALUE
		"longProp"		| Long.MAX_VALUE
		"shortProp"		| Short.MAX_VALUE
		"bigDecimalProp"| BigDecimal.ONE
		"bigIntegerProp"| BigInteger.ONE
		"dateProp"		| new Date()
	}

	@Test
	@Unroll
	def "processEntity() sets #expected localizable properties when expand is #expandCond and navigationSegments is #navigationCond"() {
		setup:
		def conversionRequest = Stub(ItemConversionRequest) {
			getEntityType() >> entityType
			getItemModel() >> item
			getOptions() >> Stub(ConversionOptions) {
				isNavigationSegmentPresent() >> navigations
				isExpandPresent() >> expand
			}
		}

		entityType.getPropertyNames() >> ["name"]
		modelService.getAttributeValues(item, "name", _) >> [(Locale.ENGLISH): "English name"]
		oDataEntry.getProperties() >> [:]

		when:
		localizedAttributesPropertyProcessor.processEntity(oDataEntry, conversionRequest)

		then:
		def feed = oDataEntry.properties[LOCALIZED_ATTRIBUTES]
		feed.entries.size() == expected

		where:
		expandCond    | expand | navigationCond | navigations | expected
		'not present' | false  | 'not present'  | false       | 0
		'present'     | true   | 'not present'  | false       | 1
		'not present' | false  | 'present'      | true        | 1
		'present'     | true   | 'present'      | true        | 1
	}

	def givenTypeDescriptorAttributeNotFound() {
		localizedAttributesPropertyProcessor.findTypeDescriptorAttributeForItem(_, _) >> Optional.empty()
	}

	def conversionRequest() {
		Stub(ItemConversionRequest) {
			getEntityType() >> entityType
			getItemModel() >> item
			getOptions() >> conversionOptions()
		}
	}

	def conversionOptions() {
		Stub(ConversionOptions) {
			getNavigationSegments() >> [Stub(NavigationSegment)]
			isNavigationSegmentPresent() >> true
		}
	}

	def storageRequest(ODataEntry entry) {
		Stub(StorageRequest) {
			getODataEntry() >> entry
		}
	}

	def oDataEntryWithLocalizations(Map<String, Object>... localizations) {
		def localizedEntries = localizations.collect { map -> entry(map) }
		Stub(ODataEntry) {
			getProperties() >> [localizedAttributes: Stub(ODataFeed) { getEntries() >> localizedEntries }]
		}
	}

	def oDataFeed(final ODataEntry... entries) {
		[localizedAttributes: Stub(ODataFeed) { getEntries() >> entries }]
	}

	def entry(final Map<String, Object> entryProperties) {
		new ODataEntryImpl(entryProperties, new MediaMetadataImpl(), new EntryMetadataImpl(), new ExpandSelectTreeNodeImpl())
	}
}

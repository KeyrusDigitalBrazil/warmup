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
package de.hybris.platform.odata2services.odata.processor.writer

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.persistence.ODataFeedBuilder
import de.hybris.platform.odata2services.odata.persistence.utils.ODataEntryBuilder
import org.apache.olingo.odata2.api.edm.EdmException
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties
import org.apache.olingo.odata2.api.ep.callback.WriteEntryCallbackContext
import org.apache.olingo.odata2.api.ep.callback.WriteFeedCallbackContext
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.exception.ODataApplicationException
import org.apache.olingo.odata2.api.uri.ExpandSelectTreeNode
import org.junit.Test
import spock.lang.Specification

@UnitTest
class CallbackWriterUnitTest extends Specification {

	def NAV_PROPERTY = "navProperty"
	def OTHER_NAV_PROPERTY = "otherPropertyName"
	def SERVICE_ROOT = "our.service/root"
	def callbackWriter = new CallbackWriter()

	def writeProperties = Mock(EntityProviderWriteProperties) {
		getServiceRoot() >> URI.create(SERVICE_ROOT)
	}

	def navigationProperty = Mock(EdmNavigationProperty) {
		getName() >> { NAV_PROPERTY }
	}

	def feedCallbackContext = Mock(WriteFeedCallbackContext) {
		getCurrentWriteProperties() >> writeProperties
		getNavigationProperty() >> navigationProperty
	}

	def entryCallbackContext = Mock(WriteEntryCallbackContext) {
		getCurrentWriteProperties() >> writeProperties
		getNavigationProperty() >> navigationProperty
	}

	@Test
	def "retrieveFeedResult with one level of \$expand and one single property has one callback"() {
		given:
		def outerLevelTreeNode = Mock(ExpandSelectTreeNode) {
			getLinks() >> expandSelectTreeNode(NAV_PROPERTY)
		}
		feedCallbackContext.getCurrentExpandSelectTreeNode() >> outerLevelTreeNode
		def properties = entryProperties(NAV_PROPERTY)
		feedCallbackContext.getEntryData() >> feedDataMap(NAV_PROPERTY, properties)

		when:
		def result = callbackWriter.retrieveFeedResult(feedCallbackContext)

		then:
		result.inlineProperties.callbacks.size() == 1
		result.inlineProperties.callbacks.containsKey(NAV_PROPERTY)

		result.feedData.contains(properties)
	}

	@Test
	def "retrieveFeedResult throws exception when expanding"() {
		given:
		def outerLevelTreeNode = Mock(ExpandSelectTreeNode) {
			getLinks() >> expandSelectTreeNode(NAV_PROPERTY)
		}
		def errorProperty = Mock(EdmNavigationProperty) {
			getName() >> { throw new EdmException(EdmException.PROPERTYNOTFOUND) }
		}
		def errorContext = Mock(WriteFeedCallbackContext) {
			getCurrentWriteProperties() >> writeProperties
			getCurrentExpandSelectTreeNode() >> outerLevelTreeNode
			getNavigationProperty() >> errorProperty
		}

		when:
		callbackWriter.retrieveFeedResult(errorContext)

		then:
		thrown(ODataApplicationException)
	}

	@Test
	def "retrieveEntryResult throws exception during expand"() {
		given:
		def outerLevelTreeNode = Mock(ExpandSelectTreeNode) {
			getLinks() >> expandSelectTreeNode(NAV_PROPERTY)
		}
		def errorProperty = Mock(EdmNavigationProperty) {
			getName() >> { throw new EdmException(EdmException.PROPERTYNOTFOUND) }
		}
		def errorContext = Mock(WriteEntryCallbackContext) {
			getCurrentWriteProperties() >> writeProperties
			getCurrentExpandSelectTreeNode() >> outerLevelTreeNode
			getNavigationProperty() >> errorProperty
		}

		when:
		callbackWriter.retrieveEntryResult(errorContext)

		then:
		thrown(ODataApplicationException)
	}

	@Test
	def "retrieveEntryResult with one level of \$expand and one single property has one callback"() {
		given:
		def outerLevelTreeNode = Mock(ExpandSelectTreeNode) {
			getLinks() >> expandSelectTreeNode(NAV_PROPERTY)
		}
		entryCallbackContext.getCurrentExpandSelectTreeNode() >> outerLevelTreeNode
		def properties = entryProperties(NAV_PROPERTY)
		entryCallbackContext.getEntryData() >> entryDataMap(NAV_PROPERTY, properties)

		when:
		def result = callbackWriter.retrieveEntryResult(entryCallbackContext)

		then:
		result.inlineProperties.callbacks.size() == 1
		result.inlineProperties.callbacks.containsKey(NAV_PROPERTY)

		result.entryData == properties
	}

	@Test
	def "retrieveEntryResult with \$expand that has null value"() {
		given:
		def outerLevelTreeNode = Mock(ExpandSelectTreeNode) {
			getLinks() >> expandSelectTreeNode(NAV_PROPERTY)
		}
		entryCallbackContext.getCurrentExpandSelectTreeNode() >> outerLevelTreeNode
		entryCallbackContext.getEntryData() >> Maps.newHashMap()

		when:
		def result = callbackWriter.retrieveEntryResult(entryCallbackContext)

		then:
		result.inlineProperties.callbacks.size() == 1
		result.inlineProperties.callbacks.containsKey(NAV_PROPERTY)

		result.entryData == null
	}

	@Test
	def "retrieveEntryResult with \$expand that has null value for collection"() {
		given:
		def outerLevelTreeNode = Mock(ExpandSelectTreeNode) {
			getLinks() >> expandSelectTreeNode(NAV_PROPERTY)
		}
		feedCallbackContext.getCurrentExpandSelectTreeNode() >> outerLevelTreeNode
		feedCallbackContext.getEntryData() >> Mock(HashMap)
		properties.get(_ as String) >> null

		when:
		def result = callbackWriter.retrieveFeedResult(feedCallbackContext)

		then:
		result.inlineProperties.callbacks.size() == 1
		result.inlineProperties.callbacks.containsKey(NAV_PROPERTY)

		result.feedData == []
	}

	Map<String, ExpandSelectTreeNode> expandSelectTreeNode(propertyName)
	{
		def links = new HashMap<>()
		def innerLevelTreeNode = Mock(ExpandSelectTreeNode) {
			getLinks() >> new HashMap<>()
		}
		links.putIfAbsent(propertyName, innerLevelTreeNode)
		links
	}

	Map<String, ExpandSelectTreeNode> twoLevelExpandSelectTreeNode()
	{
		def links = new HashMap<>()
		def innerLevelTreeNode = Mock(ExpandSelectTreeNode) {
			getLinks() >> expandSelectTreeNode(OTHER_NAV_PROPERTY)
		}
		links.putIfAbsent(NAV_PROPERTY, innerLevelTreeNode)
		links
	}

	Map<Object, Object> feedDataMap(final String navProperty, final ImmutableMap<String, Object> properties)
	{
		ImmutableMap.builder()
				.put(navProperty,
				ODataFeedBuilder.newInstance()
						.withEntries(createEntryList(properties))
						.build())
				.build()
	}

	Map<Object, Object> entryDataMap(final String navProperty, final ImmutableMap<String, Object> properties)
	{
		ImmutableMap.builder()
				.put(navProperty,
				ODataEntryBuilder.newInstance()
						.withProperties(properties)
						.build())
				.build()
	}

	List<ODataEntry> createEntryList(final ImmutableMap<String, Object> properties)
	{
		Arrays.asList(ODataEntryBuilder.newInstance().withProperties(properties).build())
	}

	ImmutableMap entryProperties(final String propName)
	{
		new ImmutableMap.Builder()
				.put("code", propName)
				.put("name", "item name")
				.put("navigationProperty", "someNavProperty")
				.build()
	}
}

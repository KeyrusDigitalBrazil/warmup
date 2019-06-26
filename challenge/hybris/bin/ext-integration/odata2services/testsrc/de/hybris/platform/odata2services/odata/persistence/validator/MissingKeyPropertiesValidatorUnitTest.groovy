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
package de.hybris.platform.odata2services.odata.persistence.validator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.persistence.exception.MissingKeyPropertyException
import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute
import org.apache.olingo.odata2.api.edm.EdmAnnotations
import org.apache.olingo.odata2.api.edm.EdmEntityType
import org.apache.olingo.odata2.api.edm.EdmProperty
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.junit.Test
import spock.lang.Specification

@UnitTest
class MissingKeyPropertiesValidatorUnitTest extends Specification {

	def validator = new MissingKeyPropertiesValidator()

	@Test
	def beforeItemLookup() {
		given:
		final ODataEntry entry = Mock(ODataEntry)

		when:
		validator.beforeItemLookup(Stub(EdmEntityType.class), entry)

		then:
		1 * entry.getProperties()
	}

	@Test
	def "validation is successful when all keys are present and have a value"() {
		given:
		def propertiesMap = new HashMap<>()
		propertiesMap.put("key1", "value1")
		propertiesMap.put("key2", "value2")

		def entityType = createEntityTypeWith2Keys(false, false)

		final ODataEntry entry = Stub(ODataEntry) {
			getProperties() >> propertiesMap
		}

		when:
		validator.beforeItemLookup(entityType, entry)

		then:
		noExceptionThrown()
	}


	@Test
	def "validation is successful when all keys are present some have a null value"() {
		given:
		def entityType = createEntityTypeWith2Keys(false, false)

		final ODataEntry entry = Stub(ODataEntry) {
			getProperties() >> ['key1': 'value1', 'key2': null]
		}

		when:
		validator.beforeItemLookup(entityType, entry)

		then:
		noExceptionThrown()
	}

	private EdmEntityType createEntityTypeWith2Keys(final boolean isKey1Nullable, final boolean isKey2Nullable) {
		Stub(EdmEntityType) {
			getName() >> "ATypeName"
			getProperty("key1") >> createKeyProperty(isKey1Nullable)
			getProperty("key2") >> createKeyProperty(isKey2Nullable)
			getPropertyNames() >> Arrays.asList("key1", "key2")
		}
	}

	@Test
	def "validation throws exception when non nullable key property is not present"() {
		given:
		def entityType = createEntityTypeWith2Keys(false, false)

		final ODataEntry entry = Stub(ODataEntry) {
			getProperties() >> ['key1': 'value1']
		}

		when:
		validator.beforeItemLookup(entityType, entry)

		then:
		MissingKeyPropertyException e = thrown()
		e.getMessage() == 'Key [key2] is required for EntityType [ATypeName]'
	}

	@Test
	def "validation is successful when nullable key property is not present"() {
		given:
		def entityType = createEntityTypeWith2Keys(false, true)

		final ODataEntry entry = Stub(ODataEntry) {
			getProperties() >> ['key1': 'value1']
		}

		when:
		validator.beforeItemLookup(entityType, entry)

		then:
		noExceptionThrown()
	}

	def createAnnotations(final EdmAnnotationAttribute uniqueAttribute, final EdmAnnotationAttribute nullableAttribute) {
		def annotations = Stub(EdmAnnotations) {
			getAnnotationAttributes() >> Arrays.asList(uniqueAttribute, nullableAttribute)
		}
		annotations
	}

	def createUniqueAnnotationAttribute() {
		def attribute = Stub(EdmAnnotationAttribute) {
			getName() >> "s:IsUnique"
			getText() >> "true"
		}
		attribute
	}

	def createNullableAnnotationAttribute(final boolean nullable) {
		def attribute = Stub(EdmAnnotationAttribute) {
			getName() >> "Nullable"
			getText() >> String.valueOf(nullable)
		}
		attribute
	}

	def createKeyProperty(final boolean nullable) {
		final EdmAnnotationAttribute uniqueAttribute = createUniqueAnnotationAttribute()
		final EdmAnnotationAttribute nullableAttribute = createNullableAnnotationAttribute(nullable)
		def annotations = createAnnotations(uniqueAttribute, nullableAttribute)
		def property = Stub(EdmProperty) {
			getAnnotations() >> annotations
		}
		property
	}
}
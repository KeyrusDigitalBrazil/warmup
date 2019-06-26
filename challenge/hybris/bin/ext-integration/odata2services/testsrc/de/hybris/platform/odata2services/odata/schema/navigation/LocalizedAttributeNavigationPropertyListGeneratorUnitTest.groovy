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
package de.hybris.platform.odata2services.odata.schema.navigation

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.odata2services.TestConstants
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder
import static org.mockito.Matchers.anyCollection
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.spy

@UnitTest
class LocalizedAttributeNavigationPropertyListGeneratorUnitTest extends Specification
{
	// Can't use Spy when the class implements an interface that has generics, have to use Mockito.spy instead
	def generator = spy(LocalizedAttributeNavigationPropertyListGenerator)

	@Test
	def "generator returns a collection of one localized navigation property when there are localized attributes"()
	{
		given:
		localizedAttributes()
		
		when:
		def collection = generator.generate(attributeModels())

		then:
		collection.size() == 1
		def navProp = collection.get(0)
		with(navProp)
		{
			getName() == "localizedAttributes"
			getRelationship().getName() == "FK_Product_localizedAttributes"
			getFromRole() == "Product"
			getToRole() ==  "${TestConstants.LOCALIZED_ENTITY_PREFIX}Product"
		}

		def annotations = navProp.getAnnotationAttributes()
		annotations.size() == 1
		def annotation = annotations.get(0)
		with(annotation)
		{
			getName() == "Nullable"
			getText() == "true"
		}
	}

	@Test
	@Unroll
	def "generator returns an empty collection when attribute collection #msg"()
	{
		given:
		noLocalizedAttributes()

		when:
		def collection = generator.generate(attributes)

		then:
		collection.isEmpty()

		where:
		attributes         | msg
		[]                 | "is empty"
		attributeModels()  | "has no localized attributes"
	}

	@Test
	def "generator throws exception when attribute collection is null"()
	{
		when:
		generator.generate(null)

		then:
		thrown IllegalArgumentException
	}

	def attributeModels()
	{
		return [simpleAttributeBuilder().build(), simpleAttributeBuilder().build()]
	}

	def typeAttributeDescriptorWithLocalizedSetTo(localized) {
		Mock(TypeAttributeDescriptor.class) {
			isLocalized() >> localized
			getTypeDescriptor() >> Mock(TypeDescriptor) {
				getTypeCode() >> "Product"
			}
		}
	}

	def noLocalizedAttributes()
	{
		doReturn(Optional.empty()).when(generator).findFirstLocalizedAttribute(anyCollection())
	}

	def localizedAttributes()
	{
		def descriptor = Optional.of(typeAttributeDescriptorWithLocalizedSetTo(true))
		doReturn(descriptor).when(generator).findFirstLocalizedAttribute(anyCollection())
	}
}

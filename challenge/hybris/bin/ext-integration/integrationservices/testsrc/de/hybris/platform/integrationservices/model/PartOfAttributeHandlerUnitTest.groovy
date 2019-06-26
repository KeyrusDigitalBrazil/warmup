package de.hybris.platform.integrationservices.model

import de.hybris.bootstrap.annotations.UnitTest
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.collectionDescriptor
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.*
import static de.hybris.platform.integrationservices.model.MockAttributeDescriptorModelBuilder.attributeDescriptorModelBuilder
import static de.hybris.platform.integrationservices.model.MockRelationAttributeDescriptorModelBuilder.relationAttribute

@UnitTest
class PartOfAttributeHandlerUnitTest extends Specification {
    private final PartOfAttributeHandler handler = new PartOfAttributeHandler()

    @Test
    @Unroll
    def "partOf is #res when the attribute descriptor has it set to #partOf"()
    {
        given:
        def attr = attributeDefinitionWith(attributeDescriptorModelBuilder().withPartOf(partOf))

        expect:
        handler.get(attr) == res

        where:
        partOf | res
        true   | true
        false  | false
        null   | false
    }

    @Test
    @Unroll
    def "partOf is #res when the attribute relation source type has it set to #partOf"()
    {
        given:
        def attr = sourceAttributeDefinitionWith(relationAttribute().withPartOf(partOf))

        expect:
        handler.get(attr) == res

        where:
        partOf | res
        true   | true
        false  | false
        null   | false
    }

    @Test
    @Unroll
    def "partOf is #res when the attribute relation target type has it set to #partOf"()
    {
        given:
        def attr = targetAttributeDefinitionWith(relationAttribute().withPartOf(partOf))

        expect:
        handler.get(attr) == res

        where:
        partOf | res
        true   | true
        false  | false
        null   | false
    }

    @Test
    @Unroll
    def "partOf is #res when the collection attribute descriptor has it set to #partOf"()
    {
        given:
        def attr = attributeDefinitionWith(collectionDescriptor().withPartOf(partOf))

        expect:
        handler.get(attr) == res

        where:
        partOf | res
        true   | true
        false  | false
        null   | false
    }

    private static def attributeDefinitionWith(final MockAttributeDescriptorModelBuilder descriptor)
    {
        simpleAttributeBuilder().withAttributeDescriptor(descriptor).build()
    }

    private static def targetAttributeDefinitionWith(final MockRelationAttributeDescriptorModelBuilder attribute)
    {
        complexRelationAttributeBuilder().withTargetAttribute(attribute).build()
    }

    private static def sourceAttributeDefinitionWith(final MockRelationAttributeDescriptorModelBuilder attribute)
    {
        complexRelationAttributeBuilder().withSourceAttribute(attribute).build()
    }

    private static def attributeDefinitionWith(final MockCollectionDescriptorModelBuilder attribute)
    {
        collectionAttributeBuilder().withAttributeDescriptor(attribute).build()
    }
}

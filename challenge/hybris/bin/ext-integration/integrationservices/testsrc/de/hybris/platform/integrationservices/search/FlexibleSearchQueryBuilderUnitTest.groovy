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

package de.hybris.platform.integrationservices.search

import com.google.common.collect.ImmutableMap
import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import org.apache.commons.lang3.time.DateUtils
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder
import static de.hybris.platform.integrationservices.model.MockIntegrationObjectItemModelBuilder.itemModelBuilder
import static org.assertj.core.api.Assertions.assertThat

@UnitTest
class FlexibleSearchQueryBuilderUnitTest extends Specification {
    def service = Mock(IntegrationObjectService)
    def builder = new FlexibleSearchQueryBuilder(service)

    @Test
    def "constructor cannot be called with null service"()
    {
        when:
        new FlexibleSearchQueryBuilder(null)

        then:
        def e = thrown(IllegalArgumentException)
        ! e.message.isEmpty()
    }
    
    @Test
    def "builds empty string query when specifications not made"()
    {
        when: "no specification were done on the builder"
        def query = builder.build()

        then: "query is an empty string"
        query
        query.query == ''
        query.queryParameters.isEmpty()
    }

    @Test
    @Unroll
    def "generates SELECT FROM #fromClause type for an existing integration object item of #type type"()
    {
        given:
        service.findAllIntegrationObjectItems('myObject') >> items

        when:
        builder.withIntegrationObjectItem('myObject', 'myItem')
        def query = builder.build().query

        then:
        query.toUpperCase().startsWith 'SELECT {MYITEM:PK}'
        query.toUpperCase().contains ' FROM '
        query.trim().endsWith fromClause

        where:
        type			| items													| fromClause
        'composed'		| [item('myItem'), item('anotherItem')]		| '{myItem* AS myitem}'
        'enumeration'	| [item('other'), enumerationItem('myItem')]	| '{myItem AS myitem}'
    }

    @Test
    def "illegal state if key condition added without integration object item"()
    {
        when:
        builder.withKeyConditionFor(new HashMap<String, Object>()).build()

        then:
        def e = thrown(IllegalStateException)
        ! e.message.isEmpty()
    }

    @Test
    def "key condition added for integration object item without key attributes"()
    {
        when:
        def query = builder
                .withIntegrationObjectItem(item("ValueObject"))
                .withKeyConditionFor(new HashMap<String, Object>())
                .build()

        then:
        !query.query.toLowerCase().contains("where")
        query.queryParameters.isEmpty()
    }

    @Test
    def "key condition uses Date for Calendar attribute values"()
    {
        given:
        def now = new Date()
        def item = ImmutableMap.of("key", DateUtils.toCalendar(now))
        def itemMetadata = itemWithSimpleKey("key")

        when:
        def query = builder
                .withIntegrationObjectItem(itemMetadata)
                .withKeyConditionFor(item)
                .build()

        then:
        query.queryParameters["key"] == now
    }

    @Test
    def "ORDER BY is added to the query when orderByPk is called"()
    {
        given:
        def itemMetadata = itemWithSimpleKey("key")

        when:
        def query = builder
                .withIntegrationObjectItem(itemMetadata)
                .orderedByPK()
                .build()
                .query

        then:
        query.toUpperCase().contains('ORDER BY {PRODUCT:PK}')
    }

    @Test
    def "ORDER BY is not present in the query by default"()
    {
        when:
        def query = builder
                .build()
                .query

        then:
        ! query.toUpperCase().contains('ORDER BY {PK}')
    }

    @Test
    def "can request total count in the response"()
    {
        when:
        def queryWithoutTotal = builder.build()
        def queryWithTotal = builder.withTotalCount().build()

        then:
        !queryWithoutTotal.needTotal
        queryWithTotal.needTotal
    }

    @Test
    def "count and start provided to search query builder"()
    {
        when:
        def query = builder
                .withCount(5)
                .withStart(10)
                .build()

        then:
        query.getCount() == 5
        query.getStart() == 10
    }

    @Test
    @Unroll
    def "exception thrown when specified integration #object does not exist"()
    {
        given:
        service.findAllIntegrationObjectItems('myObject') >> foundItems

        when:
        builder.withIntegrationObjectItem('myObject', 'myItem')

        then:
        thrown(IllegalArgumentException)

        where:
        object        | foundItems
        'object'      | []
        'object item' | [item('itemOne'), item('itemTwo')]
    }

    @Test
    def "WHERE condition is present only once when same parameter added several times"()
    {
        given:
        def itemMetadata = itemWithSimpleKey("key")

        when:
        def query = builder
                .withIntegrationObjectItem(itemMetadata)
                .withParameter("param", "value1")
                .withParameter("param", "value2")
                .build()

        then:
        assertThat(query.query.toLowerCase())
                .contains("param")
                .doesNotContain("and")
        assertThat(query.queryParameters.values()).containsOnly('value2')
    }

    @Test
    def "generates SELECT FROM WHERE ORDER BY when item type, a parameter, and order by are specified"()
    {
        when:
        def query = builder
                .withIntegrationObjectItem(item('SomeType'))
                .withParameter("param", new Object())
                .orderedByPK()
                .build()

        then:
        assertThat(query.query.capitalize())
                .startsWith("SELECT")
                .contains(" WHERE ")
                .contains(" ORDER BY ")
    }

    @Test
    def "generates WHERE clause when key condition added for an item with a simple key"()
    {
        given:
        def item = ImmutableMap.of("productCode", "A1")
        def itemMetadata = itemWithSimpleKey("productCode", "code")

        when:
        def query = builder
                .withIntegrationObjectItem(itemMetadata)
                .withKeyConditionFor(item)
                .build()

        then:
        assertThat(query.query)
                .containsIgnoringCase(" where ")
                .contains("code")
        assertThat(query.queryParameters).containsEntry("code", "A1")
    }

    @Test
    def "WHERE clause is not generated if the key condition added for an item with reference key"()
    {
        given:
        def item = new HashMap()
        def itemMetadata = itemModelBuilder()
                .withUniqueAttribute(simpleAttributeBuilder()
                .withName("code")
                .withReturnIntegrationObject("ReferencedType")
                .unique())
                .build()

        when:
        def query = builder
                .withIntegrationObjectItem(itemMetadata)
                .withKeyConditionFor(item)
                .build()

        then:
        !query.query.toLowerCase().contains("where")
        query.queryParameters.isEmpty()
    }

    def enumerationItem(final String code) {
        def type = Mock(EnumerationMetaTypeModel) {
            getCode() >> code
        }

        Mock(IntegrationObjectItemModel) {
            getCode() >> code
            getType() >> type
        }
    }

    def item(final String code) {
        item(code, code)
    }

    def item(final String integrationCode, final String platformCode) {
        Mock(IntegrationObjectItemModel) {
            getCode() >> integrationCode
            getType() >> Mock(ComposedTypeModel) {
                getCode() >> platformCode
            }
            getUniqueAttributes() >> []
        }
    }

    private IntegrationObjectItemModel itemWithSimpleKey(String attrName) {
        itemWithSimpleKey(attrName, attrName)
    }

    private IntegrationObjectItemModel itemWithSimpleKey(String attrName, String qualifier) {
        def item = Mock(IntegrationObjectItemModel) {
            getType() >> Mock(ComposedTypeModel) {
                getCode() >> "Product"
            }
        }
        def attribute = Mock(IntegrationObjectItemAttributeModel) {
            getAttributeName() >> attrName
            getIntegrationObjectItem() >> item // it's important to refer back to the same item
            getAttributeDescriptor() >> Mock(AttributeDescriptorModel) {
                getQualifier() >> qualifier
            }
        }
        item.uniqueAttributes >> [attribute]
        item
    }
}

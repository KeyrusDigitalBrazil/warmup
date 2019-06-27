/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.filter.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.filter.ExpressionVisitorParameters
import de.hybris.platform.odata2services.odata.integrationkey.IntegrationKeyToODataEntryGenerator
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequestFactory
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupStrategy
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.uri.UriInfo
import org.junit.Test
import org.springframework.core.convert.converter.Converter
import spock.lang.Specification

@UnitTest
class DefaultExpressionVisitorFactoryUnitTest extends Specification
{
	def integrationKeyConverter = Mock(IntegrationKeyToODataEntryGenerator)
	def itemLookupRequestFactory = Mock(ItemLookupRequestFactory)
	def itemLookupStrategy = Mock(ItemLookupStrategy)
	def operatorConverter = Mock(Converter)
	def entitySetNameGenerator = Mock(EntitySetNameGenerator)

	def expressionVisitorParameters = Mock(ExpressionVisitorParameters)

	def expressionVisitorFactory = new DefaultExpressionVisitorFactory()

	def setup()
	{
		expressionVisitorFactory.setIntegrationKeyConverter(integrationKeyConverter)
		expressionVisitorFactory.setItemLookupRequestFactory(itemLookupRequestFactory)
		expressionVisitorFactory.setItemLookupStrategy(itemLookupStrategy)
		expressionVisitorFactory.setOperatorConverter(operatorConverter)
		expressionVisitorFactory.setEntitySetNameGenerator(entitySetNameGenerator)

		expressionVisitorParameters.getUriInfo() >> Mock(UriInfo)
		expressionVisitorParameters.getContext() >> Mock(ODataContext)
	}
	
	@Test
	def "create creates BinaryExpressionVisitor with 4 strategies"()
	{
		when:
		def expressionVisitor = (DefaultExpressionVisitor) expressionVisitorFactory.create(expressionVisitorParameters)
		def binaryExpressionVisitor = (DefaultBinaryExpressionVisitor) expressionVisitor.getBinaryExpressionVisitor()

		then:
		binaryExpressionVisitor.getStrategies().size() == 4
	}

	@Test
	def "createNavigationPropertyWithIntegrationKeyVisitingStrategy creates expected NavigationPropertyWithIntegrationKeyVisitingStrategy"()
	{
		when:
		def navigationPropertyWithIntegrationKeyVisitingStrategy = (NavigationPropertyWithIntegrationKeyVisitingStrategy) expressionVisitorFactory.createNavigationPropertyWithIntegrationKeyVisitingStrategy(expressionVisitorParameters)

		then:
		navigationPropertyWithIntegrationKeyVisitingStrategy.getIntegrationKeyConverter() == integrationKeyConverter
		navigationPropertyWithIntegrationKeyVisitingStrategy.getContext() == expressionVisitorParameters.getContext()
		navigationPropertyWithIntegrationKeyVisitingStrategy.getItemLookupRequestFactory() == itemLookupRequestFactory
		navigationPropertyWithIntegrationKeyVisitingStrategy.getItemLookupStrategy() == itemLookupStrategy
		navigationPropertyWithIntegrationKeyVisitingStrategy.getOperatorConverter() == operatorConverter
	}


	@Test
	def "createCombineWhereClauseConditionsVisitingStrategy creates expected CombineWhereClauseConditionsVisitingStrategy"()
	{
		when:
		def combineWhereClauseConditionVisitingStrategy = (CombineWhereClauseConditionVisitingStrategy) expressionVisitorFactory.createCombineWhereClauseConditionsVisitingStrategy()

		then:
		combineWhereClauseConditionVisitingStrategy.getOperatorConverter() == operatorConverter
	}

	@Test
	def "createSimplePropertyVisitingStrategy creates expected SimplePropertyVisitingStrategy"()
	{
		when:
		def simplePropertyVisitingStrategy = (SimplePropertyVisitingStrategy) expressionVisitorFactory.createSimplePropertyVisitingStrategy()

		then:
		simplePropertyVisitingStrategy instanceof SimplePropertyVisitingStrategy
		simplePropertyVisitingStrategy.getOperatorConverter() == operatorConverter
	}

	@Test
	def "createNavigationPropertyVisitingStrategy creates expected createNavigationPropertyVisitingStrategy"()
	{
		when:
		def navigationPropertyVisitingStrategy = (NavigationPropertyVisitingStrategy) expressionVisitorFactory.createNavigationPropertyVisitingStrategy(expressionVisitorParameters)
		
		then:
		navigationPropertyVisitingStrategy instanceof NavigationPropertyVisitingStrategy
		navigationPropertyVisitingStrategy.getContext() == expressionVisitorParameters.getContext()
		navigationPropertyVisitingStrategy.getItemLookupRequestFactory() == itemLookupRequestFactory
		navigationPropertyVisitingStrategy.getItemLookupStrategy() == itemLookupStrategy
		navigationPropertyVisitingStrategy.getOperatorConverter() == operatorConverter
	}

	@Test
	def "create creates MemberExpressionVisitor with entity set name generator and uri info" ()
	{
		when:
		def memberExpressionVisitor = (DefaultMemberExpressionVisitor) expressionVisitorFactory.createMemberExpressionVisitor(expressionVisitorParameters)

		then:
		memberExpressionVisitor.getEntitySetNameGenerator() == entitySetNameGenerator
		memberExpressionVisitor.getUriInfo() == expressionVisitorParameters.getUriInfo()
	}

	@Test
	def "createPropertyExpressionVisitor creates DefaultPropertyExpressionVisitor" ()
	{
		when:
		def propertyExpressionVisitor = expressionVisitorFactory.createPropertyExpressionVisitor()

		then:
		propertyExpressionVisitor instanceof DefaultPropertyExpressionVisitor
	}

	@Test
	def "createLiteralExpressionVisitor creates DefaultLiteralExpressionVisitor" ()
	{
		when:
		def literalExpressionVisitor = expressionVisitorFactory.createLiteralExpressionVisitor()

		then:
		literalExpressionVisitor instanceof DefaultLiteralExpressionVisitor
	}

	@Test
	def "create creates a new instance for every call"()
	{
		when:
		def expressionVisitor1 = expressionVisitorFactory.create(expressionVisitorParameters)
		def expressionVisitor2 = expressionVisitorFactory.create(expressionVisitorParameters)

		then:
		expressionVisitor1 != expressionVisitor2
	}
}
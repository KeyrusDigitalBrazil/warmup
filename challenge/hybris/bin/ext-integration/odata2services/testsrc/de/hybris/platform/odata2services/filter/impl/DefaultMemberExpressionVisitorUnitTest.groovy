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
package de.hybris.platform.odata2services.filter.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.filter.NestedFilterNotSupportedException
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator
import org.apache.olingo.odata2.api.edm.EdmEntityContainer
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.edm.EdmException
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty
import org.apache.olingo.odata2.api.uri.UriInfo
import org.apache.olingo.odata2.api.uri.expression.MemberExpression
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression
import org.junit.Test
import spock.lang.Specification

@UnitTest
class DefaultMemberExpressionVisitorUnitTest extends Specification
{
	def entitySetNameGenerator = Mock(EntitySetNameGenerator)
	def uriInfo = Mock(UriInfo)

	def visitor = new DefaultMemberExpressionVisitor()

	def setup()
	{
		visitor.setEntitySetNameGenerator(entitySetNameGenerator)
		visitor.setUriInfo(uriInfo)
	}

	@Test
	def "exception is thrown if expression has multiple levels of nesting"()
	{
		given:
		def expression = Mock(MemberExpression) {
			getPath() >> Mock(PropertyExpression) {
				getUriLiteral() >> "catalogVersion"
			}
			getProperty() >> Mock(PropertyExpression) {
				getUriLiteral() >> "catalog"
			}
		}

		when:
		visitor.visit(expression, null, Mock(EdmNavigationProperty))

		then:
		NestedFilterNotSupportedException e = thrown()
		e.getMessage().concat("catalogVersion/catalog")
	}

	def "visit returns an entity set"()
	{
		given:
		def entitySet = Mock(EdmEntitySet)

		def path = Mock(EdmNavigationProperty) {
			getToRole() >> "CatalogVersion"
		}

		uriInfo.getEntityContainer() >> Mock(EdmEntityContainer) {
			getEntitySet(_ as String) >> entitySet
		}

		entitySetNameGenerator.generate(_ as String) >> "CatalogVersions"

		when:
		def result = visitor.visit(null, path, null)

		then:
		result == entitySet
	}

	@Test
	def "visit returns pathResult"()
	{
		given:
		def path = "Some String"

		when:
		def result = visitor.visit(null, path, null)

		then:
		result == path
	}

	@Test
	def "visit throws an exception"()
	{
		given:
		def path = Mock(EdmNavigationProperty) {
			getToRole() >> "CatalogVersion"
		}

		entitySetNameGenerator.generate(_ as String) >> "CatalogVersions"

		uriInfo.getEntityContainer() >> Mock(EdmEntityContainer) {
			getEntitySet(_ as String) >> { throw Mock(EdmException) }
		}

		when:
		visitor.visit(null, path, null)

		then:
		thrown InternalProcessingException
	}
}

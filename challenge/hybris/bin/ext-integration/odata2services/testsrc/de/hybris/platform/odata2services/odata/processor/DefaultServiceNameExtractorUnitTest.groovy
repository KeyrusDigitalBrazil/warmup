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
package de.hybris.platform.odata2services.odata.processor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException
import org.apache.olingo.odata2.api.exception.ODataException
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.uri.PathInfo
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class DefaultServiceNameExtractorUnitTest extends Specification {

	def serviceNameExtractor = new DefaultServiceNameExtractor()

	@Test
	@Unroll
	def "successfully extracted the service name from #uri"()
	{
		def serviceRoot = URI.create(uri)
		def pathInfo = Mock(PathInfo)
		pathInfo.getServiceRoot() >> serviceRoot
		def context = Mock(ODataContext)
		context.getPathInfo() >> pathInfo

		expect:
		def serviceName = serviceNameExtractor.extract(context, "key")
		serviceName == "serviceName"

		where:
		uri << ["https://localhost:123/context/serviceName/",
				"https://localhost:123/context/serviceName",
				"serviceName",
				"/serviceName/"]
	}

	@Test
	def "getting pathInfo from context throws ODataException"()
	{
		def context = Mock(ODataContext)
		context.getPathInfo() >> { throw new ODataException("testing throw exception") }

		when:
		serviceNameExtractor.extract(context, "key")

		then:
		thrown(InternalProcessingException)
	}

	@Test
	def "null service root throws InternalProcessingException"()
	{
		def pathInfo = Mock(PathInfo)
		def context = Mock(ODataContext)
		context.getPathInfo() >> pathInfo

		when:
		serviceNameExtractor.extract(context, "key")

		then:
		thrown(InternalProcessingException)
	}

	@Test
	def "null path throws InternalProcessingException"()
	{
		def serviceRoot = GroovyMock(URI)
		serviceRoot.getPath() >> null
		def pathInfo = Mock(PathInfo)
		pathInfo.getServiceRoot() >> serviceRoot
		def context = Mock(ODataContext)
		context.getPathInfo() >> pathInfo

		when:
		serviceNameExtractor.extract(context, "key")

		then:
		thrown(InternalProcessingException)
	}
}

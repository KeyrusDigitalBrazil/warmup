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
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class ODataNextLinkTest extends Specification {
	static final String BASE_URI = "https://my.odata/service/Products"
	def builder = ODataNextLink.Builder.nextLink()
	def currentUri = BASE_URI
	def lookupRequest = Mock(ItemLookupRequest)

	def setup()
	{
		lookupRequest.getRequestUri() >> { URI.create(currentUri) }
	}

	@Test
	def "builder is missing currentLink"()
	{
		given:
		lookupRequest.getSkip() >> 0
		lookupRequest.getTop() >> 20

		when:
		builder.withLookupRequest(lookupRequest)
				.withTotalCount(0)
				.build()
		then:
		lookupRequest.getRequestUri() >> null
		def e = thrown(IllegalArgumentException)
		e.message.contains("requestUri")
	}

	@Test
	def "builder is missing itemLookupRequest"()
	{
		given:
		lookupRequest.getSkip() >> 0
		lookupRequest.getTop() >> 20
		
		when:
		builder.withTotalCount(0)
				.build()
		then:
		def e = thrown(IllegalArgumentException)
		e.message.contains("itemLookupRequest")
	}

	@Test
	def "builder is missing skip"()
	{
		given:
		lookupRequest.getTop() >> 20

		when:
		builder.withLookupRequest(lookupRequest)
				.withTotalCount(0)
				.build()
		then:
		def e = thrown(IllegalArgumentException)
		e.message.contains("skip")
	}

	@Test
	def "builder is missing top"()
	{
		given:
		lookupRequest.getSkip() >> 0

		when:
		builder.withLookupRequest(lookupRequest)
				.withTotalCount(0)
				.build()
		then:
		def e = thrown(IllegalArgumentException)
		e.message.contains("top")
	}

	@Unroll
	@Test
	def "builder has invalid count"()
	{
		given:
		lookupRequest.getSkip() >> 0
		lookupRequest.getTop() >> 20
		
		when:
		builder.withLookupRequest(lookupRequest)
				.withTotalCount(count)
				.build()
		then:
		def e = thrown(IllegalArgumentException)
		e.message.contains("totalCount")

		where:
		count << [-1, null]
	}

	@Test
	def "no next link - request without \$skip or \$skiptoken parameter"()
	{
		given:
		lookupRequest.getSkip() >> 0
		lookupRequest.getTop() >> 20

		when:
		builder.withLookupRequest(lookupRequest)
				.withTotalCount(10)

		then:
		builder.build() == null
	}

	@Test
	def "no next link - request with \$skip parameter"()
	{
		given:
		currentUri += "?\$skip=3"
		lookupRequest.getSkip() >> 3
		lookupRequest.getTop() >> 20

		when:
		builder.withLookupRequest(lookupRequest)
				.withTotalCount(10)

		then:
		builder.build() == null
	}

	@Test
	def "no next link - request with \$skiptoken parameter"()
	{
		given:
		currentUri += "?\$skiptoken=5"
		lookupRequest.getSkip() >> 5
		lookupRequest.getTop() >> 20

		when:
		builder.withLookupRequest(lookupRequest)
				.withTotalCount(10)

		then:
		builder.build() == null
	}

	@Test
	def "next should be provided - request with \$skip parameter"()
	{
		given:
		currentUri += "?\$top=3&\$skip=4"
		lookupRequest.getTop() >> 3
		lookupRequest.getSkip() >> 4

		when:
		def next = builder.withLookupRequest(lookupRequest)
				.withTotalCount(10)
				.build()

		then:
		next.contains("\$skiptoken=7")
		next.contains("\$top=3")
	}

	@Test
	def "next should be provided - request with \$skiptoken parameter"()
	{
		given:
		currentUri += "?\$top=4&\$skiptoken=5"
		lookupRequest.getTop() >> 4
		lookupRequest.getSkip() >> 5

		when:
		def next = builder.withLookupRequest(lookupRequest)
				.withTotalCount(10)
				.build()

		then:
		next.contains("\$skiptoken=9")
		next.contains("\$top=4")
	}

	@Test
	def "request without \$skip or \$skiptoken"()
	{
		given:
		currentUri += "?\$top=5"
		lookupRequest.getTop() >> 5
		lookupRequest.getSkip() >> 0

		when:
		def next = builder.withLookupRequest(lookupRequest)
				.withTotalCount(10)
				.build()

		then:
		next.contains("\$skiptoken=5")
		next.contains("\$top=5")
	}

	@Test
	def "request without \$top"()
	{
		given:
		lookupRequest.getTop() >> 5 // simulating default page size == 5
		lookupRequest.getSkip() >> 0

		when:
		def next = builder.withLookupRequest(lookupRequest)
				.withTotalCount(10)
				.build()

		then:
		next.contains("\$skiptoken=5")
		! next.contains("\$top")
	}

	@Test
	def "request includes \$inlinecount and \$orderby"()
	{
		given:
		currentUri += "?\$skip=0&\$top=7&\$inlinecount=allpages&\$orderby=code"
		lookupRequest.getTop() >> 7
		lookupRequest.getSkip() >> 0

		when:
		def next = builder.withLookupRequest(lookupRequest)
				.withTotalCount(10)
				.build()

		then:
		next == BASE_URI + "?\$top=7&\$inlinecount=allpages&\$orderby=code&\$skiptoken=7"
	}

	@Test
	def "next contains base uri"()
	{
		given:
		lookupRequest.getTop() >> 7
		lookupRequest.getSkip() >> 0

		when:
		def next = builder.withLookupRequest(lookupRequest)
				.withTotalCount(10)
				.build()

		then:
		next.contains(BASE_URI)
	}

	@Test
	def "next does not contain \$skip"()
	{
		given:
		lookupRequest.getTop() >> 5
		lookupRequest.getSkip() >> 0

		when:
		def next = builder.withLookupRequest(lookupRequest)
				.withTotalCount(10)
				.build()

		then:
		! next.contains("\$skip=")
	}
}

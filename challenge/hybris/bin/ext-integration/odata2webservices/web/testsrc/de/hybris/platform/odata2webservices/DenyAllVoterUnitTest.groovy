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
package de.hybris.platform.odata2webservices

import de.hybris.bootstrap.annotations.UnitTest
import org.junit.Test
import org.springframework.security.access.ConfigAttribute
import org.springframework.security.web.FilterInvocation
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class DenyAllVoterUnitTest extends Specification
{
	def voter = new DenyAllVoter()

	@Test
	@Unroll
	def "supports is #isSupport when attribute is '#attribute'"()
	{
		given:
		def configAttribute = Stub(ConfigAttribute) {
			getAttribute() >> attribute
		}

		expect:
		voter.supports(configAttribute) == isSupport

		where:
		attribute 	| isSupport
		"DENY_ALL" 	| true
		"other" 	| false
		null		| false
		""			| false
	}

	@Test
	@Unroll
	def "supports is #isSupport when class is #clazz"()
	{
		expect:
		voter.supports(clazz) == isSupport

		where:
		clazz            		| isSupport
		FilterInvocation.class 	| true
		DenyAllVoter.class      | false
	}

	@Test
	@Unroll
	def "vote is #vote when attribute is '#attribute'"()
	{
		given:
		def configAttribute = Stub(ConfigAttribute) {
			getAttribute() >> attribute
		}

		expect:
		voter.vote(null, null, [configAttribute]) == vote

		where:
		attribute 	| vote
		"DENY_ALL" 	| -1
		"other" 	| 0
	}
}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.advice

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.users.AbstractUserTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static org.apache.http.HttpStatus.SC_OK

@IntegrationTest
@Unroll
class ApiDocsAdviceTest extends AbstractUserTest {

	def "Customer gets API documentation without number suffix for non-duplicates"() {
		when: "user attempts to get API documentation"
		HttpResponseDecorator response = restClient.get(
				path: getBasePath() + "/api-docs")

		then: "operationId doesn't have '_1' suffix when there is no duplicated operationId"
		def operationIdList = []
		with(response) {
			status == SC_OK
			data.paths.values().each { operationList ->
				operationList.values().each { operation ->
					operationIdList.add(operation.operationId)
				}
			}

			operationIdList.every { operationId ->
				if (operationId.toString().endsWith("_1")) {
					String operationIdWithoutSuffix = operationId.toString().substring(0, operationId.toString().indexOf("_1"))
					return operationIdList.contains(operationIdWithoutSuffix)
				}
				return true
			}
		}
	}

	def "Customer gets API documentation without operationId duplications"() {
		when: "user attempts to get API documentation"
		HttpResponseDecorator response = restClient.get(
				path: getBasePath() + "/api-docs")

		then: "operationId doesn't have duplicates"
		def operationIdList = []
		with(response) {
			status == SC_OK
			data.paths.values().each { operationList ->
				operationList.values().each { operation ->
					operationIdList.add(operation.operationId)
				}
			}

			operationIdList.every { operationId ->
				if (operationId =~ '_\\d+$') {
					println "$operationId is not unique"
					return false
				}
				return true
			}
		}
	}
}

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
package de.hybris.platform.odata2webservices.update

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.odata2webservices.enums.IntegrationType
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import org.junit.Test

import javax.annotation.Resource

@IntegrationTest
class IntegrationObjectSystemUpdateIntegrationTest extends ServicelayerTransactionalSpockSpecification {

	@Resource(name = "integrationObjectSystemUpdate")
	private IntegrationObjectSystemUpdate integrationObjectSystemUpdate
	@Resource
	private FlexibleSearchService flexibleSearchService

	@Test
	def "Update service updates "() {

		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)',
				'; PrimitiveCollection ; ')

		when:
		integrationObjectSystemUpdate.updateIntegrationType()
		def objects = findAllIntegrationObjects()

		then:
		assert objects.get(0).integrationType == IntegrationType.INBOUND
	}

	def findAllIntegrationObjects() {
		def fQuery = new FlexibleSearchQuery("SELECT {" + IntegrationObjectModel.PK + "} FROM {" + IntegrationObjectModel._TYPECODE + "}")
		flexibleSearchService.<IntegrationObjectModel> search(fQuery).getResult()
	}
}

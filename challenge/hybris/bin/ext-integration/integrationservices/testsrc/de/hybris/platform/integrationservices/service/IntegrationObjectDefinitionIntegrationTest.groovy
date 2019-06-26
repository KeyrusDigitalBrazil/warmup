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





package de.hybris.platform.integrationservices.service

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException
import org.junit.Test

import javax.annotation.Resource

@IntegrationTest
class IntegrationObjectDefinitionIntegrationTest extends ServicelayerTransactionalSpockSpecification {
	@Resource(name = "integrationObjectService")
	private IntegrationObjectService ioService

	def setup() {
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true]",
				"; UniqueCodeTest",
				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; UniqueCodeTest ; Product ; Product")
	}

	@Test
	def "uniqueness of integrationObjectItem is determined only by code"() {
		given:
		IntegrationTestUtil.importImpEx(
				'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
				'; UniqueCodeTest ; Product ; CatalogVersion')

		when:
		def ioItemType = ioService.findItemTypeCode("UniqueCodeTest", "Product")

		then:
		notThrown(AmbiguousIdentifierException)
		ioItemType == "CatalogVersion"
	}
}

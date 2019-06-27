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

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.odata2webservices.enums.IntegrationType
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.junit.Test
import spock.lang.Specification

@UnitTest
class IntegrationObjectSystemUpdateUnitTest extends Specification {

	def ioSysUpdate = new IntegrationObjectSystemUpdate()

	def modelService = Mock(ModelService)

	def setup() {
		ioSysUpdate.modelService = modelService
	}

	@Test
	def "Updates Integration Object when no value is present for IntegrationType"() {
		given:

		def integrationObject = integrationObject("IONoType", null)
		def flexibleSearchService = integrationObjectWithoutIntegrationTypeExists(integrationObject)
		ioSysUpdate.flexibleSearchService = flexibleSearchService

		when:
		ioSysUpdate.updateIntegrationType()

		then:
		1 * integrationObject.setIntegrationType(IntegrationType.INBOUND)

		1 * modelService.saveAll(_) >> { List<IntegrationObjectModel> ioModels ->
			assert ioModels.size() == 1
		}
	}

	@Test
	def "Does not update Integration Object when a value is present for IntegrationType"() {
		given:

		def integrationObject = integrationObject("IONoType", IntegrationType.INBOUND)
		def flexibleSearchService = integrationObjectWithoutIntegrationTypeExists(integrationObject)
		ioSysUpdate.flexibleSearchService = flexibleSearchService

		when:
		ioSysUpdate.updateIntegrationType()

		then:
		0 * integrationObject.setIntegrationType(IntegrationType.INBOUND)

		1 * modelService.saveAll(_) >> { List<IntegrationObjectModel> ioModels ->
			assert ioModels.size() == 1
		}
	}

	def integrationObjectWithoutIntegrationTypeExists(final IntegrationObjectModel integrationObject) {
		def flexibleSearchService = Stub(FlexibleSearchService) {
			search(_ as FlexibleSearchQuery) >> integrationObjectSearchResult(Arrays.asList(integrationObject))
		}

		flexibleSearchService
	}

	def integrationObjectSearchResult(List integrationObjects) {
		def searchResult = Stub(SearchResult) {
			getResult() >> integrationObjects
		}
		searchResult
	}

	def integrationObject(String integrationObjectName, IntegrationType integrationObjectType) {
		def integrationObject = Mock(IntegrationObjectModel) {
			getCode() >> integrationObjectName
			getIntegrationType() >> integrationObjectType
		}
		integrationObject
	}
}

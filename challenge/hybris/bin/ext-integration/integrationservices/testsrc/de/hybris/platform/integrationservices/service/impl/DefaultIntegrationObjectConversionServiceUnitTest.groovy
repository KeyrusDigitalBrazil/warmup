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
package de.hybris.platform.integrationservices.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.populator.ItemToMapConversionContext
import de.hybris.platform.integrationservices.service.IntegrationObjectNotFoundException
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.servicelayer.dto.converter.Converter
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class DefaultIntegrationObjectConversionServiceUnitTest extends Specification
{
	private static final String INTEGRATION_OBJECT = "ProductIntegrationObject"

	def conversionService = new DefaultIntegrationObjectConversionService()
	def integrationObjectService = Stub(IntegrationObjectService)
	def itemToIntegrationObjectMapConverter = Mock(Converter)
	
	def setup()
	{
		conversionService.setIntegrationObjectService(integrationObjectService)
		conversionService.setItemToIntegrationObjectMapConverter(itemToIntegrationObjectMapConverter)
	}

	@Test
	def "populate result map when integration object exists"()
	{
		given:
		def resultMap = Stub(Map)
		def itemModel = Stub(IntegrationObjectItemModel)
		integrationObjectService.findIntegrationObject(_ as String) >> Stub(IntegrationObjectModel)
		integrationObjectService.findIntegrationObjectItemByTypeCode(_ as String, _ as String) >> itemModel
		
		when:
		def map = conversionService.convert(Stub(ProductModel), INTEGRATION_OBJECT)

		then:
		map == resultMap
		1 * itemToIntegrationObjectMapConverter.convert(_ as ItemToMapConversionContext) >> resultMap
	}

	@Test
	@Unroll
	def "throws #expectedException when integration object code is '#code'"()
	{
		given:
		integrationObjectService.findIntegrationObject(_ as String) >> { throw Stub(exceptionToThrow) }

		when:
		conversionService.convert(new ItemModel(), code)

		then:
		thrown expectedException

		where:
		code 							| exceptionToThrow			| expectedException
		"${INTEGRATION_OBJECT}notFound"	| ModelNotFoundException	| IntegrationObjectNotFoundException
		""								| IllegalArgumentException	| IllegalArgumentException
		null							| IllegalArgumentException  | IllegalArgumentException
	}

	@Test
	def "throws exception when item model is null"()
	{
		when:
		conversionService.convert(null, INTEGRATION_OBJECT)

		then:
		thrown IllegalArgumentException
	}
}

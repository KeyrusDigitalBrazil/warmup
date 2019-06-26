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
package de.hybris.platform.odata2services.odata.schema.property

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import spock.lang.Ignore
import spock.lang.Specification

@UnitTest
@Ignore("Ignoring so platform doesn't run this test. The child classes will run the test cases.")
class BasePropertyListGeneratorUnitTest extends Specification
{
	def propertyGenerator = Mock(PropertyGenerator)
	def propertyListGenerator

	def mockIntegrationObjectItemModel(final Set<IntegrationObjectItemAttributeModel> attributeModels)
	{
		final IntegrationObjectItemModel itemModel = Mock(IntegrationObjectItemModel)
		itemModel.getAttributes() >> attributeModels
		return itemModel
	}
}
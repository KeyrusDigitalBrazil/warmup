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
package de.hybris.platform.odata2services.odata.schema.attribute;

import static de.hybris.platform.integrationservices.model.MockAttributeDescriptorModelBuilder.attributeDescriptorModelBuilder;
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;

public class AttributeGeneratorUnitTestHelper
{
	static void assertAttribute(final AnnotationAttribute attribute, final String attributeName, final String isTrue)
	{
		assertThat(attribute)
				.hasFieldOrPropertyWithValue("name", attributeName)
				.hasFieldOrPropertyWithValue("text", isTrue);
	}

	static IntegrationObjectItemAttributeModel givenIsUniqueReturns(final Boolean isUnique)
	{
		return simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptorModelBuilder()
						.withUnique(isUnique))
				.withUnique(isUnique)
				.build();
	}

}

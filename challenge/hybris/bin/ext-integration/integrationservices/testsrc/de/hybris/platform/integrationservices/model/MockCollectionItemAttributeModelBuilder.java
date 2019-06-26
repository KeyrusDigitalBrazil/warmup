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
package de.hybris.platform.integrationservices.model;

import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.collectionDescriptor;

public class MockCollectionItemAttributeModelBuilder extends BaseMockItemAttributeModelBuilder<MockCollectionItemAttributeModelBuilder>
{
	MockCollectionItemAttributeModelBuilder()
	{
	}

	public MockCollectionItemAttributeModelBuilder withSource(final String source)
	{
		return withIntegrationObjectItemCode(source);
	}

	public MockCollectionItemAttributeModelBuilder withTarget(final String target)
	{
		attributeDescriptorBuilderOrDefault(collectionDescriptor()).withTarget(target);
		return withReturnIntegrationObject(target);
	}

	@Override
	protected MockCollectionItemAttributeModelBuilder myself()
	{
		return this;
	}
}

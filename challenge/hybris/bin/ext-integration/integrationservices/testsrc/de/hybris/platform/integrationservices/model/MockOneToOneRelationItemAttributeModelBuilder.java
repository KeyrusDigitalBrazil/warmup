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

import static de.hybris.platform.integrationservices.model.MockRelationDescriptorModelBuilder.oneToOneRelation;

public class MockOneToOneRelationItemAttributeModelBuilder extends BaseMockItemAttributeModelBuilder<MockOneToOneRelationItemAttributeModelBuilder>
{
	private String target;

	MockOneToOneRelationItemAttributeModelBuilder()
	{
	}

	public MockOneToOneRelationItemAttributeModelBuilder withSource(final String source)
	{
		return withIntegrationObjectItemCode(source);
	}

	public MockOneToOneRelationItemAttributeModelBuilder withTarget(final String target)
	{
		this.target = target;
		return withReturnIntegrationObject(target);
	}

	@Override
	public MockOneToOneRelationItemAttributeModelBuilder withReturnIntegrationObjectItem(final IntegrationObjectItemModel item)
	{
		withTarget(item.getCode());
		return super.withReturnIntegrationObjectItem(item);
	}

	@Override
	public IntegrationObjectItemAttributeModel build()
	{
		attributeDescriptorBuilderOrDefault(oneToOneRelation()).withTarget(target);

		return super.build();
	}

	@Override
	protected MockOneToOneRelationItemAttributeModelBuilder myself()
	{
		return this;
	}
}

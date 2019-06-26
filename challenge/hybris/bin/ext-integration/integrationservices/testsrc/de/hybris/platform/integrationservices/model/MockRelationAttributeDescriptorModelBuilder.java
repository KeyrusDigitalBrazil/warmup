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

import static org.mockito.Mockito.doReturn;

import de.hybris.platform.core.model.type.RelationDescriptorModel;

/**
 * Builds {@code RelationDescriptorModel} for an attribute defined inside a relation.
 */
public class MockRelationAttributeDescriptorModelBuilder extends BaseMockAttributeDescriptorModelBuilder<MockRelationAttributeDescriptorModelBuilder, RelationDescriptorModel>
{
	private Boolean partOf;

	private MockRelationAttributeDescriptorModelBuilder()
	{
	}

	public static MockRelationAttributeDescriptorModelBuilder relationAttribute()
	{
		return new MockRelationAttributeDescriptorModelBuilder();
	}

	@Override
	public RelationDescriptorModel build()
	{
		final RelationDescriptorModel model = createMock(RelationDescriptorModel.class);
		doReturn(partOf).when(model).getPartOf();
		return model;
	}

	public MockRelationAttributeDescriptorModelBuilder withPartOf(final Boolean value)
	{
		partOf = value;
		return this;
	}

	@Override
	protected MockRelationAttributeDescriptorModelBuilder myself()
	{
		return this;
	}
}

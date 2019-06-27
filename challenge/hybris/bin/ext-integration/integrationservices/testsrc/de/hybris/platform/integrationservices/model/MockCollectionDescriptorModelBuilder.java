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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.TypeModel;

public class MockCollectionDescriptorModelBuilder extends BaseMockAttributeDescriptorModelBuilder<MockCollectionDescriptorModelBuilder, AttributeDescriptorModel>
{
	private TypeModel targetType;

	public MockCollectionDescriptorModelBuilder withTarget(final String target)
	{
		targetType = typeModel(target);
		return this;
	}

	public MockCollectionDescriptorModelBuilder withPrimitiveTarget(final String type)
	{
		targetType = primitiveTypeModel(type);
		return this;
	}

	public AttributeDescriptorModel build()
	{
		final CollectionTypeModel typeModel = collectionTypeModel();
		final AttributeDescriptorModel model = createMock(AttributeDescriptorModel.class);
		when(model.getAttributeType()).thenReturn(typeModel);
		return model;
	}

	private CollectionTypeModel collectionTypeModel()
	{
		final TypeModel typeModel = targetType != null ? targetType : typeModel(null);

		final CollectionTypeModel collectionTypeModel = mock(CollectionTypeModel.class);
		when(collectionTypeModel.getElementType()).thenReturn(typeModel);
		return collectionTypeModel;
	}

	@Override
	protected MockCollectionDescriptorModelBuilder myself()
	{
		return this;
	}
}

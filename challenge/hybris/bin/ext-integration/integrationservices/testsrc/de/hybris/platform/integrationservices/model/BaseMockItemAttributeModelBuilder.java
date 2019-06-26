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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

public abstract class BaseMockItemAttributeModelBuilder<B extends BaseMockItemAttributeModelBuilder>
{
	private static final String DEFAULT_INT_OBJECT_CODE = "testIntegrationObject";

	private BaseMockAttributeDescriptorModelBuilder mockAttributeDescriptorModelBuilder;
	private String integrationObjectCode = DEFAULT_INT_OBJECT_CODE;
	private String attributeName;
	private String returnIntegrationObjectType;
	private Boolean unique;
	private Boolean partOf;
	private Boolean autoCreate;
	private IntegrationObjectItemModel returnIntegrationObjectItem;

	BaseMockItemAttributeModelBuilder()
	{
	}

	/**
	 * No source or target is needed for a simple attribute.
	 *
	 * @return the integration object attribute definition model builder
	 */
	public static MockItemAttributeModelBuilder simpleAttributeBuilder()
	{
		return new MockItemAttributeModelBuilder()
				.withAttributeDescriptor(BaseMockAttributeDescriptorModelBuilder.attributeDescriptor());
	}

	/**
	 * Complex relationship attribute definition builder
	 *
	 * @return the integration object attribute definition model builder
	 */
	public static MockComplexRelationItemAttributeModelBuilder complexRelationAttributeBuilder()
	{
		return new MockComplexRelationItemAttributeModelBuilder()
				.withAttributeDescriptor(MockRelationDescriptorModelBuilder.oneToManyRelation());
	}

	/**
	 * One to one attribute definition builder
	 *
	 * @return the integration object attribute definition model builder
	 */
	public static MockOneToOneRelationItemAttributeModelBuilder oneToOneRelationAttributeBuilder()
	{
		return new MockOneToOneRelationItemAttributeModelBuilder()
				.withAttributeDescriptor(MockRelationDescriptorModelBuilder.oneToOneRelation());
	}

	/**
	 * Collection attribute definition builder
	 *
	 * @return the integration object attribute definition model builder
	 */
	public static MockCollectionItemAttributeModelBuilder collectionAttributeBuilder()
	{
		return new MockCollectionItemAttributeModelBuilder()
				.withAttributeDescriptor(collectionDescriptor());
	}

	public B withName(final String name)
	{
		attributeName = name;
		return myself();
	}

	public B unique()
	{
		return withUnique(true);
	}

	public B withUnique(final Boolean value)
	{
		unique = value;
		return myself();
	}

	public B withAttributeDescriptor(final BaseMockAttributeDescriptorModelBuilder builder)
	{
		mockAttributeDescriptorModelBuilder = builder;
		return myself();
	}

	<T extends BaseMockAttributeDescriptorModelBuilder> T attributeDescriptorBuilderOrDefault(final T defaultBuilder)
	{
		if (mockAttributeDescriptorModelBuilder == null)
		{
			mockAttributeDescriptorModelBuilder = defaultBuilder;
		}
		return (T) mockAttributeDescriptorModelBuilder;
	}

	public B withIntegrationObjectItemCode(final String typeCode)
	{
		this.integrationObjectCode = typeCode;
		return myself();
	}

	public B withReturnIntegrationObject(final String integrationObjectCode)
	{
		returnIntegrationObjectType = integrationObjectCode;
		returnIntegrationObjectItem = null;
		return myself();
	}

	B withReturnIntegrationObjectItem(final MockIntegrationObjectItemModelBuilder spec)
	{
		return withReturnIntegrationObjectItem(spec.build());
	}

	public B withReturnIntegrationObjectItem(final IntegrationObjectItemModel item)
	{
		returnIntegrationObjectItem = item;
		returnIntegrationObjectType = null;
		return myself();
	}

	public B withPartOf(final boolean value)
	{
		partOf = value;
		return myself();
	}

	public B withAutoCreate(final Boolean value)
	{
		autoCreate = value;
		return myself();
	}

	public IntegrationObjectItemAttributeModel build()
	{
		final IntegrationObjectItemModel objectModel = mockIntegrationObjectItemModel(integrationObjectCode);
		final IntegrationObjectItemModel returnObjectModel = deriveReturnIntegrationObjectItemModel();
		final AttributeDescriptorModel attributeDescriptorModel = mockAttributeDescriptorModelBuilder != null
				? mockAttributeDescriptorModelBuilder.build()
				: null;

		final IntegrationObjectItemAttributeModel attributeDefinition = mock(IntegrationObjectItemAttributeModel.class);
		when(attributeDefinition.getAttributeDescriptor()).thenReturn(attributeDescriptorModel);
		when(attributeDefinition.getIntegrationObjectItem()).thenReturn(objectModel);
		when(attributeDefinition.getReturnIntegrationObjectItem()).thenReturn(returnObjectModel);
		when(attributeDefinition.getAttributeName()).thenReturn(attributeName);
		when(attributeDefinition.getPartOf()).thenReturn(partOf);
		when(attributeDefinition.getAutoCreate()).thenReturn(autoCreate);
		when(attributeDefinition.getUnique()).thenReturn(unique);
		return attributeDefinition;
	}

	private IntegrationObjectItemModel deriveReturnIntegrationObjectItemModel()
	{
		return returnIntegrationObjectType != null
				? mockIntegrationObjectItemModel(returnIntegrationObjectType)
				: returnIntegrationObjectItem;
	}

	private IntegrationObjectItemModel mockIntegrationObjectItemModel(final String code)
	{
		final IntegrationObjectItemModel integrationObjectDefinitionModel = mock(IntegrationObjectItemModel.class);
		when(integrationObjectDefinitionModel.getCode()).thenReturn(code);
		return integrationObjectDefinitionModel;
	}

	protected abstract B myself();
}

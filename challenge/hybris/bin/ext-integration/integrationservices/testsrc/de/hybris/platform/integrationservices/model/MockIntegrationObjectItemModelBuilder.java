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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MockIntegrationObjectItemModelBuilder
{
	private final Set<BaseMockItemAttributeModelBuilder> attributeBuilders = new HashSet<>();
	private final Set<BaseMockItemAttributeModelBuilder> uniqueAttributeBuilders = new HashSet<>();
	private final Set<IntegrationObjectItemAttributeModel> attributes = new HashSet<>();
	private final Set<IntegrationObjectItemAttributeModel> uniqueAttributes = new HashSet<>();
	private String itemCode = "SomeItem";
	private String integrationObjectName;

	private MockIntegrationObjectItemModelBuilder()
	{}

	public static MockIntegrationObjectItemModelBuilder itemModelBuilder()
	{
		return new MockIntegrationObjectItemModelBuilder();
	}

	public MockIntegrationObjectItemModelBuilder withCode(final String code)
	{
		itemCode = code;
		return this;
	}

	public MockIntegrationObjectItemModelBuilder withAttribute(final BaseMockItemAttributeModelBuilder attributeBuilder)
	{
		attributeBuilders.add(attributeBuilder);
		return this;
	}

	public MockIntegrationObjectItemModelBuilder withUniqueAttribute(final BaseMockItemAttributeModelBuilder attribute)
	{
		uniqueAttributeBuilders.add(attribute);
		return this;
	}

	public MockIntegrationObjectItemModelBuilder withIntegrationObject(final String name)
	{
		integrationObjectName = name;
		return this;
	}

	public IntegrationObjectItemModel build()
	{
		buildAttributes();
		return stubItem();
	}

	private void buildAttributes()
	{
		provideItemCodeToReferenceAttributeBuilders();
		buildUniqueAttributes();

		final Set<IntegrationObjectItemAttributeModel> attributesFromBuilders = attributeBuilders.stream()
				.map(BaseMockItemAttributeModelBuilder::build)
				.collect(Collectors.toSet());
		attributes.addAll(attributesFromBuilders);
		attributeBuilders.clear();
	}

	private void buildUniqueAttributes()
	{
		final Set<IntegrationObjectItemAttributeModel> attributesFromBuilders = uniqueAttributeBuilders.stream()
				.map(BaseMockItemAttributeModelBuilder::build)
				.collect(Collectors.toSet());
		uniqueAttributes.addAll(attributesFromBuilders);
		uniqueAttributeBuilders.clear();
	}

	private void provideItemCodeToReferenceAttributeBuilders()
	{
		attributeBuilders.stream()
				.map(b -> b.withIntegrationObjectItemCode(itemCode))
				.filter(MockOneToOneRelationItemAttributeModelBuilder.class::isInstance)
				.map(MockOneToOneRelationItemAttributeModelBuilder.class::cast)
				.forEach(b -> b.withSource(itemCode));
	}

	private IntegrationObjectItemModel stubItem()
	{
		final IntegrationObjectModel object = integrationObject(integrationObjectName);
		final ComposedTypeModel typeModel = composedTypeModel(itemCode);

		final IntegrationObjectItemModel item = mock(IntegrationObjectItemModel.class);
		when(item.getIntegrationObject()).thenReturn(object);
		when(item.getAttributes()).thenReturn(attributes);
		when(item.getCode()).thenReturn(itemCode);
		when(item.getUniqueAttributes()).thenReturn(uniqueAttributes);
		when(item.getType()).thenReturn(typeModel);
		return item;
	}

	private IntegrationObjectModel integrationObject(final String name)
	{
		final IntegrationObjectModel model = mock(IntegrationObjectModel.class);
		when(model.getCode()).thenReturn(name);
		return model;
	}

	private static ComposedTypeModel composedTypeModel(final String itemCode)
	{
		final ComposedTypeModel model = mock(ComposedTypeModel.class);
		doReturn(itemCode).when(model).getCode();
		return model;
	}
}

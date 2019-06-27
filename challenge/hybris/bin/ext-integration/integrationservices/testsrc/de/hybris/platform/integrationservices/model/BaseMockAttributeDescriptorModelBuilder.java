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

import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;

/**
 * Encapsulates builder behavior common for all kinds of {@code AttributeDescriptorModel} builders.
 */
public abstract class BaseMockAttributeDescriptorModelBuilder<B extends BaseMockAttributeDescriptorModelBuilder, M extends AttributeDescriptorModel>
{
	private String enclosingType;
	private String qualifier;
	private Boolean unique;
	private Boolean optional;
	private Boolean partOf;
	private Object defaultValue;
	private Boolean primitive;
	private Boolean localized;

	public static MockAttributeDescriptorModelBuilder attributeDescriptor()
	{
		return new MockAttributeDescriptorModelBuilder();
	}

	public static MockCollectionDescriptorModelBuilder collectionDescriptor()
	{
		return new MockCollectionDescriptorModelBuilder();
	}

	public B withEnclosingType(final String type)
	{
		enclosingType = type;
		return myself();
	}

	public B withQualifier(final String name)
	{
		qualifier = name;
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

	public B optional()
	{
		return withOptional(true);
	}

	public B withOptional(final Boolean value)
	{
		optional = value;
		return myself();
	}

	public B withPartOf(final Boolean value)
	{
		partOf = value;
		return myself();
	}

	public B withDefaultValue(final Object value)
	{
		defaultValue = value;
		return myself();
	}

	public B withPrimitive(final Boolean b)
	{
		primitive = b;
		return myself();
	}

	public B withLocalized(final Boolean b)
	{
		localized = b;
		return myself();
	}

	protected final M createMock(final Class<M> mockClass)
	{
		final M model = mock(mockClass);
		doReturn(qualifier).when(model).getQualifier();
		doReturn(unique).when(model).getUnique();
		doReturn(optional).when(model).getOptional();
		doReturn(partOf).when(model).getPartOf();
		doReturn(defaultValue).when(model).getDefaultValue();
		doReturn(primitive).when(model).getPrimitive();
		doReturn(localized).when(model).getLocalized();
		doReturn(composedTypeModel(enclosingType)).when(model).getEnclosingType();
		return model;
	}

	public abstract M build();

	protected static ComposedTypeModel composedTypeModel(final String code)
	{
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		when(composedTypeModel.getCode()).thenReturn(code);
		return composedTypeModel;
	}

	protected static TypeModel typeModel(final String typecode)
	{
		final TypeModel typeModel = mock(TypeModel.class);
		when(typeModel.getCode()).thenReturn(typecode);
		return typeModel;
	}

	protected static AtomicTypeModel primitiveTypeModel(final String typecode)
	{
		final AtomicTypeModel typeModel = mock(AtomicTypeModel.class);
		when(typeModel.getCode()).thenReturn(typecode);
		return typeModel;
	}

	protected abstract B myself();
}

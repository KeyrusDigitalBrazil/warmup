/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cms2.servicelayer.services.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAttributeDescriptorModelHelperServiceTest
{

	@Mock
	private TypeService typeService;

	@InjectMocks
	private DefaultAttributeDescriptorModelHelperService predicate;

	@Mock
	private AttributeDescriptorModel simpleAttributeDescriptor;

	@Mock
	private AttributeDescriptorModel collectionAttributeDescriptor;

	@Mock
	private ComposedTypeModel enclosingType;

	@Mock
	private ComposedTypeModel enclosingTypeThatHasCollection;

	public static class TestType extends ItemModel
	{

		private PropertyType testProperty;

		public PropertyType getTestProperty()
		{
			return testProperty;
		}
	}

	public static class TestTypeThatHasCollection extends ItemModel
	{

		private Collection<PropertyType> testProperties;

		public Collection<PropertyType> getTestProperties()
		{
			return testProperties;
		}

	}

	public static class PropertyType
	{
		// intentionally left empty
	}

	@Before
	public void setup()
	{
		when(simpleAttributeDescriptor.getQualifier()).thenReturn("testProperty");
		when(simpleAttributeDescriptor.getDeclaringEnclosingType()).thenReturn(enclosingType);
		doReturn(TestType.class).when(typeService).getModelClass(enclosingType);

		when(collectionAttributeDescriptor.getQualifier()).thenReturn("testProperties");
		when(collectionAttributeDescriptor.getDeclaringEnclosingType()).thenReturn(enclosingTypeThatHasCollection);
		doReturn(TestTypeThatHasCollection.class).when(typeService).getModelClass(enclosingTypeThatHasCollection);
	}

	@Test
	public void givenANonCollectionPropertyThenTypeCanBeRetrieved()
	{
		assertThat(predicate.getAttributeClass(simpleAttributeDescriptor).equals(PropertyType.class), is(true));
	}

	@Test
	public void givenACollectionPropertyThenGenericTypeCanBeRetrieved()
	{
		assertThat(predicate.getAttributeClass(collectionAttributeDescriptor).equals(PropertyType.class), is(true));
	}

}

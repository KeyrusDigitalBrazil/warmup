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
package de.hybris.platform.cmsfacades.types.service.predicate;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AssignableFromAttributePredicateTest
{
	@Mock
	private TypeService typeService;
	@Mock
	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;

	@InjectMocks
	private AssignableFromAttributePredicate assignableFromAttributePredicate;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;

	@Mock
	private ComposedTypeModel composedTypeModel;

	@Mock
	private ComposedTypeModel enclosingComposedTypeModel;

	// Enclosed Type
	private class ParentType
	{

	};

	private class SubTypeOfParentType extends ParentType
	{

	};

	private class SomeEnclosedType
	{

	};

	// Enclosing Type
	private class EnclosingParentType
	{

	};

	private class SubTypeOfEnclosingParentType extends EnclosingParentType
	{

	};

	private class SomeEnclosingType
	{

	};

	@Test
	public void givenTypeCodeIsSet_WhenTypeIsAssignableToProvidedTypeCode_WillReturnTrue()
	{
		// GIVEN
		setUpEnclosedType();
		doReturn(SubTypeOfParentType.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptor);

		// WHEN / THEN
		assertThat(assignableFromAttributePredicate.test(attributeDescriptor), is(true));
	}

	@Test
	public void givenTypeCodeIsSet_WhenTypeIsNotAssignableToProvidedTypeCode_WillReturnFalse()
	{
		// GIVEN
		setUpEnclosedType();
		doReturn(SomeEnclosedType.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptor);

		// WHEN / THEN
		assertThat(assignableFromAttributePredicate.test(attributeDescriptor), is(false));
	}

	@Test
	public void givenEnclosingTypeIsSet_WhenTypeIsAssignableToProvidedEnclosingType_WillReturnTrue()
	{
		// GIVEN
		setUpEnclosingType();
		doReturn(SubTypeOfEnclosingParentType.class).when(attributeDescriptorModelHelperService).getDeclaringEnclosingTypeClass(attributeDescriptor);

		// WHEN / THEN
		assertThat(assignableFromAttributePredicate.test(attributeDescriptor), is(true));
	}

	@Test
	public void givenEnclosingTypeIsSet_WhenTypeIsNotAssignableToProvidedEnclosingType_WillReturnFalse()
	{
		// GIVEN
		setUpEnclosingType();
		doReturn(SomeEnclosingType.class).when(attributeDescriptorModelHelperService).getDeclaringEnclosingTypeClass(attributeDescriptor);

		// WHEN / THEN
		assertThat(assignableFromAttributePredicate.test(attributeDescriptor), is(false));
	}

	@Test
	public void givenTypeCodesAreSet_WhenBothAreAssignable_WillReturnTrue()
	{
		// GIVEN
		setUpEnclosedType();
		setUpEnclosingType();
		doReturn(SubTypeOfParentType.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptor);
		doReturn(SubTypeOfEnclosingParentType.class).when(attributeDescriptorModelHelperService).getDeclaringEnclosingTypeClass(attributeDescriptor);

		// WHEN / THEN
		assertThat(assignableFromAttributePredicate.test(attributeDescriptor), is(true));
	}

	@Test
	public void givenTypeCodesAreSet_WhenTypeCodeIsNotAssignable_WillReturnFalse()
	{
		// GIVEN
		setUpEnclosedType();
		setUpEnclosingType();
		doReturn(SomeEnclosedType.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptor);
		doReturn(SubTypeOfEnclosingParentType.class).when(attributeDescriptorModelHelperService).getDeclaringEnclosingTypeClass(attributeDescriptor);

		// WHEN / THEN
		assertThat(assignableFromAttributePredicate.test(attributeDescriptor), is(false));
	}

	@Test
	public void givenTypeCodesAreSet_WhenEnclosingTypeCodeIsNotAssignable_WillReturnFalse()
	{
		// GIVEN
		setUpEnclosedType();
		setUpEnclosingType();
		doReturn(SubTypeOfParentType.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptor);
		doReturn(SomeEnclosingType.class).when(attributeDescriptorModelHelperService).getDeclaringEnclosingTypeClass(attributeDescriptor);

		// WHEN / THEN
		assertThat(assignableFromAttributePredicate.test(attributeDescriptor), is(false));
	}


	// Helper methods
	private void setUpEnclosingType()
	{
		assignableFromAttributePredicate.setEnclosingTypeCode("EnclosingParentType");
		when(typeService.getComposedTypeForCode("EnclosingParentType")).thenReturn(enclosingComposedTypeModel);
		doReturn(EnclosingParentType.class).when(typeService).getModelClass(enclosingComposedTypeModel);
	}

	private void setUpEnclosedType()
	{
		assignableFromAttributePredicate.setTypeCode("ParentType");
		when(typeService.getComposedTypeForCode("ParentType")).thenReturn(composedTypeModel);
		doReturn(ParentType.class).when(typeService).getModelClass(composedTypeModel);
	}
}

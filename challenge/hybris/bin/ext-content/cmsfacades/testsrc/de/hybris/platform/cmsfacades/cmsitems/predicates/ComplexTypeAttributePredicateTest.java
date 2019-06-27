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
package de.hybris.platform.cmsfacades.cmsitems.predicates;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultAttributeDescriptorModelHelperService;
import de.hybris.platform.cmsfacades.common.predicate.attributes.EnumTypeAttributePredicate;
import de.hybris.platform.cmsfacades.types.service.predicate.ComplexTypeAttributePredicate;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

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
public class ComplexTypeAttributePredicateTest
{

	@Mock
	private DefaultAttributeDescriptorModelHelperService defaultAttributeDescriptorModelHelperService;

	@Mock
	private EnumTypeAttributePredicate enumStructureTypeAttributePredicate;

	@InjectMocks
	private ComplexTypeAttributePredicate predicate;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;

	public static class SubTypeOfItemModel extends ItemModel
	{

	};

	public static class SomeType
	{

	};

	public static enum EnumType
	{

	};

	@Test
	public void givenAttributeDescriptorWhenNeitherItemModelNorEnumWillReturnFalse()
	{
		doReturn(SomeType.class).when(defaultAttributeDescriptorModelHelperService).getAttributeClass(attributeDescriptor);
		when(enumStructureTypeAttributePredicate.test(attributeDescriptor)).thenReturn(false);

		assertThat(predicate.test(attributeDescriptor), is(false));
	}

	@Test
	public void givenAttributeDescriptorWhenItemModelWillReturnTrue()
	{
		doReturn(SubTypeOfItemModel.class).when(defaultAttributeDescriptorModelHelperService)
				.getAttributeClass(attributeDescriptor);
		when(enumStructureTypeAttributePredicate.test(attributeDescriptor)).thenReturn(false);

		assertThat(predicate.test(attributeDescriptor), is(true));
	}

	@Test
	public void givensAttributeDescriptorWhenEnumWillReturnTrue()
	{
		doReturn(EnumType.class).when(defaultAttributeDescriptorModelHelperService).getAttributeClass(attributeDescriptor);
		when(enumStructureTypeAttributePredicate.test(attributeDescriptor)).thenReturn(true);

		assertThat(predicate.test(attributeDescriptor), is(true));
	}

}

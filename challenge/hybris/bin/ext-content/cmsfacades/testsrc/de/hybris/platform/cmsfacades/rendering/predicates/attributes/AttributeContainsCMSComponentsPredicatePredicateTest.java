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
package de.hybris.platform.cmsfacades.rendering.predicates.attributes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.types.service.CMSAttributeTypeService;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AttributeContainsCMSComponentsPredicatePredicateTest
{
	private static final String NON_CMS_COMPONENT_TYPECODE = "nonCmsComponent";
	private static final String CHILD_CMS_COMPONENT_TYPECODE = "childCmsComponent";

	@Mock
	private ComposedTypeModel nonCmsComponent;

	@Mock
	private ComposedTypeModel childCmsComponent;

	@Mock
	private TypeService typeService;

	@Mock
	private AttributeDescriptorModel attributeDescriptorModel;

	@Mock
	private CMSAttributeTypeService cmsAttributeTypeService;

	@InjectMocks
	private AttributeContainsCMSComponentsPredicate predicate;

	@Before
	public void setUp()
	{
		when(nonCmsComponent.getCode()).thenReturn(NON_CMS_COMPONENT_TYPECODE);
		when(childCmsComponent.getCode()).thenReturn(CHILD_CMS_COMPONENT_TYPECODE);
		when(typeService.isAssignableFrom(AbstractCMSComponentModel._TYPECODE, NON_CMS_COMPONENT_TYPECODE)).thenReturn(false);
		when(typeService.isAssignableFrom(AbstractCMSComponentModel._TYPECODE, CHILD_CMS_COMPONENT_TYPECODE)).thenReturn(true);
	}

	@Test
	public void givenPrimitiveAttributeDescriptorModel_WhenTested_ThenItReturnsFalse()
	{
		// GIVEN
		when(attributeDescriptorModel.getPrimitive()).thenReturn(true);

		// WHEN
		final boolean result = predicate.test(attributeDescriptorModel);

		// THEN
		assertThat(result, is(false));
	}

	@Test
	public void givenNonCmsItem_WhenTested_ThenItReturnsFalse()
	{
		// GIVEN
		when(attributeDescriptorModel.getPrimitive()).thenReturn(false);
		when(cmsAttributeTypeService.getAttributeContainedType(attributeDescriptorModel)).thenReturn(nonCmsComponent);

		// WHEN
		final boolean result = predicate.test(attributeDescriptorModel);

		// THEN
		assertThat(result, is(false));
	}

	@Test
	public void givenCmsItem_WhenTested_ThenItReturnsTrue()
	{
		// GIVEN
		when(attributeDescriptorModel.getPrimitive()).thenReturn(false);
		when(cmsAttributeTypeService.getAttributeContainedType(attributeDescriptorModel)).thenReturn(childCmsComponent);

		// WHEN
		final boolean result = predicate.test(attributeDescriptorModel);

		// THEN
		assertThat(result, is(true));
	}

}

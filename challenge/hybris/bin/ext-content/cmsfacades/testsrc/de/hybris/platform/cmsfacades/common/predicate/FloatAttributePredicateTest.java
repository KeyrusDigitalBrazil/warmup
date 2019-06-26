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
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.cmsfacades.common.predicate.DefaultClassTypeAttributePredicate;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FloatAttributePredicateTest
{
	@Mock
	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;
	@Mock
	private AttributeDescriptorModel attributeDescriptor;

	@InjectMocks
	private DefaultClassTypeAttributePredicate predicate;


	@Before
	public void setup()
	{
		predicate.setTypeClass(Float.class);

	}

	@Test
	public void whenTypeIsNotDoubleShouldReturnFalse()
	{
		doReturn(String.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptor);
		assertThat(predicate.test(attributeDescriptor), is(false));
	}

	@Test
	public void whenTypeIsGrandFloatShouldReturnTrue()
	{
		doReturn(Float.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptor);
		assertThat(predicate.test(attributeDescriptor), is(true));
	}

	@Test
	public void whenTypeIsSmallFloatShouldReturnTrue()
	{
		doReturn(float.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptor);
		assertThat(predicate.test(attributeDescriptor), is(true));
	}

}

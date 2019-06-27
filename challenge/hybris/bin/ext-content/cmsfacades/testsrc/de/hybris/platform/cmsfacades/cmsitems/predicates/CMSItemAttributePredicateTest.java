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
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

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
public class CMSItemAttributePredicateTest
{
	@Mock
	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;

	@InjectMocks
	private CMSItemAttributePredicate predicate;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;

	public static class SubTypeOfCMSItemModel extends CMSItemModel
	{

	};

	public static class SomeType
	{

	};

	@Test
	public void whenTypeIsAssignableToCMsItemShouldReturnTrue()
	{
		doReturn(SubTypeOfCMSItemModel.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptor);

		final boolean test = predicate.test(attributeDescriptor);

		assertThat(test, is(true));
	}

	@Test
	public void whenTypeIsNotAssignableToCMsItemShouldReturnFalse()
	{
		doReturn(SomeType.class).when(attributeDescriptorModelHelperService).getAttributeClass(attributeDescriptor);

		final boolean test = predicate.test(attributeDescriptor);

		assertThat(test, is(false));
	}
}

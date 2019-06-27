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
package de.hybris.platform.cms2.version.predicate;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.version.converter.attribute.data.VersionAttributeDescriptor;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NonCMSItemTypePredicateTest
{
	private static final String SOME_TYPECODE = "typeCode";

	@InjectMocks
	private NonCMSItemTypePredicate predicate;

	@Mock
	private TypeService typeService;

	@Mock
	private VersionAttributeDescriptor versionAttributeDescriptor;

	@Mock
	private TypeModel typeModel;

	@Before
	public void setup()
	{
		doReturn(typeModel).when(versionAttributeDescriptor).getType();
		doReturn(SOME_TYPECODE).when(typeModel).getCode();
	}

	@Test
	public void whenTypeIsAssignableToItemButNotCMSItemShouldReturnTrue()
	{
		// GIVEN
		doReturn(true).when(typeService).isAssignableFrom(ItemModel._TYPECODE, SOME_TYPECODE);
		doReturn(false).when(typeService).isAssignableFrom(CMSItemModel._TYPECODE, SOME_TYPECODE);

		// WHEN
		final boolean test = predicate.test(versionAttributeDescriptor);

		// THEN
		assertThat(test, equalTo(true));
	}

	@Test
	public void whenTypeIsAssignableToCMSItemShouldReturnFalse()
	{
		// GIVEN
		doReturn(true).when(typeService).isAssignableFrom(ItemModel._TYPECODE, SOME_TYPECODE);
		doReturn(true).when(typeService).isAssignableFrom(CMSItemModel._TYPECODE, SOME_TYPECODE);

		// WHEN
		final boolean test = predicate.test(versionAttributeDescriptor);

		// THEN
		assertThat(test, equalTo(false));
	}

	@Test
	public void whenTypeIsNotAssignableToItemShouldReturnFalse()
	{
		// GIVEN
		doReturn(false).when(typeService).isAssignableFrom(ItemModel._TYPECODE, SOME_TYPECODE);

		// WHEN
		final boolean test = predicate.test(versionAttributeDescriptor);

		// THEN
		assertThat(test, equalTo(false));
	}

}

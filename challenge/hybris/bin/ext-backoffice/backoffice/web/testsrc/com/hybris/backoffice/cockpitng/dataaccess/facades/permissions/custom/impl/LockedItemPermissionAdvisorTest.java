/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.cockpitng.dataaccess.facades.permissions.custom.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.locking.ItemLockingService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;


@RunWith(MockitoJUnitRunner.class)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class LockedItemPermissionAdvisorTest extends AbstractCockpitngUnitTest<LockedItemPermissionAdvisor>
{

	@Spy
	@InjectMocks
	private LockedItemPermissionAdvisor advisor;

	@Mock
	private ItemLockingService itemLockingService;

	@Mock
	private ItemModel unLockedItem;

	@Mock
	private ItemModel lockedItem;

	@Before
	public void setUp()
	{
		when(itemLockingService.isLocked(lockedItem)).thenReturn(true);
		when(itemLockingService.isLocked(argThat(new ArgumentMatcher<ItemModel>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return o != lockedItem;
			}
		}))).thenReturn(false);
	}

	@Test
	public void advisorFollowsService()
	{
		final boolean deleteLockedResult = advisor.canDelete(lockedItem);
		final boolean modifyLockedResult = advisor.canModify(lockedItem);

		final boolean deleteUnLockedResult = advisor.canDelete(unLockedItem);
		final boolean modifyUnLockedResult = advisor.canModify(unLockedItem);

		assertThat(deleteLockedResult).isFalse();
		assertThat(modifyLockedResult).isFalse();

		assertThat(deleteUnLockedResult).isTrue();
		assertThat(modifyUnLockedResult).isTrue();
	}

	@Test
	public void isApplicableTo()
	{
		final boolean applicableToString = advisor.isApplicableTo("String");
		final boolean applicableToObject = advisor.isApplicableTo(new Object());
		final boolean applicableToItemModel = advisor.isApplicableTo(new ItemModel());
		final boolean applicableToProductModel = advisor.isApplicableTo(new ProductModel());

		assertThat(applicableToString).isFalse();
		assertThat(applicableToObject).isFalse();

		assertThat(applicableToItemModel).isTrue();
		assertThat(applicableToProductModel).isTrue();
	}

}

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

package de.hybris.platform.commerceservices.strategies.hooks;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class BundleCartValidationHookTest
{
	@Mock
	private EntryGroupService entryGroupService;
	@InjectMocks
	private final BundleCartValidationHook hook = new BundleCartValidationHook();
	private CommerceCartParameter parameter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		parameter = new CommerceCartParameter();
		parameter.setCart(new CartModel());
	}

	@Test
	public void shouldSurviveNullParameter()
	{
		hook.afterValidateCart(null, Collections.emptyList());
	}

	@Test
	public void shouldSurviveNullModifications()
	{
		hook.afterValidateCart(parameter, null);
	}

	@Test
	public void shouldSurviveNullCart()
	{
		hook.afterValidateCart(new CommerceCartParameter(), Collections.emptyList());
	}

	@Test
	public void shouldSurviveNullEntryGroupList()
	{
		hook.afterValidateCart(parameter, Collections.emptyList());
	}

	@Test
	public void shouldSkipCartWithoutGroups()
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();
		parameter.getCart().setEntryGroups(Collections.emptyList());
		hook.afterValidateCart(parameter, modifications);
	}

	@Test
	public void shouldTakeBundleGroupsOnly()
	{
		final EntryGroup group = new EntryGroup();
		group.setErroneous(Boolean.TRUE);
		group.setGroupType(GroupType.STANDALONE);
		parameter.getCart().setEntryGroups(Collections.singletonList(group));
		final ArrayList<CommerceCartModification> modifications = new ArrayList<>();

		hook.afterValidateCart(parameter, modifications);

		assertThat(modifications, emptyIterable());
	}

	@Test
	public void shouldTakeNestedGroups()
	{
		final EntryGroup group = new EntryGroup();
		group.setErroneous(Boolean.TRUE);
		group.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		group.setGroupNumber(Integer.valueOf(1));
		final EntryGroup root = new EntryGroup();
		root.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		root.setGroupNumber(Integer.valueOf(2));
		parameter.getCart().setEntryGroups(Collections.singletonList(root));
		final ArrayList<CommerceCartModification> modifications = new ArrayList<>();
		when(entryGroupService.getNestedGroups(root)).thenReturn(Arrays.asList(root, group));

		hook.afterValidateCart(parameter, modifications);

		assertThat(modifications, iterableWithSize(1));
		assertEquals(CommerceCartModificationStatus.ENTRY_GROUP_ERROR, modifications.get(0).getStatusCode());
		assertThat(modifications.get(0).getEntryGroupNumbers(), contains(Integer.valueOf(1)));
	}

	@Test
	public void shouldReturnAllErroneousGroups()
	{
		final EntryGroup root = new EntryGroup();
		root.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		root.setGroupNumber(Integer.valueOf(100));
		final EntryGroup group1 = new EntryGroup();
		group1.setErroneous(Boolean.TRUE);
		group1.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		group1.setGroupNumber(Integer.valueOf(1));
		final EntryGroup group2 = new EntryGroup();
		group2.setErroneous(Boolean.TRUE);
		group2.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		group2.setGroupNumber(Integer.valueOf(2));
		final EntryGroup group3 = new EntryGroup();
		group3.setErroneous(Boolean.FALSE);
		group3.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		group3.setGroupNumber(Integer.valueOf(3));
		parameter.getCart().setEntryGroups(Collections.singletonList(root));
		final ArrayList<CommerceCartModification> modifications = new ArrayList<>();
		when(entryGroupService.getNestedGroups(root)).thenReturn(Arrays.asList(root, group1, group2, group3));

		hook.afterValidateCart(parameter, modifications);

		assertThat(modifications, iterableWithSize(1));
		assertThat(modifications.get(0).getEntryGroupNumbers(),
				containsInAnyOrder(Integer.valueOf(1), Integer.valueOf(2)));
	}
}

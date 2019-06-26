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

package de.hybris.platform.configurablebundleservices.order.hook;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.RemoveEntryGroupParameter;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;


/**
 * Unit tests for {@link BundleRemoveEntryGroupMethodHook}
 */
@UnitTest
public class BundleRemoveEntryGroupMethodHookTest
{

	private static final String CART_CODE = "CartCode";

	private static final Integer ROOT_GROUP_NUMBER = 1;
	private static final Integer ANOTHER_ROOT_GROUP_NUMBER = 2;
	private static final Integer INTERMEDIATE_GROUP_NUMBER = 3;
	private static final Integer LEAF_GROUP_1_NUMBER = 4;
	private static final Integer LEAF_GROUP_2_NUMBER = 5;
	private static final Integer WRONG_GROUP_NUMBER = 1000;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@InjectMocks
	private final BundleRemoveEntryGroupMethodHook bundleRemoveEntryGroupMethodHook = new BundleRemoveEntryGroupMethodHook();


	@Mock
	private EntryGroupService entryGroupService;

	private EntryGroup rootEntryGroup;
	private CartModel cartModel;
	private RemoveEntryGroupParameter removeEntryGroupParameter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		cartModel = new CartModel();
		cartModel.setCode(CART_CODE);
		cartModel.setEntryGroups(Arrays.asList(rootEntryGroup));
		cartModel.setEntries(Collections.emptyList());

		rootEntryGroup = new EntryGroup();
		rootEntryGroup.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		rootEntryGroup.setGroupNumber(ROOT_GROUP_NUMBER);
		rootEntryGroup.setChildren(Collections.emptyList());

		given(entryGroupService.getGroupOfType(any(), eq(Collections.singletonList(ROOT_GROUP_NUMBER)), eq(GroupType.CONFIGURABLEBUNDLE))).willReturn(
				rootEntryGroup);
	}


	@Test
	public void testBeforeRemoveEntryGroupNoParameter() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter parameter can not be null");

		bundleRemoveEntryGroupMethodHook.beforeRemoveEntryGroup(null);
	}

	@Test
	public void testBeforeRemoveEntryGroupNoEntryGroupNumber() throws CommerceCartModificationException
	{
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEntryGroupNumber(null);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter parameter.entryGroupNumber can not be null");

		bundleRemoveEntryGroupMethodHook.beforeRemoveEntryGroup(removeEntryGroupParameter);
	}

	@Test
	public void testBeforeRemoveEntryGroupNoCart() throws CommerceCartModificationException
	{
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setCart(null);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter parameter.cart can not be null");

		bundleRemoveEntryGroupMethodHook.beforeRemoveEntryGroup(removeEntryGroupParameter);
	}

	@Test
	public void testBeforeRemoveEntryGroupNoSuchGroupInCart() throws CommerceCartModificationException
	{
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEntryGroupNumber(WRONG_GROUP_NUMBER);

		given(entryGroupService.getGroupOfType(any(), anyCollection(), eq(GroupType.CONFIGURABLEBUNDLE)))
				.willThrow(new IllegalArgumentException("No group with number '" + WRONG_GROUP_NUMBER
						+ "' in the order with code '" + CART_CODE + "'"));

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("Cannot remove non-existing group '" + WRONG_GROUP_NUMBER
				+ "' from the cart '" + CART_CODE + "'");

		bundleRemoveEntryGroupMethodHook.beforeRemoveEntryGroup(removeEntryGroupParameter);
	}

	@Test
	public void testBeforeRemoveEntryGroupBundleRootGroup() throws CommerceCartModificationException
	{
		final EntryGroup anotherRootGroup = entryGroupModel(ANOTHER_ROOT_GROUP_NUMBER, GroupType.CONFIGURABLEBUNDLE);
		final EntryGroup leafEntryGroup1 = entryGroupModel(LEAF_GROUP_1_NUMBER, GroupType.CONFIGURABLEBUNDLE);
		final EntryGroup leafEntryGroup2 = entryGroupModel(LEAF_GROUP_2_NUMBER,GroupType.CONFIGURABLEBUNDLE);
		final EntryGroup intermediateEntryGroup = entryGroupModel(INTERMEDIATE_GROUP_NUMBER, GroupType.CONFIGURABLEBUNDLE, leafEntryGroup1, leafEntryGroup2);

		cartModel.setEntryGroups(Arrays.asList(rootEntryGroup, anotherRootGroup));
		rootEntryGroup.setChildren(Arrays.asList(intermediateEntryGroup));

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEntryGroupNumber(ROOT_GROUP_NUMBER);

		bundleRemoveEntryGroupMethodHook.beforeRemoveEntryGroup(removeEntryGroupParameter);
	}

	@Test
	public void testBeforeRemoveEntryGroupBundleIntermediateGroup() throws CommerceCartModificationException
	{
		final EntryGroup anotherRootGroup = entryGroupModel(ANOTHER_ROOT_GROUP_NUMBER, GroupType.CONFIGURABLEBUNDLE);
		final EntryGroup leafEntryGroup1 = entryGroupModel(LEAF_GROUP_1_NUMBER, GroupType.CONFIGURABLEBUNDLE);
		final EntryGroup leafEntryGroup2 = entryGroupModel(LEAF_GROUP_2_NUMBER, GroupType.CONFIGURABLEBUNDLE);
		final EntryGroup intermediateEntryGroup = entryGroupModel(INTERMEDIATE_GROUP_NUMBER, GroupType.CONFIGURABLEBUNDLE, leafEntryGroup1, leafEntryGroup2);

		cartModel.setEntryGroups(Arrays.asList(rootEntryGroup, anotherRootGroup));
		rootEntryGroup.setChildren(Arrays.asList(intermediateEntryGroup));

		given(entryGroupService.getGroupOfType(any(), anyCollection(), eq(GroupType.CONFIGURABLEBUNDLE))).willReturn(intermediateEntryGroup);

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEntryGroupNumber(INTERMEDIATE_GROUP_NUMBER);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("Cannot remove non-root entry group with number '" + INTERMEDIATE_GROUP_NUMBER
				+ "' from the cart with code '" + CART_CODE + "'");

		bundleRemoveEntryGroupMethodHook.beforeRemoveEntryGroup(removeEntryGroupParameter);
	}

	@Test
	public void testBeforeRemoveEntryGroupBundleLeafGroup() throws CommerceCartModificationException
	{
		final EntryGroup anotherRootGroup = entryGroupModel(ANOTHER_ROOT_GROUP_NUMBER, GroupType.CONFIGURABLEBUNDLE);
		final EntryGroup leafEntryGroup1 = entryGroupModel(LEAF_GROUP_1_NUMBER, GroupType.CONFIGURABLEBUNDLE);
		final EntryGroup leafEntryGroup2 = entryGroupModel(LEAF_GROUP_2_NUMBER, GroupType.CONFIGURABLEBUNDLE);
		final EntryGroup intermediateEntryGroup = entryGroupModel(INTERMEDIATE_GROUP_NUMBER, GroupType.CONFIGURABLEBUNDLE, leafEntryGroup1, leafEntryGroup2);

		cartModel.setEntryGroups(Arrays.asList(rootEntryGroup, anotherRootGroup));
		rootEntryGroup.setChildren(Arrays.asList(intermediateEntryGroup));


		given(entryGroupService.getGroupOfType(any(), anyCollection(), eq(GroupType.CONFIGURABLEBUNDLE))).willReturn(
				leafEntryGroup1);

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEntryGroupNumber(LEAF_GROUP_1_NUMBER);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("Cannot remove non-root entry group with number '" + LEAF_GROUP_1_NUMBER
				+ "' from the cart with code '" + CART_CODE + "'");

		bundleRemoveEntryGroupMethodHook.beforeRemoveEntryGroup(removeEntryGroupParameter);
	}

	@Test
	public void testBeforeRemoveEntryGroupNonBundleRootGroup() throws CommerceCartModificationException
	{
		final EntryGroup anotherRootGroup = entryGroupModel(ANOTHER_ROOT_GROUP_NUMBER, GroupType.STANDALONE);
		final EntryGroup leafEntryGroup1 = entryGroupModel(LEAF_GROUP_1_NUMBER, GroupType.STANDALONE);
		final EntryGroup leafEntryGroup2 = entryGroupModel(LEAF_GROUP_2_NUMBER, GroupType.STANDALONE);
		final EntryGroup intermediateEntryGroup = entryGroupModel(INTERMEDIATE_GROUP_NUMBER, GroupType.STANDALONE, leafEntryGroup1, leafEntryGroup2);

		cartModel.setEntryGroups(Arrays.asList(rootEntryGroup, anotherRootGroup));
		rootEntryGroup.setChildren(Arrays.asList(intermediateEntryGroup));

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEntryGroupNumber(ROOT_GROUP_NUMBER);

		bundleRemoveEntryGroupMethodHook.beforeRemoveEntryGroup(removeEntryGroupParameter);
	}

	@Test
	public void testBeforeRemoveEntryGroupNonBundleIntermediateGroup() throws CommerceCartModificationException
	{
		final EntryGroup anotherRootGroup = entryGroupModel(ANOTHER_ROOT_GROUP_NUMBER, GroupType.STANDALONE);
		final EntryGroup leafEntryGroup1 = entryGroupModel(LEAF_GROUP_1_NUMBER, GroupType.STANDALONE);
		final EntryGroup leafEntryGroup2 = entryGroupModel(LEAF_GROUP_2_NUMBER, GroupType.STANDALONE);
		final EntryGroup intermediateEntryGroup = entryGroupModel(INTERMEDIATE_GROUP_NUMBER, GroupType.STANDALONE, leafEntryGroup1, leafEntryGroup2);

		cartModel.setEntryGroups(Arrays.asList(rootEntryGroup, anotherRootGroup));
		rootEntryGroup.setChildren(Arrays.asList(intermediateEntryGroup));
		given(entryGroupService.getGroupOfType(any(), anyCollection(), eq(GroupType.CONFIGURABLEBUNDLE))).willReturn(null);

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEntryGroupNumber(INTERMEDIATE_GROUP_NUMBER);

		bundleRemoveEntryGroupMethodHook.beforeRemoveEntryGroup(removeEntryGroupParameter);
	}

	@Test
	public void testBeforeRemoveEntryGroupNonBundleLeafGroup() throws CommerceCartModificationException
	{
		final EntryGroup anotherRootGroup = entryGroupModel(ANOTHER_ROOT_GROUP_NUMBER, GroupType.STANDALONE);
		final EntryGroup leafEntryGroup1 = entryGroupModel(LEAF_GROUP_1_NUMBER, GroupType.STANDALONE);
		final EntryGroup leafEntryGroup2 = entryGroupModel(LEAF_GROUP_2_NUMBER, GroupType.STANDALONE);
		final EntryGroup intermediateEntryGroup = entryGroupModel(INTERMEDIATE_GROUP_NUMBER, GroupType.STANDALONE, leafEntryGroup1, leafEntryGroup2);

		cartModel.setEntryGroups(Arrays.asList(rootEntryGroup, anotherRootGroup));
		rootEntryGroup.setChildren(Arrays.asList(intermediateEntryGroup));

		given(entryGroupService.getGroupOfType(any(), anyCollection(), eq(GroupType.CONFIGURABLEBUNDLE))).willReturn(null);

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEntryGroupNumber(LEAF_GROUP_1_NUMBER);

		bundleRemoveEntryGroupMethodHook.beforeRemoveEntryGroup(removeEntryGroupParameter);
	}

	@Test
	public void shouldReturnIfNoEntryGroupsInCart() throws CommerceCartModificationException
	{
		cartModel.setEntryGroups(null);
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEntryGroupNumber(ANOTHER_ROOT_GROUP_NUMBER);

		bundleRemoveEntryGroupMethodHook.beforeRemoveEntryGroup(removeEntryGroupParameter);

		verifyZeroInteractions(entryGroupService);
	}

	@Test
	public void shouldDoNothingAfterRemoveEntryGroup() throws CommerceCartModificationException
	{
		final RemoveEntryGroupParameter parameter = mock(RemoveEntryGroupParameter.class);
		final CommerceCartModification modification = mock(CommerceCartModification.class);

		bundleRemoveEntryGroupMethodHook.afterRemoveEntryGroup(parameter, modification);

		verifyZeroInteractions(parameter, modification, entryGroupService);
	}

	private EntryGroup entryGroupModel(final Integer number, GroupType type, final EntryGroup... children)
	{
		final EntryGroup result = new EntryGroup();
		result.setGroupType(type);
		result.setGroupNumber(number);
		result.setChildren(Stream.of(children)
				.collect(Collectors.toList()));
		return result;
	}
}

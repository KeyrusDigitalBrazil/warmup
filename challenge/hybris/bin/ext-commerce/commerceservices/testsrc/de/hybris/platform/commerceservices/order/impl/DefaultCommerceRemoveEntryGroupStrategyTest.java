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
package de.hybris.platform.commerceservices.order.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceUpdateCartEntryStrategy;
import de.hybris.platform.commerceservices.order.hook.CommerceRemoveEntryGroupMethodHook;
import de.hybris.platform.commerceservices.service.data.RemoveEntryGroupParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link DefaultCommerceRemoveEntryGroupStrategy}
 */
@UnitTest
public class DefaultCommerceRemoveEntryGroupStrategyTest
{
	private static final String CART_CODE = "CartCode";

	private static final Integer ROOT_GROUP_NUMBER = Integer.valueOf(1);
	private static final Integer ANOTHER_ROOT_GROUP_NUMBER = Integer.valueOf(2);
	private static final Integer INTERMEDIATE_GROUP_NUMBER = Integer.valueOf(3);
	private static final Integer LEAF_GROUP_1_NUMBER = Integer.valueOf(4);
	private static final Integer LEAF_GROUP_2_NUMBER = Integer.valueOf(5);
	private static final Integer WRONG_GROUP_NUMBER = Integer.valueOf(100500);

	@InjectMocks
	private final DefaultCommerceRemoveEntryGroupStrategy removeEntryGroupStrategy = new DefaultCommerceRemoveEntryGroupStrategy();

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private CommerceRemoveEntryGroupMethodHook removeEntryGroupMethodHook;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private CommerceUpdateCartEntryStrategy updateCartEntryStrategy;
	@Mock
	private EntryGroupService entryGroupService;

	@Mock
	private Configuration configuration;

	private EntryGroup rootEntryGroup;
	private CartModel cartModel;
	private RemoveEntryGroupParameter removeEntryGroupParameter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		removeEntryGroupStrategy.setCommerceRemoveEntryGroupHooks(Collections.singletonList(removeEntryGroupMethodHook));

		rootEntryGroup = entryGroupModel(ROOT_GROUP_NUMBER);

		cartModel = new CartModel();
		cartModel.setCode(CART_CODE);
		cartModel.setEntryGroups(Collections.singletonList(rootEntryGroup));
		cartModel.setEntries(Collections.emptyList());

		given(configurationService.getConfiguration()).willReturn(configuration);
		given(Boolean.valueOf(configuration.getBoolean(eq(CommerceServicesConstants.REMOVEENTRYGROUPHOOK_ENABLED)))).willReturn(
				Boolean.FALSE);

		given(entryGroupService.getGroup(any(), eq(ROOT_GROUP_NUMBER))).willReturn(rootEntryGroup);
		given(entryGroupService.getParent(any(), eq(ROOT_GROUP_NUMBER))).willReturn(null);
		given(entryGroupService.getParent(any(), eq(WRONG_GROUP_NUMBER))).willThrow(new IllegalArgumentException());
		given(entryGroupService.getGroup(any(), eq(WRONG_GROUP_NUMBER))).willThrow(new IllegalArgumentException());
	}

	@Test
	public void testBeforeRemoveEntryGroupIfHookEnabledOnlyInParameter() throws CommerceCartModificationException
	{
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(true);

		removeEntryGroupStrategy.beforeRemoveEntryGroup(removeEntryGroupParameter);

		verify(configurationService).getConfiguration();
	}

	@Test
	public void testBeforeRemoveEntryGroupIfHookEnabledOnlyInProperties() throws CommerceCartModificationException
	{
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(false);

		given(Boolean.valueOf(configuration.getBoolean(eq(CommerceServicesConstants.REMOVEENTRYGROUPHOOK_ENABLED)))).willReturn(
				Boolean.TRUE);

		removeEntryGroupStrategy.beforeRemoveEntryGroup(removeEntryGroupParameter);
	}

	@Test
	public void testBeforeRemoveEntryGroupIfHookEnabledInParameterAndProperties() throws CommerceCartModificationException
	{
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(true);

		given(Boolean.valueOf(configuration.getBoolean(eq(CommerceServicesConstants.REMOVEENTRYGROUPHOOK_ENABLED), eq(true))))
				.willReturn(Boolean.TRUE);

		removeEntryGroupStrategy.beforeRemoveEntryGroup(removeEntryGroupParameter);

		verify(configurationService).getConfiguration();
		verify(removeEntryGroupMethodHook).beforeRemoveEntryGroup(removeEntryGroupParameter);
	}

	@Test
	public void testBeforeRemoveEntryGroupIfHookDisabled() throws CommerceCartModificationException
	{
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(false);

		removeEntryGroupStrategy.beforeRemoveEntryGroup(removeEntryGroupParameter);
	}

	@Test
	public void testAfterRemoveEntryGroupIfHookEnabledOnlyInParameter() throws CommerceCartModificationException
	{
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(true);

		final CommerceCartModification commerceCartModification = Mockito.mock(CommerceCartModification.class);

		removeEntryGroupStrategy.afterRemoveEntryGroup(removeEntryGroupParameter, commerceCartModification);

		verify(configurationService).getConfiguration();
	}

	@Test
	public void testAfterRemoveEntryGroupIfHookEnabledOnlyInProperties() throws CommerceCartModificationException
	{
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(false);

		given(
				Boolean.valueOf(configurationService.getConfiguration().getBoolean(
						eq(CommerceServicesConstants.REMOVEENTRYGROUPHOOK_ENABLED)))).willReturn(Boolean.TRUE);

		final CommerceCartModification commerceCartModification = Mockito.mock(CommerceCartModification.class);

		removeEntryGroupStrategy.afterRemoveEntryGroup(removeEntryGroupParameter, commerceCartModification);

		verify(configurationService).getConfiguration();
	}

	@Test
	public void testAfterRemoveEntryGroupIfHookEnabledInParameterAndProperties() throws CommerceCartModificationException
	{
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(true);

		given(Boolean.valueOf(configuration.getBoolean(eq(CommerceServicesConstants.REMOVEENTRYGROUPHOOK_ENABLED), eq(true))))
				.willReturn(Boolean.TRUE);

		final CommerceCartModification commerceCartModification = Mockito.mock(CommerceCartModification.class);

		removeEntryGroupStrategy.afterRemoveEntryGroup(removeEntryGroupParameter, commerceCartModification);

		verify(configurationService).getConfiguration();
		verify(removeEntryGroupMethodHook).afterRemoveEntryGroup(removeEntryGroupParameter, commerceCartModification);
	}

	@Test
	public void testAfterRemoveEntryGroupIfHookDisabled() throws CommerceCartModificationException
	{
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(false);

		final CommerceCartModification commerceCartModification = Mockito.mock(CommerceCartModification.class);

		removeEntryGroupStrategy.afterRemoveEntryGroup(removeEntryGroupParameter, commerceCartModification);
	}

	@Test
	public void testRemoveEntryGroupNullParameter() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter parameter can not be null");

		removeEntryGroupStrategy.removeEntryGroup(null);

		verify(updateCartEntryStrategy, never()).updateQuantityForCartEntry(any());
	}

	@Test
	public void testRemoveEntryGroupNullCart() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Cart model cannot be null");

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(null);
		removeEntryGroupParameter.setEnableHooks(false);

		removeEntryGroupStrategy.removeEntryGroup(removeEntryGroupParameter);

		verify(updateCartEntryStrategy, never()).updateQuantityForCartEntry(any());
	}

	@Test
	public void testRemoveEntryGroupNullEntryGroupNumber() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter entryGroupNumber can not be null");

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(false);
		removeEntryGroupParameter.setEntryGroupNumber(null);

		removeEntryGroupStrategy.removeEntryGroup(removeEntryGroupParameter);

		verify(updateCartEntryStrategy, never()).updateQuantityForCartEntry(any());
	}

	@Test
	public void testRemoveEntryGroupNoSuchGroupInCart() throws CommerceCartModificationException
	{
		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(false);
		removeEntryGroupParameter.setEntryGroupNumber(WRONG_GROUP_NUMBER);

		final CommerceCartModification result = removeEntryGroupStrategy.removeEntryGroup(removeEntryGroupParameter);

		verify(updateCartEntryStrategy, never()).updateQuantityForCartEntry(any());

		Assert.assertEquals(CommerceCartModificationStatus.INVALID_ENTRY_GROUP_NUMBER, result.getStatusCode());
		Assert.assertEquals(WRONG_GROUP_NUMBER, result.getEntryGroupNumbers().iterator().next());

		verify(updateCartEntryStrategy, never()).updateQuantityForCartEntry(any());
	}

	@Test
	public void testRemoveEntryGroupRootGroup() throws CommerceCartModificationException
	{
		final EntryGroup anotherRootGroup = entryGroupModel(ANOTHER_ROOT_GROUP_NUMBER);
		final EntryGroup leafEntryGroup1 = entryGroupModel(LEAF_GROUP_1_NUMBER);
		final EntryGroup leafEntryGroup2 = entryGroupModel(LEAF_GROUP_2_NUMBER);
		final EntryGroup intermediateEntryGroup = entryGroupModel(INTERMEDIATE_GROUP_NUMBER, leafEntryGroup1, leafEntryGroup2);

		cartModel.setEntryGroups(Arrays.asList(rootEntryGroup, anotherRootGroup));
		rootEntryGroup.setChildren(Collections.singletonList(intermediateEntryGroup));

		given(entryGroupService.getParent(cartModel, ROOT_GROUP_NUMBER)).willReturn(null);
		given(entryGroupService.getNestedGroups(rootEntryGroup)).willReturn(
				Arrays.asList(rootEntryGroup, intermediateEntryGroup, leafEntryGroup1, leafEntryGroup2));

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(false);
		removeEntryGroupParameter.setEntryGroupNumber(ROOT_GROUP_NUMBER);

		final CommerceCartModification result = removeEntryGroupStrategy.removeEntryGroup(removeEntryGroupParameter);

		verify(updateCartEntryStrategy, never()).updateQuantityForCartEntry(any());

		Assert.assertEquals(CommerceCartModificationStatus.SUCCESSFULLY_REMOVED, result.getStatusCode());
		Assert.assertEquals(ROOT_GROUP_NUMBER, result.getEntryGroupNumbers().iterator().next());

		Assert.assertEquals(1, cartModel.getEntryGroups().size());
		final EntryGroup entryGroup = cartModel.getEntryGroups().get(0);
		Assert.assertNotNull(entryGroup);
		Assert.assertTrue(entryGroup.getChildren().isEmpty());
		Assert.assertEquals(ANOTHER_ROOT_GROUP_NUMBER, entryGroup.getGroupNumber());
	}

	@Test
	public void testRemoveEntryGroupIntermediateGroup() throws CommerceCartModificationException
	{
		final EntryGroup leafEntryGroup1 = entryGroupModel(LEAF_GROUP_1_NUMBER);
		final EntryGroup leafEntryGroup2 = entryGroupModel(LEAF_GROUP_2_NUMBER);
		final EntryGroup intermediateEntryGroup = entryGroupModel(INTERMEDIATE_GROUP_NUMBER, leafEntryGroup1, leafEntryGroup2);

		rootEntryGroup.setChildren(Collections.singletonList(intermediateEntryGroup));

		given(entryGroupService.getParent(cartModel, INTERMEDIATE_GROUP_NUMBER)).willReturn(rootEntryGroup);
		given(entryGroupService.getGroup(cartModel, INTERMEDIATE_GROUP_NUMBER)).willReturn(intermediateEntryGroup);
		given(entryGroupService.getNestedGroups(intermediateEntryGroup)).willReturn(
				Arrays.asList(intermediateEntryGroup, leafEntryGroup1, leafEntryGroup2));

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(false);
		removeEntryGroupParameter.setEntryGroupNumber(INTERMEDIATE_GROUP_NUMBER);

		final CommerceCartModification result = removeEntryGroupStrategy.removeEntryGroup(removeEntryGroupParameter);

		verify(updateCartEntryStrategy, never()).updateQuantityForCartEntry(any());

		Assert.assertEquals(CommerceCartModificationStatus.SUCCESSFULLY_REMOVED, result.getStatusCode());
		Assert.assertEquals(INTERMEDIATE_GROUP_NUMBER, result.getEntryGroupNumbers().iterator().next());

		Assert.assertEquals(1, cartModel.getEntryGroups().size());
		final EntryGroup entryGroup = cartModel.getEntryGroups().get(0);
		Assert.assertNotNull(entryGroup);
		Assert.assertTrue(entryGroup.getChildren().isEmpty());


	}

	@Test
	public void testRemoveEntryGroupLeafGroup() throws CommerceCartModificationException
	{
		final EntryGroup leafEntryGroup1 = entryGroupModel(LEAF_GROUP_1_NUMBER);
		final EntryGroup leafEntryGroup2 = entryGroupModel(LEAF_GROUP_2_NUMBER);
		final EntryGroup intermediateEntryGroup = entryGroupModel(INTERMEDIATE_GROUP_NUMBER, leafEntryGroup1, leafEntryGroup2);

		rootEntryGroup.setChildren(Collections.singletonList(intermediateEntryGroup));

		given(entryGroupService.getParent(cartModel, LEAF_GROUP_1_NUMBER)).willReturn(intermediateEntryGroup);
		given(entryGroupService.getGroup(cartModel, LEAF_GROUP_1_NUMBER)).willReturn(leafEntryGroup1);
		given(entryGroupService.getNestedGroups(leafEntryGroup1)).willReturn(Collections.emptyList());

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(false);
		removeEntryGroupParameter.setEntryGroupNumber(LEAF_GROUP_1_NUMBER);

		final CommerceCartModification result = removeEntryGroupStrategy.removeEntryGroup(removeEntryGroupParameter);

		verify(updateCartEntryStrategy, never()).updateQuantityForCartEntry(any());

		Assert.assertEquals(CommerceCartModificationStatus.SUCCESSFULLY_REMOVED, result.getStatusCode());
		Assert.assertEquals(LEAF_GROUP_1_NUMBER, result.getEntryGroupNumbers().iterator().next());

		Assert.assertEquals(1, cartModel.getEntryGroups().size());
		final EntryGroup entryGroup = cartModel.getEntryGroups().get(0);
		Assert.assertNotNull(entryGroup);
		Assert.assertEquals(1, entryGroup.getChildren().size());
		Assert.assertEquals(ROOT_GROUP_NUMBER, entryGroup.getGroupNumber());
		final EntryGroup entryGroup1 = entryGroup.getChildren().get(0);
		Assert.assertNotNull(entryGroup1);
		Assert.assertEquals(1, entryGroup1.getChildren().size());
		Assert.assertEquals(INTERMEDIATE_GROUP_NUMBER, entryGroup1.getGroupNumber());
		final EntryGroup entryGroup2 = entryGroup1.getChildren().get(0);
		Assert.assertNotNull(entryGroup2);
		Assert.assertTrue(entryGroup2.getChildren().isEmpty());
		Assert.assertEquals(LEAF_GROUP_2_NUMBER, entryGroup2.getGroupNumber());

	}

	@Test
	public void testRemoveEntryGroupRootGroupsIsEmpty() throws CommerceCartModificationException
	{
		cartModel.setEntryGroups(Collections.emptyList());

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(false);
		removeEntryGroupParameter.setEntryGroupNumber(WRONG_GROUP_NUMBER);

		final CommerceCartModification result = removeEntryGroupStrategy.removeEntryGroup(removeEntryGroupParameter);

		verify(updateCartEntryStrategy, never()).updateQuantityForCartEntry(any());

		Assert.assertEquals(CommerceCartModificationStatus.INVALID_ENTRY_GROUP_NUMBER, result.getStatusCode());
		Assert.assertEquals(WRONG_GROUP_NUMBER, result.getEntryGroupNumbers().iterator().next());
	}

	@Test
	public void testRemoveEntryGroupLeafGroupWithEntry() throws CommerceCartModificationException
	{
		final EntryGroup leafEntryGroup1 = entryGroupModel(LEAF_GROUP_1_NUMBER);
		final EntryGroup leafEntryGroup2 = entryGroupModel(LEAF_GROUP_2_NUMBER);
		final EntryGroup intermediateEntryGroup = entryGroupModel(INTERMEDIATE_GROUP_NUMBER, leafEntryGroup1, leafEntryGroup2);
		final OrderEntryModel orderEntry = Mockito.mock(OrderEntryModel.class);

		given(orderEntry.getEntryNumber()).willReturn(Integer.valueOf(1));
		given(orderEntry.getEntryGroupNumbers()).willReturn(new HashSet<>(Collections.singletonList(LEAF_GROUP_1_NUMBER)));

		cartModel.setEntries(Collections.singletonList(orderEntry));

		rootEntryGroup.setChildren(Collections.singletonList(intermediateEntryGroup));

		given(entryGroupService.getParent(cartModel, LEAF_GROUP_1_NUMBER)).willReturn(intermediateEntryGroup);
		given(entryGroupService.getGroup(cartModel, LEAF_GROUP_1_NUMBER)).willReturn(leafEntryGroup1);
		given(entryGroupService.getNestedGroups(leafEntryGroup1)).willReturn(Collections.singletonList(leafEntryGroup1));

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(false);
		removeEntryGroupParameter.setEntryGroupNumber(LEAF_GROUP_1_NUMBER);

		final CommerceCartModification result = removeEntryGroupStrategy.removeEntryGroup(removeEntryGroupParameter);

		verify(updateCartEntryStrategy).updateQuantityForCartEntry(any());

		Assert.assertEquals(CommerceCartModificationStatus.SUCCESSFULLY_REMOVED, result.getStatusCode());
		Assert.assertEquals(LEAF_GROUP_1_NUMBER, result.getEntryGroupNumbers().iterator().next());

		Assert.assertEquals(1, cartModel.getEntryGroups().size());
		final EntryGroup entryGroup = cartModel.getEntryGroups().get(0);
		Assert.assertNotNull(entryGroup);
		Assert.assertEquals(1, entryGroup.getChildren().size());
		Assert.assertEquals(ROOT_GROUP_NUMBER, entryGroup.getGroupNumber());
		final EntryGroup entryGroup1 = entryGroup.getChildren().get(0);
		Assert.assertNotNull(entryGroup1);
		Assert.assertEquals(1, entryGroup1.getChildren().size());
		Assert.assertEquals(INTERMEDIATE_GROUP_NUMBER, entryGroup1.getGroupNumber());
		final EntryGroup entryGroup2 = entryGroup1.getChildren().get(0);
		Assert.assertNotNull(entryGroup2);
		Assert.assertTrue(entryGroup2.getChildren().isEmpty());
		Assert.assertEquals(LEAF_GROUP_2_NUMBER, entryGroup2.getGroupNumber());
	}

	@Test
	public void testRemoveEntryGroupRootGroupWithEntry() throws CommerceCartModificationException
	{
		final EntryGroup anotherRootEntryGroup = entryGroupModel(ANOTHER_ROOT_GROUP_NUMBER);
		final EntryGroup leafEntryGroup1 = entryGroupModel(LEAF_GROUP_1_NUMBER);
		final EntryGroup leafEntryGroup2 = entryGroupModel(LEAF_GROUP_2_NUMBER);
		final EntryGroup intermediateEntryGroup = entryGroupModel(INTERMEDIATE_GROUP_NUMBER, leafEntryGroup1, leafEntryGroup2);

		final OrderEntryModel orderEntry = Mockito.mock(OrderEntryModel.class);

		given(orderEntry.getEntryNumber()).willReturn(Integer.valueOf(1));
		given(orderEntry.getEntryGroupNumbers()).willReturn(new HashSet<>(Collections.singletonList(ROOT_GROUP_NUMBER)));

		cartModel.setEntryGroups(Arrays.asList(rootEntryGroup, anotherRootEntryGroup));
		cartModel.setEntries(Collections.singletonList(orderEntry));

		rootEntryGroup.setChildren(Collections.singletonList(intermediateEntryGroup));

		given(entryGroupService.getNestedGroups(rootEntryGroup)).willReturn(
				Arrays.asList(rootEntryGroup, intermediateEntryGroup, leafEntryGroup1, leafEntryGroup2));

		removeEntryGroupParameter = new RemoveEntryGroupParameter();
		removeEntryGroupParameter.setCart(cartModel);
		removeEntryGroupParameter.setEnableHooks(false);
		removeEntryGroupParameter.setEntryGroupNumber(ROOT_GROUP_NUMBER);

		final CommerceCartModification result = removeEntryGroupStrategy.removeEntryGroup(removeEntryGroupParameter);

		verify(updateCartEntryStrategy).updateQuantityForCartEntry(any());

		Assert.assertEquals(CommerceCartModificationStatus.SUCCESSFULLY_REMOVED, result.getStatusCode());
		Assert.assertEquals(ROOT_GROUP_NUMBER, result.getEntryGroupNumbers().iterator().next());

		Assert.assertEquals(1, cartModel.getEntryGroups().size());
		final EntryGroup entryGroup = cartModel.getEntryGroups().get(0);
		Assert.assertNotNull(entryGroup);
		Assert.assertTrue(entryGroup.getChildren().isEmpty());
		Assert.assertEquals(ANOTHER_ROOT_GROUP_NUMBER, entryGroup.getGroupNumber());
	}

	private EntryGroup entryGroupModel(final Integer number, final EntryGroup... children)
	{
		final EntryGroup result = new EntryGroup();
		result.setGroupNumber(number);
		result.setChildren(Stream.of(children).collect(Collectors.toList()));
		return result;
	}
}

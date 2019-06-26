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
package de.hybris.platform.ruleengineservices.rao.providers.impl;

import static de.hybris.platform.ruleengineservices.util.TestUtil.createNewConverter;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.impl.DefaultEntryGroupService;
import de.hybris.platform.ruleengineservices.converters.populator.CartModelBuilder;
import de.hybris.platform.ruleengineservices.converters.populator.OrderEntryGroupRaoPopulator;
import de.hybris.platform.ruleengineservices.rao.OrderEntryGroupRAO;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultEntryGroupRAOProviderUnitTest
{
	private DefaultEntryGroupRAOProvider provider;

	private GroupType groupType;

	@Before
	public void setUp() throws Exception
	{
		provider = new DefaultEntryGroupRAOProvider();
		final OrderEntryGroupRaoPopulator entryGroupRaoPopulator = new OrderEntryGroupRaoPopulator();
		final AbstractPopulatingConverter<EntryGroup, OrderEntryGroupRAO> entryGroupRaoConverter = createNewConverter(
				OrderEntryGroupRAO.class, entryGroupRaoPopulator);
		provider.setEntryGroupRaoConverter(entryGroupRaoConverter);

		final AbstractPopulatingConverter<ProductModel, ProductRAO> productConverter = new AbstractPopulatingConverter<>();
		productConverter.setTargetClass(ProductRAO.class);
		productConverter.setPopulators(emptyList());
		provider.setEntryGroupService(new DefaultEntryGroupService());

		groupType = GroupType.valueOf("testGroupType");
	}

	@Test
	public void testExpandFactModel()
	{
		final List<EntryGroup> groups = new ArrayList<>();
		final EntryGroup root1 = generateEntryGroups("root1-", 2, 3, groups, groupType);
		final EntryGroup root2 = generateEntryGroups("root2-", 2, 3, groups, groupType);

		final CartModel cart = CartModelBuilder.newCart("2000").setCurrency("USD").addProduct("107701", 1, 1.0, 1)
				.addProduct("107702", 1, 2.0, 2).getModel();
		cart.setEntryGroups(Arrays.asList(root1, root2));

		cart.getEntries().get(0).setEntryGroupNumbers(singleton(Integer.valueOf(3))); // belongs to root1- tree
		cart.getEntries().get(1).setEntryGroupNumbers(singleton(Integer.valueOf(10))); // another entry belongs to root2- tree

		@SuppressWarnings("unchecked")
		final Set<OrderEntryGroupRAO> expanded = provider.expandFactModel(cart);

		assertThat(expanded.size()).isEqualTo(14);
		assertThat(expanded.stream().filter(groupRao -> groupRao.getExternalReferenceId().startsWith("root1")).collect(toSet()))
				.hasSize(7);
		assertThat(expanded.stream().filter(groupRao -> groupRao.getExternalReferenceId().startsWith("root2")).collect(toSet()))
				.hasSize(7);
		expanded.forEach(groupRao -> assertThat(groupRao.getExternalReferenceId().endsWith(groupRao.getEntryGroupId().toString()))
				.isTrue());
		expanded.forEach(groupRao -> assertThat(
				groupRao.getExternalReferenceId().startsWith(
						groupRao.getRootEntryGroup().getExternalReferenceId()
								.substring(0, groupRao.getRootEntryGroup().getExternalReferenceId().indexOf("-")).toString()))
				.isTrue());
	}

	@Test
	public void testExpandFactModelNoEntryGroupNumber()
	{
		final CartModel cart = CartModelBuilder.newCart("2000").setCurrency("USD").addProduct("107701", 1, 1.0, 1)
				.addProduct("107702", 1, 2.0, 2).getModel();
		cart.setEntryGroups(emptyList());

		cart.getEntries().get(0).setEntryGroupNumbers(emptySet());
		cart.getEntries().get(1).setEntryGroupNumbers(singleton(Integer.valueOf(10)));

		@SuppressWarnings("unchecked")
		final Set<OrderEntryGroupRAO> expanded = provider.expandFactModel(cart);
		assertThat(expanded.size()).isEqualTo(0);
	}

	private EntryGroup generateEntryGroups(final String extRefIdPrefix, final int childrenNumber, final int treeHeight,
			final List<EntryGroup> groups, final GroupType groupType)
	{
		final EntryGroup root = new EntryGroup();
		groups.add(root);
		final int number = groups.size();
		root.setExternalReferenceId(extRefIdPrefix + number);
		root.setGroupNumber(Integer.valueOf(number));
		root.setGroupType(groupType);
		if (treeHeight >= 2)
		{
			root.setChildren(new ArrayList<>());
			for (int j = 0; j < childrenNumber; j++)
			{
				final EntryGroup entryGroup = generateEntryGroups(extRefIdPrefix, childrenNumber, treeHeight - 1, groups, groupType);
				root.getChildren().add(entryGroup);
			}
		}
		return root;
	}
}

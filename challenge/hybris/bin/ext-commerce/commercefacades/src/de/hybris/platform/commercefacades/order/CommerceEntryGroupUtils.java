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
package de.hybris.platform.commercefacades.order;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.core.enums.GroupType;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;


/**
 * Manipulation with {@link EntryGroupData}.
 *
 * @see de.hybris.platform.order.EntryGroupService
 */
public interface CommerceEntryGroupUtils
{
	/**
	 * Flatten group tree and return it as a plain list of groups.
	 * <p>
	 * The method keeps item order unchanged: first goes root item, then its first child, then the children of that child
	 * and so forth.
	 * </p>
	 *
	 * @param root
	 *           node to start collecting from
	 * @return list, that includes the node and all its descendants
	 * @throws IllegalArgumentException
	 *            is root is null
	 */
	@Nonnull
	List<EntryGroupData> getNestedGroups(@Nonnull EntryGroupData root);

	/**
	 * Returns all leaf nodes of group tree, preventing their natural order.
	 *
	 * @param root
	 *           root node of group tree
	 * @return leaf nodes
	 * @throws IllegalArgumentException
	 *            if {@code root} is null
	 */
	@Nonnull
	List<EntryGroupData> getLeaves(@Nonnull EntryGroupData root);

	/**
	 * Returns {@link EntryGroupData} by groupNumber from given order
	 *
	 * @param groupNumber
	 *           number of the group to search for
	 * @param order
	 *           order containing entry group trees
	 *
	 * @return {@link EntryGroupData} with given groupNumber from the order
	 * @throws IllegalArgumentException
	 *            if no group with given groupNumber in the order
	 * @throws IllegalArgumentException
	 *            if {@code order} is null
	 * @throws IllegalArgumentException
	 *            if {@code order.rootGroups} is null
	 * @throws IllegalArgumentException
	 *            if {@code groupNumber} is null
	 */
	@Nonnull
	EntryGroupData getGroup(@Nonnull AbstractOrderData order, @Nonnull Integer groupNumber);
	
	/**
	 * Searches for entry group which is a type of {@code groupType} and which number belongs to the {@code groupNumbers}.
	 * @param order
	 * 				order what is expected to contain the desired group
	 * @param groupNumbers
	 * 				possible group numbers. Usually are taken from
	 * 				{@link de.hybris.platform.commercefacades.order.data.OrderEntryData#getEntryGroupNumbers()}
	 * @param groupType
	 * 				desired group type
	 * @return		group
	 * @throws		IllegalArgumentException if group was not found or any of the args is invalid
	 */
	@Nonnull
	EntryGroupData getGroup(@Nonnull AbstractOrderData order,
			@Nonnull Collection<Integer> groupNumbers, @Nonnull GroupType groupType);

	/**
	 * Returns max value of {@code EntryGroupData#groupNumber}.
	 *
	 * @param roots
	 *           root groups
	 * @return maximum group number among the whole forest
	 */
	int findMaxGroupNumber(List<EntryGroupData> roots);
}

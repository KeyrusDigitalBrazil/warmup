/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Stores a list of {@link WhereClauseCondition}s in the same order as the original list
 */
public class WhereClauseConditions
{
	private final List<WhereClauseCondition> conditions = new ArrayList<>();

	/**
	 * Creates a WhereClauseConditions with an array of {@link WhereClauseCondition}s.
	 * Null {@link WhereClauseCondition}s are not included .
	 *
	 * If {@code conditions} is null, an empty WhereClauseConditions is created.
	 *
	 * @param conditions Conditions to add to the WhereClauseConditions
	 */
	public WhereClauseConditions(final WhereClauseCondition... conditions)
	{
		if (conditions != null)
		{
			this.conditions.addAll(filterOutNulls(Arrays.asList(conditions)));
		}
	}

	/**
	 * Creates a WhereClauseConditions with a collection of {@link WhereClauseCondition}s.
	 * Null {@link WhereClauseCondition}s are not included.
	 *
	 * If {@code conditions} is null, an empty WhereClauseConditions is created.
	 *
	 * @param conditions Conditions to add to the WhereClauseConditions
	 */
	public WhereClauseConditions(final List<WhereClauseCondition> conditions)
	{
		if (conditions != null)
		{
			this.conditions.addAll(filterOutNulls(conditions));
		}
	}
	
	/**
	 * Gets a copy of the list of {@link WhereClauseCondition}
	 * @return List of conditions
	 */
	public List<WhereClauseCondition> getConditions()
	{
		return new ArrayList<>(conditions);
	}

	/**
	 * Joins the {@link WhereClauseCondition}s from this WhereClauseConditions with those of the other WhereClauseConditions.
	 * The conjunctive operator connects the last WhereClauseCondition from this WhereClauseConditions with the first
	 * WhereClauseCondition from the other WhereClauseConditions.
	 *
	 * This operation does not change the original WhereClauseCondition list of this WhereClauseConditions.
	 *
	 * @param otherWhereClauseConditions The other WhereClauseConditions to join with
	 * @param operator The operator that connects the two WhereClauseConditions
	 * @return The two WhereClauseConditions joined. If the {@code otherWhereClauseConditions} is null
	 * or {@code operator} is null/empty, this WhereClauseConditions is returned.
	 */
	public WhereClauseConditions join(final WhereClauseConditions otherWhereClauseConditions, final ConjunctiveOperator operator)
	{
		if (otherWhereClauseConditions != null && operator != null && ConjunctiveOperator.UNKNOWN != operator)
		{
			final List<WhereClauseCondition> joinedWhereClauseConditions = join(otherWhereClauseConditions.getConditions(), operator);
			return new WhereClauseConditions(joinedWhereClauseConditions);
		}
		return this;
	}

	private List<WhereClauseCondition> join(final List<WhereClauseCondition> otherConditions, final ConjunctiveOperator operator)
	{
		final List<WhereClauseCondition> joinedWhereClauseConditions = new ArrayList<>();
		final List<WhereClauseCondition> thisUpdatedConditions = updateLastWhereClauseConditionWithConjunctiveOperator(operator);
		joinedWhereClauseConditions.addAll(thisUpdatedConditions);
		joinedWhereClauseConditions.addAll(otherConditions);
		return joinedWhereClauseConditions;
	}

	private List<WhereClauseCondition> updateLastWhereClauseConditionWithConjunctiveOperator(final ConjunctiveOperator operator)
	{
		if (!this.conditions.isEmpty())
		{
			final List<WhereClauseCondition> updatedConditions = new ArrayList<>();
			final int size = this.conditions.size();
			if (size > 1)
			{
				updatedConditions.addAll(this.conditions.subList(0, size - 1));
			}
			final WhereClauseCondition lastWhereClauseCondition = this.conditions.get(size - 1);
			final WhereClauseCondition updatedLastWhereClauseCondition = new WhereClauseCondition(lastWhereClauseCondition.getCondition(), operator);
			updatedConditions.add(updatedLastWhereClauseCondition);
			return updatedConditions;
		}
		return this.conditions;
	}

	private List<WhereClauseCondition> filterOutNulls(final List<WhereClauseCondition> conditions)
	{
		return conditions.stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final WhereClauseConditions that = (WhereClauseConditions) o;

		return new EqualsBuilder()
				.append(conditions, that.conditions)
				.isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37)
				.append(conditions)
				.toHashCode();
	}

	@Override
	public String toString()
	{
		return "WhereClauseConditions{" +
				"conditions=" + conditions +
				'}';
	}
}

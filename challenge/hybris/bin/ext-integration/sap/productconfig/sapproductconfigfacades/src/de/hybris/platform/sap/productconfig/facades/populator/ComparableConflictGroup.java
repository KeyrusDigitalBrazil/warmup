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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Allows to sort conflict groups
 */
public class ComparableConflictGroup extends UiGroupData implements Comparable<ComparableConflictGroup>
{
	private List<UiGroupData> csticGroupsFlat;

	// We only need to compile the rank once and use it for every compare operation
	private Integer storedRank = null;

	/**
	 * @return the storedRank
	 */
	public Integer getStoredRank()
	{
		return storedRank;
	}


	/**
	 * @return the csticGroupsFlat
	 */
	public List<UiGroupData> getCsticGroupsFlat()
	{
		return Optional.ofNullable(csticGroupsFlat).orElseGet(this::getEmptyList);
	}

	/**
	 * @param csticGroupsFlat
	 *           the csticGroupsFlat to set
	 */
	public void setCsticGroupsFlat(final List<UiGroupData> csticGroupsFlat)
	{
		this.csticGroupsFlat = Optional.ofNullable(csticGroupsFlat).orElseGet(this::getEmptyList);
	}

	protected List getEmptyList()
	{
		return Collections.emptyList();
	}

	/**
	 * @return The rank of a conflict group. This is compiled from the list of cstic groups. The rank equals the number
	 *         of the first cstic which is part of the conflict
	 */
	public Integer rank()
	{
		if (storedRank != null)
		{
			return storedRank;
		}

		if (csticGroupsFlat == null)
		{
			throw new IllegalArgumentException("No list of groups containing all cstics");
		}

		final List<CsticData> csticsFromConflict = getCstics();
		if (csticsFromConflict == null)
		{
			throw new IllegalArgumentException("No cstics at conflict group");
		}

		final Set<String> myCstics = csticsFromConflict//
				.stream()//
				.map(a -> a.getName())//
				.collect(Collectors.toSet());


		return compileRankFromCsticList(myCstics);

	}


	protected Integer compileRankFromCsticList(final Set<String> myCstics)
	{
		int rank = 0;
		//now just check for the first occurrence in the list of flat cstic groups
		for (final UiGroupData uiGroup : csticGroupsFlat)
		{
			for (final CsticData cstic : uiGroup.getCstics())
			{
				rank++;
				if (myCstics.contains(cstic.getName()))
				{
					return toIntegerAndStore(rank);
				}
			}
		}
		return toIntegerAndStore(rank);
	}


	protected Integer toIntegerAndStore(final int rank)
	{
		final Integer determinedRank = Integer.valueOf(rank);
		storedRank = determinedRank;
		return determinedRank;
	}

	@Override
	public int compareTo(final ComparableConflictGroup otherConflictGroup)
	{
		return rank().compareTo(otherConflictGroup.rank());
	}

	@Override
	public boolean equals(final Object another)
	{
		if (another == this)
		{
			return true;
		}
		if (another == null)
		{
			return false;
		}
		if (this.getClass() == another.getClass())
		{
			return compareTo((ComparableConflictGroup) another) == 0;
		}
		else
		{
			return super.equals(another);
		}
	}

	@Override
	public int hashCode()
	{
		return rank().intValue();
	}

}

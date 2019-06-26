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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Utility class to analyze a UI path string to get the index of the group, subgroup and/or cstic, so the correct java
 * object can be obtained from the parent.<br>
 * Atypically u path string for CPQ look like this:<br>
 * <code>groups[1].subGroups[2].subGroups[3].cstics[0].value</code><br>
 * Immutable Object<br>
 */
public class PathExtractor
{
	private static final Pattern patternGroupCstic = Pattern
			.compile("^groups\\[(\\d+)\\](?:\\.subGroups\\[\\d+\\])*\\.cstics\\[(\\d+)\\](?:\\.value)?");
	private static final Pattern patternSubGroup = Pattern.compile("\\.subGroups\\[(\\d+)\\]");

	private Integer groupIndex = Integer.valueOf(-1);
	private Integer csticsIndex = Integer.valueOf(-1);
	private final List<Integer> subGroupIndices;

	/**
	 * Default Constructor.
	 *
	 * @param fieldPath
	 *           path to analyze
	 */
	public PathExtractor(final String fieldPath)
	{
		Matcher matcher = patternGroupCstic.matcher(fieldPath);
		if (matcher.find())
		{
			groupIndex = Integer.valueOf(matcher.group(1));
			csticsIndex = Integer.valueOf(matcher.group(2));
		}
		matcher = patternSubGroup.matcher(fieldPath);
		subGroupIndices = new ArrayList<Integer>();
		while (matcher.find())
		{
			final Integer subGroupIndex = Integer.valueOf(matcher.group(1));
			subGroupIndices.add(subGroupIndex);
		}
	}

	/**
	 * @return the 'root' groupIndex
	 */
	public int getGroupIndex()
	{
		return groupIndex.intValue();
	}


	/**
	 * @return the csticsIndex
	 */
	public int getCsticsIndex()
	{
		return csticsIndex.intValue();
	}

	/**
	 * @return total number of subGroupIndices found
	 */
	public int getSubGroupCount()
	{
		return subGroupIndices.size();
	}


	/**
	 * As a path string can contain any number of subgroup components, including zero, one can specify for which subgroup
	 * the index should be returned.
	 *
	 * @param subGroupNumber
	 *           index of the subGroup for which the path index should be returned
	 * @return the subGroupIndex with the given index
	 */
	public int getSubGroupIndex(final int subGroupNumber)
	{
		return subGroupIndices.get(subGroupNumber).intValue();
	}
}

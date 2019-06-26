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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;



@SuppressWarnings("javadoc")
@UnitTest
public class ComparableConflictGroupTest
{
	ComparableConflictGroup firstConflict = new ComparableConflictGroup();
	ComparableConflictGroup secondConflict = new ComparableConflictGroup();
	private final List<UiGroupData> csticGroupsFlat = new ArrayList<UiGroupData>();
	private final List<CsticData> csticsFirstConflict = new ArrayList<>();
	private final List<CsticData> csticsSecondConflict = new ArrayList<>();
	private final UiGroupData standardGroup1 = new UiGroupData();
	private final List<CsticData> allCsticsInFirstGroup = new ArrayList<>();
	private final UiGroupData standardGroup2 = new UiGroupData();
	private final List<CsticData> allCsticsInSecondGroup = new ArrayList<>();
	private final CsticData cstic1 = new CsticData();
	private final CsticData cstic2 = new CsticData();
	private final CsticData cstic3 = new CsticData();

	@Test(expected = IllegalArgumentException.class)
	public void testCsticsNull()
	{
		firstConflict.compareTo(secondConflict);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoCsticGroups()
	{
		setCsticsAtConflict();
		firstConflict.compareTo(secondConflict);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCompareNoMatchFound()
	{
		setCsticsAtConflict();
		firstConflict.setCsticGroupsFlat(csticGroupsFlat);

		firstConflict.compareTo(secondConflict);
	}

	@Test
	public void testCompare()
	{
		prepareTestData();
		assertTrue("First conflict first, as cstic1 is part of first conflict", firstConflict.compareTo(secondConflict) < 0);
	}

	@Test
	public void testCompareReverse()
	{
		prepareTestData();
		assertTrue("First conflict first, as cstic1 is part of first conflict", secondConflict.compareTo(firstConflict) > 0);
	}

	@Test
	public void testRank()
	{
		prepareTestData();
		assertEquals("First conflict must be of rank 1 as it contains the first cstic", 1, firstConflict.rank().intValue());
		assertEquals("Second conflict must be of rank 2 as it contains the second cstic", 2, secondConflict.rank().intValue());
	}

	@Test
	public void testRankOnlySecondMostImportant()
	{
		prepareTestData();
		firstConflict.getCstics().remove(cstic1);
		assertEquals("First conflict must be of rank 3 as it contains the third cstic", 3, firstConflict.rank().intValue());
	}

	@Test
	public void testStoredRankInitial()
	{
		prepareTestData();
		assertNull("Stored rank is available only after first compare operation", firstConflict.getStoredRank());
		assertNull("Stored rank is available only after first compare operation", secondConflict.getStoredRank());
	}

	@Test
	public void testStoredRank()
	{
		prepareTestData();
		assertEquals("First conflict must be of rank 1 as it contains the first cstic", 1, firstConflict.rank().intValue());
		assertEquals("Stored rank is available after first determination", Integer.valueOf(1), firstConflict.getStoredRank());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRankNoCsticList()
	{
		firstConflict.rank();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRankNoCsticsAtConflict()
	{
		firstConflict.setCsticGroupsFlat(csticGroupsFlat);
		firstConflict.rank();
	}


	@Test
	public void testCompareConflictsContainOnlySecondMostImportant()
	{
		prepareTestData();

		firstConflict.getCstics().remove(cstic1);
		assertTrue("Second conflict first, as cstic2 is the first one in entire list", firstConflict.compareTo(secondConflict) > 0);
	}

	@Test
	public void testCompareConflictsContainOnlySecondMostImportantReverse()
	{
		prepareTestData();

		firstConflict.getCstics().remove(cstic1);
		assertTrue("Second conflict first, as cstic2 is the first one in entire list", secondConflict.compareTo(firstConflict) < 0);
	}



	@Test
	public void testCompareSameRanking()
	{
		prepareTestData();

		firstConflict.getCstics().remove(cstic1);
		secondConflict.getCstics().remove(cstic2);
		assertEquals("Both conflicts only carry cstic 3, so they are equal", 0, secondConflict.compareTo(firstConflict));
	}

	@Test
	public void testCompareSameRankingReverse()
	{
		prepareTestData();

		firstConflict.getCstics().remove(cstic1);
		secondConflict.getCstics().remove(cstic2);
		assertEquals("Both conflicts only carry cstic 3, so they are equal", 0, firstConflict.compareTo(secondConflict));
	}

	@Test
	public void testCompareSameConflict()
	{
		prepareTestData();
		assertEquals("A conflict must be equal to itself", 0, firstConflict.compareTo(firstConflict));
		assertEquals("A conflict must be equal to itself", 0, secondConflict.compareTo(secondConflict));
	}

	@Test
	public void testEqualsSame()
	{
		prepareTestData();
		assertTrue(firstConflict.equals(firstConflict));
	}

	@Test
	public void testEqualsSameRanking()
	{
		prepareTestData();
		firstConflict.getCstics().remove(cstic1);
		secondConflict.getCstics().remove(cstic2);
		assertTrue(firstConflict.equals(secondConflict));
		assertTrue(secondConflict.equals(firstConflict));
	}

	@Test
	public void testEqualsNotEqual()
	{
		prepareTestData();
		assertFalse(firstConflict.equals(secondConflict));
		assertFalse(secondConflict.equals(firstConflict));
	}

	@Test
	public void testHashCodeSameRanking()
	{
		prepareTestData();
		firstConflict.getCstics().remove(cstic1);
		secondConflict.getCstics().remove(cstic2);
		assertEquals(firstConflict.hashCode(), secondConflict.hashCode());
	}

	@Test
	public void testHashCode()
	{
		prepareTestData();
		assertFalse(firstConflict.hashCode() == secondConflict.hashCode());
	}

	@Test
	public void testCompareConflictsDoNotContainCstics()
	{
		prepareTestData();

		firstConflict.getCstics().remove(cstic1);
		secondConflict.getCstics().remove(cstic2);
		secondConflict.getCstics().remove(cstic3);
		firstConflict.getCstics().remove(cstic3);
		assertEquals(0, firstConflict.compareTo(secondConflict));
	}

	@Test
	public void testToIntegerAndStore()
	{
		assertNull(firstConflict.getStoredRank());

		final int rank = 5;
		final Integer rankAsInteger = Integer.valueOf(rank);
		assertEquals(rankAsInteger, firstConflict.toIntegerAndStore(rank));
		assertEquals(rankAsInteger, firstConflict.getStoredRank());
	}

	@Test
	public void testGetCsticGroupsFlat()
	{
		assertEquals(csticGroupsFlat, firstConflict.getCsticGroupsFlat());
	}

	@Test
	public void testGetCsticGroupsFlatSetNull()
	{
		firstConflict.setCsticGroupsFlat(null);
		assertEquals(Collections.emptyList(), firstConflict.getCsticGroupsFlat());
	}


	private void setUpCstics()
	{
		csticGroupsFlat.add(standardGroup1);
		csticGroupsFlat.add(standardGroup2);
		standardGroup1.setCstics(allCsticsInFirstGroup);
		standardGroup2.setCstics(allCsticsInSecondGroup);
		allCsticsInFirstGroup.add(cstic1);
		allCsticsInFirstGroup.add(cstic2);
		allCsticsInSecondGroup.add(cstic3);
		csticsFirstConflict.add(cstic1);
		csticsFirstConflict.add(cstic3);
		cstic1.setName("Name1");
		cstic2.setName("Name2");
		cstic3.setName("Name3");
		csticsSecondConflict.add(cstic2);
		csticsSecondConflict.add(cstic3);

	}

	private void prepareTestData()
	{
		setCsticsAtConflict();
		setUpCstics();
		firstConflict.setCsticGroupsFlat(csticGroupsFlat);
		secondConflict.setCsticGroupsFlat(csticGroupsFlat);
	}


	private void setCsticsAtConflict()
	{
		firstConflict.setCstics(csticsFirstConflict);
		secondConflict.setCstics(csticsSecondConflict);
	}
}

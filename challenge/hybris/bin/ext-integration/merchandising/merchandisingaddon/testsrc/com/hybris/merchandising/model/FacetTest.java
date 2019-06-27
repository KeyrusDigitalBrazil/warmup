/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests {@link Facet}
 *
 */
public class FacetTest
{
	private static final String NAME = "Colour";
	private static final String CODE = "swatchColors";
	final Facet facet = new Facet();

	@Before
	public void setUp()
	{
		facet.setCode(CODE);
		facet.setName(NAME);
		facet.setValues(Arrays.asList("Brown", "Yellow"));
	}

	@Test
	public void testConstructor()
	{
		final Facet facet = new Facet(CODE, NAME);
		assertEquals(CODE, facet.getCode());
		assertEquals(NAME, facet.getName());

		final List<String> facetValues = new ArrayList<>();
		facetValues.add("Test");
		final Facet threeArgFacet = new Facet(CODE, NAME, facetValues);
		assertEquals(CODE, threeArgFacet.getCode());
		assertEquals(NAME, threeArgFacet.getName());
		assertNotNull(threeArgFacet.getValues());
	}

	@Test
	public void testAddValue()
	{
		final List<String> facetValues = new ArrayList<>();
		facetValues.add("Test");

		final Facet threeArgFacet = new Facet(CODE, NAME, facetValues);
		assertEquals(CODE, threeArgFacet.getCode());
		assertEquals(NAME, threeArgFacet.getName());
		assertNotNull(threeArgFacet.getValues());

		final String addedFacetValue = "Test2";
		threeArgFacet.addValue(addedFacetValue);
		assertEquals(2, threeArgFacet.getValues().size());
	}

	@Test
	public void testEquals()
	{
		final Facet facet1 = new Facet(CODE, NAME);
		final Facet facet2 = new Facet(CODE, NAME);
		assertTrue(facet1.equals(facet2));
	}

	@Test
	public void testToString()
	{
		final Facet facet1 = new Facet(CODE, NAME);
		final String facetAsString = facet1.toString();
		assertNotNull(facetAsString);
	}

	@Test
	public void testHashCode()
	{
		final int expectedHashCode = -438327872;
		final int expectedNullHashCode = 29792;
		final Facet facet1 = new Facet(CODE, NAME);
		final int hashCode = facet1.hashCode();
		assertEquals(expectedHashCode, hashCode);

		final Facet nullFacetValues = new Facet(null, null);
		final int nullHashCode = nullFacetValues.hashCode();
		assertEquals(expectedNullHashCode, nullHashCode);
	}

	@Test
	public void testCode()
	{
		assertEquals(CODE, facet.getCode());
	}

	@Test
	public void testName()
	{
		assertEquals(NAME, facet.getName());
	}

	@Test
	public void testFacetValues()
	{
		assertEquals(2, facet.getValues().size());
	}

	@Test
	public void testSerializability()
	{
		final byte[] serialized = SerializationUtils.serialize(facet);
		assertNotNull("Expected facet to be serializable", serialized);
	}
}

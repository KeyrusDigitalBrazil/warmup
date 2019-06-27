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
package de.hybris.platform.sap.productconfig.runtime.services.impl;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ClassificationSystemCPQAttributesContainerTest
{

	private ClassificationSystemCPQAttributesContainer classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ClassificationSystemCPQAttributesContainer("code", "name", "description", Collections.emptyMap(),
				Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
	}

	@Test
	public void testEqualsHashCodeSameObject()
	{
		assertEquals(classUnderTest, classUnderTest);
		assertEquals(classUnderTest.hashCode(), classUnderTest.hashCode());
	}

	@Test
	public void testEqualsHashSameDate()
	{
		final ClassificationSystemCPQAttributesContainer other = new ClassificationSystemCPQAttributesContainer("code", "name",
				"description", Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		assertEquals(classUnderTest, other);
		assertEquals(classUnderTest.hashCode(), other.hashCode());
	}


	@Test
	public void testEqualsHashNotSameCode()
	{
		final ClassificationSystemCPQAttributesContainer other = new ClassificationSystemCPQAttributesContainer("otherCode", "name",
				"description", Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		assertFalse(classUnderTest.equals(other));
		assertFalse(classUnderTest.hashCode() == other.hashCode());
	}

	@Test
	public void testEqualsHashNullObj()
	{
		assertFalse(classUnderTest.equals(ClassificationSystemCPQAttributesContainer.NULL_OBJ));
		assertFalse(classUnderTest.hashCode() == ClassificationSystemCPQAttributesContainer.NULL_OBJ.hashCode());
	}

	@Test
	public void testNullEqualsCases()
	{
		assertFalse(classUnderTest.equals(null));
		assertFalse(classUnderTest.equals(new String("Hello World")));
	}

	@Test
	public void testEqualsCodeCases()
	{
		classUnderTest = new ClassificationSystemCPQAttributesContainer(null, "name", "description", Collections.emptyMap(),
				Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());

		ClassificationSystemCPQAttributesContainer other = new ClassificationSystemCPQAttributesContainer("code", "name",
				"description", Collections.emptyMap(), Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		assertFalse(classUnderTest.equals(other));

		other = new ClassificationSystemCPQAttributesContainer(null, "name", "description", Collections.emptyMap(),
				Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap());
		assertTrue(classUnderTest.equals(other));
	}
}

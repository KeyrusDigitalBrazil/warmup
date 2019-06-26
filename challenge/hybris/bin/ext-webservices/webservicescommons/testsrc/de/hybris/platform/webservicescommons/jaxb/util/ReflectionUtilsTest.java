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
package de.hybris.platform.webservicescommons.jaxb.util;

import de.hybris.bootstrap.annotations.UnitTest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
@SuppressWarnings("unused")
public class ReflectionUtilsTest
{
	private final String stringField = "";
	private final int intField = 1;
	private final String[] arrayField = new String[0];
	private final List<String> listField = new ArrayList<>();
	private final Map<String, Integer> mapField = new HashMap<>();
	private final List<String[]>[] complexArrayField = new List[0];

	public static class ExtendedTest extends ReflectionUtilsTest
	{
		private final Map<List<String[][]>[][][], Map<Integer[][][][], List<Boolean[]>[]>> complexMapField = new HashMap<>();
	}

	@Test
	public void testGetFields()
	{
		//when
		final Collection<Field> allFields = ReflectionUtils.getAllFields(ReflectionUtilsTest.class);

		//then
		Assert.assertNotNull(allFields);

		final Set<String> names = allFields.stream().map(Field::getName).collect(Collectors.toSet());

		Assert.assertTrue(names.contains("stringField"));
		Assert.assertTrue(names.contains("intField"));
		Assert.assertTrue(names.contains("arrayField"));
		Assert.assertTrue(names.contains("listField"));
		Assert.assertTrue(names.contains("mapField"));
		Assert.assertTrue(names.contains("complexArrayField"));
	}

	@Test
	public void testGetFieldsWithInheritance()
	{
		//when
		final Collection<Field> allFields = ReflectionUtils.getAllFields(ExtendedTest.class);

		//then
		Assert.assertNotNull(allFields);

		final Set<String> names = allFields.stream().map(Field::getName).collect(Collectors.toSet());

		Assert.assertTrue(names.contains("stringField"));
		Assert.assertTrue(names.contains("intField"));
		Assert.assertTrue(names.contains("arrayField"));
		Assert.assertTrue(names.contains("listField"));
		Assert.assertTrue(names.contains("mapField"));
		Assert.assertTrue(names.contains("complexArrayField"));
		Assert.assertTrue(names.contains("complexMapField"));
	}

	@Test
	public void testArrayType()
	{
		//when
		final Class arrayType = ReflectionUtils.getArrayType(arrayField.getClass());

		//then
		Assert.assertEquals(String.class, arrayType);
	}

	@Test
	public void testComplexArrayType()
	{
		//when
		final Class arrayType = ReflectionUtils.getArrayType(complexArrayField.getClass());

		//then
		Assert.assertEquals(List.class, arrayType);
	}

	@Test
	public void testSimpleField() throws NoSuchFieldException, SecurityException
	{
		//given
		final Field field = ReflectionUtilsTest.class.getDeclaredField("stringField");

		//when
		final Collection<Class> types = ReflectionUtils.extractTypes(field);

		//then
		Assert.assertNotNull(types);
		Assert.assertEquals(1, types.size());
		Assert.assertTrue(types.contains(String.class));
	}


	@Test
	public void testPrimitiveField() throws NoSuchFieldException, SecurityException
	{
		//given
		final Field field = ReflectionUtilsTest.class.getDeclaredField("intField");

		//when
		final Collection<Class> types = ReflectionUtils.extractTypes(field);

		//then
		Assert.assertNotNull(types);
		Assert.assertEquals(1, types.size());
		Assert.assertTrue(types.contains(Integer.TYPE));
	}

	@Test
	public void testSimpleArrayField() throws NoSuchFieldException, SecurityException
	{
		//given
		final Field field = ReflectionUtilsTest.class.getDeclaredField("arrayField");

		//when
		final Collection<Class> types = ReflectionUtils.extractTypes(field);

		//then
		Assert.assertNotNull(types);
		Assert.assertEquals(1, types.size());
		Assert.assertTrue(types.contains(String.class));
	}


	@Test
	public void testComplexArrayField() throws NoSuchFieldException, SecurityException
	{
		//given
		final Field field = ReflectionUtilsTest.class.getDeclaredField("complexArrayField");

		//when
		final Collection<Class> types = ReflectionUtils.extractTypes(field);

		//then
		Assert.assertNotNull(types);
		Assert.assertEquals(1, types.size());
		Assert.assertTrue(types.contains(String.class));
	}

	@Test
	public void testSimpleListField() throws NoSuchFieldException, SecurityException
	{
		//given
		final Field field = ReflectionUtilsTest.class.getDeclaredField("listField");

		//when
		final Collection<Class> types = ReflectionUtils.extractTypes(field);

		//then
		Assert.assertNotNull(types);
		Assert.assertEquals(1, types.size());
		Assert.assertTrue(types.contains(String.class));
	}

	@Test
	public void testSimpleMapField() throws NoSuchFieldException, SecurityException
	{
		//given
		final Field field = ReflectionUtilsTest.class.getDeclaredField("mapField");

		//when
		final Collection<Class> types = ReflectionUtils.extractTypes(field);

		//then
		Assert.assertNotNull(types);
		Assert.assertEquals(2, types.size());
		Assert.assertTrue(types.contains(String.class));
		Assert.assertTrue(types.contains(Integer.class));
	}

	@Test
	public void tesComplexMapField() throws NoSuchFieldException, SecurityException
	{
		//given
		final Field field = ExtendedTest.class.getDeclaredField("complexMapField");

		//when
		final Collection<Class> types = ReflectionUtils.extractTypes(field);

		//then
		Assert.assertNotNull(types);
		Assert.assertEquals(3, types.size());
		Assert.assertTrue(types.contains(String.class));
		Assert.assertTrue(types.contains(Integer.class));
		Assert.assertTrue(types.contains(Boolean.class));
	}
}

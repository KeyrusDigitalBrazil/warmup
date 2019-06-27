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
package de.hybris.platform.webservicescommons.jaxb;

import de.hybris.platform.webservicescommons.jaxb.metadata.impl.DefaultMetadataNameProvider;
import de.hybris.platform.webservicescommons.jaxb.metadata.impl.DefaultMetadataSourceFactory;
import de.hybris.platform.webservicescommons.mapping.SubclassRegistry;
import de.hybris.platform.webservicescommons.mapping.impl.DefaultSubclassRegistry;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jersey.repackaged.com.google.common.collect.Sets;


//@IntegrationTest
public class MoxyJaxbContextFactoryTest
{

	private MoxyJaxbContextFactoryImpl factory;

	@Before
	public void setup()
	{
		final DefaultMetadataSourceFactory metadataSourceFactory = new DefaultMetadataSourceFactory();
		metadataSourceFactory.setNameProvider(new DefaultMetadataNameProvider());

		final SubclassRegistry registry = new DefaultSubclassRegistry();
		registry.registerSubclass(BaseA.class, BaseB.class);
		registry.registerSubclass(InnerA.class, InnerB.class);
		registry.registerSubclass(InnerA.class, InnerC.class);

		factory = new MoxyJaxbContextFactoryImpl();
		factory.setAnalysisDepth(5);
		factory.setMetadataSourceFactory(metadataSourceFactory);
		factory.setSubclassRegistry(registry);
	}

	@Test
	public void testJavaClass()
	{
		//given
		final Set<Class<?>> expected = Sets.newHashSet(String.class);

		//when
		final Set<Class<?>> actual = factory.computeAllClasses(String.class);

		//then
		assertClassSet(expected, actual);
	}

	@Test
	public void testSimpleClass()
	{
		//given
		final Set<Class<?>> expected = Sets.newHashSet(Simple.class, SimpleField.class);

		//when
		final Set<Class<?>> actual = factory.computeAllClasses(Simple.class);

		//then
		assertClassSet(expected, actual);
	}

	@Test
	public void testClassWithParents()
	{
		//given
		final Set<Class<?>> expected = Sets.newHashSet(InnerA.class, InnerB.class, InnerC.class, SimpleField.class);

		//when
		final Set<Class<?>> actual = factory.computeAllClasses(InnerB.class);

		//then
		assertClassSet(expected, actual);
	}

	@Test
	public void testClassWithSublcass()
	{
		//given
		final Set<Class<?>> expected = Sets.newHashSet(InnerA.class, InnerB.class, InnerC.class, SimpleField.class);

		//when
		final Set<Class<?>> actual = factory.computeAllClasses(InnerA.class);

		//then
		assertClassSet(expected, actual);
	}

	@Test
	public void testClassWithNestedSublcass()
	{
		//given
		final Set<Class<?>> expected = Sets.newHashSet(BaseA.class, BaseB.class, InnerA.class, InnerB.class, InnerC.class,
				SimpleField.class);

		//when
		final Set<Class<?>> actual = factory.computeAllClasses(BaseA.class);

		//then
		assertClassSet(expected, actual);
	}

	@Test
	public void testClassWithRecursive()
	{
		//given
		final Set<Class<?>> expected = Sets.newHashSet(Recursive.class);

		//when
		final Set<Class<?>> actual = factory.computeAllClasses(Recursive.class);

		//then
		assertClassSet(expected, actual);
	}

	@Test
	public void testLevelDepth5()
	{
		//given
		factory.setAnalysisDepth(5);
		final Set<Class<?>> expected = Sets.newHashSet(Level0.class, Level1.class, Level2.class, Level3.class, Level4.class,
				Level5.class, BaseA.class, BaseB.class, InnerA.class, InnerB.class, InnerC.class, SimpleField.class);

		//when
		final Set<Class<?>> actual = factory.computeAllClasses(Level0.class);

		//then
		assertClassSet(expected, actual);
	}

	@Test
	public void testLevelDepth4()
	{
		//given
		factory.setAnalysisDepth(4);
		final Set<Class<?>> expected = Sets.newHashSet(Level0.class, Level1.class, Level2.class, Level3.class, Level4.class,
				BaseA.class, BaseB.class);

		//when
		final Set<Class<?>> actual = factory.computeAllClasses(Level0.class);

		//then
		assertClassSet(expected, actual);
	}

	@Test
	public void testLevelDepth3()
	{
		//given
		factory.setAnalysisDepth(3);
		final Set<Class<?>> expected = Sets.newHashSet(Level0.class, Level1.class, Level2.class, Level3.class);

		//when
		final Set<Class<?>> actual = factory.computeAllClasses(Level0.class);

		//then
		assertClassSet(expected, actual);
	}

	public void assertClassSet(final Collection<Class<?>> expected, final Collection<Class<?>> actual)
	{
		Assert.assertEquals(expected.stream().map(Class::getSimpleName).collect(Collectors.toSet()),
				actual.stream().map(Class::getSimpleName).collect(Collectors.toSet()));
	}

	public static class Simple
	{
		public SimpleField field;
	}

	public static class SimpleField
	{
		public SimpleField simpleField;
	}

	public static class BaseA
	{
		public SimpleField baseA;
	}

	public static class BaseB extends BaseA
	{
		public SimpleField baseB;
		public InnerA innerField;
	}

	public static class InnerA
	{
		public SimpleField innerA;
	}

	public static class InnerB extends InnerA
	{
		public SimpleField innerB;
	}

	public static class InnerC extends InnerA
	{
		public SimpleField innerC;
	}

	public static class Recursive
	{
		public Recursive field;
	}

	public static class Level0
	{
		public Level1 field;
	}

	public static class Level1
	{
		public Level2 field;
	}

	public static class Level2
	{
		public Level3 field;
	}

	public static class Level3
	{
		public Level4 field;
		public BaseA innerField;
	}

	public static class Level4
	{
		public Level5 field;
	}

	public static class Level5
	{
		public Level6 field;
	}

	public static class Level6
	{
		public Level7 field;
	}

	public static class Level7
	{
		public Recursive field;
	}
}

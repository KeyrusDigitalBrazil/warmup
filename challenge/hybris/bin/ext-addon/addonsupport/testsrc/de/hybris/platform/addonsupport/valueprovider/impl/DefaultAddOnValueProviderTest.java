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
package de.hybris.platform.addonsupport.valueprovider.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for the {@link DefaultAddOnValueProvider} class.
 */
@UnitTest
public class DefaultAddOnValueProviderTest
{
	private DefaultAddOnValueProvider provider;

	@Mock
	private Supplier<Boolean> booleanSupplier;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final Map<String, Supplier> suppliers = new HashMap<>();

		suppliers.put("booleanValue", booleanSupplier);

		provider = new DefaultAddOnValueProvider();
		provider.setSuppliers(suppliers);
	}

	@Test
	public void testShouldGetBooleanValue()
	{
		BDDMockito.when(booleanSupplier.get()).thenReturn(Boolean.TRUE);

		Optional<Boolean> optional = provider.getValue("booleanValue", Boolean.class);
		Assert.assertTrue("Optional value not present.", optional.isPresent());
		Assert.assertEquals("Unexpected value returned.", Boolean.TRUE, optional.get());


		BDDMockito.when(booleanSupplier.get()).thenReturn(Boolean.FALSE);

		optional = provider.getValue("booleanValue", Boolean.class);
		Assert.assertTrue("Optional value not present.", optional.isPresent());
		Assert.assertEquals("Unexpected value returned.", Boolean.FALSE, optional.get());
	}

	@Test
	public void testShouldGetEmptyOptionalNoSupplierForKey()
	{
		final Optional<Boolean> optional = provider.getValue("unknownValue", Boolean.class);
		Assert.assertFalse("Optional value should not present.", optional.isPresent());
	}

	@Test
	public void testShouldGetEmptyOptionalWrongType()
	{
		BDDMockito.when(booleanSupplier.get()).thenReturn(Boolean.TRUE);

		final Optional<String> optional = provider.getValue("booleanValue", String.class);
		Assert.assertFalse("Optional value should not present.", optional.isPresent());
	}

	@Test
	public void testShouldGetEmptyOptionalNullValue()
	{
		BDDMockito.when(booleanSupplier.get()).thenReturn(null);

		final Optional<Boolean> optional = provider.getValue("booleanValue", Boolean.class);
		Assert.assertFalse("Optional value should not present.", optional.isPresent());
	}

	@Test
	public void testShouldGetEmptyOptionalForNullType()
	{
		BDDMockito.when(booleanSupplier.get()).thenReturn(Boolean.TRUE);

		final Optional<Boolean> optional = provider.getValue("booleanValue", null );
		Assert.assertFalse("Optional value should not present when supplying null type.", optional.isPresent());
	}

	@Test
	public void testShouldGetEmptyOptionalForNullKey()
	{
		BDDMockito.when(booleanSupplier.get()).thenReturn(Boolean.TRUE);

		final Optional<Boolean> optional = provider.getValue( null , Boolean.class );
		Assert.assertFalse("Optional value should not present when supplying a null key.", optional.isPresent());
	}

}

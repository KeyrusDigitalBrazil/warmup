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

package de.hybris.platform.acceleratorservices.dataimport.batch.converter;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.header.AbstractDescriptor.DescriptorParams;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.jalo.Item;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 * Test for {@link CopyrightTranslator}
 */
@UnitTest
public class CopyrightTranslatorTest
{
	@Spy
	private CopyrightTranslator translator;
	@Mock
	private Item item;
	@Mock
	StandardColumnDescriptor columnDescription;
	@Mock
	DescriptorParams descriptionParams;
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(columnDescription).when(translator).getColumnDescriptor();
		Mockito.when(columnDescription.getDescriptorData()).thenReturn(descriptionParams);
		Mockito.when(descriptionParams.getModifier("dateFormat")).thenReturn("yyyy");
	}

	@Test
	public void testNull()
	{
		Assert.assertTrue(StringUtils.isEmpty((String) translator.importValue(null, item)));
	}

	@Test
	public void testEmpty()
	{
		Assert.assertTrue(StringUtils.isEmpty((String) translator.importValue(null, item)));
	}

	@Test
	public void testValidMessageWithDateFormat()
	{

		Assert.assertEquals(translator.importValue("Copyright {0}", item), "Copyright " + DateTime.now().getYear());
	}

	@Test
	public void testValidMessageWithoutDateFormat()
	{
		Mockito.when(descriptionParams.getModifier("dateFormat")).thenReturn("");
		Assert.assertEquals(translator.importValue("Copyright {0}", item), "Copyright " + DateTime.now().getYear());
	}

	@Test
	public void testInvalidMessageOne()
	{
		Assert.assertEquals(translator.importValue("Copyright", item), "Copyright");
		Assert.assertFalse(translator.wasUnresolved());
	}

	@Test
	public void testInvalidMessageTwo()
	{
		Assert.assertEquals(translator.importValue("Copyright{1}", item), "Copyright{1}");
		Assert.assertFalse(translator.wasUnresolved());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidMessageThree()
	{
		Assert.assertEquals(translator.importValue("Copyright{}", item), "Copyright");
		Assert.assertFalse(translator.wasUnresolved());
	}

}

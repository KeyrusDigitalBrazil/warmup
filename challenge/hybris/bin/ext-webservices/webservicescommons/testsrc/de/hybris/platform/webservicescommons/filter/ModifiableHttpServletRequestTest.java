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
package de.hybris.platform.webservicescommons.filter;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class ModifiableHttpServletRequestTest
{
	private static final String[] VALUE1 = new String[]
	{ "value1" };
	private static final String[] VALUE2 = new String[]
	{ "value2a", "value2b" };
	private static final String[] VALUE3 = new String[]
	{ "value3" };
	private static final String[] VALUE3N = new String[]
	{ "new3" };
	private static final String[] VALUE4 = new String[]
	{ "new4a", "new4b" };
	private static final String[] VALUE5 = new String[]
	{ "new5" };

	private HttpServletRequest orgRequest;

	@Before
	public void setUp()
	{
		orgRequest = Mockito.mock(HttpServletRequest.class);
		Mockito.when(orgRequest.getParameterMap()).thenReturn(getOrgParameters());
	}

	private Map<String, String[]> getOrgParameters()
	{
		final Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("key1", VALUE1);
		map.put("key2", VALUE2);
		map.put("key3", VALUE3);
		return map;
	}

	private Map<String, String[]> getParameters()
	{
		final Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("key3", VALUE3N);
		map.put("key4", VALUE4);
		map.put("key5", VALUE5);
		return map;
	}

	@Test
	public void getOrgSingleValue()
	{
		final HttpServletRequest request = new ModifiableHttpServletRequest(orgRequest, getParameters());
		final String actual = request.getParameter("key1");
		Assert.assertEquals(VALUE1[0], actual);
	}

	@Test
	public void getOrgSingleFromMultiValue()
	{
		final HttpServletRequest request = new ModifiableHttpServletRequest(orgRequest, getParameters());
		final String actual = request.getParameter("key2");
		Assert.assertEquals(VALUE2[0], actual);
	}

	@Test
	public void getOrgMultiValue()
	{
		final HttpServletRequest request = new ModifiableHttpServletRequest(orgRequest, getParameters());
		final String[] actual = request.getParameterValues("key2");
		Assert.assertArrayEquals(VALUE2, actual);
	}

	@Test
	public void getSingleValueNotOverride()
	{
		final HttpServletRequest request = new ModifiableHttpServletRequest(orgRequest, getParameters());
		final String actual = request.getParameter("key3");
		Assert.assertEquals(VALUE3[0], actual);
	}

	@Test
	public void getSingleValueOverride()
	{
		final HttpServletRequest request = new ModifiableHttpServletRequest(orgRequest, getParameters(), true);
		final String actual = request.getParameter("key3");
		Assert.assertEquals(VALUE3N[0], actual);
	}

	@Test
	public void getNewSingleValue()
	{
		final HttpServletRequest request = new ModifiableHttpServletRequest(orgRequest, getParameters());
		final String actual = request.getParameter("key5");
		Assert.assertEquals(VALUE5[0], actual);
	}

	@Test
	public void getNewSingleFromMultiValue()
	{
		final HttpServletRequest request = new ModifiableHttpServletRequest(orgRequest, getParameters());
		final String actual = request.getParameter("key4");
		Assert.assertEquals(VALUE4[0], actual);
	}

	@Test
	public void getNewMultiValue()
	{
		final HttpServletRequest request = new ModifiableHttpServletRequest(orgRequest, getParameters());
		final String[] actual = request.getParameterValues("key4");
		Assert.assertArrayEquals(VALUE4, actual);
	}

	@Test
	public void getPerameterNames()
	{
		final HttpServletRequest request = new ModifiableHttpServletRequest(orgRequest, getParameters());
		final Enumeration<String> actual = request.getParameterNames();
		final Set<String> actualSet = new HashSet<String>();
		CollectionUtils.addAll(actualSet, actual);

		final Set<String> expected = new HashSet<String>(Arrays.asList("key1", "key2", "key3", "key4", "key5"));

		Assert.assertEquals(expected, actualSet);
	}

	@Test
	public void getParameterMapNotOverride()
	{
		final ModifiableHttpServletRequest request = new ModifiableHttpServletRequest(orgRequest, getParameters());
		final Map<String, String[]> actual = request.getParameterMap();
		final Map<String, String[]> expected = new HashMap<String, String[]>();
		expected.put("key1", VALUE1);
		expected.put("key2", VALUE2);
		expected.put("key3", VALUE3);
		expected.put("key4", VALUE4);
		expected.put("key5", VALUE5);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void getParameterMapOverride()
	{
		final ModifiableHttpServletRequest request = new ModifiableHttpServletRequest(orgRequest, getParameters(), true);
		final Map<String, String[]> actual = request.getParameterMap();
		final Map<String, String[]> expected = new HashMap<String, String[]>();
		expected.put("key1", VALUE1);
		expected.put("key2", VALUE2);
		expected.put("key3", VALUE3N);
		expected.put("key4", VALUE4);
		expected.put("key5", VALUE5);

		Assert.assertEquals(expected, actual);
	}
}

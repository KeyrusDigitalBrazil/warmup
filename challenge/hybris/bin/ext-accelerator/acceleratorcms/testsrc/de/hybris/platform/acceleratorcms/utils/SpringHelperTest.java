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
package de.hybris.platform.acceleratorcms.utils;

import de.hybris.bootstrap.annotations.UnitTest;

import javax.servlet.ServletRequest;

import org.apache.commons.lang.StringUtils;
import org.fest.assertions.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


@UnitTest
public class SpringHelperTest
{
	private static final String BEAN_TEST_NAME = "testName";
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testValidateNullRequest()
	{
		thrown.expect(IllegalArgumentException.class);
		SpringHelper.getSpringBean(null, BEAN_TEST_NAME, String.class, false);
	}

	@Test
	public void testValidateNullBeanName()
	{
		thrown.expect(IllegalArgumentException.class);
		SpringHelper.getSpringBean(Mockito.mock(ServletRequest.class), null, null, false);
	}

	@Test
	public void testValidateNullBeanClass()
	{
		thrown.expect(IllegalArgumentException.class);
		SpringHelper.getSpringBean(Mockito.mock(ServletRequest.class), BEAN_TEST_NAME, null, false);
	}

	@Test
	public void testGetSpringBeanForNonExisiting()
	{
		final String result = SpringHelper.getSpringBean(Mockito.mock(ServletRequest.class), BEAN_TEST_NAME, String.class, false);
		Assertions.assertThat(StringUtils.isBlank(result)).isTrue();
	}

}

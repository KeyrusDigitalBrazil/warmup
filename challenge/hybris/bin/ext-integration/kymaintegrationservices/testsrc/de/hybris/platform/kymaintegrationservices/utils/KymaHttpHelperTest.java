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

package de.hybris.platform.kymaintegrationservices.utils;

import static de.hybris.platform.kymaintegrationservices.utils.KymaHttpHelper.getDefaultHeaders;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@UnitTest
public class KymaHttpHelperTest
{
	@Test
	public void testDefaultHeaders()
	{
		final HttpHeaders headers = getDefaultHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		assertEquals(Arrays.asList(MediaType.ALL), headers.getAccept());
		assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
	}
}

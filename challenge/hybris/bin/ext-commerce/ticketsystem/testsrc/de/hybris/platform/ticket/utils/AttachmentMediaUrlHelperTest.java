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
package de.hybris.platform.ticket.utils;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests of AttachmentMediaUrlHelper class.
 */
@UnitTest
public class AttachmentMediaUrlHelperTest
{

	/**
	 * Should add ~ if the url start with /.
	 */
	@Test
	public void shouldAddTilde()
	{
		final String url = "/medias/test.png?context=bWFzdGVyfHJvb3R8";

		Assert.assertEquals("~/medias/test.png?context=bWFzdGVyfHJvb3R8", AttachmentMediaUrlHelper.urlHelper(url));
	}

	/**
	 * Should not add ~ if the url not starting with /.
	 */
	@Test
	public void shouldNotAddTilde()
	{
		final String url = "securemedias?mediaPK=8797240066078";

		Assert.assertEquals("securemedias?mediaPK=8797240066078", AttachmentMediaUrlHelper.urlHelper(url));
	}
}

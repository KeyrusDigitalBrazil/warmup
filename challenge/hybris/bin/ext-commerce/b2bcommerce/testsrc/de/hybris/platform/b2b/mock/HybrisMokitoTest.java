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
package de.hybris.platform.b2b.mock;

import de.hybris.platform.servicelayer.MockTest;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;


@Ignore
@ContextConfiguration(locations =
{ "classpath:/servicelayer/test/servicelayer-mock-base-test.xml",
		"classpath:/servicelayer/test/servicelayer-mock-i18n-test.xml" })
public class HybrisMokitoTest extends MockTest
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(HybrisMokitoTest.class);

	public HybrisMokitoTest()
	{
		// enable @Mock annotation
		super();
		MockitoAnnotations.initMocks(this);
	}

}

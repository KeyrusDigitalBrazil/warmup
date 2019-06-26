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
package de.hybris.platform.sap.core.bol.test;

import de.hybris.platform.sap.core.jco.rec.SapcoreJCoRecJUnitTest;

import org.springframework.test.context.ContextConfiguration;


/**
 * Base test class for junit test in the hybris environment without starting the server.
 */
@ContextConfiguration(locations =
{ "classpath:sapcorebol-spring.xml", "classpath:global-sapcorejco-spring.xml", "sapcorebol-test-spring.xml" })
public abstract class SapcorebolSpringJUnitTest extends SapcoreJCoRecJUnitTest//SapcoreJCoJUnitTest // NOPMD
{

}

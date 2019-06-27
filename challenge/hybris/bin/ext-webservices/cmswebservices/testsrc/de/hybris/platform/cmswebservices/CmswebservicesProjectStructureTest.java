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
package de.hybris.platform.cmswebservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.test.structure.AbstractProjectStructureTest;

import java.util.logging.Logger;


/**
 * Test to find duplicate jars in the extension. If the jars are already defined in the platform, they should not be
 * redefined in this extension.
 */
@IntegrationTest
public class CmswebservicesProjectStructureTest extends AbstractProjectStructureTest
{
	private static final Logger log = Logger.getLogger(CmswebservicesProjectStructureTest.class.getName());

	public CmswebservicesProjectStructureTest()
	{
		super(true, CmswebservicesConstants.EXTENSIONNAME);
	}

}

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
package de.hybris.platform.webservicescommons.structure;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.test.structure.AbstractProjectStructureTest;
import de.hybris.platform.webservicescommons.constants.WebservicescommonsConstants;


@IntegrationTest
@SuppressWarnings("squid:S2187")
public class ProjectStructureTest extends AbstractProjectStructureTest
{
	public ProjectStructureTest()
	{
		super(WebservicescommonsConstants.EXTENSIONNAME);
	}
}

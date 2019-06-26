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
package com.sap.hybris.scpiconnector.setup;

import static com.sap.hybris.scpiconnector.constants.ScpiconnectorConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.sap.hybris.scpiconnector.constants.ScpiconnectorConstants;
import com.sap.hybris.scpiconnector.service.ScpiconnectorService;


@SystemSetup(extension = ScpiconnectorConstants.EXTENSIONNAME)
public class ScpiconnectorSystemSetup
{
	private final ScpiconnectorService scpiconnectorService;

	public ScpiconnectorSystemSetup(final ScpiconnectorService scpiconnectorService)
	{
		this.scpiconnectorService = scpiconnectorService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		scpiconnectorService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return ScpiconnectorSystemSetup.class.getResourceAsStream("/scpiconnector/sap-hybris-platform.png");
	}
}

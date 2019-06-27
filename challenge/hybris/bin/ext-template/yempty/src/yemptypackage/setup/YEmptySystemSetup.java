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
package yemptypackage.setup;

import static yemptypackage.constants.YEmptyConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import yemptypackage.constants.YEmptyConstants;
import yemptypackage.service.YEmptyService;


@SystemSetup(extension = YEmptyConstants.EXTENSIONNAME)
public class YEmptySystemSetup
{
	private final YEmptyService yemptyService;

	public YEmptySystemSetup(final YEmptyService yemptyService)
	{
		this.yemptyService = yemptyService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		yemptyService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return YEmptySystemSetup.class.getResourceAsStream("/yempty/sap-hybris-platform.png");
	}
}

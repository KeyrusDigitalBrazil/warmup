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
package de.hybris.platform.sap.sapcpiorderexchangeb2b.setup;

import static de.hybris.platform.sap.sapcpiorderexchangeb2b.constants.Sapcpiorderexchangeb2bConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import de.hybris.platform.sap.sapcpiorderexchangeb2b.constants.Sapcpiorderexchangeb2bConstants;
import de.hybris.platform.sap.sapcpiorderexchangeb2b.service.Sapcpiorderexchangeb2bService;


@SystemSetup(extension = Sapcpiorderexchangeb2bConstants.EXTENSIONNAME)
public class Sapcpiorderexchangeb2bSystemSetup
{
	private final Sapcpiorderexchangeb2bService sapcpiorderexchangeb2bService;

	public Sapcpiorderexchangeb2bSystemSetup(final Sapcpiorderexchangeb2bService sapcpiorderexchangeb2bService)
	{
		this.sapcpiorderexchangeb2bService = sapcpiorderexchangeb2bService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpiorderexchangeb2bService.createLogo(PLATFORM_LOGO_CODE);
	}

}

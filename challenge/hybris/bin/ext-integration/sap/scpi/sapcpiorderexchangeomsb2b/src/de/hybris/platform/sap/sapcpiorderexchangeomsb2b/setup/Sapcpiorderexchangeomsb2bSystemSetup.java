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
package de.hybris.platform.sap.sapcpiorderexchangeomsb2b.setup;

import static de.hybris.platform.sap.sapcpiorderexchangeomsb2b.constants.Sapcpiorderexchangeomsb2bConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import de.hybris.platform.sap.sapcpiorderexchangeomsb2b.constants.Sapcpiorderexchangeomsb2bConstants;
import de.hybris.platform.sap.sapcpiorderexchangeomsb2b.service.Sapcpiorderexchangeomsb2bService;

@SystemSetup(extension = Sapcpiorderexchangeomsb2bConstants.EXTENSIONNAME)
public class Sapcpiorderexchangeomsb2bSystemSetup
{
	private final Sapcpiorderexchangeomsb2bService sapcpiorderexchangeomsb2bService;

	public Sapcpiorderexchangeomsb2bSystemSetup(final Sapcpiorderexchangeomsb2bService sapcpiorderexchangeomsb2bService)
	{
		this.sapcpiorderexchangeomsb2bService = sapcpiorderexchangeomsb2bService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpiorderexchangeomsb2bService.createLogo(PLATFORM_LOGO_CODE);
	}

}

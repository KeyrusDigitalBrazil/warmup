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
package com.sap.hybris.sec.eventpublisher.b2b.setup;

import static com.sap.hybris.sec.eventpublisher.b2b.constants.Eventpublisherb2bConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.sap.hybris.sec.eventpublisher.b2b.constants.Eventpublisherb2bConstants;
import com.sap.hybris.sec.eventpublisher.b2b.service.Eventpublisherb2bService;


@SystemSetup(extension = Eventpublisherb2bConstants.EXTENSIONNAME)
public class Eventpublisherb2bSystemSetup
{
	private final Eventpublisherb2bService eventpublisherb2bService;

	public Eventpublisherb2bSystemSetup(final Eventpublisherb2bService eventpublisherb2bService)
	{
		this.eventpublisherb2bService = eventpublisherb2bService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		eventpublisherb2bService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return Eventpublisherb2bSystemSetup.class.getResourceAsStream("/eventpublisherb2b/sap-hybris-platform.png");
	}
}

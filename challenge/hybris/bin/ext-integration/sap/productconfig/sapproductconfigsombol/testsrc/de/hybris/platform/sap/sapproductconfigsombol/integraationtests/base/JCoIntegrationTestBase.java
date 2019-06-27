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
package de.hybris.platform.sap.sapproductconfigsombol.integraationtests.base;

import de.hybris.platform.sap.core.configuration.impl.DefaultSAPConfigurationService;
import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.JCoRecording;
import de.hybris.platform.util.Utilities;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(locations =
{ "classpath:test/integration_test-sapcore-connection-spring.xml" })
@JCoRecording(mode = JCoRecMode.PLAYBACK, recordingExtensionName = "sapproductconfigsombol")
@SuppressWarnings("javadoc")
public class JCoIntegrationTestBase extends JCORecTestBase
{

	@Resource
	DefaultSAPConfigurationService defaultSAPConfigurationService;

	public final static String DATA_PATH_PREFIX = "resources/test/";

	@Override
	public void setUp()
	{
		super.setUp();

		defaultSAPConfigurationService.setRfcDestinationName("SAP_ERP_617");

	}

	public static String getCanonicalPathOfExtensionsapproductconfigsombolTest() throws IOException
	{
		return Utilities.getExtensionInfo("sapproductconfigsombol").getExtensionDirectory().getCanonicalPath() + File.separator;
	}

}

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
package de.hybris.platform.sap.productconfig.cpiorderexchange.ssc.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;

import com.sap.sce.kbrt.ext_configuration;


@UnitTest
public class DefaultExternalConfigurationParserTest
{

	private DefaultExternalConfigurationParser classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new DefaultExternalConfigurationParser();
	}

	@Test
	public void testSetGetLanguage() throws Exception
	{
		classUnderTest.set_language("DE");
		assertEquals("DE", classUnderTest.get_language());
	}

	@Test
	public void testReadExternalConfigFromString() throws Exception
	{
		final String str = "<CONFIGURATION CFGINFO=\"VCOND=WEC_SURCHARGE\" CLIENT=\"000\""
				+ " COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"2\" KBNAME=\"WCEM_MULTILEVEL_KB\""
				+ " KBPROFILE=\"WCEM_MULTILEVEL_PROFILE\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\""
				+ " NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\""
				+ " COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\""
				+ " OBJ_KEY=\"WCEM_MULTILEVEL\" OBJ_TXT=\"SAP Complex Multi level Test\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\">"
				+ "<CSTICS>" + "	<CSTIC AUTHOR=\"5\" CHARC=\"EXP_NO_USERS\" CHARC_TXT=\"Expected Number of Users\" VALUE=\"250.0\"/>"
				+ "</CSTICS></INST></CONFIGURATION>";

		final ext_configuration externalConfig = classUnderTest.readExternalConfigFromString(str);

		assertNotNull(externalConfig);
		assertEquals("VCOND=WEC_SURCHARGE", externalConfig.get_cfg_info());
		assertEquals("000", externalConfig.get_client());
		assertEquals("WCEM_MULTILEVEL_PROFILE", externalConfig.get_kb_profile_name());
		assertEquals(Integer.valueOf(1), externalConfig.get_root_id());
	}

}

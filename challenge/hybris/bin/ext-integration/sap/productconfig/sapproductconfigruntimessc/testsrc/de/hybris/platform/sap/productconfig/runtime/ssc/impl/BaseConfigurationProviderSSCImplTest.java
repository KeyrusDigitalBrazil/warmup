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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.cfg.client.IKnowledgeBaseData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.KnowledgeBaseData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;


/**
 * Unit Tests
 */
@UnitTest
public class BaseConfigurationProviderSSCImplTest
{
	private static final String P_CODE = "pCode";
	private final BaseConfigurationProviderSSCImpl classUnderTest = new BaseConfigurationProviderSSCImpl()
	{
		@Override
		public IKnowledgeBaseData[] callSSCtoFindKBs(final KBKey kbKey, final String kbDateString)
		{
			if (P_CODE.equals(kbKey.getProductCode()))
			{
				if (kbDateString == null)
				{
					return allKbs;
				}
				else
				{
					return validKbs;
				}
			}
			return null;
		}

		@Override
		protected ConfigModel fillConfigModel(final String qualifiedId)
		{
			return null;
		}

		@Override
		public String changeConfiguration(final ConfigModel model) throws ConfigurationEngineException
		{
			return null;
		}

		@Override
		public void releaseSession(final String configId, final String version)
		{
			// empty
		}
	};

	private static final String sessionId = "session1";
	private static final String configId = "12938";
	private IKnowledgeBaseData[] allKbs;
	private IKnowledgeBaseData[] validKbs;
	private final ConfigModel configModel = new ConfigModelImpl();


	@Before
	public void setUp() throws IpcCommandException
	{
		MockitoAnnotations.initMocks(this);
		allKbs = new IKnowledgeBaseData[]
		{ createKBData("kbName", "v1"), createKBData("kbName", "v2"), createKBData("kbName", "v3") };
		validKbs = new IKnowledgeBaseData[]
		{ createKBData("kbName", "v1"), createKBData("kbName", "v2") };

		configModel.setKbKey(new KBKeyImpl(P_CODE, P_CODE, "LOGSYS", "VERSION"));

	}

	protected IKnowledgeBaseData createKBData(final String name, final String version)
	{
		final IKnowledgeBaseData kb1 = new KnowledgeBaseData();
		kb1.setKbName(name);
		kb1.setKbVersion(version);
		return kb1;
	}

	@Test
	public void testQualifiedId()
	{
		final String qualifiedId = classUnderTest.retrieveQualifiedId(sessionId, configId);
		assertTrue(qualifiedId.contains(sessionId));
		assertTrue(qualifiedId.contains(configId));

	}

	@Test
	public void testGetFormattedDateNotNull()
	{
		assertNotNull(classUnderTest.getFormattedDate(new KBKeyImpl(P_CODE)));
	}

	@Test
	public void testGetFormattedDate()
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.set(2016, Calendar.JANUARY, 5);
		final KBKeyImpl kbKey = new KBKeyImpl(P_CODE, "kbName", "logSys", "kbVersion", calendar.getTime());

		final String formattedDate = classUnderTest.getFormattedDate(kbKey);

		assertEquals("20160105", formattedDate);

	}

	@Test
	public void testFindKBInList_null()
	{
		assertNull(classUnderTest.findKBInList(new KBKeyImpl(P_CODE), null));
	}

	@Test
	public void testFindKBInList_match()
	{
		final IKnowledgeBaseData matchedKB = classUnderTest.findKBInList(new KBKeyImpl(P_CODE, "kbName", null, "v2"), allKbs);
		assertNotNull(matchedKB);
		assertSame(allKbs[1], matchedKB);
	}

	@Test
	public void testFindKBInList_versionNotMatching()
	{
		assertNull(classUnderTest.findKBInList(new KBKeyImpl(P_CODE, "kbName", null, "xx"), allKbs));
	}

	@Test
	public void testFindKBInList_nameNotMatching()
	{
		assertNull(classUnderTest.findKBInList(new KBKeyImpl(P_CODE, "xx", null, "v1"), allKbs));
	}

	@Test
	public void isKbVersionExists_true()
	{
		assertTrue(classUnderTest.isKbVersionExists(new KBKeyImpl(P_CODE, "kbName", null, "v3")));
	}

	@Test
	public void isKbVersionExists_false()
	{
		assertFalse(classUnderTest.isKbVersionExists(new KBKeyImpl(P_CODE, "kbName", null, "v4")));
	}

	@Test
	public void isKbVersionValid_true()
	{
		assertTrue(classUnderTest.isKbVersionValid(new KBKeyImpl(P_CODE, "kbName", null, "v2")));
	}

	@Test
	public void isKbVersionValid_false()
	{
		assertFalse(classUnderTest.isKbVersionValid(new KBKeyImpl(P_CODE, "kbName", null, "v3")));
	}

	@Test
	public void isKbForDateExists_true()
	{
		assertTrue(classUnderTest.isKbForDateExists(P_CODE, new Date()));
	}

	@Test
	public void isKbForDateExists_false()
	{
		assertFalse(classUnderTest.isKbForDateExists("123", new Date()));
	}

	@Test
	public void extractKbKey()
	{
		final KBKey extractKbKey = classUnderTest.extractKbKey(P_CODE, extConfig);
		assertEquals(P_CODE, extractKbKey.getProductCode());
		assertEquals("CPQ_TABLE", extractKbKey.getKbName());
		assertEquals("v2", extractKbKey.getKbVersion());
	}

	@Test
	public void testUpdateProductCode()
	{
		final KBKey oldKey = configModel.getKbKey();
		classUnderTest.updateProductCode(configModel, "VARIANTCODE");
		assertEquals(oldKey.getDate(), configModel.getKbKey().getDate());
		assertEquals(oldKey.getKbLogsys(), configModel.getKbKey().getKbLogsys());
		assertEquals(oldKey.getKbVersion(), configModel.getKbKey().getKbVersion());
		assertEquals(oldKey.getKbName(), configModel.getKbKey().getKbName());
		assertEquals("VARIANTCODE", configModel.getKbKey().getProductCode());
	}


	private final static String extConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"T\" "
			+ "CONSISTENT=\"T\" KBBUILD=\"1\" KBNAME=\"CPQ_TABLE\" KBPROFILE=\"CPQ_TABLE\" KBVERSION=\"v2\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\" \" ROOT_NR=\"1\" "
			+ "SCEVERSION=\"SCE 5.0\"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"T\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"CPQ_TABLE\" "
			+ "OBJ_TXT=\"CPQ Table\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"CPQ_TABLE_COLOR\" CHARC_TXT=\"Table Color\" VALUE=\"WHITE\" "
			+ "VALUE_TXT=\"White\"/><CSTIC AUTHOR=\"8\" CHARC=\"CPQ_TABLE_HEIGHT\" CHARC_TXT=\"Table Height\" VALUE=\"70\" VALUE_TXT=\"70 cm\"/></CSTICS></INST><PARTS/>"
			+ "<NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";


}

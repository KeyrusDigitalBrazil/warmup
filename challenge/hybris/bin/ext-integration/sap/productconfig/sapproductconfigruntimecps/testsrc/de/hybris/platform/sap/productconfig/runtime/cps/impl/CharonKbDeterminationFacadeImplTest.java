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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.KnowledgeBaseHeadersCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.client.KbDeterminationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKnowledgebaseKey;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CommerceExternalConfigurationStrategy;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.impl.CommerceExternalConfigurationStrategyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.exp.HttpException;


@SuppressWarnings("javadoc")
@UnitTest
public class CharonKbDeterminationFacadeImplTest
{
	private static final String KB_LOGSYS = "LOGSYS";
	private static final String KB_VERSION = "1.0";
	private static final String P_CODE = "PRODUCT";
	private static final Integer KBID = Integer.valueOf(234);
	private static final Date TODAY = new Date();
	private static final String KB_NAME = "KBNAME";
	private static final String EXT_CFG_TEMPLATE = "{\"externalConfiguration\":{\"kbId\": \"234\", \"kbKey\": {\"logsys\": \"%s\", \"name\": \"%s\", \"version\": \"%s\"  }},\"unitCodes\":{\"PCE\":\"PCE\"}}";

	private final CPSMasterDataKnowledgebaseKey kbKey = new CPSMasterDataKnowledgebaseKey();
	private final CPSMasterDataKnowledgebaseKey key = new CPSMasterDataKnowledgebaseKey();
	private final CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy = new CommerceExternalConfigurationStrategyImpl();
	private final CharonKbDeterminationFacadeImpl classUnderTest = new CharonKbDeterminationFacadeImpl();

	private List<CPSMasterDataKBHeaderInfo> kbList;
	private CPSExternalConfiguration externalConfigStructured;
	private CPSMasterDataKBHeaderInfo kb;
	List<CPSMasterDataKBHeaderInfo> knowledgebases;

	@Mock
	private KbDeterminationClient client;
	@Mock
	private KnowledgeBaseHeadersCacheAccessService knowledgeBasesCacheAccessService;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setRequestErrorHandler(new RequestErrorHandlerImpl());
		classUnderTest.setKnowledgeBasesCacheAccessService(knowledgeBasesCacheAccessService);
		kbList = new ArrayList<>();
		kb = new CPSMasterDataKBHeaderInfo();
		kb.setKey(key);
		kb.setId(KBID);
		kb.setValidFromDate("19990101");
		key.setLogsys(KB_LOGSYS);
		key.setName(KB_NAME);
		key.setVersion(KB_VERSION);

		kbList.add(kb);
		Mockito.when(knowledgeBasesCacheAccessService.getKnowledgeBases(P_CODE)).thenReturn(kbList);
		externalConfigStructured = new CPSExternalConfiguration();
		externalConfigStructured.setKbKey(kbKey);
		kbKey.setLogsys(KB_LOGSYS);
		kbKey.setName(KB_NAME);
		kbKey.setVersion(KB_VERSION);
		classUnderTest.setCommerceExternalConfigurationStrategy(commerceExternalConfigurationStrategy);

		knowledgebases = new ArrayList();
		knowledgebases.add(kb);
		given(knowledgeBasesCacheAccessService.getKnowledgeBases(P_CODE)).willReturn(knowledgebases);
	}


	@Test
	public void testObjectMapper()
	{
		assertNotNull(classUnderTest.getObjectMapper());
		assertSame(classUnderTest.getObjectMapper(), classUnderTest.getObjectMapper());
	}

	@Test
	public void testParseKBKeyFromExtConfig()
	{
		final String extCfg = String.format(EXT_CFG_TEMPLATE, KB_LOGSYS, KB_NAME, KB_VERSION);
		final KBKey kbKey = classUnderTest.parseKBKeyFromExtConfig(P_CODE, extCfg);
		assertNotNull(kbKey);
		assertEquals(P_CODE, kbKey.getProductCode());
		assertEquals(KB_NAME, kbKey.getKbName());
		assertEquals(KB_VERSION, kbKey.getKbVersion());
		assertEquals(KB_LOGSYS, kbKey.getKbLogsys());
	}

	@Test(expected = IllegalStateException.class)
	public void testParseKBKeyFromExtConfigInvalidFormat()
	{
		final KBKey kbKey = classUnderTest.parseKBKeyFromExtConfig(P_CODE, "XXX");
	}

	@Test
	public void testHasKbForExtConfig()
	{
		final String extCfg = String.format(EXT_CFG_TEMPLATE, KB_LOGSYS, KB_NAME, KB_VERSION);
		assertTrue(classUnderTest.hasKbForExtConfig(P_CODE, extCfg));
	}

	@Test
	public void testHasKbForKeyMatching()
	{
		assertTrue(classUnderTest.hasKBForKey(new KBKeyImpl(P_CODE, KB_NAME, KB_LOGSYS, KB_VERSION)));
	}

	@Test
	public void testHasKbForKeyVersionNotMatching()
	{
		assertFalse(classUnderTest.hasKBForKey(new KBKeyImpl(P_CODE, KB_NAME, KB_LOGSYS, "xx")));
	}

	@Test
	public void testHasKbForKeyLogSysNotMatching()
	{
		assertFalse(classUnderTest.hasKBForKey(new KBKeyImpl(P_CODE, KB_NAME, "xxx", KB_VERSION)));
	}

	@Test
	public void testHasKbForKeyNameNotMatching()
	{
		assertFalse(classUnderTest.hasKBForKey(new KBKeyImpl(P_CODE, "xxx", KB_LOGSYS, KB_VERSION)));
	}

	@Test
	public void testResultIdAvailable()
	{
		final List<CPSMasterDataKBHeaderInfo> knowledgebases = kbList;
		if (knowledgebases.isEmpty())
		{
			throw new IllegalStateException("No KB found for product and date: " + P_CODE + " / " + TODAY);
		}
		final Integer readKbId = knowledgebases.get(0).getId();
		assertEquals(KBID, readKbId);

	}

	@Test(expected = IllegalStateException.class)
	public void testResultIdAvailableEmptyList()
	{
		final List<CPSMasterDataKBHeaderInfo> knowledgebases = Collections.emptyList();
		if (knowledgebases.isEmpty())
		{
			throw new IllegalStateException("No KB found for product and date: " + P_CODE + " / " + TODAY);
		}
		knowledgebases.get(0).getId();
	}


	@Test
	public void testExtConfigurationStrategy()
	{
		assertEquals(commerceExternalConfigurationStrategy, classUnderTest.getCommerceExternalConfigurationStrategy());
	}

	@Test
	public void testGetCurrentKbIdForProductPastDate()
	{
		assertNotNull(classUnderTest.getCurrentKbIdForProduct(P_CODE));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetCurrentKbIdForProductFutureDate()
	{
		kb.setValidFromDate("20990101");
		classUnderTest.getCurrentKbIdForProduct(P_CODE);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetCurrentKbIdForProductInvalidDate()
	{
		kb.setValidFromDate("xxxxx");
		classUnderTest.getCurrentKbIdForProduct(P_CODE);
	}

	@Test
	public void tesHasKbForDateHttpException()
	{
		classUnderTest.setRequestErrorHandler(Mockito.mock(RequestErrorHandler.class));
		given(knowledgeBasesCacheAccessService.getKnowledgeBases(P_CODE)).willThrow(new HttpException(500, "dummy"));
		assertFalse(classUnderTest.hasKbForDate(P_CODE, TODAY));
	}

	@Test(expected = IllegalStateException.class)
	public void tesHasKbForDateHttpExceptionWithDefaultErrorHandler()
	{
		given(knowledgeBasesCacheAccessService.getKnowledgeBases(P_CODE)).willThrow(new HttpException(500, "dummy"));
		classUnderTest.hasKbForDate(P_CODE, TODAY);
	}

	@Test
	public void testHasKbForDateFalse()
	{
		kb.setValidFromDate("20990101");
		assertFalse(classUnderTest.hasKbForDate(P_CODE, TODAY));
	}

	@Test
	public void testHasKbForDateTrue()
	{
		assertTrue(classUnderTest.hasKbForDate(P_CODE, TODAY));
	}


	@Test
	public void testHasValidKBForKey()
	{
		assertTrue(classUnderTest.hasValidKBForKey(new KBKeyImpl(P_CODE, KB_NAME, KB_LOGSYS, KB_VERSION)));
	}

	@Test
	public void testHasValidKBForKeyFalse()
	{
		kb.setValidFromDate("20990101");
		assertFalse(classUnderTest.hasValidKBForKey(new KBKeyImpl(P_CODE, KB_NAME, KB_LOGSYS, KB_VERSION)));
	}

}

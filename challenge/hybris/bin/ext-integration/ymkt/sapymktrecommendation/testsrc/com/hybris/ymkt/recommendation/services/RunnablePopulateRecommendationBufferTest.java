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
package com.hybris.ymkt.recommendation.services;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.SecureRandom;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.LeadingObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.Scenario;
import com.hybris.ymkt.recommendationbuffer.dao.RecommendationBufferDao;
import com.hybris.ymkt.recommendationbuffer.service.impl.DefaultRecommendationBufferService;

@ManualTest
public class RunnablePopulateRecommendationBufferTest extends ServicelayerTransactionalTest
{
	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Resource
	private DefaultRecommendationBufferService recommendationBufferService;

	@Resource
	private ModelService modelService;

	@Resource
	private RecommendationBufferDao recommendationBufferDao;

	@Resource(name = "ODataService_PROD_RECO_RUNTIME_SRV")
	private ODataService oDataService;

	private static final String PROD_RECO_RUNTIME_SRV = "/sap/opu/odata/sap/PROD_RECO_RUNTIME_SRV/";
	private static final String LEADING_OBJ_TYPE = "SAP_HYBRIS_PRODUCT";
	private static final String SCENARIO_ID = "YUE_SCENARIO_ID";
	private static final String HASH_ID = "65E6FB3DDD2CC15E87022676955B7963";

	public static void disableCertificates() throws Exception
	{
		final TrustManager[] trustAllCerts =
		{ (TrustManager) Proxy.getProxyClass(X509TrustManager.class.getClassLoader(), X509TrustManager.class)
				.getConstructor(InvocationHandler.class).newInstance((InvocationHandler) (o, m, args) -> null) };

		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	@Before
	public void setUp() throws Exception
	{
		disableCertificates();

		oDataService.setRootUrl(
				configurationService.getConfiguration().getString("sapymktcommon.odata.url.root") + PROD_RECO_RUNTIME_SRV);
		oDataService.setUser(configurationService.getConfiguration().getString("sapymktcommon.odata.url.user"));
		oDataService.setPassword(configurationService.getConfiguration().getString("sapymktcommon.odata.url.password"));
		oDataService.setSapClient(configurationService.getConfiguration().getString("sapymktcommon.odata.url.sap-client"));

		recommendationBufferService.setExpiryOffset(34);
		recommendationBufferService.setEnableRecommendationBuffer(true);
		recommendationBufferService.setRecommendationBufferDao(recommendationBufferDao);
		recommendationBufferService.setModelService(modelService);
	}

	@After
	public void tearDown()
	{

	}

	@Test
	public void testFillBuffer_ManyLeadingItems() throws Throwable
	{
		//Send 2 leading items. Expect 4 recommendations in buffer
		//One for all leading items, one per single leading item and one with empty leading items
		final RecommendationScenario recoScenario = new RecommendationScenario("6de4ae57e795a737", "COOKIE_ID");
		recoScenario.getScenarios().add(new Scenario(SCENARIO_ID));
		recoScenario.getScenarios().get(0).getLeadingObjects().add(new LeadingObject(LEADING_OBJ_TYPE, "1934793"));
		recoScenario.getScenarios().get(0).getLeadingObjects().add(new LeadingObject(LEADING_OBJ_TYPE, "1992693"));

		final RunnablePopulateRecommendationBuffer rb = new RunnablePopulateRecommendationBuffer();
		rb.setoDataService(oDataService);
		rb.setRecommendationBufferService(recommendationBufferService);
		rb.setRecommendationScenario(recoScenario);

		rb.run();

		Assert.assertEquals(1, recommendationBufferDao.findRecommendation(SCENARIO_ID, HASH_ID, "1934793,1992693").size());
		Assert.assertEquals(1, recommendationBufferDao.findRecommendation(SCENARIO_ID, HASH_ID, "1934793").size());
		Assert.assertEquals(1, recommendationBufferDao.findRecommendation(SCENARIO_ID, HASH_ID, "1992693").size());
		Assert.assertEquals(1, recommendationBufferDao.findRecommendation(SCENARIO_ID, HASH_ID, "").size());
	}

	@Test
	public void testFillBuffer_SingleLeadingItem() throws Throwable
	{
		//Send 1 leading item. Expect 2 recommendations in buffer
		//One for single leading item and one with empty leading items
		final RecommendationScenario recoScenario = new RecommendationScenario("6de4ae57e795a737", "COOKIE_ID");
		recoScenario.getScenarios().add(new Scenario(SCENARIO_ID));
		recoScenario.getScenarios().get(0).getLeadingObjects().add(new LeadingObject(LEADING_OBJ_TYPE, "1934793"));

		final RunnablePopulateRecommendationBuffer rb = new RunnablePopulateRecommendationBuffer();
		rb.setoDataService(oDataService);
		rb.setRecommendationBufferService(recommendationBufferService);
		rb.setRecommendationScenario(recoScenario);

		rb.run();

		Assert.assertEquals(1, recommendationBufferDao.findRecommendation(SCENARIO_ID, HASH_ID, "1934793").size());
		Assert.assertEquals(1, recommendationBufferDao.findRecommendation(SCENARIO_ID, HASH_ID, "").size());
	}

	@Test
	public void testFillBuffer_NoLeadingItems() throws Throwable
	{
		//Send 0 leading items. Expect 1 recommendation in buffer
		final RecommendationScenario recoScenario = new RecommendationScenario("6de4ae57e795a737", "COOKIE_ID");
		recoScenario.getScenarios().add(new Scenario(SCENARIO_ID));

		final RunnablePopulateRecommendationBuffer rb = new RunnablePopulateRecommendationBuffer();
		rb.setoDataService(oDataService);
		rb.setRecommendationBufferService(recommendationBufferService);
		rb.setRecommendationScenario(recoScenario);

		rb.run();

		Assert.assertEquals(1, recommendationBufferDao.findRecommendation(SCENARIO_ID, HASH_ID, "").size());
	}

	@Test
	public void testFillBuffer_NoUser() throws Throwable
	{
		//Request recommendation with no user. Expect one recommendation with a type mapping
		final RecommendationScenario recoScenario = new RecommendationScenario("", "ANONYMOUS");
		recoScenario.getScenarios().add(new Scenario(SCENARIO_ID));

		final RunnablePopulateRecommendationBuffer rb = new RunnablePopulateRecommendationBuffer();
		rb.setoDataService(oDataService);
		rb.setRecommendationBufferService(recommendationBufferService);
		rb.setRecommendationScenario(recoScenario);

		rb.run();

		Assert.assertEquals(1, recommendationBufferDao.findRecommendation(SCENARIO_ID, HASH_ID, "").size());
		Assert.assertEquals(1, recommendationBufferDao.findRecoTypeMapping("G", SCENARIO_ID).size());
	}

	@Test
	public void testFillBuffer_LoggedInUser() throws Throwable
	{
		//Request recommendation with logged in user. Expect one recommendation with one user mapping
		final RecommendationScenario recoScenario = new RecommendationScenario("bobby.customer@hybris.com", "SAP_HYBRIS_CONSUMER");
		recoScenario.getScenarios().add(new Scenario(SCENARIO_ID));

		final RunnablePopulateRecommendationBuffer rb = new RunnablePopulateRecommendationBuffer();
		rb.setoDataService(oDataService);
		rb.setRecommendationBufferService(recommendationBufferService);
		rb.setRecommendationScenario(recoScenario);

		rb.run();

		Assert.assertEquals(1, recommendationBufferDao.findRecommendation(SCENARIO_ID, HASH_ID, "").size());
		Assert.assertEquals(1, recommendationBufferDao.findRecommendationMapping("bobby.customer@hybris.com", SCENARIO_ID).size());
	}
}

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

import static org.junit.Assert.assertNotNull;

import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.LeadingObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.ResultObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.Scenario;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.ScenarioHash;
import com.hybris.ymkt.recommendationbuffer.service.impl.DefaultRecommendationBufferService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.testframework.Assert;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * Test the runnable by checking the number of recommendations returned and saved
 */

@IntegrationTest
public class RunnablePopulateRecommendationBufferIntegrationTest extends ServicelayerTransactionalTest
{
	@Mock
	private ODataService oDataService;
	
	@Resource
	private DefaultRecommendationBufferService recommendationBufferService;
	
	@Spy
	private RunnablePopulateRecommendationBuffer rb = new RunnablePopulateRecommendationBuffer();
	
	private static final String LEADING_OBJ_TYPE = "SAP_HYBRIS_PRODUCT";
	private static final String CONSUMER_TYPE = "SAP_HYBRIS_CONSUMER";
	private static final String CUSTOMER_ID = "bobby.customer@hybris.com";
	private static final String SCENARIO_ID = "YUE_SCENARIO_ID";
	private static final String HASH_ID = "65E6FB3DDD2CC15E87022676955B7963";
	
	final List<Scenario> scenarios = Arrays.asList(new Scenario(SCENARIO_ID));
	final List<ScenarioHash> scenarioHashes = Arrays.asList(new ScenarioHash(SCENARIO_ID));
	final List<ResultObject> recoList1 = Arrays.asList(new ResultObject("111111"));
	final List<ResultObject> recoList2 = Arrays.asList(new ResultObject("222222"));
	final List<ResultObject> recoList3 = Arrays.asList(new ResultObject("333333"));
	final List<ResultObject> recoList4 = Arrays.asList(new ResultObject("444444"));
	
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		
		//Setup runnable
		rb.setoDataService(oDataService);
		rb.setRecommendationBufferService(recommendationBufferService);
		
		//Hashes to be returned
		scenarioHashes.get(0).setHashId(HASH_ID);
		scenarioHashes.get(0).setResultScope("P");
		scenarioHashes.get(0).setExpiresOn(new Date());
	}

	@After
	public void tearDown()
	{
		rb.getRecommendationList().clear();
	}

	@Test
	public void testFillBuffer_NoLeadingItem() throws IOException
	{
		//Test get recommendation with zero leading items
		//Expect one recommendation returned, one recommendation saved

		final RecommendationScenario recommendationScenario = Mockito.spy(new RecommendationScenario(CUSTOMER_ID, CONSUMER_TYPE));
		rb.setRecommendationScenario(recommendationScenario);
		
		//Make sure there are no leading items
		scenarios.get(0).getLeadingObjects().clear();
		
		//Simulate call to getRecommendationFromBackend()
		//Do nothing for executeRecommendationScenario()
		Mockito.doNothing().when(rb).executeRecommendationScenario(recommendationScenario);
		
		//Return predefined results for updateRecommendationBuffer() 
		Mockito.when(recommendationScenario.getScenarios()).thenReturn(scenarios);
		Mockito.when(recommendationScenario.getScenarioHashes()).thenReturn(scenarioHashes);
		Mockito.when(recommendationScenario.getResultObjects()).thenReturn(recoList1);
		
		//Call to get recommendations
		rb.run();
		
		Assert.assertEquals(1, rb.getRecommendationList().size());
		assertNotNull(recommendationBufferService.getPersonalizedRecommendation(CUSTOMER_ID, SCENARIO_ID, ""));
	}

	@Test
	public void testFillBuffer_NoLeadingItem_NoUser() throws IOException
	{
		//Test get recommendation with zero leading items
		//Expect one recommendation returned, one recommendation saved

		final RecommendationScenario recommendationScenario = Mockito.spy(new RecommendationScenario("", "ANONYMOUS"));
		rb.setRecommendationScenario(recommendationScenario);
		
		//Make sure there are no leading items
		scenarios.get(0).getLeadingObjects().clear();
		
		//Set reco type for anonymous user
		scenarioHashes.get(0).setResultScope("G");
		
		//Simulate call to getRecommendationFromBackend()
		//Do nothing for executeRecommendationScenario()
		Mockito.doNothing().when(rb).executeRecommendationScenario(recommendationScenario);
		
		//Return predefined results for updateRecommendationBuffer() 
		Mockito.when(recommendationScenario.getScenarios()).thenReturn(scenarios);
		Mockito.when(recommendationScenario.getScenarioHashes()).thenReturn(scenarioHashes);
		Mockito.when(recommendationScenario.getResultObjects()).thenReturn(recoList4);
		
		//Call to get recommendations
		rb.run();
		
		Assert.assertEquals(1, rb.getRecommendationList().size());
		assertNotNull(recommendationBufferService.getGenericRecommendation(SCENARIO_ID, ""));
	}
	
	@Test
	public void testFillBuffer_OneLeadingItem() throws IOException
	{
		//Test get recommendation with one leading item 
		//Expect one recommendation returned, two recommendations saved
		
		final RecommendationScenario recommendationScenario = Mockito.spy(new RecommendationScenario(CUSTOMER_ID, CONSUMER_TYPE));
		rb.setRecommendationScenario(recommendationScenario);
		
		//Add one leading item
		scenarios.get(0).getLeadingObjects().add(new LeadingObject(LEADING_OBJ_TYPE, "123456"));
		
		//Simulate call to getRecommendationFromBackend()
		//Do nothing for executeRecommendationScenario()
		Mockito.doNothing().when(rb).executeRecommendationScenario(recommendationScenario);
		
		//Return predefined results for updateRecommendationBuffer() 
		Mockito.when(recommendationScenario.getScenarios()).thenReturn(scenarios);
		Mockito.when(recommendationScenario.getScenarioHashes()).thenReturn(scenarioHashes);
		Mockito.when(recommendationScenario.getResultObjects()).thenReturn(recoList1).thenReturn(recoList2);
		
		//Call to get recommendations
		rb.run();
		
		Assert.assertEquals(1, rb.getRecommendationList().size());
		assertNotNull(recommendationBufferService.getPersonalizedRecommendation(CUSTOMER_ID, SCENARIO_ID, ""));
		assertNotNull(recommendationBufferService.getPersonalizedRecommendation(CUSTOMER_ID, SCENARIO_ID, "123456"));
	}
	
	@Test
	public void testFillBuffer_TwoLeadingItems() throws IOException
	{
		//Test get recommendation with two leading item 
		//Expect one recommendation returned, four recommendations saved 
		//One for all leading items, one for each each leading item, and one with no leading items
		
		final RecommendationScenario recommendationScenario = Mockito.spy(new RecommendationScenario(CUSTOMER_ID, CONSUMER_TYPE));
		rb.setRecommendationScenario(recommendationScenario);
		
		//Add two leading items
		scenarios.get(0).getLeadingObjects().add(new LeadingObject(LEADING_OBJ_TYPE, "123456"));
		scenarios.get(0).getLeadingObjects().add(new LeadingObject(LEADING_OBJ_TYPE, "678901"));
		
		//Simulate call to getRecommendationFromBackend()
		//Do nothing for executeRecommendationScenario()
		Mockito.doNothing().when(rb).executeRecommendationScenario(recommendationScenario);
		
		//Return predefined results for updateRecommendationBuffer() 
		Mockito.when(recommendationScenario.getScenarios()).thenReturn(scenarios);
		Mockito.when(recommendationScenario.getScenarioHashes()).thenReturn(scenarioHashes);
		Mockito.when(recommendationScenario.getResultObjects()) //
			.thenReturn(recoList1) //
			.thenReturn(recoList2) //
			.thenReturn(recoList3) //
			.thenReturn(recoList4);
		
		//Call to get recommendations
		rb.run();
		
		Assert.assertEquals(1, rb.getRecommendationList().size());
		assertNotNull(recommendationBufferService.getPersonalizedRecommendation(CUSTOMER_ID, SCENARIO_ID, ""));
		assertNotNull(recommendationBufferService.getPersonalizedRecommendation(CUSTOMER_ID, SCENARIO_ID, "123456"));
		assertNotNull(recommendationBufferService.getPersonalizedRecommendation(CUSTOMER_ID, SCENARIO_ID, "678901"));
		assertNotNull(recommendationBufferService.getPersonalizedRecommendation(CUSTOMER_ID, SCENARIO_ID, "123456,678901"));
	}
}

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

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hybris.ymkt.recommendationbuffer.model.SAPRecoImpressionAggrModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoImpressionModel;


@UnitTest
public class ImpressionServiceTest
{
	static ImpressionService impressionService = new ImpressionService();

	final SAPRecoImpressionModel impression1 = new SAPRecoImpressionModel();
	final SAPRecoImpressionModel impression2 = new SAPRecoImpressionModel();
	final SAPRecoImpressionModel impression3 = new SAPRecoImpressionModel();
	final SAPRecoImpressionModel impression4 = new SAPRecoImpressionModel();
	final SAPRecoImpressionModel impression5 = new SAPRecoImpressionModel();
	final SAPRecoImpressionModel impression6 = new SAPRecoImpressionModel();
	final SAPRecoImpressionModel impression7 = new SAPRecoImpressionModel();
	final SAPRecoImpressionModel impression8 = new SAPRecoImpressionModel();
	final SAPRecoImpressionModel impression9 = new SAPRecoImpressionModel();

	final long date1 = 1485435695000L; // 2017/01/26 13:01:35 GMT
	final long date2 = 1485435755000L; // 2017/01/26 13:02:00 GMT
	final long date3 = 1485435815000L; // 2017/01/26 13:03:00 GMT
	final long date4 = 1485436001000L; // 2017/01/26 13:06:41 GMT
	final long date5 = 1485436199000L; // 2017/01/26 13:09:59 GMT

	final long date6 = 1482506537000L; // 2016/12/23 15:22:17 GMT
	final long date7 = 1485251635000L; // 2017/01/24 09:53:55 GMT
	final long date8 = 1485251636000L; // 2017/01/24 09:53:56 GMT
	final long date9 = 1485522300000L; // 2017/01/27 13:05:00 GMT

	final int ITEM_COUNT_01 = 1;
	final int ITEM_COUNT_02 = 2;

	final int IMPRESSION_COUNT_01 = 1;
	final int IMPRESSION_COUNT_02 = 2;

	final String SCENARIO_ID_01 = "AAAA";
	final String SCENARIO_ID_02 = "BBBB";

	@Before
	public void setUp() throws Exception
	{
		impression1.setScenarioId(SCENARIO_ID_01);
		impression1.setImpressionCount(IMPRESSION_COUNT_01);
		impression1.setItemCount(ITEM_COUNT_01);
		impression1.setTimeStamp(new Date(date1));

		impression2.setScenarioId(SCENARIO_ID_01);
		impression2.setImpressionCount(IMPRESSION_COUNT_01);
		impression2.setItemCount(ITEM_COUNT_01);
		impression2.setTimeStamp(new Date(date2));

		impression3.setScenarioId(SCENARIO_ID_01);
		impression3.setImpressionCount(IMPRESSION_COUNT_01);
		impression3.setItemCount(ITEM_COUNT_01);
		impression3.setTimeStamp(new Date(date3));

		impression4.setScenarioId(SCENARIO_ID_01);
		impression4.setImpressionCount(IMPRESSION_COUNT_01);
		impression4.setItemCount(ITEM_COUNT_01);
		impression4.setTimeStamp(new Date(date4));

		impression5.setScenarioId(SCENARIO_ID_02);
		impression5.setImpressionCount(IMPRESSION_COUNT_02);
		impression5.setItemCount(ITEM_COUNT_02);
		impression5.setTimeStamp(new Date(date5));

		impression6.setScenarioId(SCENARIO_ID_02);
		impression6.setImpressionCount(IMPRESSION_COUNT_02);
		impression6.setItemCount(ITEM_COUNT_02);
		impression6.setTimeStamp(new Date(date6));

		impression7.setScenarioId(SCENARIO_ID_02);
		impression7.setImpressionCount(IMPRESSION_COUNT_02);
		impression7.setItemCount(ITEM_COUNT_02);
		impression7.setTimeStamp(new Date(date7));

		impression8.setScenarioId(SCENARIO_ID_02);
		impression8.setImpressionCount(IMPRESSION_COUNT_02);
		impression8.setItemCount(ITEM_COUNT_02);
		impression8.setTimeStamp(new Date(date8));

		impression9.setScenarioId(SCENARIO_ID_02);
		impression9.setImpressionCount(IMPRESSION_COUNT_02);
		impression9.setItemCount(ITEM_COUNT_02);
		impression9.setTimeStamp(new Date(date9));
	}

	@Test
	public void testAggregateImpressionsWith2minWindow()
	{
		final List<SAPRecoImpressionModel> impressionsList = Arrays.asList(impression1, impression2, impression3, impression4,
				impression5);

		//Expected dates for 2mins window
		final long expectedDate1 = 1485435660000L; // 2017/01/26 13:01:00 GMT
		final long expectedDate2 = 1485435780000L; // 2017/01/26 13:03:00 GMT
		final long expectedDate3 = 1485436020000L; // 2017/01/26 13:07:00 GMT
		final long expectedDate4 = 1485436140000L; // 2017/01/26 13:09:00 GMT

		//aggregate
		impressionService.setAggregationTimeWindow(120000L); //2mins
		List<SAPRecoImpressionAggrModel> aggregatedImpressionsList = impressionService
				.aggregateImpressionsByScenario(impressionsList);

		assertEquals(4, aggregatedImpressionsList.size());

		//Expected: impression1 + impression2
		assertEquals(SCENARIO_ID_02, aggregatedImpressionsList.get(0).getScenarioId());
		assertEquals(2, aggregatedImpressionsList.get(0).getImpressionCount().intValue());
		assertEquals(2, aggregatedImpressionsList.get(0).getItemCount().intValue());
		assertEquals(expectedDate4, aggregatedImpressionsList.get(0).getTimeStamp().getTime());

		//Expected: impression3
		assertEquals(SCENARIO_ID_01, aggregatedImpressionsList.get(1).getScenarioId());
		assertEquals(1, aggregatedImpressionsList.get(1).getImpressionCount().intValue());
		assertEquals(1, aggregatedImpressionsList.get(1).getItemCount().intValue());
		assertEquals(expectedDate3, aggregatedImpressionsList.get(1).getTimeStamp().getTime());

		//Expected: impression4
		assertEquals(SCENARIO_ID_01, aggregatedImpressionsList.get(2).getScenarioId());
		assertEquals(2, aggregatedImpressionsList.get(2).getImpressionCount().intValue());
		assertEquals(2, aggregatedImpressionsList.get(2).getItemCount().intValue());
		assertEquals(expectedDate2, aggregatedImpressionsList.get(2).getTimeStamp().getTime());

		//Expected: impression5
		assertEquals(SCENARIO_ID_01, aggregatedImpressionsList.get(3).getScenarioId());
		assertEquals(1, aggregatedImpressionsList.get(3).getImpressionCount().intValue());
		assertEquals(1, aggregatedImpressionsList.get(3).getItemCount().intValue());
		assertEquals(expectedDate1, aggregatedImpressionsList.get(3).getTimeStamp().getTime());
	}

	@Test
	public void testAggregateImpressionsWith5minWindow()
	{
		final List<SAPRecoImpressionModel> impressionsList = Arrays.asList(impression1, impression2, impression3, impression4,
				impression5);

		//Expected dates for 5mins window
		final long expectedDate1 = 1485435750000L; // 2017/01/26 13:02:30 GMT
		final long expectedDate2 = 1485436050000L; // 2017/01/26 13:07:30 GMT

		//aggregate
		impressionService.setAggregationTimeWindow(300000L); //5mins
		List<SAPRecoImpressionAggrModel> aggregatedImpressionsList = impressionService
				.aggregateImpressionsByScenario(impressionsList);

		assertEquals(3, aggregatedImpressionsList.size());

		//Expected: impression4
		assertEquals(SCENARIO_ID_01, aggregatedImpressionsList.get(0).getScenarioId());
		assertEquals(1, aggregatedImpressionsList.get(0).getImpressionCount().intValue());
		assertEquals(1, aggregatedImpressionsList.get(0).getItemCount().intValue());
		assertEquals(expectedDate2, aggregatedImpressionsList.get(0).getTimeStamp().getTime());

		//Expected: impression5
		assertEquals(SCENARIO_ID_02, aggregatedImpressionsList.get(1).getScenarioId());
		assertEquals(2, aggregatedImpressionsList.get(1).getImpressionCount().intValue());
		assertEquals(2, aggregatedImpressionsList.get(1).getItemCount().intValue());
		assertEquals(expectedDate2, aggregatedImpressionsList.get(1).getTimeStamp().getTime());

		//Expected: impression1 + impression2 + impression3
		assertEquals(SCENARIO_ID_01, aggregatedImpressionsList.get(2).getScenarioId());
		assertEquals(3, aggregatedImpressionsList.get(2).getImpressionCount().intValue());
		assertEquals(3, aggregatedImpressionsList.get(2).getItemCount().intValue());
		assertEquals(expectedDate1, aggregatedImpressionsList.get(2).getTimeStamp().getTime());


	}

	@Test
	public void testAggregateImpressionsWith15minWindow()
	{
		final List<SAPRecoImpressionModel> impressionsList = Arrays.asList(impression1, impression2, impression3, impression4,
				impression5);

		//Expected dates for 15mins window
		final long expectedDate1 = 1485436050000L; // 2017/01/26 13:07:30 GMT

		//aggregate
		impressionService.setAggregationTimeWindow(900000L); ////15mins
		List<SAPRecoImpressionAggrModel> aggregatedImpressionsList = impressionService
				.aggregateImpressionsByScenario(impressionsList);

		assertEquals(2, aggregatedImpressionsList.size());

		//Expected: impression1 + impression2 + impression3 + impression4
		assertEquals(SCENARIO_ID_01, aggregatedImpressionsList.get(0).getScenarioId());
		assertEquals(4, aggregatedImpressionsList.get(0).getImpressionCount().intValue());
		assertEquals(4, aggregatedImpressionsList.get(0).getItemCount().intValue());
		assertEquals(expectedDate1, aggregatedImpressionsList.get(0).getTimeStamp().getTime());

		//Expected: impression5
		assertEquals(SCENARIO_ID_02, aggregatedImpressionsList.get(1).getScenarioId());
		assertEquals(2, aggregatedImpressionsList.get(1).getImpressionCount().intValue());
		assertEquals(2, aggregatedImpressionsList.get(1).getItemCount().intValue());
		assertEquals(expectedDate1, aggregatedImpressionsList.get(1).getTimeStamp().getTime());
	}

	@Test
	public void testAggregateImpressionsWith5minWindowMultiDay()
	{
		final List<SAPRecoImpressionModel> impressionsList = Arrays.asList(impression1, impression2, impression3, impression4,
				impression5, impression6, impression7, impression8, impression9);

		//Expected dates for 5mins window
		final long expectedDate1 = 1485435750000L; // 2017/01/26 13:02:30 GMT
		final long expectedDate2 = 1485436050000L; // 2017/01/26 13:07:30 GMT
		final long expectedDate3 = 1482506550000L; // 2017/01/23 15:22:30 GMT
		final long expectedDate4 = 1485251550000L; // 2017/01/24 09:52:30 GMT
		final long expectedDate5 = 1485522450000L; // 2017/01/27 13:07:30 GMT

		//aggregate
		impressionService.setAggregationTimeWindow(300000L); ////5mins
		List<SAPRecoImpressionAggrModel> aggregatedImpressionsList = impressionService
				.aggregateImpressionsByScenario(impressionsList);

		assertEquals(6, aggregatedImpressionsList.size());

		//Expected: impression9
		assertEquals(SCENARIO_ID_02, aggregatedImpressionsList.get(5).getScenarioId());
		assertEquals(2, aggregatedImpressionsList.get(5).getImpressionCount().intValue());
		assertEquals(2, aggregatedImpressionsList.get(5).getItemCount().intValue());
		assertEquals(expectedDate5, aggregatedImpressionsList.get(5).getTimeStamp().getTime());

		//Expected: impression1 + impression2
		assertEquals(SCENARIO_ID_01, aggregatedImpressionsList.get(4).getScenarioId());
		assertEquals(3, aggregatedImpressionsList.get(4).getImpressionCount().intValue());
		assertEquals(3, aggregatedImpressionsList.get(4).getItemCount().intValue());
		assertEquals(expectedDate1, aggregatedImpressionsList.get(4).getTimeStamp().getTime());

		//Expected: impression5
		assertEquals(SCENARIO_ID_02, aggregatedImpressionsList.get(3).getScenarioId());
		assertEquals(2, aggregatedImpressionsList.get(3).getImpressionCount().intValue());
		assertEquals(2, aggregatedImpressionsList.get(3).getItemCount().intValue());
		assertEquals(expectedDate2, aggregatedImpressionsList.get(3).getTimeStamp().getTime());

		//Expected: impression3 + impression4
		assertEquals(SCENARIO_ID_01, aggregatedImpressionsList.get(2).getScenarioId());
		assertEquals(1, aggregatedImpressionsList.get(2).getImpressionCount().intValue());
		assertEquals(1, aggregatedImpressionsList.get(2).getItemCount().intValue());
		assertEquals(expectedDate2, aggregatedImpressionsList.get(2).getTimeStamp().getTime());

		//Expected: impression7 + impression8
		assertEquals(SCENARIO_ID_02, aggregatedImpressionsList.get(1).getScenarioId());
		assertEquals(4, aggregatedImpressionsList.get(1).getImpressionCount().intValue());
		assertEquals(4, aggregatedImpressionsList.get(1).getItemCount().intValue());
		assertEquals(expectedDate4, aggregatedImpressionsList.get(1).getTimeStamp().getTime());

		//Expected: impression6
		assertEquals(SCENARIO_ID_02, aggregatedImpressionsList.get(0).getScenarioId());
		assertEquals(2, aggregatedImpressionsList.get(0).getImpressionCount().intValue());
		assertEquals(2, aggregatedImpressionsList.get(0).getItemCount().intValue());
		assertEquals(expectedDate3, aggregatedImpressionsList.get(0).getTimeStamp().getTime());
	}

}

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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.hybris.ymkt.recommendation.dao.ImpressionContext;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoImpressionAggrModel;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoImpressionModel;


@IntegrationTest
public class ImpressionServiceIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private ImpressionService impressionService;

	private static final int IMPRESSION_COUNT = 2;
	private static final int ITEM_COUNT = 4;
	private static final int BATCH_SIZE = 2;

	private static final String SCENARIOID_01 = "SAP_TEST_SCENARIOID1";
	private static final String SCENARIOID_02 = "SAP_TEST_SCENARIOID2";

	private final long DATE1 = 1485453815000L; // 2017/01/26 01:03:35 PM
	private final long DATE2 = 1485453750000L; // 2017/01/26 01:02:30 PM
	private final long DATE3 = 1485453780000L; // 2017/01/26 01:03:00 PM
	private final long DATE4 = 1485453900000L; // 2017/01/26 01:05:00 PM
	private final long DATE5 = 1485531765000L; // 2017/01/27 10:42:45 AM
	//	private final long DATE6 = 1485453810000L; // 2017/01/27 01:03:30 PM

	List<SAPRecoImpressionModel> impressionsList;

	@Before
	public void setUp()
	{
		impressionService.setReadBatchSize(5000);

		final SAPRecoImpressionModel MODEL1 = new SAPRecoImpressionModel();
		final SAPRecoImpressionModel MODEL2 = new SAPRecoImpressionModel();
		final SAPRecoImpressionModel MODEL3 = new SAPRecoImpressionModel();
		final SAPRecoImpressionModel MODEL4 = new SAPRecoImpressionModel();
		final SAPRecoImpressionModel MODEL5 = new SAPRecoImpressionModel();

		MODEL1.setScenarioId(SCENARIOID_01);
		MODEL1.setImpressionCount(1);
		MODEL1.setItemCount(1);
		MODEL1.setTimeStamp(new Date(DATE1));

		MODEL2.setScenarioId(SCENARIOID_01);
		MODEL2.setImpressionCount(1);
		MODEL2.setItemCount(1);
		MODEL2.setTimeStamp(new Date(DATE2));

		MODEL3.setScenarioId(SCENARIOID_01);
		MODEL3.setImpressionCount(1);
		MODEL3.setItemCount(1);
		MODEL3.setTimeStamp(new Date(DATE3));

		MODEL4.setScenarioId(SCENARIOID_01);
		MODEL4.setImpressionCount(1);
		MODEL4.setItemCount(1);
		MODEL4.setTimeStamp(new Date(DATE4));

		MODEL5.setScenarioId(SCENARIOID_02);
		MODEL5.setImpressionCount(2);
		MODEL5.setItemCount(2);
		MODEL5.setTimeStamp(new Date(DATE5));

		impressionsList = Arrays.asList(MODEL1, MODEL2, MODEL3, MODEL4, MODEL5);
	}

	@Test
	public void testSaveImpression()
	{
		final ImpressionContext impressionContext = new ImpressionContext(SCENARIOID_01, IMPRESSION_COUNT, ITEM_COUNT,
				new Date(DATE1));
		impressionService.saveImpression(impressionContext);

		final List<SAPRecoImpressionModel> impressionResult = impressionService.recommendationBufferService
				.getImpressions(BATCH_SIZE);
		assertEquals(1, impressionResult.size());
		assertEquals(SCENARIOID_01, impressionResult.get(0).getScenarioId());
		assertEquals(IMPRESSION_COUNT, impressionResult.get(0).getImpressionCount().intValue());
		assertEquals(ITEM_COUNT, impressionResult.get(0).getItemCount().intValue());
		assertEquals(DATE1, impressionResult.get(0).getTimeStamp().getTime());
		modelService.remove(impressionResult.get(0).getPk());
	}

	@Test
	public void testSaveAggregatedImpression()
	{
		final SAPRecoImpressionAggrModel aggregatedImpression = modelService.create(SAPRecoImpressionAggrModel.class);
		aggregatedImpression.setScenarioId(SCENARIOID_01);
		aggregatedImpression.setImpressionCount(IMPRESSION_COUNT);
		aggregatedImpression.setItemCount(ITEM_COUNT);
		aggregatedImpression.setTimeStamp(new Date());
		modelService.save(aggregatedImpression);

		final List<SAPRecoImpressionAggrModel> impressionResult = impressionService.recommendationBufferService
				.getAggregatedImpressions(BATCH_SIZE);
		assertEquals(1, impressionResult.size());
		assertEquals(SCENARIOID_01, impressionResult.get(0).getScenarioId());
		assertEquals(IMPRESSION_COUNT, impressionResult.get(0).getImpressionCount().intValue());
		assertEquals(ITEM_COUNT, impressionResult.get(0).getItemCount().intValue());
		modelService.remove(impressionResult.get(0).getPk());
	}

	@Test
	public void testFindImpressionWithBatch5()
	{
		modelService.saveAll(impressionsList); //5 impressions
		List<Integer> results = findImpressionWithBatch(5);
		assertEquals(1, results.size());
		assertEquals(5, results.get(0).intValue());
	}

	@Test
	public void testFindImpressionWithBatch2()
	{
		modelService.saveAll(impressionsList); //5 impressions
		List<Integer> results = findImpressionWithBatch(2);
		assertEquals(3, results.size());
		assertEquals(2, results.get(0).intValue());
		assertEquals(2, results.get(1).intValue());
		assertEquals(1, results.get(2).intValue());
	}

	private List<Integer> findImpressionWithBatch(final int readBatchSize)
	{
		int impressionsListSize = 0;
		List<Integer> results = new ArrayList<>();

		do
		{
			List<SAPRecoImpressionModel> impressionResult = impressionService.recommendationBufferService
					.getImpressions(readBatchSize);

			if (impressionResult.isEmpty())
			{
				return results;
			}

			impressionsListSize = impressionResult.size();
			results.add(impressionsListSize);
			modelService.removeAll(impressionResult);
		}
		while (impressionsListSize == readBatchSize);

		return results;
	}

}



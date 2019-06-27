/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousing.sourcing.fitness.evaluation.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.data.sourcing.SourcingLocation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

@UnitTest
public class ScoreEvaluatorTest {
	private static Double FIVE = Double.valueOf(5D);
	
	private ScoreEvaluator evaluator;
	private SourcingLocation location;
	
	@Mock
	private WarehouseModel warehouse;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		evaluator = new ScoreEvaluator();
	}

	@Test
	public void shouldEvaluateScore() {
		when(warehouse.getScore()).thenReturn(FIVE);
		
		location = new SourcingLocation();
		location.setWarehouse(warehouse);

		final Double locEval = evaluator.evaluate(location);
		Assert.assertEquals(5D, locEval.doubleValue(), 0.01D);
	}

	@Test
	public void shouldReturnNaNIfLocationWarehouseIsNull() {
		location = new SourcingLocation();
		
		final Double locEval = evaluator.evaluate(location);
		Assert.assertEquals(Double.NaN, locEval.doubleValue(), 0.01D);
	}

	@Test
	public void shouldReturnNaNIfLocationScoreIsNull() {
		when(warehouse.getScore()).thenReturn(null);
		
		location = new SourcingLocation();
		location.setWarehouse(warehouse);

		final Double locEval = evaluator.evaluate(location);
		Assert.assertEquals(Double.NaN, locEval.doubleValue(), 0.01D);
	}
}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.yacceleratorordermanagement.actions.consignment;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.externalfulfillment.strategy.ConsignmentPreFulfillmentStrategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProcessConsignmentPreFulfillmentActionTest
{

	@InjectMocks
	private ProcessConsignmentPreFulfillmentAction action;

	@Mock
	private Map<String, List<ConsignmentPreFulfillmentStrategy>> preFulfillmentStrategyRegistry;
	@Mock
	private ModelService modelService;
	@Mock
	private BusinessProcessParameterModel strategyContextParam;
	@Mock
	private BusinessProcessParameterModel declineContextParam;

	private FirstConsignmentPreFulfillmentStrategy firstConsignmentPreFulfillmentStrategy;
	private SecondConsignmentPreFulfillmentStrategy secondConsignmentPreFulfillmentStrategy;
	private ThirdConsignmentPreFulfillmentStrategy thirdConsignmentPreFulfillmentStrategy;

	private ConsignmentModel consignment;
	private ConsignmentProcessModel consignmentProcess;
	private List<ConsignmentPreFulfillmentStrategy> consignmentPreFulfillmentStrategies;
	private Object fulfillmentSystemConfig;

	@Before
	public void setup()
	{
		firstConsignmentPreFulfillmentStrategy = new FirstConsignmentPreFulfillmentStrategy();
		secondConsignmentPreFulfillmentStrategy = new SecondConsignmentPreFulfillmentStrategy();
		thirdConsignmentPreFulfillmentStrategy = new ThirdConsignmentPreFulfillmentStrategy();
		consignmentPreFulfillmentStrategies = Arrays
				.asList(firstConsignmentPreFulfillmentStrategy, secondConsignmentPreFulfillmentStrategy);
		fulfillmentSystemConfig = new Object();
		consignmentProcess = new ConsignmentProcessModel();
		consignment = new ConsignmentModel();
		consignmentProcess.setConsignment(consignment);
		consignmentProcess.setContextParameters(Arrays.asList(strategyContextParam, declineContextParam));
		consignment.setFulfillmentSystemConfig(fulfillmentSystemConfig);

		when(preFulfillmentStrategyRegistry.get(Object.class.getSimpleName())).thenReturn(consignmentPreFulfillmentStrategies);
		when(strategyContextParam.getName()).thenReturn(action.LAST_EXECUTED_STRATEGY);
		when(declineContextParam.getName()).thenReturn("declineEntries");
		doNothing().when(modelService).save(any());
	}

	@Test
	public void shouldTransitionToOkWhenNoPreProcessingRequired()
	{
		//Given
		consignmentProcess.setContextParameters(Arrays.asList(declineContextParam));
		when(preFulfillmentStrategyRegistry.get(Object.class.getSimpleName())).thenReturn(Collections.emptyList());
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(ProcessConsignmentPreFulfillmentAction.Transition.OK.toString(), transition);
		assertEquals(1, consignmentProcess.getContextParameters().size());
	}

	@Test
	public void shouldTransitionToOKWhenNothingExecutedAndOnlyOneGoAheadPreFulfillmentStrategyToExecute()
	{
		//Given
		when(preFulfillmentStrategyRegistry.get(Object.class.getSimpleName()))
				.thenReturn(Arrays.asList(firstConsignmentPreFulfillmentStrategy));
		consignmentProcess.setContextParameters(Arrays.asList(declineContextParam));
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(ProcessConsignmentPreFulfillmentAction.Transition.OK.toString(), transition);
		assertEquals(2, consignmentProcess.getContextParameters().size());
		final BusinessProcessParameterModel strategyParam = consignmentProcess.getContextParameters().stream()
				.filter(param -> action.LAST_EXECUTED_STRATEGY.equals(param.getName())).findFirst().get();
		assertEquals(FirstConsignmentPreFulfillmentStrategy.class.getSimpleName(), strategyParam.getValue());
	}

	@Test
	public void shouldTransitionToWaitWhenNothingExecutedAndNoGoAheadPreFulfillmentStrategyToExecute()
	{
		//Given
		when(preFulfillmentStrategyRegistry.get(Object.class.getSimpleName()))
				.thenReturn(Arrays.asList(secondConsignmentPreFulfillmentStrategy));
		consignmentProcess.setContextParameters(Arrays.asList(declineContextParam));
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(ProcessConsignmentPreFulfillmentAction.Transition.WAIT.toString(), transition);
		assertEquals(2, consignmentProcess.getContextParameters().size());
		final BusinessProcessParameterModel strategyParam = consignmentProcess.getContextParameters().stream()
				.filter(param -> action.LAST_EXECUTED_STRATEGY.equals(param.getName())).findFirst().get();
		assertEquals(SecondConsignmentPreFulfillmentStrategy.class.getSimpleName(), strategyParam.getValue());
	}

	@Test
	public void shouldTransitionToOkWhenOneStrategyExecutedAndOnlyOneGoAheadPreFulfillmentStrategyToExecute()
	{
		//Given
		when(preFulfillmentStrategyRegistry.get(Object.class.getSimpleName()))
				.thenReturn(Arrays.asList(firstConsignmentPreFulfillmentStrategy, thirdConsignmentPreFulfillmentStrategy));
		when(strategyContextParam.getValue()).thenReturn(FirstConsignmentPreFulfillmentStrategy.class.getSimpleName());
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(ProcessConsignmentPreFulfillmentAction.Transition.OK.toString(), transition);
		assertEquals(2, consignmentProcess.getContextParameters().size());
		final BusinessProcessParameterModel strategyParam = consignmentProcess.getContextParameters().stream()
				.filter(param -> action.LAST_EXECUTED_STRATEGY.equals(param.getName())).findFirst().get();
		assertEquals(ThirdConsignmentPreFulfillmentStrategy.class.getSimpleName(), strategyParam.getValue());
	}

	@Test
	public void shouldTransitionToWaitWhenOneStrategyExecutedAndMixOfGoAheadAndNoGoAheadPreFulfillmentStrategyToExecute()
	{
		//Given
		when(preFulfillmentStrategyRegistry.get(Object.class.getSimpleName())).thenReturn(
				Arrays.asList(firstConsignmentPreFulfillmentStrategy, thirdConsignmentPreFulfillmentStrategy,
						secondConsignmentPreFulfillmentStrategy));
		when(strategyContextParam.getValue()).thenReturn(FirstConsignmentPreFulfillmentStrategy.class.getSimpleName());
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(ProcessConsignmentPreFulfillmentAction.Transition.WAIT.toString(), transition);
		assertEquals(2, consignmentProcess.getContextParameters().size());
		final BusinessProcessParameterModel strategyParam = consignmentProcess.getContextParameters().stream()
				.filter(param -> action.LAST_EXECUTED_STRATEGY.equals(param.getName())).findFirst().get();
		assertEquals(SecondConsignmentPreFulfillmentStrategy.class.getSimpleName(), strategyParam.getValue());
	}

	@Test
	public void shouldTransitionToWaitWhenAllButLastOneNoGoAheadPreFulfillmentStrategyToExecute()
	{
		//Given
		when(preFulfillmentStrategyRegistry.get(Object.class.getSimpleName())).thenReturn(
				Arrays.asList(firstConsignmentPreFulfillmentStrategy, thirdConsignmentPreFulfillmentStrategy,
						secondConsignmentPreFulfillmentStrategy));
		when(strategyContextParam.getValue()).thenReturn(ThirdConsignmentPreFulfillmentStrategy.class.getSimpleName());
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(ProcessConsignmentPreFulfillmentAction.Transition.WAIT.toString(), transition);
		assertEquals(2, consignmentProcess.getContextParameters().size());
		final BusinessProcessParameterModel strategyParam = consignmentProcess.getContextParameters().stream()
				.filter(param -> action.LAST_EXECUTED_STRATEGY.equals(param.getName())).findFirst().get();
		assertEquals(SecondConsignmentPreFulfillmentStrategy.class.getSimpleName(), strategyParam.getValue());
	}

	@Test
	public void shouldTransitionToOkWhenAllPreFulfillmentStrategiesAlreadyBeenExecuted()
	{
		//Given
		when(preFulfillmentStrategyRegistry.get(Object.class.getSimpleName())).thenReturn(
				Arrays.asList(firstConsignmentPreFulfillmentStrategy, thirdConsignmentPreFulfillmentStrategy,
						secondConsignmentPreFulfillmentStrategy));
		when(strategyContextParam.getValue()).thenReturn(SecondConsignmentPreFulfillmentStrategy.class.getSimpleName());
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(ProcessConsignmentPreFulfillmentAction.Transition.OK.toString(), transition);
		assertEquals(2, consignmentProcess.getContextParameters().size());
		final BusinessProcessParameterModel strategyParam = consignmentProcess.getContextParameters().stream()
				.filter(param -> action.LAST_EXECUTED_STRATEGY.equals(param.getName())).findFirst().get();
		assertEquals(SecondConsignmentPreFulfillmentStrategy.class.getSimpleName(), strategyParam.getValue());
	}

	@Test
	public void shouldTransitionToErrorWhenInvalidLastExecutedStrategy()
	{
		//Given
		when(preFulfillmentStrategyRegistry.get(Object.class.getSimpleName())).thenReturn(
				Arrays.asList(firstConsignmentPreFulfillmentStrategy, thirdConsignmentPreFulfillmentStrategy,
						secondConsignmentPreFulfillmentStrategy));
		when(strategyContextParam.getValue()).thenReturn("InvalidStrategy");
		//When
		final String transition = action.execute(consignmentProcess);
		//Then
		assertEquals(ProcessConsignmentPreFulfillmentAction.Transition.ERROR.toString(), transition);
		verify(modelService, times(1)).save(consignment);
		assertEquals(ConsignmentStatus.CANCELLED, consignment.getStatus());
	}


	@Test
	public void shouldGetNullAsLastExecutedConsignmentPreFulfillmentStrategy()
	{
		//Given
		consignmentProcess.setContextParameters(Arrays.asList(declineContextParam));
		//When
		final ConsignmentPreFulfillmentStrategy consignmentPreFulfillmentStrategy = action
				.getLastExecutedConsignmentPreFulfillmentStrategy(consignmentProcess, consignmentPreFulfillmentStrategies);
		//Then
		assertEquals(null, consignmentPreFulfillmentStrategy);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionWhenInvalidLastStrategy()
	{
		//Given
		when(strategyContextParam.getValue()).thenReturn("InvalidStrategy");
		//When
		action.getLastExecutedConsignmentPreFulfillmentStrategy(consignmentProcess, consignmentPreFulfillmentStrategies);
	}

	@Test
	public void shouldSetStrategyInContextParam()
	{
		//Given
		consignmentProcess.setContextParameters(Collections.EMPTY_LIST);
		//When
		action.setStrategyInContextParam(firstConsignmentPreFulfillmentStrategy, consignmentProcess);
		//Then
		assertEquals(1, consignmentProcess.getContextParameters().size());
		verify(modelService).save(consignmentProcess);

	}

	@Test
	public void shouldCleanStrategyContextParamWhenParamPresent()
	{
		//When
		action.cleanStrategyContextParam(consignmentProcess);
		//Then
		assertEquals(1, consignmentProcess.getContextParameters().size());
	}

	@Test
	public void shouldCleanStrategyContextParamNoParamPresent()
	{
		//Given
		consignmentProcess.setContextParameters(Arrays.asList(declineContextParam));
		//When
		action.cleanStrategyContextParam(consignmentProcess);
		//Then
		assertEquals(1, consignmentProcess.getContextParameters().size());
	}



	protected static class FirstConsignmentPreFulfillmentStrategy implements ConsignmentPreFulfillmentStrategy
	{
		private static final Logger LOGGER = LoggerFactory.getLogger(FirstConsignmentPreFulfillmentStrategy.class);

		@Override
		public void perform(final ConsignmentModel consignment)
		{
			LOGGER.info("Executing FirstConsignmentPreFulfillmentStrategy");
		}

		@Override
		public boolean canProceedAfterPerform(ConsignmentModel consignment)
		{
			return true;
		}
	}

	protected static class SecondConsignmentPreFulfillmentStrategy implements ConsignmentPreFulfillmentStrategy
	{
		private static final Logger LOGGER = LoggerFactory.getLogger(SecondConsignmentPreFulfillmentStrategy.class);

		@Override
		public void perform(final ConsignmentModel consignment)
		{
			LOGGER.info("Executing SecondConsignmentPreFulfillmentStrategy");
		}

		@Override
		public boolean canProceedAfterPerform(ConsignmentModel consignment)
		{
			return false;
		}
	}

	protected static class ThirdConsignmentPreFulfillmentStrategy implements ConsignmentPreFulfillmentStrategy
	{
		private static final Logger LOGGER = LoggerFactory.getLogger(ThirdConsignmentPreFulfillmentStrategy.class);

		@Override
		public void perform(final ConsignmentModel consignment)
		{
			LOGGER.info("Executing ThirdConsignmentPreFulfillmentStrategy");
		}

		@Override
		public boolean canProceedAfterPerform(ConsignmentModel consignment)
		{
			return true;
		}
	}

}

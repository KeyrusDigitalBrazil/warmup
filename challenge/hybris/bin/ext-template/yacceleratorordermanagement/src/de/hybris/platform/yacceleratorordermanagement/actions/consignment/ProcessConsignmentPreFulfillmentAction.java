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

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.warehousing.externalfulfillment.strategy.ConsignmentPreFulfillmentStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.springframework.util.Assert.isTrue;


/**
 * Redirects the {@link ConsignmentModel} to wait node, if more pre fulfillment steps are required, else move to sendConsignmentToExternalFulfillmentSystem step in {@link ConsignmentProcessModel}.
 */
public class ProcessConsignmentPreFulfillmentAction extends AbstractAction<ConsignmentProcessModel>
{
	protected static final String LAST_EXECUTED_STRATEGY = "lastExecutedStrategy";
	private static final Logger LOG = LoggerFactory.getLogger(ProcessConsignmentPreFulfillmentAction.class);
	private Map<String, List<ConsignmentPreFulfillmentStrategy>> preFulfillmentStrategyRegistry;

	@Override
	public String execute(final ConsignmentProcessModel consignmentProcess)
	{
		LOG.info("Process: {} in step {}", consignmentProcess.getCode(), getClass().getSimpleName());
		final ConsignmentModel consignment = consignmentProcess.getConsignment();

		String transition = Transition.OK.toString();

		final List<ConsignmentPreFulfillmentStrategy> consignmentPreFulfillmentStrategies = getConsignmentPreFulfillmentStrategies(
				consignment);

		if (CollectionUtils.isNotEmpty(consignmentPreFulfillmentStrategies))
		{
			try
			{
				ConsignmentPreFulfillmentStrategy lastExecutedConsignmentPreFulfillmentStrategy = getLastExecutedConsignmentPreFulfillmentStrategy(
						consignmentProcess, consignmentPreFulfillmentStrategies);

				int indexOfNextStrategyToExecute = 0;
				boolean goAhead = true;
				if (lastExecutedConsignmentPreFulfillmentStrategy != null)
				{
					indexOfNextStrategyToExecute =
							consignmentPreFulfillmentStrategies.lastIndexOf(lastExecutedConsignmentPreFulfillmentStrategy) + 1;
				}

				while (indexOfNextStrategyToExecute <= consignmentPreFulfillmentStrategies.size() - 1 && goAhead)
				{
					final ConsignmentPreFulfillmentStrategy nextConsignmentPreFulfillmentStrategy = consignmentPreFulfillmentStrategies
							.get(indexOfNextStrategyToExecute);
					nextConsignmentPreFulfillmentStrategy.perform(consignment);
					goAhead = nextConsignmentPreFulfillmentStrategy.canProceedAfterPerform(consignment);
					setStrategyInContextParam(nextConsignmentPreFulfillmentStrategy, consignmentProcess);
					indexOfNextStrategyToExecute++;
				}
				if (!goAhead)
				{
					transition = Transition.WAIT.toString();
				}
			}
			catch (final IllegalStateException e)//NOSONAR
			{
				LOG.error(e.getMessage());
				consignment.setStatus(ConsignmentStatus.CANCELLED);
				getModelService().save(consignment);
				transition = Transition.ERROR.toString();
			}
		}

		LOG.debug("Process: {} transitions to {}", consignmentProcess.getCode(), transition);
		return transition;
	}

	/**
	 * Returns the list of {@link ConsignmentPreFulfillmentStrategy}(s), that needs to be applied on the given {@link ConsignmentModel}
	 *
	 * @param consignment
	 * 		the {@link ConsignmentModel}
	 * @return the list of {@link ConsignmentPreFulfillmentStrategy}(s)
	 */
	protected List<ConsignmentPreFulfillmentStrategy> getConsignmentPreFulfillmentStrategies(final ConsignmentModel consignment)
	{
		validateParameterNotNullStandardMessage("consignment", consignment);
		validateParameterNotNullStandardMessage("fulfillmentSystemConfig", consignment.getFulfillmentSystemConfig());
		return getPreFulfillmentStrategyRegistry().get(consignment.getFulfillmentSystemConfig().getClass().getSimpleName());
	}

	/**
	 * Evaluates the last executed {@link ConsignmentPreFulfillmentStrategy} for the given {@link ConsignmentModel}
	 *
	 * @param consignmentProcess
	 * 		the {@link ConsignmentProcessModel}
	 * @param consignmentPreFulfillmentStrategies
	 * 		the list of {@link ConsignmentPreFulfillmentStrategy}(s) to choose from
	 * @return the last executed {@link ConsignmentPreFulfillmentStrategy}.
	 * <br>Returns <i><strong>null</i></strong>, if no strategy was executed before
	 * @throws IllegalStateException
	 * 		if the last executed {@link ConsignmentPreFulfillmentStrategy} is invalid
	 */
	protected ConsignmentPreFulfillmentStrategy getLastExecutedConsignmentPreFulfillmentStrategy(
			final ConsignmentProcessModel consignmentProcess,
			final List<ConsignmentPreFulfillmentStrategy> consignmentPreFulfillmentStrategies) throws IllegalStateException
	{
		validateParameterNotNullStandardMessage("consignmentProcess", consignmentProcess);
		isTrue(CollectionUtils.isNotEmpty(consignmentPreFulfillmentStrategies));

		final Optional<BusinessProcessParameterModel> preFulfillmentStrategyContextParam = consignmentProcess.getContextParameters()
				.stream().filter(param -> LAST_EXECUTED_STRATEGY.equals(param.getName())).findFirst();

		if (preFulfillmentStrategyContextParam.isPresent())
		{
			final Object lastExecutedStrategyName = preFulfillmentStrategyContextParam.get().getValue();
			final Optional<ConsignmentPreFulfillmentStrategy> consignmentPreFulfillmentStrategyOptional = consignmentPreFulfillmentStrategies //NOSONAR
					.stream().filter(
							preFulfillmentStrategy -> lastExecutedStrategyName.equals(preFulfillmentStrategy.getClass().getSimpleName()))
					.findFirst();
			if (!consignmentPreFulfillmentStrategyOptional.isPresent())
			{
				throw new IllegalStateException(
						"Cannot find next ConsignmentPreFulfillmentStrategy to execute, since last executed ConsignmentPreFulfillmentStrategy: ["
								+ lastExecutedStrategyName + "] is not a valid strategy. Cancelling the current consignment[ "
								+ consignmentProcess.getConsignment().getCode() + "]!");
			}
			return consignmentPreFulfillmentStrategyOptional.get();
		}
		return null;
	}

	/**
	 * Sets the current executed {@link ConsignmentPreFulfillmentStrategy} in the context params of given {@link ConsignmentProcessModel}
	 *
	 * @param consignmentPreFulfillmentStrategy
	 * 		the {@link ConsignmentPreFulfillmentStrategy}
	 * @param consignmentProcess
	 * 		the {@link ConsignmentProcessModel}
	 */
	protected void setStrategyInContextParam(final ConsignmentPreFulfillmentStrategy consignmentPreFulfillmentStrategy,
			final ConsignmentProcessModel consignmentProcess)
	{
		validateParameterNotNullStandardMessage("consignmentProcess", consignmentProcess);
		validateParameterNotNullStandardMessage("consignmentPreFulfillmentStrategy", consignmentPreFulfillmentStrategy);

		cleanStrategyContextParam(consignmentProcess);

		final BusinessProcessParameterModel preFulfillmentStrategyParam = new BusinessProcessParameterModel();
		preFulfillmentStrategyParam.setName(LAST_EXECUTED_STRATEGY);
		preFulfillmentStrategyParam.setValue(consignmentPreFulfillmentStrategy.getClass().getSimpleName());
		preFulfillmentStrategyParam.setProcess(consignmentProcess);

		final Collection<BusinessProcessParameterModel> contextParams = new ArrayList<>();
		contextParams.addAll(consignmentProcess.getContextParameters());
		contextParams.add(preFulfillmentStrategyParam);

		consignmentProcess.setContextParameters(contextParams);
		getModelService().save(consignmentProcess);
	}

	/**
	 * Removes the strategy context param from {@link ConsignmentProcessModel#CONTEXTPARAMETERS}(if any exists), before attempting to set a new param
	 *
	 * @param consignmentProcess
	 * 		the {@link ConsignmentProcessModel} for the consignment to be fulfilled
	 */
	protected void cleanStrategyContextParam(final ConsignmentProcessModel consignmentProcess)
	{
		validateParameterNotNullStandardMessage("consignmentProcess", consignmentProcess);
		final Collection<BusinessProcessParameterModel> contextParams = new ArrayList<>();
		contextParams.addAll(consignmentProcess.getContextParameters());

		final Optional<BusinessProcessParameterModel> preFulfillmentStrategyParamOptional = contextParams.stream()
				.filter(param -> LAST_EXECUTED_STRATEGY.equals(param.getName())).findFirst();
		if (preFulfillmentStrategyParamOptional.isPresent())
		{
			final BusinessProcessParameterModel preFulfillmentStrategyParam = preFulfillmentStrategyParamOptional.get();
			contextParams.remove(preFulfillmentStrategyParam);
			getModelService().remove(preFulfillmentStrategyParam);
			consignmentProcess.setContextParameters(contextParams);
			getModelService().save(consignmentProcess);
		}

	}

	/**
	 * Evaluates if the given {@link ConsignmentPreFulfillmentStrategy} is last pre fulfillment strategy to be executed
	 *
	 * @param consignmentPreFulfillmentStrategy
	 * 		the given {@link ConsignmentPreFulfillmentStrategy} to be checked
	 * @param consignmentPreFulfillmentStrategies
	 * 		the given list of {@link ConsignmentPreFulfillmentStrategy}(s) to check against
	 * @return true if there is no more strategy to be executed after this strategy
	 */
	protected boolean isTerminalStrategy(final ConsignmentPreFulfillmentStrategy consignmentPreFulfillmentStrategy,
			final List<ConsignmentPreFulfillmentStrategy> consignmentPreFulfillmentStrategies)
	{
		return consignmentPreFulfillmentStrategies.size() - 1 == consignmentPreFulfillmentStrategies
				.lastIndexOf(consignmentPreFulfillmentStrategy);
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	protected Map<String, List<ConsignmentPreFulfillmentStrategy>> getPreFulfillmentStrategyRegistry()
	{
		return preFulfillmentStrategyRegistry;
	}

	@Required
	public void setPreFulfillmentStrategyRegistry(
			final Map<String, List<ConsignmentPreFulfillmentStrategy>> preFulfillmentStrategyRegistry)
	{
		this.preFulfillmentStrategyRegistry = preFulfillmentStrategyRegistry;
	}

	protected enum Transition
	{
		WAIT, OK, ERROR;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<>();

			for (final Transition transition : Transition.values())
			{
				res.add(transition.toString());
			}
			return res;
		}
	}
}

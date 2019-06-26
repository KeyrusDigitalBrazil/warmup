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
package de.hybris.platform.ruleengine.init;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.ruleengine.concurrency.RuleEngineTaskProcessor;
import de.hybris.platform.ruleengine.concurrency.TaskExecutionFuture;
import de.hybris.platform.ruleengine.concurrency.TaskResult;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;


public class BulkyTestDataLoader<T extends ItemModel> extends ServicelayerTest
{

	private static final Logger LOGGER = LoggerFactory.getLogger(BulkyTestDataLoader.class);
	private static String PRE_DESTROY_TOUT_PARAM = "ruleengine.test.dataload.task.predestroytimeout";      // NOSONAR

	@Resource
	private ModelService modelService;
	@Resource
	private ConfigurationService configurationService;
	@Resource
	private RuleEngineTaskProcessor<T, TaskResult> defaultRuleEngineTaskProcessor;

	protected Stopwatch stopwatch = Stopwatch.createUnstarted();

	public void loadData(final List<T> itemModels)
	{
		final long predestroyTimeout = getConfigurationService().getConfiguration().getLong(PRE_DESTROY_TOUT_PARAM, 10000);

		stopwatch.start();
		final TaskExecutionFuture<TaskResult> taskExecutionFuture = defaultRuleEngineTaskProcessor
				.execute(itemModels, itemsPartition -> modelService.saveAll(itemsPartition), predestroyTimeout);
		taskExecutionFuture.waitForTasksToFinish();

		LOGGER.info("Loading bulky data ({} items partitioned in N threads) finished in [{}]", itemModels.size(),
				stopwatch.stop().toString());
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}
}

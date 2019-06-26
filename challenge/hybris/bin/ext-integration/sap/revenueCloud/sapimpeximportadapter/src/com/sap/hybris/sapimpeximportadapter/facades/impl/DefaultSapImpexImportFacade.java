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
package com.sap.hybris.sapimpeximportadapter.facades.impl;

import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.io.InputStream;

import com.google.common.base.Preconditions;
import com.sap.hybris.sapimpeximportadapter.facades.ImpexImportFacade;
import com.sap.hybris.sapimpeximportadapter.services.ImpexImportService;


/**
 * Default implementation for {@link ImpexImportFacade}}
 * 
 * @deprecated since 1811, please use {@link de.hybris.platform.integrationservices.model.IntegrationObjectModel}
 */
@Deprecated
public class DefaultSapImpexImportFacade implements ImpexImportFacade
{


	private ImportService importService;
	private ImpexImportService impexImportService;
	private ModelService modelService;
	private TimeService timeService;
	private TaskService taskService;


	/**
	 * Creates and imports impex from the input stream payload
	 *
	 * @param inputStream
	 *           - input stream of impex payload
	 */
	@Override
	public void createAndImportImpexMedia(final InputStream inputStream)
	{
		final ImpExMediaModel impexMedia = impexImportService.createImpexMedia(inputStream);
		impexMedia.setRemoveOnSuccess(true);
		modelService.save(impexMedia);
		this.scheduleImportTask(impexMedia);
	}


	/**
	 * Creates a task model and schedules a the impex import task
	 *
	 * @param impexMedia
	 *           - impex media model
	 */
	public void scheduleImportTask(final ImpExMediaModel impexMedia)
	{
		Preconditions.checkArgument(impexMedia != null, "impexMedia cannot be null");
		final TaskModel task = getModelService().create(TaskModel.class);
		task.setRunnerBean("sapImpexImportTaskRunner");
		task.setExecutionTimeMillis(Long.valueOf(timeService.getCurrentTime().getTime()));
		task.setContext(impexMedia);
		this.taskService.scheduleTask(task);
	}


	/**
	 * @return the importService
	 */
	public ImportService getImportService()
	{
		return importService;
	}


	/**
	 * @param importService
	 *           the importService to set
	 */
	public void setImportService(final ImportService importService)
	{
		this.importService = importService;
	}


	/**
	 * @return the impexImportService
	 */
	public ImpexImportService getImpexImportService()
	{
		return impexImportService;
	}


	/**
	 * @param impexImportService
	 *           the impexImportService to set
	 */
	public void setImpexImportService(final ImpexImportService impexImportService)
	{
		this.impexImportService = impexImportService;
	}


	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}


	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the timeService
	 */
	public TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * @return the taskService
	 */
	public TaskService getTaskService()
	{
		return taskService;
	}

	/**
	 * @param taskService
	 *           the taskService to set
	 */
	public void setTaskService(final TaskService taskService)
	{
		this.taskService = taskService;
	}


}

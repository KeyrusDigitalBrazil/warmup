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
package de.hybris.platform.apiregistryservices.task;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * A Task Runner for OAuthClientDetails removal
 */
public class RemoveOAuthClientTask implements TaskRunner<TaskModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(RemoveOAuthClientTask.class);
	private ModelService modelService;

	@Override
	public void run(final TaskService taskService, final TaskModel taskModel)
	{
		if (taskModel == null || taskModel.getContextItem() == null || modelService.isRemoved(taskModel.getContextItem()))
		{
			LOG.error("There is no task for RemoveOAuthClientTask runner bean. Or the OAuthClientDetailsModel was already deleted");
			return;
		}
		final OAuthClientDetailsModel oldClient = (OAuthClientDetailsModel) taskModel.getContextItem();
		final String oldClientId = oldClient.getClientId();
		getModelService().remove(oldClient);
		LOG.info("Successfully deleted OAuthClientDetails with id : {}", oldClientId);
	}

	@Override
	public void handleError(final TaskService taskService, final TaskModel taskModel, final Throwable error)
	{
		LOG.error("Failed to delete the OAuthClientDetails", error);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}

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

package de.hybris.platform.apiregistryservices.services.impl;

import de.hybris.platform.apiregistryservices.dao.CredentialDao;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.CredentialService;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link de.hybris.platform.apiregistryservices.services.CredentialService}
 */
public class DefaultCredentialService implements CredentialService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCredentialService.class);

	private ModelService modelService;
	private CredentialDao credentialDao;
	private TaskService taskService;


	@Override
	public List<ExposedOAuthCredentialModel> getCredentialsByClientId(final String clientId)
	{
		return getCredentialDao().getAllExposedOAuthCredentialsByClientId(clientId);
	}

	@Override
	public void resetCredentials(final List<ExposedOAuthCredentialModel> credentials, final String clientId,
			final String clientSecret, final Integer gracePeriod)
	{
		final OAuthClientDetailsModel oldClient = credentials.get(0).getOAuthClientDetails();

		final OAuthClientDetailsModel newClient = getModelService().clone(oldClient);
		newClient.setClientId(clientId);
		newClient.setClientSecret(clientSecret);

		credentials.forEach(cred -> resetCredential(cred, newClient));

		getModelService().save(newClient);
		getModelService().saveAll(credentials);
		LOG.info("New OAuthClientDetails with id : {} has been activated", newClient.getClientId());

		if (gracePeriod > 0)
		{
			final TaskModel oldClientRemovalTask = getModelService().create(TaskModel.class);
			oldClientRemovalTask.setExecutionDate(new Date(System.currentTimeMillis() + gracePeriod));
			oldClientRemovalTask.setContextItem(oldClient);
			oldClientRemovalTask.setRunnerBean("removeOAuthClientTask");
			getTaskService().scheduleTask(oldClientRemovalTask);
		}
		else
		{
			removeOldClient(oldClient);
		}
	}

	protected void removeOldClient(final OAuthClientDetailsModel oldClient)
	{
		try
		{
			getModelService().remove(oldClient);
			LOG.info("Successfully deleted OAuthClientDetails with id : {}", oldClient.getClientId());
		}
		catch (final ModelRemovalException e)
		{
			LOG.error(String.format("Cannot delete the missing OAuthClientDetails with id : {%s}", oldClient.getClientId()), e);
		}
	}

	protected void resetCredential(final ExposedOAuthCredentialModel credential, final OAuthClientDetailsModel newClient)
	{
		credential.setOAuthClientDetails(newClient);
		credential.setPassword(newClient.getClientSecret());
		LOG.info("Activating new OAuthClientDetails with id : {} for ExposedOAuthCredential with id : {}", newClient.getClientId(),
				credential.getId());
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

	protected TaskService getTaskService()
	{
		return taskService;
	}

	@Required
	public void setTaskService(final TaskService taskService)
	{
		this.taskService = taskService;
	}

	protected CredentialDao getCredentialDao()
	{
		return credentialDao;
	}

	@Required
	public void setCredentialDao(final CredentialDao credentialDao)
	{
		this.credentialDao = credentialDao;
	}
}

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
package de.hybris.platform.smarteditwebservices.configuration.service.impl;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.smarteditwebservices.configuration.dao.SmarteditConfigurationDao;
import de.hybris.platform.smarteditwebservices.configuration.service.SmarteditConfigurationService;
import de.hybris.platform.smarteditwebservices.model.SmarteditConfigurationModel;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the Smartedit Configuration Service.
 * It has dependencies on the {@link ModelService} and {@link SmarteditConfigurationDao}
 */
public class DefaultSmarteditConfigurationService implements SmarteditConfigurationService
{

	private SmarteditConfigurationDao smarteditConfigurationDao;

	private ModelService modelService;


	@Override
	public List<SmarteditConfigurationModel> findAll()
	{
		return getSmarteditConfigurationDao().loadAll();
	}


	@Override
	public SmarteditConfigurationModel create(final SmarteditConfigurationModel configurationModel)
	{
		if (getSmarteditConfigurationDao().findByKey(configurationModel.getKey()) != null)
		{
			throw new AmbiguousIdentifierException("Key " + configurationModel.getKey() + " already exists.");
		}
		SmarteditConfigurationModel configuration = new SmarteditConfigurationModel();
		configuration.setKey(configurationModel.getKey());
		configuration.setValue(configurationModel.getValue());
		getModelService().save(configuration);
		return configuration;
	}

	@Override
	public SmarteditConfigurationModel update(final String uid, final SmarteditConfigurationModel configurationModel)
	{
		SmarteditConfigurationModel configuration = findByKey(uid);
		configuration.setValue(configurationModel.getValue());
		getModelService().save(configuration);
		return configuration;
	}

	@Override
	public void delete(final String uid)
	{
		getModelService().remove(findByKey(uid));
	}

	@Override
	public SmarteditConfigurationModel findByKey(final String key)
	{
		final SmarteditConfigurationModel model = getSmarteditConfigurationDao().findByKey(key);
		if (model == null)
		{
			throw new UnknownIdentifierException("Could not find configuration with key [" + key + "].");
		}
		return model;
	}

	protected SmarteditConfigurationDao getSmarteditConfigurationDao()
	{
		return smarteditConfigurationDao;
	}

	@Required
	public void setSmarteditConfigurationDao(final SmarteditConfigurationDao smarteditConfigurationDao)
	{
		this.smarteditConfigurationDao = smarteditConfigurationDao;
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

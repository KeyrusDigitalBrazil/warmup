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

import de.hybris.platform.apiregistryservices.dao.DestinationDao;
import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link DestinationService}
 *
 * @param <T>
 *           the type parameter which extends the {@link AbstractDestinationModel} type
 */
public class DefaultDestinationService<T extends AbstractDestinationModel> implements DestinationService
{
	private DestinationDao<T> destinationDao;

	@Override
	public List<T> getDestinationsByChannel(final DestinationChannel channel)
	{
		return getDestinationDao().getDestinationsByChannel(channel);
	}

	@Override
	public List<T> getDestinationsByDestinationTargetId(final String  destinationTargetId)
	{
		return getDestinationDao().getDestinationsByDestinationTargetId(destinationTargetId);
	}

	@Override
	public List<ExposedDestinationModel> getActiveExposedDestinationsByClientId(final String clientId)
	{
		return getDestinationDao().findActiveExposedDestinationsByClientId(clientId);
	}

	@Override
	public List<ExposedDestinationModel> getActiveExposedDestinationsByChannel(final DestinationChannel channel)
	{
		return getDestinationDao().findActiveExposedDestinationsByChannel(channel);
	}

	@Override
	public T getDestinationById(final String id)
	{
		return getDestinationDao().getDestinationById(id);
	}

	@Override
	public List getAllDestinations()
	{
		return getDestinationDao().findAllDestinations();
	}

	protected DestinationDao<T> getDestinationDao()
	{
		return destinationDao;
	}

	@Required
	public void setDestinationDao(final DestinationDao<T> destinationDao)
	{
		this.destinationDao = destinationDao;
	}

}

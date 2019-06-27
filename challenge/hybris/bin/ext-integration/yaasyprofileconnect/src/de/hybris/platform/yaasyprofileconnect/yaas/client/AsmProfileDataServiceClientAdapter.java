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
package de.hybris.platform.yaasyprofileconnect.yaas.client;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.yaasyprofileconnect.yaas.Profile;

import org.springframework.beans.factory.annotation.Required;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


/**
 * Adapter around the Charon client to retrieve profile in a blocking way without blocking every Charon client.
 */
public class AsmProfileDataServiceClientAdapter implements AsmProfileDataServiceClient
{
    private ApiRegistryClientService apiRegistryClientService;
    private final Scheduler scheduler = Schedulers.io();

    @Override
    public Profile getProfile(final String id)
    {
        return getAdaptee().getProfileAsync(id)
                .subscribeOn(scheduler)
                .toBlocking().first();
    }

    @Override
    public Profile getProfile(final String id, final String fields)
    {
        return getAdaptee().getProfileAsync(id, fields)
                .subscribeOn(scheduler)
                .toBlocking().first();
    }

    @Override
    public Observable<Profile> getProfileAsync(final String id)
    {
        return getAdaptee().getProfileAsync(id);
    }

    @Override
    public Observable<Profile> getProfileAsync(final String id, final String fields)
    {
        return getAdaptee().getProfileAsync(id, fields);
    }

	public AsmProfileDataServiceClient getAdaptee()
	{
		try
		{
			return getApiRegistryClientService().lookupClient(AsmProfileDataServiceClient.class);
		}
		catch (final CredentialException e)
		{
			throw new SystemException(e);
		}
	}

	protected ApiRegistryClientService getApiRegistryClientService()
	{
		return apiRegistryClientService;
	}

	@Required
	public void setApiRegistryClientService(final ApiRegistryClientService apiRegistryClientService)
	{
		this.apiRegistryClientService = apiRegistryClientService;
	}
}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.gigya.gigyaservices.retention;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.constants.GigyaservicesConstants;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.retention.hook.ItemCleanupHook;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.gigya.socialize.GSResponse;


/**
 * Hook to delete user from gigya if property 'gigyaservices.delete.user.from.gigya' is set to 'true'
 */
public class GigyaUserCleanupHook implements ItemCleanupHook<CustomerModel>
{

	private static final Logger LOG = Logger.getLogger(GigyaUserCleanupHook.class);

	private GigyaService gigyaService;

	private GenericDao<GigyaConfigModel> gigyaConfigGenericDao;

	@Override
	public void cleanupRelatedObjects(final CustomerModel gigyaUser)
	{
		final List<GigyaConfigModel> gigyaConfigs = getGigyaConfigGenericDao()
				.find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, gigyaUser.getGyApiKey()));
		if (CollectionUtils.isNotEmpty(gigyaConfigs) && BooleanUtils.isTrue(gigyaConfigs.get(0).getDeleteUser()))
		{
			try
			{
				final GSResponse res = getGigyaService().callRawGigyaApiWithConfig("accounts.deleteAccount",
						Collections.singletonMap("UID", gigyaUser.getGyUID()), gigyaConfigs.get(0),
						GigyaservicesConstants.MAX_RETRIES, GigyaservicesConstants.TRY_NUM);
				if (res != null && res.getErrorCode() == 0)
				{
					LOG.info(String.format("Gigya user with uid=%s and gigya uid=%s deleted.", gigyaUser.getUid(),
							gigyaUser.getGyUID()));
				}
				else
				{
					throw new GigyaApiException(String.format("Error while deleting gigya user with uid=%s and gigya uid=%s.",
							gigyaUser.getUid(), gigyaUser.getGyUID()));
				}
			}
			catch (final GigyaApiException e)
			{
				throw new GigyaApiException("Error in deleting user from gigya for uid=" + gigyaUser.getUid(), e);
			}
		}
	}

	public GigyaService getGigyaService()
	{
		return gigyaService;
	}

	@Required
	public void setGigyaService(final GigyaService gigyaService)
	{
		this.gigyaService = gigyaService;
	}

	public GenericDao<GigyaConfigModel> getGigyaConfigGenericDao()
	{
		return gigyaConfigGenericDao;
	}

	@Required
	public void setGigyaConfigGenericDao(final GenericDao<GigyaConfigModel> gigyaConfigGenericDao)
	{
		this.gigyaConfigGenericDao = gigyaConfigGenericDao;
	}
}

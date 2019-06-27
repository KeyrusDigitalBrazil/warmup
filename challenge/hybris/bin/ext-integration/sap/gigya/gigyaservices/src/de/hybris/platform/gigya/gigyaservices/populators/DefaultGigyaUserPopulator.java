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
package de.hybris.platform.gigya.gigyaservices.populators;

import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.constants.GigyaservicesConstants;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSyncDirection;
import de.hybris.platform.gigya.gigyaservices.enums.GyAttributeType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;


/**
 * Populator to convert gigya user model to gsObject
 */
public class DefaultGigyaUserPopulator implements Populator<CustomerModel, GSObject>
{

	private static final Logger LOG = Logger.getLogger(DefaultGigyaUserPopulator.class);

	private GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao;

	private ModelService modelService;

	private CustomerNameStrategy customerNameStrategy;

	@Override
	public void populate(final CustomerModel gigyaUser, final GSObject gsObject)
	{
		populateMandatoryValues(gigyaUser, gsObject);
		final List<GigyaFieldMappingModel> allMappings = gigyaFieldMappingGenericDao.find();
		allMappings.stream()
				.filter(mapping -> (mapping.getSyncDirection().equals(GigyaSyncDirection.BOTH)
						|| mapping.getSyncDirection().equals(GigyaSyncDirection.H2G))
						&& !GyAttributeType.COMPLEX.equals(mapping.getHybrisType()))
				.forEach(mapping -> mapAndPopulateGigyaObject(gigyaUser, gsObject, mapping));
	}

	/**
	 * Maps and populate data in GSObject
	 *
	 * @param gigyaUser
	 * @param gsObject
	 * @param mapping
	 */
	private void mapAndPopulateGigyaObject(final CustomerModel gigyaUser, final GSObject gsObject,
			final GigyaFieldMappingModel mapping)
	{
		if (mapping.getGigyaAttributeName().contains("."))
		{
			final String[] levels = mapping.getGigyaAttributeName().split("\\.");
			GSObject temp = gsObject;
			for (int i = 0; i < levels.length - 1; i++)
			{
				temp = mapLevels(gsObject, levels, temp, i);
			}
			handleAttribute(temp, mapping, gigyaUser, levels[levels.length - 1]);
		}
		else
		{
			handleAttribute(gsObject, mapping, gigyaUser, mapping.getGigyaAttributeName());
		}
	}

	/**
	 * Maps different levels of attribute
	 */
	private GSObject mapLevels(final GSObject gsObject, final String[] levels, GSObject temp, final int i)
	{
		try
		{
			if (temp.containsKey(levels[i]))
			{
				temp = gsObject.getObject(levels[i]);
			}
			else
			{
				temp.put(levels[i], new GSObject());
				temp = temp.getObject(levels[i]);
			}
		}
		catch (final GSKeyNotFoundException e)
		{
			LOG.error(e);
		}
		return temp;
	}

	/**
	 * Populate mandatory values like email, name in gsObject
	 *
	 * @param gigyaUser
	 * @param gsObject
	 * @throws GSKeyNotFoundException
	 */
	private void populateMandatoryValues(final CustomerModel gigyaUser, final GSObject gsObject)
	{
		gsObject.put("UID", gigyaUser.getGyUID());
		GSObject profileObject = new GSObject();
		if (gsObject.containsKey(GigyaservicesConstants.GIGYA_PROFILE))
		{
			try
			{
				profileObject = (GSObject) gsObject.get(GigyaservicesConstants.GIGYA_PROFILE);
			}
			catch (final GSKeyNotFoundException e)
			{
				LOG.error("Exception while fetching profile object " + e);
			}
		}

		final String[] name = customerNameStrategy.splitName(gigyaUser.getName());
		profileObject.put("firstName", name[0]);
		profileObject.put("lastName", name[1]);
		profileObject.put("email", gigyaUser.getUid());

		gsObject.put(GigyaservicesConstants.GIGYA_PROFILE, profileObject);
	}

	/**
	 * Method to set values in gsObject from gigyaUser object
	 *
	 * @param gsObject
	 * @param mapping
	 * @param gigyaUser
	 * @param gigyaAttributeName
	 */
	private void handleAttribute(final GSObject gsObject, final GigyaFieldMappingModel mapping, final CustomerModel gigyaUser,
			final String gigyaAttributeName)
	{
		if (GyAttributeType.DATE.equals(mapping.getHybrisType()))
		{
			final SimpleDateFormat formatter;
			final String pattern = Config.getParameter("gigya.dateFormat");
			if (null != pattern)
			{
				formatter = new SimpleDateFormat(pattern);
			}
			else
			{
				formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			}
			gsObject.put(gigyaAttributeName,
					formatter.format(modelService.getAttributeValue(gigyaUser, mapping.getHybrisAttributeName())));
		}
		else
		{
			gsObject.put(gigyaAttributeName, modelService.getAttributeValue(gigyaUser, mapping.getHybrisAttributeName()).toString());
		}
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public GenericDao<GigyaFieldMappingModel> getGigyaFieldMappingGenericDao()
	{
		return gigyaFieldMappingGenericDao;
	}

	@Required
	public void setGigyaFieldMappingGenericDao(final GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao)
	{
		this.gigyaFieldMappingGenericDao = gigyaFieldMappingGenericDao;
	}

	public CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	@Required
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}
}
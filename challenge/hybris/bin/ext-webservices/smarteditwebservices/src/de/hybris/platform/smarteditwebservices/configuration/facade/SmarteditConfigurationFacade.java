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
package de.hybris.platform.smarteditwebservices.configuration.facade;

import de.hybris.platform.smarteditwebservices.data.ConfigurationData;

import java.util.List;
import java.util.Optional;

/**
 * Interface methods for SmarteditConfigurationFacade.
 * The implementing class will provide methods for validating, populating and persisting configuration models.
 */
public interface SmarteditConfigurationFacade
{
	/**
	 * Finds all ConfigurationData stores in the data store.
	 * @return a list of {@link ConfigurationData}
	 */
	List<ConfigurationData> findAll();

	/**
	 * Create a new configuration model
	 * @param configurationData the data bean containing the values to be saved
	 * @return the {@link ConfigurationData} created
	 */
	ConfigurationData create(ConfigurationData configurationData);

	/**
	 * Updates the configuration model represented by the uid
	 * @param uid is the unique identifier of this configuration
	 * @param configurationData - the data bean to be updated
	 * @return the configuration bean updated
	 */
	ConfigurationData update(String uid, ConfigurationData configurationData);

	/**
	 * Finds a configuration data bean by its unique identifier
	 * @param uid the configuration's unique identifier
	 * @return the {@link ConfigurationData} represented by this uid
	 */
	ConfigurationData findByUid(String uid);

	/**
	 * Finds a configuration data bean using one of the identifiers that are provided by default
	 * @param key one of the {@link DefaultConfigurationKey}
	 * @return the {@link ConfigurationData} represented by this {@link DefaultConfigurationKey}
	 */
	ConfigurationData findByDefaultConfigurationKey(DefaultConfigurationKey key);

	/**
	 * Will try and find a configuration data bean using one of the identifiers that are provided by default
	 * @param key one of the {@link DefaultConfigurationKey}
	 * @return the {@link Optional<ConfigurationData>} represented by this {@link DefaultConfigurationKey}
	 */
	Optional<ConfigurationData> tryAndFindByDefaultConfigurationKey(DefaultConfigurationKey key);

	/**
	 * Deletes the configuration model represented by this unique identifier
	 * @param uid the model's unique identifier
	 */
	void delete(String uid);
}

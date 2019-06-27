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
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;

import java.util.List;


/**
 * Service to retrieve {@link ProductConfigurationModel}s from the persistence.
 */
/**
 *
 */
public interface ProductConfigurationPersistenceService
{

	/**
	 * read the {@link ProductConfigurationModel} by configId
	 *
	 * @param configId
	 *           configuration id
	 * @return product configuration persistence model
	 */
	default ProductConfigurationModel getByConfigId(final String configId)
	{
		return getByConfigId(configId, false);
	}


	/**
	 * read the {@link ProductConfigurationModel} by configId
	 *
	 * @param configId
	 *           configuration id
	 *
	 * @param allowNull
	 *           if <code>true</code> null is returned if no configuration with the given id is found, otherwise an
	 *           exception is thrown
	 * @return n product configuration persistence model
	 */
	ProductConfigurationModel getByConfigId(String configId, boolean allowNull);


	/**
	 * read the {@link ProductConfigurationModel} associated to the given product code for the current user. In case of an
	 * anoymous user, the user seesion id is used.
	 *
	 * @param productCode
	 *           product Code
	 * @return product configuration persistence model
	 */
	ProductConfigurationModel getByProductCode(String productCode);

	/**
	 * read all {@link ProductConfigurationModel}s currently linked to the given product and user
	 *
	 * @param productCode
	 *           product Code
	 * @param user
	 *           user model
	 * @return product configuration persistence model
	 */
	ProductConfigurationModel getByProductCodeAndUser(String productCode, UserModel user);

	/**
	 * read the {@link AbstractOrderEntryModel} by PK
	 *
	 * @param cartEntryKey
	 *           cart Entry PK
	 * @return order entry
	 */
	AbstractOrderEntryModel getOrderEntryByPK(String cartEntryKey);

	/**
	 * get the order entry to which the given config is currently assigned to. <b>throws an AmbigiousIdentifierException, if
	 * there exists more than one OrderEntry linked to the given configId.</b>
	 *
	 * @param configId
	 *           product configuration id
	 * @param isDraft
	 *           if<code>true</code> draft link will be checked, otherwise default link
	 * @return order entry
	 */
	AbstractOrderEntryModel getOrderEntryByConfigId(String configId, boolean isDraft);

	/**
	 * Get all order entries to which the given config is currently assigned to. In most scenarios this is exactly one or no
	 * entry at all.
	 *
	 * @param configId
	 *           product configuration id
	 *
	 * @return order entry
	 */
	List<AbstractOrderEntryModel> getAllOrderEntriesByConfigId(String configId);

	/**
	 * read the list of {@link ProductConfigurationModel} associated to the given user session id
	 *
	 * @param userSessionId
	 *           id of the user session
	 * @return list of product configuration persistence models
	 */
	List<ProductConfigurationModel> getByUserSessionId(String userSessionId);


	/**
	 * Gets configurations that are not related to abstract order entries and have not been touched for a certain time
	 * (specified in days)
	 *
	 * @param thresholdInDays
	 *           Select entries that are older than current date - threshold
	 * @param pageSize
	 *           max number of models to be read within one invocation
	 * @param currentPage
	 *           page idx to read
	 * @return List of product configuration persistence models
	 */
	SearchPageData<ProductConfigurationModel> getProductRelatedByThreshold(Integer thresholdInDays, int pageSize, int currentPage);

	/**
	 * Gets configurations that are neither related to any abstract order entry nor are related to any Product. So they are
	 * considered orphaned.
	 *
	 * @param pageSize
	 *           max number of models to be read within one invocation
	 * @param currentPage
	 *           page idx to read
	 * @return List of product configuration persistence models
	 */
	SearchPageData<ProductConfigurationModel> getOrphaned(int pageSize, int currentPage);

	/**
	 * Gets all configurations currently persistet
	 *
	 * @param pageSize
	 *           max number of models to be read within one invocation
	 * @param currentPage
	 *           page idx to read
	 * @return List of product configuration persistence models
	 */
	SearchPageData<ProductConfigurationModel> getAll(int pageSize, int currentPage);

	/**
	 * Checks whether a configuration is linked to more than the given abstract order entry.
	 *
	 * @param configId
	 *           config id to check
	 * @param cartEntryKey
	 *           Pk of abstract order entry
	 * @return <code>true</code>, only if the given configuration id is only linked to the given abstract order entry.
	 */
	boolean isOnlyRelatedToGivenEntry(String configId, String cartEntryKey);


}

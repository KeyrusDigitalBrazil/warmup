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
package de.hybris.platform.sap.sapordermgmtbol.transaction.util;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.List;

/**
 *	Dummy implementation of BaseStoreService
 */
public class DummyBaseStoreService implements BaseStoreService
{

	@Override
	public List<BaseStoreModel> getAllBaseStores()
	{
		return null;
	}

	@Override
	public BaseStoreModel getBaseStoreForUid(final String arg0) throws AmbiguousIdentifierException, UnknownIdentifierException
	{
		return null;
	}

	@Override
	public BaseStoreModel getCurrentBaseStore()
	{
		return null;
	}

}

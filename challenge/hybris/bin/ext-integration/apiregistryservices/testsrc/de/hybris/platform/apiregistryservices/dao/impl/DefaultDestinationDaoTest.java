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

package de.hybris.platform.apiregistryservices.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.dao.DestinationDao;
import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class DefaultDestinationDaoTest extends ServicelayerTest
{
    @Resource
	 private DestinationDao<AbstractDestinationModel> destinationDao;

    @Before
    public void setUp() throws Exception
    {
        importCsv("/test/apis.impex", "UTF-8");
    }

    @Test
    public void getDestinationsByChannel() throws Exception
    {
        final List destinationsByChannel = destinationDao.getDestinationsByChannel(DestinationChannel.KYMA);
		assertTrue(destinationsByChannel.size() == 5);
    }

    @Test
    public void getDestinationById() throws Exception
    {
        final AbstractDestinationModel firstDest = destinationDao.getDestinationById("first_dest");
        assertEquals("e1", firstDest.getEndpoint().getId());
    }

	@Test
	public void getActiveDestinationsByChannel() throws Exception
	{
		final List destinationsByChannel = destinationDao.findActiveExposedDestinationsByChannel(DestinationChannel.KYMA);
		assertTrue(destinationsByChannel.size() == 4);
	}

	@Test
	public void getActiveDestinationsByClientId() throws Exception
	{
		final List destinationsByChannel = destinationDao
				.findActiveExposedDestinationsByClientId("kyma");
		assertTrue(destinationsByChannel.size() == 4);
	}
}

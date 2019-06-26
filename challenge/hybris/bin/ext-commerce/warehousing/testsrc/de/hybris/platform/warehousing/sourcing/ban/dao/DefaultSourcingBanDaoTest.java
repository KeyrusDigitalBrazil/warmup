/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.sourcing.ban.dao;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.warehousing.model.SourcingBanModel;
import de.hybris.platform.warehousing.sourcing.ban.dao.impl.DefaultSourcingBanDao;
import de.hybris.platform.warehousing.util.BaseSourcingIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class DefaultSourcingBanDaoTest extends BaseSourcingIntegrationTest
{
	@Resource
	private DefaultSourcingBanDao sourcingBanDao;
	@Resource
	private ModelService modelService;
	@Resource
	private TimeService timeService;

	private SourcingBanModel ban1;
	private SourcingBanModel ban2;
	private WarehouseModel boston;
	private WarehouseModel montreal;

	@Before
	public void setup()
	{
		boston = warehouses.Boston();
		montreal = warehouses.Montreal();
		ban1 = modelService.create(SourcingBanModel.class);
		ban1.setWarehouse(boston);
		modelService.save(ban1);
	}

	@After
	public void cleanup()
	{
		modelService.remove(ban1);
		timeService.resetTimeOffset();
	}


	@Test
	public void shouldFindSourcingBanByWarehouse()
	{
		//given sourcingBan for warehouse Boston in the setup

		//when fetching the sourcing ban
		final Collection<SourcingBanModel> sourcingBanModels = sourcingBanDao.getSourcingBan(Collections.singleton(boston),
				LocalDate.now().minusDays(1).toDate());

		//then we find one ban attached to Boston
		assertNotNull(sourcingBanModels);
		assertEquals(sourcingBanModels.iterator().next().getWarehouse(), boston);
	}

	@Test
	public void shouldNotFindSourcingBanByWarehouse()
	{
		//changing the system time for 2 days before
		timeService.setCurrentTime(new Date(ban1.getCreationtime().getTime() - (2 * 24 * 60 * 60 * 1000))); //  - 2 days
		// create a ban 2 days before
		ban2 = modelService.create(SourcingBanModel.class);
		ban2.setWarehouse(boston);

		modelService.save(ban2);

		//when fetching the sourcing ban for montreal in the last day
		final Collection<SourcingBanModel> sourcingBanModels = sourcingBanDao.getSourcingBan(Collections.singleton(montreal),
				LocalDate.now().minusDays(1).toDate());

		//then we find no ban attached to montreal for last day
		assertTrue(sourcingBanModels.isEmpty());
	}

}

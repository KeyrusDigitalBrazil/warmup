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
package de.hybris.platform.basecommerce.util;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Utilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.GenericApplicationContext;


public abstract class BaseCommerceBaseTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(BaseCommerceBaseTest.class);


	/**
	 * Please do not access this field directly: use {@link #getFlexibleSearchService()} instead.
	 */
	@Resource
	protected FlexibleSearchService flexibleSearchService; //NOPMD

	public BaseCommerceBaseTest()
	{
		// disable spring integration polling.
		try
		{
			final SpringCustomContextLoader springCustomContextLoader = new SpringCustomContextLoader(this.getClass());
			springCustomContextLoader.loadApplicationContexts((GenericApplicationContext) Registry.getCoreApplicationContext());
			springCustomContextLoader
					.loadApplicationContextByConvention((GenericApplicationContext) Registry.getCoreApplicationContext());
		}
		catch (final Exception e)
		{
			throw new RuntimeException(e.getMessage(), e); //NOPMD
		}
	}

	@Before
	public void initMocks()
	{
		MockitoAnnotations.initMocks(this);
	}

	protected List<BusinessProcessModel> getProcesses(final String processDefinitionName, final List<ProcessState> processStates)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery("select {" + BusinessProcessModel.PK + "} from {"
				+ BusinessProcessModel._TYPECODE + "} where {" + BusinessProcessModel.STATE + "} in (?state) AND {"
				+ BusinessProcessModel.PROCESSDEFINITIONNAME + "} = ?processDefinitionName");
		query.addQueryParameter(BusinessProcessModel.PROCESSDEFINITIONNAME, processDefinitionName);
		query.addQueryParameter(BusinessProcessModel.STATE, processStates);
		final SearchResult<BusinessProcessModel> result = getFlexibleSearchService().search(query);
		return result.getResult();
	}

	protected boolean waitForProcessToEnd(final String processDefinitionName, final long maxWait) throws InterruptedException
	{
		final long start = System.currentTimeMillis();
		while (true)
		{
			final List<BusinessProcessModel> processes = getProcesses(processDefinitionName, Arrays.asList(new ProcessState[]
			{ ProcessState.RUNNING, ProcessState.CREATED, ProcessState.WAITING }));

			if (CollectionUtils.isEmpty(processes))
			{
				return true;
			}
			if (System.currentTimeMillis() - start > maxWait)
			{
				LOG.warn(String.format("BusinessProcesses with processDefinitionName %s are still in running! Waited for %s",
						processDefinitionName, Utilities.formatTime(System.currentTimeMillis() - start)));
				for (final BusinessProcessModel process : processes)
				{
					LOG.warn(String.format("Process %s has state: %s", process.getCode(), process.getState()));
				}
				return false;
			}
			else
			{
				Thread.sleep(1000);
			}
		}
	}

	protected OrderModel getOrderForCode(final String orderCode)
	{
		final DefaultGenericDao defaultGenericDao = new DefaultGenericDao(OrderModel._TYPECODE);
		defaultGenericDao.setFlexibleSearchService(getFlexibleSearchService());
		final List<OrderModel> orders = defaultGenericDao.find(Collections.singletonMap(OrderModel.CODE, orderCode));
		Assert.assertFalse(orders.isEmpty());
		final OrderModel orderModel = orders.get(0);
		Assert.assertNotNull("Order should have been loaded from database", orderModel);
		return orderModel;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}

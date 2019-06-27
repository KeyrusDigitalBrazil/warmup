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
package de.hybris.platform.warehousing.util;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.model.SourcingConfigModel;
import de.hybris.platform.warehousing.returns.service.RestockConfigService;
import de.hybris.platform.warehousing.sourcing.SourcingService;
import de.hybris.platform.warehousing.util.models.Addresses;
import de.hybris.platform.warehousing.util.models.Asns;
import de.hybris.platform.warehousing.util.models.AtpFormulas;
import de.hybris.platform.warehousing.util.models.BaseStores;
import de.hybris.platform.warehousing.util.models.DeliveryModes;
import de.hybris.platform.warehousing.util.models.Orders;
import de.hybris.platform.warehousing.util.models.PointsOfService;
import de.hybris.platform.warehousing.util.models.Products;
import de.hybris.platform.warehousing.util.models.StockLevels;
import de.hybris.platform.warehousing.util.models.Users;
import de.hybris.platform.warehousing.util.models.Warehouses;

import javax.annotation.Resource;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class BaseSourcingIntegrationTest extends BaseWarehousingIntegrationTest
{
	@Resource
	protected SourcingService sourcingService;
	@Resource
	protected Orders orders;
	@Resource
	protected BaseStores baseStores;
	@Resource
	protected Warehouses warehouses;
	@Resource
	protected Addresses addresses;
	@Resource
	protected StockLevels stockLevels;
	@Resource
	protected DeliveryModes deliveryModes;
	@Resource
	protected PointsOfService pointsOfService;
	@Resource
	protected Products products;
	@Resource
	protected Users users;
	@Resource
	protected Asns asns;
	@Resource
	protected AtpFormulas atpFormulas;
	@Resource
	protected RestockConfigService restockConfigService;

    public BaseSourcingIntegrationTest() {
    }


    @Before
	public void setupShopper()
	{
		users.Nancy();
	}

	@Before
	public void setupBaseStore()
	{
		baseStores.NorthAmerica().setPointsOfService(Lists.newArrayList( //
				pointsOfService.Boston(), //
				pointsOfService.Montreal_Downtown() //
		));
		saveAll();
	}

	@After
	public void resetFactors()
	{
		modelService.remove(baseStores.NorthAmerica().getSourcingConfig());
	}

	/**
	 * Assert that the sourcing result selected the correct warehouse and sourced the correct quantity.
	 *
	 * @param results
	 * @param expectedWarehouse
	 * @param expectedAllocation
	 */
	protected void assertSourcingResultContents(final SourcingResults results, final WarehouseModel expectedWarehouse,
			final Map<ProductModel, Long> expectedAllocation)
	{
		final Optional<SourcingResult> sourcingResult = results.getResults().stream()
				.filter(result -> result.getWarehouse().getCode().equals(expectedWarehouse.getCode())).findFirst();

		assertTrue("No sourcing result with warehouse " + expectedWarehouse.getCode(), sourcingResult.isPresent());
		assertEquals(expectedWarehouse.getCode(), sourcingResult.get().getWarehouse().getCode());
		assertTrue(sourcingResult.get().getAllocation().entrySet().stream().allMatch(
				result -> expectedAllocation.get(result.getKey().getProduct()).equals(result.getValue())
		));
	}


	/**
	 * Sets the sourcing factors to use.
	 * @param baseStore
	 * @param allocation
	 * @param distance
	 * @param priority
	 */
	protected void setSourcingFactors(final BaseStoreModel baseStore, final int allocation, final int distance, final int priority, final int score)
	{
		final SourcingConfigModel sourcingConfig = baseStore.getSourcingConfig();
		sourcingConfig.setDistanceWeightFactor(distance);
		sourcingConfig.setAllocationWeightFactor(allocation);
		sourcingConfig.setPriorityWeightFactor(priority);
		sourcingConfig.setScoreWeightFactor(score);
		modelService.save(sourcingConfig);
	}

	/**
	 * refresh order.
	 * @param order
	 * @return
	 */
	protected OrderModel refreshOrder(OrderModel order)
	{
		final AbstractOrderEntryModel orderEntry = order.getEntries().get(0);
		//Refresh consignment entries to update quantityShipped
		orderEntry.getConsignmentEntries().stream().forEach(entry -> modelService.refresh(entry));
		return order;
	}
}

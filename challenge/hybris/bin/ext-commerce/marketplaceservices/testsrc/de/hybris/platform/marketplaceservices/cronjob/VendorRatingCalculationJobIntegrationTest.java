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
package de.hybris.platform.marketplaceservices.cronjob;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.marketplaceservices.model.CustomerVendorReviewModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;


@IntegrationTest
public class VendorRatingCalculationJobIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String VENDOR_CODE = "vendor1";
	private static final String WAREHOUSE_CODE = "warehouse1";
	private static final String CONSIGNMENT_CODE = "consignment1";
	private static final double DELTA = 0.0001;

	@Resource
	private VendorRatingCalculationJob vendorRatingCalculationJob;

	@Resource
	private ModelService modelService;

	VendorModel vendor;
	CustomerVendorReviewModel review1, review2;

	@Before
	public void setup() throws IOException, ImpExException
	{
		final CustomerModel customer = new CustomerModel();
		customer.setUid(UUID.randomUUID().toString());

		final AddressModel shippingAddress = new AddressModel();
		shippingAddress.setOwner(customer);

		vendor = new VendorModel();
		vendor.setCode(VENDOR_CODE);
		vendor.setActive(true);

		final WarehouseModel warehouse = new WarehouseModel();
		warehouse.setCode(WAREHOUSE_CODE);
		warehouse.setVendor(vendor);

		final ConsignmentModel consignment = new ConsignmentModel();
		consignment.setCode(CONSIGNMENT_CODE);
		consignment.setWarehouse(warehouse);
		consignment.setShippingAddress(shippingAddress);
		consignment.setStatus(ConsignmentStatus.SHIPPED);

		review1 = new CustomerVendorReviewModel();
		review1.setSatisfaction(1.0);
		review1.setDelivery(2.0);
		review1.setCommunication(3.0);
		review1.setConsignment(consignment);
		review1.setUser(customer);
		review1.setCreateDate(new Date());
		review1.setApprovalStatus(CustomerReviewApprovalType.APPROVED);
		review1.setVendor(vendor);


		review2 = new CustomerVendorReviewModel();
		review2.setSatisfaction(3.0);
		review2.setDelivery(5.0);
		review2.setCommunication(2.0);
		review2.setConsignment(consignment);
		review2.setUser(customer);
		review2.setCreateDate(new Date());
		review2.setApprovalStatus(CustomerReviewApprovalType.APPROVED);
		review2.setVendor(vendor);

		modelService.save(review1);
		modelService.save(review2);
	}

	@Test
	public void testVendorRatingCalculationJob()
	{
		final PerformResult result = vendorRatingCalculationJob.perform(new CronJobModel());
		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		modelService.refresh(vendor);
		assertEquals(2L, vendor.getReviewCount().longValue());
		assertEquals(2.0, vendor.getSatisfactionRating(), DELTA);
		assertEquals(3.5, vendor.getDeliveryRating(), DELTA);
		assertEquals(2.5, vendor.getCommunicationRating(), DELTA);
		assertEquals(2.6667, vendor.getAverageRating(), DELTA);
	}

}

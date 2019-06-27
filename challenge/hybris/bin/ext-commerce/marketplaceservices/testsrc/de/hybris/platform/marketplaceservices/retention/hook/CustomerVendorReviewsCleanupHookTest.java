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
package de.hybris.platform.marketplaceservices.retention.hook;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.marketplaceservices.model.CustomerVendorReviewModel;
import de.hybris.platform.marketplaceservices.vendor.daos.CustomerVendorReviewDao;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for {@link CustomerVendorReviewsCleanupHook}
 */
@IntegrationTest
public class CustomerVendorReviewsCleanupHookTest extends ServicelayerTransactionalTest
{
	private static final String VENDOR_CODE = "testvendor";
	private static final String WAREHOUSE_CODE = "testwarehouse";
	private static final String CONSIGNMENT_CODE = "testconsignment";
	private static final String REVIEW_COMMENT = "test review comment";


	@Resource(name = "customerVendorReviewsCleanupHook")
	private CustomerVendorReviewsCleanupHook hook;

	@Resource
	private ModelService modelService;

	@Resource
	private CommerceCommonI18NService commerceCommonI18NService;

	@Resource
	private CustomerVendorReviewDao customerVendorReviewDao;

	private VendorModel vendor;
	private CustomerVendorReviewModel review;
	private CustomerModel customer;
	private ConsignmentModel consignment;
	private LanguageModel language;

	@Before
	public void prepare()
	{
		customer = new CustomerModel();
		customer.setUid(UUID.randomUUID().toString());

		final AddressModel shippingAddress = new AddressModel();
		shippingAddress.setOwner(customer);

		vendor = new VendorModel();
		vendor.setCode(VENDOR_CODE);
		vendor.setActive(true);

		final WarehouseModel warehouse = new WarehouseModel();
		warehouse.setCode(WAREHOUSE_CODE);
		warehouse.setVendor(vendor);

		consignment = new ConsignmentModel();
		consignment.setCode(CONSIGNMENT_CODE);
		consignment.setWarehouse(warehouse);
		consignment.setShippingAddress(shippingAddress);
		consignment.setStatus(ConsignmentStatus.SHIPPED);

		language = commerceCommonI18NService.getCurrentLanguage();

		review = modelService.create(CustomerVendorReviewModel.class);
		review.setApprovalStatus(CustomerReviewApprovalType.APPROVED);
		review.setCommunication(5.0D);
		review.setDelivery(5.0D);
		review.setSatisfaction(5.0D);
		review.setConsignment(consignment);
		review.setCreateDate(Calendar.getInstance().getTime());
		review.setLanguage(language);
		review.setUser(customer);
		review.setVendor(vendor);
		modelService.save(review);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCleanupRelatedObjects_exception()
	{
		hook.cleanupRelatedObjects(null);
	}

	@Test
	public void testCleanupRelatedObjects()
	{
		Collection<CustomerVendorReviewModel> reviews = customerVendorReviewDao.findReviewsByUser(customer);
		Assert.assertEquals(1, reviews.size());
		Assert.assertEquals(customer.getUid(), reviews.iterator().next().getUser().getUid());

		hook.cleanupRelatedObjects(customer);
		reviews = customerVendorReviewDao.findReviewsByUser(customer);
		Assert.assertTrue(CollectionUtils.isEmpty(reviews));
	}
}

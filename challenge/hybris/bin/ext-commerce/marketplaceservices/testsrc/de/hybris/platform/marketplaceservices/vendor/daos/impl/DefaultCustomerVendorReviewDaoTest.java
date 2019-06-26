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
package de.hybris.platform.marketplaceservices.vendor.daos.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
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

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCustomerVendorReviewDaoTest extends ServicelayerTransactionalTest
{
	protected static final double DELTA = 0.001;
	protected static final String VENDOR_CODE = "vendor1";
	protected static final String WAREHOUSE_CODE = "warehouse1";
	protected static final String CONSIGNMENT_CODE = "consignment1";
	protected static final String REVIEW_COMMENT = "review comment";

	protected static final double SATISFACTION_VALUE = 1.5;
	protected static final double DELIVERY_VALUE = 2.5;
	protected static final double COMMUNICATION_VALUE = 4.0;

	@Resource(name = "customerVendorReviewDao")
	private CustomerVendorReviewDao customerVendorReviewDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "commerceCommonI18NService")
	private CommerceCommonI18NService commerceCommonI18NService;

	private VendorModel vendor;
	private CustomerVendorReviewModel approvedReview, pendingReview;
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
	}

	@Test
	public void testFindReviewsByVendor()
	{
		createApprovedReview();
		createPendingReview();

		final Collection<CustomerVendorReviewModel> reviews = customerVendorReviewDao.findReviewsForVendor(vendor);

		assertNotNull(reviews);
		assertTrue(reviews.size() == 1); // pending reviews are not included
		final CustomerVendorReviewModel myReview = reviews.iterator().next();
		assertEquals(myReview.getConsignment(), approvedReview.getConsignment());
		assertEquals(DELTA, myReview.getSatisfaction(), approvedReview.getSatisfaction());
		assertEquals(DELTA, myReview.getDelivery(), approvedReview.getDelivery());
		assertEquals(DELTA, myReview.getCommunication(), approvedReview.getCommunication());
	}

	@Test
	public void testPostedPendingReview()
	{
		createPendingReview();
		assertTrue(customerVendorReviewDao.postedReview(CONSIGNMENT_CODE, customer));
	}

	@Test
	public void testPostedApprovedReview()
	{
		createApprovedReview();
		assertTrue(customerVendorReviewDao.postedReview(CONSIGNMENT_CODE, customer));
	}

	@Test
	public void testFindPagedReviewsForVendor()
	{
		createVendorReview();
		final SearchPageData<CustomerVendorReviewModel> result = customerVendorReviewDao.findPagedReviewsForVendor(VENDOR_CODE,
				language, createPageableData());

		assertEquals(1, result.getResults().size());
		assertEquals(10, result.getPagination().getPageSize());
		assertEquals(1, result.getPagination().getTotalNumberOfResults());
		assertEquals(2, result.getSorts().size());
		assertEquals("byCreateDateAsc", result.getPagination().getSort());
	}

	@Test
	public void testFindReviewsByUser()
	{
		createVendorReview();

		final Collection<CustomerVendorReviewModel> results = customerVendorReviewDao.findReviewsByUser(customer);

		assertEquals(1, results.size());
		assertEquals(customer.getUid(), results.iterator().next().getUser().getUid());
	}

	protected PageableData createPageableData()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setSort("byCreateDateAsc");
		pageableData.setPageSize(10);
		return pageableData;
	}

	protected void createVendorReview()
	{
		approvedReview = new CustomerVendorReviewModel();
		approvedReview.setSatisfaction(3.0);
		approvedReview.setDelivery(4.0);
		approvedReview.setCommunication(5.0);
		approvedReview.setConsignment(consignment);
		approvedReview.setUser(customer);
		approvedReview.setCreateDate(new Date());
		approvedReview.setApprovalStatus(CustomerReviewApprovalType.APPROVED);
		approvedReview.setVendor(vendor);
		approvedReview.setLanguage(language);
		approvedReview.setComment(REVIEW_COMMENT);
		modelService.save(approvedReview);
	}

	protected void createApprovedReview()
	{
		approvedReview = new CustomerVendorReviewModel();
		approvedReview.setSatisfaction(1.0);
		approvedReview.setDelivery(2.0);
		approvedReview.setCommunication(3.0);
		approvedReview.setConsignment(consignment);
		approvedReview.setUser(customer);
		approvedReview.setCreateDate(new Date());
		approvedReview.setApprovalStatus(CustomerReviewApprovalType.APPROVED);
		approvedReview.setVendor(vendor);
		modelService.save(approvedReview);
	}

	protected void createPendingReview()
	{
		pendingReview = new CustomerVendorReviewModel();
		pendingReview.setSatisfaction(2.0);
		pendingReview.setDelivery(3.0);
		pendingReview.setCommunication(4.0);
		pendingReview.setConsignment(consignment);
		pendingReview.setUser(customer);
		pendingReview.setCreateDate(new Date());
		pendingReview.setVendor(vendor);
		modelService.save(pendingReview);
	}
}

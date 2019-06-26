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
package de.hybris.platform.customerreview.services;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.constants.CustomerReviewConstants;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * JUnit Tests for the Customer Review Service.
 *
 */
public class CustomerReviewServiceTest extends ServicelayerTransactionalTest
{
	@Resource
	private CustomerReviewService customerReviewService;
	@Resource
	private ProductService productService;
	@Resource
	private UserService userService;

	private UserModel userModel01;
	private UserModel userModel02;
	private ProductModel productModel01;
	private ProductModel productModel02;

	private String oldMinimalRating;
	private String oldMaximalRating;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		productModel01 = productService.getProductForCode("testProduct1");
		productModel02 = productService.getProductForCode("testProduct2");
		userModel01 = userService.getUserForUID("anonymous");
		userModel02 = userService.getCurrentUser();

		oldMinimalRating = Config.getParameter(CustomerReviewConstants.KEYS.MINIMAL_RATING);
		oldMaximalRating = Config.getParameter(CustomerReviewConstants.KEYS.MAXIMAL_RATING);

		// Set some configuration values to be used generally in the tests
		Config.setParameter(CustomerReviewConstants.KEYS.MINIMAL_RATING, String.valueOf(0));
		Config.setParameter(CustomerReviewConstants.KEYS.MAXIMAL_RATING, String.valueOf(4));
	}

	@After
	public void tearDown()
	{
		Config.setParameter(CustomerReviewConstants.KEYS.MINIMAL_RATING, oldMinimalRating);
		Config.setParameter(CustomerReviewConstants.KEYS.MAXIMAL_RATING, oldMaximalRating);
	}

	@Test
	public void testCustomerReviewService()
	{
		Assert.assertEquals(0, customerReviewService.getReviewsForCustomer(userModel01).size());

		Assert.assertEquals("no rating", 0, customerReviewService.getNumberOfReviews(productModel01).intValue());
		customerReviewService.createCustomerReview(Double.valueOf(1), "headline_anonymous", "comment_anonymous", userModel01,
				productModel01);
		Assert.assertEquals("rating 1", 1, customerReviewService.getNumberOfReviews(productModel01).intValue());
		customerReviewService.createCustomerReview(Double.valueOf(2), "headline_admin", "comment_admin", userModel02,
				productModel01);
		Assert.assertEquals("rating 2", 2, customerReviewService.getNumberOfReviews(productModel01).intValue());
		Assert.assertEquals("average rating 1.5", 1.5, customerReviewService.getAverageRating(productModel01).doubleValue(), 0.001);
		final List<CustomerReviewModel> reviews = customerReviewService.getReviewsForProduct(productModel01);
		final Set<String> comments = new TreeSet<String>();
		comments.add("comment_anonymous");
		comments.add("comment_admin");
		for (final CustomerReviewModel reviewModel : reviews)
		{
			Assert.assertTrue(comments.contains(reviewModel.getComment()));
		}

		Assert.assertEquals(1, customerReviewService.getReviewsForCustomer(userModel01).size());
		Assert.assertEquals(1, customerReviewService.getReviewsForCustomer(userModel02).size());

		customerReviewService
				.createCustomerReview(Double.valueOf(3d), "Test_headline", "Test_comment", userModel02, productModel02);
		Assert.assertEquals(2, customerReviewService.getReviewsForCustomer(userModel02).size());
	}



}

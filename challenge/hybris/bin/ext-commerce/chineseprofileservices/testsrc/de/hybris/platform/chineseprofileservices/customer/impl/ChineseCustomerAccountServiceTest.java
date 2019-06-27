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
package de.hybris.platform.chineseprofileservices.customer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chineseprofileservices.customer.ChineseCustomerAccountService;
import de.hybris.platform.chineseprofileservices.data.VerificationData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.daos.UserDao;

import java.util.Calendar;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ChineseCustomerAccountServiceTest extends ServicelayerTransactionalTest
{

	@Resource(name = "chineseCustomerAccountService")
	private ChineseCustomerAccountService customerAccountService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "chineseUserDao")
	private UserDao userDao;

	private VerificationData data;

	private CustomerModel customer;

	@Before
	public void prepare()
	{
		customer = new CustomerModel();
		customer.setLoginDisabled(false);
		customer.setUid("test@gmail.com");
		customer.setMobileNumber("12345678910");
		customer.setEncodedPassword("123456123456");
		modelService.save(customer);

		data = new VerificationData();
		data.setMobileNumber("13800000000");
		data.setTime(Calendar.getInstance().getTime());
	}

	@Test
	public void test_generateVerificationCode()
	{
		final String code = customerAccountService.generateVerificationCode();
		assertEquals(4, code.length());
	}

	@Test
	public void test_updateMobileNumber()
	{
		customer.setMobileNumber("13800000000");
		customerAccountService.updateMobileNumber(customer);

		final CustomerModel model = (CustomerModel) userDao.findUserByUID("13800000000");
		assertEquals("13800000000", model.getMobileNumber());
	}

	@Test
	public void test_getCustomerForMobileNumber()
	{
		customer.setMobileNumber("13800000000");
		modelService.save(customer);
		final Optional<CustomerModel> customer = customerAccountService.getCustomerForMobileNumber("13800000000");
		assertEquals("13800000000", customer.get().getMobileNumber());
	}

	@Test
	public void test_getUnknowCustomerForMobileNumber()
	{
		modelService.remove(customer);
		final Optional<CustomerModel> customer = customerAccountService.getCustomerForMobileNumber("13800000000");
		assertFalse(customer.isPresent());
	}
}

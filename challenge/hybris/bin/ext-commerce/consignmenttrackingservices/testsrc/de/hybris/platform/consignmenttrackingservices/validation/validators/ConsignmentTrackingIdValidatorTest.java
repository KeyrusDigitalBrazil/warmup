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
package de.hybris.platform.consignmenttrackingservices.validation.validators;

import de.hybris.platform.consignmenttrackingservices.model.CarrierModel;
import de.hybris.platform.consignmenttrackingservices.service.ConsignmentTrackingService;
import de.hybris.platform.consignmenttrackingservices.validation.annotations.ConsignmentTrackingIdValid;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import javax.validation.ConstraintValidatorContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
/**
 * 
 */
public class ConsignmentTrackingIdValidatorTest
{

	@Mock
	ConsignmentTrackingService consignmentTrackingService;

	@Mock
	ConsignmentTrackingIdValid constraintAnnotation;
	
	@Mock
	ConstraintValidatorContext context;

	@Mock
	ConsignmentModel consignment;

	@Mock
	CarrierModel carriermodel;


	private ConsignmentTrackingIdValidator ctvalidator;

	private static final String TRACKINGID = "id";

	private static final String CARRIERDETAIL = "carrierdetail";

	@Before
	public void init()

	{
		MockitoAnnotations.initMocks(this);
		ctvalidator = new ConsignmentTrackingIdValidator();
		ctvalidator.initialize(constraintAnnotation);
		consignment.setTrackingID(TRACKINGID);


	}

	@Test
	public void testIsVaild()
	{
		Assert.assertFalse(ctvalidator.isValid(new Object(), context));

		Assert.assertTrue(ctvalidator.isValid(consignment, context));

	}

	@Test
	public void testCheckConsignmentInfo_Details_Is_Null()
	{
		Mockito.when(consignment.getCarrierDetails()).thenReturn(null);

		Assert.assertTrue(ctvalidator.isValid(consignment, context));
	}

	@Test
	public void testCheckConsignmentInfo_Details_Not_Null()
	{
		Mockito.when(consignment.getCarrierDetails()).thenReturn(carriermodel);
		Mockito.when(carriermodel.getCode()).thenReturn("Default");

		Assert.assertTrue(ctvalidator.isValid(consignment, context));
	}

}

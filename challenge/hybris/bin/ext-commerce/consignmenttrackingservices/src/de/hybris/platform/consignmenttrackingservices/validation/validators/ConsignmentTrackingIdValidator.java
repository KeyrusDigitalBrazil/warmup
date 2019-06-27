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

import de.hybris.platform.consignmenttrackingservices.adaptors.CarrierAdaptor;
import de.hybris.platform.consignmenttrackingservices.service.ConsignmentTrackingService;
import de.hybris.platform.consignmenttrackingservices.validation.annotations.ConsignmentTrackingIdValid;
import de.hybris.platform.core.Registry;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Validates the correctness of the input trackingId of consignment.This validator delegates the actual validation
 * process to actual implemented carrier.
 */
public class ConsignmentTrackingIdValidator implements ConstraintValidator<ConsignmentTrackingIdValid, Object>
{
	public static final String DEFAULT_CARRIER_CODE = "Default";

	private static final Logger LOG = Logger.getLogger(ConsignmentTrackingIdValidator.class.getName());

	private ConsignmentTrackingService consignmentTrackingService;

	@Override
	public void initialize(final ConsignmentTrackingIdValid constraintAnnotation)
	{
		consignmentTrackingService = (ConsignmentTrackingService) Registry.getApplicationContext().getBean(
				"consignmentTrackingService");
	}

	/**
	 * validate the correctness of consignment tracking ID
	 *
	 * @param Object
	 *           value should be an instance of ConsignmentModel passed down from backoffice
	 * @param ConstraintValidatorContext
	 *           context is not used here
	 * @return boolean indicating if the trackingID from consignmentModel is valid
	 */
	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context)
	{
		if (value instanceof ConsignmentModel)
		{
			final Map<String, CarrierAdaptor> adaptors = consignmentTrackingService.getAllCarrierAdaptors();
			if (MapUtils.isEmpty(adaptors))
			{
				return true;
			}
			final ConsignmentModel consignment = (ConsignmentModel) value;

			return checkConsignmentInfo(consignment, adaptors);
		}
		else
		{
			LOG.error("Provided object is not an instance of ConsignmentModel: " + value.getClass());
			return false;
		}
	}

	/**
	 * Core logic to check the correctness of consignment.
	 *
	 * @param consignment
	 *           The instance of consignment
	 * @param adaptors
	 *           Adaptor to get carrier info.
	 * @return The check result.
	 */
	protected boolean checkConsignmentInfo(final ConsignmentModel consignment, final Map<String, CarrierAdaptor> adaptors)
	{
		if (StringUtils.isEmpty(consignment.getTrackingID()) && consignment.getCarrierDetails() == null)
		{
			return true;
		}
		else if (consignment.getCarrierDetails() != null)
		{
			final String carrierCode = consignment.getCarrierDetails().getCode();
			if (DEFAULT_CARRIER_CODE.equals(carrierCode))
			{
				return true;
			}
			final CarrierAdaptor carrierAdaptor = adaptors.get(carrierCode);
			if (carrierAdaptor == null)
			{
				return false;
			}
			return carrierAdaptor.isTrackingIdValid(consignment.getTrackingID());
		}
		return true;
	}
}

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
package de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.request;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.acceleratorservices.payment.data.CustomerBillToData;
import de.hybris.platform.acceleratorservices.payment.data.CustomerShipToData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


public class CustomerInfoRequestPopulator extends AbstractRequestPopulator<CreateSubscriptionRequest, PaymentData>
{
	private static final String PAYMENT_DATA_TARGET_NOT_NULL_MGS = "Parameter [PaymentData] target cannot be null";

	@Override
	public void populate(final CreateSubscriptionRequest source, final PaymentData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [CreateSubscriptionRequest] source cannot be null");
		validateParameterNotNull(target, PAYMENT_DATA_TARGET_NOT_NULL_MGS);

		populateBillingInfo(source.getCustomerBillToData(), target);
		populateShippingInfo(source.getCustomerShipToData(), target);
	}

	protected void populateBillingInfo(final CustomerBillToData source, final PaymentData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [CustomerBillToData] source cannot be null");
		validateParameterNotNull(target, PAYMENT_DATA_TARGET_NOT_NULL_MGS);

		addRequestQueryParam(target, "billTo_city", source.getBillToCity());
		addRequestQueryParam(target, "billTo_company", source.getBillToCompany());
		addRequestQueryParam(target, "billTo_companyTaxID", source.getBillToCompanyTaxId());
		addRequestQueryParam(target, "billTo_country", source.getBillToCountry());
		addRequestQueryParam(target, "billTo_customerID", source.getBillToCustomerIdRef());
		addRequestQueryParam(target, "billTo_dateOfBirth", source.getBillToDateOfBirth());
		addRequestQueryParam(target, "billTo_email", source.getBillToEmail());
		addRequestQueryParam(target, "billTo_firstName", source.getBillToFirstName());
		addRequestQueryParam(target, "billTo_lastName", source.getBillToLastName());
		addRequestQueryParam(target, "billTo_phoneNumber", source.getBillToPhoneNumber());
		addRequestQueryParam(target, "billTo_postalCode", source.getBillToPostalCode());
		addRequestQueryParam(target, "billTo_state", source.getBillToState());
		addRequestQueryParam(target, "billTo_street1", source.getBillToStreet1());
		addRequestQueryParam(target, "billTo_street2", source.getBillToStreet2());
		addRequestQueryParam(target, "billTo_titleCode", source.getBillToTitleCode());
	}

	protected void populateShippingInfo(final CustomerShipToData source, final PaymentData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [CustomerShipToData] source cannot be null");
		validateParameterNotNull(target, PAYMENT_DATA_TARGET_NOT_NULL_MGS);

		addRequestQueryParam(target, "shipTo_city", source.getShipToCity());
		addRequestQueryParam(target, "shipTo_company", source.getShipToCompany());
		addRequestQueryParam(target, "shipTo_country", source.getShipToCountry());
		addRequestQueryParam(target, "shipTo_firstName", source.getShipToFirstName());
		addRequestQueryParam(target, "shipTo_lastName", source.getShipToLastName());
		addRequestQueryParam(target, "shipTo_phoneNumber", source.getShipToPhoneNumber());
		addRequestQueryParam(target, "shipTo_postalCode", source.getShipToPostalCode());
		addRequestQueryParam(target, "shipTo_shippingMethod", source.getShipToShippingMethod());
		addRequestQueryParam(target, "shipTo_state", source.getShipToState());
		addRequestQueryParam(target, "shipTo_street1", source.getShipToStreet1());
		addRequestQueryParam(target, "shipTo_street2", source.getShipToStreet2());
	}
}

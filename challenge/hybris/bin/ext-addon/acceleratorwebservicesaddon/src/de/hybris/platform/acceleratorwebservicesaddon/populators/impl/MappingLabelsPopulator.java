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
package de.hybris.platform.acceleratorwebservicesaddon.populators.impl;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.request.AbstractRequestPopulator;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.HashMap;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_COMBINED_EXPIRY_DATE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_COMBINED_SEPARATOR_EXPIRY_DATE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_ACCOUNT_HOLDER_NAME;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_BILLTO_CITY;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_BILLTO_COUNTRY;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_BILLTO_FIRSTNAME;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_BILLTO_LASTNAME;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_BILLTO_POSTALCODE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_BILLTO_STREET1;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_CARD_CVN;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_CARD_EXPIRATION_MONTH;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_CARD_EXPIRATION_YEAR;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_CARD_EXPIRY_DATE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_CARD_NUMBER;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_CARD_TYPE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_AMOUNT;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_CARD_NUMBER;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_CURRENCY;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_DECISION;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_REASON_CODE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_SUBSCRIPTION_ID;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_SUBSCRIPTION_ID_PUBLIC_SIGNATURE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_USES_PUBLIC_SIGNATURE;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Default implementation of mapping labels populators
 */
public class MappingLabelsPopulator extends AbstractRequestPopulator<CreateSubscriptionRequest, PaymentData>
{
	public static final String HYBRIS_ACCOUNT_HOLDER_NAME = "hybris_account_holder_name";
	public static final String HYBRIS_CARD_TYPE = "hybris_card_type";
	public static final String HYBRIS_CARD_NUMBER = "hybris_card_number";
	public static final String HYBRIS_CARD_EXPIRATION_MONTH = "hybris_card_expiration_month";
	public static final String HYBRIS_CARD_EXPIRATION_YEAR = "hybris_card_expiration_year";
	public static final String HYBRIS_CARD_EXPIRY_DATE = "hybris_card_expiry_date";
	public static final String HYBRIS_CARD_CVN = "hybris_card_cvn";
	public static final String HYBRIS_COMBINED_EXPIRY_DATE = "hybris_combined_expiry_date";
	public static final String HYBRIS_SEPARATOR_EXPIRY_DATE = "hybris_separator_expiry_date";
	public static final String HYBRIS_BILLTO_COUNTRY = "hybris_billTo_country";
	public static final String HYBRIS_BILLTO_FIRSTNAME = "hybris_billTo_firstname";
	public static final String HYBRIS_BILLTO_LASTNAME = "hybris_billTo_lastname";
	public static final String HYBRIS_BILLTO_STREET1 = "hybris_billTo_street1";
	public static final String HYBRIS_BILLTO_CITY = "hybris_billTo_city";
	public static final String HYBRIS_BILLTO_POSTALCODE = "hybris_billTo_postalcode";
	public static final String HYBRIS_SOP_DECISION = "hybris_sop_decision";
	public static final String HYBRIS_SOP_AMOUNT = "hybris_sop_amount";
	public static final String HYBRIS_SOP_CURRENCY = "hybris_sop_currency";
	public static final String HYBRIS_SOP_REASON_CODE = "hybris_sop_reason_code";
	public static final String HYBRIS_SOP_CARD_NUMBER = "hybris_sop_card_number";
	public static final String HYBRIS_SOP_SUBSCRIPTION_ID = "hybris_sop_subscriptionID";
	public static final String HYBRIS_SOP_USES_PUBLIC_SIGNATURE = "hybris_sop_uses_public_signature";
	public static final String HYBRIS_SOP_PUBLIC_SIGNATURE = "hybris_sop_public_signature";

	private ConfigurationService configurationService;
	private BaseStoreService baseStoreService;

	@Override
	public void populate(final CreateSubscriptionRequest source, final PaymentData target)
	{
		validateParameterNotNull(source, "Parameter [CreateSubscriptionRequest] source cannot be null");
		validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");

		Configuration config = getConfigurationService().getConfiguration();
		validateParameterNotNull(config, "Configuration cannot be null");

		String paymentProvider = getBaseStoreService().getBaseStoreForUid(source.getSiteName()).getPaymentProvider().toLowerCase();
		target.setMappingLabels(new HashMap<>());

		populateCardInfoLabels(target, paymentProvider, config);
		populateAddressLabels(target, paymentProvider, config);
		populateSopLabels(target, paymentProvider, config);
	}

	/**
	 * Populates labels related to card infos
	 *
	 * @param target
	 * 		{@link PaymentData} to be updated with card infos labels
	 * @param paymentProvider
	 * 		The payment provider used by the basestore
	 * @param config
	 * 		The config containing all labels
	 */
	private void populateCardInfoLabels(final PaymentData target, final String paymentProvider, final Configuration config)
	{
		target.getMappingLabels()
				.put(HYBRIS_ACCOUNT_HOLDER_NAME, config.getString(paymentProvider + PAYMENT_LABEL_ACCOUNT_HOLDER_NAME));
		target.getMappingLabels().put(HYBRIS_CARD_TYPE, config.getString(paymentProvider + PAYMENT_LABEL_CARD_TYPE));
		target.getMappingLabels().put(HYBRIS_CARD_CVN, config.getString(paymentProvider + PAYMENT_LABEL_CARD_CVN));
		target.getMappingLabels()
				.put(HYBRIS_CARD_EXPIRATION_MONTH, config.getString(paymentProvider + PAYMENT_LABEL_CARD_EXPIRATION_MONTH));
		target.getMappingLabels()
				.put(HYBRIS_CARD_EXPIRATION_YEAR, config.getString(paymentProvider + PAYMENT_LABEL_CARD_EXPIRATION_YEAR));
		target.getMappingLabels().put(HYBRIS_CARD_EXPIRY_DATE, config.getString(paymentProvider + PAYMENT_LABEL_CARD_EXPIRY_DATE));
		target.getMappingLabels().put(HYBRIS_CARD_NUMBER, config.getString(paymentProvider + PAYMENT_LABEL_CARD_NUMBER));
		target.getMappingLabels()
				.put(HYBRIS_COMBINED_EXPIRY_DATE, config.getString(paymentProvider + PAYMENT_COMBINED_EXPIRY_DATE));
		target.getMappingLabels()
				.put(HYBRIS_SEPARATOR_EXPIRY_DATE, config.getString(paymentProvider + PAYMENT_COMBINED_SEPARATOR_EXPIRY_DATE));
	}

	/**
	 * Populates labels related to the billing address
	 *
	 * @param target
	 * 		{@link PaymentData} to be updated with billing address labels
	 * @param paymentProvider
	 * 		The payment provider used by the basestore
	 * @param config
	 * 		The config containing all labels
	 */
	private void populateAddressLabels(final PaymentData target, final String paymentProvider, final Configuration config)
	{
		target.getMappingLabels().put(HYBRIS_BILLTO_COUNTRY, config.getString(paymentProvider + PAYMENT_LABEL_BILLTO_COUNTRY));
		target.getMappingLabels().put(HYBRIS_BILLTO_FIRSTNAME, config.getString(paymentProvider + PAYMENT_LABEL_BILLTO_FIRSTNAME));
		target.getMappingLabels().put(HYBRIS_BILLTO_LASTNAME, config.getString(paymentProvider + PAYMENT_LABEL_BILLTO_LASTNAME));
		target.getMappingLabels().put(HYBRIS_BILLTO_STREET1, config.getString(paymentProvider + PAYMENT_LABEL_BILLTO_STREET1));
		target.getMappingLabels().put(HYBRIS_BILLTO_CITY, config.getString(paymentProvider + PAYMENT_LABEL_BILLTO_CITY));
		target.getMappingLabels()
				.put(HYBRIS_BILLTO_POSTALCODE, config.getString(paymentProvider + PAYMENT_LABEL_BILLTO_POSTALCODE));
	}

	/**
	 * Populates labels related to silent order post process
	 *
	 * @param target
	 * 		{@link PaymentData} to be updated with silent order post labels
	 * @param paymentProvider
	 * 		The payment provider used by the basestore
	 * @param config
	 * 		The config containing all labels
	 */
	private void populateSopLabels(final PaymentData target, final String paymentProvider, final Configuration config)
	{
		target.getMappingLabels().put(HYBRIS_SOP_DECISION, config.getString(paymentProvider + PAYMENT_LABEL_SOP_DECISION));
		target.getMappingLabels().put(HYBRIS_SOP_AMOUNT, config.getString(paymentProvider + PAYMENT_LABEL_SOP_AMOUNT));
		target.getMappingLabels().put(HYBRIS_SOP_CURRENCY, config.getString(paymentProvider + PAYMENT_LABEL_SOP_CURRENCY));
		target.getMappingLabels().put(HYBRIS_SOP_REASON_CODE, config.getString(paymentProvider + PAYMENT_LABEL_SOP_REASON_CODE));
		target.getMappingLabels().put(HYBRIS_SOP_CARD_NUMBER, config.getString(paymentProvider + PAYMENT_LABEL_SOP_CARD_NUMBER));
		target.getMappingLabels()
				.put(HYBRIS_SOP_SUBSCRIPTION_ID, config.getString(paymentProvider + PAYMENT_LABEL_SOP_SUBSCRIPTION_ID));
		target.getMappingLabels()
				.put(HYBRIS_SOP_USES_PUBLIC_SIGNATURE, config.getString(paymentProvider + PAYMENT_USES_PUBLIC_SIGNATURE));
		target.getMappingLabels().put(HYBRIS_SOP_PUBLIC_SIGNATURE,
				config.getString(paymentProvider + PAYMENT_LABEL_SOP_SUBSCRIPTION_ID_PUBLIC_SIGNATURE));
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}
}


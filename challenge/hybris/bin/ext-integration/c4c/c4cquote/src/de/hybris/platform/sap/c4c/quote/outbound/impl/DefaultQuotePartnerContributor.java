/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.c4c.quote.outbound.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.MapUtils;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.c4c.quote.constants.QuoteCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerRoles;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;


/**
 *
 */
public class DefaultQuotePartnerContributor implements RawItemContributor<QuoteModel>
{

	private static final Set<String> COLUMNS = new HashSet<>(Arrays.asList(QuoteCsvColumns.QUOTE_ID,
			QuoteCsvColumns.PARTNER_ROLE_CODE, QuoteCsvColumns.PARTNER_CODE));

	private B2BUnitService<B2BUnitModel, CustomerModel> b2bUnitService;

	@Override
	public Set<String> getColumns()
	{
		return COLUMNS;
	}

	@Override
	public List<Map<String, Object>> createRows(final QuoteModel quote)
	{
		final Map<String, Object> soldToRow = createPartnerRow(quote, PartnerRoles.SOLD_TO, soldToFromQuote(quote));
		final Map<String, Object> contactRow = createPartnerRow(quote, PartnerRoles.CONTACT, contactFromQuote(quote));
		final Map<String, Object> shipToRow = createPartnerRow(quote, PartnerRoles.SHIP_TO,
				getPartnerId(quote, PartnerRoles.SHIP_TO));
		final Map<String, Object> billToRow = createPartnerRow(quote, PartnerRoles.BILL_TO,
				getPartnerId(quote, PartnerRoles.BILL_TO));

		final List<Map<String, Object>> result = new ArrayList<>(3);

		if (!MapUtils.isEmpty(soldToRow))
		{
			result.add(soldToRow);
		}
		if (!MapUtils.isEmpty(contactRow))
		{
			result.add(contactRow);
		}
		if (!MapUtils.isEmpty(shipToRow))
		{
			result.add(shipToRow);
		}
		if (!MapUtils.isEmpty(billToRow))
		{
			result.add(billToRow);
		}
		return result;
	}

	/**
	 * @param shipTo
	 * @param quote
	 *
	 */
	private String getPartnerId(final QuoteModel quote, final PartnerRoles partnerRole)
	{
		final AddressModel address = addressForPartnerRole(quote, partnerRole);
		if (address != null)
		{
			final String sapCustomer = address.getSapCustomerID();
			if (sapCustomer == null || sapCustomer.isEmpty())
			{
				return soldToFromQuote(quote);
			}
			else
			{
				return sapCustomer;
			}
		}

		return soldToFromQuote(quote);
	}

	protected AddressModel addressForPartnerRole(final QuoteModel quote, final PartnerRoles partnerRole)
	{
		AddressModel result = null;
		if (partnerRole == PartnerRoles.SHIP_TO)
		{
			result = quote.getDeliveryAddress();
		}
		else if (partnerRole == PartnerRoles.BILL_TO)
		{
			result = quote.getPaymentAddress();
		}
		return result;
	}

	protected String contactFromQuote(final QuoteModel quote)
	{
		return ((B2BCustomerModel) quote.getUser()).getCustomerID();
	}

	protected String soldToFromQuote(final QuoteModel quote)
	{
		final B2BCustomerModel customer = (B2BCustomerModel) quote.getUser();
		final B2BUnitModel defaultB2BUnit = customer.getDefaultB2BUnit();
		final CompanyModel rootUnit = getB2bUnitService().getRootUnit(defaultB2BUnit);
		return rootUnit.getUid();
	}

	protected Map<String, Object> createPartnerRow(final QuoteModel quote, final PartnerRoles partnerRole, final String partnerId)
	{
		validateMandatoryParameters(partnerRole, partnerId);
		final Map<String, Object> row = new HashMap<>();
		row.put(QuoteCsvColumns.QUOTE_ID, quote.getCode());
		row.put(QuoteCsvColumns.PARTNER_ROLE_CODE, partnerRole.getCode());
		row.put(QuoteCsvColumns.PARTNER_CODE, partnerId);
		return row;
	}

	protected void validateMandatoryParameters(PartnerRoles partnerRole, String partnerId)
	{
		validateParameterNotNullStandardMessage(QuoteCsvColumns.PARTNER_ROLE_CODE, partnerRole.getCode());
		validateParameterNotNullStandardMessage(QuoteCsvColumns.PARTNER_CODE, partnerId);
	}

	@SuppressWarnings("javadoc")
	public B2BUnitService<B2BUnitModel, CustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@SuppressWarnings("javadoc")
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, CustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}

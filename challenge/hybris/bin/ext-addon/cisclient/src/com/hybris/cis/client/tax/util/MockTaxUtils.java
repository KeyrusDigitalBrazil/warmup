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
package com.hybris.cis.client.tax.util;

import com.hybris.cis.client.shared.models.CisDecision;

import com.hybris.cis.client.shared.models.CisLineItem;
import com.hybris.cis.client.shared.models.CisOrder;
import com.hybris.cis.client.tax.models.CisTaxDoc;
import com.hybris.cis.client.tax.models.CisTaxLine;
import com.hybris.cis.client.tax.models.CisTaxValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MockTaxUtils
{

	public CisTaxDoc getCisTaxDoc(final CisOrder order)
	{
		final List<CisTaxLine> taxLines = new ArrayList<CisTaxLine>();

		final CisTaxDoc taxDoc = new CisTaxDoc();
		taxDoc.setRoundingMode(RoundingMode.HALF_EVEN);
		taxDoc.setDecision(CisDecision.ACCEPT);
		taxDoc.setDate(new Date());
		taxDoc.setId(order.getId());

		BigDecimal totalTax = BigDecimal.ZERO;
		BigDecimal subTotal = BigDecimal.ZERO;

		for (final CisLineItem line : order.getLineItems())
		{
			taxLines.add(getTaxLine(line));
			subTotal = subTotal.add(line.getUnitPrice());
		}

		taxDoc.setTaxLines(taxLines);
		taxDoc.setSubTotal(subTotal);

		for (final CisTaxLine line : taxLines)
		{
			totalTax = totalTax.add(line.getTotalTax());
		}

		taxDoc.setTotal(subTotal.add(totalTax));
		taxDoc.setTotalTax(totalTax);

		return taxDoc;
	}

	public CisTaxLine getTaxLine(final CisLineItem lineItem)
	{
		final CisTaxLine taxLine = new CisTaxLine();
		final CisTaxValue federalTaxValue = new CisTaxValue();
		final CisTaxValue stateTaxValue = new CisTaxValue();
		final CisTaxValue cityTaxValue = new CisTaxValue();

		final List<CisTaxValue> taxValues = new ArrayList<CisTaxValue>();
		taxLine.setRoundingMode(RoundingMode.HALF_EVEN);
		taxLine.setId(lineItem.getId().toString());

		final BigDecimal taxLineAmount = BigDecimal.valueOf(lineItem.getQuantity().intValue()).multiply(lineItem.getUnitPrice());
		final BigDecimal fivePercentAmount = taxLineAmount.multiply(BigDecimal.valueOf(0.05));


		federalTaxValue.setJurisdiction("FEDERAL");
		federalTaxValue.setLevel("FEDERAL");
		federalTaxValue.setRate(BigDecimal.valueOf(5));
		federalTaxValue.setValue(fivePercentAmount);

		stateTaxValue.setJurisdiction("STATE");
		stateTaxValue.setLevel("STATE");
		stateTaxValue.setRate(BigDecimal.valueOf(0));
		stateTaxValue.setValue(BigDecimal.valueOf(0));

		cityTaxValue.setJurisdiction("CITY");
		cityTaxValue.setLevel("CITY");
		cityTaxValue.setRate(BigDecimal.valueOf(0));
		cityTaxValue.setValue(BigDecimal.valueOf(0));


		taxValues.add(stateTaxValue);
		taxValues.add(cityTaxValue);
		taxValues.add(federalTaxValue);
		taxLine.setTaxValues(taxValues);
		taxLine.setTotalTax(fivePercentAmount);

		return taxLine;
	}

}

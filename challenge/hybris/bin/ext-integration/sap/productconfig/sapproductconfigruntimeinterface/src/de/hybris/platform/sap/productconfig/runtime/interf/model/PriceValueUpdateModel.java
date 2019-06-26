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
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Pojo for storing characteristic specific price related data. Used for the asynchronous price request where the
 * configuration model is already present and we use this model to read prices attached to the characteristics values
 */
public class PriceValueUpdateModel
{
	private CsticQualifier csticQualifier;
	private List<String> selectedValues;
	private Map<String, PriceModel> valuePrices;
	private boolean showDeltaPrices;


	/**
	 * @return ID of characteristic
	 */
	public CsticQualifier getCsticQualifier()
	{
		return csticQualifier;
	}

	/**
	 * @param csticQualifier
	 *           ID of characteristic
	 */
	public void setCsticQualifier(final CsticQualifier csticQualifier)
	{
		this.csticQualifier = csticQualifier;
	}

	/**
	 * @return List of selected values, used for delta price calculation
	 */
	public List<String> getSelectedValues()
	{
		return Optional.ofNullable(selectedValues).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	/**
	 * @param selectedValues
	 *           List of selected values, used for delta price calculation
	 */
	public void setSelectedValues(final List<String> selectedValues)
	{
		this.selectedValues = Optional.ofNullable(selectedValues).map(List::stream).orElseGet(Stream::empty)
				.collect(Collectors.toList());
	}

	/**
	 * @return List of prices per value. Those prices can represent delta prices (comparing to the currently selectedProductConfigMessageImplTest
	 *         value) or absolute ones
	 */
	public Map<String, PriceModel> getValuePrices()
	{
		return valuePrices;
	}

	/**
	 * @param valuePrices
	 *           List of prices per value. Those prices can represent delta prices (comparing to the currently selected
	 *           value) or absolute ones
	 */
	public void setValuePrices(final Map<String, PriceModel> valuePrices)
	{
		this.valuePrices = valuePrices;
	}

	/**
	 * @return true if delta prices are returned; false if absolute value prices are returned
	 */
	public boolean isShowDeltaPrices()
	{
		return showDeltaPrices;
	}

	/**
	 * @param showDeltaPrices
	 *           set flag whether delta prices are returned
	 */
	public void setShowDeltaPrices(final boolean showDeltaPrices)
	{
		this.showDeltaPrices = showDeltaPrices;
	}

}

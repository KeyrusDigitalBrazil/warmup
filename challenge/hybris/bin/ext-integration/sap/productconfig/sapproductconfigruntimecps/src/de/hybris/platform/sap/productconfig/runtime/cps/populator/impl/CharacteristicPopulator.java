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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Responsible to populate characteristics
 */
public class CharacteristicPopulator implements ContextualPopulator<CPSCharacteristic, CsticModel, MasterDataContext>
{
	protected static final String INTERVAL_TYPE_UNCONSTRAINED = "0";
	private ContextualConverter<CPSValue, CsticValueModel, MasterDataContext> valueConverter;
	private ContextualConverter<CPSPossibleValue, CsticValueModel, MasterDataContext> possibleValueConverter;

	/**
	 * @return the valueConverter
	 */
	public ContextualConverter<CPSValue, CsticValueModel, MasterDataContext> getValueConverter()
	{
		return valueConverter;
	}


	/**
	 * @param valueConverter
	 *           the valueConverter to set
	 */
	public void setValueConverter(final ContextualConverter<CPSValue, CsticValueModel, MasterDataContext> valueConverter)
	{
		this.valueConverter = valueConverter;
	}


	/**
	 * @return the possibleValueConverter
	 */
	public ContextualConverter<CPSPossibleValue, CsticValueModel, MasterDataContext> getPossibleValueConverter()
	{
		return possibleValueConverter;
	}


	/**
	 * @param possibleValueConverter
	 *           the possibleValueConverter to set
	 */
	public void setPossibleValueConverter(
			final ContextualConverter<CPSPossibleValue, CsticValueModel, MasterDataContext> possibleValueConverter)
	{
		this.possibleValueConverter = possibleValueConverter;
	}


	@Override
	public void populate(final CPSCharacteristic source, final CsticModel target, final MasterDataContext ctxt)
	{
		populateCoreAttributes(source, target);
		populateInstanceReference(source, target);

		populateValues(source, target, ctxt);
		populatePossibleValues(source, target, ctxt);

	}

	protected void populateInstanceReference(final CPSCharacteristic source, final CsticModel target)
	{
		final CPSItem parentItem = source.getParentItem();
		if (parentItem == null)
		{
			throw new IllegalStateException("Characteristic does not carry a parent: " + source.getId());
		}
		target.setInstanceName(parentItem.getKey());
		target.setInstanceId(parentItem.getId());

	}


	protected void populateValues(final CPSCharacteristic source, final CsticModel target, final MasterDataContext ctxt)
	{
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		if (source.getValues() != null)
		{
			for (final CPSValue value : source.getValues())
			{
				final CsticValueModel valueModel = getValueConverter().convertWithContext(value, ctxt);
				assignedValues.add(valueModel);
			}

			if (source.getPossibleValues() != null)
			{
				final List<String> possibleValues = source.getPossibleValues().stream().map(CPSPossibleValue::getValueLow)
						.collect(Collectors.toList());

				Collections.sort(assignedValues,
						Comparator.comparing(csticValue -> possibleValues.contains(csticValue.getName())
								? Integer.valueOf(possibleValues.indexOf(csticValue.getName()))
								: Integer.valueOf(assignedValues.size() - 1)));
			}

		}
		target.setAssignedValuesWithoutCheckForChange(assignedValues);
	}

	protected void populatePossibleValues(final CPSCharacteristic source, final CsticModel target, final MasterDataContext ctxt)
	{
		final List<CsticValueModel> possibleValues = new ArrayList<>();
		boolean hasOneInterval = false;
		final boolean constrained = isConstrained(source);
		target.setConstrained(constrained);
		final boolean allowsAdditionalValue = allowsAdditionalValue(source);
		target.setAllowsAdditionalValues(allowsAdditionalValue);
		if (source.getPossibleValues() != null)
		{
			hasOneInterval = fillPossibleValues(source, possibleValues, ctxt);
		}


		final List<CsticValueModel> assignedValues = target.getAssignedValues();
		if (mergeAssignedWithPossibleValues(constrained, allowsAdditionalValue, assignedValues))
		{
			for (final CsticValueModel assignedValue : assignedValues)
			{
				if (!possibleValues.contains(assignedValue))
				{
					possibleValues.add(assignedValue);
				}
			}
		}
		target.setAssignableValues(possibleValues);
		target.setIntervalInDomain(hasOneInterval);
	}


	protected boolean mergeAssignedWithPossibleValues(final boolean constrained, final boolean allowsAdditionalValue,
			final List<CsticValueModel> assignedValues)
	{
		return (constrained || allowsAdditionalValue) && CollectionUtils.isNotEmpty(assignedValues);
	}

	/**
	 * Check whether a characteristics is domain constrained. We don't consider attribute
	 * {@link CPSPossibleValue#isSelectable()} because we are interested in the _static_ domain only. Even if a
	 * restrictable characteristic has no runtime domain, this check will state correctly that it is constrained
	 *
	 * @param source
	 *           Characteristic in CPS representation
	 * @return domain constrained
	 */
	protected boolean isConstrained(final CPSCharacteristic source)
	{
		return CollectionUtils.isNotEmpty(source.getPossibleValues()) && !allowsAdditionalValue(source);
	}


	protected boolean allowsAdditionalValue(final CPSCharacteristic source)
	{
		final List<CPSPossibleValue> possibleValues = source.getPossibleValues();
		return null != possibleValues
				&& possibleValues.stream().filter(a -> INTERVAL_TYPE_UNCONSTRAINED.equals(a.getIntervalType())).count() == 1;
	}


	protected boolean fillPossibleValues(final CPSCharacteristic source, final List<CsticValueModel> possibleValues,
			final MasterDataContext ctxt)
	{
		boolean hasOneInterval = false;
		for (final CPSPossibleValue possibleValue : source.getPossibleValues())
		{
			if (possibleValue.isSelectable() && !INTERVAL_TYPE_UNCONSTRAINED.equals(possibleValue.getIntervalType()))
			{
				if (CPSIntervalType.isInterval(possibleValue.getIntervalType()))
				{
					hasOneInterval = true;
				}
				final CsticValueModel csticValueModel = getPossibleValueConverter().convertWithContext(possibleValue, ctxt);
				possibleValues.add(csticValueModel);
			}
		}
		return hasOneInterval;
	}

	protected void populateCoreAttributes(final CPSCharacteristic source, final CsticModel target)
	{
		target.setName(source.getId());
		target.setStaticDomainLength(source.getPossibleValues().size());
		target.setVisible(source.isVisible());
		target.setComplete(source.isComplete());
		target.setConsistent(source.isConsistent());
		target.setReadonly(source.isReadOnly());
		target.setRequired(source.isRequired());
	}

}

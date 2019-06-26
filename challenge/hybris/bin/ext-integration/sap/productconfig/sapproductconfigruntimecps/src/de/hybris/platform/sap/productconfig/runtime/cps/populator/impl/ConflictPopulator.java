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

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSConflictTextParser;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSChoice;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConflict;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSNogood;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Responsible to populate solvable conflicts
 */
public class ConflictPopulator implements Populator<CPSConflict, SolvableConflictModel>
{
	private Converter<CPSChoice, ConflictingAssumptionModel> conflictAssumptionConverter;

	private CPSConflictTextParser conflictTextParser;

	@Required
	public void setConflictAssumptionConverter(final Converter<CPSChoice, ConflictingAssumptionModel> conflictAssumptionConverter)
	{
		this.conflictAssumptionConverter = conflictAssumptionConverter;
	}

	protected Converter<CPSChoice, ConflictingAssumptionModel> getConflictAssumptionConverter()
	{
		return this.conflictAssumptionConverter;
	}

	/**
	 * Populate the conflicts delivered by CPS engine, in the model format
	 *
	 * @param source
	 *           The conflict in CPS format
	 * @param target
	 *           The target conflict model format
	 */
	@Override
	public void populate(final CPSConflict source, final SolvableConflictModel target)
	{
		populateCoreData(source, target);
		populateConflictingAssumptions(source, target);
	}

	protected void populateConflictingAssumptions(final CPSConflict source, final SolvableConflictModel target)
	{
		if (CollectionUtils.isEmpty(source.getNogoods()))
		{
			return;
		}

		final List<ConflictingAssumptionModel> conflictingAssumptions = new ArrayList<>();
		for (final CPSNogood nogood : source.getNogoods())
		{
			if (CollectionUtils.isNotEmpty(nogood.getChoices()))
			{
				conflictingAssumptions.addAll(convertChoicesToAssumptions(nogood));
			}
		}

		target.setConflictingAssumptions(conflictingAssumptions);
	}

	protected List<ConflictingAssumptionModel> convertChoicesToAssumptions(final CPSNogood nogood)
	{
		final List<ConflictingAssumptionModel> conflictingAssumptions = new ArrayList<>();

		for (final CPSChoice choice : nogood.getChoices())
		{
			final ConflictingAssumptionModel assumption = getConflictAssumptionConverter().convert(choice);
			assumption.setId(nogood.getId());
			conflictingAssumptions.add(assumption);
		}

		return conflictingAssumptions;
	}

	protected void populateCoreData(final CPSConflict source, final SolvableConflictModel target)
	{
		target.setId(source.getId());
		target.setDescription(getConflictTextParser().parseConflictText(source.getExplanation()));
	}

	protected CPSConflictTextParser getConflictTextParser()
	{
		return conflictTextParser;
	}

	/**
	 * @param conflictTextParser
	 *           the conflictTextParser to set
	 */
	@Required
	public void setConflictTextParser(final CPSConflictTextParser conflictTextParser)
	{
		this.conflictTextParser = conflictTextParser;
	}
}

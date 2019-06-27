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
package de.hybris.platform.sap.productconfig.runtime.mock.util;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictingAssumptionModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;

import java.util.ArrayList;
import java.util.List;


/**
 * Utility class, to create cloned copies of model objects, which are used within tests.
 */
public class ModelCloneFactory
{
	private ModelCloneFactory()
	{
		// private constructor - Utility class to hide public constructor
	}

	/**
	 * Clone the ConfigModel, including all sub models.
	 *
	 * @param model
	 *           The ConfigModel to clone
	 * @return The cloned model
	 */
	public static ConfigModel cloneConfigModel(final ConfigModel model)
	{
		final ConfigModel clonedConfigModel = new ConfigModelImpl();

		clonedConfigModel.setId(model.getId());
		clonedConfigModel.setKbId(model.getKbId());
		clonedConfigModel.setVersion(model.getVersion());
		clonedConfigModel.setName(model.getName());
		clonedConfigModel.setComplete(model.isComplete());
		clonedConfigModel.setConsistent(model.isConsistent());
		clonedConfigModel.setId(model.getId());
		clonedConfigModel.setSingleLevel(model.isSingleLevel());
		clonedConfigModel.setId(model.getId());

		if (model.getRootInstance() != null)
		{
			clonedConfigModel.setRootInstance(cloneInstanceModel(model.getRootInstance()));
		}
		if (model.getBasePrice() != null)
		{
			clonedConfigModel.setBasePrice(clonePriceModel(model.getBasePrice()));
		}
		if (model.getSelectedOptionsPrice() != null)
		{
			clonedConfigModel.setSelectedOptionsPrice(clonePriceModel(model.getSelectedOptionsPrice()));
		}
		if (model.getCurrentTotalPrice() != null)
		{
			clonedConfigModel.setCurrentTotalPrice(clonePriceModel(model.getCurrentTotalPrice()));
		}
		if (model.getSolvableConflicts() != null)
		{
			final List<SolvableConflictModel> clonedSolvableConflictModels = new ArrayList<>();
			for (final SolvableConflictModel solvableConflictModel : model.getSolvableConflicts())
			{
				final SolvableConflictModel clonedSolvableConflictModel = cloneSolvableConflictModel(solvableConflictModel);
				clonedSolvableConflictModels.add(clonedSolvableConflictModel);
			}
			clonedConfigModel.setSolvableConflicts(clonedSolvableConflictModels);
		}
		if (model.getKbKey() != null)
		{

			clonedConfigModel.setKbKey(cloneKbKey(model.getKbKey()));
		}

		return clonedConfigModel;
	}

	protected static KBKey cloneKbKey(final KBKey kbKey)
	{
		return new KBKeyImpl(kbKey.getProductCode(), kbKey.getKbName(), kbKey.getKbLogsys(), kbKey.getKbVersion(), kbKey.getDate());
	}


	protected static SolvableConflictModel cloneSolvableConflictModel(final SolvableConflictModel solvableConflict)
	{
		final SolvableConflictModel clonedSolvableConflictModel = new SolvableConflictModelImpl();

		clonedSolvableConflictModel.setId(solvableConflict.getId());
		clonedSolvableConflictModel.setDescription(solvableConflict.getDescription());

		final List<ConflictingAssumptionModel> clonedConflictAssumptionModels = new ArrayList<>();
		for (final ConflictingAssumptionModel conflictAssumptionModel : solvableConflict.getConflictingAssumptions())
		{
			clonedConflictAssumptionModels.add(cloneConflictAssumptionModel(conflictAssumptionModel));
		}
		clonedSolvableConflictModel.setConflictingAssumptions(clonedConflictAssumptionModels);

		return clonedSolvableConflictModel;
	}

	protected static ConflictingAssumptionModel cloneConflictAssumptionModel(
			final ConflictingAssumptionModel conflictAssumptionModel)
	{
		final ConflictingAssumptionModel clonedConflictAssumptionModel = new ConflictingAssumptionModelImpl();

		clonedConflictAssumptionModel.setId(conflictAssumptionModel.getId());
		clonedConflictAssumptionModel.setCsticName(conflictAssumptionModel.getCsticName());
		clonedConflictAssumptionModel.setInstanceId(conflictAssumptionModel.getInstanceId());
		clonedConflictAssumptionModel.setValueName(conflictAssumptionModel.getValueName());

		return clonedConflictAssumptionModel;
	}

	protected static PriceModel clonePriceModel(final PriceModel priceModel)
	{
		if (PriceModel.NO_PRICE.equals(priceModel))
		{
			return PriceModel.NO_PRICE;
		}
		if (PriceModel.PRICE_NA.equals(priceModel))
		{
			return PriceModel.PRICE_NA;
		}

		final PriceModel clonedPriceModel = new PriceModelImpl();

		clonedPriceModel.setCurrency(priceModel.getCurrency());
		clonedPriceModel.setPriceValue(priceModel.getPriceValue());

		return clonedPriceModel;
	}

	protected static InstanceModel cloneInstanceModel(final InstanceModel instanceModel)
	{
		final InstanceModel clonedInstanceModel = new InstanceModelImpl();

		clonedInstanceModel.setId(instanceModel.getId());
		clonedInstanceModel.setName(instanceModel.getName());
		clonedInstanceModel.setLanguageDependentName(instanceModel.getLanguageDependentName());
		clonedInstanceModel.setComplete(instanceModel.isComplete());
		clonedInstanceModel.setConsistent(instanceModel.isConsistent());
		clonedInstanceModel.setPosition(instanceModel.getPosition());
		clonedInstanceModel.setRootInstance(instanceModel.isRootInstance());

		final List<InstanceModel> clonedSubInstances = new ArrayList<>();
		for (final InstanceModel subInstance : instanceModel.getSubInstances())
		{
			final InstanceModel clonedSubInstance = cloneInstanceModel(subInstance);
			clonedSubInstances.add(clonedSubInstance);
		}
		clonedInstanceModel.setSubInstances(clonedSubInstances);

		final List<CsticModel> clonedCstics = new ArrayList<>();
		for (final CsticModel cstic : instanceModel.getCstics())
		{
			final CsticModel clonedCstic = cloneCsticModel(cstic);
			clonedCstics.add(clonedCstic);
		}
		clonedInstanceModel.setCstics(clonedCstics);

		final List<CsticGroupModel> clonedCsticGroups = new ArrayList<>();
		for (final CsticGroupModel csticGroup : instanceModel.getCsticGroups())
		{
			final CsticGroupModel clonedCsticGroup = cloneCsticGroupModel(csticGroup);
			clonedCsticGroups.add(clonedCsticGroup);
		}
		clonedInstanceModel.setCsticGroups(clonedCsticGroups);

		return clonedInstanceModel;
	}

	protected static CsticGroupModel cloneCsticGroupModel(final CsticGroupModel csticGroupModel)
	{
		final CsticGroupModel clonedCsticGroupModel = new CsticGroupModelImpl();

		clonedCsticGroupModel.setName(csticGroupModel.getName());
		clonedCsticGroupModel.setDescription(csticGroupModel.getDescription());
		clonedCsticGroupModel.setCsticNames(csticGroupModel.getCsticNames());

		return clonedCsticGroupModel;
	}

	protected static CsticModel cloneCsticModel(final CsticModel csticModel)
	{
		final CsticModel clonedCstic = new CsticModelImpl();

		clonedCstic.setName(csticModel.getName());
		clonedCstic.setInstanceId(csticModel.getInstanceId());
		clonedCstic.setInstanceName(csticModel.getInstanceName());
		clonedCstic.setLanguageDependentName(csticModel.getLanguageDependentName());
		clonedCstic.setLongText(csticModel.getLongText());
		clonedCstic.setAuthor(csticModel.getAuthor());
		clonedCstic.setValueType(csticModel.getValueType());
		clonedCstic.setTypeLength(csticModel.getTypeLength());
		clonedCstic.setNumberScale(csticModel.getNumberScale());
		clonedCstic.setStaticDomainLength(csticModel.getStaticDomainLength());
		clonedCstic.setConsistent(csticModel.isConsistent());
		clonedCstic.setConstrained(csticModel.isConstrained());
		clonedCstic.setComplete(csticModel.isComplete());
		clonedCstic.setMultivalued(csticModel.isMultivalued());
		clonedCstic.setReadonly(csticModel.isReadonly());
		clonedCstic.setRequired(csticModel.isRequired());
		clonedCstic.setRetractTriggered(csticModel.isRetractTriggered());
		clonedCstic.setVisible(csticModel.isVisible());
		clonedCstic.setAllowsAdditionalValues(csticModel.isAllowsAdditionalValues());
		clonedCstic.setEntryFieldMask(csticModel.getEntryFieldMask());
		clonedCstic.setIntervalInDomain(csticModel.isIntervalInDomain());
		clonedCstic.setPlaceholder(csticModel.getPlaceholder());


		final List<CsticValueModel> clonedAssignedValues = new ArrayList<>(csticModel.getAssignedValues().size());
		for (final CsticValueModel assignedValue : csticModel.getAssignedValues())
		{
			final CsticValueModel clonedAssignedValue = cloneCsticValueModel(assignedValue);
			clonedAssignedValues.add(clonedAssignedValue);
		}
		clonedCstic.setAssignedValues(clonedAssignedValues);

		final List<CsticValueModel> clonedAssignableValues = new ArrayList<>(csticModel.getAssignableValues().size());
		for (final CsticValueModel assignableValue : csticModel.getAssignableValues())
		{
			final CsticValueModel clonedAssignableValue = cloneCsticValueModel(assignableValue);
			clonedAssignableValues.add(clonedAssignableValue);
		}

		clonedCstic.setAssignableValues(clonedAssignableValues);
		clonedCstic.setChangedByFrontend(false);

		return clonedCstic;
	}

	/**
	 * Clone a given CsticValueModel in a new object of type CsticValueModel
	 *
	 * @param csticValueModel
	 *           The model, which should be cloned
	 * @return The cloned model object
	 */
	public static CsticValueModel cloneCsticValueModel(final CsticValueModel csticValueModel)
	{
		final CsticValueModel clonedCsticValue = new CsticValueModelImpl();

		clonedCsticValue.setName(csticValueModel.getName());
		clonedCsticValue.setAuthor(csticValueModel.getAuthor());
		clonedCsticValue.setAuthorExternal(csticValueModel.getAuthorExternal());
		clonedCsticValue.setDomainValue(csticValueModel.isDomainValue());
		clonedCsticValue.setLanguageDependentName(csticValueModel.getLanguageDependentName());
		clonedCsticValue.setNumeric(csticValueModel.isNumeric());
		clonedCsticValue.setSelectable(csticValueModel.isSelectable());

		clonedCsticValue.setDeltaPrice(clonePriceModel(csticValueModel.getDeltaPrice()));
		clonedCsticValue.setValuePrice(clonePriceModel(csticValueModel.getValuePrice()));

		return clonedCsticValue;
	}
}

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
package de.hybris.platform.sap.productconfig.runtime.mock.impl;

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
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;
import de.hybris.platform.sap.productconfig.runtime.mock.ConfigMock;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;


public abstract class BaseRunTimeConfigMockImpl implements ConfigMock
{
	private static final String VERSION = "0";
	protected static final String ROOT_INST_ID = "1";
	private String configId = "";
	private int conflictAssumptionIdCounter = 0;


	protected InstanceModel createDefaultRootInstance(final ConfigModel model, final String productCode, final String langDepName)
	{
		final InstanceModel rootInstance = createInstance();
		rootInstance.setId(ROOT_INST_ID);
		rootInstance.setName(productCode);
		rootInstance.setLanguageDependentName(langDepName);
		rootInstance.setRootInstance(true);
		rootInstance.setComplete(false);
		rootInstance.setConsistent(true);
		rootInstance.setSubInstances(Collections.emptyList());
		model.setRootInstance(rootInstance);
		return rootInstance;
	}


	protected ConfigModel createDefaultConfigModel(final String name, final boolean isSingelLevel)
	{
		final ConfigModel model = new ConfigModelImpl();
		model.setId(configId);
		model.setVersion(VERSION);
		model.setKbId(configId);
		model.setName(name);
		model.setComplete(false);
		model.setConsistent(true);
		model.setSingleLevel(isSingelLevel);
		return model;
	}

	protected ConfigModel createDefaultConfigModel(final String name)
	{
		return createDefaultConfigModel(name, true);
	}

	protected InstanceModel createInstance()
	{
		final InstanceModel instance = new InstanceModelImpl();
		instance.setRootInstance(false);
		instance.setComplete(false);
		instance.setConsistent(false);
		instance.setSubInstances(new ArrayList<>());
		instance.setPosition("");
		return instance;
	}


	@Override
	public void checkModel(final ConfigModel model)
	{
		model.setComplete(false);
		model.setConsistent(false);
		model.setSolvableConflicts(new ArrayList<SolvableConflictModel>(1));
		model.getMessages().clear();
		resetConflictAssumptionId();

		final InstanceModel rootInstance = model.getRootInstance();
		if (rootInstance != null)
		{
			checkInstance(model, rootInstance);
			model.setComplete(rootInstance.isComplete());
			model.setConsistent(rootInstance.isConsistent());
		}
	}

	@Override
	public void checkInstance(final ConfigModel model, final InstanceModel instance)
	{
		instance.setComplete(true);
		instance.setConsistent(true);

		final List<CsticModel> cstics = instance.getCstics();

		for (final CsticModel cstic : cstics)
		{

			checkCstic(model, instance, cstic);

			if (!cstic.isComplete())
			{
				instance.setComplete(false);
			}
			if (!cstic.isConsistent())
			{
				instance.setConsistent(false);
			}

			model.getMessages();
		}

		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			checkInstance(model, subInstance);
			if (!subInstance.isComplete())
			{
				instance.setComplete(false);
			}
			if (!subInstance.isConsistent())
			{
				instance.setConsistent(false);
			}
		}
	}

	@Override
	public void checkCstic(final ConfigModel model, final InstanceModel instance, final CsticModel cstic)
	{
		cstic.getMessages().clear();
		// Check "consistent"
		cstic.setConsistent(true);

		// Check "complete"
		cstic.setComplete(true);
		if (cstic.isRequired() && cstic.getAssignedValues().isEmpty())
		{
			cstic.setComplete(false);
		}
		// Check "visible"
		cstic.setVisible(true);
		cstic.setReadonly(false);

		// make sure newly assigned value is in domain
		if (cstic.isAllowsAdditionalValues() && !cstic.isMultivalued() && cstic.getAssignedValues().size() == 1)
		{
			final CsticValueModel csticValueModel = cstic.getAssignedValues().get(0);
			List<CsticValueModel> assignableValues = cstic.getAssignableValues();
			if (!assignableValues.contains(csticValueModel))
			{
				assignableValues = new ArrayList(cstic.getAssignableValues());
				assignableValues.add(csticValueModel);
				if (CsticModel.TYPE_FLOAT == cstic.getValueType())
				{
					formatValueNumeric(csticValueModel, csticValueModel.getName());
				}
				else
				{
					csticValueModel.setLanguageDependentName(csticValueModel.getName());
				}
				cstic.setAssignableValues(assignableValues);
			}
		}
	}

	protected void formatValueNumeric(final CsticValueModel value, final String strValue)
	{
		final DecimalFormat format = new DecimalFormat("#,###,##0.00#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		value.setName(strValue);
		value.setLanguageDependentName(format.format(new BigDecimal(strValue)));
	}

	protected void addCsticGroup(final List<CsticGroupModel> csticGroups, final String name, final String description,
			final String... cstics)
	{
		final CsticGroupModel csticGroup = new CsticGroupModelImpl();
		csticGroup.setName(name);
		csticGroup.setDescription(description);

		final List<String> csticNames = new ArrayList<>();
		if (null != cstics)
		{
			for (final String cstic : cstics)
			{
				csticNames.add(cstic);
			}
		}

		csticGroup.setCsticNames(csticNames);
		csticGroups.add(csticGroup);
	}



	protected PriceModel createPrice(final long priceValue)
	{
		return createPrice(new BigDecimal(priceValue));
	}

	protected PriceModel createPrice(final BigDecimal priceValue)
	{
		return createPrice(priceValue, false);
	}

	protected PriceModel createPrice(final BigDecimal priceValue, final BigDecimal obsoletePrice)
	{
		return createPrice(priceValue, obsoletePrice, false);
	}

	protected PriceModel createPrice(final BigDecimal priceValue, final BigDecimal obsoletePrice, final boolean allowZeroPrice)
	{
		PriceModel price = PriceModel.NO_PRICE;
		if (priceValue != null && (allowZeroPrice || priceValue.longValue() != 0))
		{
			price = new PriceModelImpl();
			price.setCurrency("EUR");
			price.setPriceValue(priceValue);
			price.setObsoletePriceValue(obsoletePrice);
		}
		return price;
	}



	protected PriceModel createPrice(final BigDecimal priceValue, final boolean allowZeroPrice)
	{
		PriceModel price = PriceModel.NO_PRICE;
		if (priceValue != null && (allowZeroPrice || priceValue.longValue() != 0))
		{
			price = new PriceModelImpl();
			price.setCurrency("EUR");
			price.setPriceValue(priceValue);
		}
		return price;
	}

	protected SolvableConflictModel createSolvableConflict(final CsticValueModel value, final CsticModel cstic,
			final InstanceModel instance, final String conflictText)
	{

		return createSolvableConflict(value, cstic, instance, conflictText, null, null);
	}

	protected SolvableConflictModel createSolvableConflict(final CsticValueModel value, final CsticModel cstic,
			final InstanceModel instance, final String conflictText, final CsticValueModel value2, final CsticModel cstic2)
	{

		return createSolvableConflict(value, cstic, instance, conflictText, value2, cstic2, instance);
	}

	protected SolvableConflictModel createSolvableConflict(final CsticValueModel value, final CsticModel cstic,
			final InstanceModel instance, final String conflictText, final CsticValueModel value2, final CsticModel cstic2,
			final InstanceModel instance2)
	{

		final SolvableConflictModel solvableConflictModel = new SolvableConflictModelImpl();
		String conflictLongName = "";
		if (conflictText != null)
		{
			conflictLongName = conflictText;
		}
		else
		{
			conflictLongName = "Precondition " + System.currentTimeMillis() + " violated";
		}

		solvableConflictModel.setDescription(conflictLongName);

		final List<ConflictingAssumptionModel> conflictingAssumptionsList = new ArrayList<>();
		conflictingAssumptionsList.add(createConflictAssumption(value, cstic, instance));
		if (cstic2 != null)
		{
			conflictingAssumptionsList.add(createConflictAssumption(value2, cstic2, instance2));
		}
		if (CollectionUtils.isNotEmpty(conflictingAssumptionsList))
		{
			final String groupId = conflictingAssumptionsList.get(0).getId();
			solvableConflictModel.setId(groupId);
		}
		solvableConflictModel.setConflictingAssumptions(conflictingAssumptionsList);
		return solvableConflictModel;
	}


	protected SolvableConflictModel createSolvableConflict(final CsticValueModel value, final CsticModel cstic,
			final InstanceModel instance)
	{
		return createSolvableConflict(value, cstic, instance, null);
	}

	protected ConflictingAssumptionModel createConflictAssumption(final CsticValueModel value, final CsticModel cstic,
			final InstanceModel instance)
	{

		final String observableName = cstic.getName();
		final String observableValueName = value.getName();
		final String asumptionId = Integer.toString(getNextConflictAssumptionId());

		final ConflictingAssumptionModel assumptionModel = new ConflictingAssumptionModelImpl();
		assumptionModel.setCsticName(observableName);
		assumptionModel.setValueName(observableValueName);
		assumptionModel.setInstanceId(instance.getId());
		assumptionModel.setId(asumptionId);

		return assumptionModel;
	}

	public void resetConflictAssumptionId()
	{
		conflictAssumptionIdCounter = 0;
	}

	public int getNextConflictAssumptionId()
	{
		return conflictAssumptionIdCounter++;
	}

	public void setConfigId(final String nextConfigId)
	{
		configId = nextConfigId;
	}

	public String getConfigId()
	{
		return configId;
	}

}

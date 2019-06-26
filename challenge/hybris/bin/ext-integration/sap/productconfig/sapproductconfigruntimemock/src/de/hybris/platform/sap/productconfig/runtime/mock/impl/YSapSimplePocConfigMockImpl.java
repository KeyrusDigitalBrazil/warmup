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
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


public class YSapSimplePocConfigMockImpl extends BaseRunTimeConfigMockImpl
{
	public static final String CONFIG_NAME = "Config Name";
	public static final String ROOT_INSTANCE_NAME = "YSAP_SIMPLE_POC";
	public static final String ROOT_INSTANCE_LANG_DEP_NAME = "ySAP simple product for POC";
	public static final String CB_NAME = "YSAP_POC_SIMPLE_FLAG";
	public static final String CB_LD_NAME = "Simple Flag: Hide options";
	public static final String NUM_NAME = "WCEM_NUMBER_SIMPLE";
	public static final String NUM_LD_NAME = "Num w/o decimal";
	public static final String RB_NAME = "WCEM_RADIO_BUTTON";
	public static final String EXP_NO_USERS = "EXP_NO_USERS";
	public static final String LANG_DEPENDENT_EXP_NO_USERS = "Expected Number of Users";
	public static final String RB_LD_NAME = "Radio Button Group";
	public static final String CB_TRUE = "X";
	private static final String VALUE_1 = "Value 1";
	private static final String VALUE_2 = "Value 2";
	private static final String VALUE_3 = "Value 3";
	private static final String VALUE_4 = "Value 4";


	@Override
	public ConfigModel createDefaultConfiguration()
	{
		// Model
		final ConfigModel model = createDefaultConfigModel(CONFIG_NAME);

		// root instance
		final InstanceModel rootInstance = createDefaultRootInstance(model, ROOT_INSTANCE_NAME, ROOT_INSTANCE_LANG_DEP_NAME);

		// Characteristics and Values
		final List<CsticModel> cstics = new ArrayList<>();
		cstics.add(createCBCstic());
		cstics.add(createNumCstic());
		cstics.add(createExpNoUsersCstic());
		cstics.add(createRBCstic());
		rootInstance.setCstics(cstics);

		// groups
		final List<CsticGroupModel> groups = new ArrayList<>();
		addCsticGroup(groups, InstanceModel.GENERAL_GROUP_NAME, null, CB_NAME, NUM_NAME, EXP_NO_USERS, RB_NAME);
		rootInstance.setCsticGroups(groups);

		return model;
	}

	protected CsticModel createCBCstic()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INST_ID, ROOT_INSTANCE_NAME);
		builder.withName(CB_NAME, CB_LD_NAME);
		builder.simpleFlag("Hide").selected();
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createNumCstic()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INST_ID, ROOT_INSTANCE_NAME);
		builder.withName(NUM_NAME, NUM_LD_NAME);
		builder.numericType(0, 8);
		builder.withDefaultUIState().required().hidden();
		return builder.build();
	}

	protected CsticModel createExpNoUsersCstic()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INST_ID, ROOT_INSTANCE_NAME);
		builder.withName(EXP_NO_USERS, LANG_DEPENDENT_EXP_NO_USERS);
		builder.numericType(0, 10);
		builder.withDefaultUIState().required().hidden();
		return builder.build();
	}

	protected CsticModel createRBCstic()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INST_ID, ROOT_INSTANCE_NAME);
		builder.withName(RB_NAME, RB_LD_NAME);
		builder.stringType().singleSelection();
		builder.addOption("VAL1", VALUE_1).addOption("VAL2", VALUE_2).addSelectedOption("VAL3", VALUE_3).addOption("VAL4", VALUE_4);
		builder.withDefaultUIState().required().hidden();
		return builder.build();
	}

	protected CsticValueModel createRBValue(final String name, final String languageDependentName, final boolean domainValue)
	{
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName(name);
		value.setLanguageDependentName(languageDependentName);
		value.setDomainValue(domainValue);
		return value;
	}

	public CsticValueModel retrieveValue(final InstanceModel instance, final String csticName)
	{
		CsticValueModel value = null;

		final List<CsticModel> cstics = instance.getCstics();
		for (final CsticModel cstic : cstics)
		{
			if (cstic.getName().equalsIgnoreCase(csticName))
			{
				final List<CsticValueModel> values = cstic.getAssignedValues();
				if (values != null && !values.isEmpty())
				{
					value = values.get(0);
				}
				break;
			}
		}
		return value;
	}

	@Override
	public void checkCstic(final ConfigModel model, final InstanceModel instance, final CsticModel cstic)
	{
		super.checkCstic(model, instance, cstic);
		// Check "consistent"
		if (cstic.getName().equalsIgnoreCase(NUM_NAME) && CollectionUtils.isNotEmpty(cstic.getAssignedValues()))
		{
			final String val = cstic.getAssignedValues().get(0).getName();
			if (val != null && Float.parseFloat(val) > 99999999)
			{
				cstic.setConsistent(false);
			}
		}

		// Check "visible"
		if (cstic.getName().equalsIgnoreCase(NUM_NAME) || cstic.getName().equalsIgnoreCase(RB_NAME)
				|| cstic.getName().equalsIgnoreCase(EXP_NO_USERS))
		{
			final CsticValueModel value = retrieveValue(instance, CB_NAME);

			if (value != null && value.getName().equalsIgnoreCase(CB_TRUE))
			{
				cstic.setVisible(false);
			}
		}
	}
}

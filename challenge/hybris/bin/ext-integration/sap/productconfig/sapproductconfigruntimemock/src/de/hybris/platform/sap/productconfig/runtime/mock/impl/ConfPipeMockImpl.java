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
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class ConfPipeMockImpl extends BaseRunTimeConfigMockImpl
{
	private static final String PLASTIC_TYPE = "PL";
	private static final String STEEL_TYPE = "ST";
	private static final String ROOT_INSTANCE_LANG_DEP_NAME = "Pipe";
	private static final String ROOT_INSTANCE_NAME = "CONF_PIPE";
	private static final String TYPE_NAME = "CPQ_PIPE_TYPE";
	private static final String LENGTH_NAME = "CPQ_PIPE_LENGTH";
	private static final String OUTER_DIA_NAME = "CPQ_PIPE_OUTER";
	private static final String INNER_DIA_NAME = "CPQ_PIPE_INNER";
	private static final String NAME = "name";
	private static final String VALUE_15 = "15";
	private static final String VALUE_20 = "20";
	private static final String VALUE_25 = "25";
	private static final String VALUE_30 = "30";
	private static final String VALUE_40 = "40";

	private final Map<String, String> variantParams = new HashMap<>();


	@Override
	public ConfigModel createDefaultConfiguration()
	{
		// Model
		final ConfigModel model = createDefaultConfigModel(ROOT_INSTANCE_NAME);

		// root instance
		final InstanceModel rootInstance = createDefaultRootInstance(model, ROOT_INSTANCE_NAME, ROOT_INSTANCE_LANG_DEP_NAME);

		// Characteristics and Values
		final List<CsticModel> cstics = new ArrayList<>();
		cstics.add(createType());
		cstics.add(createOuterDiameter());
		cstics.add(createInnerDiameter());
		cstics.add(createLength());
		rootInstance.setCstics(cstics);

		// groups
		final List<CsticGroupModel> groups = new ArrayList<>();
		addCsticGroup(groups, InstanceModel.GENERAL_GROUP_NAME, null, TYPE_NAME, OUTER_DIA_NAME, INNER_DIA_NAME, LENGTH_NAME);
		rootInstance.setCsticGroups(groups);

		return model;
	}

	public void setVariantCode(final String productCode)
	{
		if (null != productCode)
		{
			// CONF_PIPE-OD-ID-TN ==> OD = outer diamater; ID = inner diameter; TN = type name
			// CONF_PIPE-30-15-PL ==> OD = 30; ID = 15; PN = PL (plastic)
			final String[] components = productCode.split("-");
			variantParams.put(NAME, components[0]);
			variantParams.put(OUTER_DIA_NAME, components[1]);
			variantParams.put(INNER_DIA_NAME, components[2]);
			variantParams.put(TYPE_NAME, components[3]);
		}
	}

	private CsticModel createType()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INST_ID, ROOT_INSTANCE_NAME);
		builder.withName(TYPE_NAME, "type of pipe");
		builder.stringType().singleSelection();
		builder.addOption(STEEL_TYPE, "Steel", STEEL_TYPE.equals(variantParams.get(TYPE_NAME)));
		builder.addOption(PLASTIC_TYPE, "Plastic", PLASTIC_TYPE.equals(variantParams.get(TYPE_NAME)));
		builder.withDefaultUIState().required();
		return builder.build();
	}

	private CsticModel createOuterDiameter()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INST_ID, ROOT_INSTANCE_NAME);
		builder.withName(OUTER_DIA_NAME, "Outer Diamater in cm");
		builder.stringType().singleSelection();
		builder.addOption(VALUE_30, VALUE_30, VALUE_30.equals(variantParams.get(OUTER_DIA_NAME)));
		builder.addOption(VALUE_40, VALUE_40, VALUE_40.equals(variantParams.get(OUTER_DIA_NAME)));
		builder.withDefaultUIState().required();
		return builder.build();
	}

	private CsticModel createInnerDiameter()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INST_ID, ROOT_INSTANCE_NAME);
		builder.withName(INNER_DIA_NAME, "Inner Diamater in cm");
		builder.stringType().singleSelection();
		builder.addOption(VALUE_15, VALUE_15, VALUE_15.equals(variantParams.get(INNER_DIA_NAME)));
		builder.addOption(VALUE_20, VALUE_20, VALUE_20.equals(variantParams.get(INNER_DIA_NAME)));
		builder.addOption(VALUE_25, VALUE_25, VALUE_25.equals(variantParams.get(INNER_DIA_NAME)));
		builder.withDefaultUIState().required();
		return builder.build();
	}

	private CsticModel createLength()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INST_ID, ROOT_INSTANCE_NAME);
		builder.withName(LENGTH_NAME, "Pipe Length in cm");
		builder.numericType(0, 4).simpleInput();
		builder.withDefaultUIState();
		return builder.build();
	}

	@Override
	public boolean isChangeabeleVariant()
	{
		return true;
	}

}

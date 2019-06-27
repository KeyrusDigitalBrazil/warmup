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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


public class GenericTestConfigModelImpl
{

	private final Properties properties;

	public static final String CONFIG_ID = "";
	public static final String VERSION = "version";
	private int instanceId = 1;
	public static final String CONFIG_NAME = "Generic UI Group Test Product";
	public static final String ROOT_INSTANCE_NAME = "$1-ROOT";
	public static final String ROOT_INSTANCE_LANG_DEP_NAME = "Generic UI Group Test Product Root";
	public static final String CSTIC_MULTIVALUED = "Multi";


	public GenericTestConfigModelImpl(final Properties properties)
	{
		this.properties = properties;
	}

	public ConfigModel createDefaultConfiguration()
	{
		// Model
		final ConfigModel model = createDefaultConfigModel(CONFIG_NAME, false);

		// Root Instance
		final InstanceModel rootInstance = createDefaultRootInstance(model, ROOT_INSTANCE_LANG_DEP_NAME);
		rootInstance.setId(String.valueOf(instanceId++));
		rootInstance.setName(ROOT_INSTANCE_NAME);
		rootInstance.setPosition("");
		rootInstance.setComplete(true);

		processInstance(rootInstance, "root");

		return model;
	}

	private void processInstance(final InstanceModel instance, final String instancePropertyName)
	{
		// Process cstics
		final List<CsticModel> csticList = retrieveCsticListForInstance(instancePropertyName);
		instance.setCstics(csticList);

		// Process groups
		final List<CsticGroupModel> csticGroupList = retrieveCsticGroupListForInstance(instancePropertyName);
		instance.setCsticGroups(csticGroupList);

		// process subinstances
		final List<InstanceModel> subinstanceList = new ArrayList<InstanceModel>();
		final String subinstancesPropertyString = properties.getProperty(instancePropertyName + ".subinstances");

		if (subinstancesPropertyString != null && !subinstancesPropertyString.isEmpty())
		{
			final String[] subinstancesPropertiesArray = subinstancesPropertyString.split(";");
			for (int i = 0; i < subinstancesPropertiesArray.length; i++)
			{
				final String subinstanceNameAndLDName = subinstancesPropertiesArray[i];
				final String[] names = subinstanceNameAndLDName.split(",");
				final InstanceModel subinstance = new InstanceModelImpl();
				subinstance.setName(names[0]);
				subinstance.setLanguageDependentName(names[1]);
				subinstance.setId(String.valueOf(instanceId++));
				subinstance.setPosition("");
				subinstance.setRootInstance(false);
				subinstance.setComplete(true);
				subinstance.setConsistent(true);
				subinstanceList.add(subinstance);

				processInstance(subinstance, instancePropertyName + "." + names[0]);
			}
		}
		instance.setSubInstances(subinstanceList);
	}

	private List<CsticGroupModel> retrieveCsticGroupListForInstance(final String instancePropertyName)
	{
		final List<CsticGroupModel> csticGroupList = new ArrayList<CsticGroupModel>();

		final String groupsPropertyString = properties.getProperty(instancePropertyName + ".groups");

		if (groupsPropertyString != null && !groupsPropertyString.isEmpty())
		{
			final String[] groupssPropertiesArray = groupsPropertyString.split(";");
			for (int i = 0; i < groupssPropertiesArray.length; i++)
			{
				final String groupNameAndLDNameAndCsticNames = groupssPropertiesArray[i];
				final String[] names = groupNameAndLDNameAndCsticNames.split(",");

				final CsticGroupModel groupModel = new CsticGroupModelImpl();
				groupModel.setName(names[0]);
				groupModel.setDescription(names[1]);

				final List<String> csticNames = new ArrayList<String>();
				if (names.length > 2)
				{
					for (int j = 2; j < names.length; j++)
					{
						csticNames.add(names[j]);
					}
				}

				groupModel.setCsticNames(csticNames);
				csticGroupList.add(groupModel);
			}
		}

		return csticGroupList;
	}


	private List<CsticModel> retrieveCsticListForInstance(final String instancePropertyName)
	{
		final List<CsticModel> csticList = new ArrayList<CsticModel>();

		final String csticsPropertyString = properties.getProperty(instancePropertyName + ".cstics");

		if (csticsPropertyString != null && !csticsPropertyString.isEmpty())
		{
			final String[] csticsPropertiesArray = csticsPropertyString.split(";");
			for (int i = 0; i < csticsPropertiesArray.length; i++)
			{
				final String csticsDefinition = csticsPropertiesArray[i];
				final String[] csticProps = csticsDefinition.split(",");

				// Value Type
				int valueType;
				if (csticProps.length > 2)
				{
					valueType = getValueTypeFromString(csticProps[2]);
				}
				else
				{
					valueType = CsticModel.TYPE_FLOAT;
				}

				// Single- / Multi-level
				boolean multivalued = false;
				if (csticProps.length > 3)
				{
					if (csticProps[3].equalsIgnoreCase(CSTIC_MULTIVALUED))
					{
						multivalued = true;
					}
				}

				csticList.add(createCstic(csticProps[0], csticProps[1], valueType, multivalued, instancePropertyName));
			}
		}

		return csticList;
	}

	private int getValueTypeFromString(final String name)
	{
		int valueType;
		final String lowerCase = name.toLowerCase(Locale.ENGLISH);

		if ("string".equals(lowerCase))
		{
			valueType = CsticModel.TYPE_STRING;
		}
		else if ("float".equals(lowerCase))
		{
			valueType = CsticModel.TYPE_FLOAT;
		}
		else if ("boolean".equals(lowerCase))
		{
			valueType = CsticModel.TYPE_BOOLEAN;
		}
		else
		{
			valueType = CsticModel.TYPE_FLOAT;
		}

		return valueType;
	}


	private CsticModel createCstic(final String name, final String languageDependentName, final int valueType,
			final boolean multivalued, final String instancePropertyName)
	{
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(name);
		cstic.setLanguageDependentName(languageDependentName);
		cstic.setValueType(valueType);
		cstic.setMultivalued(multivalued);
		cstic.setTypeLength(10);
		cstic.setNumberScale(3);

		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setMultivalued(multivalued);
		cstic.setReadonly(false);
		cstic.setRequired(false);
		cstic.setVisible(true);

		final List<CsticValueModel> assignedValues = retrieveAssignedValues(name, instancePropertyName);
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = retrieveAssignableValues(name, instancePropertyName);
		cstic.setAssignableValues(assignableValues);

		return cstic;
	}

	private List<CsticValueModel> retrieveAssignableValues(final String csticName, final String instancePropertyName)
	{
		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();

		final String csticAssignableValuesString = properties
				.getProperty(instancePropertyName + ".cstics." + csticName + ".assignableValues");

		if (csticAssignableValuesString != null && !csticAssignableValuesString.isEmpty())
		{
			final String[] valueArray = csticAssignableValuesString.split(";");
			for (int i = 0; i < valueArray.length; i++)
			{
				final String valueDefinition = valueArray[i];
				final String[] valueProps = valueDefinition.split(",");
				assignableValues.add(createCsticValue(valueProps[0], valueProps[1]));
			}
		}
		return assignableValues;
	}

	private List<CsticValueModel> retrieveAssignedValues(final String csticName, final String instancePropertyName)
	{
		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();

		final String csticAssignedValuesString = properties
				.getProperty(instancePropertyName + ".cstics." + csticName + ".assignedValues");

		if (csticAssignedValuesString != null && !csticAssignedValuesString.isEmpty())
		{
			final String[] valueArray = csticAssignedValuesString.split(";");
			for (int i = 0; i < valueArray.length; i++)
			{
				final String valueDefinition = valueArray[i];
				final String[] valueProps = valueDefinition.split(",");
				assignedValues.add(createCsticValue(valueProps[0], valueProps[1]));
			}
		}
		return assignedValues;
	}

	private CsticValueModel createCsticValue(final String valueName, final String valueLDName)
	{
		final CsticValueModel valueModel = new CsticValueModelImpl();
		valueModel.setName(valueName);
		valueModel.setLanguageDependentName(valueLDName);
		return valueModel;
	}

	protected ConfigModel createDefaultConfigModel(final String name, final boolean isSingelLevel)
	{
		final ConfigModel model = new ConfigModelImpl();
		model.setId(CONFIG_ID);
		model.setVersion(VERSION);
		model.setName(name);
		model.setComplete(false);
		model.setConsistent(true);
		model.setSingleLevel(isSingelLevel);
		return model;
	}

	protected InstanceModel createDefaultRootInstance(final ConfigModel model, final String langDepName)
	{
		final InstanceModel rootInstance = new InstanceModelImpl();
		rootInstance.setId("1");
		rootInstance.setName("ROOT_INSTANCE");
		rootInstance.setLanguageDependentName(langDepName);
		rootInstance.setRootInstance(true);
		rootInstance.setComplete(false);
		rootInstance.setConsistent(true);
		rootInstance.setSubInstances(Collections.EMPTY_LIST);
		model.setRootInstance(rootInstance);
		return rootInstance;
	}
}

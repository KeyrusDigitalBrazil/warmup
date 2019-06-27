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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ConfigurationTestData
{
	public static final String CONFIG_ID = "1";
	public static final String CONFIG_NAME = "Config Name";

	public static final String ROOT_INSTANCE_ID = "1";
	public static final String ROOT_INSTANCE_NAME = "SIMPLE_PRODUCT";
	public static final String ROOT_INSTANCE_LANG_DEP_NAME = "simple product for";

	public static final String STR_NAME = "SAP_STRING_SIMPLE";
	public static final String STR_LD_NAME = "Simple String:";

	public static final String INT_NUM_NAME = "SAP_INT_NUM_SIMPLE";
	public static final String INT_NUM_LD_NAME = "Simple Interval Numeric String:";

	public static final String CHBOX_NAME = "SAP_CHECKBOX_SIMPLE";
	public static final String CHBOX_LD_NAME = "Simple Checkbox:";

	public static final String RB_NAME = "SAP_RADIOBUTTON_SIMPLE";
	public static final String RB_LD_NAME = "Simple RadioButton:";

	public static final String RB_NAME_FLOAT = "SAP_RADIOBUTTON_SIMPLE_FLOAT";
	public static final String RB_LD_NAME_FLOAT = "Float RadioButton:";

	public static final String DROPD_NAME = "SAP_DROPDOWN_SIMPLE";
	public static final String DROPD_LD_NAME = "Simple DropDownList:";

	public static final String CHBOX_LIST_NAME = "SAP_CHECKBOX_LIST_SIMPLE";
	public static final String CHBOX_LIST_LD_NAME = "Checkbox List:";

	public static final String RB_ADDV_NAME = "SAP_RADIOBUTTON_ADDVALUE";
	public static final String RB_ADDV_LD_NAME = "Radio Button With additional Value:";

	public static final String DROPD_AADV_NAME = "SAP_DROPDOWN_ADDVALUE";
	public static final String DROPD_ADDV_LD_NAME = "DropDownList with Additional Values:";
	private static final String DROPD_ADDV_NAME = null;

	public static final String NUM_PLACEHOLDER = "11 - 22 ; 33 - 44";

	public static final String AUTHOR_EXTERNAL_DEFAULT = "8";
	public static final String AUTHOR_EXTERNAL_CONSTRAINT = "4";

	public static final BigDecimal BASE_PRICE = new BigDecimal(100);
	public static final BigDecimal SELECTED_OPTIONS_PRICE = new BigDecimal(50);
	public static final BigDecimal TOTAL_PRICE = new BigDecimal(150);

	static int instanceId = 1;

	public static ConfigModel createConfigModelWithCstic()
	{
		final ConfigModel model = createEmptyConfigModel();

		final PriceModel basePrice = new PriceModelImpl();
		basePrice.setPriceValue(new BigDecimal(1000));
		basePrice.setCurrency("EUR");
		model.setBasePrice(basePrice);

		final PriceModel selectedOptionsPrice = new PriceModelImpl();
		selectedOptionsPrice.setPriceValue(new BigDecimal(50));
		selectedOptionsPrice.setCurrency("EUR");
		model.setSelectedOptionsPrice(selectedOptionsPrice);

		final PriceModel currentTotalPrice = new PriceModelImpl();
		currentTotalPrice.setPriceValue(new BigDecimal(1050));
		currentTotalPrice.setCurrency("EUR");
		model.setCurrentTotalPrice(currentTotalPrice);

		final InstanceModel rootInstance = model.getRootInstance();
		// Characteristics and Values

		final List<CsticModel> cstics = new ArrayList<>();
		cstics.add(createSTRCstic());
		rootInstance.setCstics(cstics);

		return model;
	}

	public static ConfigModel createConfigModelWithGroups()
	{
		final ConfigModel configModel = createConfigModelWithCstic();

		final InstanceModel rootInstance = configModel.getRootInstance();

		rootInstance.addCstic(createCheckBoxListCsticWithValue2Assigned());
		rootInstance.addCstic(createCheckBoxCstic());

		final List<CsticGroupModel> csticGroups = new ArrayList<>();
		final CsticGroupModel group1 = createCsticGroup("GROUP1", "Group 1", STR_NAME, CHBOX_NAME);
		final CsticGroupModel group2 = createCsticGroup("GROUP2", "Group 2", CHBOX_LIST_NAME);

		csticGroups.add(group1);
		csticGroups.add(group2);

		rootInstance.setCsticGroups(csticGroups);

		return configModel;
	}

	public static ConfigModel createConfigModelWith1GroupAndAssignedValues()
	{
		final ConfigModel configModel = createConfigModelWithCstic();

		final InstanceModel rootInstance = configModel.getRootInstance();
		final List<CsticGroupModel> csticGroups = new ArrayList<>();

		createGroup1WithCsticAndAssignedValues(rootInstance, csticGroups);

		rootInstance.setCsticGroups(csticGroups);
		return configModel;
	}

	public static ConfigModel createConfigModelWith1GroupAndAssignedValuesMultiValued()
	{
		final ConfigModel configModel = createConfigModelWithCstic();

		final InstanceModel rootInstance = configModel.getRootInstance();
		final List<CsticGroupModel> csticGroups = new ArrayList<>();

		createGroup1WithCsticAndAssignedValuesMultiValued(rootInstance, csticGroups);

		rootInstance.setCsticGroups(csticGroups);
		return configModel;
	}

	public static ConfigModel createConfigModelWith2GroupAndAssignedValues()
	{
		final ConfigModel configModel = createConfigModelWithCstic();

		final InstanceModel rootInstance = configModel.getRootInstance();
		final List<CsticGroupModel> csticGroups = new ArrayList<>();

		createGroup1WithCsticAndAssignedValues(rootInstance, csticGroups);
		createGroup2WithCsticAndAssignedValues(rootInstance, csticGroups);

		rootInstance.setCsticGroups(csticGroups);
		return configModel;
	}

	public static ConfigModel createConfigModelWith3GroupAndAssignedValues()
	{
		final ConfigModel configModel = createConfigModelWithCstic();

		final InstanceModel rootInstance = configModel.getRootInstance();
		final List<CsticGroupModel> csticGroups = new ArrayList<>();

		createGroup0WithInvisibleCsticAndAssignedValue(rootInstance, csticGroups);
		createGroup1WithCsticAndAssignedValues(rootInstance, csticGroups);
		createGroup2WithCsticAndAssignedValues(rootInstance, csticGroups);

		rootInstance.setCsticGroups(csticGroups);
		return configModel;
	}


	protected static void createGroup1WithCsticAndAssignedValues(final InstanceModel rootInstance,
			final List<CsticGroupModel> csticGroups)
	{
		rootInstance.addCstic(createRadioButtonCsticWithValue2Assigned("CSTIC_1.2", CsticValueModel.AUTHOR_USER,
				CsticValueModel.AUTHOR_EXTERNAL_USER, true));
		rootInstance.addCstic(createRadioButtonCsticWithValue2Assigned("CSTIC_1.3", CsticValueModel.AUTHOR_USER,
				CsticValueModel.AUTHOR_EXTERNAL_USER, false));
		rootInstance
				.addCstic(createSTRCsticWithValue("CSTIC_1.4", "defaultValue", CsticValueModel.AUTHOR_USER, AUTHOR_EXTERNAL_DEFAULT));
		rootInstance.addCstic(createRadioButtonCsticWithValue2Assigned("CSTIC_1.5", CsticValueModel.AUTHOR_SYSTEM,
				AUTHOR_EXTERNAL_CONSTRAINT, true));

		final CsticGroupModel group1 = createCsticGroup("GROUP1", "Group 1", STR_NAME, "CSTIC_1.2", "CSTIC_1.3", "CSTIC_1.4",
				"CSTIC_1.5");
		csticGroups.add(group1);
	}

	protected static void createGroup1WithCsticAndAssignedValuesMultiValued(final InstanceModel rootInstance,
			final List<CsticGroupModel> csticGroups)
	{
		rootInstance.addCstic(createRadioButtonCsticWithValue2Assigned("CSTIC_1.2", CsticValueModel.AUTHOR_USER,
				CsticValueModel.AUTHOR_EXTERNAL_USER, true));
		rootInstance.addCstic(createRadioButtonCsticWithValue2Assigned("CSTIC_1.3", CsticValueModel.AUTHOR_USER,
				CsticValueModel.AUTHOR_EXTERNAL_USER, false));
		rootInstance.addCstic(createCheckBoxListCsticWithValue2AndValue3Assigned());
		rootInstance
				.addCstic(createSTRCsticWithValue("CSTIC_1.4", "defaultValue", CsticValueModel.AUTHOR_USER, AUTHOR_EXTERNAL_DEFAULT));
		rootInstance.addCstic(createRadioButtonCsticWithValue2Assigned("CSTIC_1.5", CsticValueModel.AUTHOR_SYSTEM,
				AUTHOR_EXTERNAL_CONSTRAINT, true));

		final CsticGroupModel group1 = createCsticGroup("GROUP1", "Group 1", STR_NAME, "CSTIC_1.2", "CSTIC_1.3", CHBOX_LIST_NAME,
				"CSTIC_1.4", "CSTIC_1.5");
		csticGroups.add(group1);
	}


	protected static void createGroup2WithCsticAndAssignedValues(final InstanceModel rootInstance,
			final List<CsticGroupModel> csticGroups)
	{
		rootInstance
				.addCstic(createSTRCsticWithValue("CSTIC_2.1", "defaultValue", CsticValueModel.AUTHOR_USER, AUTHOR_EXTERNAL_DEFAULT));
		rootInstance.addCstic(createRadioButtonCsticWithValue2Assigned("CSTIC_2.2", CsticValueModel.AUTHOR_USER, "8", false));
		rootInstance.addCstic(createRadioButtonCsticWithValue2Assigned("CSTIC_2.3", CsticValueModel.AUTHOR_USER,
				CsticValueModel.AUTHOR_EXTERNAL_USER, true));
		rootInstance.addCstic(createRadioButtonCstic());
		rootInstance.addCstic(createRadioButtonCsticWithValue2Assigned("CSTIC_2.5", CsticValueModel.AUTHOR_USER,
				CsticValueModel.AUTHOR_EXTERNAL_USER, true));

		final CsticGroupModel group2 = createCsticGroup("GROUP2", "Group 2", "CSTIC_2.1", "CSTIC_2.2", "CSTIC_2.3", RB_NAME,
				"CSTIC_2.5");
		csticGroups.add(group2);
	}

	protected static void createGroup0WithInvisibleCsticAndAssignedValue(final InstanceModel rootInstance,
			final List<CsticGroupModel> csticGroups)
	{
		rootInstance.addCstic(createRadioButtonCsticWithValue2Assigned("CSTIC_0.1", CsticValueModel.AUTHOR_USER, "8", false));

		final CsticGroupModel group0 = createCsticGroup("GROUP0", "Group 0", "CSTIC_0.1");
		csticGroups.add(group0);
	}


	public static ConfigModel createConfigModelWithGroupsAllVisible()
	{
		final ConfigModel configModel = createConfigModelWithCstic();

		configModel.setBasePrice(createPriceModel("EUR", BASE_PRICE));
		configModel.setSelectedOptionsPrice(createPriceModel("EUR", SELECTED_OPTIONS_PRICE));
		configModel.setCurrentTotalPrice(createPriceModel("EUR", TOTAL_PRICE));

		final InstanceModel rootInstance = configModel.getRootInstance();

		rootInstance.addCstic(createCheckBoxListCsticWithValue2Assigned());
		rootInstance.addCstic(createCheckBoxCsticVisible());

		final List<CsticGroupModel> csticGroups = new ArrayList<>();
		final CsticGroupModel group1 = createCsticGroup("GROUP1", "Group 1", STR_NAME, CHBOX_NAME);
		final CsticGroupModel group2 = createCsticGroup("GROUP2", "Group 2", CHBOX_LIST_NAME);

		csticGroups.add(group1);
		csticGroups.add(group2);

		rootInstance.setCsticGroups(csticGroups);

		return configModel;
	}

	protected static PriceModel createPriceModel(final String currency, final BigDecimal priceValue)
	{
		final PriceModel basePrice = new PriceModelImpl();
		basePrice.setCurrency(currency);
		basePrice.setPriceValue(priceValue);
		return basePrice;
	}


	public static ConfigModel createConfigModelWithOneGroup()
	{
		final ConfigModel configModel = createConfigModelWithCstic();

		final InstanceModel rootInstance = configModel.getRootInstance();

		rootInstance.addCstic(createCheckBoxListCsticWithValue2Assigned());
		rootInstance.addCstic(createCheckBoxCstic());

		return configModel;
	}

	public static ConfigModel createConfigModel()
	{
		final ConfigModel model = new ConfigModelImpl();

		model.setId("WCEM_TEST_PRODUCT");
		model.setName(CONFIG_NAME);
		model.setComplete(false);
		model.setConsistent(true);

		final InstanceModel rootInstance = createRootInstance();

		final List<InstanceModel> subInstances = new ArrayList<>();
		final InstanceModel subInstance = setSubInstance("Disselected UI Types");
		subInstances.add(subInstance);

		rootInstance.setSubInstances(subInstances);
		model.setRootInstance(rootInstance);

		return model;
	}

	public static ConfigModel createEmptyConfigModel()
	{
		final ConfigModel model = new ConfigModelImpl();

		model.setId(CONFIG_ID);
		model.setName(CONFIG_NAME);
		model.setComplete(false);
		model.setConsistent(true);

		// Root Instance
		final InstanceModel rootInstance;
		rootInstance = createInstance();

		model.setRootInstance(rootInstance);

		return model;
	}

	public static ConfigModel createConfigModelWithSubInstanceOnly()
	{
		final ConfigModel model = createEmptyConfigModel();

		final InstanceModel rootInstance = model.getRootInstance();


		final List<InstanceModel> subInstances = rootInstance.getSubInstances();
		subInstances.add(createInstance());
		rootInstance.setSubInstances(subInstances);

		return model;
	}

	public static InstanceModel createRootInstance()
	{
		final InstanceModel rootInstance = new InstanceModelImpl();
		rootInstance.setId("WCEM_TEST_PRODUCT");
		rootInstance.setName(ROOT_INSTANCE_NAME);
		rootInstance.setLanguageDependentName("Selected UI Types");

		final List<CsticModel> cstics = new ArrayList<>();
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setLanguageDependentName("Radio Button Group");

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		final CsticValueModel assignableValue1 = createCsticValue("Value 1", "4.99");
		assignableValues.add(assignableValue1);
		final CsticValueModel assignableValue2 = createCsticValue("Value 2", "2.99");
		assignableValues.add(assignableValue2);
		final CsticValueModel assignableValue3 = createCsticValue("Value 3", "0.99");
		assignableValues.add(assignableValue3);
		cstic.setAssignableValues(assignableValues);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModel assignedValue = createCsticValue("Value 1", "4.99");
		assignedValues.add(assignedValue);
		cstic.setAssignedValues(assignedValues);
		cstic.setVisible(true);
		cstics.add(cstic);

		rootInstance.setCstics(cstics);

		return rootInstance;
	}

	public static InstanceModel setSubInstance(final String languageDependentName)
	{
		final InstanceModel subInstance = new InstanceModelImpl();
		subInstance.setLanguageDependentName(languageDependentName);

		final List<CsticModel> cstics = new ArrayList<>();
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setLanguageDependentName(languageDependentName);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		final CsticValueModel assignableValue1 = createCsticValue("Flag 1", "6.33");
		assignableValues.add(assignableValue1);
		final CsticValueModel assignableValue2 = createCsticValue("Flag 2", "9.11");
		assignableValues.add(assignableValue2);
		final CsticValueModel assignableValue3 = createCsticValue("Flag 3", "1.55");
		assignableValues.add(assignableValue3);
		cstic.setAssignableValues(assignableValues);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModel assignedValue = createCsticValue("Flag 3", "1.55");
		assignedValues.add(assignedValue);
		cstic.setAssignedValues(assignedValues);
		cstics.add(cstic);

		subInstance.setCstics(cstics);

		return subInstance;
	}

	public static CsticValueModel createCsticValue(final String languageDependentName, final String price)
	{
		final CsticValueModel assignableValue = new CsticValueModelImpl();
		assignableValue.setLanguageDependentName(languageDependentName);
		final PriceModel deltaPrice = new PriceModelImpl();
		deltaPrice.setCurrency("EURO");
		deltaPrice.setPriceValue(new BigDecimal(price));
		assignableValue.setDeltaPrice(deltaPrice);

		return assignableValue;
	}

	/**
	 * @return
	 */
	private static InstanceModel createInstance()
	{
		final InstanceModel rootInstance;
		rootInstance = new InstanceModelImpl();
		rootInstance.setId(ROOT_INSTANCE_ID);
		rootInstance.setName(ROOT_INSTANCE_NAME);
		rootInstance.setLanguageDependentName(ROOT_INSTANCE_LANG_DEP_NAME);
		rootInstance.setRootInstance(true);
		rootInstance.setComplete(false);
		rootInstance.setConsistent(true);
		rootInstance.setSubInstances(new ArrayList<>());
		return rootInstance;
	}

	public static ConfigModel createConfigModelWithSubInstance()
	{
		final ConfigModel model = new ConfigModelImpl();

		model.setId(CONFIG_ID);
		model.setName(CONFIG_NAME);
		model.setComplete(false);
		model.setConsistent(true);

		// Root Instance
		final InstanceModel rootInstance = new InstanceModelImpl();
		rootInstance.setId(ROOT_INSTANCE_ID);
		rootInstance.setName(ROOT_INSTANCE_NAME);
		rootInstance.setLanguageDependentName(ROOT_INSTANCE_LANG_DEP_NAME);
		rootInstance.setRootInstance(true);
		rootInstance.setComplete(false);
		rootInstance.setConsistent(true);
		final ArrayList<InstanceModel> subInstances = new ArrayList<>();
		subInstances.add(createSubInstance("SUBINSTANCE1"));
		rootInstance.setSubInstances(subInstances);

		model.setRootInstance(rootInstance);

		// Characteristics and Values

		final List<CsticModel> cstics = new ArrayList<>();
		cstics.add(createSTRCstic());
		rootInstance.setCstics(cstics);

		return model;
	}

	public static ConfigModel createConfigModelWithGroupsAndSubInstances()
	{
		final ConfigModel model = createConfigModelWithGroups();

		final ArrayList<InstanceModel> subInstancesLevel1 = new ArrayList<>();
		final InstanceModel subInstance1Level1 = createSubInstance("SUBINSTANCE1LEVEL1");
		subInstancesLevel1.add(subInstance1Level1);

		final List<CsticGroupModel> csticGroups = new ArrayList<>();
		final CsticGroupModel group1 = createCsticGroup("GROUP1INST1", "Group 1", STR_NAME, CHBOX_NAME);
		final CsticGroupModel group2 = createCsticGroup("GROUP2INST1", "Group 2", CHBOX_LIST_NAME);
		csticGroups.add(group1);
		csticGroups.add(group2);
		subInstance1Level1.setCsticGroups(csticGroups);
		List<CsticModel> cstics = new ArrayList<>();
		cstics.add(createSTRCstic());
		cstics.add(createCheckBoxCstic());
		cstics.add(createCheckBoxListCsticWithValue2Assigned());
		subInstance1Level1.setCstics(cstics);


		final ArrayList<InstanceModel> subInstancesLevel2 = new ArrayList<>();
		final InstanceModel subInstance1Level2 = createSubInstance("SUBINSTANCE1LEVEL2");
		cstics = new ArrayList<>();
		cstics.add(createSTRCstic());
		cstics.add(createCheckBoxCstic());
		cstics.add(createCheckBoxListCsticWithValue2Assigned());
		subInstance1Level2.setCstics(cstics);
		subInstancesLevel2.add(subInstance1Level2);
		subInstancesLevel2.add(createSubInstance("SUBINSTANCE2LEVEL2"));
		subInstance1Level1.setSubInstances(subInstancesLevel2);

		final ArrayList<InstanceModel> subInstancesLevel3 = new ArrayList<>();
		subInstancesLevel3.add(createSubInstance("SUBINSTANCE1LEVEL3"));
		subInstance1Level2.setSubInstances(subInstancesLevel3);

		subInstancesLevel1.add(createSubInstance("SUBINSTANCE2LEVEL1"));
		model.getRootInstance().setSubInstances(subInstancesLevel1);

		return model;
	}

	public static ConfigModel createConfigModelWithGroupsAndSubInstancesAllVisible()
	{
		final ConfigModel model = createConfigModelWithGroupsAllVisible();

		final ArrayList<InstanceModel> subInstancesLevel1 = new ArrayList<>();
		final InstanceModel subInstance1Level1 = createSubInstance("SUBINSTANCE1LEVEL1");
		subInstancesLevel1.add(subInstance1Level1);

		final List<CsticGroupModel> csticGroups = new ArrayList<>();
		final CsticGroupModel group1 = createCsticGroup("GROUP1INST1", "Group 1", STR_NAME, CHBOX_NAME);
		final CsticGroupModel group2 = createCsticGroup("GROUP2INST1", "Group 2", CHBOX_LIST_NAME);
		csticGroups.add(group1);
		csticGroups.add(group2);
		subInstance1Level1.setCsticGroups(csticGroups);
		List<CsticModel> cstics = new ArrayList<>();
		cstics.add(createSTRCstic());
		cstics.add(createCheckBoxCsticVisible());
		cstics.add(createCheckBoxListCsticWithValue2Assigned());
		subInstance1Level1.setCstics(cstics);


		final ArrayList<InstanceModel> subInstancesLevel2 = new ArrayList<>();
		final InstanceModel subInstance1Level2 = createSubInstance("SUBINSTANCE1LEVEL2");
		cstics = new ArrayList<>();
		cstics.add(createSTRCstic());
		cstics.add(createCheckBoxCsticVisible());
		cstics.add(createCheckBoxListCsticWithValue2Assigned());
		subInstance1Level2.setCstics(cstics);
		subInstancesLevel2.add(subInstance1Level2);
		subInstancesLevel2.add(createSubInstance("SUBINSTANCE2LEVEL2"));
		subInstance1Level1.setSubInstances(subInstancesLevel2);

		final ArrayList<InstanceModel> subInstancesLevel3 = new ArrayList<>();
		subInstancesLevel3.add(createSubInstance("SUBINSTANCE1LEVEL3"));
		subInstance1Level2.setSubInstances(subInstancesLevel3);

		subInstancesLevel1.add(createSubInstance("SUBINSTANCE2LEVEL1"));
		model.getRootInstance().setSubInstances(subInstancesLevel1);

		return model;
	}


	private static CsticGroupModel createCsticGroup(final String groupName, final String description, final String... csticNames)
	{
		final List<String> csticNamesInGroup = new ArrayList<>();
		for (final String csticName : csticNames)
		{
			csticNamesInGroup.add(csticName);
		}

		final CsticGroupModel csticGroup = new CsticGroupModelImpl();
		csticGroup.setName(groupName);
		csticGroup.setDescription(description);
		csticGroup.setCsticNames(csticNamesInGroup);

		return csticGroup;
	}

	public static CsticModel createSTRCstic()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(STR_NAME);
		cstic.setLanguageDependentName(STR_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		setDefaultProperties(cstic);
		cstic.setVisible(true);
		cstic.setLongText("Model long text");

		return cstic;
	}

	public static CsticModel createSTRCsticWithValue(final String name, final String value, final String author,
			final String authorExternal)
	{

		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(name);
		cstic.setLanguageDependentName(name.replace("_", " "));
		cstic.setValueType(CsticModel.TYPE_STRING);
		setDefaultProperties(cstic);
		cstic.setVisible(true);
		cstic.setLongText("Model long text");

		final CsticValueModel value3 = createCsticValue(value);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		value3.setAuthor(author);
		value3.setAuthorExternal(authorExternal);
		assignedValues.add(value3);
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		return cstic;
	}



	public static CsticModel createReadOnlyCstic()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(STR_NAME);
		cstic.setLanguageDependentName(STR_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		setDefaultProperties(cstic);

		cstic.setReadonly(true);

		return cstic;
	}

	public static CsticModel createCheckBoxCsticVisible()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(CHBOX_NAME);
		cstic.setLanguageDependentName(CHBOX_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(true);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(true);
		cstic.setSingleValue("X");

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName("X");
		assignableValues.add(value);
		cstic.setAssignableValues(assignableValues);
		cstic.setStaticDomainLength(assignableValues.size());

		return cstic;
	}

	public static CsticModel createCheckBoxCstic()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(CHBOX_NAME);
		cstic.setLanguageDependentName(CHBOX_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(true);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);
		cstic.setSingleValue("X");

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName("X");
		assignableValues.add(value);
		cstic.setAssignableValues(assignableValues);
		cstic.setStaticDomainLength(assignableValues.size());

		return cstic;
	}

	public static CsticModel createCheckBoxListCsticWithValue2Assigned()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(CHBOX_LIST_NAME);
		cstic.setLanguageDependentName(CHBOX_LIST_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(30);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(true);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(true);

		final CsticValueModelImpl value1 = createCsticValueModel(1);
		final CsticValueModelImpl value2 = createCsticValueModel(2);
		final CsticValueModelImpl value3 = createCsticValueModel(3);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		value2.setAuthor(CsticValueModel.AUTHOR_USER);
		value2.setAuthorExternal(CsticValueModel.AUTHOR_EXTERNAL_USER);
		assignedValues.add(value2);
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(value1);
		assignableValues.add(value2);
		assignableValues.add(value3);
		cstic.setAssignableValues(assignableValues);
		cstic.setStaticDomainLength(assignableValues.size());

		return cstic;
	}

	public static CsticModel createCheckBoxListCsticWithValue2AndValue3Assigned()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(CHBOX_LIST_NAME);
		cstic.setLanguageDependentName(CHBOX_LIST_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(30);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(true);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(true);

		final CsticValueModelImpl value1 = createCsticValueModel(1);
		final CsticValueModelImpl value2 = createCsticValueModel(2);
		final CsticValueModelImpl value3 = createCsticValueModel(3);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		value2.setAuthor(CsticValueModel.AUTHOR_USER);
		value2.setAuthorExternal(CsticValueModel.AUTHOR_EXTERNAL_USER);
		assignedValues.add(value2);
		value3.setAuthor(CsticValueModel.AUTHOR_USER);
		value3.setAuthorExternal(CsticValueModel.AUTHOR_EXTERNAL_USER);
		assignedValues.add(value3);
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(value1);
		assignableValues.add(value2);
		assignableValues.add(value3);
		cstic.setAssignableValues(assignableValues);
		cstic.setStaticDomainLength(assignableValues.size());

		return cstic;
	}


	public static CsticModel createNumericCsticWithIntervalValues()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(INT_NUM_NAME);
		cstic.setLanguageDependentName(INT_NUM_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		setDefaultProperties(cstic);
		cstic.setIntervalInDomain(true);

		return cstic;
	}

	private static CsticValueModelImpl createCsticValueModel(final int ii)
	{
		final CsticValueModelImpl value1 = new CsticValueModelImpl();
		value1.setName("VAL" + ii);
		value1.setLanguageDependentName("VALUE " + ii);
		value1.setDomainValue(true);
		return value1;
	}

	public static CsticModel createRadioButtonCstic()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(RB_NAME);
		cstic.setLanguageDependentName(RB_LD_NAME_FLOAT);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(createCsticValue("VALUE_1"));
		assignableValues.add(createCsticValue("VALUE_2"));
		cstic.setAssignableValues(assignableValues);

		return cstic;
	}


	public static CsticModel createRadioButtonCsticWithValue2Assigned(final String name, final String author,
			final String authorExternal, final boolean visible)
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(name);
		cstic.setLanguageDependentName(name.replace("_", " "));
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(visible);

		final CsticValueModel value1 = createCsticValue("VALUE_1");
		final CsticValueModel value2 = createCsticValue("VALUE_2");
		final CsticValueModel value3 = createCsticValue("VALUE_3");

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		value2.setAuthor(CsticValueModel.AUTHOR_USER);
		value2.setAuthorExternal(CsticValueModel.AUTHOR_EXTERNAL_USER);
		assignedValues.add(value2);
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(value1);
		assignableValues.add(value2);
		assignableValues.add(value3);
		cstic.setAssignableValues(assignableValues);

		return cstic;
	}


	public static CsticModel createRadioButtonCsticFloat()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(RB_LD_NAME_FLOAT);
		cstic.setLanguageDependentName(RB_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		cstic.setTypeLength(8);
		cstic.setNumberScale(2);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(createCsticValue("1234.56"));
		assignableValues.add(createCsticValue("1"));
		cstic.setAssignableValues(assignableValues);

		return cstic;
	}

	public static CsticModel createDropDownCstic()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(CHBOX_NAME);
		cstic.setLanguageDependentName(CHBOX_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(createCsticValue("VALUE_1"));
		assignableValues.add(createCsticValue("VALUE_2"));
		assignableValues.add(createCsticValue("VALUE_3"));
		assignableValues.add(createCsticValue("VALUE_4"));
		assignableValues.add(createCsticValue("VALUE_5"));
		cstic.setAssignableValues(assignableValues);

		return cstic;
	}

	private static CsticValueModel createCsticValue(final String value)
	{
		final CsticValueModel valueModel = new CsticValueModelImpl();
		valueModel.setName(value);
		valueModel.setLanguageDependentName(value);
		return valueModel;
	}

	private static CsticValueModel createCsticValueWithMessages(final String value, final String messageText)
	{
		final CsticValueModel valueModel = createCsticValue("VALUE_WITH_MESSAGE");
		final ProductConfigMessageBuilder builder = new ProductConfigMessageBuilder();
		builder.appendBasicFields(messageText, "messagekey1", ProductConfigMessageSeverity.INFO);
		builder.appendSourceAndType(ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
		valueModel.getMessages().add(builder.build());
		return valueModel;
	}


	public static CsticModel createUndefinedCstic()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName("DUMMY");
		cstic.setLanguageDependentName("dummy");
		cstic.setValueType(CsticModel.TYPE_UNDEFINED);
		setDefaultProperties(cstic);

		return cstic;
	}

	private static void setDefaultProperties(final CsticModel cstic)
	{
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		cstic.setAssignableValues(assignableValues);
	}

	public static CsticModel createFloatCstic()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		final String name = "Numeric";
		cstic.setName(name);
		cstic.setLanguageDependentName(name);
		setDefaultProperties(cstic);
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		cstic.setTypeLength(8);
		cstic.setNumberScale(2);
		cstic.setSingleValue("0");

		return cstic;
	}

	public static CsticModel createIntegerCstic()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		final String name = "Numeric";
		cstic.setName(name);
		cstic.setLanguageDependentName(name);
		setDefaultProperties(cstic);
		cstic.setValueType(CsticModel.TYPE_INTEGER);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setSingleValue("0");

		return cstic;
	}


	private static InstanceModel createSubInstance(final String instanceName)
	{
		final InstanceModel subInstance = new InstanceModelImpl();
		instanceId++;
		subInstance.setId(String.valueOf(instanceId));
		subInstance.setName(instanceName);
		return subInstance;
	}

	/**
	 * @return
	 */
	public static CsticModel createRadioButtonWithAddValueCstic()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(RB_ADDV_NAME);
		cstic.setLanguageDependentName(RB_ADDV_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);
		cstic.setAllowsAdditionalValues(true);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(createCsticValue("VALUE_1"));
		assignableValues.add(createCsticValue("VALUE_2"));
		cstic.setAssignableValues(assignableValues);

		return cstic;
	}

	/**
	 * @return
	 */
	public static CsticModel createDropDownWithAddValueCstic()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(DROPD_AADV_NAME);
		cstic.setLanguageDependentName(DROPD_ADDV_LD_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);
		cstic.setAllowsAdditionalValues(true);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(createCsticValue("VAL1"));
		assignableValues.add(createCsticValue("VAL2"));
		assignableValues.add(createCsticValue("VAL3"));
		assignableValues.add(createCsticValue("VAL4"));
		assignableValues.add(createCsticValue("VAL5"));
		cstic.setAssignableValues(assignableValues);

		return cstic;
	}

	/**
	 * @return cstic with messages
	 */
	public static CsticModel createRadioWithAddValueCsticandValueMessages()
	{
		final CsticModelImpl cstic = new CsticModelImpl();
		cstic.setName(DROPD_AADV_NAME);
		cstic.setLanguageDependentName(RB_ADDV_NAME);
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(8);
		cstic.setNumberScale(0);
		cstic.setComplete(false);
		cstic.setConsistent(true);
		cstic.setConstrained(true);
		cstic.setMultivalued(false);
		cstic.setReadonly(false);
		cstic.setRequired(true);
		cstic.setVisible(false);
		cstic.setAllowsAdditionalValues(true);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		cstic.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(createCsticValueWithMessages("VAL1", "message VAL1"));
		assignableValues.add(createCsticValueWithMessages("VAL2", "message_VAL2"));
		assignableValues.add(createCsticValueWithMessages("VAL3", "message_VAL3"));

		cstic.setAssignableValues(assignableValues);

		return cstic;
	}

	public static void addPlaceholder(final CsticModel model)
	{
		model.setPlaceholder(NUM_PLACEHOLDER);
	}

	public static void setAssignedValue(final String name, final CsticModel csticModel, final String author)
	{
		setAssignedValue(name, csticModel, author, null);
	}

	public static void setAssignedValue(final String name, final CsticModel csticModel, final String author, final String price)
	{
		final CsticValueModelImpl value = new CsticValueModelImpl();
		value.setName(name);
		value.setAuthorExternal(author);
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		assignedValues.add(value);
		csticModel.setAssignedValuesWithoutCheckForChange(assignedValues);
		setPrice(value, price);
	}

	public static void setAssignedValues(final CsticModel csticModel, final Map<String, String> valueNameAndAuthor)
	{
		setAssignedValues(csticModel, valueNameAndAuthor, null);
	}

	public static void setPrice(final CsticValueModel value, final String price)
	{
		if (price == null)
		{
			value.setValuePrice(PriceModel.NO_PRICE);
		}
		else
		{
			final PriceModel deltaPrice = new PriceModelImpl();
			deltaPrice.setPriceValue(new BigDecimal(price));
			deltaPrice.setCurrency("EUR");
			value.setValuePrice(deltaPrice);
		}
	}


	public static void setAssignedValues(final CsticModel csticModel, final Map<String, String> valueNameAndAuthor,
			final Map<String, String> valueAndPrice)
	{
		final CsticValueModelImpl value1 = createCsticValueModel(1);
		final CsticValueModelImpl value2 = createCsticValueModel(2);
		final CsticValueModelImpl value3 = createCsticValueModel(3);
		final CsticValueModelImpl value4 = createCsticValueModel(4);

		value2.setAuthorExternal(valueNameAndAuthor.get(value2.getName()));
		value3.setAuthorExternal(valueNameAndAuthor.get(value3.getName()));
		value4.setAuthorExternal(valueNameAndAuthor.get(value4.getName()));

		if (valueAndPrice != null)
		{
			setPrice(value2, valueAndPrice.get(value2.getName()));
			setPrice(value3, valueAndPrice.get(value3.getName()));
			setPrice(value4, valueAndPrice.get(value4.getName()));
		}
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		assignedValues.add(value2);
		assignedValues.add(value3);
		assignedValues.add(value4);
		csticModel.setAssignedValuesWithoutCheckForChange(assignedValues);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		assignableValues.add(value1);
		assignableValues.add(value2);
		assignableValues.add(value3);
		assignableValues.add(value4);
		csticModel.setAssignableValues(assignableValues);
		csticModel.setStaticDomainLength(assignableValues.size());
	}

}

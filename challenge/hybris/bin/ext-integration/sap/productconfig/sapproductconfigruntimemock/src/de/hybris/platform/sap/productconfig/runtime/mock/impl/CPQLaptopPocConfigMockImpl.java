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


public class CPQLaptopPocConfigMockImpl extends BaseRunTimeConfigMockImpl
{
	public static final String CONFIG_NAME = "Config Name";
	public static final String ROOT_INSTANCE_ID = ROOT_INST_ID;
	public static final String ROOT_INSTANCE_NAME = "CPQ_LAPTOP";
	public static final String ROOT_INSTANCE_LANG_DEP_NAME = "Laptop Professional Plus";
	public static final String KB_ID = "23";

	//Cstics:
	//EXP_NO_USERS
	public static final String EXP_NUMBER = "EXP_NUMBER";
	public static final String LANG_DEPENDENT_EXP_NUMBER = "Expected Number";
	//CPQ_DISPLAY:
	private static final String CPQ_DISPLAY = "CPQ_DISPLAY";
	private static final String LANG_DEPENDENT_NAME_DISPLAY = "Display";
	private static final String CPQ_DISPLAY_13 = "13";
	private static final String CPQ_DISPLAY_15 = "15";
	private static final String CPQ_DISPLAY_17 = "17";
	private static final String INCH_HD_NON_GLARE_13 = "13-inch HD+ Non-Glare";
	private static final String INCH_HD_NON_GLARE_LED_15 = "15-inch HD Non-Glare LED";
	private static final String INCH_HD_NON_GLARE_LED_17 = "17-inch HD+ Non-Glare LED";

	//CPQ_CPU
	protected static final String CPQ_CPU = "CPQ_CPU";
	private static final String LANG_DEPENDENT_NAME_CPU = "Processor";
	private static final String INTELI5_33 = "INTELI5_33";
	private static final String CPQ_CPU_2_6G = "2.6GHz Intel i5-6600k";
	private static final String INTELI5_35 = "INTELI5_35";
	private static final String CPQ_CPU_3_5G = "3.5GHz Intel i5-6600k";
	protected static final String INTELI7_34 = "INTELI7_34";
	private static final String CPQ_CPU_3_4G = "3.4GHz Intel i7-6700k";
	protected static final String INTELI7_40 = "INTELI7_40";
	private static final String CPQ_CPU_4G = "4.00GHz Intel i7-6700k";

	//CPQ_RAM
	private static final String CPQ_RAM = "CPQ_RAM";
	private static final String LANG_DEPENDENT_NAME_MEMORY = "Memory";
	private static final String CPQ_RAM_16GB = "16GB";
	private static final String CPQ_RAM_16GB_KINGSTON = "16GB DDR3 Kingston";
	private static final String CPQ_RAM_32GB = "32GB";
	private static final String CPQ_RAM_32GB_CORSAIR_DOMINATOR = "32GB Corsair Dominator";
	private static final String CPQ_RAM_8GB = "8GB";
	private static final String CPQ_RAM_8GB_DDR3_KINGSTON = "8GB DDR3 Kingston";

	//CPQ_MONITOR
	private static final String CPQ_MONITOR = "CPQ_MONITOR";
	private static final String LANG_DEPENDENT_NAME_MONITOR = "Secondary Monitor";
	private static final String NONE = "NONE";
	private static final String NONE_VALUE = "None";
	private static final String CPQ_MONITOR_27 = "27";
	private static final String CPQ_MONITOR_27_INCH_ASUS_ULTRA_WIDE_LED = "27-inch Asus Ultra-wide LED";
	private static final String CPQ_MONITOR_24 = "24";
	private static final String CPQ_MONITOR_24_INCH_ASUS_1MS_3D_GAMING = "24-inch Asus 1ms 3D Gaming";
	private static final String CPQ_MONITOR_24HD = "24HD";
	private static final String CPQ_MONITOR_24_INCH_ASUS_2MS_LED_HDMI = "24-inch Asus 2ms LED HDMI";
	private static final String CPQ_MONITOR_21 = "21";
	private static final String CPQ_MONITOR_21_INCH_ASUS_5MS_LED_DVI = "21-inch Asus 5ms LED DVI";

	//CPQ_PRINTER
	private static final String CPQ_PRINTER = "CPQ_PRINTER";
	private static final String LANG_DEPENDENT_NAME_PRINTER = "Printer";
	private static final String EPSONCOLOR = "EPSONCOLOR";
	private static final String EPSON_WORK_FORCE_PRO_COLOR = "Epson WorkForce Pro Color";
	private static final String BROTHER = "BROTHER";
	private static final String BROTHER_LASER_COLOR = "Brother Laser Color";
	private static final String EPSONBW = "EPSONBW";
	private static final String EPSON_EXPRESSION_BLACK_WHITE = "Epson Expression Black White";

	//CPQ_OS
	private static final String CPQ_OS = "CPQ_OS";
	private static final String LANG_DEPENDENT_NAME_OS = "Operating System";
	private static final String LINUSDEBIAN = "LINUSDEBIAN";
	private static final String LINUX_DEBIAN = "Linux Debian";
	private static final String LINUXESUSE = "LINUXESUSE";
	private static final String LINUX_OPENSUSE = "Linux OPENSUSE";
	private static final String MS10 = "MS10";
	private static final String MICROSOFT_WINDOWS_10 = "Microsoft Windows 10";
	private static final String MS8 = "MS8";
	private static final String MICROSOFT_WINDOWS_8_1 = "Microsoft Windows 8.1";

	//CPQ_SECURITY
	private static final String CPQ_SECURITY = "CPQ_SECURITY";
	private static final String LANG_DEPENDENT_NAME_SECURITY = "Security";
	private static final String MCAFEE = "MCAFEE";
	private static final String MC_AFEE_ANTIVIRUS_PLUS = "McAfee Antivirus Plus";
	private static final String TRUESCRIPT = "TRUESCRIPT";
	private static final String TRUE_SCRIPT = "TrueScript";
	private static final String KEEPASS = "KEEPASS";
	private static final String KEE_PASS_X = "KeePassX";
	private static final String BULLGUARD = "BULLGUARD";
	private static final String BULL_GUARD_INTERNET_SECURITY = "BullGuard Internet Security";
	private static final String NORTON = "NORTON";
	private static final String NORTON_SECURITY_DELUXE = "Norton Security Deluxe";

	//CPQ_SOFTWARE
	protected static final String CPQ_SOFTWARE = "CPQ_SOFTWARE";
	private static final String LANG_DEPENDENT_NAME_SOFTWARE = "Other Software";
	private static final String MAGIX_MUSIC = "MAGIX_MUSIC";
	private static final String MAGIX_MUSIC_MAKER = "Magix Music Maker";
	private static final String ADOBE_PHOTO = "ADOBE_PHOTO";
	private static final String ADOBE_PHOTO_SHOP = "Adobe PhotoShop";
	protected static final String GAMEBUILDER = "GAMEBUILDER";
	private static final String GAME_BUILDER_BASIC = "Game Builder Basic";
	private static final String PAINTER = "PAINTER";
	private static final String COREL_PAINTER = "Corel Painter";
	private static final String ADOBER = "ADOBER";
	private static final String ADOBE_READER = "Adobe  Reader";

	@Override
	public ConfigModel createDefaultConfiguration()
	{
		// Model
		final ConfigModel model = createDefaultConfigModel("Configuration for CPQ_LAPTOP " + getConfigId());
		model.setKbId(KB_ID);

		// root instance
		final InstanceModel rootInstance = createDefaultRootInstance(model, ROOT_INSTANCE_NAME, ROOT_INSTANCE_LANG_DEP_NAME);

		// cstic groups:
		final List<CsticGroupModel> csticGroups = createCsticGroupList();
		rootInstance.setCsticGroups(csticGroups);

		// cstics and Values:
		final List<CsticModel> cstics = new ArrayList<>();
		cstics.add(createExpNoUsersCstic());
		cstics.add(createDisplayCstic());
		cstics.add(createProcessorCstic());
		cstics.add(createMemoryCstic());
		cstics.add(createMonitorCstic());
		cstics.add(createPrinterCstic());
		cstics.add(createOperatingSystemCstic());
		cstics.add(createSecurityCstic());
		cstics.add(createSoftwareCstic());

		rootInstance.setCstics(cstics);

		return model;
	}

	@Override
	public void checkModel(final ConfigModel model)
	{
		super.checkModel(model);

		final InstanceModel rootInstance = model.getRootInstance();
		if (!INTELI7_40.equalsIgnoreCase(rootInstance.getCstic(CPQ_CPU).getSingleValue()))
		{
			final CsticModel cstic = rootInstance.getCstic(CPQ_SOFTWARE);
			if (cstic.getAssignableValues().size() < 5)
			{
				final CsticModel softwareCstic = createSoftwareCstic();
				cstic.setAssignableValues(softwareCstic.getAssignableValues());
			}
		}
	}

	protected CsticModel createExpNoUsersCstic()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INSTANCE_ID, ROOT_INSTANCE_NAME);
		builder.withName(EXP_NUMBER, LANG_DEPENDENT_EXP_NUMBER);
		builder.numericType(0, 10).simpleInput();
		builder.withDefaultUIState().required();
		return builder.build();
	}

	protected CsticModel createSoftwareCstic()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INSTANCE_ID, ROOT_INSTANCE_NAME);
		builder.withName(CPQ_SOFTWARE, LANG_DEPENDENT_NAME_SOFTWARE);
		builder.stringType().multiSelection();
		builder.addOption(ADOBER, ADOBE_READER).addOption(PAINTER, COREL_PAINTER).addOption(GAMEBUILDER, GAME_BUILDER_BASIC)
				.addOption(ADOBE_PHOTO, ADOBE_PHOTO_SHOP).addOption(MAGIX_MUSIC, MAGIX_MUSIC_MAKER);
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createSecurityCstic()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INSTANCE_ID, ROOT_INSTANCE_NAME);
		builder.withName(CPQ_SECURITY, LANG_DEPENDENT_NAME_SECURITY);
		builder.stringType().multiSelection();
		builder.addOption(NORTON, NORTON_SECURITY_DELUXE).addOption(BULLGUARD, BULL_GUARD_INTERNET_SECURITY)
				.addOption(KEEPASS, KEE_PASS_X).addOption(TRUESCRIPT, TRUE_SCRIPT).addOption(MCAFEE, MC_AFEE_ANTIVIRUS_PLUS);
		builder.withDefaultUIState().required();
		return builder.build();
	}

	protected CsticModel createOperatingSystemCstic()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INSTANCE_ID, ROOT_INSTANCE_NAME);
		builder.withName(CPQ_OS, LANG_DEPENDENT_NAME_OS);
		builder.stringType().singleSelection();
		builder.addSelectedOption(NONE, NONE_VALUE).addOption(MS8, MICROSOFT_WINDOWS_8_1).addOption(MS10, MICROSOFT_WINDOWS_10)
				.addOption(LINUXESUSE, LINUX_OPENSUSE).addOption(LINUSDEBIAN, LINUX_DEBIAN);
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createPrinterCstic()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INSTANCE_ID, ROOT_INSTANCE_NAME);
		builder.withName(CPQ_PRINTER, LANG_DEPENDENT_NAME_PRINTER);
		builder.stringType().singleSelection();
		builder.addSelectedOption(NONE, NONE_VALUE).addOption(EPSONBW, EPSON_EXPRESSION_BLACK_WHITE)
				.addOption(BROTHER, BROTHER_LASER_COLOR).addOption(EPSONCOLOR, EPSON_WORK_FORCE_PRO_COLOR);
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createMonitorCstic()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INSTANCE_ID, ROOT_INSTANCE_NAME);
		builder.withName(CPQ_MONITOR, LANG_DEPENDENT_NAME_MONITOR);
		builder.stringType().singleSelection();
		builder.addSelectedOption(NONE, NONE_VALUE).addOption(CPQ_MONITOR_21, CPQ_MONITOR_21_INCH_ASUS_5MS_LED_DVI)
				.addOption(CPQ_MONITOR_24HD, CPQ_MONITOR_24_INCH_ASUS_2MS_LED_HDMI)
				.addOption(CPQ_MONITOR_24, CPQ_MONITOR_24_INCH_ASUS_1MS_3D_GAMING)
				.addOption(CPQ_MONITOR_27, CPQ_MONITOR_27_INCH_ASUS_ULTRA_WIDE_LED);
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticModel createMemoryCstic()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INSTANCE_ID, ROOT_INSTANCE_NAME);
		builder.withName(CPQ_RAM, LANG_DEPENDENT_NAME_MEMORY);
		builder.stringType().singleSelection();
		builder.addSelectedOption(CPQ_RAM_8GB, CPQ_RAM_8GB_DDR3_KINGSTON).addOption(CPQ_RAM_32GB, CPQ_RAM_32GB_CORSAIR_DOMINATOR)
				.addOption(CPQ_RAM_16GB, CPQ_RAM_16GB_KINGSTON).addOption(CPQ_RAM_8GB, CPQ_RAM_8GB_DDR3_KINGSTON);
		builder.withDefaultUIState().required();
		return builder.build();
	}

	protected CsticModel createProcessorCstic()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INSTANCE_ID, ROOT_INSTANCE_NAME);
		builder.withName(CPQ_CPU, LANG_DEPENDENT_NAME_CPU);
		builder.stringType().singleSelection();
		builder.addOption(INTELI7_40, CPQ_CPU_4G).addOption(INTELI7_34, CPQ_CPU_3_4G).addSelectedOption(INTELI5_35, CPQ_CPU_3_5G)
				.addOption(INTELI5_33, CPQ_CPU_2_6G);
		builder.withDefaultUIState().required();
		return builder.build();
	}

	protected CsticModel createDisplayCstic()
	{

		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INSTANCE_ID, ROOT_INSTANCE_NAME);
		builder.withName(CPQ_DISPLAY, LANG_DEPENDENT_NAME_DISPLAY);
		builder.stringType().singleSelection();
		builder.addOption(CPQ_DISPLAY_13, INCH_HD_NON_GLARE_13).addOption(CPQ_DISPLAY_15, INCH_HD_NON_GLARE_LED_15)
				.addOption(CPQ_DISPLAY_17, INCH_HD_NON_GLARE_LED_17);
		builder.withDefaultUIState();
		return builder.build();
	}

	protected CsticValueModel createCsticValue(final String csticName, final String langDependenName)
	{
		CsticValueModel value;
		value = new CsticValueModelImpl();
		value.setName(csticName);
		value.setLanguageDependentName(langDependenName);
		value.setDomainValue(true);

		return value;
	}

	protected List<CsticGroupModel> createCsticGroupList()
	{
		final List<CsticGroupModel> groups = new ArrayList<>();

		// General group:
		addCsticGroup(groups, InstanceModel.GENERAL_GROUP_NAME, null, null);

		//First group:
		addCsticGroup(groups, "1", "Core components", EXP_NUMBER, CPQ_DISPLAY, CPQ_CPU, CPQ_RAM);

		//Second group:
		addCsticGroup(groups, "2", "Peripherals & Accessories", CPQ_MONITOR, CPQ_PRINTER);

		//Third group:
		addCsticGroup(groups, "3", "Software", CPQ_OS, CPQ_SECURITY, CPQ_SOFTWARE);

		return groups;
	}
}

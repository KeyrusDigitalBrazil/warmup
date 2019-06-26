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
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;


@SuppressWarnings("javadoc")
public class DummyConfigurationKD990SolImpl extends ConfigurationImpl
{
	private static final String ROOT_INSTANCE = "{\"id\":\"00000029\",\"objectType\":\"MARA\",\"objectKey\":\"KD990SOL\",\"objectText\":\"SolarInstallation\",\"classType\":\"300\",\"author\":\"\",\"quantity\":\"1.000\",\"quantityUnit\":\"\",\"consistent\":true,\"complete\":true}";
	private static final String INSTANCE_0 = "{\"id\":\"00000029\",\"objectType\":\"MARA\",\"objectKey\":\"KD990SOL\",\"objectText\":\"SolarInstallation\",\"classType\":\"300\",\"author\":\"\",\"quantity\":\"1.000\",\"quantityUnit\":\"\",\"consistent\":true,\"complete\":true}";
	private static final String INSTANCE_1 = "{\"id\":\"00000030\",\"objectType\":\"MARA\",\"objectKey\":\"KD990WRES\",\"objectText\":\"WaterReservoir\",\"classType\":\"300\",\"author\":\"\",\"quantity\":\"1.000\",\"quantityUnit\":\"ST\",\"consistent\":true,\"complete\":true}";
	private static final String INSTANCE_2 = "{\"id\":\"00000031\",\"objectType\":\"MARA\",\"objectKey\":\"KD990EMET\",\"objectText\":\"PowerMeter\",\"classType\":\"300\",\"author\":\"\",\"quantity\":\"1.000\",\"quantityUnit\":\"ST\",\"consistent\":true,\"complete\":true}";
	private static final String INSTANCE_3 = "{\"id\":\"00000032\",\"objectType\":\"MARA\",\"objectKey\":\"KD990WLINE\",\"objectText\":\"Waterline\",\"classType\":\"300\",\"author\":\"\",\"quantity\":\"1.000\",\"quantityUnit\":\"ST\",\"consistent\":true,\"complete\":true}";
	private static final String PART_OF_RELATION_0 = "{\"instId\":\"00000030\",\"parentInstId\":\"00000029\",\"posNr\":\"0010\",\"objectType\":\"MARA\",\"objectKey\":\"KD990WRES\",\"classType\":\"300\",\"author\":\"\"}";
	private static final String PART_OF_RELATION_1 = "{\"instId\":\"00000031\",\"parentInstId\":\"00000029\",\"posNr\":\"0020\",\"objectType\":\"MARA\",\"objectKey\":\"KD990EMET\",\"classType\":\"300\",\"author\":\"\"}";
	private static final String PART_OF_RELATION_2 = "{\"instId\":\"00000032\",\"parentInstId\":\"00000030\",\"posNr\":\"0010\",\"objectType\":\"MARA\",\"objectKey\":\"KD990WLINE\",\"classType\":\"300\",\"author\":\"\"}";
	private static final String CHARACTERISTIC_VALUE_0 = "{\"instId\":\"00000029\",\"characteristic\":\"CHHI_HX\",\"characteristicText\":\"HousetopX(m)\",\"value\":\"4.00\",\"valueText\":\"4,00MET\",\"author\":\"\",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_1 = "{\"instId\":\"00000029\",\"characteristic\":\"CHHI_HY\",\"characteristicText\":\"HousetopY(m)\",\"value\":\"4.00\",\"valueText\":\"4,00MET\",\"author\":\"\",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_2 = "{\"instId\":\"00000029\",\"characteristic\":\"CHHI_HSU\",\"characteristicText\":\"HousetopSurface\",\"value\":\"16.00\",\"valueText\":\"16,00MET\",\"author\":\"4\",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_3 = "{\"instId\":\"00000029\",\"characteristic\":\"CHHI_HS\",\"characteristicText\":\"HousetopSurfaceCategory\",\"value\":\"B\",\"valueText\":\"Big\",\"author\":\"4\",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_4 = "{\"instId\":\"00000029\",\"characteristic\":\"CHHI_MAN\",\"characteristicText\":\"PanelManufacturer\",\"value\":\"B\",\"valueText\":\"BenQ\",\"author\":\"4\",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_5 = "{\"instId\":\"00000029\",\"characteristic\":\"CHHI_BATT\",\"characteristicText\":\"Battery\",\"value\":\"T\",\"valueText\":\"Tesla\",\"author\":\"\",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_6 = "{\"instId\":\"00000029\",\"characteristic\":\"CHHI_INS\",\"characteristicText\":\"Insurance\",\"value\":\"N\",\"valueText\":\"No\",\"author\":\"8\",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_7 = "{\"instId\":\"00000029\",\"characteristic\":\"SOL_WATER\",\"characteristicText\":\"WaterHeatingIncluded\",\"value\":\"Y\",\"valueText\":\"Yes\",\"author\":\"\",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_8 = "{\"instId\":\"00000030\",\"characteristic\":\"WRES_LOCATION\",\"characteristicText\":\"ReservoirLocation\",\"value\":\"C\",\"valueText\":\"Cellar\",\"author\":\"8\",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_9 = "{\"instId\":\"00000030\",\"characteristic\":\"WRES_PROTECTION\",\"characteristicText\":\"Protection\",\"value\":\"02\",\"valueText\":\"Homeenvironment\",\"author\":\"\",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_10 = "{\"instId\":\"00000031\",\"characteristic\":\"EMET_CONNECT\",\"characteristicText\":\"Connectiontype\",\"value\":\"B\",\"valueText\":\"Bi\",\"author\":\"8\",\"invisible\":false}";


	private final ObjectMapper objectMapper = new ObjectMapper();

	public DummyConfigurationKD990SolImpl()
	{
		super();
		try
		{
			setKbKey(new KBKeyImpl("KD990SOL"));
			fillMultiLevelConfiguration();
		}
		catch (final IOException exc)
		{
			throw new IllegalStateException("Could not create configuration object", exc);
		}
	}

	private void fillMultiLevelConfiguration() throws IOException
	{

		setRootInstance(objectMapper.readValue(ROOT_INSTANCE, InstanceImpl.class));
		addInstance(objectMapper.readValue(INSTANCE_0, InstanceImpl.class));
		addInstance(objectMapper.readValue(INSTANCE_1, InstanceImpl.class));
		addInstance(objectMapper.readValue(INSTANCE_2, InstanceImpl.class));
		addInstance(objectMapper.readValue(INSTANCE_3, InstanceImpl.class));

		addPartOfRelation(objectMapper.readValue(PART_OF_RELATION_0, PartOfRelationImpl.class));
		addPartOfRelation(objectMapper.readValue(PART_OF_RELATION_1, PartOfRelationImpl.class));
		addPartOfRelation(objectMapper.readValue(PART_OF_RELATION_2, PartOfRelationImpl.class));

		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_0, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_1, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_2, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_3, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_4, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_5, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_6, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_7, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_8, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_9, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_10, CharacteristicValueImpl.class));
	}

}

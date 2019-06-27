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

import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@SuppressWarnings("javadoc")
public class DummyConfigurationWecDragonCarImpl extends ConfigurationImpl
{

	private static final String ROOT_INSTANCE = "{\"id\":\"00000001\",\"objectType\":\"MARA\",\"objectKey\":\"WEC_DRAGON_CAR\",\"objectText\":\"WEC_DRAGON_CAR product\",\"classType\":\"300\",\"author\":\" \",\"quantity\":\"1.000\",\"quantityUnit\":\"\",\"consistent\":true,\"complete\":true}";
	private static final String CHARACTERISTIC_VALUE_0 = "{\"instId\":\"0000001\",\"characteristic\":\"WEC_DC_ENGINE\",\"characteristicText\":\"Car Engine\",\"value\":\"H\",\"valueText\":\"Hybrid Engine\",\"author\":\" \",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_1 = "{\"instId\":\"0000001\",\"characteristic\":\"WEC_DC_PEARLESCENT\",\"characteristicText\":\"Car Finish\",\"value\":\"N\",\"valueText\":\"No\",\"author\":\"4\",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_2a = "{\"instId\":\"0000001\",\"characteristic\":\"WEC_DC_ACCESSORY\",\"characteristicText\":\"Dragon Accessories\",\"value\":\"CUPH\",\"valueText\":\"Cup Holder\",\"author\":\" \",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_2b = "{\"instId\":\"0000001\",\"characteristic\":\"WEC_DC_ACCESSORY\",\"characteristicText\":\"Dragon Accessories\",\"value\":\"GPS\",\"valueText\":\"Navigation\",\"author\":\" \",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_3 = "{\"instId\":\"0000001\",\"characteristic\":\"WEC_DC_COLOR\",\"characteristicText\":\"Car Color\",\"value\":\"RD\",\"valueText\":\"Red\",\"author\":\" \",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_4 = "{\"instId\":\"0000001\",\"characteristic\":\"WEC_DC_LIGHTNING\",\"characteristicText\":\"Car Lightning\",\"value\":\"S\",\"valueText\":\"Standard Lighning\",\"author\":\" \",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_5 = "{\"instId\":\"0000001\",\"characteristic\":\"WEC_DC_REGULATIONS\",\"characteristicText\":\"Regulations\",\"value\":\"EU_R\",\"valueText\":\"EU Renewable energy act\",\"author\":\"4\",\"invisible\":false}";
	private static final String CHARACTERISTIC_VALUE_6 = "{\"instId\":\"0000001\",\"characteristic\":\"WEC_DC_ROAD_PERMISSION\",\"characteristicText\":\"Road Permission\",\"value\":\"EU\",\"valueText\":\"EU Road Permission\",\"author\":\" \",\"invisible\":false}";


	private final ObjectMapper objectMapper = new ObjectMapper();

	public DummyConfigurationWecDragonCarImpl()
	{
		super();
		try
		{
			setKbKey(new KBKeyImpl("WEC_DRAGON_CAR"));
			fillConfigValue();
		}
		catch (final IOException exc)
		{
			throw new IllegalStateException("Could not create configuration object", exc);
		}
	}


	private void fillConfigValue() throws JsonParseException, JsonMappingException, IOException
	{
		setRootInstance(objectMapper.readValue(ROOT_INSTANCE, InstanceImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_0, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_1, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_2a, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_2b, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_3, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_4, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_5, CharacteristicValueImpl.class));
		addCharacteristicValue(objectMapper.readValue(CHARACTERISTIC_VALUE_6, CharacteristicValueImpl.class));

	}
}

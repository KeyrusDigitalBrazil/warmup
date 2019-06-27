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
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.bootstrap.annotations.UnitTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSItemLinkToggleModelToDataAttributePopulatorTest
{
	@InjectMocks
	private CMSItemLinkToggleModelToDataAttributePopulator populator;

	private Map<String, Object> itemMap = new HashMap<>();

	@Test
	public void testWhenLinkToggleIsCreated_shouldCreateObjectWithNewExternalAndUrlLinkFieldsAndRemoveOldFields()
	{
		// GIVEN
		itemMap.put("fakeParam", "fakeValue");
		itemMap.put(FIELD_URL_LINK_NAME, "urlLinkData");
		itemMap.put(FIELD_EXTERNAL_NAME, false);
		assertThat(itemMap.size(), is(3));

		// WHEN
		populator.populate(null, itemMap);

		// THEN
		assertThat(itemMap.size(), is(2));
		assertThat(itemMap.containsKey(FIELD_EXTERNAL_NAME), is(false));
		assertThat(itemMap.containsKey(FIELD_URL_LINK_NAME), is(false));

		assertThat(itemMap.get(FIELD_LINK_TOGGLE_NAME) instanceof Map, is(true));
		Map<String, Object> linkToggle = (HashMap<String, Object>) itemMap.get(FIELD_LINK_TOGGLE_NAME);
		assertThat(linkToggle.get(FIELD_EXTERNAL_NAME), is(false));
		assertThat(linkToggle.get(FIELD_URL_LINK_NAME), is("urlLinkData"));
	}
}

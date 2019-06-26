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
package de.hybris.platform.cmsoccaddon.jaxb.adapters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsoccaddon.data.ComponentListWsDTO;
import de.hybris.platform.cmsoccaddon.data.ComponentWsDTO;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentListWsDTOAdapter.ListAdaptedComponents;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


/**
 * JUnit Tests for the ComponentListWsDTOAdapter
 */
@UnitTest
public class ComponentListWsDTOAdapterTest
{
	private static final String TEST_NAME = "TestName";

	private final ComponentListWsDTOAdapter componentListWsDTOAdapter = new ComponentListWsDTOAdapter();
	private final ComponentListWsDTO componentList = new ComponentListWsDTO();

	@Test
	public void shouldNotMarshalEmptyComponentList()
	{
		final ListAdaptedComponents listResult = componentListWsDTOAdapter.marshal(componentList);

		assertThat(listResult, equalTo(null));
	}

	@Test
	public void shouldNotMarshalNullComponentList()
	{
		final ListAdaptedComponents listResult = componentListWsDTOAdapter.marshal(null);

		assertThat(listResult, equalTo(null));
	}

	@Test
	public void shouldMarshalComponentList()
	{
		final ComponentWsDTO componentWsDTO = new ComponentWsDTO();
		final List<ComponentWsDTO> listComponentWsDTO = new ArrayList<ComponentWsDTO>();
		componentWsDTO.setName(TEST_NAME);
		listComponentWsDTO.add(componentWsDTO);
		componentList.setComponent(listComponentWsDTO);

		final ListAdaptedComponents listResult = componentListWsDTOAdapter.marshal(componentList);

		assertThat(listResult.components.get(0).name, equalTo(TEST_NAME));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationException() throws Exception
	{
		componentListWsDTOAdapter.unmarshal(null);
	}
}
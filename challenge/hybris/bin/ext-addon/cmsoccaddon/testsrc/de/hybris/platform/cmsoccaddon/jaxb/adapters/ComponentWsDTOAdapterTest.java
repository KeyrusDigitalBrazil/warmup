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
import de.hybris.platform.cmsoccaddon.data.ComponentWsDTO;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentAdapterUtil.ComponentAdaptedData;

import org.junit.Test;


/**
 * JUnit Tests for the ComponentWsDTOAdapter
 */
@UnitTest
public class ComponentWsDTOAdapterTest
{
	private static final String TEST_NAME = "TestName";

	ComponentWsDTOAdapter componentWsDTOAdapter = new ComponentWsDTOAdapter();

	@Test
	public void shouldNotMarshalNullComponent()
	{
		final ComponentAdaptedData componentAdaptedDataResult = componentWsDTOAdapter.marshal(null);

		assertThat(componentAdaptedDataResult, equalTo(null));
	}

	@Test
	public void shouldMarshalComponent()
	{
		final ComponentWsDTO componentWsDTO = new ComponentWsDTO();
		componentWsDTO.setName(TEST_NAME);

		final ComponentAdaptedData componentAdaptedDataResult = componentWsDTOAdapter.marshal(componentWsDTO);

		assertThat(componentAdaptedDataResult.name, equalTo(TEST_NAME));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationException() throws Exception
	{
		componentWsDTOAdapter.unmarshal(null);
	}
}
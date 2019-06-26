/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.cockpitng.dataaccess.facades.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.search.data.SearchAttributeDescriptor;
import com.hybris.cockpitng.search.data.SearchQueryCondition;
import com.hybris.cockpitng.search.data.SearchQueryConditionList;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.pageable.Pageable;


@RunWith(MockitoJUnitRunner.class)
public class SubtypesSearchFacadeTest
{
	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private TypeService typeService;
	@InjectMocks
	private SubtypesSearchFacade facade;

	@Before
	public void setUp()
	{
		final List<ComposedTypeModel> mocks = Arrays.asList(createSubtypeMock("Customer"), createSubtypeMock("Employee"),
				createSubtypeMock("TestEmployee"));
		given(composedTypeModel.getAllSubTypes()).willReturn(mocks);
		given(typeService.getComposedTypeForCode("User")).willReturn(composedTypeModel);
		given(typeService.getComposedTypeForClass(any())).willReturn(composedTypeModel);
	}

	@Test
	public void shouldFilterTypesByTypedText()
	{
		// given
		final String typedText = "cust";

		// when
		final List<ComposedTypeModel> result = facade.filterTypesByNames(new ArrayList<>(composedTypeModel.getAllSubTypes()),
				typedText);

		// then
		assertThat(result.get(0).getName()).isEqualTo(CustomerModel._TYPECODE);
	}

	@Test
	public void shouldReturnNotEmptyPageableWhenCodePassed()
	{
		// given
		final SearchQueryData mock = createSearchQueryMock("User");

		// when
		final Pageable pageable = facade.search(mock);

		// then
		assertThat(pageable.getAllResults()).isNotEmpty();
	}

	@Test
	public void shouldReturnEmptyPageableWhenCodeNotPassed()
	{
		// given
		final SearchQueryData mock = mock(SearchQueryData.class);
		given(mock.getPageSize()).willReturn(10);

		// when
		final Pageable pageable = facade.search(mock);

		// then
		assertThat(pageable.getAllResults()).isEmpty();
	}

	private SearchQueryData createSearchQueryMock(final String codeValue)
	{
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		final SearchQueryConditionList searchQueryConditionList = mock(SearchQueryConditionList.class);
		final SearchQueryCondition searchQueryCondition = mock(SearchQueryCondition.class);
		final SearchAttributeDescriptor searchAttributeDescriptor = mock(SearchAttributeDescriptor.class);

		given(searchAttributeDescriptor.getAttributeName()).willReturn(SubtypesSearchFacade.CODE);
		given(searchQueryCondition.getValue()).willReturn(codeValue);
		given(searchQueryCondition.getDescriptor()).willReturn(searchAttributeDescriptor);
		given(searchQueryConditionList.getConditions()).willReturn(Lists.newArrayList(searchQueryCondition));
		final List list = Lists.newArrayList(searchQueryConditionList);
		given(searchQueryData.getConditions()).willReturn(list);
		given(searchQueryData.getPageSize()).willReturn(10);

		return searchQueryData;
	}

	private ComposedTypeModel createSubtypeMock(final String name)
	{
		final ComposedTypeModel typeModel = mock(ComposedTypeModel.class);
		given(typeModel.getName()).willReturn(name);
		return typeModel;
	}

}

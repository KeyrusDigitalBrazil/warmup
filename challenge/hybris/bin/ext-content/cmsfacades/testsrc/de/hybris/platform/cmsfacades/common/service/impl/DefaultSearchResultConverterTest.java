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
package de.hybris.platform.cmsfacades.common.service.impl;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSearchResultConverterTest
{
	private AbstractRestrictionModel model1;
	private AbstractRestrictionModel model2;
	private MockRestrictionData data1;
	private MockRestrictionData data2;
	@Mock
	private Function<AbstractRestrictionModel, MockRestrictionData> convertFunction;

	private final DefaultSearchResultConverter converter = new DefaultSearchResultConverter();

	@Before
	public void setUp()
	{
		when(convertFunction.apply(any())).thenReturn(data1).thenReturn(data2);
	}

	@Test
	public void shouldConvertSearchResultModelToData()
	{
		final List<AbstractRestrictionModel> models = Arrays.asList(model1, model2);
		final SearchResult<AbstractRestrictionModel> modelSearchResult = new SearchResultImpl<>(models, 10, 2, 0);

		final SearchResult<MockRestrictionData> result = converter.convert(modelSearchResult, convertFunction);
		assertThat(result.getResult(), contains(data1, data2));
		assertThat(result.getTotalCount(), equalTo(10));
		assertThat(result.getRequestedCount(), equalTo(2));
		assertThat(result.getRequestedStart(), equalTo(0));
	}

}

class MockRestrictionData
{
	public String uid;
	public String name;
}

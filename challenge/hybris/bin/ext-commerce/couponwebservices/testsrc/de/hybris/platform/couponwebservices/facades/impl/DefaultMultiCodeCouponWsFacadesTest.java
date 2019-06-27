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
package de.hybris.platform.couponwebservices.facades.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.core.servicelayer.data.SortData;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.couponwebservices.dto.MultiCodeCouponWsDTO;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.paginated.dao.PaginatedGenericDao;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMultiCodeCouponWsFacadesTest
{
	@InjectMocks
	private DefaultMultiCodeCouponWsFacades multiCodeCouponWsFacades;
	@Mock
	private PaginatedGenericDao<MultiCodeCouponModel> couponPaginatedGenericDao;
	@Mock
	private Converter<MultiCodeCouponModel, MultiCodeCouponWsDTO> multiCodeCouponWsDTOConverter;
	@Mock
	private MultiCodeCouponModel multiCodeCouponModel;
	@Mock
	private MultiCodeCouponWsDTO mutliCodeCuponDTO;
	@Captor
	private ArgumentCaptor<SearchPageData<MultiCodeCouponModel>> searchPageDataArgumentCaptor;

	private final PaginationData pagination = new PaginationData();
	private final List<SortData> sorts = Collections.emptyList();

	@Test
	public void shouldRaiseExceptionWhenPaginationParameterIsNull() throws Exception
	{
		//when
		final Throwable exception = catchThrowable(() -> multiCodeCouponWsFacades.getCoupons(null, sorts));
		//then
		assertThat(exception).isExactlyInstanceOf(IllegalArgumentException.class).hasMessageContaining("pagination");
	}

	@Test
	public void shouldRaiseExceptionWhenSortsParameterIsNull() throws Exception
	{
		//when
		final Throwable exception = catchThrowable(() -> multiCodeCouponWsFacades.getCoupons(pagination, null));
		//then
		assertThat(exception).isExactlyInstanceOf(IllegalArgumentException.class).hasMessageContaining("sorts");
	}

	@Test
	public void shouldProvideConvertedCoupons() throws Exception
	{
		//given
		given(couponPaginatedGenericDao.find(anyObject())).willReturn(createResults(multiCodeCouponModel));
		given(multiCodeCouponWsDTOConverter.convert(multiCodeCouponModel)).willReturn(mutliCodeCuponDTO);
		//when
		final SearchPageData<MultiCodeCouponWsDTO> coupons = multiCodeCouponWsFacades.getCoupons(pagination, sorts);
		//then
		assertThat(coupons.getResults()).hasSize(1).contains(mutliCodeCuponDTO);
	}

	@Test
	public void shouldQueryCouponsWithInstrumentedPaginationAndSorting() throws Exception
	{
		//given
		given(couponPaginatedGenericDao.find(anyObject())).willReturn(createResults(multiCodeCouponModel));
		given(multiCodeCouponWsDTOConverter.convert(multiCodeCouponModel)).willReturn(mutliCodeCuponDTO);
		//when
		multiCodeCouponWsFacades.getCoupons(pagination, sorts);
		//then
		verify(couponPaginatedGenericDao).find(searchPageDataArgumentCaptor.capture());
		assertThat(searchPageDataArgumentCaptor.getValue().getSorts()).isEqualTo(sorts);
		assertThat(searchPageDataArgumentCaptor.getValue().getPagination()).isEqualTo(pagination);
	}

	private SearchPageData<MultiCodeCouponModel> createResults(final MultiCodeCouponModel multiCodeCouponModel)
	{
		final SearchPageData<MultiCodeCouponModel> searchPageData = new SearchPageData<>();
		searchPageData.setResults(Lists.newArrayList(multiCodeCouponModel));
		return searchPageData;
	}
}

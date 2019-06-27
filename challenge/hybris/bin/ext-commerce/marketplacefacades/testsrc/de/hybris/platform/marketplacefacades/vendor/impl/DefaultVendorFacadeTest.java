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
package de.hybris.platform.marketplacefacades.vendor.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@UnitTest
public class DefaultVendorFacadeTest
{
	private static final String VENDOR1_CODE = "vendor1";
	private static final String SORT_CODE = "byNameAsc";
	private static final int CURRENT_PAGE = 0;
	private static final int PAGE_SIZE = 2;
	private static final int NUM_OF_PAGES = 1;
	@Mock
	private VendorService vendorService;
	@Mock
	private Converter<VendorModel, VendorData> vendorConverter;

	private DefaultVendorFacade defaultVendorFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultVendorFacade = new DefaultVendorFacade();
		defaultVendorFacade.setVendorConverter(vendorConverter);
		defaultVendorFacade.setVendorService(vendorService);
	}

	@Test
	public void testGetPagedIndexVendors()
	{
		final SearchPageData<VendorModel> pageVendors = new SearchPageData<>();

		final VendorModel vendor1 = new VendorModel();
		vendor1.setCode(VENDOR1_CODE);
		final List<VendorModel> vendors = Arrays.asList(vendor1);

		final PaginationData paginnationData = new PaginationData();
		paginnationData.setCurrentPage(CURRENT_PAGE);
		paginnationData.setNumberOfPages(NUM_OF_PAGES);
		paginnationData.setPageSize(PAGE_SIZE);

		final SortData sortData = new SortData();
		sortData.setCode(SORT_CODE);
		final List<SortData> sorts = Arrays.asList(sortData);

		pageVendors.setResults(vendors);
		pageVendors.setPagination(paginnationData);
		pageVendors.setSorts(sorts);

		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(CURRENT_PAGE);
		pageableData.setSort(SORT_CODE);
		pageableData.setPageSize(PAGE_SIZE);


		Mockito.doAnswer(new Answer<VendorData>()
		{
			@Override
			public VendorData answer(final InvocationOnMock invocation)
			{
				final Object[] args = invocation.getArguments();
				final VendorModel vendorModel = (VendorModel) args[0];
				final VendorData vendorData = new VendorData();
				vendorData.setCode(vendorModel.getCode());
				return vendorData;
			}
		}).when(vendorConverter).convert(Mockito.any(VendorModel.class));

		given(vendorService.getIndexVendors(pageableData)).willReturn(pageVendors);

		final SearchPageData<VendorData> pageVendorData = defaultVendorFacade.getPagedIndexVendors(pageableData);

		assertEquals(1, pageVendorData.getResults().size());
		assertEquals(VENDOR1_CODE, pageVendorData.getResults().get(0).getCode());
		assertEquals(1, pageVendorData.getSorts().size());
		assertEquals(SORT_CODE, pageVendorData.getSorts().get(0).getCode());
		assertEquals(PAGE_SIZE, pageVendorData.getPagination().getPageSize());
		assertEquals(NUM_OF_PAGES, pageVendorData.getPagination().getNumberOfPages());
		assertEquals(CURRENT_PAGE, pageVendorData.getPagination().getCurrentPage());
	}
}

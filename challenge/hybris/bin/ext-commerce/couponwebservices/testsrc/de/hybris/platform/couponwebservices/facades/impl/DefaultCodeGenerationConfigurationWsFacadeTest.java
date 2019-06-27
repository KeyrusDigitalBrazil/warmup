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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;


import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.core.servicelayer.data.SortData;
import de.hybris.platform.couponservices.model.CodeGenerationConfigurationModel;
import de.hybris.platform.couponwebservices.CodeGenerationConfigurationNotFoundException;
import de.hybris.platform.couponwebservices.dto.CodeGenerationConfigurationWsDTO;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.paginated.dao.PaginatedGenericDao;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCodeGenerationConfigurationWsFacadeTest
{
	@InjectMocks
	private DefaultCodeGenerationConfigurationWsFacade facade;
	@Mock
	private PagedGenericDao<CodeGenerationConfigurationModel> codeGenerationConfigurationPagedGenericDao;
	@Mock
	private PaginatedGenericDao<CodeGenerationConfigurationModel> codeGenerationConfigurationPaginatedGenericDao;
	@Mock
	private Converter<CodeGenerationConfigurationModel, CodeGenerationConfigurationWsDTO> codeGenerationConfigurationWsDTOConverter;
	@Mock
	private CodeGenerationConfigurationModel codeGenerationConfiguration;
	@Captor
	private ArgumentCaptor<SearchPageData<CodeGenerationConfigurationModel>> captor;
	private List<SortData> sorts;
	private PaginationData pagination;

	@Before
	public void setUp() throws Exception
	{
		sorts = emptyList();
		pagination = new PaginationData();
	}


	@Test
	public void shouldProvideConvertedCodeGenerationConfigurations() throws Exception
	{
		//given
		final SearchPageData<CodeGenerationConfigurationModel> searchPageData = createDaoResult(codeGenerationConfiguration);
		final CodeGenerationConfigurationWsDTO codeConfigurationDTO = new CodeGenerationConfigurationWsDTO();
		given(codeGenerationConfigurationWsDTOConverter.convert(codeGenerationConfiguration)).willReturn(codeConfigurationDTO);
		given(codeGenerationConfigurationPaginatedGenericDao.find(anyObject())).willReturn(searchPageData);
		//when
		final SearchPageData<CodeGenerationConfigurationWsDTO> results = facade.getCodeGenerationConfigurations(pagination, sorts);
		//then
		assertThat(results.getResults()).containsExactly(codeConfigurationDTO);
	}

	@Test
	public void shouldIssueQueryWithPaginationAndSortInstrumentation() throws Exception
	{
		//given
		final SearchPageData<CodeGenerationConfigurationModel> searchPageData = createDaoResult(codeGenerationConfiguration);
		final CodeGenerationConfigurationWsDTO codeConfigurationDTO = new CodeGenerationConfigurationWsDTO();
		given(codeGenerationConfigurationWsDTOConverter.convert(codeGenerationConfiguration)).willReturn(codeConfigurationDTO);
		given(codeGenerationConfigurationPaginatedGenericDao.find(anyObject())).willReturn(searchPageData);
		//when
		facade.getCodeGenerationConfigurations(pagination, sorts);
		//then
		verify(codeGenerationConfigurationPaginatedGenericDao).find(captor.capture());
		assertThat(captor.getValue().getPagination()).isEqualTo(pagination);
		assertThat(captor.getValue().getSorts()).isEqualTo(sorts);
	}

	@Test
	public void shouldRaiseExceptionOnEmptyCodeGenerationConfigurationsResults() throws Exception
	{
		//given
		final SearchPageData<CodeGenerationConfigurationModel> searchPageData = createDaoResult();
		given(codeGenerationConfigurationPaginatedGenericDao.find(anyObject())).willReturn(searchPageData);
		//when
		final Throwable throwable = catchThrowable(() -> facade.getCodeGenerationConfigurations(pagination, sorts));
		//then
		assertThat(throwable).isInstanceOf(CodeGenerationConfigurationNotFoundException.class)
				.hasMessage("No Code Generation Configurations found on the System");
	}

	private SearchPageData<CodeGenerationConfigurationModel> createDaoResult(
			CodeGenerationConfigurationModel... codeGenerationConfigurationModels)
	{
		final SearchPageData<CodeGenerationConfigurationModel> result = new SearchPageData<>();
		result.setSorts(sorts);
		result.setPagination(pagination);
		result.setResults(asList(codeGenerationConfigurationModels));
		return result;

	}
}

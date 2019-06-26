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
package com.hybris.backoffice.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.pageable.Pageable;



@RunWith(MockitoJUnitRunner.class)
public class WorkflowTemplateReferenceSearchFacadeTest
{
	public static final int PAGE_SIZE = 5;
	@InjectMocks
	private WorkflowTemplateReferenceSearchFacade facade;
	@Mock
	private WorkflowFacade workflowFacade;
	private WorkflowTemplateModel templateABC;
	private WorkflowTemplateModel templateCDE;
	private WorkflowTemplateModel adHocTemplate;

	@Before
	public void setUp()
	{
		adHocTemplate = mockTemplateWithName("adHoc");
		when(workflowFacade.getAdHocWorkflowTemplate()).thenReturn(adHocTemplate);
		templateABC = mockTemplateWithName("AbC");
		templateCDE = mockTemplateWithName("cDe");
		when(workflowFacade.getAllVisibleWorkflowTemplatesForCurrentUser())
				.thenReturn(Lists.newArrayList(templateABC, templateCDE));
	}

	@Test
	public void testAdHocTemplateIsAdded()
	{
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		when(searchQueryData.getSearchQueryText()).thenReturn("");
		doReturn(Integer.valueOf(PAGE_SIZE)).when(searchQueryData).getPageSize();

		final Pageable<WorkflowTemplateModel> pageable = facade.search(searchQueryData);

		assertThat(pageable.getTotalCount()).isEqualTo(3);
		assertThat(pageable.getPageSize()).isEqualTo(PAGE_SIZE);
		assertThat(pageable.getCurrentPage()).containsExactly(templateABC, templateCDE, adHocTemplate);
	}

	@Test
	public void testFilterByNameOneMatching()
	{
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		doReturn(Integer.valueOf(PAGE_SIZE)).when(searchQueryData).getPageSize();
		when(searchQueryData.getSearchQueryText()).thenReturn("b");

		final Pageable<WorkflowTemplateModel> pageable = facade.search(searchQueryData);

		assertThat(pageable.getTotalCount()).isEqualTo(1);
		assertThat(pageable.getCurrentPage()).containsExactly(templateABC);
	}

	@Test
	public void testCaseInsensitiveSearch()
	{
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		doReturn(Integer.valueOf(PAGE_SIZE)).when(searchQueryData).getPageSize();
		when(searchQueryData.getSearchQueryText()).thenReturn("C");

		final Pageable<WorkflowTemplateModel> pageable = facade.search(searchQueryData);

		assertThat(pageable.getTotalCount()).isEqualTo(3);
		assertThat(pageable.getCurrentPage()).containsExactly(templateABC, templateCDE, adHocTemplate);
	}

	protected WorkflowTemplateModel mockTemplateWithName(final String name)
	{
		final WorkflowTemplateModel template = mock(WorkflowTemplateModel.class);
		when(template.getName()).thenReturn(name);
		return template;
	}
}

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
package com.hybris.backoffice.workflow.impl;

import static com.hybris.backoffice.workflow.impl.WorkflowSearchPageable.TOTAL_COUNT_PAGE_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowStatus;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.hybris.backoffice.workflow.WorkflowSearchData;
import com.hybris.cockpitng.search.data.pageable.Pageable;



@RunWith(MockitoJUnitRunner.class)
public class WorkflowSearchPageableTest
{

	@Mock
	private WorkflowService workflowService;
	private List<WorkflowStatus> statuses;
	private EnumSet<WorkflowStatus> statusesSet;
	private Date dateFrom;
	private Date dateTo;
	private static final int PAGE_SIZE = 15;

	@Before
	public void setUp()
	{
		dateFrom = new Date(1L);
		dateTo = new Date(100000L);
		statuses = Lists.newArrayList(WorkflowStatus.FINISHED, WorkflowStatus.FINISHED);
		statusesSet = EnumSet.of(WorkflowStatus.FINISHED, WorkflowStatus.FINISHED);
	}

	@Test
	public void testPagesAreOverlapped()
	{
		mockAdhocSearch(10);
		mockWorkflowsSearch(10);

		final Pageable<WorkflowModel> pageable = createPageable();

		final List<WorkflowModel> currentPage = pageable.getCurrentPage();

		assertThat(currentPage).hasSize(PAGE_SIZE);
		verify(workflowService).getAllWorkflows(statusesSet, dateFrom, dateTo, 0, PAGE_SIZE);
		verify(workflowService).getAllAdhocWorkflows(statusesSet, dateFrom, dateTo, 0, 5);
	}

	@Test
	public void testMultiplePagesOverlapped()
	{
		mockAdhocSearch(20);
		mockWorkflowsSearch(20);

		final Pageable<WorkflowModel> pageable = createPageable();

		pageable.setPageNumber(1);
		final List<WorkflowModel> currentPage = pageable.getCurrentPage();

		assertThat(currentPage).hasSize(PAGE_SIZE);
		verify(workflowService).getAllWorkflows(statusesSet, dateFrom, dateTo, 15, PAGE_SIZE);
		verify(workflowService).getAllAdhocWorkflows(statusesSet, dateFrom, dateTo, 0, 10);
	}

	@Test
	public void testDataOnlyFromAdHoc()
	{
		mockAdhocSearch(10);
		mockWorkflowsSearch(10);

		final Pageable<WorkflowModel> pageable = createPageable();

		pageable.setPageNumber(1);
		final List<WorkflowModel> currentPage = pageable.getCurrentPage();

		assertThat(currentPage).hasSize(5);
		verify(workflowService).getAllWorkflows(statusesSet, dateFrom, dateTo, 15, PAGE_SIZE);
		verify(workflowService).getAllAdhocWorkflows(statusesSet, dateFrom, dateTo, 5, PAGE_SIZE);
	}

	@Test
	public void testDataOnlyFromRegularWorkflows()
	{
		mockAdhocSearch(10);
		mockWorkflowsSearch(20);

		final Pageable<WorkflowModel> pageable = createPageable();

		final List<WorkflowModel> currentPage = pageable.getCurrentPage();

		assertThat(currentPage).hasSize(PAGE_SIZE);
		verify(workflowService).getAllWorkflows(statusesSet, dateFrom, dateTo, 0, PAGE_SIZE);
		verify(workflowService).getAllAdhocWorkflows(statusesSet, dateFrom, dateTo, 0, TOTAL_COUNT_PAGE_SIZE);
	}

	@Test
	public void testDataOnlyFromRegularWorkflowsDataSizeSameAsPageSize()
	{
		mockAdhocSearch(0);
		mockWorkflowsSearch(PAGE_SIZE);

		final Pageable<WorkflowModel> pageable = createPageable();

		final List<WorkflowModel> currentPage = pageable.getCurrentPage();

		assertThat(currentPage).hasSize(PAGE_SIZE);
		verify(workflowService).getAllWorkflows(statusesSet, dateFrom, dateTo, 0, PAGE_SIZE);
		verify(workflowService).getAllAdhocWorkflows(statusesSet, dateFrom, dateTo, 0, TOTAL_COUNT_PAGE_SIZE);
	}

	@Test
	public void testOnlyRegularWorkflowsAvailable()
	{
		mockAdhocSearch(0);
		mockWorkflowsSearch(10);

		final Pageable<WorkflowModel> pageable = createPageable();

		final List<WorkflowModel> currentPage = pageable.getCurrentPage();

		assertThat(currentPage).hasSize(10);
		verify(workflowService).getAllWorkflows(statusesSet, dateFrom, dateTo, 0, PAGE_SIZE);
		verify(workflowService).getAllAdhocWorkflows(statusesSet, dateFrom, dateTo, 0, 5);
	}

	@Test
	public void testGetNextPage()
	{
		mockWorkflowsSearch(10);
		mockAdhocSearch(10);

		final Pageable<WorkflowModel> pageable = createPageable();

		pageable.getCurrentPage();
		final List<WorkflowModel> currentPage = pageable.nextPage();

		assertThat(currentPage).hasSize(5);
		verify(workflowService).getAllWorkflows(statusesSet, dateFrom, dateTo, 0, PAGE_SIZE);
		verify(workflowService).getAllAdhocWorkflows(statusesSet, dateFrom, dateTo, 0, 5);
		verify(workflowService).getAllAdhocWorkflows(statusesSet, dateFrom, dateTo, 5, PAGE_SIZE);
	}

	@Test
	public void testGetPreviousPage()
	{
		mockWorkflowsSearch(10);
		mockAdhocSearch(10);

		final Pageable<WorkflowModel> pageable = createPageable();

		pageable.setPageNumber(1);
		pageable.getCurrentPage();
		final List<WorkflowModel> currentPage = pageable.previousPage();

		assertThat(currentPage).hasSize(PAGE_SIZE);
		verify(workflowService).getAllWorkflows(statusesSet, dateFrom, dateTo, 0, PAGE_SIZE);
		verify(workflowService).getAllWorkflows(statusesSet, dateFrom, dateTo, 15, PAGE_SIZE);
		verify(workflowService).getAllAdhocWorkflows(statusesSet, dateFrom, dateTo, 5, PAGE_SIZE);
		verify(workflowService).getAllAdhocWorkflows(statusesSet, dateFrom, dateTo, 0, 5);
	}

	@Test
	public void testGetTotalCount()
	{
		mockAdhocSearch(10);
		mockWorkflowsSearch(20);

		final Pageable<WorkflowModel> pageable = createPageable();

		assertThat(pageable.getTotalCount()).isEqualTo(30);
	}

	@Test
	public void testHasNextPage()
	{
		mockAdhocSearch(10);
		mockWorkflowsSearch(20);

		final Pageable<WorkflowModel> pageable = createPageable();

		assertThat(pageable.hasNextPage()).isTrue();
	}

	@Test
	public void testDoesNotHaveNextPage()
	{
		mockAdhocSearch(PAGE_SIZE);
		mockWorkflowsSearch(0);

		final Pageable<WorkflowModel> pageable = createPageable();

		assertThat(pageable.hasNextPage()).isFalse();
	}

	@Test
	public void testHasPreviousPage()
	{
		mockAdhocSearch(10);
		mockWorkflowsSearch(20);

		final Pageable<WorkflowModel> pageable = createPageable();
		pageable.setPageNumber(2);

		assertThat(pageable.hasPreviousPage()).isTrue();
	}

	@Test
	public void testDoesNotHavePreviousPage()
	{
		mockAdhocSearch(10);
		mockWorkflowsSearch(20);

		final Pageable<WorkflowModel> pageable = createPageable();

		assertThat(pageable.hasPreviousPage()).isFalse();
	}

	@Test
	public void testRefreshDoesNotChangeCurrentPage()
	{
		mockAdhocSearch(10);
		mockWorkflowsSearch(6);

		final Pageable<WorkflowModel> pageable = createPageable();
		pageable.setPageNumber(1);
		assertThat(pageable.getCurrentPage()).hasSize(1);
		pageable.refresh();

		assertThat(pageable.getPageNumber()).isEqualTo(1);
		assertThat(pageable.getCurrentPage()).hasSize(1);
	}

	@Test
	public void testGettingEmptyPagesReturnsEmptyLists()
	{
		mockAdhocSearch(10);
		mockWorkflowsSearch(20);

		final Pageable<WorkflowModel> pageable = createPageable();
		pageable.setPageNumber(6);
		final List<WorkflowModel> nextPage = pageable.nextPage();

		pageable.refresh();
		final List<WorkflowModel> previousPageAfterRefresh = pageable.previousPage();

		assertThat(nextPage).isEmpty();
		assertThat(previousPageAfterRefresh).isEmpty();
	}

	@Test
	public void testGettingAllResults()
	{
		doAnswer(inv -> mockWorkflows(20)).when(workflowService).getAllWorkflows(any(), any(), any());
		doAnswer(inv -> mockWorkflows(10)).when(workflowService).getAllAdhocWorkflows(any(), any(), any());

		final Pageable<WorkflowModel> pageable = createPageable();
		final List<WorkflowModel> allResults = pageable.getAllResults();

		assertThat(allResults).hasSize(30);
	}

	protected void mockWorkflowsSearch(final int workflowsTotalCount)
	{
		doAnswer(inv -> {
			int startIndex = ((Integer) inv.getArguments()[3]).intValue();
			int pageSize = ((Integer) inv.getArguments()[4]).intValue();
			int itemsToMock = workflowsTotalCount>startIndex ? workflowsTotalCount - startIndex:0;
			itemsToMock = itemsToMock > pageSize ? pageSize : itemsToMock;

			return mockSearchResult(workflowsTotalCount, itemsToMock);

		}).when(workflowService).getAllWorkflows(any(), eq(dateFrom), eq(dateTo), anyInt(), anyInt());
	}

	protected void mockAdhocSearch(final int adhocTotalCount)
	{
		doAnswer(inv -> {
			int pageSize = ((Integer) inv.getArguments()[4]).intValue();
			int startIndex = ((Integer) inv.getArguments()[3]).intValue();
			int itemsToMock = adhocTotalCount>startIndex ? adhocTotalCount - startIndex:0;
			itemsToMock = itemsToMock >= 0 ? itemsToMock : 0;
			itemsToMock = itemsToMock > pageSize ? pageSize : itemsToMock;

			return mockSearchResult(adhocTotalCount, itemsToMock);

		}).when(workflowService).getAllAdhocWorkflows(any(), eq(dateFrom), eq(dateTo), anyInt(), anyInt());
	}

	protected SearchResult<WorkflowModel> mockSearchResult(final int totalCount, final int resultCount)
	{
		final SearchResult result = mock(SearchResult.class);
		doReturn(Integer.valueOf(totalCount)).when(result).getTotalCount();
		doReturn(Integer.valueOf(resultCount)).when(result).getCount();
		final List<WorkflowModel> workflowModels = mockWorkflows(resultCount);
		doReturn(workflowModels).when(result).getResult();
		return result;
	}

	protected List<WorkflowModel> mockWorkflows(final int numberOfWorkflowsToMock)
	{
		final List<WorkflowModel> workflows = new ArrayList<>(numberOfWorkflowsToMock);
		for (int i = 0; i < numberOfWorkflowsToMock; i++)
		{

			final WorkflowModel workflow = mock(WorkflowModel.class);
			workflows.add(workflow);
		}
		return workflows;
	}

	protected WorkflowSearchPageable createPageable()
	{
		final WorkflowSearchData searchData = new WorkflowSearchData(PAGE_SIZE, statuses, dateFrom, dateTo);
		final WorkflowSearchPageable workflowSearchPageable = new WorkflowSearchPageable(searchData);
		workflowSearchPageable.setWorkflowService(workflowService);
		return workflowSearchPageable;
	}
}

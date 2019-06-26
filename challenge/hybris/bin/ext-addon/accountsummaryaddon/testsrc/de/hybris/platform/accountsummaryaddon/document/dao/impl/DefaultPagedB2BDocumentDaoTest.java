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
package de.hybris.platform.accountsummaryaddon.document.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.accountsummaryaddon.B2BIntegrationTest;
import de.hybris.platform.accountsummaryaddon.document.AccountSummaryDocumentQuery;
import de.hybris.platform.accountsummaryaddon.document.AmountRange;
import de.hybris.platform.accountsummaryaddon.document.B2BDocumentQueryBuilder;
import de.hybris.platform.accountsummaryaddon.document.DateRange;
import de.hybris.platform.accountsummaryaddon.document.dao.PagedB2BDocumentDao;
import de.hybris.platform.accountsummaryaddon.enums.DocumentStatus;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.utils.AccountSummaryAddonUtils;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultPagedB2BDocumentDaoTest extends B2BIntegrationTest
{

	private static final String SORT_BY_DOCUMENT_STATUS_DESC = "byDocumentStatusDesc";
	private static final String SORT_BY_DOCUMENT_TYPE_ASC = "byDocumentTypeAsc";
	private static final String DOCUMENT_TYPE_PURCHASE_ORDER = "Purchase Order";
	private static final String DOCUMENT_TYPE_INVOICE = "Invoice";
	private static final String DOCUMENT_NUMBER_CRN_005 = "CRN-005";
	private static final String AMOUNT_85_20 = "85.20";
	private static final String AMOUNT_75_31 = "75.31";
	private static final String UNIT_PRONTO_SERVICES = "Pronto Services";
	private static final String UNIT_SERVICES_WEST = "Services West";
	private static final String UNIT_SERVICES_EAST = "Services East";
	private static final String AMOUNT_76_31 = "76.31";
	private static final String AMOUNT_75_30 = "75.30";
	private static final String FILTER_BY_AMOUNT = "amount";
	private static final String DATE_2013_08_11 = "2013-08-11";
	private static final String FILTER_BY_DATE = "date";
	private static final String DOCUMENT_STATUS_OPEN = "OPEN";
	private static final String DATE_08_11_2013 = "08/11/2013";
	private static final String DOCUMENT_NUMBER_PUR_002 = "PUR-002";
	private static final String DOCUMENT_NUMBER_CRN_006 = "CRN-006";
	private static final String UNIT_CUSTOM_RETAIL = "Custom Retail";
	private static final String FILTER_BY_DOCUMENT_NUMBER = "documentNumber";
	private static final String DOCUMENT_NUMBER_DBN_002 = "DBN-002";
	private static final String DOCUMENT_NUMBER_DBN_001 = "DBN-001";
	private static final String DOCUMENT_STATUS_CLOSED = "CLOSED";
	private static final String USER_MARK_RIVERS_RUSTIC_HW_COM = "mark.rivers@rustic-hw.com";

	private static final String UNKNOWN = "UNKNOWN";
	private static final String FILTER_BY_DOCUMENT_TYPE = "documentType";

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Resource
	private PagedB2BDocumentDao pagedB2BDocumentDao;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/accountsummaryaddon/test/testOrganizations.csv", "utf-8");
		importCsv("/accountsummaryaddon/test/testB2bdocument.csv", "utf-8");
	}

	@Test
	public void shouldReturnAllDocumentsAscSort()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.STATUS, true).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(10, result.getResults().size());

		for (final B2BDocumentModel document : result.getResults())
		{
			System.out.println(document.getDocumentNumber());
			TestCase.assertEquals(DocumentStatus.OPEN, document.getStatus());
		}
	}

	@Test
	public void shouldReturnAllDocumentsDescSort()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.STATUS, false).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(10, result.getResults().size());

		TestCase.assertEquals(DocumentStatus.CLOSED, result.getResults().get(0).getStatus());
		TestCase.assertEquals(DocumentStatus.CLOSED, result.getResults().get(1).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(2).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(3).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(4).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(5).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(6).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(7).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(8).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(9).getStatus());
	}

	@Test
	public void shouldReturnOnlyDocumentsWherePurchaseOrder()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.STATUS, true).addCriteria(
				B2BDocumentModel.DOCUMENTTYPE, DOCUMENT_TYPE_PURCHASE_ORDER).build();

		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(1, result.getResults().size());

		TestCase.assertEquals(DOCUMENT_TYPE_PURCHASE_ORDER, result.getResults().get(0).getDocumentType().getCode());
	}

	@Test
	public void shouldReturnOnlyDocumentsLikeNote()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.STATUS, true)
				.addCriteria(B2BDocumentModel.DOCUMENTTYPE, "Note").addCriteria(B2BDocumentModel.DOCUMENTNUMBER, "DBN").build();


		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(2, result.getResults().size());

		TestCase.assertEquals("Debit Note", result.getResults().get(0).getDocumentType().getCode());
		TestCase.assertEquals("Debit Note", result.getResults().get(1).getDocumentType().getCode());
	}

	@Test
	public void shouldReturnOnlyFirstPageSortByDueDate()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 2, B2BDocumentModel.DUEDATE, true).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(2, result.getResults().size());
		TestCase.assertEquals(DOCUMENT_NUMBER_CRN_005, result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals(DOCUMENT_NUMBER_CRN_006, result.getResults().get(1).getDocumentNumber());

		final Date date0 = result.getResults().get(0).getDueDate();
		final Date date1 = result.getResults().get(1).getDueDate();
		TestCase.assertEquals("2013-07-07", sdf.format(date0));
		TestCase.assertEquals("2013-07-08", sdf.format(date1));
	}

	@Test
	public void shouldReturnSecondPageSortByAmount()
	{
		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(1, 2, B2BDocumentModel.AMOUNT, true).build();

		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(2, result.getResults().size());

		TestCase.assertEquals(AMOUNT_75_31, result.getResults().get(0).getAmount().toString());
		TestCase.assertEquals(AMOUNT_85_20, result.getResults().get(1).getAmount().toString());
	}

	@Test
	public void shouldReturnDocumentAssociateToUser()
	{
		login(USER_MARK_RIVERS_RUSTIC_HW_COM);

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(3, result.getResults().size());
		TestCase.assertEquals(DOCUMENT_NUMBER_DBN_001, result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals(DOCUMENT_NUMBER_DBN_002, result.getResults().get(1).getDocumentNumber());
		TestCase.assertEquals(DOCUMENT_NUMBER_PUR_002, result.getResults().get(2).getDocumentNumber());
	}

	@Test
	public void shouldReturnOpenDocuments()
	{
		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria("status", DocumentStatus.OPEN).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(10, result.getResults().size());
		for (final B2BDocumentModel document : result.getResults())
		{
			TestCase.assertEquals(DocumentStatus.OPEN, document.getStatus());
		}
	}

	@Test
	public void shouldReturnProntoServicesDocuments()
	{
		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN).addCriteria(B2BDocumentModel.UNIT, UNIT_PRONTO_SERVICES)
				.build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(2, result.getResults().size());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(0).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(1).getStatus());
	}

	@Test
	public void shouldReturnEmptyResultForServicesWestDocuments()
	{
		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN).addCriteria(B2BDocumentModel.UNIT, UNIT_SERVICES_WEST)
				.build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(0, result.getResults().size());
	}

	@Test
	public void shouldReturnDocumentsBetweenAmountRange()
	{
		final AmountRange amountRange = new AmountRange(new BigDecimal(AMOUNT_75_30), new BigDecimal(AMOUNT_76_31));

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN).addCriteria(B2BDocumentModel.AMOUNT, amountRange).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(1, result.getResults().size());
		TestCase.assertEquals(DOCUMENT_NUMBER_PUR_002, result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals(AMOUNT_75_31, result.getResults().get(0).getAmount().toPlainString());
	}

	@Test
	public void shouldReturnDocumentsBetweenOpenamountRange()
	{
		final AmountRange amountRange = new AmountRange(new BigDecimal("26.54"), new BigDecimal("26.54"));

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN).addCriteria(B2BDocumentModel.OPENAMOUNT, amountRange)
				.build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(1, result.getResults().size());
		TestCase.assertEquals(DOCUMENT_NUMBER_DBN_002, result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals("26.54", result.getResults().get(0).getOpenAmount().toPlainString());
	}

	@Test
	public void shouldReturnDocumentsBetweenDateRange() throws ParseException
	{
		final Date minDate = DateUtils.parseDate("2013-08-10", new String[]
		{ "yyyy-MM-dd" });
		final Date maxDate = DateUtils.parseDate(DATE_2013_08_11, new String[]
		{ "yyyy-MM-dd" });

		final DateRange dateRange = new DateRange(minDate, maxDate);

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN).addCriteria(B2BDocumentModel.DATE, dateRange).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(2, result.getResults().size());

		final Date date0 = result.getResults().get(0).getDate();
		final Date date1 = result.getResults().get(1).getDate();
		date0.after(minDate);
		TestCase.assertEquals(DATE_2013_08_11, sdf.format(date0));
		TestCase.assertEquals(DATE_2013_08_11, sdf.format(date1));
	}

	@Test
	public void shouldReturnDocumentsBetweenDueDateRange() throws ParseException
	{
		final Date minDate = DateUtils.parseDate("2013-08-16", new String[]
		{ "yyyy-MM-dd" });
		final Date maxDate = DateUtils.parseDate("2013-08-17", new String[]
		{ "yyyy-MM-dd" });

		final DateRange dateRange = new DateRange(minDate, maxDate);

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.STATUS, DocumentStatus.OPEN).addCriteria(B2BDocumentModel.DUEDATE, dateRange).build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(1, result.getResults().size());

		final Date date0 = result.getResults().get(0).getDueDate();
		TestCase.assertEquals("2013-08-16", sdf.format(date0));
	}

	// No result
	@Test
	public void shouldNotReturnResult()
	{
		login(USER_MARK_RIVERS_RUSTIC_HW_COM);

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria(B2BDocumentModel.DOCUMENTTYPE, "UNKNOW").build();
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.findDocuments(query);

		TestCase.assertEquals(0, result.getResults().size());
	}

	// unknow criteria
	@Test
	public void shouldGetErrorCriteriaNotFound()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(0, 10, B2BDocumentModel.OPENAMOUNT, true)
				.addCriteria("unknowcriteria", "any").build();

		try
		{
			pagedB2BDocumentDao.findDocuments(query);
			TestCase.fail();
		}
		catch (final FlexibleSearchException e)
		{
			//Success
			TestCase.assertTrue(StringUtils.startsWith(e.getMessage(), "cannot search unknown field"));
		}
	}

	// invalidate page
	@Test
	public void shouldGetErrorInvalidPage()
	{

		final AccountSummaryDocumentQuery query = new B2BDocumentQueryBuilder(-1, 10, B2BDocumentModel.OPENAMOUNT, true).build();
		try
		{
			pagedB2BDocumentDao.findDocuments(query);
			TestCase.fail();
		}
		catch (final IllegalArgumentException e)
		{
			//Success
			TestCase.assertEquals("pageableData current page must be zero or greater", e.getMessage());
		}
	}

	/**
	 * This test should return all results except the documents whose documentType.displayInAllList=false. Total
	 * documents are 15 in the testB2bdocument.csv, but the search result will only return 14 since,
	 * documentType.displayInAllList for Statement is set to false
	 */
	@Test
	public void shouldReturnAllPagedDocumentsSortedByDefaultSort()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getAllPagedDocuments(
				createPageableData(0, 15, StringUtils.EMPTY), Collections.singletonList(AccountSummaryAddonUtils
						.createFilterByCriteriaObject(StringUtils.EMPTY, StringUtils.EMPTY)));

		TestCase.assertEquals(14, result.getResults().size());
	}

	/**
	 * This test should return all results including the documents whose documentType.displayInAllList=false. Total
	 * documents are 15 in the testB2bdocument.csv, since the search criteria is documentType, result will return all
	 * documents i.e. 15
	 */
	@Test
	public void shouldReturnAllPagedDocumentsFilteredByDocumentTypeSortedByDefaultSort()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getAllPagedDocuments(
				createPageableData(0, 15, StringUtils.EMPTY), Collections.singletonList(AccountSummaryAddonUtils
						.createTypeCriteriaObject(StringUtils.EMPTY, StringUtils.EMPTY, FILTER_BY_DOCUMENT_TYPE)));

		TestCase.assertEquals(15, result.getResults().size());
	}

	/**
	 * This test should return all results including the documents whose documentType.displayInAllList=false. Total
	 * documents are 15 in the testB2bdocument.csv, since the search criteria is documentNumber, result will return all
	 * documents i.e. 15
	 */
	@Test
	public void shouldReturnAllPagedDocumentsFilteredByDocumentNumberRange()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao
				.getAllPagedDocuments(createPageableData(0, 15, StringUtils.EMPTY), Collections
						.singletonList(AccountSummaryAddonUtils.createRangeCriteriaObject(StringUtils.EMPTY, StringUtils.EMPTY,
								StringUtils.EMPTY, FILTER_BY_DOCUMENT_NUMBER)));

		TestCase.assertEquals(15, result.getResults().size());
	}

	/**
	 * This test should return only 1 result including the document whose documentType.displayInAllList=false. Since the
	 * search criteria is documentNumber From: STA for Unit Pronto Goods, result will return only 1 document
	 */
	@Test
	public void shouldReturnAllPagedDocumentsForProntoGoodsFilteredByDocumentNumberRange()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getPagedDocumentsForUnit("Pronto Goods",
				createPageableData(0, 10, StringUtils.EMPTY), Collections.singletonList(AccountSummaryAddonUtils
						.createRangeCriteriaObject("STA", StringUtils.EMPTY, StringUtils.EMPTY, FILTER_BY_DOCUMENT_NUMBER)));

		TestCase.assertEquals(1, result.getResults().size());
		TestCase.assertEquals("STA-001", result.getResults().get(0).getDocumentNumber());
	}

	/**
	 * This test returns 5 results as page size is set to 5, sorted byDocumentStatusDesc.
	 */
	@Test
	public void shouldReturnAllPagedDocumentsSortedByDocumentStatusDesc()
	{

		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getAllPagedDocuments(
				createPageableData(0, 5, SORT_BY_DOCUMENT_STATUS_DESC), Collections.singletonList(AccountSummaryAddonUtils
						.createFilterByCriteriaObject(StringUtils.EMPTY, StringUtils.EMPTY)));

		TestCase.assertEquals(5, result.getResults().size());

		TestCase.assertEquals(DocumentStatus.CLOSED, result.getResults().get(0).getStatus());
		TestCase.assertEquals(DocumentStatus.CLOSED, result.getResults().get(1).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(2).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(3).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(4).getStatus());
	}

	/**
	 * This test returns 1 result, since the test data contains only 1 document with type Purchase Order sorted
	 * byDocumentStatusDesc.
	 */
	@Test
	public void shouldReturnPagedDocumentsWithPurchaseOrder()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getAllPagedDocuments(
				createPageableData(0, 10, SORT_BY_DOCUMENT_STATUS_DESC), Collections.singletonList(AccountSummaryAddonUtils
						.createTypeCriteriaObject(DOCUMENT_TYPE_PURCHASE_ORDER, StringUtils.EMPTY, FILTER_BY_DOCUMENT_TYPE)));

		TestCase.assertEquals(1, result.getResults().size());

		TestCase.assertEquals(DOCUMENT_TYPE_PURCHASE_ORDER, result.getResults().get(0).getDocumentType().getCode());
	}

	/**
	 * This test returns 3 results, for unit "Pronto" sorted byDocumentNumberDesc with documentType "Invoice".
	 */
	@Test
	public void shouldReturnPagedDocumentsWithInvoiceForPronto()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getPagedDocumentsForUnit("Pronto",
				createPageableData(0, 10, "byDocumentNumberDesc"), Collections.singletonList(AccountSummaryAddonUtils
						.createTypeCriteriaObject(DOCUMENT_TYPE_INVOICE, StringUtils.EMPTY, FILTER_BY_DOCUMENT_TYPE)));

		TestCase.assertEquals(3, result.getResults().size());

		final B2BDocumentModel b2bDocumentModel = result.getResults().get(0);
		final B2BDocumentModel b2bDocumentModel1 = result.getResults().get(1);
		final B2BDocumentModel b2bDocumentModel2 = result.getResults().get(2);
		TestCase.assertEquals(DOCUMENT_TYPE_INVOICE, b2bDocumentModel.getDocumentType().getCode());
		TestCase.assertEquals(DOCUMENT_TYPE_INVOICE, b2bDocumentModel1.getDocumentType().getCode());
		TestCase.assertEquals(DOCUMENT_TYPE_INVOICE, b2bDocumentModel2.getDocumentType().getCode());
		TestCase.assertEquals("INV-004", b2bDocumentModel.getDocumentNumber());
		TestCase.assertEquals("INC-004", b2bDocumentModel1.getDocumentNumber());
		TestCase.assertEquals("CRN-004", b2bDocumentModel2.getDocumentNumber());
	}

	/**
	 * This test should return only 2 results as set on pageSize, sorted by "byDueDateAsc". It should return documents
	 * with number CRN-005 & CRN-006 as the dueDate for these documents is the earliest.
	 */
	@Test
	public void shouldReturnOnlyFirstPageSortedByDueDateAsc()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getAllPagedDocuments(
				createPageableData(0, 2, "byDueDateAsc"), Collections.singletonList(AccountSummaryAddonUtils
						.createFilterByCriteriaObject(StringUtils.EMPTY, StringUtils.EMPTY)));

		TestCase.assertEquals(2, result.getResults().size());

		final B2BDocumentModel b2bDocumentModel1 = result.getResults().get(0);
		final B2BDocumentModel b2bDocumentModel2 = result.getResults().get(1);
		TestCase.assertEquals(DOCUMENT_NUMBER_CRN_005, b2bDocumentModel1.getDocumentNumber());
		TestCase.assertEquals(DOCUMENT_NUMBER_CRN_006, b2bDocumentModel2.getDocumentNumber());

		TestCase.assertEquals("2013-07-07", sdf.format(b2bDocumentModel1.getDueDate()));
		TestCase.assertEquals("2013-07-08", sdf.format(b2bDocumentModel2.getDueDate()));
	}

	/**
	 * This test will return total of 2 results, sorted by "byOriginalAmountAsc" but on page 1. That's why 3rd & 4th
	 * document's amount is compared.
	 */
	@Test
	public void shouldReturnSecondPageSortedByOriginalAmountAsc()
	{

		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getAllPagedDocuments(
				createPageableData(1, 2, "byOriginalAmountAsc"), Collections.singletonList(AccountSummaryAddonUtils
						.createFilterByCriteriaObject(StringUtils.EMPTY, StringUtils.EMPTY)));

		TestCase.assertEquals(2, result.getResults().size());

		TestCase.assertEquals(AMOUNT_75_31, result.getResults().get(0).getAmount().toString());
		TestCase.assertEquals(AMOUNT_85_20, result.getResults().get(1).getAmount().toString());
	}

	/**
	 * This test will return total of 3 results, sorted by "byOpenAmountDesc" for the user mark.rivers@rustic-hw.com.
	 */
	@Test
	public void shouldReturnPagedDocumentAssociatedToUserSortedByOpenAmountDesc()
	{
		login(USER_MARK_RIVERS_RUSTIC_HW_COM);

		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getAllPagedDocuments(
				createPageableData(0, 10, "byOpenAmountDesc"), Collections.singletonList(AccountSummaryAddonUtils
						.createFilterByCriteriaObject(StringUtils.EMPTY, StringUtils.EMPTY)));

		TestCase.assertEquals(3, result.getResults().size());
		TestCase.assertEquals(DOCUMENT_NUMBER_PUR_002, result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals(DOCUMENT_NUMBER_DBN_002, result.getResults().get(1).getDocumentNumber());
		TestCase.assertEquals(DOCUMENT_NUMBER_DBN_001, result.getResults().get(2).getDocumentNumber());
	}

	/**
	 * This test will return total of 10 results cause of the page size set to 10, sorted by "byOpenAmountDesc" with
	 * documentStatus "Open".
	 */
	@Test
	public void shouldReturnPagedOpenDocumentsSortedByOpenAmountAsc()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getAllPagedDocuments(
				createPageableData(0, 10, "byOpenAmountAsc"), Collections.singletonList(AccountSummaryAddonUtils
						.createFilterByCriteriaObject(DOCUMENT_STATUS_OPEN, StringUtils.EMPTY)));

		TestCase.assertEquals(10, result.getResults().size());
		for (final B2BDocumentModel document : result.getResults())
		{
			TestCase.assertEquals(DocumentStatus.OPEN, document.getStatus());
		}
	}

	/**
	 * This test will return total of 2 results, for unit "Pronto Services" with documentStatus "Open".
	 */
	@Test
	public void shouldReturnPagedOpenDocumentsForProntoServicesSortedByCreationDateAsc()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getPagedDocumentsForUnit(UNIT_PRONTO_SERVICES,
				createPageableData(0, 10, StringUtils.EMPTY), Collections.singletonList(AccountSummaryAddonUtils
						.createFilterByCriteriaObject(DOCUMENT_STATUS_OPEN, StringUtils.EMPTY)));

		TestCase.assertEquals(2, result.getResults().size());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(0).getStatus());
		TestCase.assertEquals(DocumentStatus.OPEN, result.getResults().get(1).getStatus());
	}

	/**
	 * This test will return total of 0 results, for unit "Services West" with documentStatus "Open". Since the test data
	 * does not contain any documents for Services West.
	 */
	@Test
	public void shouldReturnNoOpenDocumentsForServicesWest()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getPagedDocumentsForUnit(UNIT_SERVICES_WEST,
				createPageableData(0, 10, StringUtils.EMPTY), Collections.singletonList(AccountSummaryAddonUtils
						.createFilterByCriteriaObject(DOCUMENT_STATUS_OPEN, StringUtils.EMPTY)));

		TestCase.assertEquals(0, result.getResults().size());
	}

	/**
	 * This test will return total of 1 result, filtered by amount from "75.30" to "76.31" with documentStatus "Open".
	 */
	@Test
	public void shouldReturnPagedOpenDocumentsForAmountRange()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getAllPagedDocuments(
				createPageableData(0, 10, StringUtils.EMPTY), Collections.singletonList(AccountSummaryAddonUtils
						.createAmountRangeCriteriaObject(AMOUNT_75_30, AMOUNT_76_31, DOCUMENT_STATUS_OPEN, FILTER_BY_AMOUNT)));

		TestCase.assertEquals(1, result.getResults().size());
		TestCase.assertEquals(DOCUMENT_NUMBER_PUR_002, result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals(AMOUNT_75_31, result.getResults().get(0).getAmount().toPlainString());
	}

	/**
	 * This test will return total of 1 result, for unit "Custom Retail" filtered by amount from "24" to "27" with
	 * documentStatus "Open".
	 */
	@Test
	public void shouldReturnPagedOpenDocumentsForCustomRetailFilteredByAmountRange()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getPagedDocumentsForUnit(UNIT_CUSTOM_RETAIL,
				createPageableData(0, 10, StringUtils.EMPTY), Collections.singletonList(AccountSummaryAddonUtils
						.createAmountRangeCriteriaObject("24", "27", DOCUMENT_STATUS_OPEN, FILTER_BY_AMOUNT)));

		TestCase.assertEquals(1, result.getResults().size());
		TestCase.assertEquals(DOCUMENT_NUMBER_DBN_002, result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals("26.28", result.getResults().get(0).getAmount().toPlainString());
	}

	/**
	 * This test will return total of 2 result, filtered by date from "08/10/2013" to "08/11/2013" with documentStatus
	 * "Open", "sorted byDocumentDateAsc".
	 */
	@Test
	public void shouldReturnPagedOpenDocumentsFilteredByDateRange() throws ParseException
	{

		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getAllPagedDocuments(
				createPageableData(0, 10, "byDocumentDateAsc"), Collections.singletonList(AccountSummaryAddonUtils
						.createDateRangeCriteriaObject("08/10/2013", DATE_08_11_2013, DOCUMENT_STATUS_OPEN, FILTER_BY_DATE)));

		TestCase.assertEquals(2, result.getResults().size());

		TestCase.assertEquals(DATE_2013_08_11, sdf.format(result.getResults().get(0).getDate()));
		TestCase.assertEquals(DATE_2013_08_11, sdf.format(result.getResults().get(1).getDate()));
	}

	/**
	 * This test will return total of 1 result, for unit "Custom Retail" filtered by date from "08/09/2013" to
	 * "08/11/2013" with documentStatus "Open".
	 */
	@Test
	public void shouldReturnPagedOpenDocumentsForCustomRetailFilteredByDateRange() throws ParseException
	{

		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getPagedDocumentsForUnit(UNIT_CUSTOM_RETAIL,
				createPageableData(0, 10, StringUtils.EMPTY), Collections.singletonList(AccountSummaryAddonUtils
						.createDateRangeCriteriaObject("08/09/2013", DATE_08_11_2013, DOCUMENT_STATUS_OPEN, FILTER_BY_DATE)));

		TestCase.assertEquals(1, result.getResults().size());

		final B2BDocumentModel b2bDocumentModel = result.getResults().get(0);
		TestCase.assertEquals("2013-08-09", sdf.format(b2bDocumentModel.getDate()));
		TestCase.assertEquals(DOCUMENT_NUMBER_PUR_002, b2bDocumentModel.getDocumentNumber());
	}

	/**
	 * This test will return total of 1 result, filtered by documentNumber from "CRN-003" to "CRN-006" with
	 * documentStatus "Closed".
	 */
	@Test
	public void shouldReturnPagedClosedDocumentsFilteredByDocumentNumberRange()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getAllPagedDocuments(
				createPageableData(0, 10, StringUtils.EMPTY), Collections.singletonList(AccountSummaryAddonUtils
						.createRangeCriteriaObject("CRN-003", DOCUMENT_NUMBER_CRN_006, DOCUMENT_STATUS_CLOSED,
								FILTER_BY_DOCUMENT_NUMBER)));

		TestCase.assertEquals(1, result.getResults().size());
		final B2BDocumentModel b2bDocumentModel = result.getResults().get(0);
		TestCase.assertEquals(DOCUMENT_NUMBER_CRN_006, b2bDocumentModel.getDocumentNumber());
		TestCase.assertEquals(DOCUMENT_STATUS_CLOSED, b2bDocumentModel.getStatus().name());
	}

	/**
	 * This test will return total of 1 result, for unit "Custom Retail" filtered by documentNumber from "DBN-001" to
	 * "DBN-002" with documentStatus "Closed".
	 */
	@Test
	public void shouldReturnPagedClosedDocumentsForCustomRetailFilteredByDocumentNumberRange()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getPagedDocumentsForUnit(UNIT_CUSTOM_RETAIL,
				createPageableData(0, 10, StringUtils.EMPTY), Collections.singletonList(AccountSummaryAddonUtils
						.createRangeCriteriaObject(DOCUMENT_NUMBER_DBN_001, DOCUMENT_NUMBER_DBN_002, DOCUMENT_STATUS_CLOSED,
								FILTER_BY_DOCUMENT_NUMBER)));

		TestCase.assertEquals(1, result.getResults().size());
		final B2BDocumentModel b2bDocumentModel = result.getResults().get(0);
		TestCase.assertEquals(DOCUMENT_NUMBER_DBN_001, b2bDocumentModel.getDocumentNumber());
		TestCase.assertEquals(DOCUMENT_STATUS_CLOSED, b2bDocumentModel.getStatus().name());
	}

	/**
	 * This test will return total of 3 result, for unit "Services East" filtered by documentNumber with wildcard search
	 * on "005".
	 */
	@Test
	public void shouldReturnPagedDocumentsForServicesEastFilteredByDocumentNumber()
	{
		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getPagedDocumentsForUnit(UNIT_SERVICES_EAST,
				createPageableData(0, 10, SORT_BY_DOCUMENT_TYPE_ASC), Collections.singletonList(AccountSummaryAddonUtils
						.createSingleValueCriteriaObject("005", StringUtils.EMPTY, FILTER_BY_DOCUMENT_NUMBER)));

		TestCase.assertEquals(3, result.getResults().size());

		TestCase.assertEquals("CRN-005", result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals("INV-005", result.getResults().get(1).getDocumentNumber());
		TestCase.assertEquals("TES-005", result.getResults().get(2).getDocumentNumber());

	}

	/**
	 * This test will return total of 0 result, filtered by documentType "UNKNOWN"
	 */
	@Test
	public void shouldNotReturnResultFilteredByDocumentType()
	{
		login(USER_MARK_RIVERS_RUSTIC_HW_COM);

		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getAllPagedDocuments(
				createPageableData(0, 10, StringUtils.EMPTY), Collections.singletonList(AccountSummaryAddonUtils
						.createTypeCriteriaObject(UNKNOWN, StringUtils.EMPTY, FILTER_BY_DOCUMENT_TYPE)));

		TestCase.assertEquals(0, result.getResults().size());
	}

	/**
	 * This test will return total of 0 result, for unit "Services East" filtered by documentType "UNKNOWN"
	 */
	@Test
	public void shouldNotReturnResultForServicesEastFilteredByDocumentType()
	{
		login(USER_MARK_RIVERS_RUSTIC_HW_COM);

		final SearchPageData<B2BDocumentModel> result = pagedB2BDocumentDao.getPagedDocumentsForUnit("Services East",
				createPageableData(0, 10, StringUtils.EMPTY), Collections.singletonList(AccountSummaryAddonUtils
						.createTypeCriteriaObject(UNKNOWN, StringUtils.EMPTY, FILTER_BY_DOCUMENT_TYPE)));

		TestCase.assertEquals(0, result.getResults().size());
	}
}

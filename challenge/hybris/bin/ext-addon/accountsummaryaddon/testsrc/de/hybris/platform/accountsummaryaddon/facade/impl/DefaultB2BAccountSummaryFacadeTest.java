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
package de.hybris.platform.accountsummaryaddon.facade.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.accountsummaryaddon.B2BIntegrationTest;
import de.hybris.platform.accountsummaryaddon.constants.AccountsummaryaddonConstants;
import de.hybris.platform.accountsummaryaddon.document.criteria.DefaultCriteria;
import de.hybris.platform.accountsummaryaddon.document.criteria.FilterByCriteriaData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BAmountBalanceData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData;
import de.hybris.platform.accountsummaryaddon.facade.B2BAccountSummaryFacade;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.query.QueryParameters;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.Config;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BAccountSummaryFacadeTest extends B2BIntegrationTest
{

	private static final String BY_DOCUMENT_TYPE_ASC = "byDocumentTypeAsc";
	private static final String DOCUMENT_NUMBER = "documentNumber";
	private static final String AMOUNT_RANGE = "amountRange";
	private static final String DUE_DATE_RANGE = "dueDateRange";
	private static final String DATE_RANGE = "dateRange";
	private static final String DOCUMENT_NUMBER_RANGE = "documentNumberRange";
	private static final String DOCUMENT_TYPE = "documentType";
	private static final String UNIT_PRONTO_GOODS = "Pronto Goods";
	private static final String SORT_BY_DOCUMENT_STATUS_ASC = "byDocumentStatusAsc";
	private static final String UNIT_PRONTO = "Pronto";
	private static final String UNIT_SERVICES_EAST = "Services East";
	private static final String UNIT_CUSTOM_RETAIL = "Custom Retail";
	private static final String SORT_BY_DOCUMENT_DATE_ASC = "byDocumentDateAsc";

	@Resource
	private B2BAccountSummaryFacade b2bAccountSummaryFacade;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource(name = "filterByList")
	private Map<String, DefaultCriteria> filterByList;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/accountsummaryaddon/test/testOrganizations.csv", "utf-8");
		importCsv("/accountsummaryaddon/test/testB2bdocument.csv", "utf-8");

		final CurrencyModel currency = commonI18NService.getCurrency("USD");
		commonI18NService.setCurrentCurrency(currency);
	}

	protected FilterByCriteriaData createFilterByCriteriaData(final String startRange, final String endRange,
			final String documentTypeCode, final String documentStatus, final String filterByValue)
	{
		final FilterByCriteriaData filterByCriteriaData = new FilterByCriteriaData();
		filterByCriteriaData.setStartRange(startRange);
		filterByCriteriaData.setEndRange(endRange);
		filterByCriteriaData.setDocumentTypeCode(documentTypeCode);
		filterByCriteriaData.setDocumentStatus(documentStatus);
		filterByCriteriaData.setFilterByValue(filterByValue);

		return filterByCriteriaData;
	}

	@Test
	public void shouldGetValidResult()
	{
		final Map<String, String> params = QueryParameters.with("currentPage", "0").and("pageSize", "10")
				.and("unit", UNIT_CUSTOM_RETAIL).and("status", AccountsummaryaddonConstants.SEARCH_STATUS_OPEN)
				.and("searchBy", B2BDocumentModel.DOCUMENTNUMBER).and("searchValue", "PU").buildMap();

		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.findDocuments(params);

		TestCase.assertEquals(1, result.getResults().size());
		TestCase.assertEquals("PUR-002", result.getResults().get(0).getDocumentNumber());
	}

	//should get amount balance for 2 documents and 1 range (no past due)
	@Test
	@Ignore
	public void shouldGetAmountBalanceFor2DocumentsAnd1RangeNoPastDue()
	{
		setDateRanges("1", "", "", "", "", "", "", "");

		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid(UNIT_CUSTOM_RETAIL);
		final B2BAmountBalanceData results = b2bAccountSummaryFacade.getAmountBalance(unit);

		TestCase.assertEquals(1, results.getDueBalance().size());

		TestCase.assertEquals("$ 0,00", results.getDueBalance().values().toArray()[0]);

		TestCase.assertEquals("$ 102,08", results.getOpenBalance());
		TestCase.assertEquals("$ 0,00", results.getPastDueBalance());
		TestCase.assertEquals("$ 102,08", results.getCurrentBalance());
	}

	//should get amount balance for 2 documents and 1 range (only one past due)
	@Test
	@Ignore
	public void shouldGetAmountBalanceFor2DocumentsAnd1RangeOnePastDue()
	{
		setDateRanges("1", "", "", "", "", "", "", "");

		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid(UNIT_PRONTO_GOODS);
		final B2BAmountBalanceData results = b2bAccountSummaryFacade.getAmountBalance(unit);

		TestCase.assertEquals(1, results.getDueBalance().size());

		TestCase.assertEquals("$ 25,54", results.getDueBalance().values().toArray()[0]);

		TestCase.assertEquals("$ 41,08", results.getOpenBalance());
		TestCase.assertEquals("$ 25,54", results.getPastDueBalance());
		TestCase.assertEquals("$ 15,54", results.getCurrentBalance());
	}

	//should get amount balance for 2 documents and 3 range (only one past due)
	@Test
	@Ignore
	public void shouldGetAmountBalanceFor2DocumentsAnd3RangeOnePastDue()
	{
		setDateRanges("1", "10", "11", "30", "31", "", "", "");

		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid(UNIT_PRONTO_GOODS);
		final B2BAmountBalanceData results = b2bAccountSummaryFacade.getAmountBalance(unit);

		TestCase.assertEquals(3, results.getDueBalance().size());

		TestCase.assertEquals("$ 0,00", results.getDueBalance().values().toArray()[0]);
		TestCase.assertEquals("$ 25,54", results.getDueBalance().values().toArray()[1]);
		TestCase.assertEquals("$ 0,00", results.getDueBalance().values().toArray()[2]);

		TestCase.assertEquals("$ 41,08", results.getOpenBalance());
		TestCase.assertEquals("$ 25,54", results.getPastDueBalance());
		TestCase.assertEquals("$ 15,54", results.getCurrentBalance());
	}

	//should get amount balance for 3 documents and 3 range (2 past due)
	@Test
	@Ignore
	public void shouldGetAmountBalanceFor3DocumentsAnd3Range2PastDue()
	{
		setDateRanges("1", "5", "6", "30", "31", "", "", "");

		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid(UNIT_PRONTO);
		final B2BAmountBalanceData results = b2bAccountSummaryFacade.getAmountBalance(unit);

		TestCase.assertEquals(3, results.getDueBalance().size());

		TestCase.assertEquals("$ 21,51", results.getDueBalance().values().toArray()[0]);
		TestCase.assertEquals("$ 25,54", results.getDueBalance().values().toArray()[1]);
		TestCase.assertEquals("$ 0,00", results.getDueBalance().values().toArray()[2]);

		TestCase.assertEquals("$ 62,59", results.getOpenBalance());
		TestCase.assertEquals("$ 47,05", results.getPastDueBalance());
		TestCase.assertEquals("$ 15,54", results.getCurrentBalance());
	}


	//should get amount balance for 3 documents and 3 range (2 past due and 1 not includeInOpenBalance)
	@Test
	@Ignore
	public void shouldGetAmountBalanceFor3DocumentsAnd3Range2PastDueButOneNotIncludeInOpenBalance()
	{
		setDateRanges("1", "5", "6", "30", "31", "", "", "");

		final B2BUnitModel unit = new B2BUnitModel();
		unit.setUid(UNIT_SERVICES_EAST);
		final B2BAmountBalanceData results = b2bAccountSummaryFacade.getAmountBalance(unit);

		TestCase.assertEquals(3, results.getDueBalance().size());

		TestCase.assertEquals("$ 25,54", results.getDueBalance().values().toArray()[0]);
		TestCase.assertEquals("$ 0,00", results.getDueBalance().values().toArray()[1]);
		TestCase.assertEquals("$ 12,54", results.getDueBalance().values().toArray()[2]);

		TestCase.assertEquals("$ 38,08", results.getOpenBalance());
		TestCase.assertEquals("$ 38,08", results.getPastDueBalance());
		TestCase.assertEquals("$ 0,00", results.getCurrentBalance());
	}

	/**
	 * test how the attribute "displayInAllList" of document type impacts the search result If the document type of one
	 * document is set to displayInAllList=false when search all open documents, this document should not be in search
	 * result
	 **/
	@Test
	public void shouldNotIncludeStatementDocument()
	{

		final Map<String, String> params = QueryParameters.with("currentPage", "0").and("pageSize", "10")
				.and("unit", UNIT_CUSTOM_RETAIL).and("sort", B2BDocumentModel.STATUS).and("searchRangeMax", "")
				.and("searchRangeMin", "").and("status", "status_all").and("searchBy", B2BDocumentModel.DOCUMENTNUMBER)
				.and("searchValue", "").buildMap();

		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.findDocuments(params);

		int count = 0;
		for (final B2BDocumentData element : result.getResults())
		{
			if (!element.getDocumentType().getDisplayInAllList().booleanValue())
			{
				count++;
			}
		}

		TestCase.assertEquals(0, count);
		TestCase.assertEquals(0, result.getPagination().getCurrentPage());
		TestCase.assertEquals(1, result.getPagination().getNumberOfPages());
		TestCase.assertEquals(3, result.getPagination().getTotalNumberOfResults());
	}

	@Test
	public void shouldIncludeStatementDocumentWhenSearchByDocumentNumber()
	{

		final Map<String, String> params = QueryParameters.with("currentPage", "0").and("pageSize", "10")
				.and("unit", UNIT_PRONTO_GOODS).and("status", AccountsummaryaddonConstants.SEARCH_STATUS_OPEN)
				.and("searchBy", B2BDocumentModel.DOCUMENTNUMBER).and("searchValue", "STA-001").buildMap();

		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.findDocuments(params);

		TestCase.assertEquals(1, result.getResults().size());
	}

	/**
	 * test how the attribute "displayInAllList" of document type impacts the search result If the document type of one
	 * document is set to displayInAllList=false when search by document number or type, this document should be in
	 * search result
	 **/
	@Test
	public void shouldIncludeStatementDocumentWhenSearchByDocumentType()
	{

		final Map<String, String> params = QueryParameters.with("currentPage", "0").and("pageSize", "10")
				.and("unit", UNIT_PRONTO_GOODS).and("searchBy", B2BDocumentModel.DOCUMENTTYPE).and("searchValue", "Statement")
				.buildMap();

		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.findDocuments(params);

		TestCase.assertEquals(1, result.getResults().size());
	}

	public void setDateRanges(final String minRange1, final String maxRange1, final String minRange2, final String maxRange2,
			final String minRange3, final String maxRange3, final String minRange4, final String maxRange4)
	{
		final Calendar baseDate = new GregorianCalendar(2013, Calendar.AUGUST, 13, 12, 0);
		final int days = Days.daysBetween(new DateTime(baseDate.getTime()), new DateTime()).getDays();

		Config.setParameter("accountsummaryaddon.daterange.1.start", minRange1);
		Config.setParameter("accountsummaryaddon.daterange.1.end", getRange(maxRange1, days));

		Config.setParameter("accountsummaryaddon.daterange.2.start", getRange(minRange2, days));
		Config.setParameter("accountsummaryaddon.daterange.2.end", getRange(maxRange2, days));

		Config.setParameter("accountsummaryaddon.daterange.3.start", getRange(minRange3, days));
		Config.setParameter("accountsummaryaddon.daterange.3.end", getRange(maxRange3, days));

		Config.setParameter("accountsummaryaddon.daterange.4.start", getRange(minRange4, days));
		Config.setParameter("accountsummaryaddon.daterange..end", getRange(maxRange4, days));
	}

	public String getRange(final String range, final int shift)
	{
		if (StringUtils.isEmpty(range))
		{
			return "";
		}
		else
		{
			final int total = shift + Integer.parseInt(range);
			return Integer.toString(total);
		}
	}


	/**
	 * This test will return 2 documents for unit: "Custom Retail" filtered on amountRange from "24" to "27" & sorted by
	 * byDocumentDateAsc based on the documents in testB2bdocument.csv
	 */
	@Test
	public void shouldGetPagedDocumentsForUnitFilteredByAmountRange()
	{
		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.getPagedDocumentsForUnit(UNIT_CUSTOM_RETAIL,
				createPageableData(0, 10, SORT_BY_DOCUMENT_DATE_ASC),
				createFilterByCriteriaData("24", "27", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY),
				filterByList.get(AMOUNT_RANGE));

		TestCase.assertEquals(2, result.getResults().size());
		TestCase.assertEquals("DBN-001", result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals("DBN-002", result.getResults().get(1).getDocumentNumber());
	}

	/**
	 * This test will return 1 document for unit: "Custom Retail" filtered on dueDateRange from "07/16/2013" to
	 * "09/16/2013" & sorted by byDocumentDateAsc based on the documents in testB2bdocument.csv
	 */
	@Test
	public void shouldGetPagedDocumentsForUnitFilteredByDateRange()
	{
		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.getPagedDocumentsForUnit(UNIT_CUSTOM_RETAIL,
				createPageableData(0, 10, SORT_BY_DOCUMENT_DATE_ASC),
				createFilterByCriteriaData("07/16/2013", "09/16/2013", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY),
				filterByList.get(DUE_DATE_RANGE));

		TestCase.assertEquals(1, result.getResults().size());
		TestCase.assertEquals("PUR-002", result.getResults().get(0).getDocumentNumber());
	}

	/**
	 * This test will return 2 documents for unit: "Services East" filtered on documentNumberRange from "INV-005" onwards
	 * & sorted by byDocumentDateAsc based on the documents in testB2bdocument.csv
	 */
	@Test
	public void shouldGetPagedDocumentsForUnitFilteredByDocumentNumberSortedByDocumentNumberDesc()
	{
		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.getPagedDocumentsForUnit(UNIT_SERVICES_EAST,
				createPageableData(0, 10, "byDocumentNumberDesc"),
				createFilterByCriteriaData("INV-005", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY),
				filterByList.get(DOCUMENT_NUMBER_RANGE));

		TestCase.assertEquals(2, result.getResults().size());
		TestCase.assertEquals("TES-005", result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals("INV-005", result.getResults().get(1).getDocumentNumber());
	}

	/**
	 * This test will return 3 documents for unit: "Pronto" filtered on documentType "Invoice" & sorted by
	 * byDocumentDateAsc based on the documents in testB2bdocument.csv
	 */
	@Test
	public void shouldGetPagedDocumentsForUnitFilteredByDocumentTypeSortedByDocumentDateAsc()
	{
		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.getPagedDocumentsForUnit(UNIT_PRONTO,
				createPageableData(0, 10, SORT_BY_DOCUMENT_DATE_ASC),
				createFilterByCriteriaData(StringUtils.EMPTY, StringUtils.EMPTY, "Invoice", StringUtils.EMPTY, StringUtils.EMPTY),
				filterByList.get(DOCUMENT_TYPE));

		TestCase.assertEquals(3, result.getResults().size());
		TestCase.assertEquals("INC-004", result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals("INV-004", result.getResults().get(1).getDocumentNumber());
		TestCase.assertEquals("CRN-004", result.getResults().get(2).getDocumentNumber());
	}

	/**
	 * This test will return 2 documents for unit: "Pronto Goods" filtered on dateRange from "07/08/2013" onwards &
	 * sorted by byDocumentStatusAsc based on the documents in testB2bdocument.csv. Ideally it would have returned 3
	 * documents, but since this unit contains a document of type Statement whose displayInAllList is set to false. So it
	 * would only return 2 documents instead of 3
	 */
	@Test
	public void shouldNotIncludeStatementDocumentInResult()
	{

		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.getPagedDocumentsForUnit(UNIT_PRONTO_GOODS,
				createPageableData(0, 10, SORT_BY_DOCUMENT_STATUS_ASC),
				createFilterByCriteriaData("07/08/2013", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY),
				filterByList.get(DATE_RANGE));

		int count = 0;
		for (final B2BDocumentData element : result.getResults())
		{
			if (!element.getDocumentType().getDisplayInAllList().booleanValue())
			{
				count++;
			}
		}

		TestCase.assertEquals(0, count);
		TestCase.assertEquals(0, result.getPagination().getCurrentPage());
		TestCase.assertEquals(1, result.getPagination().getNumberOfPages());
		TestCase.assertEquals(2, result.getPagination().getTotalNumberOfResults());
	}

	/**
	 * This test will return 3 documents for unit: "Pronto Goods" filtered on documentNumberRange from "CRN-003" onwards
	 * & sorted by byDocumentStatusAsc based on the documents in testB2bdocument.csv. Since we are searching on
	 * documentNumber, it would return 3 documents, including the document with documentType=Statement. When searching by
	 * documentNumber or documentType, the result documents will include documents with documentType.displayInAllList is
	 * set to false as well.
	 */
	@Test
	public void shouldIncludeStatementDocumentInResultWhenFilteredByDocumentNumber()
	{

		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.getPagedDocumentsForUnit(UNIT_PRONTO_GOODS,
				createPageableData(0, 10, SORT_BY_DOCUMENT_STATUS_ASC),
				createFilterByCriteriaData("CRN-003", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY),
				filterByList.get(DOCUMENT_NUMBER_RANGE));

		TestCase.assertEquals(0, result.getPagination().getCurrentPage());
		TestCase.assertEquals(1, result.getPagination().getNumberOfPages());
		TestCase.assertEquals(3, result.getPagination().getTotalNumberOfResults());
	}

	/**
	 * This test will return 1 document for unit: "Pronto Goods" filtered on documentType "Statement" & sorted by
	 * byDocumentStatusAsc based on the documents in testB2bdocument.csv. Since we are searching on documentNumber, it
	 * would return 1 document. When searching by documentNumber or documentType, the result documents will include
	 * documents with documentType.displayInAllList is set to false as well.
	 */
	@Test
	public void shouldIncludeStatementDocumentInResultWhenFilteredByDocumentType()
	{

		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.getPagedDocumentsForUnit(UNIT_PRONTO_GOODS,
				createPageableData(0, 10, SORT_BY_DOCUMENT_STATUS_ASC),
				createFilterByCriteriaData(StringUtils.EMPTY, StringUtils.EMPTY, "Statement", StringUtils.EMPTY, StringUtils.EMPTY),
				filterByList.get(DOCUMENT_TYPE));

		TestCase.assertEquals(0, result.getPagination().getCurrentPage());
		TestCase.assertEquals(1, result.getPagination().getNumberOfPages());
		TestCase.assertEquals(1, result.getPagination().getTotalNumberOfResults());
	}

	/**
	 * This test will return 3 documents for unit: "Services East" filtered on documentNumber "005" & sorted by
	 * byDocumentTypeAsc based on the documents in testB2bdocument.csv. Since we are searching on documentNumber, it
	 * would return 3 documents. When searching by documentNumber or documentType, the result documents will include
	 * documents with documentType.displayInAllList is set to false as well.
	 */
	@Test
	public void shouldReturnResultWithWildCardSearchWhenFilteredByDocumentNumber()
	{

		final SearchPageData<B2BDocumentData> result = b2bAccountSummaryFacade.getPagedDocumentsForUnit(UNIT_SERVICES_EAST,
				createPageableData(0, 10, BY_DOCUMENT_TYPE_ASC),
				createFilterByCriteriaData(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, "005"),
				filterByList.get(DOCUMENT_NUMBER));

		TestCase.assertEquals(3, result.getResults().size());
		TestCase.assertEquals("CRN-005", result.getResults().get(0).getDocumentNumber());
		TestCase.assertEquals("INV-005", result.getResults().get(1).getDocumentNumber());
		TestCase.assertEquals("TES-005", result.getResults().get(2).getDocumentNumber());
	}
}

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
package com.hybris.backoffice.excel.data;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

import org.junit.Test;


public class ImpexTest
{
	private static final String PRODUCT_TYPE_CODE = ProductModel._TYPECODE;
	private static final String CATEGORY_TYPE_CODE = CategoryModel._TYPECODE;

	private static final String APPROVAL_STATUS_CHECK = "Check";
	private static final String FIRST_PRODUCT_NAME = "Jeans";
	private static final String SECOND_PRODUCT_NAME = "Blue Jeans";
	private static final String CATEGORY_NAME = "Blue";
	private static final String CATEGORY_VERSION = "version(code, catalog(id))";
	private static final String APPROVAL_STATUS_APPROVED = "Approved";

	private static final Integer FIRST_ROW_INDEX = 0;
	private static final Integer SECOND_ROW_INDEX = 1;

	private static final ImpexHeaderValue PRODUCT_HEADER_NAME = new ImpexHeaderValue("name", false);
	private static final ImpexHeaderValue PRODUCT_HEADER_APPROVAL_STATUS = new ImpexHeaderValue("approvalStatus", false);
	private static final ImpexHeaderValue CATEGORY_HEADER_CODE = new ImpexHeaderValue("code", false);
	private static final ImpexHeaderValue CATEGORY_HEADER_VERSION = new ImpexHeaderValue("version(code, catalog(id))", false);

	@Test
	public void shouldFindUpdatesForTypeCode()
	{
		// given
		final Impex impex = new Impex();
		final ImpexForType expectedImpexForType = impex.createNewImpex("aTypeCode");
		impex.createNewImpex("otherTypeCode");

		// when
		final ImpexForType result = impex.findUpdates("aTypeCode");

		// then
		assertThat(result).isEqualTo(expectedImpexForType);
	}

	@Test
	public void shouldMergeImpexWhenMainImpexIsEmpty()
	{
		// given
		final Impex mainImpex = new Impex();
		final Impex subImpex = new Impex();
		final ImpexForType impexForType = subImpex.findUpdates(PRODUCT_TYPE_CODE);
		impexForType.putValue(FIRST_ROW_INDEX, PRODUCT_HEADER_NAME, FIRST_PRODUCT_NAME);

		// when
		mainImpex.mergeImpex(subImpex, PRODUCT_TYPE_CODE, FIRST_ROW_INDEX);

		// then
		assertThat(mainImpex.getImpexes()).hasSize(1);
		assertThat(mainImpex.getImpexes().get(0).getTypeCode()).isEqualTo(PRODUCT_TYPE_CODE);
		assertThat(mainImpex.getImpexes().get(0).getImpexTable().rowKeySet().size()).isEqualTo(1);
		assertThat(mainImpex.getImpexes().get(0).getImpexTable().columnKeySet().size()).isEqualTo(1);
		assertThat(mainImpex.getImpexes().get(0).getImpexTable().get(FIRST_ROW_INDEX, PRODUCT_HEADER_NAME))
				.isEqualTo(FIRST_PRODUCT_NAME);
	}

	@Test
	public void shouldMergeImpexForTheSameTypeCodeForExistingRow()
	{
		// given
		final Impex mainImpex = new Impex();
		final ImpexForType mainImpexForType = mainImpex.findUpdates(PRODUCT_TYPE_CODE);
		mainImpexForType.putValue(FIRST_ROW_INDEX, PRODUCT_HEADER_NAME, FIRST_PRODUCT_NAME);

		final Impex subImpex = new Impex();
		final ImpexForType impexForType = subImpex.findUpdates(PRODUCT_TYPE_CODE);
		impexForType.putValue(FIRST_ROW_INDEX, PRODUCT_HEADER_APPROVAL_STATUS, APPROVAL_STATUS_APPROVED);

		// when
		mainImpex.mergeImpex(subImpex, PRODUCT_TYPE_CODE, FIRST_ROW_INDEX);

		// then
		assertThat(mainImpex.getImpexes()).hasSize(1);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getTypeCode()).isEqualTo(PRODUCT_TYPE_CODE);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().rowKeySet().size()).isEqualTo(1);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().columnKeySet().size()).isEqualTo(2);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().get(FIRST_ROW_INDEX, PRODUCT_HEADER_NAME))
				.isEqualTo(FIRST_PRODUCT_NAME);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().get(FIRST_ROW_INDEX, PRODUCT_HEADER_APPROVAL_STATUS))
				.isEqualTo(APPROVAL_STATUS_APPROVED);
	}

	@Test
	public void shouldMergeImpexForTheSameTypeCodeForNextRow()
	{
		// given
		final Impex mainImpex = new Impex();
		final ImpexForType mainImpexForType = mainImpex.findUpdates(PRODUCT_TYPE_CODE);
		mainImpexForType.putValue(FIRST_ROW_INDEX, PRODUCT_HEADER_NAME, FIRST_PRODUCT_NAME);
		mainImpexForType.putValue(FIRST_ROW_INDEX, PRODUCT_HEADER_APPROVAL_STATUS, APPROVAL_STATUS_APPROVED);

		final Impex subImpex = new Impex();
		final ImpexForType impexForType = subImpex.findUpdates(PRODUCT_TYPE_CODE);
		impexForType.putValue(SECOND_ROW_INDEX, PRODUCT_HEADER_NAME, SECOND_PRODUCT_NAME);

		// when
		mainImpex.mergeImpex(subImpex, PRODUCT_TYPE_CODE, SECOND_ROW_INDEX);

		// then
		assertThat(mainImpex.getImpexes()).hasSize(1);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getTypeCode()).isEqualTo(PRODUCT_TYPE_CODE);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().rowKeySet().size()).isEqualTo(2);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().columnKeySet().size()).isEqualTo(2);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().get(FIRST_ROW_INDEX, PRODUCT_HEADER_NAME))
				.isEqualTo(FIRST_PRODUCT_NAME);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().get(FIRST_ROW_INDEX, PRODUCT_HEADER_APPROVAL_STATUS))
				.isEqualTo(APPROVAL_STATUS_APPROVED);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().get(SECOND_ROW_INDEX, PRODUCT_HEADER_NAME))
				.isEqualTo(SECOND_PRODUCT_NAME);
	}

	@Test
	public void shouldMergeImpexForDifferentTypeCode()
	{
		// given
		final Impex mainImpex = new Impex();
		final ImpexForType mainProductImpex = mainImpex.findUpdates(PRODUCT_TYPE_CODE);
		mainProductImpex.putValue(FIRST_ROW_INDEX, PRODUCT_HEADER_NAME, FIRST_PRODUCT_NAME);
		mainProductImpex.putValue(FIRST_ROW_INDEX, PRODUCT_HEADER_APPROVAL_STATUS, APPROVAL_STATUS_APPROVED);

		final Impex subImpex = new Impex();
		final ImpexForType productImpex = subImpex.findUpdates(PRODUCT_TYPE_CODE);
		productImpex.putValue(FIRST_ROW_INDEX, PRODUCT_HEADER_NAME, SECOND_PRODUCT_NAME);
		final ImpexForType categorySubImpex = subImpex.findUpdates(CATEGORY_TYPE_CODE);
		categorySubImpex.putValue(FIRST_ROW_INDEX, CATEGORY_HEADER_CODE, CATEGORY_NAME);
		categorySubImpex.putValue(FIRST_ROW_INDEX, CATEGORY_HEADER_VERSION, CATEGORY_VERSION);

		// when
		mainImpex.mergeImpex(subImpex, PRODUCT_TYPE_CODE, SECOND_ROW_INDEX);

		// then
		assertThat(mainImpex.getImpexes()).hasSize(2);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getTypeCode()).isEqualTo(CATEGORY_TYPE_CODE);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().rowKeySet().size()).isEqualTo(1);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().columnKeySet().size()).isEqualTo(2);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().get(FIRST_ROW_INDEX, CATEGORY_HEADER_CODE))
				.isEqualTo(CATEGORY_NAME);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().get(FIRST_ROW_INDEX, CATEGORY_HEADER_VERSION))
				.isEqualTo(CATEGORY_VERSION);

		assertThat(mainImpex.getImpexes().get(SECOND_ROW_INDEX).getTypeCode()).isEqualTo(PRODUCT_TYPE_CODE);
		assertThat(mainImpex.getImpexes().get(SECOND_ROW_INDEX).getImpexTable().rowKeySet().size()).isEqualTo(2);
		assertThat(mainImpex.getImpexes().get(SECOND_ROW_INDEX).getImpexTable().columnKeySet().size()).isEqualTo(2);
		assertThat(mainImpex.getImpexes().get(SECOND_ROW_INDEX).getImpexTable().get(FIRST_ROW_INDEX, PRODUCT_HEADER_NAME))
				.isEqualTo(FIRST_PRODUCT_NAME);
		assertThat(
				mainImpex.getImpexes().get(SECOND_ROW_INDEX).getImpexTable().get(FIRST_ROW_INDEX, PRODUCT_HEADER_APPROVAL_STATUS))
						.isEqualTo(APPROVAL_STATUS_APPROVED);
		assertThat(mainImpex.getImpexes().get(SECOND_ROW_INDEX).getImpexTable().get(SECOND_ROW_INDEX, PRODUCT_HEADER_NAME))
				.isEqualTo(SECOND_PRODUCT_NAME);
	}

	@Test
	public void shouldMergeTwoImpexes()
	{
		// given
		final Impex mainImpex = new Impex();
		final ImpexForType mainProductImpex = mainImpex.findUpdates(PRODUCT_TYPE_CODE);
		mainProductImpex.putValue(FIRST_ROW_INDEX, PRODUCT_HEADER_NAME, FIRST_PRODUCT_NAME);
		mainProductImpex.putValue(FIRST_ROW_INDEX, PRODUCT_HEADER_APPROVAL_STATUS, APPROVAL_STATUS_APPROVED);

		final Impex subImpex = new Impex();
		final ImpexForType categoryImpex = subImpex.findUpdates(CATEGORY_TYPE_CODE);
		categoryImpex.putValue(FIRST_ROW_INDEX, CATEGORY_HEADER_CODE, CATEGORY_NAME);
		categoryImpex.putValue(FIRST_ROW_INDEX, CATEGORY_HEADER_VERSION, CATEGORY_VERSION);

		final ImpexForType productSubImpex = subImpex.findUpdates(PRODUCT_TYPE_CODE);
		productSubImpex.putValue(FIRST_ROW_INDEX, PRODUCT_HEADER_NAME, SECOND_PRODUCT_NAME);
		productSubImpex.putValue(FIRST_ROW_INDEX, PRODUCT_HEADER_APPROVAL_STATUS, APPROVAL_STATUS_CHECK);

		// when
		mainImpex.mergeImpex(subImpex);

		// then
		assertThat(mainImpex.getImpexes()).hasSize(2);

		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getTypeCode()).isEqualTo(CATEGORY_TYPE_CODE);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().rowKeySet().size()).isEqualTo(1);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().columnKeySet().size()).isEqualTo(2);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().get(FIRST_ROW_INDEX, CATEGORY_HEADER_CODE))
				.isEqualTo(CATEGORY_NAME);
		assertThat(mainImpex.getImpexes().get(FIRST_ROW_INDEX).getImpexTable().get(FIRST_ROW_INDEX, CATEGORY_HEADER_VERSION))
				.isEqualTo(CATEGORY_VERSION);

		assertThat(mainImpex.getImpexes().get(SECOND_ROW_INDEX).getTypeCode()).isEqualTo(PRODUCT_TYPE_CODE);
		assertThat(mainImpex.getImpexes().get(SECOND_ROW_INDEX).getImpexTable().rowKeySet().size()).isEqualTo(2);
		assertThat(mainImpex.getImpexes().get(SECOND_ROW_INDEX).getImpexTable().columnKeySet().size()).isEqualTo(2);
		assertThat(mainImpex.getImpexes().get(SECOND_ROW_INDEX).getImpexTable().get(FIRST_ROW_INDEX, PRODUCT_HEADER_NAME))
				.isEqualTo(FIRST_PRODUCT_NAME);
		assertThat(
				mainImpex.getImpexes().get(SECOND_ROW_INDEX).getImpexTable().get(FIRST_ROW_INDEX, PRODUCT_HEADER_APPROVAL_STATUS))
						.isEqualTo(APPROVAL_STATUS_APPROVED);
		assertThat(mainImpex.getImpexes().get(SECOND_ROW_INDEX).getImpexTable().get(SECOND_ROW_INDEX, PRODUCT_HEADER_NAME))
				.isEqualTo(SECOND_PRODUCT_NAME);
		assertThat(
				mainImpex.getImpexes().get(SECOND_ROW_INDEX).getImpexTable().get(SECOND_ROW_INDEX, PRODUCT_HEADER_APPROVAL_STATUS))
						.isEqualTo(APPROVAL_STATUS_CHECK);
	}
}

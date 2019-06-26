/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.dataimportcommons.facades;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;

@UnitTest
public class ErrorCodeUnitTest
{
	@Test
	public void testClassifiesNullInputMessageAsNullErrorCode()
	{
		assertThat(ErrorCode.classify(null)).isNull();
	}

	@Test
	public void testClassifiesBlankInputMessagesAsNullErrorCode()
	{
		assertThat(ErrorCode.classify(" ")).isNull();
	}

	@Test
	public void testClassifiesRandomTextAsUNCLASSIFIED()
	{
		assertClassification("ImpEx never produces this message", ErrorCode.UNCLASSIFIED, null);
	}

	@Test
	public void testMissingRequiredAttributeError()
	{
		assertClassification("value is NULL for mandatory attribute Product.code", ErrorCode.MISSING_REQUIRED_ATTRIBUTE,
				ErrorType.ATTRIBUTE_VALUE);
	}

	@Test
	public void testUnknownAttributeError()
	{
		assertClassification("unknown attribute '$catalogVersion' in header 'INSERT_UPDATE Product'", ErrorCode.UNKNOWN_ATTRIBUTE,
				ErrorType.HEADER);
	}

	@Test
	public void testNotExistingItem()
	{
		assertClassification("no existing item found for update", ErrorCode.NOT_EXISTING_ITEM, ErrorType.REFERENCE);
	}

	@Test
	public void testNotExistingReferencedItem()
	{
		assertClassification("column 4: could not resolve item for piece", ErrorCode.REFERENCE_VIOLATION, ErrorType.REFERENCE);
	}

	@Test
	public void testUniqueKeyViolation()
	{
		assertClassification("conflict between existing item Product 'A1'", ErrorCode.UNIQUE_KEY_VIOLATION,
				ErrorType.ATTRIBUTE_VALUE);
	}

	@Test
	public void testNotEnoughAttributeValues()
	{
		assertClassification("item reference a for attribute CatalogVersion.version does not provide enough values at position 1",
				ErrorCode.NOT_ENOUGH_ATTRIBUTE_VALUES, ErrorType.ATTRIBUTE_VALUE);
	}

	@Test
	public void testInvalidValueFormat()
	{
		assertClassification("cannot parse number 'a' with format specified pattern '#,##0.###' due to Unparseable number: \"a\"",
				ErrorCode.INVALID_DATA_FORMAT, ErrorType.ATTRIBUTE_VALUE);
		assertClassification("Exception 'cannot parse number 'one' with format specified pattern '#,##0.###' due to Unparseable number: \"one\"' in handling exception: cannot parse number 'one' with format specified pattern '#,##0.###' due to Unparseable number: \"one\"",
				ErrorCode.INVALID_DATA_FORMAT, ErrorType.ATTRIBUTE_VALUE);
	}

	@Test
	public void testMoreThanOneItemExist()
	{
		assertClassification("more than one item found for 'M25687' using query with values", ErrorCode.MORE_THAN_ONE_ITEM_FOUND,
				ErrorType.ATTRIBUTE_VALUE);
	}

	@Test
	public void testInvalidReferencedValue()
	{
		assertClassification(
				"error finding existing item : column='catalogversion' value='', , column 5: cannot resolve value '' for attribute 'catalogversion'",
				ErrorCode.REFERENCE_VIOLATION, ErrorType.REFERENCE);
	}

	@Test
	public void testHeaderNotFound()
	{
		assertClassification("no current header for value line: INSERT_UPDTE Product;;code[unique=true]; name[lang=en]",
				ErrorCode.HEADER_NOT_FOUND, ErrorType.HEADER);
	}

	@Test
	public void testUnknownType()
	{
		assertClassification("unknown type 'MyProduct' in header 'INSERT_UPDATE MyProduct'", ErrorCode.UNKNOWN_TYPE,
				ErrorType.HEADER);
	}

	@Test
	public void testCannotRemoveItem()
	{
		assertClassification("could not remove item 8796160688270 due to [de.hybris.platform.category.interceptors.CategoryRemovalValidator@6427d506]:cannot remove [C12], since this category still has sub-categories", ErrorCode.CANNOT_REMOVE_ITEM,
				ErrorType.REFERENCE);
	}

	@Test
	public void testNoVariantType()
	{
		assertClassification("Proposed base product Product 'M35364' (8796224159745) got no variant type - cannot be base product",
				ErrorCode.NO_VARIANT_TYPE, ErrorType.ATTRIBUTE_VALUE);
	}

	private void assertClassification(final String inputMsg, final ErrorCode expCode, final ErrorType expType)
	{
		final ErrorCode ecode = ErrorCode.classify(inputMsg);
		assertThat(ecode).isSameAs(expCode);
		assertThat(ecode.getType()).isSameAs(expType);
	}

	@Test
	public void testToStringReturnsCodeValue()
	{
		assertThat(ErrorCode.UNCLASSIFIED.toString()).isEqualTo("UNCLASSIFIED");
	}
}

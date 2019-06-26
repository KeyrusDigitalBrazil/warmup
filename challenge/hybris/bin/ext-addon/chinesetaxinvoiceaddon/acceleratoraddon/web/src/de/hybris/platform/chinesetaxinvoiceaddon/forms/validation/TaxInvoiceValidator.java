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
package de.hybris.platform.chinesetaxinvoiceaddon.forms.validation;

import de.hybris.platform.chinesetaxinvoiceaddon.forms.TaxInvoiceForm;
import de.hybris.platform.chinesetaxinvoiceservices.enums.InvoiceRecipientType;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component("taxInvoiceValidator")
public class TaxInvoiceValidator implements Validator
{

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return TaxInvoiceForm.class == clazz;
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final TaxInvoiceForm invoiceForm = (TaxInvoiceForm) object;
		validateNotNullField(invoiceForm.getCategory(), InvoiceField.CATEGORY, errors);
		validateNotNullField(invoiceForm.getRecipientType(), InvoiceField.RECIPIENT_TYPE, errors);
		validateInvoiceName(invoiceForm, 255, errors);
	}

	protected static void validateInvoiceName(final TaxInvoiceForm invoiceForm, final int maxFieldLength, final Errors errors)
	{

		if (StringUtils.isNotBlank(invoiceForm.getRecipientType())
				&& invoiceForm.getRecipientType().equals(InvoiceRecipientType.UNIT.getCode()))
		{
			final String recipient = invoiceForm.getRecipient();
			if (recipient == null || StringUtils.isEmpty(recipient) || (StringUtils.length(recipient) > maxFieldLength))
			{
				errors.rejectValue(InvoiceField.RECIPIENT.getFieldKey(), InvoiceField.RECIPIENT.getErrorKey());
			}
		}
	}

	protected static void validateNotNullField(final String invoiceField, final InvoiceField fieldType, final Errors errors)
	{
		if (invoiceField == null || StringUtils.isEmpty(invoiceField))
		{
			errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
		}
	}

	private enum InvoiceField
	{

		RECIPIENT("recipient", "invoice.recipient.invalid"), CATEGORY("category", "invoice.category.invalid"), RECIPIENT_TYPE(
				"recipientType", "invoice.recipientType.invalid");

		private final String fieldKey;
		private final String errorKey;

		private InvoiceField(final String fieldKey, final String errorKey)
		{
			this.fieldKey = fieldKey;
			this.errorKey = errorKey;
		}

		public String getFieldKey()
		{
			return fieldKey;
		}

		public String getErrorKey()
		{
			return errorKey;
		}
	}
}

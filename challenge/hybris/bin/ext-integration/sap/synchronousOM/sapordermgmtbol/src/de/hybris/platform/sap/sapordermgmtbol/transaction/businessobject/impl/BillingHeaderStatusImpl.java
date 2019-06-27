/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillingHeaderStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;


/**
 * Billing status for the header in the sales document
 *
 */
public class BillingHeaderStatusImpl extends BillingStatusImpl implements BillingHeaderStatus
{


	/**
	 *
	 */
	public BillingHeaderStatusImpl()
	{
		super();
	}


	/**
	 * Only for unit tests. Other callers need to instantiate via {@link GenericFactory} and call init method}.
	 *
	 * @param key
	 *           status key
	 */
	public BillingHeaderStatusImpl(final EStatus key)
	{
		super(key);
	}


	/**
	 * Only for unit tests. Other callers should use generic factory and init method
	 *
	 * @param dlvStatus
	 *           Delivery Status
	 * @param ordInvoiceStatus
	 *           Order Invoice Status
	 * @param dlvInvoiceStatus
	 *           Delivery Invoice Status
	 * @param rjStatus
	 *           Rejected Status
	 */
	public BillingHeaderStatusImpl(final EStatus dlvStatus, final EStatus ordInvoiceStatus, final EStatus dlvInvoiceStatus,
			final EStatus rjStatus)
	{
		super(dlvStatus, ordInvoiceStatus, dlvInvoiceStatus, rjStatus);
	}

	@Override
	public boolean isNotRelevant()
	{
		// FKSAK FKSTK LFSTK
		// ----------------------------------
		// Empty Empty Empty
		// Empty Empty B
		// Empty Empty C
		// -----------------------------------

		final boolean isNotRelevant = this.ordInvoiceStatus == EStatus.NOT_RELEVANT
				&& this.dlvInvoiceStatus == EStatus.NOT_RELEVANT;
		final boolean isProcessed = this.dlvStatus == EStatus.NOT_RELEVANT || this.dlvStatus == EStatus.PARTIALLY_PROCESSED
				|| this.dlvStatus == EStatus.PROCESSED;
		return isNotRelevant && isProcessed;

	}


	@Override
	@SuppressWarnings("squid:MethodCyclomaticComplexity")
	public boolean isNotProcessed()
	{
		// FKSAK FKSTK LFSTK
		// ------------------------------------
		// Empty Empty A
		// A Empty Does not matter
		// Empty A Does not matter
		// A A Does not matter
		// ------------------------------------
		final boolean isProcessesdStatus = this.ordInvoiceStatus == EStatus.NOT_RELEVANT
				&& this.dlvInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvStatus == EStatus.NOT_PROCESSED;
		final boolean ordInvoiceNotProcessedDlvInvoiceNotRelevent = this.ordInvoiceStatus == EStatus.NOT_PROCESSED
				&& this.dlvInvoiceStatus == EStatus.NOT_RELEVANT;
		final boolean ordInvoiceNotReleventDlvInvoiceNotProcessed = this.ordInvoiceStatus == EStatus.NOT_RELEVANT
				&& this.dlvInvoiceStatus == EStatus.NOT_PROCESSED;
		final boolean ordInvoiceNotProcessedDlvNotProcessed = this.ordInvoiceStatus == EStatus.NOT_PROCESSED
				&& this.dlvInvoiceStatus == EStatus.NOT_PROCESSED;
		final boolean wecStatusNotProcessed = this.ordInvoiceStatus == null && this.dlvInvoiceStatus == null
				&& this.wecStatus == EStatus.NOT_PROCESSED;

		final boolean isOtherStatus = ordInvoiceNotProcessedDlvInvoiceNotRelevent || ordInvoiceNotReleventDlvInvoiceNotProcessed
				|| ordInvoiceNotProcessedDlvNotProcessed || wecStatusNotProcessed;
		return isProcessesdStatus || isOtherStatus;
	}

	@SuppressWarnings("squid:MethodCyclomaticComplexity")
	@Override
	public boolean isPartiallyProcessed()
	{
		// FKSAA FKSTA LFSTA
		// ------------------------------------
		// B Does not mater Does not matter
		// C Empty A
		// C Empty B
		// C Empty C
		// C A Does not matter
		// Does not matter B Does not matter
		// Does not matter C B
		// A C Does not matter
		// ------------------------------------
		final boolean isPartiallyProcessed = this.ordInvoiceStatus == EStatus.PARTIALLY_PROCESSED;
		final boolean ordInvoiceProcessedDlvInvoiceNotRelevantDlvNotProcessed = this.ordInvoiceStatus == EStatus.PROCESSED
				&& this.dlvInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvStatus == EStatus.NOT_PROCESSED;
		final boolean ordInvoiceProcessedDlvInvoiceNotRelevantDlvPartiallyProcessed = this.ordInvoiceStatus == EStatus.PROCESSED
				&& this.dlvInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvStatus == EStatus.PARTIALLY_PROCESSED;
		final boolean ordInvoiceProcessedDlvInvoiceNotRelevantDlvProcessed = this.ordInvoiceStatus == EStatus.PROCESSED
				&& this.dlvInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvStatus == EStatus.PROCESSED;
		final boolean ordInvoiceProcessedDlvInvoiceNotProcessed = this.ordInvoiceStatus == EStatus.PROCESSED
				&& this.dlvInvoiceStatus == EStatus.NOT_PROCESSED;
		final boolean dlvInvoicePartiallyProcessed = this.dlvInvoiceStatus == EStatus.PARTIALLY_PROCESSED;
		final boolean dlvInvoiceProcessedDlvPartiallyProcessed = this.dlvInvoiceStatus == EStatus.PROCESSED
				&& this.dlvStatus == EStatus.PARTIALLY_PROCESSED;
		final boolean ordInvoiceNotProcessedDlvInvoiceProcessed = this.ordInvoiceStatus == EStatus.NOT_PROCESSED
				&& this.dlvInvoiceStatus == EStatus.PROCESSED;
		final boolean ordInvoicePartiallyProcessed = isPartiallyProcessed || ordInvoiceProcessedDlvInvoiceNotRelevantDlvNotProcessed
				|| ordInvoiceProcessedDlvInvoiceNotRelevantDlvPartiallyProcessed
				|| ordInvoiceProcessedDlvInvoiceNotRelevantDlvProcessed;
		final boolean ordInvoiceProcessed = ordInvoiceProcessedDlvInvoiceNotProcessed || dlvInvoicePartiallyProcessed
				|| dlvInvoiceProcessedDlvPartiallyProcessed || ordInvoiceNotProcessedDlvInvoiceProcessed;
		return ordInvoicePartiallyProcessed || ordInvoiceProcessed;

	}

	@SuppressWarnings("squid:S1067")
	@Override
	public boolean isProcessed()
	{
		// FKSAA FKSTA LFSTA
		// -------------------------------------
		// C Empty Does not matter
		// Empty C C
		// C C C
		// -------------------------------------
		return (this.ordInvoiceStatus == EStatus.PROCESSED && this.dlvInvoiceStatus == EStatus.NOT_RELEVANT)
				|| (this.ordInvoiceStatus == EStatus.NOT_RELEVANT && this.dlvInvoiceStatus == EStatus.PROCESSED
						&& this.dlvStatus == EStatus.PROCESSED)
				|| (this.ordInvoiceStatus == EStatus.PROCESSED && this.dlvInvoiceStatus == EStatus.PROCESSED
						&& this.dlvStatus == EStatus.PROCESSED);
	}

}

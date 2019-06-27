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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

/**
 * This enumeration represents a status object. <br>
 *
 */
public enum EStatus
{
	/**
	 * Status is not applicable.
	 */
	NOT_RELEVANT(' '),
	/**
	 * Status is not processed yet.
	 */
	NOT_PROCESSED('A'),
	/**
	 * Status has been processed, but is not yet completely processed.
	 */
	PARTIALLY_PROCESSED('B'),
	/**
	 * Status has already been processed.
	 */
	PROCESSED('C'),
	/**
	 * Status has been cancelled.
	 */
	CANCELLED('D'),
	/**
	 * Status is expired.
	 */
	EXPIRED('E'),
	/**
	 * Status is accepted.
	 */
	ACCEPTED('F'),
	/**
	 * Status is open.
	 */
	OPEN('G'),
	/**
	 * Status Request for Quotation
	 */
	REQUEST_QUOTATION('O'),
	/**
	 * Status is not defined.
	 */
	UNDEFINED('Z');

	private char status;

	EStatus(final char status)
	{
		this.setStatus(status);
	}

	/**
	 * Determines status for given backend status.<br>
	 *
	 * @param bkndStatus
	 *           Backend status
	 * @return status
	 */
	@SuppressWarnings("squid:MethodCyclomaticComplexity")
	public static EStatus getStatusType(final char bkndStatus)
	{
		EStatus status;
		switch (bkndStatus)
		{
			case 'A':
				status = NOT_PROCESSED;
				break;
			case 'B':
				status = PARTIALLY_PROCESSED;
				break;
			case 'C':
				status = PROCESSED;
				break;
			case 'D':
				status = CANCELLED;
				break;
			case ' ':
				status = NOT_RELEVANT;
				break;
			case 'E':
				status = EXPIRED;
				break;
			case 'F':
				status = ACCEPTED;
				break;
			case 'G':
				status = OPEN;
				break;
			case 'O':
				status = REQUEST_QUOTATION;
				break;
			default:
				status = UNDEFINED;
		}
		return status;
	}

	private void setStatus(final char status)
	{
		this.status = status;
	}

	/**
	 * Returns status.<br>
	 *
	 * @return status
	 */
	public char getStatus()
	{
		return status;
	}

	/**
	 * Cumulates status<br>
	 *
	 * @param inStatus
	 *           Additional status to be concerned in the cumulation
	 * @param status
	 *           Previously cumulated status
	 * @return cumulated status
	 */
	public static EStatus cumulateStatus(final EStatus inStatus, final EStatus status)
	{

		EStatus result = status;
		switch (status)
		{
			case NOT_RELEVANT:
				result = inStatus;
				break;
			case CANCELLED:
				result = inStatus;
				break;
			case NOT_PROCESSED:
				result = evaluateNotProcessedStatus(result, inStatus);
				break;
			case PARTIALLY_PROCESSED:
				result = EStatus.PARTIALLY_PROCESSED;
				break;
			case PROCESSED:
				result = evaluateProcessedStatus(result, inStatus);
				break;
			default:
				result = status;

		}
		return result;
	}

	/**
	 * Evaluate Not Processed Status
	 *
	 * @param result
	 * @param inStatus
	 * @return EStatus
	 */
	private static EStatus evaluateNotProcessedStatus(final EStatus result, final EStatus inStatus)
	{
		EStatus evaluatedResult = result;
		if (inStatus == EStatus.PARTIALLY_PROCESSED || inStatus == EStatus.PROCESSED)
		{
			evaluatedResult = EStatus.PARTIALLY_PROCESSED;
		}
		return evaluatedResult;
	}

	/**
	 * Evaluate Not Processed Status
	 *
	 * @param result
	 * @param inStatus
	 * @return EStatus
	 */
	private static EStatus evaluateProcessedStatus(final EStatus result, final EStatus inStatus)
	{
		EStatus evaluatedResult = result;
		if (inStatus == EStatus.PARTIALLY_PROCESSED || inStatus == EStatus.NOT_PROCESSED)
		{
			evaluatedResult = EStatus.PARTIALLY_PROCESSED;
		}
		return evaluatedResult;
	}

}

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

/**
 * @ngdoc object
 * @name ValidationError.object:ValidationError
 * @description
 * An object representing the backend response for any erroy of type "ValidationError"
 */
export interface ValidationError {
	language?: string;
	message: string;
	reason: string;
	subject: string;
	subjectType: string;
	errorCode: string;
	type: "ValidationError";
}
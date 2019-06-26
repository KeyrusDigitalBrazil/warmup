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
 * @name User.object:User
 * @description
 * An object containing information about a user in SmartEdit. 
 */
export interface User {
	// Note: 
	// - We should be careful when adding fields to this DTO. It should not contain any 
	// confidential or personal information.  
	uid: string;
	displayName: string;
	readableLanguages: string[];
	writeableLanguages: string[];
}
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

import {TypedMap} from 'smarteditcommons';

/**
 * @description
 * Interface for synchronization information
 */
export interface ISyncStatus {
	catalogVersionUuid: string;
	dependentItemTypesOutOfSync: TypedMap<string>[];
	itemId: string;
	itemType: string;
	name: string;
	status: string;
	lastSyncStatus: number;
	selectedDependencies: ISyncStatus[];
	sharedDependencies: ISyncStatus[];
	unavailableDependencies: ISyncStatus[];
}
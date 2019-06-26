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
import {Cloneable, TypedMap} from 'smarteditcommons';

/** @internal */
export class ConfigurationItem {
	key: string;
	value: string;
	isNew?: boolean;
	toDelete?: boolean;
	requiresUserCheck?: boolean;
	isCheckedByUser?: boolean;
	hasErrors?: boolean;
	errors?: TypedMap<{message: string}[]>;
}
/** @internal */
// Configuration structure from a REST payload standpoint
export type Configuration = ConfigurationItem[];

/** @internal */
// Configuration structure after conversion to object
export type ConfigurationObject = TypedMap<Cloneable>;

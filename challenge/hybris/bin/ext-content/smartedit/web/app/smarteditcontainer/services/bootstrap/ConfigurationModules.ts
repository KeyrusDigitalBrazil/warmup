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

/** @internal */
export interface Module {

	/*
	 * absolute URL location of a "plugin" application to be added to smarteditcontainer or smartedit
	 */
	location: string;

	/*
	 * top most angular module name to be found at the given location
	 */
	name: string;

	/*
	 * name of another application that this is extending
	 */
	extends?: string;
}
/** @internal */
export interface ConfigurationModules {

	authenticationMap: TypedMap<string>;

	applications: Module[];
}
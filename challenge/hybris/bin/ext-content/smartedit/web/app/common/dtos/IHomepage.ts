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
import {Payload} from 'smarteditcommons';

export interface IHomepageVersions extends Payload {
	current?: IHomepage;
	old?: IHomepage;
	fallback?: IHomepage;
}

export interface IHomepage extends Payload {
	uid: string;
	name: string;
	catalogVersionUuid: string;
}
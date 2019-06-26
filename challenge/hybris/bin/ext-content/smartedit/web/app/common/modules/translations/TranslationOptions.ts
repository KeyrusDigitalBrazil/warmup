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
import {TranslationFile} from './TranslationFile';
import {Payload, TypedMap} from 'smarteditcommons/dtos';

/** @internal */
export class TranslationOptions {
	prefix?: string;
	key: string;
	suffix?: string;
	files?: TranslationFile[];
	fileMap?: TypedMap<string>;
	$http?: Payload;
}

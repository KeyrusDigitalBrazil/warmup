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
import {CMSRestriction} from './';

/**
 * @description
 * Interface for cms-page information
 */
export interface ICMSPage {
	name: string;
	label?: string;
	type?: {
		[index: string]: string
	};
	uid: string;
	uuid: string;
	[index: string]: any;
	pageStatus: CMSPageStatus;
	template?: string;
	masterTemplate: string;
	title: {
		[index: string]: string;
	};
	defaultPage: boolean;
	creationtime: Date;
	modifiedtime: Date;
	onlyOneRestrictionMustApply?: boolean;
	restrictions: CMSRestriction[];
	identifier?: string;
	typeCode: CMSPageTypes;
	homepage: boolean;
	catalogVersion: string;
}

export enum CMSPageTypes {
	ContentPage = 'ContentPage',
	CategoryPage = 'CategoryPage',
	ProductPage = 'ProductPage'
}

export enum CMSPageStatus {
	ACTIVE = 'ACTIVE',
	DELETED = 'DELETED'
}

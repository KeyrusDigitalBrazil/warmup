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

import * as angular from "angular";
import {SeInjectable} from 'smarteditcommons';
import {PersonalizationsmarteditRestService} from "personalizationsmarteditcontainer/service/PersonalizationsmarteditRestService";

// copy of cmsSmarteditServicesModule.interfaces:IPageVersion
interface IPageVersion {
	uid: string;
	itemUUID: string;
	creationtime: Date;
	label: string;
	description?: string;
}

@SeInjectable()
export class VersionCheckerService {

	// storage for asynchronusly changed page version
	private version: IPageVersion;

	constructor(
		private $q: angular.IQService,
		private personalizationsmarteditRestService: PersonalizationsmarteditRestService,
		private pageVersionSelectionService: any
	) {}

	public setVersion(version: IPageVersion) {
		this.version = version;
	}

	public provideTranlationKey(key: string): angular.IPromise<string> {
		const TRANSLATE_NS: string = 'personalization.se.cms.actionitem.page.version.rollback.confirmation';

		this.version = this.version || this.pageVersionSelectionService.getSelectedPageVersion();

		if (!!this.version) {
			return this.personalizationsmarteditRestService.checkVersionConflict(this.version.uid).then(
				(response: any) => {
					return response.result ? key : TRANSLATE_NS;
				}, () => {
					return key;
				}
			);
		} else {
			return this.$q.when(key);
		}
	}

}



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
import {SeInjectable} from "smarteditcommons";

@SeInjectable()
export class SeRichTextLoaderService {

	private loadDeferred: angular.IDeferred<{}>;

	constructor(
		private $q: angular.IQService,
		private $interval: angular.IIntervalService
	) {
		this.loadDeferred = this.$q.defer();

		const checkLoadedInterval = this.$interval(() => {
			if (CKEDITOR.status === 'loaded') {
				this.loadDeferred.resolve();
				$interval.cancel(checkLoadedInterval);
			}
		}, 100);
	}

	load(): angular.IPromise<{}> {
		const deferred = this.$q.defer();
		this.loadDeferred.promise.then(function() {
			deferred.resolve();
		});
		return deferred.promise;
	}

}

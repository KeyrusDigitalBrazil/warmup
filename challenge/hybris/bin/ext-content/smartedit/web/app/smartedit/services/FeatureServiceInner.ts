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
import * as angular from 'angular';
import * as lo from 'lodash';
import {CloneableUtils, GatewayProxied, IContextualMenuButton, IDecorator, IFeatureService, SeInjectable, TypedMap} from 'smarteditcommons';
import {ContextualMenuService} from 'smartedit/services';

/** @internal */
@GatewayProxied('_registerAliases', 'addToolbarItem', 'register', 'enable', 'disable', '_remoteEnablingFromInner', '_remoteDisablingFromInner', 'addDecorator', 'getFeatureProperty', 'addContextualMenuButton')
@SeInjectable()
export class FeatureService extends IFeatureService {

	constructor(
		private $log: angular.ILogService,
		private decoratorService: any,
		lodash: lo.LoDashStatic,
		cloneableUtils: CloneableUtils,
		private $q: angular.IQService,
		private contextualMenuService: ContextualMenuService
	) {
		super(lodash, cloneableUtils);
	}

	addDecorator(configuration: IDecorator): angular.IPromise<void> {
		const prevEnablingCallback = configuration.enablingCallback;
		const prevDisablingCallback = configuration.disablingCallback;
		const displayCondition = configuration.displayCondition;

		configuration.enablingCallback = function() {
			this.enable(configuration.key, displayCondition);

			if (prevEnablingCallback) {
				prevEnablingCallback();
			}
		}.bind(this.decoratorService);

		configuration.disablingCallback = function() {
			this.disable(configuration.key);

			if (prevDisablingCallback) {
				prevDisablingCallback();
			}
		}.bind(this.decoratorService);

		delete configuration.displayCondition;

		return this.register(configuration);
	}

	addContextualMenuButton(item: IContextualMenuButton): angular.IPromise<void> {
		const clone = angular.copy(item);

		delete item.nameI18nKey;
		delete item.descriptionI18nKey;
		delete item.regexpKeys;

		clone.enablingCallback = function() {
			const mapping: TypedMap<IContextualMenuButton[]> = {};
			clone.regexpKeys.forEach((regexpKey: string) => {
				mapping[regexpKey] = [item];
			});
			this.addItems(mapping);
		}.bind(this.contextualMenuService);

		clone.disablingCallback = function() {
			this.removeItemByKey(clone.key);
		}.bind(this.contextualMenuService);

		return this.register(clone);
	}

	protected _remoteEnablingFromInner(key: string): angular.IPromise<void> {
		if (this._featuresToAlias && this._featuresToAlias[key]) {
			this._featuresToAlias[key].enablingCallback();
		} else {
			this.$log.warn("could not enable feature named " + key + ", it was not found in the iframe");
		}
		return this.$q.when();
	}

	protected _remoteDisablingFromInner(key: string): angular.IPromise<void> {
		if (this._featuresToAlias && this._featuresToAlias[key]) {
			this._featuresToAlias[key].disablingCallback();
		} else {
			this.$log.warn("could not disable feature named " + key + ", it was not found in the iframe");
		}
		return this.$q.when();
	}

}
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

import {
	GatewayProxied,
	IPermissionService,
	IPerspective,
	IPerspectiveService,
	IWaitDialogService,
	SeInjectable,
	SystemEventService
} from 'smarteditcommons';
import {FeatureService, StorageService} from 'smarteditcontainer/services';

interface IPerspectiveData {
	activePerspective: IPerspective;
	previousPerspective: IPerspective;
	previousSwitchToArg: string;
}

/** @internal */
@GatewayProxied('register', 'switchTo', 'hasActivePerspective', 'isEmptyPerspectiveActive', 'selectDefault', 'refreshPerspective', 'getActivePerspectiveKey', 'isHotkeyEnabledForActivePerspective')
@SeInjectable()
export class PerspectiveService extends IPerspectiveService {

	private PERSPECTIVE_COOKIE_NAME: string = 'smartedit-perspectives';
	private INITIAL_SWITCHTO_ARG = 'INITIAL_SWITCHTO_ARG';

	private data: IPerspectiveData = {
		activePerspective: undefined,
		previousPerspective: undefined,
		previousSwitchToArg: this.INITIAL_SWITCHTO_ARG
	};

	private immutablePerspectives: IPerspective[] = []; // once a perspective is registered it will always exists in this variable
	private perspectives: IPerspective[] = [];

	constructor(
		private $rootScope: angular.IRootScopeService,
		private $location: angular.ILocationService,
		private STORE_FRONT_CONTEXT: string,
		private $log: angular.ILogService,
		private $q: angular.IQService,
		private isBlank: (arg: any) => boolean,
		private uniqueArray: any,
		private systemEventService: SystemEventService,
		private featureService: FeatureService,
		private waitDialogService: IWaitDialogService,
		private storageService: StorageService,
		private crossFrameEventService: any,
		private NONE_PERSPECTIVE: string,
		private ALL_PERSPECTIVE: string,
		private EVENTS: any,
		private EVENT_PERSPECTIVE_CHANGED: string,
		private EVENT_PERSPECTIVE_UNLOADING: string,
		private EVENT_PERSPECTIVE_ADDED: string,
		private EVENT_PERSPECTIVE_REFRESHED: string,
		private permissionService: IPermissionService) {
		super();
		this._addDefaultPerspectives();
		this._registerEventHandlers();
	}

	register(configuration: IPerspective): angular.IPromise<void> {
		this._validate(configuration);

		let perspective: IPerspective = this._findByKey(configuration.key);

		if (!perspective) {
			perspective = configuration;
			perspective.isHotkeyDisabled = !!configuration.isHotkeyDisabled;
			this.immutablePerspectives.push(perspective);
			this.perspectives.push(perspective);
		}

		perspective.features = this.uniqueArray(perspective.features || [], configuration.features || []);
		perspective.perspectives = this.uniqueArray(perspective.perspectives || [], configuration.perspectives || []);
		perspective.permissions = this.uniqueArray(perspective.permissions || [], configuration.permissions || []);

		this.systemEventService.publishAsync(this.EVENT_PERSPECTIVE_ADDED);

		return this.$q.when();
	}

	// Filters immutablePerspectives to determine which perspectives are available, taking into account security
	getPerspectives(): angular.IPromise<IPerspective[]> {
		const promises: angular.IPromise<boolean>[] = [];
		this.immutablePerspectives.forEach((perspective: IPerspective) => {
			let promise: angular.IPromise<boolean>;

			if (perspective.permissions && perspective.permissions.length > 0) {
				promise = this.permissionService.isPermitted([{
					names: perspective.permissions
				}]);
			} else {
				promise = this.$q.when(true);
			}
			promises.push(promise);
		});

		return this.$q.all(promises).then((results: boolean[]) => {
			return this.immutablePerspectives.filter((perspective: IPerspective, index: number) => results[index]);
		});
	}

	hasActivePerspective(): angular.IPromise<boolean> {
		return this.$q.when(Boolean(this.data.activePerspective));
	}

	getActivePerspective(): IPerspective {
		return this.data.activePerspective ? angular.copy(this._findByKey(this.data.activePerspective.key)) : null;
	}

	isEmptyPerspectiveActive(): angular.IPromise<boolean> {
		return this.$q.when((!!this.data.activePerspective && this.data.activePerspective.key === this.NONE_PERSPECTIVE));
	}

	switchTo(key: string): angular.IPromise<void> {
		if (!this._changeActivePerspective(key)) {
			this.waitDialogService.hideWaitModal();
			return this.$q.when();
		}

		this._handleUnloadEvent(key);

		this.waitDialogService.showWaitModal();
		const featuresFromPreviousPerspective: string[] = [];
		if (this.data.previousPerspective) {
			this._fetchAllFeatures(this.data.previousPerspective, featuresFromPreviousPerspective);
		}
		const featuresFromNewPerspective: string[] = [];
		this._fetchAllFeatures(this.data.activePerspective, featuresFromNewPerspective);

		// deactivating any active feature not belonging to either the perspective or one of its nested pespectives
		featuresFromPreviousPerspective.filter((featureKey: string) => {
			return !featuresFromNewPerspective.some((f: string) => {
				return featureKey === f;
			});
		}).forEach((featureKey: string) => {
			this.featureService.disable(featureKey);
		});

		// activating any feature belonging to either the perspective or one of its nested pespectives
		const permissionPromises: angular.IPromise<void>[] = [];
		featuresFromNewPerspective.filter((feature: string) => {
			return !featuresFromPreviousPerspective.some((f: string) => {
				return feature === f;
			});
		}).forEach((featureKey: string) => {
			permissionPromises.push(this._enableOrDisableFeature(featureKey));
		});

		this.$q.all(permissionPromises).then(() => {
			if (this.data.activePerspective.key === this.NONE_PERSPECTIVE) {
				this.waitDialogService.hideWaitModal();
			}
			this.crossFrameEventService.publish(this.EVENT_PERSPECTIVE_CHANGED, this.data.activePerspective.key !== this.NONE_PERSPECTIVE);
		}, (e) => {
			this.$log.error(e);
		});

		return this.$q.when();
	}

	selectDefault(): angular.IPromise<void> {
		return this.getPerspectives().then((perspectives: IPerspective[]) => {
			return this.storageService.getValueFromCookie(this.PERSPECTIVE_COOKIE_NAME, true).then((cookieValue: string) => {
				const perspectiveAvailable = perspectives.find((p: IPerspective) => p.key === cookieValue);
				let defaultPerspective: string;
				let perspective: string;
				if (!perspectiveAvailable) {
					this.$log.warn('Cannot select mode "' + cookieValue + '" It might not exist or is restricted.');
					if (!!cookieValue) {
						this._disableAllFeaturesForPerspective(cookieValue);
					}
					defaultPerspective = this.NONE_PERSPECTIVE;
					perspective = this.NONE_PERSPECTIVE;
				} else {
					defaultPerspective = (cookieValue && this._findByKey(cookieValue)) ? cookieValue : this.NONE_PERSPECTIVE;
					perspective = (this.data.previousPerspective) ? this.data.previousPerspective.key : defaultPerspective;
				}
				if (defaultPerspective !== this.NONE_PERSPECTIVE) {
					this._disableAllFeaturesForPerspective(defaultPerspective);
				}
				return this.switchTo(perspective);
			});
		});
	}

	refreshPerspective(): angular.IPromise<void> {
		return this.getPerspectives().then((result: IPerspective[]) => {
			const activePerspective: IPerspective = this.getActivePerspective();
			if (!activePerspective) {
				this.selectDefault();
			} else {
				this.perspectives = result;
				if (!this._findByKey(activePerspective.key)) {
					this.switchTo(this.NONE_PERSPECTIVE);
				} else {
					const features: string[] = [];
					const permissionPromises: angular.IPromise<void>[] = [];

					this._fetchAllFeatures(activePerspective, features);
					features.forEach((featureKey: string) => {
						permissionPromises.push(this._enableOrDisableFeature(featureKey));
					});

					this.$q.all(permissionPromises).then(() => {
						this.waitDialogService.hideWaitModal();
						this.crossFrameEventService.publish(this.EVENT_PERSPECTIVE_REFRESHED, activePerspective.key !== this.NONE_PERSPECTIVE);
					}, (e) => {
						this.$log.error(e);
					});
				}
			}
		});
	}

	getActivePerspectiveKey(): angular.IPromise<string> {
		const activePerspective: IPerspective = this.getActivePerspective();
		return this.$q.when(activePerspective ? activePerspective.key : null);
	}

	isHotkeyEnabledForActivePerspective(): angular.IPromise<boolean> {
		const activePerspective: IPerspective = this.getActivePerspective();
		return this.$q.when(activePerspective && !activePerspective.isHotkeyDisabled);
	}

	private _addDefaultPerspectives() {
		this.register({
			key: this.NONE_PERSPECTIVE,
			nameI18nKey: 'se.perspective.none.name',
			isHotkeyDisabled: true
		} as IPerspective);

		this.register({
			key: this.ALL_PERSPECTIVE,
			nameI18nKey: 'se.perspective.all.name',
			descriptionI18nKey: 'se.perspective.all.description'
		} as IPerspective);
	}

	private _registerEventHandlers() {
		this.systemEventService.subscribe(this.EVENTS.LOGOUT, this._clearPerspectiveFeatures.bind(this));
		this.crossFrameEventService.subscribe(this.EVENTS.USER_HAS_CHANGED, this._clearPerspectiveFeatures.bind(this));
		// clear the features when navigating to another page than the storefront. this is preventing a flickering of toolbar icons when goign back to storefront on another page.
		this.$rootScope.$on('$routeChangeSuccess', () => {
			if (this.$location.path() !== this.STORE_FRONT_CONTEXT) {
				this._clearPerspectiveFeatures();
			}
		});
	}

	private _validate(configuration: IPerspective) {
		if (this.isBlank(configuration.key)) {
			throw new Error("perspectiveService.configuration.key.error.required");
		}
		if (this.isBlank(configuration.nameI18nKey)) {
			throw new Error("perspectiveService.configuration.nameI18nKey.error.required");
		}
		if ([this.NONE_PERSPECTIVE, this.ALL_PERSPECTIVE].indexOf(configuration.key) === -1 &&
			(this.isBlank(configuration.features) || configuration.features.length === 0)) {
			throw new Error("perspectiveService.configuration.features.error.required");
		}
	}

	private _findByKey(key: string): IPerspective {
		return this.perspectives.find((perspective: IPerspective) => perspective.key === key);
	}

	private _fetchAllFeatures(perspective: IPerspective, holder: string[]) {
		if (!holder) {
			holder = [];
		}

		if (perspective.key === this.ALL_PERSPECTIVE) {
			this.uniqueArray(holder, (this.featureService.getFeatureKeys() || []));
		} else {
			this.uniqueArray(holder, (perspective.features || []));

			(perspective.perspectives || []).forEach((perspectiveKey: string) => {
				const nestedPerspective = this._findByKey(perspectiveKey);
				if (nestedPerspective) {
					this._fetchAllFeatures(nestedPerspective, holder);
				} else {
					this.$log.debug("nested perspective " + perspectiveKey + " was not found in the registry");
				}
			});
		}
	}

	private _enableOrDisableFeature(featureKey: string): angular.IPromise<void> {
		return this.featureService.getFeatureProperty(featureKey, "permissions").then((permissionNames: string[]) => {
			if (!Array.isArray(permissionNames)) {
				permissionNames = [];
			}
			return this.permissionService.isPermitted([{
				names: permissionNames
			}]).then((allowCallback: boolean) => {
				if (allowCallback) {
					this.featureService.enable(featureKey);
				} else {
					this.featureService.disable(featureKey);
				}
			});
		});
	}

	/**
	 * Takes care of sending EVENT_PERSPECTIVE_UNLOADING when perspectives change.
	 *
	 * This function tracks the "key" argument in calls to switchTo(..) function in order to detect when a
	 * perspective is being switched.
	 */
	private _handleUnloadEvent(nextPerspectiveKey: string) {
		if (nextPerspectiveKey !== this.data.previousSwitchToArg && this.data.previousSwitchToArg !== this.INITIAL_SWITCHTO_ARG) {
			this.crossFrameEventService.publish(this.EVENT_PERSPECTIVE_UNLOADING, this.data.previousSwitchToArg);
		}
		this.data.previousSwitchToArg = nextPerspectiveKey;
	}

	private _retrievePerspective(key: string): IPerspective {
		// Validation
		// Change the perspective only if it makes sense.
		if (this.data.activePerspective && this.data.activePerspective.key === key) {
			return null;
		}

		const newPerspective: IPerspective = this._findByKey(key);
		if (!newPerspective) {
			throw new Error("switchTo() - Couldn't find perspective with key " + key);
		}

		return newPerspective;
	}

	private _changeActivePerspective(newPerspectiveKey: string) {
		const newPerspective = this._retrievePerspective(newPerspectiveKey);
		if (newPerspective) {
			this.data.previousPerspective = this.data.activePerspective;
			this.data.activePerspective = newPerspective;
			this.storageService.putValueInCookie(this.PERSPECTIVE_COOKIE_NAME, newPerspective.key, true);
		}
		return newPerspective;
	}

	private _disableAllFeaturesForPerspective(perspectiveName: string) {
		const features: string[] = [];
		this._fetchAllFeatures(this._findByKey(perspectiveName), features);
		features.forEach((featureKey: string) => {
			this.featureService.disable(featureKey);
		});
	}

	private _clearPerspectiveFeatures() {
		// De-activates all current perspective's features (Still leaves the cookie in the system).
		const perspectiveFeatures: string[] = [];
		if (this.data && this.data.activePerspective) {
			this._fetchAllFeatures(this.data.activePerspective, perspectiveFeatures);
		}

		perspectiveFeatures.forEach((feature: string) => {
			this.featureService.disable(feature);
		});
		return this.$q.when();
	}
}

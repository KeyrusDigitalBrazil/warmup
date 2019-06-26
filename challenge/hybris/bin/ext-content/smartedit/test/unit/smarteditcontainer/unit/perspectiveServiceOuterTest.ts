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

import {coreAnnotationsHelper, promiseHelper, IExtensiblePromise} from 'testhelpers';
import {FeatureService, PerspectiveService, StorageService} from 'smarteditcontainer/services';
import {annotationService, CrossFrameEventService, GatewayProxied, IPerspective, SystemEventService} from 'smarteditcommons';

describe('outer perspectiveService', () => {

	const $rootScope = jasmine.createSpyObj<angular.IRootScopeService>('$rootScope', ['$on']);
	const $location = jasmine.createSpyObj<angular.ILocationService>('$location', ['path']);
	const $log: jasmine.SpyObj<angular.ILogService> = jasmine.createSpyObj('log', ['error', 'debug', 'warn']);
	const $q: jasmine.SpyObj<angular.IQService> = promiseHelper.$q();
	const systemEventService: jasmine.SpyObj<SystemEventService> = jasmine.createSpyObj('systemEventService', ['publishAsync', 'subscribe']);
	const waitDialogService: any = jasmine.createSpyObj('waitDialogService', ['showWaitModal', 'hideWaitModal']);
	const featureService: jasmine.SpyObj<FeatureService> = jasmine.createSpyObj('featureService', ['enable', 'disable', 'getFeatureKeys', 'getFeatureProperty']);
	const storageService: jasmine.SpyObj<StorageService> = jasmine.createSpyObj('storageService', ['getValueFromCookie', 'putValueInCookie']);
	const crossFrameEventService: jasmine.SpyObj<CrossFrameEventService> = jasmine.createSpyObj('crossFrameEventService', ['publish', 'subscribe']);
	const permissionService = jasmine.createSpyObj('permissionService', ['isPermitted']);
	const isBlank = jasmine.createSpy('isBlank');
	const uniqueArray = jasmine.createSpy('uniqueArray');

	let perspectiveService: PerspectiveService;

	const EVENTS: any = {
		LOGOUT: 'logout',
		EXPERIENCE_UPDATE: 'clear',
		USER_HAS_CHANGED: 'USER_HAS_CHANGED'
	};

	const NONE_PERSPECTIVE: string = 'se.none';
	const ALL_PERSPECTIVE: string = 'se.all';
	const EVENT_PERSPECTIVE_CHANGED: string = 'EVENT_PERSPECTIVE_CHANGED';
	const EVENT_PERSPECTIVE_ADDED: string = 'EVENT_PERSPECTIVE_ADDED';
	const EVENT_PERSPECTIVE_UNLOADING: string = 'EVENT_PERSPECTIVE_UNLOADING';
	const EVENT_PERSPECTIVE_REFRESHED: string = 'EVENT_PERSPECTIVE_REFRESHED';
	const STORE_FRONT_CONTEXT: string = '/STORE_FRONT_CONTEXT';

	beforeEach(() => {
		systemEventService.subscribe.calls.reset();
		featureService.enable.calls.reset();
		crossFrameEventService.publish.calls.reset();
		crossFrameEventService.subscribe.calls.reset();
		featureService.disable.calls.reset();

		coreAnnotationsHelper.init();
		perspectiveService = new PerspectiveService($rootScope, $location, STORE_FRONT_CONTEXT, $log, $q, isBlank, uniqueArray, systemEventService,
			featureService, waitDialogService, storageService, crossFrameEventService, NONE_PERSPECTIVE,
			ALL_PERSPECTIVE, EVENTS, EVENT_PERSPECTIVE_CHANGED, EVENT_PERSPECTIVE_UNLOADING, EVENT_PERSPECTIVE_ADDED,
			EVENT_PERSPECTIVE_REFRESHED, permissionService);
	});

    /*
     * Default returns values for:
     * featureService
     * permissionService
     */
	beforeEach(() => {
		featureService.getFeatureKeys.and.returnValue([]);
		featureService.getFeatureProperty.and.returnValue($q.when([]));
		permissionService.isPermitted.and.returnValue($q.when(true));
		isBlank.and.callFake(function(value: string) {
			return (typeof value === 'undefined' || value === null || value === "null" || value.toString().trim().length === 0);
		});
		uniqueArray.and.callFake(function(array1: string[], array2: string[]) {
			array2.forEach(function(instance) {
				if (array1.indexOf(instance) === -1) {
					array1.push(instance);
				}
			});
			return array1;
		});
	});

	describe('initialization', () => {

		it('checks GatewayProxied', () => {
			expect(annotationService.getClassAnnotation(PerspectiveService, GatewayProxied))
				.toEqual(['register', 'switchTo', 'hasActivePerspective', 'isEmptyPerspectiveActive', 'selectDefault', 'refreshPerspective', 'getActivePerspectiveKey', 'isHotkeyEnabledForActivePerspective']);
		});

		it('initializes', () => {
			expect(systemEventService.subscribe.calls.count()).toBe(1);
			expect(systemEventService.subscribe).toHaveBeenCalledWith(EVENTS.LOGOUT, jasmine.any(Function));

			expect(crossFrameEventService.subscribe.calls.count()).toBe(1);
			expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(EVENTS.USER_HAS_CHANGED, jasmine.any(Function));
		});

		it('_registerEventHandlers will register event handlers', () => {
			// GIVEN
			spyOn(perspectiveService as any, '_registerEventHandlers').and.callThrough();
			spyOn((perspectiveService as any), '_clearPerspectiveFeatures').and.callThrough();
			systemEventService.subscribe.calls.reset();
			crossFrameEventService.subscribe.calls.reset();
			$rootScope.$on.calls.reset();

			// WHEN
			(perspectiveService as any)._registerEventHandlers();

			// THEN
			expect(systemEventService.subscribe.calls.count()).toBe(1);
			expect(systemEventService.subscribe).toHaveBeenCalledWith(EVENTS.LOGOUT, jasmine.any(Function));

			expect(crossFrameEventService.subscribe.calls.count()).toBe(1);
			expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(EVENTS.USER_HAS_CHANGED, jasmine.any(Function));

			expect($rootScope.$on.calls.count()).toBe(1);
			expect($rootScope.$on).toHaveBeenCalledWith('$routeChangeSuccess', jasmine.any(Function));

			const logoutCallback = systemEventService.subscribe.calls.argsFor(0)[1];
			logoutCallback();

			const clearCallback = crossFrameEventService.subscribe.calls.argsFor(0)[1];
			clearCallback();

			const routeChangeCallback = $rootScope.$on.calls.argsFor(0)[1];
			routeChangeCallback();

			expect((perspectiveService as any)._clearPerspectiveFeatures).toHaveBeenCalled();
		});

		it('GIVEN perspectives are initialized', () => {
			// WHEN
			const perspectives = (getPerspectives() as IExtensiblePromise<IPerspective[]>).value;

			const nonePerspective = findPerspective(perspectives, NONE_PERSPECTIVE);
			const allPerspective = findPerspective(perspectives, ALL_PERSPECTIVE);

			// THEN
			expect(perspectives.length).toBe(2);
			expect(nonePerspective.nameI18nKey).toBe('se.perspective.none.name');
			expect(allPerspective.nameI18nKey).toBe('se.perspective.all.name');
		});

	});

	describe('register', () => {
		it('throws error if key is not provided', () => {
			expect(() => {
				perspectiveService.register({
					nameI18nKey: 'somenameI18nKey',
					features: ['abc', 'xyz']
				} as IPerspective);
			}).toThrowError('perspectiveService.configuration.key.error.required');
		});

		it('throws error if nameI18nKey is not provided', () => {
			expect(() => {
				perspectiveService.register({
					key: 'somekey',
					features: ['abc', 'xyz']
				} as IPerspective);
			}).toThrowError('perspectiveService.configuration.nameI18nKey.error.required');
		});

		it('throws error if features is not provided and perspective is neither se.none nor se.all', () => {
			expect(() => {
				perspectiveService.register({
					key: 'somekey',
					nameI18nKey: 'somenameI18nKey'
				} as IPerspective);
			}).toThrowError('perspectiveService.configuration.features.error.required');
		});

		it('throws error is features is empty', () => {
			expect(() => {
				perspectiveService.register({
					key: 'somekey',
					nameI18nKey: 'somenameI18nKey',
					features: []
				} as IPerspective);
			}).toThrowError('perspectiveService.configuration.features.error.required');
		});

		it('is successful when perspective key is ' + NONE_PERSPECTIVE + ' and features are not provided', () => {
			// GIVEN
			const configuration = {
				key: NONE_PERSPECTIVE,
				nameI18nKey: 'somenameI18nKey'
			} as IPerspective;

			// WHEN
			perspectiveService.register(configuration);
			const perspective = getPerspective(configuration.key);

			// THEN
			expect(perspective).toBeDefined();
		});

		it('is successful when perspective key is ' + ALL_PERSPECTIVE + ' and features are not provided', () => {
			// GIVEN
			const configuration = {
				key: ALL_PERSPECTIVE,
				nameI18nKey: 'somenameI18nKey'
			} as IPerspective;

			// WHEN
			perspectiveService.register(configuration);

			const perspective = getPerspective(configuration.key);

			// THEN
			expect(perspective).toBeTruthy();
		});

		it('GIVEN that perspective configuration has features, THEN register pushes to the features list a Perspective instantiated from configuration and sends a notification', () => {
			// GIVEN
			perspectiveService.register({
				key: 'somekey',
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				features: ['abc', 'xyz'],
				perspectives: ['persp1', 'persp2']
			} as IPerspective);

			expect(systemEventService.publishAsync).toHaveBeenCalledWith(EVENT_PERSPECTIVE_ADDED);

			// WHEN
			const perspectives = (getPerspectives() as IExtensiblePromise<IPerspective[]>).value;

			// THEN Expect to have 2 default + 1 registered
			expect(perspectives.length).toBe(3);
			const perspective = perspectives[2];

			expect(perspective).toEqual(jasmine.objectContaining({
				key: 'somekey',
				nameI18nKey: 'somenameI18nKey',
				features: ['abc', 'xyz'],
				perspectives: ['persp1', 'persp2'],
				descriptionI18nKey: 'somedescriptionI18nKey'
			}));
		});

		it('does not override existing perspectives but merges features and nested perspectives', () => {
			// GIVEN
			perspectiveService.register({
				key: 'somekey',
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				features: ['abc', 'xyz'],
				perspectives: ['persp1', 'persp2']
			});

			perspectiveService.register({
				key: 'somekey3',
				nameI18nKey: 'somenameI18nKey3',
				descriptionI18nKey: 'somedescriptionI18nKey3',
				features: ['zzz'],
				perspectives: ['xxx']
			});

			perspectiveService.register({
				key: 'somekey',
				nameI18nKey: 'somenameI18nKey2',
				descriptionI18nKey: 'somedescriptionI18nKey2',
				features: ['xyz', 'def'],
				perspectives: ['persp2', 'persp3']
			});

			expect(systemEventService.publishAsync).toHaveBeenCalledWith(EVENT_PERSPECTIVE_ADDED);

			// WHEN
			const perspectives = (getPerspectives() as IExtensiblePromise<IPerspective[]>).value;
			const res1 = findPerspective(perspectives, 'somekey');
			const res2 = findPerspective(perspectives, 'somekey3');

			// THEN
			expect(res1).toEqual(jasmine.objectContaining({
				key: 'somekey',
				nameI18nKey: 'somenameI18nKey',
				features: ['abc', 'xyz', 'def'],
				perspectives: ['persp1', 'persp2', 'persp3'],
				descriptionI18nKey: 'somedescriptionI18nKey'
			}));

			expect(res2).toEqual(jasmine.objectContaining({
				key: 'somekey3',
				nameI18nKey: 'somenameI18nKey3',
				features: ['zzz'],
				perspectives: ['xxx'],
				descriptionI18nKey: 'somedescriptionI18nKey3'
			}));

		});

		it('adds a perspective with its permissions and sends an ' + EVENT_PERSPECTIVE_ADDED + ' event', () => {
			// GIVEN
			const configuration = {
				key: 'perspectiveKey',
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				features: ['abc', 'xyz'],
				perspectives: ['persp1'],
				permissions: ['permission1', 'permission2']
			};

			permissionService.isPermitted.and.returnValue($q.when(true));

			// WHEN
			perspectiveService.register(configuration);

			const perspective = getPerspective(configuration.key);

			// THEN
			expect(perspective.permissions).toEqual(configuration.permissions);
			expect(systemEventService.publishAsync).toHaveBeenCalledWith(EVENT_PERSPECTIVE_ADDED);
		});

		it('does not override existing perspectives but merges permissions and sends an ' + EVENT_PERSPECTIVE_ADDED + ' event', () => {
			// GIVEN
			const configuration1 = {
				key: 'perspectiveKey',
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				features: ['abc', 'xyz'],
				perspectives: ['persp1'],
				permissions: ['permission1', 'permission2']
			};

			const configuration2 = {
				key: 'perspectiveKey',
				nameI18nKey: 'somenameI18nKey2',
				descriptionI18nKey: 'somedescriptionI18nKey',
				features: ['abc', 'xyz'],
				perspectives: ['persp1'],
				permissions: ['permission2', 'permission3']
			};

			permissionService.isPermitted.and.returnValue($q.when(true));

			// WHEN
			perspectiveService.register(configuration1);
			perspectiveService.register(configuration2);
			const perspective = getPerspective(configuration1.key);

			// THEN
			expect(perspective.permissions).toEqual(['permission1', 'permission2', 'permission3']);
			expect(systemEventService.publishAsync).toHaveBeenCalledWith(EVENT_PERSPECTIVE_ADDED);
		});
	});

	describe('_fetchAllFeatures', () => {
		it('collects all features of nested perspectives, returns a set of unique features, and log message if a nested perspective does not exist', () => {
			// GIVEN
			const perspective1 = {
				key: 'persp1',
				nameI18nKey: 'persp1',
				features: ['feat1', 'feat2'],
				perspectives: ['persp2']
			};
			const perspective2 = {
				key: 'persp2',
				nameI18nKey: 'persp2',
				features: ['feat2', 'feat3'],
				perspectives: ['persp3', 'persp4']
			};
			const perspective4 = {
				key: 'persp4',
				nameI18nKey: 'persp4',
				features: ['feat3', 'feat4'],
				perspectives: [] as string[]
			};
			perspectiveService.register(perspective1);
			perspectiveService.register(perspective2);
			perspectiveService.register(perspective4);

			const holder: string[] = [];

			// WHEN
			(perspectiveService as any)._fetchAllFeatures(perspective1, holder);

			// THEN
			expect(holder).toEqual(['feat1', 'feat2', 'feat3', 'feat4']);
			expect($log.debug).toHaveBeenCalledWith('nested perspective persp3 was not found in the registry');
		});
	});

	describe('refreshPerspective', () => {
		it('should select the default perspective if there is no active perspective', () => {
			// GIVEN
			spyOn(perspectiveService, 'switchTo').and.returnValue($q.when());
			storageService.getValueFromCookie.and.returnValue($q.when(null));

			expect(perspectiveService.getActivePerspective()).toBeNull();

			// WHEN
			const result = perspectiveService.refreshPerspective();

			// THEN
			expect(storageService.getValueFromCookie).toHaveBeenCalledWith('smartedit-perspectives', true);
			result.then(() => {
				expect(perspectiveService.switchTo).toHaveBeenCalledWith(NONE_PERSPECTIVE);
			});
		});

		it('will publish a perspective refreshed event after a successful refreshPerspective', () => {
			// GIVEN
			const perspective0 = {
				key: 'persp0',
				nameI18nKey: 'persp0',
				features: ['feat0', 'feat2']
			};

			perspectiveService.register(perspective0);
			perspectiveService.switchTo(perspective0.key);

			expect(featureService.enable.calls.count()).toBe(2);
			expect(featureService.enable.calls.argsFor(0)).toEqual(['feat0']);
			expect(featureService.enable.calls.argsFor(1)).toEqual(['feat2']);
			expect(featureService.disable).not.toHaveBeenCalled();

			// WHEN
			perspectiveService.refreshPerspective();

			// THEN
			expect(featureService.enable.calls.count()).toBe(4);
			expect(featureService.enable.calls.argsFor(2)).toEqual(['feat0']);
			expect(featureService.enable.calls.argsFor(3)).toEqual(['feat2']);
			expect(featureService.disable).not.toHaveBeenCalled();

			expect(crossFrameEventService.publish).toHaveBeenCalledWith('EVENT_PERSPECTIVE_REFRESHED', true);
		});

		it('WILL select NONE_PERSPECTIVE if the active perspective is not permitted', () => {
			const PERMISSION1 = 'permission1';

			// GIVEN
			const perspective0 = {
				key: 'persp0',
				nameI18nKey: 'persp0',
				features: ['feat0', 'feat2'],
				permissions: [PERMISSION1]
			};


			perspectiveService.register(perspective0);
			perspectiveService.switchTo(perspective0.key);

			permissionService.isPermitted.and.callFake(() => {
				return $q.when(false);
			});

			featureService.disable.calls.reset();
			featureService.enable.calls.reset();
			featureService.getFeatureProperty.calls.reset();

			// WHEN
			const result = perspectiveService.refreshPerspective();

			result.then(() => {
				expect(featureService.enable).not.toHaveBeenCalled();

				expect(featureService.disable.calls.count()).toBe(2);
				expect(featureService.disable.calls.argsFor(0)).toEqual(['feat0']);
				expect(featureService.disable.calls.argsFor(1)).toEqual(['feat2']);
			});
		});
	});

	describe('switchTo', () => {
		beforeEach(() => {
			storageService.putValueInCookie.calls.reset();
			waitDialogService.showWaitModal.calls.reset();
			waitDialogService.hideWaitModal.calls.reset();
			featureService.enable.calls.reset();
			featureService.disable.calls.reset();
		});

		it('WILL silently do nothing if trying to switch to the same perspecitve as the activate one', () => {
			// GIVEN
			perspectiveService.register({
				key: 'aperspective',
				nameI18nKey: 'perspective.none.name',
				descriptionI18nKey: 'perspective.none.description',
				features: ['fakeFeature']
			});

			// WHEN
			perspectiveService.switchTo('aperspective');
			perspectiveService.switchTo('aperspective');

			// THEN
			expect(storageService.putValueInCookie).toHaveBeenCalledTimes(1);
			expect(waitDialogService.showWaitModal).toHaveBeenCalledTimes(1);
			expect(featureService.enable).toHaveBeenCalledTimes(1);
			expect(featureService.disable).not.toHaveBeenCalled();
		});

		it('WILL throw an error if required perspective is not found', () => {
			// GIVEN
			spyOn((perspectiveService as any), '_findByKey').and.returnValue(null);

			// WHEN/THEN
			expect(() => {
				perspectiveService.switchTo('aperspective');
			}).toThrowError('switchTo() - Couldn\'t find perspective with key aperspective');

			expect((perspectiveService as any)._findByKey).toHaveBeenCalledWith('aperspective');
			expect(storageService.putValueInCookie).not.toHaveBeenCalled();
			expect(waitDialogService.showWaitModal).not.toHaveBeenCalled();
			expect(featureService.enable).not.toHaveBeenCalled();
			expect(featureService.disable).not.toHaveBeenCalled();
		});

		it('NONE_PERSPECTIVE WILL publish a rerender/false and hide the wait modal', () => {
			// WHEN
			perspectiveService.switchTo(NONE_PERSPECTIVE);

			// THEN
			expect(waitDialogService.showWaitModal).toHaveBeenCalled();
			expect(waitDialogService.hideWaitModal).toHaveBeenCalled();
		});


		it('WILL activate all (nested) features of a perspective and notify to rerender', () => {
			// GIVEN
			const perspective0 = {
				key: 'persp0',
				nameI18nKey: 'persp0',
				features: ['feat0']
			};
			const perspective1 = {
				key: 'persp1',
				nameI18nKey: 'persp1',
				features: ['feat1', 'feat2'],
				perspectives: ['persp2']
			};
			const perspective2 = {
				key: 'persp2',
				nameI18nKey: 'persp2',
				features: ['feat2', 'feat3'],
				perspectives: ['persp3', 'persp4']
			};
			const perspective4 = {
				key: 'persp4',
				nameI18nKey: 'persp4',
				features: ['feat3', 'feat4'],
				perspectives: [] as string[]
			};
			perspectiveService.register(perspective0);
			perspectiveService.register(perspective1);
			perspectiveService.register(perspective2);
			perspectiveService.register(perspective4);

			// WHEN
			perspectiveService.switchTo('persp1');

			// THEN
			expect(storageService.putValueInCookie).toHaveBeenCalledWith('smartedit-perspectives', 'persp1', true);
			expect(waitDialogService.showWaitModal).toHaveBeenCalled();
			expect(featureService.enable.calls.count()).toBe(4);
			expect(featureService.enable.calls.argsFor(0)).toEqual(['feat1']);
			expect(featureService.enable.calls.argsFor(1)).toEqual(['feat2']);
			expect(featureService.enable.calls.argsFor(2)).toEqual(['feat3']);
			expect(featureService.enable.calls.argsFor(3)).toEqual(['feat4']);
			expect(waitDialogService.hideWaitModal).not.toHaveBeenCalled();
		});

		it('WILL disable features of previous perspective not present in new one and activate features of new perspective not present in previous one', () => {
			// GIVEN
			const perspective0 = {
				key: 'persp0',
				nameI18nKey: 'persp0',
				features: ['feat0', 'feat2', 'feat3']
			};

			const perspective1 = {
				key: 'persp1',
				nameI18nKey: 'persp1',
				features: ['feat1', 'feat2'],
				perspectives: ['persp2']
			};
			const perspective2 = {
				key: 'persp2',
				nameI18nKey: 'persp2',
				features: ['feat2', 'feat3'],
				perspectives: ['persp3', 'persp4']
			};
			const perspective4 = {
				key: 'persp4',
				nameI18nKey: 'persp4',
				features: ['feat3', 'feat4'],
				perspectives: [] as string[]
			};
			perspectiveService.register(perspective0);
			perspectiveService.register(perspective1);
			perspectiveService.register(perspective2);
			perspectiveService.register(perspective4);

			// WHEN
			perspectiveService.switchTo('persp0');
			perspectiveService.switchTo('persp1');

			// THEN
			expect(storageService.putValueInCookie).toHaveBeenCalledWith('smartedit-perspectives', 'persp1', true);
			expect(featureService.disable.calls.count()).toBe(1);
			expect(featureService.disable).toHaveBeenCalledWith('feat0');
			expect(waitDialogService.showWaitModal).toHaveBeenCalled();
			expect(featureService.enable.calls.count()).toBe(5);
			expect(featureService.enable.calls.argsFor(3)).toEqual(['feat1']);
			expect(featureService.enable.calls.argsFor(4)).toEqual(['feat4']);
			expect(waitDialogService.hideWaitModal).not.toHaveBeenCalled();
		});

		it('WILL throw an error WHEN the perspective is not found', () => {
			expect(
				() => {
					perspectiveService.switchTo('whatever');
				}
			).toThrow();
			expect(waitDialogService.showWaitModal).not.toHaveBeenCalled();
		});

		it('will publish a perspective changed event after a successful switch', () => {
			// GIVEN
			const perspective0 = {
				key: 'persp0',
				nameI18nKey: 'persp0',
				features: ['feat0', 'feat2', 'feat3']
			};

			perspectiveService.register(perspective0);

			// WHEN
			perspectiveService.switchTo(perspective0.key);

			// THEN
			expect(crossFrameEventService.publish).toHaveBeenCalledWith(EVENT_PERSPECTIVE_CHANGED, true);
		});

		it('enable function called when feature has permission', () => {
			// GIVEN
			featureService.getFeatureProperty.and.returnValue($q.when(['se.fake.permission']));
			permissionService.isPermitted.and.returnValue($q.when(true));

			const perspective = {
				key: 'persp',
				nameI18nKey: 'persp',
				features: ['feat']
			};

			// WHEN
			perspectiveService.register(perspective);
			perspectiveService.switchTo(perspective.key);

			// THEN
			expect(featureService.getFeatureProperty).toHaveBeenCalledWith('feat', 'permissions');
			expect(permissionService.isPermitted).toHaveBeenCalledWith([{
				names: ['se.fake.permission']
			}]);
			expect(featureService.enable).toHaveBeenCalled();
		});

		it('when getFeatureProperty for permissions returns undefined, an empty array is used for permission.names', () => {
			// GIVEN
			featureService.getFeatureProperty.and.returnValue($q.when(undefined));

			const perspective = {
				key: 'persp',
				nameI18nKey: 'persp',
				features: ['feat']
			};

			// WHEN
			perspectiveService.register(perspective);
			perspectiveService.switchTo(perspective.key);

			// THEN
			expect(featureService.getFeatureProperty).toHaveBeenCalledWith('feat', 'permissions');
			expect(permissionService.isPermitted).toHaveBeenCalledWith([{
				names: []
			}]);
		});

		it('enable function is not called when feature does not have permission', () => {
			// GIVEN
			featureService.getFeatureProperty.and.returnValue($q.when(['se.fake.permission']));
			permissionService.isPermitted.and.returnValue($q.when(false));

			const perspective = {
				key: 'persp',
				nameI18nKey: 'persp',
				features: ['feat']
			};

			// WHEN
			perspectiveService.register(perspective);
			perspectiveService.switchTo(perspective.key);

			// THEN
			expect(featureService.getFeatureProperty).toHaveBeenCalledWith('feat', 'permissions');
			expect(permissionService.isPermitted).toHaveBeenCalledWith([{
				names: ['se.fake.permission']
			}]);
			expect(featureService.enable).not.toHaveBeenCalled();
		});

		it('getPerspectives should return all perspectives after permissions are removed', () => {
			// GIVEN
			const PERSPECTIVE_NAME_1 = 'persp_1';
			const perspective1 = {
				key: PERSPECTIVE_NAME_1,
				nameI18nKey: PERSPECTIVE_NAME_1,
				features: ['feat_1'],
				permissions: ['permission_1']
			};

			// WHEN
			permissionService.isPermitted.and.returnValue($q.when(false));
			perspectiveService.register(perspective1);
			perspectiveService.switchTo(perspective1.key);
			perspectiveService.refreshPerspective();

			// THEN PERMISSIONS APPLIED
			perspectiveService.getPerspectives().then(function(result: IPerspective[]) {
				expect(result.length).toEqual(2);
			});

			// THEN PERMISSIONS REMOVED
			permissionService.isPermitted.and.returnValue($q.when(true));
			perspectiveService.getPerspectives().then(function(result: IPerspective[]) {
				expect(result.length).toEqual(3);
			});
		});
	});

	describe('selectDefault', () => {
		it('WILL select NONE_PERSPECTIVE if none is found in smartedit-perspectives cookie', () => {
			// GIVEN
			spyOn(perspectiveService, 'switchTo').and.returnValue($q.when());
			spyOn((perspectiveService as any), '_disableAllFeaturesForPerspective');
			storageService.getValueFromCookie.and.returnValue($q.when(null));

			expect(perspectiveService.getActivePerspective()).toBeNull();

			// WHEN
			perspectiveService.selectDefault();

			// THEN
			expect(storageService.getValueFromCookie).toHaveBeenCalledWith('smartedit-perspectives', true);
			expect(perspectiveService.switchTo).toHaveBeenCalledWith(NONE_PERSPECTIVE);
			expect((perspectiveService as any)._disableAllFeaturesForPerspective).not.toHaveBeenCalled();
		});

		it('WILL select the perspective by disabling all features first if a perspective already exists', () => {
			// GIVEN
			spyOn(perspectiveService, 'switchTo').and.returnValue($q.when());
			spyOn((perspectiveService as any), '_disableAllFeaturesForPerspective');
			storageService.getValueFromCookie.and.returnValue($q.when(ALL_PERSPECTIVE));

			expect(perspectiveService.getActivePerspective()).toBeNull();

			// WHEN
			perspectiveService.selectDefault();

			// THEN
			expect(storageService.getValueFromCookie).toHaveBeenCalledWith('smartedit-perspectives', true);
			expect(perspectiveService.switchTo).toHaveBeenCalledWith(ALL_PERSPECTIVE);
			expect((perspectiveService as any)._disableAllFeaturesForPerspective).toHaveBeenCalledWith(ALL_PERSPECTIVE);
		});

		it('WILL select NONE_PERSPECTIVE if perspective stored in cookie has no permission', () => {
			// GIVEN
			const PERMISSION1 = 'permission1';
			const PERSPECTIVE_WITHOUT_PAERMISSIONS = {
				key: 'persp0',
				nameI18nKey: 'persp0',
				features: ['feat0', 'feat2'],
				permissions: [PERMISSION1]
			};
			perspectiveService.register(PERSPECTIVE_WITHOUT_PAERMISSIONS);

			permissionService.isPermitted.and.callFake(() => {
				return $q.when(false);
			});

			spyOn(perspectiveService, 'switchTo').and.returnValue($q.when());
			spyOn((perspectiveService as any), '_disableAllFeaturesForPerspective');
			storageService.getValueFromCookie.and.returnValue($q.when(PERSPECTIVE_WITHOUT_PAERMISSIONS.key));

			// WHEN
			perspectiveService.selectDefault();

			// THEN
			expect(perspectiveService.switchTo).toHaveBeenCalledWith(NONE_PERSPECTIVE);
			expect((perspectiveService as any)._disableAllFeaturesForPerspective).toHaveBeenCalledWith(PERSPECTIVE_WITHOUT_PAERMISSIONS.key);
		});

		it('WILL select perspective stored in cookie if perspective has permission', () => {
			// GIVEN
			const PERMISSION1 = 'permission1';
			const PERSPECTIVE_WITHOUT_PAERMISSIONS = {
				key: 'persp0',
				nameI18nKey: 'persp0',
				features: ['feat0', 'feat2'],
				permissions: [PERMISSION1]
			};
			perspectiveService.register(PERSPECTIVE_WITHOUT_PAERMISSIONS);

			permissionService.isPermitted.and.callFake(() => {
				return $q.when(true);
			});

			spyOn(perspectiveService, 'switchTo').and.returnValue($q.when());
			storageService.getValueFromCookie.and.returnValue($q.when(PERSPECTIVE_WITHOUT_PAERMISSIONS.key));

			// WHEN
			perspectiveService.selectDefault();

			// THEN
			expect(perspectiveService.switchTo).toHaveBeenCalledWith(PERSPECTIVE_WITHOUT_PAERMISSIONS.key);
		});
	});

	describe('getActivePerspective', () => {
		it('returns null when there is no active perspective', () => {
			expect(perspectiveService.getActivePerspective()).toBeNull();
		});

		it('returns active perspective', () => {

			// GIVEN
			perspectiveService.switchTo(NONE_PERSPECTIVE);

			const nonePerspective = getPerspective(NONE_PERSPECTIVE);

			// WHEN
			const activePerspective = perspectiveService.getActivePerspective();

			// THEN
			expect(activePerspective).toEqual(nonePerspective);
		});
	});

	describe('isEmptyPerspectiveActive', () => {
		it('returns true if active perspective is NONE_PERSPECTIVE', () => {
			// GIVEN
			const nonePerspective = {
				key: NONE_PERSPECTIVE,
				nameI18nKey: NONE_PERSPECTIVE,
				features: ['feat0']
			};
			perspectiveService.register(nonePerspective);

			// WHEN
			perspectiveService.switchTo(nonePerspective.key);

			// THEN
			expect((perspectiveService.isEmptyPerspectiveActive() as IExtensiblePromise<boolean>).value).toBe(true);
		});

		it('returns false if active perspective is not NONE_PERSPECTIVE', () => {
			expect((perspectiveService.isEmptyPerspectiveActive() as IExtensiblePromise<boolean>).value).toBe(false);
		});
	});

	describe('getPerspectives', () => {

		it('returns perspectives for which user is granted permission', () => {

			// GIVEN
			const PERMISSION1 = 'permission1';
			const PERMISSION2 = 'permission2';

			permissionService.isPermitted.calls.reset();

			const perspectiveConfig1 = {
				key: 'perspectiveKey1',
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				features: ['abc', 'xyz'],
				permissions: [PERMISSION1]
			};

			const perspectiveConfig2 = {
				key: 'perspectiveKey2',
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				features: ['abc', 'xyz'],
				permissions: [PERMISSION2]
			};

			const perspectiveConfig3 = {
				key: 'perspectiveKey3',
				nameI18nKey: 'somenameI18nKey',
				descriptionI18nKey: 'somedescriptionI18nKey',
				features: ['abc', 'xyz']
			};

			permissionService.isPermitted.and.callFake(function(permissions: [{names: string[]}]) {
				switch (permissions[0].names[0]) {
					case PERMISSION1:
						return $q.when(false);

					case PERMISSION2:
						return $q.when(true);

					default:
						return $q.when(false);
				}
			});

			perspectiveService.register(perspectiveConfig1);
			perspectiveService.register(perspectiveConfig2);
			perspectiveService.register(perspectiveConfig3);

			// WHEN
			const perspectives = (getPerspectives() as IExtensiblePromise<IPerspective[]>).value;

			const perspective1 = findPerspective(perspectives, perspectiveConfig1.key);
			const perspective2 = findPerspective(perspectives, perspectiveConfig2.key);
			const perspective3 = findPerspective(perspectives, perspectiveConfig3.key);

			// THEN
			expect(perspective1).toBeUndefined();
			expect(perspective2).toBeDefined();
			expect(perspective3).toBeDefined();

			expect(permissionService.isPermitted).toHaveBeenCalledTimes(2);
			expect(permissionService.isPermitted.calls.argsFor(0)[0]).toEqual([jasmine.objectContaining({
				names: perspectiveConfig1.permissions
			})]);
			expect(permissionService.isPermitted.calls.argsFor(1)[0]).toEqual([jasmine.objectContaining({
				names: perspectiveConfig2.permissions
			})]);
		});

	});

	describe('getActivePerspectiveKey', function() {
		it(' will return the key of the perspective loaded in the storefront', function() {
			// GIVEN
			const SOME_PERSPECTIVE_KEY = 'some.perspective';
			const somePerspective = {
				key: SOME_PERSPECTIVE_KEY,
				nameI18nKey: SOME_PERSPECTIVE_KEY,
				features: ['feat0']
			};
			perspectiveService.register(somePerspective);

			// WHEN
			perspectiveService.switchTo(SOME_PERSPECTIVE_KEY);

			// THEN
			expect((perspectiveService.getActivePerspectiveKey() as IExtensiblePromise<string>).value).toBe(SOME_PERSPECTIVE_KEY);
		});

		it('returns null if no perspective is loaded', function() {
			expect((perspectiveService.getActivePerspectiveKey() as IExtensiblePromise<string>).value).toBe(null);
		});
	});

    /*
     * This function is used to simply calling perspectiveService.getPerspectives(). It returns
     * a promise.
     */
	function getPerspectives(): angular.IPromise<IPerspective[]> {
		return perspectiveService.getPerspectives().then((result: IPerspective[]) => {
			return result;
		});
	}

	function getPerspective(key: string): IPerspective {
		return findPerspective((getPerspectives() as IExtensiblePromise<IPerspective[]>).value, key);
	}

	function findPerspective(perspectives: IPerspective[], key: string): IPerspective {
		return perspectives.find((perspective) => perspective.key === key);
	}
});
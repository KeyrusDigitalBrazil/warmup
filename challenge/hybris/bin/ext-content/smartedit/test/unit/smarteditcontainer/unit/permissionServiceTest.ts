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
import {annotationService, CrossFrameEventService, GatewayProxied, SystemEventService, TypedMap} from "smarteditcommons";
import * as angular from "angular";
import {PermissionService} from "smarteditcontainer/services";
import {coreAnnotationsHelper, promiseHelper} from 'testhelpers';

/* tslint:disable:no-empty */
describe('smarteditContainer permissionService', function() {

	const DUMMY_RULE_NAME = "dummyRuleName";
	const DUMMY_RULE_NAME1 = DUMMY_RULE_NAME + "1";
	const DUMMY_RULE_NAME2 = DUMMY_RULE_NAME + "2";
	const DUMMY_RULE_NAME3 = DUMMY_RULE_NAME + "3";

	const DUMMY_PERMISSION_NAME = "namepace.dummyPermissionName";
	const DUMMY_PERMISSION_NAME1 = DUMMY_PERMISSION_NAME + "1";
	const DUMMY_PERMISSION_NAME2 = DUMMY_PERMISSION_NAME + "2";
	const DUMMY_PERMISSION_NAME3 = DUMMY_PERMISSION_NAME + "3";
	const INVALID_PERMISSION_NAME = "namepace.invalidPermissionName";

	const DUMMY_PERMISSION_CONTEXT1 = {
		permission: "context1"
	};
	const DUMMY_PERMISSION_CONTEXT2 = {
		permission: "context2"
	};

	const $q: angular.IQService = promiseHelper.$q();
	let $log: angular.ILogService;
	let DEFAULT_RULE_NAME: string;
	let EVENTS: TypedMap<string>;
	let EVENT_PERSPECTIVE_CHANGED: string;
	let systemEventService: jasmine.SpyObj<SystemEventService>;
	let crossFrameEventService: jasmine.SpyObj<CrossFrameEventService>;

	let permissionService: PermissionService;

	beforeEach(function() {
		jasmine.clock().uninstall();
		jasmine.clock().install();
	});

	beforeEach(() => {
		coreAnnotationsHelper.init();

		$log = jasmine.createSpyObj<angular.ILogService>('$log', ['error']);
		DEFAULT_RULE_NAME = 'DEFAULT_RULE_NAME';
		EVENTS = {
			AUTHORIZATION_SUCCESS: 'USER_HAS_CHANGED',
			EXPERIENCE_UPDATE: 'EXPERIENCE_UPDATE',
			PAGE_CHANGE: 'PAGE_CHANGE',
			PERMISSION_CACHE_CLEANED: 'PERMISSION_CACHE_CLEANED'
		};
		EVENT_PERSPECTIVE_CHANGED = 'EVENT_PERSPECTIVE_CHANGED';
		systemEventService = jasmine.createSpyObj<SystemEventService>('systemEventService', ['subscribe']);
		crossFrameEventService = jasmine.createSpyObj<CrossFrameEventService>('crossFrameEventService', ['subscribe', 'publish']);

		PermissionService.resetForTests();
		permissionService = new PermissionService(
			$q,
			$log,
			DEFAULT_RULE_NAME,
			EVENTS,
			EVENT_PERSPECTIVE_CHANGED,
			systemEventService,
			crossFrameEventService,
		);
	});

	afterEach(function() {
		jasmine.clock().uninstall();
	});

	describe('initialization', () => {
		it('checks GatewayProxied', () => {
			expect(annotationService.getClassAnnotation(PermissionService, GatewayProxied))
				.toEqual(["isPermitted",
					"clearCache",
					"registerPermission",
					"unregisterDefaultRule",
					"registerDefaultRule",
					"registerRule",
					"_registerRule",
					"_remoteCallRuleVerify",
					"_registerDefaultRule"]);
		});

		it('checks event subscriptions', () => {
			expect(crossFrameEventService.subscribe.calls.count()).toBe(3);
			expect(systemEventService.subscribe.calls.count()).toBe(1);

			expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(EVENTS.USER_HAS_CHANGED, jasmine.any(Function));
			expect(systemEventService.subscribe).toHaveBeenCalledWith(EVENTS.EXPERIENCE_UPDATE, jasmine.any(Function));
			expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(EVENTS.PAGE_CHANGE, jasmine.any(Function));
			expect(crossFrameEventService.subscribe).toHaveBeenCalledWith(EVENT_PERSPECTIVE_CHANGED, jasmine.any(Function));
		});

		it('(permissionService as any)._registerEventHandlers', () => {
			// GIVEN
			spyOn(permissionService as any, '_registerEventHandlers').and.callThrough();
			spyOn(permissionService, 'clearCache').and.callThrough();
			systemEventService.subscribe.calls.reset();
			crossFrameEventService.subscribe.calls.reset();

			// WHEN
			(permissionService as any)._registerEventHandlers();

			// THEN
			const experienceUpdateCallback = systemEventService.subscribe.calls.argsFor(0)[1];
			experienceUpdateCallback();
			const userHasChangedCallback = crossFrameEventService.subscribe.calls.argsFor(0)[1];
			userHasChangedCallback();
			const pageChangeCallback = crossFrameEventService.subscribe.calls.argsFor(1)[1];
			pageChangeCallback();
			const perspectiveChangeCallback = crossFrameEventService.subscribe.calls.argsFor(2)[1];
			perspectiveChangeCallback();

			expect(permissionService.clearCache).toHaveBeenCalledTimes(4);
		});
	});

	describe('PermissionService.registerRule', function() {
		it('throws exception if Rule.names does not exist', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerRule({
					verify() {}
				} as any);
			}).toThrow(new Error("Rule names must be array"));
		});

		it('throws exception if Rule.names is not of type Array', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerRule({
					names: "not.an.array",
					verify() {}
				} as any);
			}).toThrow(new Error("Rule names must be array"));
		});

		it('throws exception if Rule.names is empty', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerRule({
					names: [],
					verify() {}
				} as any);
			}).toThrow(new Error("Rule requires at least one name"));
		});

		it('throws exception if Rule does not have a verify function', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerRule({
					names: [DUMMY_RULE_NAME]
				} as any);
			}).toThrow(new Error("Rule requires a verify function"));
		});

		it('throws exception if Rule.verify is not a function', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerRule({
					names: [DUMMY_RULE_NAME],
					verify: "notAFunction"
				} as any);
			}).toThrow(new Error("Rule verify must be a function"));
		});

		it('throws exception if Rule exists with same name', function() {

			// Given
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME],
				verify() {}
			} as any);

			// When/Then
			expect(function() {
				permissionService.registerRule({
					names: [DUMMY_RULE_NAME],
					verify() {}
				} as any);
			}).toThrow(new Error("Rule already exists: " + DUMMY_RULE_NAME));
		});

		it('throws exception if trying to register default rule', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerRule({
					names: [DEFAULT_RULE_NAME],
					verify() {}
				} as any);
			}).toThrow(new Error("Register default rule using permissionService.registerDefaultRule()"));
		});

		it('adds Rule to PermissionService', function() {

			// Given
			const dummyRule = {
				names: [DUMMY_RULE_NAME],
				verify() {}
			} as any;

			// When
			permissionService.registerRule(dummyRule);

			// Then
			expect((permissionService as any)._getRule(DUMMY_RULE_NAME)).toEqual(jasmine.objectContaining(dummyRule));
		});
	});

	describe('PermissionService.registerDefaultRule', function() {
		it('adds default rule to PermissionService', function() {

			// Given
			const dummyDefaultRule = {
				names: [DEFAULT_RULE_NAME],
				verify() {}
			} as any;

			// When
			permissionService.registerDefaultRule(dummyDefaultRule);

			// Then
			expect((permissionService as any)._getRule(DEFAULT_RULE_NAME)).toEqual(jasmine.objectContaining(dummyDefaultRule));
		});

		it('throws exception if default rule name is not DEFAULT_RULE_NAME', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerDefaultRule({
					names: [DUMMY_RULE_NAME],
					verify() {}
				} as any);
			}).toThrow(new Error("Default rule name must be DEFAULT_RULE_NAME"));
		});
	});

	describe('PermissionService.unregisterDefaultRule', function() {
		it('removes the default rule', function() {

			// Given
			permissionService.registerDefaultRule({
				names: [DEFAULT_RULE_NAME],
				verify() {}
			} as any);

			// When
			permissionService.unregisterDefaultRule();

			// Then
			expect((permissionService as any)._getRule(DEFAULT_RULE_NAME)).toBeUndefined();
		});
	});

	describe('PermissionService.registerPermission', function() {
		it('throws exception if Permission does not have names parameter', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerPermission({
					rules: [DUMMY_RULE_NAME]
				} as any);
			}).toThrow(new Error("Permission aliases must be an array"));
		});

		it('throws exception if parameter names is not of type Array', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerPermission({
					aliases: "not.an.array",
					rules: []
				} as any);
			}).toThrow(new Error("Permission aliases must be an array"));
		});

		it('throws exception if Permission.names is empty', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerPermission({
					aliases: [],
					rules: [DUMMY_RULE_NAME]
				});
			}).toThrow(new Error("Permission requires at least one alias"));
		});

		it('throws exception if Permission does not have rules parameter', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerPermission({
					aliases: [DUMMY_PERMISSION_NAME]
				} as any);
			}).toThrow(new Error("Permission rules must be an array"));
		});

		it('throws exception if parameter rules is not of type Array', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerPermission({
					aliases: [DUMMY_PERMISSION_NAME],
					rules: "not.an.array"
				} as any);
			}).toThrow(new Error("Permission rules must be an array"));
		});

		it('throws exception if Permission.rules is empty', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerPermission({
					aliases: [DUMMY_PERMISSION_NAME],
					rules: []
				});
			}).toThrow(new Error("Permission requires at least one rule"));
		});

		it('throws exception if permission exists with same name', function() {

			// Given
			const dummyRule = {
				names: [DUMMY_RULE_NAME],
				verify() {}
			} as any;

			permissionService.registerRule(dummyRule);

			const dummyPermission = {
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME]
			};

			permissionService.registerPermission(dummyPermission);

			// When/Then
			expect(function() {
				permissionService.registerPermission(dummyPermission);
			}).toThrow(new Error("Permission already exists: " + DUMMY_PERMISSION_NAME));
		});

		it('throws exception if permission name is not a name space', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerPermission({
					aliases: ["iAmNotNameSpaced"],
					rules: [DUMMY_RULE_NAME]
				});
			}).toThrow(new Error("Permission aliases must be prefixed with namespace and a full stop"));
		});

		it('throws exception if no rule is registered with one of the given rule names', function() {

			// Given/When/Then
			expect(function() {
				permissionService.registerPermission({
					aliases: [DUMMY_PERMISSION_NAME],
					rules: [DUMMY_RULE_NAME]
				});
			}).toThrow(new Error("Permission found but no rule found named: " + DUMMY_RULE_NAME));
		});

		it('adds permission to PermissionService', function() {

			// Given
			const dummyRule = {
				names: [DUMMY_RULE_NAME],
				verify() {}
			} as any;

			permissionService.registerRule(dummyRule);

			const dummyPermission = {
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME]
			};

			// When
			permissionService.registerPermission(dummyPermission);

			// Then
			expect(permissionService.getPermission(DUMMY_PERMISSION_NAME)).toEqual(dummyPermission);
		});
	});

	describe('PermissionService.isPermitted', function() {
		it('throws exception when a permission has no names', function() {

			// Given/Then/When
			expect(function() {
				permissionService.isPermitted([{} as any]);
			}).toThrow(new Error("Requested Permission requires at least one name"));
		});

		it('throws error when a rule is not found', function() {

			// Given
			const dummyRule = {
				names: [DUMMY_RULE_NAME],
				verify() {}
			} as any;

			permissionService.registerRule(dummyRule);

			const dummyPermission = {
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME]
			};

			permissionService.registerPermission(dummyPermission);

			const rule = (permissionService as any)._getRule(DUMMY_RULE_NAME);
			rule.names = [];

			// When/Then
			expect(function() {
				permissionService.isPermitted([{
					names: [DUMMY_PERMISSION_NAME]
				}]);
			}).toThrow(new Error("Permission found but no rule found named: " + DUMMY_RULE_NAME));
		});

		it('returns true when permission exists with verified rule', function() {

			// Given
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME],
				verify() {
					return $q.when(true);
				}
			});

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME]
			});

			// When
			const isPermitted = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME]
			}]);

			// Then
			expect(isPermitted).toBeResolvedWithData(true);
		});

		it('returns false when permission contains no rules', function() {

			// Given
			const dummyRule = {
				names: [DUMMY_RULE_NAME],
				verify() {}
			} as any;

			permissionService.registerRule(dummyRule);

			const dummyPermission = {
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME]
			};

			permissionService.registerPermission(dummyPermission);

			dummyPermission.rules.pop();

			// When/Then
			expect(function() {
				permissionService.isPermitted([{
					names: [DUMMY_PERMISSION_NAME]
				}]);
			}).toThrow(new Error("Permission has no rules"));
		});

		it('returns false when at least one rule is false', function() {

			// Given
			const DUMMY_RULE_NAME2_FALSE = DUMMY_RULE_NAME2 + "_false";

			permissionService.registerRule({
				names: [DUMMY_RULE_NAME1],
				verify() {
					return $q.when(true);
				}
			});
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME2_FALSE],
				verify() {
					return $q.when(false);
				}
			});
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME3],
				verify() {
					return $q.when(true);
				}
			});

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME1, DUMMY_RULE_NAME2_FALSE, DUMMY_RULE_NAME3]
			});

			// When
			const isPermitted = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME]
			}]);

			// Then
			expect(isPermitted).toBeRejectedWithData(false);
		});

		it('returns true when two permissions are requested whose rules are true', function() {

			// Given
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME1],
				verify() {
					return $q.when(true);
				}
			});
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME2],
				verify() {
					return $q.when(true);
				}
			});

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME1],
				rules: [DUMMY_RULE_NAME1]
			});
			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME2],
				rules: [DUMMY_RULE_NAME2]
			});

			// When
			const isPermitted = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME1]
			}, {
				names: [DUMMY_PERMISSION_NAME2]
			}]);

			// Then
			expect(isPermitted).toBeResolvedWithData(true);
		});

		it('returns false when at least one permission requested has a false rule', function() {

			// Given
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME1],
				verify() {
					return $q.when(true);
				}
			});
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME2],
				verify() {
					return $q.when(true);
				}
			});
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME3],
				verify() {
					return $q.when(false);
				}
			});

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME1],
				rules: [DUMMY_RULE_NAME1]
			});
			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME2],
				rules: [DUMMY_RULE_NAME2]
			});
			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME3],
				rules: [DUMMY_RULE_NAME3]
			});

			// When
			const isPermitted = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME1]
			}, {
				names: [DUMMY_PERMISSION_NAME2]
			}, {
				names: [DUMMY_PERMISSION_NAME3]
			}]);

			// Then
			expect(isPermitted).toBeRejectedWithData(false);
		});

		it('returns false when requested permission does not exist and no default rule is registered', function() {
			expect(function() {
				permissionService.isPermitted([{
					names: [INVALID_PERMISSION_NAME]
				}]);
			}).toThrow(new Error("Permission has no rules"));
		});

		it('returns true when requested permission exists and called with permission name as a parameter', function() {

			// Given
			const verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

			const dummyRule = {
				names: [DUMMY_RULE_NAME],
				verify
			};

			permissionService.registerRule(dummyRule);
			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME]
			});

			// When
			const isPermitted = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME]
			}]);

			// Then
			expect(verify).toHaveBeenCalledWith([{
				name: DUMMY_PERMISSION_NAME,
				context: undefined
			}]);
			expect(isPermitted).toBeResolvedWithData(true);
		});

		it('is called with two permissions, referencing 1 rule, then rule.verify is called once with with 2 permission name', function() {

			// Given
			const verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

			const dummyRule = {
				names: [DUMMY_RULE_NAME],
				verify
			};

			permissionService.registerRule(dummyRule);
			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME1, DUMMY_PERMISSION_NAME2],
				rules: [DUMMY_RULE_NAME]
			});

			// When
			const isPermitted = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME1, DUMMY_PERMISSION_NAME2]
			}]);

			// Then
			expect(verify).toHaveBeenCalledWith([{
				name: DUMMY_PERMISSION_NAME1,
				context: undefined
			}, {
				name: DUMMY_PERMISSION_NAME2,
				context: undefined
			}]);
			expect(isPermitted).toBeResolvedWithData(true);
		});

		it('is called with one permission, referencing 1 rule, then rule.verify is called once with with 1 permission name', function() {

			// Given
			const verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

			const dummyRule = {
				names: [DUMMY_RULE_NAME],
				verify
			};

			permissionService.registerRule(dummyRule);
			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME1, DUMMY_PERMISSION_NAME2],
				rules: [DUMMY_RULE_NAME]
			});

			// When
			const isPermitted = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME2]
			}]);

			// Then
			expect(verify).toHaveBeenCalledWith([{
				name: DUMMY_PERMISSION_NAME2,
				context: undefined
			}]);
			expect(isPermitted).toBeResolvedWithData(true);
		});

		it('only calls the same rule.verify with a given permission name once for a given context', function() {

			// Given
			const verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

			const dummyRule = {
				names: [DUMMY_RULE_NAME],
				verify
			};

			permissionService.registerRule(dummyRule);
			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME]
			});

			// When
			permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME],
				context: DUMMY_PERMISSION_CONTEXT1
			}, {
				names: [DUMMY_PERMISSION_NAME],
				context: DUMMY_PERMISSION_CONTEXT1
			}]);

			// Then
			expect(verify).toHaveBeenCalledWith([{
				name: DUMMY_PERMISSION_NAME,
				context: DUMMY_PERMISSION_CONTEXT1
			}]);
		});

		it('calls the same rule.verify with a given permission name more than once if the contexts associated to the permission names are different', function() {

			// Given
			const verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

			const dummyRule = {
				names: [DUMMY_RULE_NAME],
				verify
			};

			permissionService.registerRule(dummyRule);
			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME]
			});

			// When
			permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME],
				context: DUMMY_PERMISSION_CONTEXT1
			}, {
				names: [DUMMY_PERMISSION_NAME],
				context: DUMMY_PERMISSION_CONTEXT2
			}]);

			// Then
			expect(verify).toHaveBeenCalledWith([{
				name: DUMMY_PERMISSION_NAME,
				context: DUMMY_PERMISSION_CONTEXT1
			}, {
				name: DUMMY_PERMISSION_NAME,
				context: DUMMY_PERMISSION_CONTEXT2
			}]);
		});

		it('calls two rules with different permission names but the same context if it is shared', function() {

			// Given
			const verify1 = jasmine.createSpy("verify").and.returnValue($q.when(true));
			const verify2 = jasmine.createSpy("verify").and.returnValue($q.when(true));
			const dummyRule1 = {
				names: [DUMMY_RULE_NAME1],
				verify: verify1
			};

			const dummyRule2 = {
				names: [DUMMY_RULE_NAME2],
				verify: verify2
			};

			permissionService.registerRule(dummyRule1);
			permissionService.registerRule(dummyRule2);

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME1],
				rules: [DUMMY_RULE_NAME1]
			});

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME2],
				rules: [DUMMY_RULE_NAME2]
			});

			// When
			permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME1, DUMMY_PERMISSION_NAME2],
				context: DUMMY_PERMISSION_CONTEXT1
			}]);

			// Then
			expect(verify1).toHaveBeenCalledWith([{
				name: DUMMY_PERMISSION_NAME1,
				context: DUMMY_PERMISSION_CONTEXT1
			}]);
			expect(verify2).toHaveBeenCalledWith([{
				name: DUMMY_PERMISSION_NAME2,
				context: DUMMY_PERMISSION_CONTEXT1
			}]);
		});

		it('returns true when requested permission does not exist but default rule is registered and called with permission name as a parameter', function() {

			// Given
			const verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

			const dummyDefaultRule = {
				names: [DEFAULT_RULE_NAME],
				verify
			};

			permissionService.registerDefaultRule(dummyDefaultRule);

			// When
			const isPermitted = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME]
			}]);

			// Then
			expect(verify).toHaveBeenCalledWith([{
				name: DUMMY_PERMISSION_NAME,
				context: undefined
			}]);
			expect(isPermitted).toBeResolvedWithData(true);
		});

		it('only calls a rule once when multiple permissions use the same rule', function() {

			// Given
			const verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

			const dummyRule = {
				names: [DUMMY_RULE_NAME],
				verify
			};

			permissionService.registerRule(dummyRule);

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME1],
				rules: [DUMMY_RULE_NAME]
			});
			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME2],
				rules: [DUMMY_RULE_NAME]
			});

			// When
			const isPermitted = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME1]
			}, {
				names: [DUMMY_PERMISSION_NAME2]
			}]);

			// Then
			expect(isPermitted).toBeResolvedWithData(true);
			expect(verify).toHaveBeenCalledTimes(1);
		});

		it('only calls a rule once when multiple permissions reference the same rule with different names', function() {

			// Given
			const verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

			const dummyRule = {
				names: [DUMMY_RULE_NAME1, DUMMY_RULE_NAME2],
				verify
			};

			permissionService.registerRule(dummyRule);

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME1],
				rules: [DUMMY_RULE_NAME1]
			});

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME2],
				rules: [DUMMY_RULE_NAME2]
			});

			// When
			permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME1, DUMMY_PERMISSION_NAME2]
			}]);

			// Then
			expect(verify).toHaveBeenCalledTimes(1);
			expect(verify).toHaveBeenCalledWith([{
				name: DUMMY_PERMISSION_NAME1,
				context: undefined
			}, {
				name: DUMMY_PERMISSION_NAME2,
				context: undefined
			}]);
		});

		it('returns false when one asynchronous rule returns false before the other rules return', function() {

			// Given
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME1],
				verify() {
					const deferred = $q.defer();

					setTimeout(() => {
						deferred.resolve(true);
					}, 5000);

					return deferred.promise as any;
				}
			});
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME2],
				verify() {
					const deferred = $q.defer();

					setTimeout(() => {
						deferred.resolve(false);
					}, 1000);

					return deferred.promise as any;
				}
			});

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME1, DUMMY_RULE_NAME2]
			});

			// When
			const isPermitted = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME]
			}]);

			jasmine.clock().tick(2000);

			// Then
			expect(isPermitted).toBeRejectedWithData(false);
		});

		it('returns error message when one asynchronous rule rejects before the other rules return', function() {

			// Given
			const ERROR_MESSAGE = "error.message";

			permissionService.registerRule({
				names: [DUMMY_RULE_NAME1],
				verify() {
					const deferred = $q.defer();

					setTimeout(() => {
						deferred.resolve(true);
					}, 5000);

					return deferred.promise as any;
				}
			});
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME2],
				verify() {
					return $q.reject(ERROR_MESSAGE);
				}
			});

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME1, DUMMY_RULE_NAME2]
			});

			// When
			const isPermitted = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME]
			}]);

			jasmine.clock().tick(2000);

			// Then
			expect(isPermitted).toBeRejectedWithData(false);
		});

		it('caches the result of the first call for the same permission and returns the cached result on the second call', function() {

			// Given
			const verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

			const dummyRule = {
				names: [DUMMY_RULE_NAME],
				verify
			};

			permissionService.registerRule(dummyRule);

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME]
			});

			// When
			const isPermitted = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME]
			}]);

			// $rootScope.$apply();

			const isPermittedCached = permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME]
			}]);

			// Then
			// Non-Cached
			expect(isPermitted).toBeResolvedWithData(true);
			expect(verify).toHaveBeenCalledTimes(1);
			expect(verify).toHaveBeenCalledWith([{
				name: DUMMY_PERMISSION_NAME,
				context: undefined
			}]);

			// Cached
			expect(isPermittedCached).toBeResolvedWithData(true);
		});

		it('console logs error message when a rule is rejected', function() {
			// Given
			const ERROR_MESSAGE = "error.message";

			permissionService.registerRule({
				names: [DUMMY_RULE_NAME1],
				verify() {
					return $q.reject(ERROR_MESSAGE);
				}
			});

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME1]
			});

			// When
			permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME]
			}]);

			// $rootScope.$apply();

			// Then
			expect($log.error).toHaveBeenCalledWith(ERROR_MESSAGE);
		});
	});

	describe('(permissionService as any)._generateCacheKey', function() {
		it('returns a valid cache key', function() {

			// Given
			const permissions = [{
				name: DUMMY_PERMISSION_NAME1,
				context: {
					key: "value"
				}
			}, {
				name: DUMMY_PERMISSION_NAME2,
				context: undefined
			}];

			// When
			const key = (permissionService as any)._generateCacheKey(permissions);

			// Then
			expect(key).toEqual(JSON.stringify(permissions));
		});

		it('returns the same key for a given set of permission names, regardless of the order', function() {

			// Given
			const permission1 = {
				name: DUMMY_PERMISSION_NAME1,
				context: {
					key: "value"
				}
			};

			const permission2 = {
				name: DUMMY_PERMISSION_NAME2,
				context: undefined
			} as any;

			const permissions1 = [permission1, permission2];
			const permissions2 = [permission2, permission1];

			// When
			const key1 = (permissionService as any)._generateCacheKey(permissions1);
			const key2 = (permissionService as any)._generateCacheKey(permissions2);

			// Then
			expect(key1).toEqual(key2);
		});
	});

	describe('PermissionService.hasCachedResult', function() {
		it('returns false when no cache section exists for a given rule', function() {

			// Given
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME],
				verify() {}
			} as any);

			const rule = (permissionService as any)._getRule(DUMMY_RULE_NAME);

			// When
			const hasCachedResult = permissionService.hasCachedResult(rule, null);

			// Then
			expect(hasCachedResult).toBe(false);
		});

		it('returns true when a cache section exists for a given rule and a result exists with the given key', function() {
			// Given
			permissionService.registerRule({
				names: [DUMMY_RULE_NAME],
				verify() {
					return $q.when(true);
				}
			});

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME]
			});

			permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME]
			}]);

			// $rootScope.$apply();

			const key = (permissionService as any)._generateCacheKey([{
				name: DUMMY_PERMISSION_NAME
			}]);

			// When
			const hasCachedResult = permissionService.hasCachedResult(DUMMY_RULE_NAME, key);

			// Then
			expect(hasCachedResult).toBe(true);
		});
	});

	describe('PermissionService.clearCache', function() {
		it('removes all cached values', function() {

			// Given
			const verify1 = jasmine.createSpy("verify").and.returnValue($q.when(true));
			const verify2 = jasmine.createSpy("verify").and.returnValue($q.when(true));

			const dummyRule1 = {
				names: [DUMMY_RULE_NAME1],
				verify: verify1
			};

			const dummyRule2 = {
				names: [DUMMY_RULE_NAME2],
				verify: verify2
			};

			permissionService.registerRule(dummyRule1);
			permissionService.registerRule(dummyRule2);

			permissionService.registerPermission({
				aliases: [DUMMY_PERMISSION_NAME],
				rules: [DUMMY_RULE_NAME1, DUMMY_RULE_NAME2]
			});

			// When
			permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME]
			}]);

			// $rootScope.$apply();

			permissionService.clearCache();

			permissionService.isPermitted([{
				names: [DUMMY_PERMISSION_NAME]
			}]);

			// $rootScope.$apply();

			// Then
			expect(crossFrameEventService.publish).toHaveBeenCalledWith(EVENTS.PERMISSION_CACHE_CLEANED);
			expect(verify1).toHaveBeenCalledTimes(2);
			expect(verify2).toHaveBeenCalledTimes(2);
		});
	});
});

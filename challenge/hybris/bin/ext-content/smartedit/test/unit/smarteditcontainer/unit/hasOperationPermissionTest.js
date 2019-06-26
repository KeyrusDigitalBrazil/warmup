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
describe('hasOperationPermission', function() {
    var GRANTED_PERMISSION = 'GRANTED_PERMISSION';
    var DENIED_PERMISSION = 'DENIED_PERMISSION';
    var FAILED_OPERATION = 'FAILED_OPERATION';
    var EVENTS = {
        PERMISSION_CACHE_CLEANED: 'PERMISSION_CACHE_CLEANED'
    };

    var INVALID_PERMISSIONS_INPUT = {
        names: ["invalid"]
    };

    var GRANTED_PERMISSIONS_ARRAY = [{
        names: [GRANTED_PERMISSION]
    }];

    var DENIED_PERMISSION_ARRAY = [{
        names: [DENIED_PERMISSION]
    }];

    var FAILED_OPERATION_ARRAY = [{
        names: [FAILED_OPERATION]
    }];

    var parentScope, scope, element, ctrl;
    var permissionService, systemEventService;
    var $q;
    var $log;

    beforeEach(angular.mock.module('coretemplates'));
    beforeEach(angular.mock.module('hasOperationPermissionModule'));

    //mock subscribe of systemEventService
    beforeEach(angular.mock.module('smarteditCommonsModule', function($provide) {

        $log = jasmine.createSpyObj('$log', ['error']);
        $provide.value('$log', $log);

        systemEventService = jasmine.createSpyObj('systemEventService', ['subscribe']);

        systemEventService.subscribe.and.callFake(function() {
            return function() {};
        });

        $provide.value('systemEventService', systemEventService);
        $provide.value('EVENTS', EVENTS);

        $provide.service('permissionService', function($q) {
            this.isPermitted = jasmine.createSpy('isPermitted');
            this.isPermitted.and.callFake(function(operation) {
                if (JSON.stringify(operation) === JSON.stringify(FAILED_OPERATION_ARRAY)) {
                    return $q.reject();
                }
                return $q.when(JSON.stringify(operation) === JSON.stringify(GRANTED_PERMISSIONS_ARRAY));
            });
        });
    }));

    beforeEach(inject(function(yjQuery, $compile, $rootScope, _$q_, _permissionService_) {
        $q = _$q_;
        permissionService = _permissionService_;

        parentScope = $rootScope.$new();
        yjQuery.extend(parentScope, {
            permissionKey: GRANTED_PERMISSION
        });

        element = $compile('<div ' +
            'data-has-operation-permission="permissionKey">' +
            '<span>Permission Granted</span>' +
            '</div>')(parentScope);
        parentScope.$digest();

        scope = element.isolateScope();
        ctrl = scope.ctrl;

    }));

    it('should call the permission service when first instantiated', function() {
        expect(permissionService.isPermitted).toHaveBeenCalledWith(GRANTED_PERMISSIONS_ARRAY);
    });

    it('should transclude the nested element if the given operation is permitted by the user', function() {
        expect(element.find('span')).toExist();
        expect(element.text()).toContain('Permission Granted');
    });

    it('should attach an event listener for the authorization success event', function() {
        expect(systemEventService.subscribe).toHaveBeenCalledWith(EVENTS.PERMISSION_CACHE_CLEANED, jasmine.any(Function));
    });

    it('should call the authorization service when the operation is changed', function() {
        parentScope.permissionKey = DENIED_PERMISSION;
        parentScope.$digest();

        expect(permissionService.isPermitted.calls.mostRecent().args[0]).toEqual(DENIED_PERMISSION_ARRAY);
    });

    it('should remove the nested element from the DOM if the given operation is changed a denied permission', function() {
        parentScope.permissionKey = DENIED_PERMISSION;
        parentScope.$digest();

        expect(element.find('span')).not.toExist();
        expect(element.text()).not.toContain('Permission Granted');
    });

    it('should throw an error if invalid permissions input is provided', function() {
        parentScope.permissionKey = INVALID_PERMISSIONS_INPUT;
        parentScope.$digest();
        expect($log.error.calls.argsFor(0)[0]).toEqual(new Error("Permission should be string or an array of objects"));
    });

});

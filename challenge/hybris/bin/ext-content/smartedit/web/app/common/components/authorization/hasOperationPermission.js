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
/**
 * @ngdoc overview
 * @name hasOperationPermissionModule
 * @description
 * This module provides a directive used to determine if the current user has permission to perform the action defined
 * by a given permission key(s), and removes/adds elements from the DOM accordingly.
 */
angular.module('hasOperationPermissionModule', ['smarteditServicesModule', 'yLoDashModule'])

    .controller('hasOperationPermissionController', function($log, EVENTS, systemEventService, permissionService, lodash) {

        this.refreshIsPermissionGranted = function() {
            permissionService.isPermitted(this._validateAndPreparePermissions(this.hasOperationPermission)).then(function(isPermissionGranted) {
                this.isPermissionGranted = isPermissionGranted;
            }.bind(this), function(error) {
                $log.error('Failed to retrieve authorization', error);
                this.isPermissionGranted = false;
            }.bind(this));
        };

        this._validateAndPreparePermissions = function(permissions) {
            if (typeof permissions !== 'string' && !Array.isArray(permissions)) {
                throw new Error("Permission should be string or an array of objects");
            }

            var preparedPermissions = lodash.cloneDeep(permissions);
            if (typeof permissions === 'string') {
                preparedPermissions = [{
                    names: permissions.split(",")
                }];
            }
            return preparedPermissions;
        };

        this.$onInit = function() {
            // NOTE: Refreshing permission checking should only be done after permissions have been cleaned 
            // (PERMISSION_CACHE_CLEANED). If this is done as soon after user is changed (USER_CHANGED) then there's a race 
            // condition between when the cache is cleaned and when this permission checking is executed. 
            this._unregisterHandler = systemEventService.subscribe(EVENTS.PERMISSION_CACHE_CLEANED, this.refreshIsPermissionGranted.bind(this));
        };

        this.$onChanges = function(changesObject) {
            if (changesObject.hasOperationPermission) {
                this.isPermissionGranted = false;
                this.refreshIsPermissionGranted();
            }
        };

        this.$onDestroy = function() {
            this._unregisterHandler();
        };

    })

    /**
     * @ngdoc directive
     * @name hasOperationPermissionModule.directive:hasOperationPermission
     * @scope
     * @restrict A
     * @element ANY
     *
     * @description
     * Authorization HTML mark-up that will remove elements from the DOM if the user does not have authorization defined
     * by the input parameter permission keys. This directive makes use of the {@link smarteditCommonsModule.service:PermissionServiceInterface IPermissionService}
     * permissionService} service to validate if the current user has access to the given permission set or not.
     *
     * It takes a comma-separated list of permission names or an array of permission name objects structured as follows:
     *
     * {
     *     names: ["permission1", "permission2"],
     *     context: {
     *         data: "with the context property, extra data can be included to check a permission when the Rule.verify function is called"
     *     }
     * }
     *
     *
     * @param {< String || Object[]} has-operation-permission A comma-separated list of permission names or an array of permission name objects.
     */
    .directive('hasOperationPermission', function() {
        return {
            transclude: true,
            restrict: 'A',
            templateUrl: 'hasOperationPermissionTemplate.html',
            controller: 'hasOperationPermissionController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                hasOperationPermission: '<'
            }
        };
    });

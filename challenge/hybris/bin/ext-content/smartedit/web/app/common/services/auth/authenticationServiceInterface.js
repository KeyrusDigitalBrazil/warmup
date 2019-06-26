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
 * @name authenticationModule
 *
 * @description
 * # The authenticationModule
 *
 * The authentication module provides a service to authenticate and logout from SmartEdit.
 * It also allows the management of entry points used to authenticate the different resources in the application.
 *
 */
angular.module('authenticationInterfaceModule', [])
    /**
     * @ngdoc service
     * @name authenticationModule.object:DEFAULT_AUTH_MAP
     *
     * @description
     * The default authentication map contains the entry points to use before an authentication map
     * can be loaded from the configuration.
     */
    .factory('DEFAULT_AUTH_MAP', function(I18N_ROOT_RESOURCE_URI, DEFAULT_AUTHENTICATION_ENTRY_POINT) {
        var DEFAULT_ENTRY_POINT_MATCHER = "^(?!" + I18N_ROOT_RESOURCE_URI + '\/.*$).*$';
        var DEFAULT_AUTH_MAP = {};
        DEFAULT_AUTH_MAP[DEFAULT_ENTRY_POINT_MATCHER] = DEFAULT_AUTHENTICATION_ENTRY_POINT;

        return DEFAULT_AUTH_MAP;
    })
    /**
     * @ngdoc service
     * @name authenticationModule.object:DEFAULT_CREDENTIALS_MAP
     *
     * @description
     * The default credentials map contains the credentials to use before an authentication map
     * can be loaded from the configuration.
     */
    .factory('DEFAULT_CREDENTIALS_MAP', function(DEFAULT_AUTHENTICATION_ENTRY_POINT, DEFAULT_AUTHENTICATION_CLIENT_ID) {
        var DEFAULT_CREDENTIALS_MAP = {};
        DEFAULT_CREDENTIALS_MAP[DEFAULT_AUTHENTICATION_ENTRY_POINT] = {
            client_id: DEFAULT_AUTHENTICATION_CLIENT_ID
        };
        return DEFAULT_CREDENTIALS_MAP;
    })
    /**
     * @ngdoc service
     * @name authenticationModule.service:authenticationService
     *
     * @description
     * The authenticationService is used to authenticate and logout from SmartEdit.
     * It also allows the management of entry points used to authenticate the different resources in the application.
     *
     */
    .factory('AuthenticationServiceInterface', function() {


        var AuthenticationServiceInterface = function() {

        };


        /**
         * @ngdoc method
         * @name authenticationModule.service:authenticationService#authenticate
         * @methodOf authenticationModule.service:authenticationService
         *
         * @description
         * Authenticates the current SmartEdit user against the entry point assigned to the requested resource. If no
         * suitable entry point is found, the resource will be authenticated against the
         * {@link resourceLocationsModule.object:DEFAULT_AUTHENTICATION_ENTRY_POINT DEFAULT_AUTHENTICATION_ENTRY_POINT}
         *
         * @param {String} resource The URI identifying the resource to access.
         * @returns {Promise} A promise that resolves if the authentication is successful.
         */
        AuthenticationServiceInterface.prototype.authenticate = function() {};


        /**
         * @ngdoc method
         * @name authenticationModule.service:authenticationService#logout
         * @methodOf authenticationModule.service:authenticationService
         *
         * @description
         * The logout method removes all stored authentication tokens and redirects to the
         * landing page.
         *
         */
        AuthenticationServiceInterface.prototype.logout = function() {};


        AuthenticationServiceInterface.prototype.isReAuthInProgress = function() {};


        /**
         * @ngdoc method
         * @name authenticationModule.service:authenticationService#setReAuthInProgress
         * @methodOf authenticationModule.service:authenticationService
         *
         * @description
         * Used to indicate that the user is currently within a re-authentication flow for the given entry point.
         * This flow is entered by default through authentication token expiry.
         *
         * @param {String} entryPoint The entry point which the user must be re-authenticated against.
         *
         */
        AuthenticationServiceInterface.prototype.setReAuthInProgress = function() {};


        /**
         * @ngdoc method
         * @name authenticationModule.service:authenticationService#filterEntryPoints
         * @methodOf authenticationModule.service:authenticationService
         * 
         * @description
         * Will retrieve all relevant authentication entry points for a given resource.
         * A relevant entry point is an entry value of the authenticationMap found in {@link smarteditServicesModule.sharedDataService sharedDataService}.The key used in that map is a regular expression matching the resource.
         * When no entry point is found, the method returns the {@link resourceLocationsModule.object:DEFAULT_AUTHENTICATION_ENTRY_POINT DEFAULT_AUTHENTICATION_ENTRY_POINT}
         * @param {string} resource The URL for which a relevant authentication entry point must be found.
         */
        AuthenticationServiceInterface.prototype.filterEntryPoints = function() {};


        /**
         * @ngdoc method
         * @name authenticationModule.service:authenticationService##isAuthEntryPoint
         * @methodOf authenticationModule.service:authenticationService
         *
         * @description
         * Indicates if the resource URI provided is one of the registered authentication entry points.
         *
         * @param {String} resource The URI to compare
         * @returns {Boolean} Flag that will be true if the resource URI provided is an authentication entry point.
         */
        AuthenticationServiceInterface.prototype.isAuthEntryPoint = function() {};


        /**
         * @ngdoc method
         * @name authenticationModule.service:authenticationService##isAuthenticated
         * @methodOf authenticationModule.service:authenticationService
         *
         * @description
         * Indicates if the resource URI provided maps to a registered authentication entry point and the associated entry point has an authentication token.
         *
         * @param {String} resource The URI to compare
         * @returns {Boolean} Flag that will be true if the resource URI provided maps to an authentication entry point which has an authentication token.
         */
        AuthenticationServiceInterface.prototype.isAuthenticated = function() {};

        return AuthenticationServiceInterface;
    });

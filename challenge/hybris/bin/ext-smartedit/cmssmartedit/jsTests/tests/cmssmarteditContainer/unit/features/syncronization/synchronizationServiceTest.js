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
describe("sync service  - unit test", function() {
    var synchronizationService, $q, $rootScope, crossFrameEventService;
    var mockTimer, authenticationService, timerService;

    var theCatalog = {
        catalogId: "catalog",
        sourceCatalogVersion: 'sourceVersion',
        targetCatalogVersion: 'targetVersion'
    };
    var theCatalogGetStatus = {
        "date": "2016-02-12T16:08:29+0000",
        "syncStatus": "FINISHED"
    };
    var theCatalogUpdateStatus = {
        "date": "2016-02-12T17:09:29+0000",
        "syncStatus": "FINISHED"
    };

    var secondCatalog = {
        catalogId: 'second catalog'
    };
    var secondCatalogGetStatus = {
        "date": "2016-04-01T12:00:00+0000",
        "syncStatus": "PENDING"
    };

    var theAbortedCatalog = {
        catalogId: "abortedCatalog",
        sourceCatalogVersion: 'sourceVersion',
        targetCatalogVersion: 'targetVersion'
    };

    var theAbortedCatalogStatus = {
        "date": "2016-02-12T16:08:29+0000",
        "syncStatus": "ABORTED"
    };

    var theAbortedCatalogUpdateStatus = {
        "date": "2016-02-12T17:09:29+0000",
        "syncStatus": "ABORTED"
    };

    beforeEach(angular.mock.module('configModule'));
    beforeEach(angular.mock.module('pascalprecht.translate'));

    beforeEach(function() {
        window.addModulesIfNotDeclared([
            'timerModule',
            'alertServiceModule',
            'authenticationModule',
            'confirmationModalServiceModule'
        ]);
    });

    beforeEach(angular.mock.module("synchronizationServiceModule", function($provide) {
        $provide.value('operationContextService', {
            register: angular.noop
        });
        $provide.value('OPERATION_CONTEXT', {
            CMS: 'CMS'
        });

        authenticationService = jasmine.createSpyObj('authenticationService', ['isAuthenticated']);
        authenticationService.isAuthenticated.and.callFake(function(url) {
            var test = "/cmswebservices";
            if (url === test) {
                return $q.when(true);
            } else {
                return $q.when(false);
            }
        });
        $provide.value('authenticationService', authenticationService);

        var restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        var restServiceForSync = jasmine.createSpyObj('searchRestService', ['get', 'update']);

        restServiceFactory.get.and.callFake(function() {
            return restServiceForSync;
        });

        restServiceForSync.update.and.callFake(function(value) {
            if (value.catalog === "catalog") {
                return $q.when(theCatalogUpdateStatus);
            } else if (value.catalog === "abortedCatalog") {
                return $q.when(theAbortedCatalogUpdateStatus);
            }
        });

        restServiceForSync.get.and.callFake(function(value) {
            if (value.catalog === "catalog") {
                return $q.when(theCatalogGetStatus);
            } else if (value.catalog === "second catalog") {
                return $q.when(secondCatalogGetStatus);
            } else if (value.catalog === "abortedCatalog") {
                return $q.when(theAbortedCatalogStatus);
            }
        });

        $provide.value('restServiceFactory', restServiceFactory);

        var alertService = jasmine.createSpyObj('alertService', ['pushAlerts']);
        $provide.value('alertService', alertService);

        mockTimer = jasmine.createSpyObj('Timer', ['start', 'restart', 'stop']);
        timerService = jasmine.createSpyObj('timerService', ['createTimer']);
        timerService.createTimer.and.returnValue(mockTimer);

        $provide.value('timerService', timerService);

        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['publish']);

        $provide.value('crossFrameEventService', crossFrameEventService);
    }));

    beforeEach(inject(function(_$rootScope_, _synchronizationService_, _$q_) {
        $rootScope = _$rootScope_;
        $q = _$q_;
        synchronizationService = _synchronizationService_;
    }));


    it('should update sync status ', function() {

        var result = synchronizationService.updateCatalogSync(theCatalog);

        $rootScope.$digest();

        result.then(
            function(response) {
                expect(response.date).toEqual("2016-02-12T17:09:29+0000");
                expect(response.syncStatus).toEqual("FINISHED");
            }
        );
        $rootScope.$digest();
    });


    it('should get catalog sync status', function() {

        var result = synchronizationService.getCatalogSyncStatus(theCatalog);

        $rootScope.$digest();

        result.then(
            function(response) {
                expect(response.date).toEqual("2016-02-12T16:08:29+0000");
                expect(response.syncStatus).toEqual("FINISHED");
            }
        );
        $rootScope.$digest();
    });

    it('should call "get synchronization status" after interval.', function() {

        var callback = jasmine.createSpy('callback');

        synchronizationService.startAutoGetSyncData(theCatalog, callback);
        expect(timerService.createTimer).toHaveBeenCalledWith(jasmine.any(Function), jasmine.any(Number));

        var timerFn = timerService.createTimer.calls.argsFor(0)[0];
        timerFn();
        $rootScope.$digest();
        expect(callback.calls.count()).toBe(1);

        timerFn();
        $rootScope.$digest();
        expect(callback.calls.count()).toBe(2);
    });

    it('stopAutoGetSyncData should stop the timer', function() {
        // GIVEN
        var callback = jasmine.createSpy('callback');
        synchronizationService.startAutoGetSyncData(theCatalog, callback);
        expect(mockTimer.stop).not.toHaveBeenCalled();

        // WHEN
        synchronizationService.stopAutoGetSyncData(theCatalog);

        // THEN
        expect(mockTimer.stop).toHaveBeenCalled();
    });

    it('should stop calling "get sync update" on authentication failure', function() {
        // GIVEN
        var callback = jasmine.createSpy('callback');

        authenticationService.isAuthenticated.and.callFake(function() {
            return $q.when(false);
        });
        spyOn(synchronizationService, 'stopAutoGetSyncData').and.callThrough();

        synchronizationService.startAutoGetSyncData(secondCatalog, callback);
        var timerFn = timerService.createTimer.calls.argsFor(0)[0];
        timerFn();
        $rootScope.$digest();

        expect(synchronizationService.stopAutoGetSyncData).toHaveBeenCalled();
    });

    it('should continue calling "get sync update" on authentication success', function() {
        var callback = jasmine.createSpy('callback').and.returnValue($q.reject());

        spyOn(synchronizationService, 'stopAutoGetSyncData').and.callThrough();
        spyOn(synchronizationService, 'getCatalogSyncStatus').and.callThrough();

        synchronizationService.startAutoGetSyncData(theCatalog, callback);
        var timerFn = timerService.createTimer.calls.argsFor(0)[0];
        timerFn();
        $rootScope.$digest();

        expect(synchronizationService.stopAutoGetSyncData).not.toHaveBeenCalled();
        expect(synchronizationService.getCatalogSyncStatus).toHaveBeenCalled();
    });

    it('updateCatalogSync should mark that synchronization is in progress', function() {
        // GIVEN
        var jobKey = "JOB_KEY";
        spyOn(synchronizationService, '_getJobKey').and.returnValue(jobKey);
        spyOn(synchronizationService, 'addCatalogSyncRequest');

        // WHEN
        synchronizationService.updateCatalogSync(theCatalog);

        // THEN
        expect(synchronizationService.addCatalogSyncRequest).toHaveBeenCalledWith(jobKey);
    });

    it('GIVEN synchronization is in progress WHEN stopAutoGetSyncData is called THEN should only mark the timer as discardWhenNextSynced = true and the timer should not be stopped', function() {
        // GIVEN
        var callback = jasmine.createSpy('callback');
        expect(mockTimer.discardWhenNextSynced).toEqual(undefined);
        synchronizationService.updateCatalogSync(theCatalog);
        synchronizationService.startAutoGetSyncData(theCatalog, callback);

        // WHEN
        $rootScope.$digest();
        synchronizationService.stopAutoGetSyncData(theCatalog);

        // THEN
        expect(mockTimer.stop).not.toHaveBeenCalled();
        expect(mockTimer.discardWhenNextSynced).toEqual(true);
    });

    it('GIVEN synchronization is in progress WHEN synchronization is finished THEN it sends SYNCHRONIZATION_EVENT.CATALOG_SYNCHRONIZED event AND removes job from "synchronization requested" array', function() {
        // GIVEN
        var callback = jasmine.createSpy('callback').and.returnValue($q.resolve());
        spyOn(synchronizationService, 'removeCatalogSyncRequest');
        synchronizationService.updateCatalogSync(theCatalog);
        synchronizationService.startAutoGetSyncData(theCatalog, callback);

        // WHEN
        var timerFn = timerService.createTimer.calls.argsFor(0)[0];
        timerFn();
        $rootScope.$digest();

        // THEN
        expect(crossFrameEventService.publish).toHaveBeenCalledWith("CATALOG_SYNCHRONIZED_EVENT", theCatalog);
        expect(synchronizationService.removeCatalogSyncRequest).toHaveBeenCalled();
    });

    it('GIVEN synchronization is in progress WHEN synchronization is finished AND the job should be discarded THEN the timer is stopped AND the job is removed from "synchronization requested" array', function() {
        // GIVEN
        var callback = jasmine.createSpy('callback').and.returnValue($q.resolve());
        spyOn(synchronizationService, 'removeCatalogSyncRequest');
        synchronizationService.updateCatalogSync(theCatalog);
        synchronizationService.startAutoGetSyncData(theCatalog, callback);
        synchronizationService.stopAutoGetSyncData(theCatalog);

        // WHEN
        var timerFn = timerService.createTimer.calls.argsFor(0)[0];
        timerFn();
        $rootScope.$digest();

        // THEN
        expect(mockTimer.stop).toHaveBeenCalled();
        expect(synchronizationService.removeCatalogSyncRequest).toHaveBeenCalled();
    });

    it('GIVEN synchronization is in progress WHEN synchronization is aborted THEN the timer is stopped AND the job is removed from "synchronization requested" array', function() {
        // GIVEN
        var callback = jasmine.createSpy('callback').and.returnValue($q.resolve());
        spyOn(synchronizationService, 'removeCatalogSyncRequest');
        synchronizationService.updateCatalogSync(theAbortedCatalog);
        synchronizationService.startAutoGetSyncData(theAbortedCatalog, callback);
        synchronizationService.stopAutoGetSyncData(theAbortedCatalog);

        // WHEN
        var timerFn = timerService.createTimer.calls.argsFor(0)[0];
        timerFn();
        $rootScope.$digest();

        // THEN
        expect(mockTimer.stop).toHaveBeenCalled();
        expect(synchronizationService.removeCatalogSyncRequest).toHaveBeenCalled();
    });
});

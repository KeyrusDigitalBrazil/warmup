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
describe('administrationModule', function() {

    var configurationService, $q, $rootScope;
    var generateForm = function() {
        this.$dirty = true;
        this.$invalid = false;
        this.$valid = true;
        this.$pristine = true;
        this.$setPristine = function() {
            //empty
        };
    };

    beforeEach(angular.mock.module('administrationModule'));

    beforeEach(inject(function(_configurationService_, _$q_, _$rootScope_) {
        $q = _$q_;
        $rootScope = _$rootScope_;
        configurationService = _configurationService_;
        configurationService.editorCRUDService = jasmine.createSpyObj('editorCRUDService', ['update', 'remove', 'save']);
        configurationService.editorCRUDService.remove.and.callFake(function() {
            return $q.when(true);
        });
    }));

    describe('Submit Function - ', function() {
        it('should return a rejected promise when the form is not dirty', function() {
            var newForm = new generateForm();
            newForm.$dirty = false;
            expect(configurationService.submit(newForm)).toBeRejected();
        });
        it('should return a resolved promise for a success save on a simple form', function() {
            var newForm = new generateForm();
            configurationService.configuration = [{
                toDelete: false,
                isNew: true
            }];
            expect(configurationService.submit(newForm)).toBeRejected();
        });
        it('should return a resolved promise when remove is successful', function() {
            var newForm = new generateForm();
            //config for remove
            configurationService.configuration = [{
                toDelete: true
            }];
            expect(configurationService.submit(newForm)).toBeResolved();
        });
        it('should return a rejected promise when saving the form fails', function() {
            var newForm = new generateForm();
            //config for save
            configurationService.configuration = [{
                toDelete: false,
                isNew: true
            }];
            expect(configurationService.submit(newForm)).toBeRejected();
        });
        it('should return a rejected promise when updating the form fails', function() {
            var newForm = new generateForm();
            //config for update
            configurationService.configuration = [{
                toDelete: false,
                isNew: false
            }];
            expect(configurationService.submit(newForm)).toBeRejected();
        });
    });
});

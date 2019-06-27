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
/* jshint unused:false, undef:false */
describe('clonePageWizardService', function() {

    var $rootScope, clonePageWizardService;
    var mocks;
    var uriContext = {
        a: 'b'
    };

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('clonePageWizardServiceModule')
            .mock('catalogService', 'retrieveUriContext').and.returnResolvedPromise(uriContext)
            .mock('pageFacade', 'retrievePageUriContext').and.returnResolvedPromise(uriContext)
            .mock('modalWizard', 'open').and.returnValue('')
            .service('clonePageWizardService');

        clonePageWizardService = harness.service;
        mocks = harness.mocks;
        $rootScope = harness.injected.$rootScope;
    });

    describe('clonePageWizardService', function() {
        it('GIVEN pageData is sent to the method THEN will delegate to the modal wizard a valid basePageUid', function() {
            clonePageWizardService.openClonePageWizard({
                uuid: 'some uuid'
            });
            $rootScope.$digest();
            expect(mocks.modalWizard.open).toHaveBeenCalledWith({
                controller: 'clonePageWizardController',
                controllerAs: 'clonePageWizardCtrl',
                properties: {
                    uriContext: uriContext,
                    basePageUUID: 'some uuid'
                }
            });
        });

        it('GIVEN no pageData sent to the method THEN will delegate to the modal wizard with a undefined basePageUid', function() {
            clonePageWizardService.openClonePageWizard();
            $rootScope.$digest();
            expect(mocks.modalWizard.open).toHaveBeenCalledWith({
                controller: 'clonePageWizardController',
                controllerAs: 'clonePageWizardCtrl',
                properties: {
                    uriContext: uriContext,
                    basePageUUID: undefined
                }
            });
        });
    });

});

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
describe('addPageWizardService', function() {

    var $rootScope, addPageWizardService;
    var mocks;
    var uriContext = {
        a: 'b'
    };

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('addPageServiceModule')
            .mock('catalogService', 'retrieveUriContext').and.returnResolvedPromise(uriContext)
            .mock('modalWizard', 'open').and.returnValue('')
            .service('addPageWizardService');

        addPageWizardService = harness.service;
        mocks = harness.mocks;
        $rootScope = harness.injected.$rootScope;
    });

    describe('openAddPageWizard', function() {
        it('should delegate to the modal wizard', function() {
            addPageWizardService.openAddPageWizard();
            $rootScope.$digest();
            expect(mocks.modalWizard.open).toHaveBeenCalledWith({
                controller: 'addPageWizardController',
                controllerAs: 'addPageWizardCtl',
                properties: {
                    uriContext: uriContext
                }
            });
        });
    });

});

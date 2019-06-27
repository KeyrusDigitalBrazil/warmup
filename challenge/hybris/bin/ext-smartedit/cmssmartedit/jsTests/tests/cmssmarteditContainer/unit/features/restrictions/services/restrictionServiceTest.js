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
describe('restrictionService', function() {

    var restrictionsService, cmsitemsRestService, typeStructureRestService;
    var structuresRestService, structureModeManagerFactory;
    var $q;

    var MOCK_STRUCTURE_URI = "/cmswebservices/v1/types?code=:smarteditComponentType&mode=EDIT";

    var MOCK_MODE_MANAGER = {
        validateMode: function(mode) {
            //empty;
        }
    };

    var TYPE_RESTRICTION_STRUCTURE = [{
        category: "RESTRICTION",
        code: "RESTRICTIONTYPE1"
    }, {
        category: "RESTRICTION",
        code: "RESTRICTIONTYPE2"
    }, {
        category: "RESTRICTION",
        code: "RESTRICTIONTYPE3"
    }];

    beforeEach(angular.mock.module('restrictionsServiceModule', function($provide) {

        cmsitemsRestService = jasmine.createSpyObj('cmsitemsRestService', ['get']);
        cmsitemsRestService.get.and.callFake(function() {
            return $q.when({});
        });
        $provide.value('cmsitemsRestService', cmsitemsRestService);

        structuresRestService = jasmine.createSpyObj('structuresRestService', ['getUriForContext']);
        structuresRestService.getUriForContext.and.callFake(function() {
            return $q.when(MOCK_STRUCTURE_URI);
        });
        $provide.value('structuresRestService', structuresRestService);

        structureModeManagerFactory = jasmine.createSpyObj('structureModeManagerFactory', ['createModeManager']);
        structureModeManagerFactory.createModeManager.and.callFake(function() {
            return MOCK_MODE_MANAGER;
        });
        $provide.value('structureModeManagerFactory', structureModeManagerFactory);

        typeStructureRestService = jasmine.createSpyObj('typeStructureRestService', ['getStructuresByCategory']);
        typeStructureRestService.getStructuresByCategory.and.callFake(function() {
            return TYPE_RESTRICTION_STRUCTURE;
        });
        $provide.value('typeStructureRestService', typeStructureRestService);
    }));

    beforeEach(inject(function(_$q_, _restrictionsService_) {
        $q = _$q_;
        restrictionsService = _restrictionsService_;
    }));

    it('should get the structure API URI', function() {
        var mode = 'edit';
        var typeCode = 'CMSTimeRestriction';
        expect(restrictionsService.getStructureApiUri(mode, typeCode)).toBeResolvedWithData(MOCK_STRUCTURE_URI);
        expect(structuresRestService.getUriForContext).toHaveBeenCalledWith(mode, typeCode);
    });
});

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
describe('seProductCatalogVersionsSelector controller', function() {

    var $rootScope, $q, controller, catalogService, modalService, $log, $componentController, $translate;

    var VERSION1 = "version1";
    var VERSION2 = "version2";
    var CATALOG_VERSION_UUID1 = "catalog1Version/Online";
    var CATALOG_VERSION_UUID2 = "catalog2Version/Online";

    var DUMMY_CATALOG_SINGLE_RESULT = [{
        catalogId: "catalog1",
        versions: [{
            version: VERSION1,
            active: true,
            uuid: CATALOG_VERSION_UUID1
        }, {
            version: VERSION2,
            active: false,
            uuid: CATALOG_VERSION_UUID2
        }]
    }];

    var DUMMY_MULTIPLE_CATALOGS_RESULT = [{
        catalogId: "catalog1",
        versions: [{
            version: VERSION1,
            active: true,
            uuid: CATALOG_VERSION_UUID1
        }, {
            version: VERSION2,
            active: false,
            uuid: CATALOG_VERSION_UUID2
        }]
    }, {
        catalogId: "catalog2",
        versions: [{
            version: VERSION1,
            active: true,
            uuid: CATALOG_VERSION_UUID1
        }, {
            version: VERSION2,
            active: false,
            uuid: CATALOG_VERSION_UUID2
        }]
    }];

    var PARSED_VERSION_ID_LABEL = [{
        id: CATALOG_VERSION_UUID1,
        label: VERSION1
    }, {
        id: CATALOG_VERSION_UUID2,
        label: VERSION2
    }];

    var l10nFilterFunction = function() {
        return 'catalogName';
    };

    beforeEach(function() {
        angular.mock.module('yLoDashModule');
    });

    beforeEach(angular.mock.module('seProductCatalogVersionsSelectorModule', function($provide) {
        catalogService = jasmine.createSpyObj('catalogService', ['getProductCatalogsForSite']);
        $provide.value('catalogService', catalogService);

        modalService = jasmine.createSpyObj('modalService', ['open']);
        $provide.value('modalService', modalService);

        $provide.value('l10nFilter', l10nFilterFunction);

        $translate = jasmine.createSpyObj('$translate', ['instant']);
        $translate.instant.and.callFake(function(string) {
            return '_' + string;
        });

        $provide.value('$translate', $translate);

        var systemEventService = jasmine.createSpyObj('systemEventService', ['subscribe']);
        $provide.value('systemEventService', systemEventService);
        $provide.value('MODAL_BUTTON_ACTIONS', {});
        $provide.value('MODAL_BUTTON_STYLES', {});
        $provide.value('LINKED_DROPDOWN', 'some linked dropdown');
    }));

    beforeEach(inject(function(_$q_, _$rootScope_, _$componentController_, _$log_) {
        $q = _$q_;
        $log = _$log_;
        $rootScope = _$rootScope_;
        $componentController = _$componentController_;
    }));

    beforeEach(function() {
        controller = $componentController('seProductCatalogVersionsSelector', null, {
            model: {
                catalogDescriptor: {
                    siteId: "electronics"
                },
                previewCatalog: "electronics_electyronics-catalog_online"
            }
        });
    });

    it('should set single product catalog version select template when only one product catalog is available for site', function() {

        // Given
        controller.model.catalogVersions = [CATALOG_VERSION_UUID1];
        catalogService.getProductCatalogsForSite.and.returnValue($q.when(DUMMY_CATALOG_SINGLE_RESULT));

        // When
        controller.$onInit();
        $rootScope.$digest();

        // Then
        expect(controller.isSingleVersionSelector).toBe(true);
        expect(controller.isMultiVersionSelector).toBe(false);
    });

    it('should set fetchStrategy.fetchOptions to be set for single product catalog version', function() {

        // Given
        controller.model.catalogVersions = [CATALOG_VERSION_UUID1];
        catalogService.getProductCatalogsForSite.and.returnValue($q.when(DUMMY_CATALOG_SINGLE_RESULT));

        // When
        controller.$onInit();
        $rootScope.$digest();
        var productCatalogVersionsPromise = controller.fetchStrategy.fetchAll();
        $rootScope.$digest();

        // Then
        expect(controller.fetchStrategy.fetchAll).toEqual(jasmine.any(Function));
        expect(productCatalogVersionsPromise).toBeResolvedWithData(PARSED_VERSION_ID_LABEL);

    });

    it('should set multiple product catalog version select template when only one product catalog is available for site', function() {

        // Given
        controller.model.catalogVersions = [CATALOG_VERSION_UUID1, CATALOG_VERSION_UUID2];
        catalogService.getProductCatalogsForSite.and.returnValue($q.when(DUMMY_MULTIPLE_CATALOGS_RESULT));

        // When
        controller.$onInit();
        $rootScope.$digest();

        // Then
        expect(controller.isSingleVersionSelector).toBe(false);
        expect(controller.isMultiVersionSelector).toBe(true);
    });

    it('should map product catalogs versions to id and label fields', function() {

        // Given
        var mappedVersionsExpected = [{
            id: CATALOG_VERSION_UUID1,
            label: VERSION1
        }, {
            id: CATALOG_VERSION_UUID2,
            label: VERSION2
        }];

        // When
        var mappedVersions = controller.parseSingleCatalogVersion(DUMMY_CATALOG_SINGLE_RESULT[0]);

        // Then
        expect(mappedVersions).toEqual(mappedVersionsExpected);
    });
});

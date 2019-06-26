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
describe('structureModeManagerFactory', function() {

    var structureModeManagerFactory;
    var mockModes = ['a', 'b'];

    function getManager(optionalModes) {
        var modes = optionalModes || mockModes;
        return structureModeManagerFactory.createModeManager(modes);
    }

    beforeEach(angular.mock.module('structuresRestServiceModule', function($provide) {
        $provide.value('operationContextService', {
            register: angular.noop
        });
        $provide.value('OPERATION_CONTEXT', {
            CMS: 'CMS'
        });
    }));

    beforeEach(inject(function(_structureModeManagerFactory_) {
        structureModeManagerFactory = _structureModeManagerFactory_;
    }));

    it('constructor stores the passed in array of string modes', function() {
        expect(getManager().getSupportedModes()).toEqualData(mockModes);
    });

    it('isModeSupported() returns false for a not supported mode', function() {
        expect(getManager().isModeSupported('dummy')).toBe(false);
    });

    it('isModeSupported() returns true for a supported mode', function() {
        expect(getManager().isModeSupported('a')).toBe(true);
    });

    it('validateMode() throws exception for a non supported mode', function() {
        function f() {
            getManager().validateMode('dummy');
        }
        expect(f).toThrow();
    });

    it('validateMode() doesnt throw an exception for a supported mode', function() {
        function f() {
            getManager().validateMode('a');
        }
        expect(f).not.toThrow();
    });

});

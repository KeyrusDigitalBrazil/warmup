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
describe('ylodash', function() {

    var lodash;

    beforeEach(inject(function(_lodash_) {
        lodash = _lodash_;
    }));

    it('loads the right underscore library', function() {
        // Arrange
        var nestedArray = [1, [2, [3, [4]], 5]];
        var flattenedArray = [1, 2, 3, 4, 5];

        // Act
        var result = lodash.flattenDeep(nestedArray);

        // Assert
        expect(result).toEqual(flattenedArray);
    });

});

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
module.exports = function() {
    return {
        config: function(data, conf) {
            return {
                options: {
                    // BUNDLE_LOCATION will be replaced in the custom task with a relative path from DEST to
                    // the smartedit bundle directory of the given extension

                    smarteditContent: `<!--3rd prty libs-->    
        <script src="BUNDLE_LOCATION/webroot/static-resources/dist/smartedit/js/thirdparties.js"></script>
        <script src="BUNDLE_LOCATION/webroot/static-resources/thirdparties/ckeditor/ckeditor.js"></script>
        
        <!-- 3rd party css -->
        <link rel="stylesheet" href="BUNDLE_LOCATION/webroot/static-resources/dist/smartedit/css/outer-styling.css">
        
        <!--libs-->
        <script src="BUNDLE_LOCATION/webroot/static-resources/smarteditloader/js/smarteditloader.js"></script>
        <script src="BUNDLE_LOCATION/webroot/static-resources/smarteditcontainer/js/smarteditcontainer.js"></script>`,

                    // Extension specific html content to be inserted into the <head> of the generated index.html
                    // This content will be inserted AFTER the smartedit content

                    headerContent: "",

                    bundleContent: '<script src="BUNDLE_LOCATION/test/e2e/mocks/configurationMocks.js"></script>',

                    // Destination path and file relative to the extension root
                    dest: ""
                }
            };
        }
    };
};

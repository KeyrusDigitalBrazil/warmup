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
/* jshint esversion: 6 */
module.exports = function() {

    return {
        targets: [
            'landingPage',
            'smarteditE2e'
        ],
        config: function(data, conf) {

            conf.landingPage = {

                // TODO - really the html below is duplicated with the bundle version, there should be only one to maintain DRY principle

                // base smartedit files
                "smarteditContent": `<!--3rd prty libs-->    
<script src="static-resources/dist/smartedit/js/thirdparties.js"></script>
<script src="static-resources/thirdparties/ckeditor/ckeditor.js"></script>

<!-- 3rd party css -->
<link rel="stylesheet" href="static-resources/dist/smartedit/css/outer-styling.css">

<!--libs-->
<script src="static-resources/smarteditloader/js/smarteditloader.js"></script>
<script src="static-resources/smarteditcontainer/js/smarteditcontainer.js"></script>
`, // Note: smarteditloader.js MUST be loaded before smarteditcontainer.js for sourceMap to work properly.

                bundleContent: "",

                // path and file to output to
                // the path must be either absolute or relative to the root of the extension
                "dest": "web/webroot/WEB-INF/views/index.jsp"
            };


            conf.smarteditE2e = {
                headerContent: "",
                // TODO: smartedit.bundlePaths.test.e2e.applicationPath once smartedit/test is renamed to smartedit/jsTests
                dest: "test/e2e/smartedit.html"
            };

            return conf;
        }
    };

};




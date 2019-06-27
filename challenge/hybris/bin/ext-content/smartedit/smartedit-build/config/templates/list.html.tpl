<!DOCTYPE html>
<html>
<head>
    <title>E2E Tests</title>



    <script src="/smartedit-build/webroot/static-resources/dist/smartedit/js/thirdparties.js"></script>
    <link rel="stylesheet" href="/smartedit-build/webroot/static-resources/dist/smartedit/css/outer-styling.css">
</head>
<body>
    <script>
        var items = {{items}};
        items.unshift({
            key: 'smartedit application'
        });

        function prettyPrint(ugly) {
            if (ugly) {
                return JSON.stringify(ugly, undefined, 4);
            }
            return ugly;
        }

    </script>
    <script type="text/template" id="testListTpl">
        <div class="container">
            <table class="table table-striped table-condensed">
            <%
                smarteditLodash.each(items, function(item) {
            %>
            <tr>
                <td>
                    <div class="dropdown">
                        <button class="btn btn-default btn-sm dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true">
                            <span class="glyphicon glyphicon-th-list" aria-hidden="true"></span>
                        </button>
                        <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">
                            <li><textarea cols=150 rows=10 disabled><%= prettyPrint(item.data) %></textarea></li>
                        </ul>
                    </div>
                </td>
                <td class="col col-lg-11">
                    <a data-key="<%= item.key %>"><h5><%= item.key %></h5></a>
                </td>
            </tr>
            <%
                });
            %>
            </table>
        </div>
    </script>

    <!-- placeholder for e2e list -->
    <div id="testList"></div>

    <script>
        var template = smarteditJQuery("#testListTpl").html();
        smarteditJQuery("#testList").html(smarteditLodash.template(template, {items: items}));

        smarteditJQuery('a').click(function(e) {
            e.preventDefault();
            goToTest(e.currentTarget.dataset.key);
        });

        function goToTest(key) {
            var item = items.find(function(item) {
                return key === item.key;
            });
            if (item.data && item.data.jsFiles && item.data.jsFiles.length) {
                sessionStorage.setItem("additionalTestJSFiles", JSON.stringify(item.data.jsFiles));
            } else {
                sessionStorage.removeItem("additionalTestJSFiles");
            }
            window.location = 'smartedit.html';
        }
    </script>
</body>
</html>
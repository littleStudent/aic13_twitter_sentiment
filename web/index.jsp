<%--
  Created by IntelliJ IDEA.
  User: yarra
  Date: 20/11/13
  Time: 21:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Sentiment Analysis Application</title>

    <link href="css/ui-lightness/jquery-ui-1.10.3.custom.css" rel="stylesheet">
    <script src="js/jquery-1.9.0.js"></script>
    <script src="js/jquery-ui-1.10.0.custom.js"></script>

    <script type="text/javascript" src="js/plugins/jqplot.canvasTextRenderer.min.js"></script>
    <script type="text/javascript" src="js/plugins/jqplot.canvasAxisLabelRenderer.min.js"></script>
    <link class="include" rel="stylesheet" type="text/css" href="js/jquery.jqplot.min.css"/>
    <script class="include" type="text/javascript" src="js/jquery.jqplot.min.js"></script>

    <style>
        .bottom-three {
            margin-bottom: 3cm;
        }

        #column1 {
            float: left;
            margin-right: 50px;
            margin-left: 50px;
        }

        #column2 {
            float: left;
        }

        #label_connected {
            color: #00FF00;
            display: none;
        }

        #label_disconnected {
            color: #FF0000;
            display: block;
        }

    </style>

    <script type="text/javascript">
        $(document).ready(function () {
            $(".datepicker").datepicker({ dateFormat: 'yy-mm-dd' });
        });

        window.onload = render();

        var ws = null;
        var arr = [3, 7, 9, 1, 4, 6, 8, 2, 5];
        var plot1;

        function connect() {
            ws = new WebSocket("ws://127.0.0.1:8080/websocket/endpoint");

            ws.onopen = function () {
                setConnected(true);
            }

            ws.onclose = function () {
                setConnected(false);
            }

            ws.onmessage = function (msg) {
                var table = document.getElementById("result_table");
                var msgdata = msg.data.split(",");

                <%--cloudwatch message from server--%>
                if (msgdata[0] === "w") {
                    //alert(msgdata[0]);
                    arr.push(parseInt(msgdata[1]));
                    render();
                }
                <%--analysis result from server--%>
                else {
                    for (var i = 0, row; row = table.rows[i]; i++) {
                        if (parseInt(table.rows[i].cells[0].innerHTML) == parseInt(msgdata[0])) {
                            table.rows[i].cells[4].innerHTML = msgdata[1];
                        }
                    }
                }
            }
        }

        function disconnect() {
            if (ws != null) {
                ws.close();
            }

            setConnected(false);
        }

        function setConnected(connected) {
            document.getElementById("connect").disabled = connected;
            document.getElementById("disconnect").disabled = !connected;
            document.getElementById("start_analysis").disabled = !connected;

            if (connected) {
                document.getElementById("label_disconnected").style.display = "none";
                document.getElementById("label_connected").style.display = "block";
            } else {
                document.getElementById("label_disconnected").style.display = "block";
                document.getElementById("label_connected").style.display = "none";
            }
        }

        function start_analysis() {
            var table = document.getElementById("result_table");
            var name = document.getElementById("company_name").value;
            var begin = document.getElementById("begin").value;
            var end = document.getElementById("end").value;

            if (name == null || name == "" || begin == null || begin == "" || end == null || end == "") {
                alert("Please fill out all input fields!");
                return false;
            }

            var rowCount = table.rows.length;
            var row = table.insertRow(rowCount);

            var cell0 = row.insertCell(0);
            cell0.innerHTML = rowCount;

            var cell1 = row.insertCell(1);
            cell1.innerHTML = name;

            var cell2 = row.insertCell(2);
            cell2.innerHTML = begin;

            var cell3 = row.insertCell(3);
            cell3.innerHTML = end;

            var cell4 = row.insertCell(4);
            cell4.innerHTML = "pending...";

            ws.send(rowCount + "," + name + "," + begin + "," + end);
        }

        function render() {
            $(document).ready(function () {
                if (plot1) {
                    plot1.destroy();
                }
                plot1 = $.jqplot('chart1', [arr]);
            });
        }

    </script>
</head>

<body>

<div id="column1">

    <h2>Connect to Server:</h2>

    <table>
        <th>Status:</th>
        <th>
            <div id="label_disconnected">disconnected</div>
            <div id="label_connected">connected</div>
        </th>
    </table>

    <div>
        <button id="connect" onclick="connect();">Connect</button>
        <button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
    </div>

    <h3>Enter Information for Analysis:</h3>

    <table id="analysis_information">
        <tr>
            <td><label>Company name:</label></td>
            <td><input type="text" id="company_name" value=""/></td>
        </tr>
        <tr>
            <td><label>Begin: </label></td>
            <td><input type="text" id="begin" name="begin" class="datepicker" value=""/></td>
        </tr>
        <tr>
            <td><label>End: </label></td>
            <td><input type="text" id="end" name="end" class="datepicker" value=""/></td>
        </tr>
    </table>

    <input type="button" id="start_analysis" value="Start Analysis" onclick="start_analysis()" disabled="disabled"/>

    <div class="bottom-three"></div>

    <p>Results:</p>
    <table id="result_table" border="1">
        <tr>
            <th>ID</th>
            <th>Company Name</th>
            <th>Start Date</th>
            <th>End Date</th>
            <th>Analysis Result</th>
        </tr>
    </table>
</div>

<div id="column2">
    <div id="chart1" class="plot" style="width:700px;height:500px;">
    </div>
</div>

</body>
</html>
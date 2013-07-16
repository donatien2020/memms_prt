<%@ page import="org.grails.plugins.google.visualization.util.DateUtil" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="main" />
    <title><g:message code="default.report.label" /></title>
  <r:require module="reports"/>

  <link href='http://fonts.googleapis.com/css?family=Droid+Sans:400,700&subset=latin' rel='stylesheet' type='text/css'>
  
  <script type="text/javascript" src="http://www.google.com/jsapi"></script>
</head>
<body>
<g:render template="${template}"/>
</body>
</html>


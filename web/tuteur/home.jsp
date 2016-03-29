<%@ page import="org.json.JSONObject" %>
<%@ page import="java.util.Iterator" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags
    -->
    <link rel="stylesheet" href="css/bootstrap.min.css"/>
    <link rel="stylesheet" href="css/home.css"/>
    <title>Accueil</title>
</head>
<body id="home">
<%
    JSONObject user = (JSONObject) request.getAttribute("user");
    Iterator<String> iterator = user.keys();
    String key = null;
    int type = user.getInt("type");
%>
<div class="container">
    <div class="row " >
        <div class="col-md-9 ">
            <h2 class="text-center"><b>Université de Constantine 2</b></h2>
            <h4 class="text-center"><b>Gestion de Stage des Etudiants TLSI</b></h4>

        </div>
        <div class="col-md-3 pull-right" style="margin-bottom: 30px;margin-top: 30px;">
            <button type="button" id="modify-info" class="btn btn-success" onclick="setupModifyProfile();">
                Modifier Infos
            </button>
            <button type="button" id="logout" class="btn btn-danger " onclick="doLogout();">
                Se deconnecter
            </button>

        </div>

    </div>
    <!-- end of top row -->

    <div class="row">

        <div class="col-md-2 col-lg-2">

            <ul class="list-group">
                <%if (type == 2) {%>
                <li class="menu list-group-item btn-link" onclick="setProposerTheme()">Proposer thèmes</li>
                <%}%>
                <li class="menu list-group-item btn-link" onclick="fetchStudents()">Mentionner les notes</li>
                <li class="menu list-group-item btn-link" onclick="fetchReclamations()">Consulter les reclamations</li>
                <li class="menu list-group-item btn-link" onclick="fetchEtat()" >Suivre Etat</li>

            </ul>
        </div>

        <!-- Center -->
        <div class="col-md-7 col-lg-7" id="mid-container">


        </div>


        <div class="col-md-3 col-lg-3">

            <ul class="list-group">
                <% while (iterator.hasNext()) { %>
                <%  key = iterator.next();%>
                <li class="list-group-item">
                    <%= (key + ": " + user.get(key))%>
                </li>
                <%}%>
            </ul>

        </div>

    </div>
    <!-- end of second row -->

</div>
<!-- end of container -->

<script type="text/javascript" src="js/jquery-min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/sha256.js"></script>
<script type="text/javascript" src="js/home.js"></script>

</body>
</html>

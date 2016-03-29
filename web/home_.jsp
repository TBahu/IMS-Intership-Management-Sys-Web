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
        <link rel="stylesheet" href="css/style.css"/>
        <title>Accueil</title>
        </head>
        <body id="home">
            <%
    JSONObject user = (JSONObject)request.getAttribute("user");
    int userType = user.getInt("type");
    Iterator<String> iterator = user.keys();
    String key = null;
%>
            <div class="container">
        <div id="top-home" class="row">
        <div>
        </div>
        </div>
        <!-- end of top row -->

        <div class="row">

        <div class="col-md-2 col-lg-2">

        <ul class="list-group">
            <% if (userType == 0){%>
        <li class="menu list-group-item">Déposer une reclamation</li>
        <li class="menu list-group-item">Choisir un thèmes</li>
        <li class="menu list-group-item">Suivre votre état</li>
            <%} else if(userType == 1 || userType == 2){%>

        <li class="menu list-group-item">Proposer un thèmes</li>
        <li class="menu list-group-item">Mentionner les notes</li>
        <li class="menu list-group-item">Consulter les reclamations</li>

            <%}%>
        </ul>

        <footer>
        <div>
        <button type="button" id="logout" class="btn btn-block btn-lg btn-danger">
        Se deconnecter
        </button>
        </div>
        </footer>
        </div>
        <div class="col-md-7 col-lg-7">


        </div>


        <div class="col-md-3 col-lg-3">

        <ul class="list-group">
            <% while(iterator.hasNext()){ %>
            <%key = iterator.next();%>
            <li class="list-group-item" ><%= (key + ": "+user.get(key))%></li>
            <%}%>

        </ul>

        <footer>
        <div>
        <button type="button" id="modify-info" class="btn btn-block btn-lg btn-success">
        Modifier Infos
        </button>
        </div>
        </footer>


        </div>

        </div><!-- end of second row -->

        </div><!-- end of container -->

        <script type="text/javascript" src="js/jquery-min.js"></script>
        <script type="text/javascript" src="js/bootstrap.min.js"></script>
        <script type="text/javascript" src="js/script.js"></script>

        </body>
        </html>

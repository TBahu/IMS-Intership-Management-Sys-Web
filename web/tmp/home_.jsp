<%@ page import="org.json.JSONObject" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags
    -->
    <link rel="stylesheet" href="../css/bootstrap.min.css"/>
    <link rel="stylesheet" href="../css/style.css"/>
    <title>Accueil</title>
</head>
<body id="home">
<%

    JSONObject user = (JSONObject)request.getAttribute("user");
%>
<div class="container">
    <div id="top-home" class="row">
        <div>
            <%=user.toString()%>
        </div>
    </div>
    <!-- end of top row -->

    <div class="row">

        <div class="col-md-2 col-lg-2">

            <ul class="list-group">
                <li class="menu list-group-item">Reclamer</li>
                <li class="menu list-group-item">Thèmes</li>
                <li class="menu list-group-item">Etat</li>

                <li class="menu list-group-item">Thèmes</li>
                <li class="menu list-group-item">Notes</li>
                <li class="menu list-group-item">Reclamations</li>

            </ul>

            <footer>
                <div>
                    <button type="button" id="logout" class="btn btn-block btn-lg btn-danger side-bottom">
                        Se deconnecter
                    </button>
                </div>
            </footer>
        </div>
        <div class="col-md-8 col-lg-8">


        </div>


        <div class="col-md-2 col-lg-2">

            <ul class="list-unstyled menu">
            </ul>

            <footer>
                <div>
                    <button type="button" id="modify-info" class="btn btn-block btn-lg side-bottom btn-success">
                        Modifier Infos
                    </button>
                </div>
            </footer>


        </div>

    </div>
    <!-- end of second row -->

</div>
<!-- end of container -->

<script type="text/javascript" src="../js/jquery-min.js"></script>
<script type="text/javascript" src="../js/bootstrap.min.js"></script>

</body>
</html>

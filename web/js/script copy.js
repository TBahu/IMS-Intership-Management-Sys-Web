/**
 * Created by newton on 4/16/15.
 */

var errorTimeOut;
//window.onload = function(){
var doLogin = function () {
    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
            var jsonObj = JSON.parse(xmlHttp.responseText);
            if (jsonObj["response code"] == 1) {
                //window.location.href("/home_.jsp");
                //window.location.replace("/home_.jsp");
            } else {
                errorTimeOut = setTimeout(function () {
                    clearError();
                }, 3000);
                document.getElementById(("error")).innerHTML = jsonObj["response message"];

            }
        }
    }

    xmlHttp.open("POST", "/login", true);
    xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttp.send("email=" + email + "&" + "password=" + password);

};

var clearError = function () {
    if (errorTimeOut) {
        clearTimeout(errorTimeOut);
    }
    var errorLabel = document.getElementById("error");
    errorLabel.innerHTML = "";
};

var recover = function () {
    window.open("/recover.html", "_self");
};

var validateAndSend = function () {
    var email = document.getElementById("email").value;
    var errorLabel = document.getElementById("error");
    var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    var statusLabel = document.getElementById("status");
    if (re.test(email)) {
        //do AJAX here and change status
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                var jsonObj = JSON.parse(xmlHttp.responseText);
                statusLabel.style.color = "green";
                statusLabel.innerHTML = jsonObj["response message"];
                var forgotTop = document.getElementById("forgot-top");
                forgotTop.parentNode.removeChild(forgotTop);
                var btnGetCode = document.getElementById("get-code");
                btnGetCode.parentNode.removeChild(btnGetCode);
            }

        };
        xmlHttp.open("POST", "/forgot-password", true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.send("email=" + email + "&" + "demand=yes");

    } else {
        errorLabel.innerHTML = "Vous avez entrez une addresse email invalide!";
        errorTimeOut = setTimeout(function () {
            errorLabel.innerHTML = "";
        }, 3000);
    }
};


var getRegistrationPage = function () {
    window.open("/signup.html");
};

var getForgotPage = function () {
    window.open("/forgot.html");
};

//};


var setRegisterContent = function () {


};
/*
 var setLoginContents = function(){

 var loginContents = '<legend>Authentification</legend>'+
 '<div class="form-group">'+
 '<input type="email" id="email" name="email"'+
 'placeholder="Addresse email" autofocus="autofocus" required="required" class="form-control"/>'+

 '</div>'+

 '<br/>'+
 '<div class="form-group">'+
 '<input type="password" id="password" name="password"  class="form-control"'+
 'placeholder="Mot de passe"/>'+

 '</div>'+
 '<br/>'+
 '<div>'+
 '<input type="submit" id="login" onclick="doLogin()"'+
 'value="S\'authentifier" class="btn btn-primary btn-lg btn-block"/>'
 '</div>';
 document.getElementById("form-contents").innerHTML = loginContents;
 //            <button type="button" id="to-register" class="btn-link" onclick="setRegisterContent()">S'inscrire</button>


 var registerButton = document.getElementById("to-register");
 registerButton.onclick = setLoginContents;
 registerButton.innerText = 'S\'authentifier';
 var forgotButton = document.getElementById("to-forgot");

 };*/

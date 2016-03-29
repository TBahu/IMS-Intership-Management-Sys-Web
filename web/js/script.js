/**
 * Created by newton on 4/16/15.
 */

var errorTimeOut;
var sessionId;
var type;
var hashPassword = function(password){
    return CryptoJS.SHA256(password);
};

var validateInitForm= function () {
    var code = document.getElementById("code").value;
    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;
    var confirmedPassword = document.getElementById("confirm-password").value;
    var errorLabel = document.getElementById("error");
    var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    //a password should be 8 characters to begin with and the two should match
    //re.test(email);
    var valid = true;
    var errorMessage = "";
    if(!re.test(email)) {
        //email is invalid
        errorMessage += "Addresse email invalide <br/>";
        valid =false;
    }

    if(password !== confirmedPassword){
        errorMessage += "Les mots de passes ne sont pas égaux <br/>";
        valid = false;
    }

    if(password.length < 8){
        errorMessage += "Le mot de passe doit être au minimum 8 caractères <br/>";
        valid = false;
    }

    if(code.length != 8){
        errorMessage += "Code d'initialisation doit être exactement 6 caractères <br/>";
        valid = false;
    }

    if(valid){

        return [code,email,hashPassword(password)]
    }
    //show error
    errorLabel.innerHTML = "Une addresse email invalide ou mot de passe trôp court! <br/>  ";
    errorTimeOut = setTimeout(function () {
        errorLabel.innerHTML = "";
    }, 3000);
    return false;

};

var isValidEmail= function () {
    var email = document.getElementById("email").value;
    var errorLabel = document.getElementById("error");
    var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    //a password should be 8 characters to begin with
    //re.test(email);
    if (re.test(email)) {
        return email;
    } else {
        errorLabel.innerHTML = "Une addresse email invalide!";
        errorTimeOut = setTimeout(function () {
            errorLabel.innerHTML = "";
        }, 3000);
        return false;
    }
};
var doLogin = function () {


    var values = validateLoginForm();
    if(values){
        //all went well from the form
        //hide all buttons and show a progress bar
        document.getElementById("btn-login").setAttribute("class", "btn btn-primary btn-lg btn-block hidden");
        document.getElementById("btn-left-bottom").setAttribute("class", "btn btn-link hidden");
        document.getElementById("btn-right-bottom").setAttribute("class", "btn btn-link hidden");
        document.getElementById("progress-div").setAttribute("class","progress");
        //make ajax request
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                var jsonObj = JSON.parse(xmlHttp.responseText);
                if (jsonObj["response code"] == 1) {
                    //if all went successfully redirect
                    console.log(xmlHttp.responseText);
                    sessionId = jsonObj.sessionid;
                    location.replace("/home?sessionid="+sessionId,"_self");

                } else {
                    errorTimeOut = setTimeout(function () {
                        clearError();
                    }, 3000);
                    document.getElementById(("error")).innerHTML = jsonObj["response message"];
                    //hide progress bar and show button
                    document.getElementById("btn-login").setAttribute("class", "btn btn-primary btn-lg btn-block");
                    document.getElementById("progress-div").setAttribute("class","progress hidden");
                    document.getElementById("btn-left-bottom").setAttribute("class", "btn btn-link");
                    document.getElementById("btn-right-bottom").setAttribute("class", "btn btn-link");

                }
            }
        }
        xmlHttp.open("POST", "/login", true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.send("email=" + values[0] + "&" + "password=" + values[1]);
        console.log(values);

    }


};

var doRegister = function () {

    var values = validateSignUpForm();
    if(values){
        //hide all buttons and show progress bar
        document.getElementById("btn-signup").setAttribute("class", "btn btn-primary btn-lg btn-block hidden");
        document.getElementById("btn-left-bottom").setAttribute("class", "btn btn-link hidden");
        document.getElementById("btn-right-bottom").setAttribute("class", "btn btn-link hidden");

        document.getElementById("progress-div").setAttribute("class","progress");

        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                var jsonObj = JSON.parse(xmlHttp.responseText);
                if (jsonObj["response code"] == 1) {
                    //if all went successfully redirect
                    window.location.replace("/home?sessionid="+jsonObj.sessionid,"_self");
                } else {
                    errorTimeOut = setTimeout(function () {
                        clearError();
                    }, 3000);
                    document.getElementById(("error")).innerHTML = jsonObj["response message"];
                    //hide progress bar and show button
                    document.getElementById("btn-signup").setAttribute("class", "btn btn-primary btn-lg btn-block");
                    document.getElementById("progress-div").setAttribute("class","progress hidden");
                    document.getElementById("btn-left-bottom").setAttribute("class", "btn btn-link");
                    document.getElementById("btn-right-bottom").setAttribute("class", "btn btn-link");

                }
            }
        }
        xmlHttp.open("POST", "/register", true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.send("id="+values[0]+"&"+"email=" + values[1] + "&" + "password=" + values[2]);

    }

    //show them later  and hide progress if something went wrong

    //redirect if everything was successful



};

var demandCode = function () {
    //if all went successfully after code request
    var email;
    if(email = isValidEmail()){
        //email is valid
        //make ajax request
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                var jsonObj = JSON.parse(xmlHttp.responseText);
                document.getElementById("btn-demand").setAttribute("class", "btn btn-primary btn-lg btn-block");
                document.getElementById("progress-div").setAttribute("class","progress hidden");
                document.getElementById("btn-left-bottom").setAttribute("class", "btn btn-link");
                document.getElementById("btn-right-bottom").setAttribute("class", "btn btn-link");
                var status  = document.getElementById("status");
                if (jsonObj["response code"] == 1) {
                    //if all went successfully show the response as status
                    status.style.fontWeight ="bold";
                    status.style.color = "green";
                    status.innerHTML = jsonObj["response message"];
                    show(); //show input fields
                } else {
                    status.style.fontWeight ="bold";
                    status.style.color = "red";
                    document.getElementById(("status")).innerHTML = jsonObj["response message"];
                    errorTimeOut = setTimeout(function () {
                        //clearError(id);
                    }, 3000);

                    //hide progress bar and show button
                    document.getElementById("btn-signup").setAttribute("class", "btn btn-primary btn-lg btn-block");
                    document.getElementById("progress-div").setAttribute("class","progress hidden");
                    document.getElementById("btn-left-bottom").setAttribute("class", "btn btn-link");
                    document.getElementById("btn-right-bottom").setAttribute("class", "btn btn-link");

                }
            }
        }
        }
        xmlHttp.open("POST", "/forgot-password", true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.send("demand=yes"+"&"+"email=" + email);
    //else display error

};

var changePassword = function () {

    var values = validateInitForm();
    if(values){
        document.getElementById("btn-demand").setAttribute("class", "btn btn-primary btn-lg btn-block hidden");
        document.getElementById("btn-left-bottom").setAttribute("class", "btn btn-link hidden");
        document.getElementById("btn-right-bottom").setAttribute("class", "btn btn-link hidden");
        document.getElementById("progress-div").setAttribute("class","progress");
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function () {
            if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                var jsonObj = JSON.parse(xmlHttp.responseText);
                document.getElementById("btn-signup").setAttribute("class", "btn btn-primary btn-lg btn-block");
                document.getElementById("progress-div").setAttribute("class","progress hidden");
                document.getElementById("btn-left-bottom").setAttribute("class", "btn btn-link");
                document.getElementById("btn-right-bottom").setAttribute("class", "btn btn-link");
                if (jsonObj["response code"] == 1) {
                    //if all went successfully show the response as status
                    var status  = document.getElementById("status");
                    status.style.fontWeight ="bold";
                    status.style.color = "green";
                    status.innerHTML = jsonObj["response message"];
                    console.log(status.innerHTML);
                    //show(); //show input fields
                } else {
                    errorTimeOut = setTimeout(function () {
                        clearError();
                    }, 3000);
                    document.getElementById(("status")).innerHTML = jsonObj["response message"];
                    //hide progress bar and show button
                    document.getElementById("btn-signup").setAttribute("class", "btn btn-primary btn-lg btn-block");
                    document.getElementById("progress-div").setAttribute("class","progress hidden");
                    document.getElementById("btn-left-bottom").setAttribute("class", "btn btn-link");
                    document.getElementById("btn-right-bottom").setAttribute("class", "btn btn-link");

                }
            }
        }
        xmlHttp.open("POST", "/forgot-password", true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.send("demand=no"+"&"+"code="+values[0]+"&"+"email=" + values[1]+"&"+"password="+values[2]);



    }


    //show them and hide progress incase of error after ajax

    //redirect to auth if successful




};

var clearError = function () {
    if (errorTimeOut) {
        clearTimeout(errorTimeOut);
    }
    var errorLabel = document.getElementById("error");
    errorLabel.innerHTML = "";
};

var validateSignUpForm= function () {
    var id = document.getElementById("identifier").value;
    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;
    var confirmedPassword = document.getElementById("confirm-password").value;
    var errorLabel = document.getElementById("error");
    var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    //a password should be 8 characters to begin with and the two should match
    //re.test(email);
    var valid = true;
    var errorMessage = "";
    if(!re.test(email)) {
        //email is invalid
        errorMessage += "Addresse email invalide <br/>";
        valid =false;
    }

    if(password !== confirmedPassword){
        errorMessage += "Les mots de passes ne sont pas égaux <br/>";
        valid = false;
    }

    if(password.length < 8){
        errorMessage += "Le mot de passe doit être au minimum 8 caractères <br/>";
        valid = false;
    }

    if(id.length != 6){
        errorMessage += "Identifier doit être exactement 6 caractères <br/>";
        valid = false;
    }

    if(valid){

        return [id,email,hashPassword(password)]
    }
    //show error
        errorLabel.innerHTML = "Une addresse email invalide ou mot de passe trôp court! <br/>  ";
        errorTimeOut = setTimeout(function () {
            errorLabel.innerHTML = "";
        }, 3000);
        return false;

};

var validateLoginForm= function () {
    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;
    var errorLabel = document.getElementById("error");
    var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    //a password should be 8 characters to begin with
    //re.test(email);
    if(re.test(email) && password.length >= 8){
        //email is valid and password acceptable
        //hash the password
        var hashed = hashPassword(password);//password should not be transferred in plain text
        return [email,hashed];//so that login can proceed
    }else {
        errorLabel.innerHTML = "Une addresse email invalide ou mot de passe trôp court!";
        errorTimeOut = setTimeout(function () {
            errorLabel.innerHTML = "";
        }, 3000);
        return false;
    }

    /*if (re.test(email)) {
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
     }*/
};


var setRegisterContent = function () {
    //document.getElementsByName("title")[0].innerHTML = "Inscription";


    var formContent = document.getElementById("form-contents");
    if (formContent) {
        //if a form has child nodes remove them
        //use jquery empty function because at this point jquery has been loaded already
        //$("#form-content").empty();//pay attention to the id selector
        formContent.innerHTML = "";
    }

    //also remove elements from the lower block
    var lowerBlock = document.getElementById("lower-div")
    if (lowerBlock) {
        //$("#lower-div").empty();//pay attention to the id selector
        lowerBlock.innerHTML = "";
    }
    //create and add legend
    var legend = document.createElement("legend");
    legend.setAttribute("id", "legend");
    legend.setAttribute("class", "text-center");
    legend.innerHTML = "Inscription";
    //create and add error
    var error = document.createElement("span");
    error.setAttribute("id", "error");
    error.style.fontSize = "small";
    error.style.color = "red";
    error.style.margin = "5px";
    formContent.appendChild(legend);
    formContent.appendChild(error);

    //create and add identifier div and its input
    var identifierDiv = document.createElement("div");
    identifierDiv.setAttribute("id", "identifier-div");
    identifierDiv.setAttribute("class", "form-group");
    var identifierInput = document.createElement("input");
    identifierInput.setAttribute("type", "text");
    identifierInput.setAttribute("id", "identifier");
    identifierInput.setAttribute("name", "id");
    identifierInput.setAttribute("placeholder", "Identifier");
    identifierInput.setAttribute("autofocus", "autofocus");
    identifierInput.setAttribute("required", "required");
    identifierInput.setAttribute("class", "form-control");
    identifierDiv.appendChild(identifierInput);
    identifierDiv.innerHTML += "<br/>";
    formContent.appendChild(identifierDiv);

    //create and add email div and its input
    var emailDiv = document.createElement("div");
    emailDiv.setAttribute("id", "email-div");
    emailDiv.setAttribute("class", "form-group");
    var emailInput = document.createElement("input");
    emailInput.setAttribute("type", "email");
    emailInput.setAttribute("id", "email");
    emailInput.setAttribute("name", "email");
    emailInput.setAttribute("placeholder", "Addresse email");
    emailInput.setAttribute("autofocus", "autofocus");
    emailInput.setAttribute("required", "required");
    emailInput.setAttribute("class", "form-control");
    emailDiv.appendChild(emailInput);
    emailDiv.innerHTML += "<br/>";
    formContent.appendChild(emailDiv);

    //create the password div and add to it password input
    var passwordDiv = document.createElement("div");
    passwordDiv.setAttribute("class", "form-group");
    passwordDiv.setAttribute("id", "password-div");
    //create the confirm password input
    var passwordInput = document.createElement("input");
    passwordInput.setAttribute("type", "password");
    passwordInput.setAttribute("id", "password");
    passwordInput.setAttribute("name", "password");
    passwordInput.setAttribute("placeholder", "Mot de passe");
    passwordInput.setAttribute("required", "required");
    passwordInput.setAttribute("class", "form-control");
    passwordDiv.appendChild(passwordInput);
    passwordDiv.innerHTML += "<br/>";
    formContent.appendChild(passwordDiv);


    //create the confirm password div and add to it password input
    var confirmPasswordDiv = document.createElement("div");
    confirmPasswordDiv.setAttribute("class", "form-group");
    confirmPasswordDiv.setAttribute("id", "password-div");
    //create the confirm password input
    var confirmPasswordInput = document.createElement("input");
    confirmPasswordInput.setAttribute("type", "password");
    confirmPasswordInput.setAttribute("id", "confirm-password");
    confirmPasswordInput.setAttribute("name", "password");
    confirmPasswordInput.setAttribute("placeholder", "Confirmez mot de passe");
    confirmPasswordInput.setAttribute("required", "required");
    confirmPasswordInput.setAttribute("class", "form-control");
    confirmPasswordDiv.appendChild(confirmPasswordInput);
    confirmPasswordDiv.innerHTML += "<br/>";
    formContent.appendChild(confirmPasswordDiv);


    //create the main button div and add it to it a button for signup
    var buttonDiv = document.createElement("div");
    buttonDiv.setAttribute("id", "button-div");

    var button = document.createElement("button");
    button.setAttribute("id", "btn-signup");
    button.setAttribute("class", "btn btn-primary btn-lg btn-block");
    button.setAttribute("type", "button");
    button.setAttribute("onclick","doRegister()");
    button.style.backgroundColor = "#55aace";
    button.innerHTML = "S'inscrire";

    buttonDiv.appendChild(button);
    buttonDiv.innerHTML += "<br/>";
    formContent.appendChild(buttonDiv);

    var progressDiv = document.createElement("div");
    progressDiv.setAttribute("class","progress hidden");
    progressDiv.setAttribute("id","progress-div");
    var bar = document.createElement("div");
    bar.setAttribute("class","progress-bar progress-bar-striped active");
    bar.setAttribute("role","progressbar");
    bar.style.width = "100%";

    progressDiv.appendChild(bar);
    formContent.appendChild(progressDiv);

    //create the lower buttons and add them to lower div
    var lowerDiv = document.createElement("div");
    lowerDiv.setAttribute("id", "lower-div");
    lowerDiv.style.marginTop = "20px";

    var leftButton = document.createElement("button");
    leftButton.setAttribute("id", "btn-left-bottom")
    leftButton.setAttribute("class", "btn-link");
    leftButton.setAttribute("type", "button");
    leftButton.setAttribute("onclick", "setLoginContents()");
    leftButton.innerHTML = "S'authentifier";
    leftButton.style.marginRight = "50px";
    leftButton.style.marginLeft = "10px";


    var rightButton = document.createElement("button");
    rightButton.setAttribute("id", "btn-right-bottom");
    rightButton.setAttribute("class", "btn-link");
    rightButton.setAttribute("type", "button");
    rightButton.setAttribute("onclick", "setRecoverContents()");
    rightButton.innerHTML = "Réinitialiser mot de passe";
    rightButton.style.marginRight = "0px";
    rightButton.style.marginLeft = "40px";
    lowerDiv.appendChild(leftButton);
    lowerDiv.appendChild(rightButton);
    lowerDiv.innerHTML += "<br/>";
    formContent.appendChild(lowerDiv);

};
var setLoginContents = function () {
    var formContent = document.getElementById("form-contents");
    if (formContent) {
        //if a form has child nodes remove them
        //use jquery empty function
        //$("#form-contents").empty();//pay attention to the id selector
        formContent.innerHTML = "";

    }

    //also remove elements from the lower block
    var lowerBlock = document.getElementById("lower-div")
    if (lowerBlock) {
        //$("#lower-div").empty();//pay attention to the id selector
        lowerBlock.innerHTML = "";
    }
    //create and add legend
    var legend = document.createElement("legend");
    legend.setAttribute("id", "legend");
    legend.setAttribute("class", "text-center");
    legend.innerHTML = "Authentification";
    //create and add error
    var error = document.createElement("span");
    error.setAttribute("id", "error");
    error.style.fontSize = "small";
    error.style.color = "red";
    error.style.margin = "5px";
    formContent.appendChild(legend);
    formContent.appendChild(error);

    //create and add email div and its input
    var emailDiv = document.createElement("div");
    emailDiv.setAttribute("id", "email-div");
    emailDiv.setAttribute("class", "form-group");
    var emailInput = document.createElement("input");
    emailInput.setAttribute("type", "email");
    emailInput.setAttribute("id", "email");
    emailInput.setAttribute("name", "email");
    emailInput.setAttribute("placeholder", "Addresse email");
    emailInput.setAttribute("autofocus", "autofocus");
    emailInput.setAttribute("required", "required");
    emailInput.setAttribute("class", "form-control");
    emailInput.onfocus = clearError;
    emailDiv.appendChild(emailInput);
    emailDiv.innerHTML += "<br/>";
    formContent.appendChild(emailDiv);

    //create the password div and add to it password input
    var passwordDiv = document.createElement("div");
    passwordDiv.setAttribute("class", "form-group");
    passwordDiv.setAttribute("id", "password-div");
    //create the confirm password input
    var passwordInput = document.createElement("input");
    passwordInput.setAttribute("type", "password");
    passwordInput.setAttribute("id", "password");
    passwordInput.setAttribute("name", "password");
    passwordInput.setAttribute("placeholder", "Mot de passe");
    passwordInput.setAttribute("required", "required");
    passwordInput.setAttribute("class", "form-control");
    passwordDiv.appendChild(passwordInput);
    passwordDiv.innerHTML += "<br/>";
    formContent.appendChild(passwordDiv);


    //create the main button div and add it to it a button for login
    var buttonDiv = document.createElement("div");
    buttonDiv.setAttribute("id", "button-div");

    var button = document.createElement("button");
    button.setAttribute("id", "btn-login");
    button.setAttribute("class", "btn btn-primary btn-lg btn-block");
    button.setAttribute("type", "button");
    button.setAttribute("onclick", "doLogin()");
    button.style.backgroundColor = "#55aace";
    //button.onclick = doLogin;
    button.innerHTML = "S'authentifier";

    buttonDiv.appendChild(button);
    buttonDiv.innerHTML += "<br/>";
    formContent.appendChild(buttonDiv);

    var progressDiv = document.createElement("div");
    progressDiv.setAttribute("class","progress hidden");
    progressDiv.setAttribute("id","progress-div");
    var bar = document.createElement("div");
    bar.setAttribute("class","progress-bar progress-bar-striped active");
    bar.setAttribute("role","progressbar");
    bar.style.width = "100%";

    progressDiv.appendChild(bar);
    formContent.appendChild(progressDiv);
    //create the lower buttons and add them to lower div
    var lowerDiv = document.createElement("div");
    lowerDiv.setAttribute("id", "lower-div");
    lowerDiv.style.marginTop = "20px";

    var leftButton = document.createElement("button");
    leftButton.setAttribute("id", "btn-left-bottom")
    leftButton.setAttribute("class", "btn-link");
    leftButton.setAttribute("type", "button");
    leftButton.setAttribute("onclick", "setRegisterContent()");
    leftButton.innerHTML = "S'inscrire";
    leftButton.style.marginRight = "50px";
    leftButton.style.marginLeft = "10px";


    var rightButton = document.createElement("button");
    rightButton.setAttribute("id", "btn-right-bottom");
    rightButton.setAttribute("class", "btn-link");
    rightButton.setAttribute("onclick", "setRecoverContents()");
    rightButton.setAttribute("type", "button");
    rightButton.innerHTML = "Réinitialiser mot de passe";
    rightButton.style.marginRight = "0px";
    rightButton.style.marginLeft = "40px";
    lowerDiv.appendChild(leftButton);
    lowerDiv.appendChild(rightButton);
    lowerDiv.innerHTML += "<br/>";
    formContent.appendChild(lowerDiv);

};

var setRecoverContents = function () {

    var formContent = document.getElementById("form-contents");
    if (formContent) {
        //if a form has child nodes remove them
        //use jquery empty function
        //$("#form-contents").empty();//pay attention to the id selector
        formContent.innerHTML = "";

    }

    //also remove elements from the lower block
    var lowerBlock = document.getElementById("lower-div")
    if (lowerBlock) {
        //$("#lower-div").empty();//pay attention to the id selector
        lowerBlock.innerHTML = "";
    }
    //create and add legend
    var legend = document.createElement("legend");
    legend.setAttribute("id", "legend");
    legend.setAttribute("class", "text-center");
    legend.innerHTML = "Réinitialisation de mot de passe";
    formContent.appendChild(legend);

    //create and add status label
    var status = document.createElement("span");
    status.setAttribute("id", "status");
    status.setAttribute("class", "text-center");
    status.innerHTML = "Entrer votre addresse email ici et cliquez le button pour demande un code d'initialisation";
    formContent.appendChild(status);
    formContent.innerHTML += ("<br/>");

    //create and add error span(em)
    var span = document.createElement("span");
    var emph = document.createElement("em");
    emph.setAttribute("id", "error");
    emph.style.fontSize = "small";
    emph.style.color = "red";
    span.style.margin = "5px";
    span.appendChild(emph);
    formContent.appendChild(span);

    //create and add email div and its input
    var emailDiv = document.createElement("div");
    emailDiv.setAttribute("id", "email-div");
    emailDiv.setAttribute("class", "form-group");
    var emailInput = document.createElement("input");
    emailInput.setAttribute("type", "email");
    emailInput.setAttribute("onfocus", "clearError()");
    emailInput.setAttribute("id", "email");
    emailInput.setAttribute("name", "email");
    emailInput.setAttribute("placeholder", "Addresse email");
    emailInput.setAttribute("autofocus", "autofocus");
    emailInput.setAttribute("required", "required");
    emailInput.setAttribute("class", "form-control");



    formContent.innerHTML += "<br/>";
    emailDiv.appendChild(emailInput);
    emailDiv.innerHTML += "<br/>";
    formContent.appendChild(emailDiv);

    //create and add code div and its input
    var codeDiv = document.createElement("div");
    codeDiv.setAttribute("id", "code-div");
    codeDiv.setAttribute("class", "form-group hidden");
    var codeInput = document.createElement("input");
    codeInput.setAttribute("type", "text");
    codeInput.setAttribute("id", "code");
    codeInput.setAttribute("name", "code");
    codeInput.setAttribute("placeholder", "Code d'initialisation");
    codeInput.setAttribute("autofocus", "autofocus");
    codeInput.setAttribute("required", "required");
    codeInput.setAttribute("class", "form-control");
    codeDiv.appendChild(codeInput);
    codeDiv.innerHTML += "<br/>";
    formContent.appendChild(codeDiv);
    //create the password div and add to it password input
    var passwordDiv = document.createElement("div");
    passwordDiv.setAttribute("class", "form-group hidden");
    passwordDiv.setAttribute("id", "password-div");
    //create the  password input
    var passwordInput = document.createElement("input");
    passwordInput.setAttribute("type", "password");
    passwordInput.setAttribute("id", "password");
    passwordInput.setAttribute("name", "password");
    passwordInput.setAttribute("placeholder", "Nouveau mot de passe");
    passwordInput.setAttribute("required", "required");
    passwordInput.setAttribute("class", "form-control");
    passwordDiv.appendChild(passwordInput);
    passwordDiv.innerHTML += "<br/>";
    formContent.appendChild(passwordDiv);

    //create the confirm password div and add to it password input
    var confirmPasswordDiv = document.createElement("div");
    confirmPasswordDiv.setAttribute("class", "form-group hidden");
    confirmPasswordDiv.setAttribute("id", "confirm-password-div");
    //create the confirm password input
    var confirmPasswordInput = document.createElement("input");
    confirmPasswordInput.setAttribute("type", "password");
    confirmPasswordInput.setAttribute("id", "confirm-password");
    confirmPasswordInput.setAttribute("name", "password");
    confirmPasswordInput.setAttribute("placeholder", "Confirmez nouveau mot de passe");
    confirmPasswordInput.setAttribute("required", "required");
    confirmPasswordInput.setAttribute("class", "form-control");
    confirmPasswordDiv.appendChild(confirmPasswordInput);
    confirmPasswordDiv.innerHTML += "<br/>";
    formContent.appendChild(confirmPasswordDiv);

    //create the main button div and add it to it a button for password recovery
    var buttonDiv = document.createElement("div");
    buttonDiv.setAttribute("id", "button-div");

    var button = document.createElement("button");
    button.setAttribute("id", "btn-demand");
    button.setAttribute("class", "btn btn-primary btn-lg btn-block");
    button.setAttribute("type", "button");
    button.setAttribute("onclick", "demandCode()");
    button.style.backgroundColor = "#55aace";
    button.innerHTML = "Demander un code ";

    buttonDiv.appendChild(button);
    buttonDiv.innerHTML += "<br/>";
    formContent.appendChild(buttonDiv);

    //progress
    var progressDiv = document.createElement("div");
    progressDiv.setAttribute("class","progress hidden");
    progressDiv.setAttribute("id","progress-div");
    var bar = document.createElement("div");
    bar.setAttribute("class","progress-bar progress-bar-striped active");
    bar.setAttribute("role","progressbar");
    bar.style.width = "100%";

    progressDiv.appendChild(bar);
    formContent.appendChild(progressDiv);

    //create the lower buttons and add them to lower div
    var lowerDiv = document.createElement("div");
    lowerDiv.setAttribute("id", "lower-div");
    lowerDiv.style.marginTop = "10px";

    //ready have code
    var readyButton = document.createElement("button");
    readyButton.setAttribute("id", "btn-ready")
    readyButton.setAttribute("class", "btn-link");
    readyButton.setAttribute("type", "button");
    readyButton.setAttribute("onclick", "show()");
    readyButton.innerHTML = "Déjà reçu un code?";
    readyButton.style.marginLeft = "0px";

    //login
    var leftButton = document.createElement("button");
    leftButton.setAttribute("id", "btn-left-bottom")
    leftButton.setAttribute("class", "btn-link");
    leftButton.setAttribute("type", "button");
    leftButton.setAttribute("onclick", "setLoginContents()");
    leftButton.innerHTML = "S'authentifier";
    leftButton.style.marginRight = "90px";
    leftButton.style.marginLeft = "0px";

    //sign up

    var rightButton = document.createElement("button");
    rightButton.setAttribute("id", "btn-right-bottom");
    rightButton.setAttribute("class", "btn-link");
    rightButton.setAttribute("type", "button");
    rightButton.setAttribute("onclick", "setRegisterContent()");
    rightButton.innerHTML = "S'inscrire";
    rightButton.style.marginRight = "0px";
    rightButton.style.marginLeft = "90px";
    lowerDiv.appendChild(readyButton);
    lowerDiv.innerHTML += "<br/>";
    lowerDiv.appendChild(leftButton);
    lowerDiv.appendChild(rightButton);
    lowerDiv.innerHTML += "<br/>";
    formContent.appendChild(lowerDiv);

};


var show = function () {
    document.getElementById("code-div").setAttribute("class", "form-group show");
    document.getElementById("password-div").setAttribute("class", "form-group show");
    document.getElementById("confirm-password-div").setAttribute("class", "form-group show");
    document.getElementById("btn-ready").setAttribute("class", "btn-link hidden");
    var initButton = document.getElementById("btn-demand");
    initButton.setAttribute("class", "btn btn-primary btn-lg btn-block");
    initButton.setAttribute("onclick", "changePassword()");//change the password
    initButton.innerHTML = "Réinitialiser ";

};




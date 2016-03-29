var clearError = function () {
    if (errorTimeOut) {
        clearTimeout(errorTimeOut);
    }
    var errorLabel = document.getElementById("error");
    errorLabel.innerHTML = "";
};


var setLoginContents = function () {
    var formContent = document.getElementById("form-contents");
    var formContainer = formContent.parentNode;
    //create and add legend
    var legend = document.createElement("legend");
    legend.setAttribute("id", "legend");
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
    button.style.backgroundColor = "#55aace";
    button.onclick = doLogin();
    button.innerHTML = "S'authentifier";

    buttonDiv.appendChild(button);
    buttonDiv.innerHTML += "<br/>";
    formContent.appendChild(buttonDiv);

    //create the lower buttons and add them to lower div
    //lower div is a child of the form-container
    var lowerDiv = document.createElement("div");
    lowerDiv.setAttribute("id", "lower-div");
    lowerDiv.style.marginTop = "50px";

    var leftButton = document.createElement("button");
    leftButton.setAttribute("id", "btn-left-bottom")
    leftButton.setAttribute("class", "btn-link");
    leftButton.setAttribute("type", "button");
    leftButton.innerHTML = "S'inscrire";
    leftButton.style.marginRight = "50px";
    leftButton.style.marginLeft = "10px";
    leftButton.style.marginBottom = "10px"

    var rightButton = document.createElement("button");
    rightButton.setAttribute("id", "btn-right-bottom");
    rightButton.setAttribute("class", "btn-link");
    rightButton.setAttribute("type", "button");
    rightButton.innerHTML = "Réinitialiser mot de passe";
    rightButton.style.marginRight = "20px";
    rightButton.style.marginLeft = "70px";
    rightButton.style.marginBottom = "10px"

    lowerDiv.appendChild(leftButton);
    lowerDiv.appendChild(rightButton);
    lowerDiv.innerHTML += "<br/>";
    formContainer.appendChild(lowerDiv);

};

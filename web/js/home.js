/**
 * Created by newton on 5/29/15.
 */

var userEmail;
var type;
var sessionId;
var state=0;//0-10 main states,11-20  reclamation states
var timeOut;
var colorSuccess = "#5cb85c";
var colorError = "#d9534f";
var reclamations=[];
var students = [];
var studentsStates = [];
var themes = [];
var studentEtat = [];

var doLogout = function(){

    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
            //var jsonObj = JSON.parse(xmlHttp.responseText);
            if (true) {
                //if all went successfully redirect
                //console.log(xmlHttp.responseText);
                location.replace("/","_self");

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
    xmlHttp.open("POST", "/logout", true);
    xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttp.send(null);

};

var fetchInfo = function(){

    var params  = window.location.search.substr(1);
    sessionId = params.substr(10);

    var xmlHttp = new XMLHttpRequest();

    xmlHttp.onreadystatechange = function(){

        if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
            var jsonResponse = JSON.parse(xmlHttp.responseText);
            var user = jsonResponse.user;
            userEmail = user.email;
            type = user.type;
            //fetchReclamations();
            if(type == 2)
                showSuccess(user.firstname+" "+user.lastname," Bienvenue Professor ",colorSuccess);
            else if(type == 1)
                showSuccess(user.firstname+" "+user.lastname," Bienvenue Tuteur ",colorSuccess);
            else if(type == 0)
                showSuccess(user.firstname+" "+user.lastname," Bienvenue Etudiant ",colorSuccess);
            //
            console.log(jsonResponse);

        }
    }

    xmlHttp.open("POST","/user/info",true);
    xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlHttp.send("sessionid="+sessionId);


};

var fetchReclamations = function(){
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function(){

        if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
            var jsonResponse = JSON.parse(xmlHttp.responseText);
            if(jsonResponse["response code"] == 1){
                reclamations = jsonResponse.reclamations;
                setConsulterReclamation();
            }else{
                showSuccess(jsonResponse["response message"],"Erreur",colorError);
            }
        }

    }

    xmlHttp.open("POST","/reclamation",true);
    xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlHttp.send("sessionid="+sessionId+"&"+"action=consulter"+"&"+"email_recepteur="+userEmail);

};

window.onload = function(){
    fetchInfo();
};

var setProposerTheme = function(){
    if(state === 1){
        //the proposer content are visible
        //return
        return;
    }

    var midContainer = document.getElementById("mid-container");
    midContainer.innerHTML ="";
    //top span
    var div = document.createElement("div");
    var topSpan = document.createElement("span");
    topSpan.setAttribute("class","text-uppercase");
    topSpan.innerHTML = "Entrer un titre et description d'un thème";
    var br = document.createElement("br");
    div.appendChild(topSpan);
    midContainer.appendChild(div);
    //feedback
    var div = document.createElement("div");
    var errorSpan = document.createElement("span");
    errorSpan.setAttribute("class","text-center text-danger");
    errorSpan.setAttribute("id","feedback");
    errorSpan.style.fontWeight = "bold";
    div.appendChild(errorSpan);
    midContainer.appendChild(div);

    //set the title input
    var div = document.createElement("div");
    div.setAttribute("class","form-group");

    var label = document.createElement("label");
    label.setAttribute("for","title");
    label.setAttribute("class","text-left text-primary");
    label.innerHTML = "Titre";

    var input = document.createElement("input");
    input.setAttribute("class","input-group");
    input.setAttribute("id","title");

    div.appendChild(label);
    div.insertBefore(br,label);
    div.appendChild(input);

    midContainer.appendChild(div);

    //set the description input

    var div = document.createElement("div");
    div.setAttribute("class","form-group");

    var label = document.createElement("label");
    label.setAttribute("for","description");
    label.setAttribute("class","text-left text-primary");
    label.innerHTML = "Description";

    var textArea = document.createElement("textarea");
    textArea.setAttribute("class","input-group");
    textArea.setAttribute("id","description");
    textArea.setAttribute("rows","10");


    div.appendChild(label);
    div.appendChild(textArea);
    midContainer.appendChild(div);


    //set the submit button

    var div = document.createElement("div");
    div.setAttribute("class","pull-right");

    var button = document.createElement("button");
    button.setAttribute("class","btn btn-block btn-primary");
    button.setAttribute("onclick","submitTheme()");
    button.innerHTML = "Soumettre";

    div.appendChild(button);
    midContainer.appendChild(div);
    state = 1;

};

var setConsulterReclamation = function(){
    console.log(reclamations);
    /*if(state === 3){
        //the proposer content are visible
        //return
        return;
    }*/
    var midContainer = document.getElementById("mid-container");
    midContainer.innerHTML ="";
    //list the reclamations
    var ul = document.createElement("ul");
    ul.setAttribute("class","list-group");
    var item;
    var li;
    for(var i=0;i<reclamations.length;++i){
        item = reclamations[i];
        li = document.createElement("li");
        if(item.read_status == 0){
            li.setAttribute("class","reclamation-non-read list-group-item btn-link");
        }else{
            li.setAttribute("class","reclamation-read list-group-item btn-link");
        }
        li.setAttribute("id","reclamation-"+i);
        li.innerHTML =
            "De : "+item.fullname+"<br/> "+
            "E-mail : "+item.email_sender +"<br/>"+
            "Date : "+item.date+"<br/>";
        li.setAttribute("onclick","readReclamation("+i+")");
        ul.appendChild(li);
    }
    midContainer.appendChild(ul);

    state = 0;
};

var updateFeedback = function( message,time){
    var feedback = document.getElementById("feedback");
    timeOut = setTimeout(function(){
        feedback.innerHTML = "";
    },time);
    feedback.innerHTML = message;

};

var submitTheme = function(){
    var title = document.getElementById("title").value;
    var description = document.getElementById("description").value;

    if(title == "" || description =="" || title.length <10 || description.length < 20){
        updateFeedback("Le thème contient des informations invalides..!",1500);
        return;
    }

    //send the theme to the server
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function(){

        if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
            var obj = JSON.parse(xmlHttp.responseText);
            var responseCode = obj["response code"];
            if(responseCode === 1){
                showSuccess(obj["response message"],"Succès",colorSuccess);
            }else if(responseCode === 2){
                //showSuccess(obj["response message"],"Erreur",colorError);
                updateFeedback(obj["response message"],2000);

            }else{
                showSuccess(obj["response message"],"Erreur",colorError);

            }


        }

    };

    xmlHttp.open("POST","/manage-theme",true);
    xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlHttp.send("action=add"+"&"+"type="+type+"&"+"email="+userEmail+"&"+"title="+title+"&"+"description="+description);

};

var showSuccess = function(message,headerContent,jumboColor){

     var midContainer = document.getElementById("mid-container");
     midContainer.innerHTML="";
     //jumbotron
     var jumbo = document.createElement("div");
     jumbo.setAttribute("class","jumbotron");
     jumbo.style.backgroundColor =jumboColor;

     //container
     var container = document.createElement("div");
     container.setAttribute("class","container");

     jumbo.appendChild(container);

     var header = document.createElement("h2");
     header.style.color="#ffffff";
     header.innerHTML=headerContent;
     container.appendChild(header);

     var paragraph = document.createElement("p");
     paragraph.style.color ="#ffffff";
     paragraph.style.fontSize ="medium";
     paragraph.style.fontWeight="normal";
     paragraph.innerHTML = message;

     container.appendChild(paragraph);

     midContainer.appendChild(jumbo);
     state = 0;
     // var br = document.createElement("br");
 };


/** Created by Philip
 *  on 3 June 2015
 */
//Read Reclammation
var readReclamation = function(i){
    var reclamation = reclamations[i];
    var midContainer = document.getElementById("mid-container");
    midContainer.innerHTML="";
    //jumbotron
    var jumbo = document.createElement("div");
    jumbo.setAttribute("class","jumbotron");
    jumbo.style.backgroundColor ="#bc8f8f";

    //container
    var container = document.createElement("div");
    container.setAttribute("class","container");
    jumbo.appendChild(container);

    var from = document.createElement("h5");
    from.style.color="#ffffff";
    from.innerHTML= "De : " + reclamation.fullname; //FROM
    container.appendChild(from);

    var email = document.createElement("h5");
    email.style.color="#ffffff";
    email.innerHTML= " Email : "+ reclamation.email_sender ; //EMAIL
    container.appendChild(email);

    var date = document.createElement("h5");
    date.style.color="#ffffff";
    date.innerHTML="Le : "+ reclamation.date; //DATE
    container.appendChild(date);

    var hr = document.createElement("hr");
    container.appendChild(hr);

    var hr = document.createElement("hr");
    container.appendChild(hr);

    //Description
    var paragraph = document.createElement("p");
    paragraph.style.fontSize ="medium";
    paragraph.style.fontWeight="normal";
    paragraph.innerHTML = reclamation.description; //Description

    container.appendChild(paragraph);

    midContainer.appendChild(jumbo);
    state = 0;

}

//change read status
var changeStatus = function(email){
    //Add code here
}

var setupModifyProfile = function (){
    //Check State if already visible
    if(state == 2){
        return; //Already Visible
    }

    //Set up Structures ; not visible
    var midContainer = document.getElementById("mid-container");
    midContainer.innerHTML =""; //Empty content

    //top span
    var div = document.createElement("div");
    var topSpan = document.createElement("span");
    topSpan.setAttribute("class","text-uppercase");
    topSpan.style.font.bold;
    topSpan.innerHTML = "Remplir les Champs a Changer. ";
    //var br = document.createElement("br");

    var br = document.createElement("br");
    div.appendChild(topSpan);
    div.appendChild(br);
    midContainer.appendChild(div);


    //feedback
    var div = document.createElement("div");
    var errorSpan = document.createElement("span");
    errorSpan.setAttribute("class","text-center text-danger");
    errorSpan.setAttribute("id","feedback");
    errorSpan.style.fontWeight = "bold";

    div.appendChild(errorSpan);
    midContainer.appendChild(div);

    //Form Group
    var divForm = document.createElement("div");
    divForm.setAttribute("class","form-group");
    divForm.setAttribute("id","modifyForm");

    //Password
    var passColumn = document.createElement("div");
    var inputPass = document.createElement("input");
    inputPass.setAttribute("class","form-control");
    inputPass.setAttribute("id","password");
    inputPass.setAttribute("type","password");
    inputPass.setAttribute("placeholder","Password");
    inputPass.setAttribute("autocomplete","on");
    inputPass.setAttribute("autofocus","");

    //Add Email to emailColumn
    var br = document.createElement("br");
    passColumn.appendChild(inputPass);
    passColumn.appendChild(br);

    //Add to Email Row & to form Group
    divForm.appendChild(passColumn);

    //name Column
    var nameColumn = document.createElement("div");
    var inputName = document.createElement("input");
    inputName.setAttribute("class","form-control");
    inputName.setAttribute("id","name");
    inputName.setAttribute("type","text");
    inputName.setAttribute("placeholder","Prenom");
    inputName.setAttribute("autocomplete","on");
    inputName.setAttribute("autofocus","");

    //Add name to nameColumn
    var br = document.createElement("br");
    nameColumn.appendChild(inputName);
    nameColumn.appendChild(br);

    //Add to name Row & to form Group
    divForm.appendChild(nameColumn);


    //Name Column
    var lastnameColumn = document.createElement("div");
    var inputlastName = document.createElement("input");
    inputlastName.setAttribute("class","form-control");
    inputlastName.setAttribute("id","lastname");
    inputlastName.setAttribute("type","text");
    inputlastName.setAttribute("placeholder","Nom");
    inputlastName.setAttribute("autocomplete","on");
    inputlastName.setAttribute("autofocus","");

    //Add name to lastNameColumn
    var br = document.createElement("br");
    lastnameColumn.appendChild(inputlastName);
    lastnameColumn.appendChild(br);

    //Add to Name Row form Group
    divForm.appendChild(lastnameColumn);

    //Address Field
    var addressColumn = document.createElement("div");
    var inputAddress = document.createElement("input");
    inputAddress.setAttribute("class","form-control");
    inputAddress.setAttribute("id","address");
    inputAddress.setAttribute("type","text");
    inputAddress.setAttribute("placeholder","Addresse");
    inputAddress.setAttribute("autocomplete","on");
    inputAddress.setAttribute("autofocus","");

    //Add address to addressColumn
    var br = document.createElement("br");
    addressColumn.appendChild(inputAddress);
    addressColumn.appendChild(br);

    //Add to Address  form Group
    divForm.appendChild(addressColumn);

    //Telephone Column
    var telColumn = document.createElement("div");
    var inputTel = document.createElement("input");
    inputTel.setAttribute("class","form-control");
    inputTel.setAttribute("id","tel");
    inputTel.setAttribute("type","text");
    inputTel.setAttribute("placeholder","Telephone");
    inputTel.setAttribute("autocomplete","on");
    inputTel.setAttribute("autofocus","");

    //Add Telephone to TelephoneColumn
    var br = document.createElement("br");
    telColumn.appendChild(inputTel);
    telColumn.appendChild(br);

    //Add to Telephone  form Group
    divForm.appendChild(telColumn);

    //Sex Column
    var sexColumn = document.createElement("div");
    //Radio Buttons for Sexe
    var label = document.createElement("label");
    label.innerHTML = "  Homme";

    //Male input radiobutton
    var inputMale = document.createElement("input");
    inputMale.setAttribute("id","sexM");
    inputMale.setAttribute("type","radio");
    inputMale.setAttribute("name","sex");
    inputMale.setAttribute("value","M");
    inputMale.setAttribute("checked","");
    //
    //Add to column
    sexColumn.appendChild(label);
    sexColumn.appendChild(inputMale);

    //Radio Buttons for Sexe
    var label = document.createElement("label");
    label.innerHTML = "   Femme";

    //Female input radiobutton
    var inputFem = document.createElement("input");
    inputFem.setAttribute("id","sexF");
    inputFem.setAttribute("type","radio");
    inputFem.setAttribute("name","sex");
    inputFem.setAttribute("value","F");
    //
    //Add to column
    sexColumn.appendChild(label);
    sexColumn.appendChild(inputFem);
    var br = document.createElement("br");
    sexColumn.appendChild(br);

    divForm.appendChild(sexColumn);


    //Login Button
    var rowModify = document.createElement("div");
    var inputLoginNode = document.createElement("input");
    inputLoginNode.className ="btn btn-primary pull-right";
    inputLoginNode.setAttribute("id","modifyBtn");
    inputLoginNode.setAttribute("type","button");
    inputLoginNode.setAttribute("onclick","modifyRequest();");
    inputLoginNode.setAttribute("value","Soumettre");

    //
    var br = document.createElement("br");
    rowModify.appendChild(inputLoginNode);
    rowModify.appendChild(br);

    //Add to Form Group and to Mid Container
    divForm.appendChild(rowModify);
    midContainer.appendChild(divForm);

    state = 2;
}

//Handle for the Modification Request
var modifyRequest = function() {
    console.log("Called modifyReq ");
    //Get Fields and Check Fields Requested
    var tel      = document.getElementById("tel").value;
    var name     = document.getElementById("name").value;
    var lastname = document.getElementById("lastname").value;
    var address  = document.getElementById("address").value;
    var password = document.getElementById("password").value;
    var sex      = document.getElementById("sexM").value;


    if(!(document.getElementById("sexM").checked)){
        sex = "F";
    }

    //Check Tel
    if(!(tel == "")){
        if(!checkTel(tel)){
            updateFeedback("Tel est invalide!", 2000);
            return;
        }
    }
    //Check address field
    if(!(address == "")){
        if(address.length < 2 || address.length > 30){
            updateFeedback("Adresse est invalide!", 2000);
            return;
        }
    }

    //Check Password
    if(!(password == "")) {
        if(password.length < 8) {
            updateFeedback("Mot de Pass doit etre au moins 8 mots !", 2000);
            return;
        }else {
            //hash password before sending
            password = hashPassword(password);
        }
    }

    //Check name fields
    if(!(lastname == "")){
        if(!checkNames(lastname)){
            updateFeedback("Nom est invalide!", 2000);
            return;
        }
    }

    if(!(name =="")){
        if(!checkNames(name)){
            updateFeedback("Prenom est invalide!", 2000);
            return;
        }
    }

    //All fields non-empty were validated

    //Send Request to Server
    //console.log(email+" "+password+" "+name+" " +lastname+" "+address+" "+sex+" "+tel);
    sendModifyRequest(password,name,lastname,address,sex,tel);

}

var sendModifyRequest = function(password,name,lastname,address,sex,tel) {
    var xmlHttp = new XMLHttpRequest();

    xmlHttp.onreadystatechange = function () {

        if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
            try {
                var obj = JSON.parse(xmlHttp.responseText);
            }catch(e){
                showSuccess("Response Erreur ; Unknown Object","Erreur",colorError);
            }
            var responseCode = obj["response code"];
            if(responseCode === 1){
                showSuccess(obj["response message"],"Succès",colorSuccess);
                updateInfo();
            }else if(responseCode === 2){
                updateFeedback(obj["response message"],3000);
            }else{
                showSuccess(obj["response message"],"Erreur",colorError);

            }
        }
    };
    try {
        //Open Connection
        xmlHttp.open("POST", "/manage-profile", true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.send("action=update" +"&"
        + "password=" + password + "&" + "name=" + name + "&" + "lastName=" + lastname + "&"
        + "address=" + address + "&" + "sex=" + sex + "&" + "tel=" + tel + "&" + "oldEmail=" + userEmail+"&"
        + "userType="+type);
    }catch(e){
        showSuccess("Server code 500","Erreur",colorError);
    }
}

//Update Info
var updateInfo = function(){
    var params  = window.location.search.substr(1);
    sessionId = params.substr(10);

    var xmlHttp = new XMLHttpRequest();

    xmlHttp.onreadystatechange = function(){

        if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
            var jsonResponse = JSON.parse(xmlHttp.responseText);
            var user = jsonResponse.user;
            userEmail = user.email;
            type = user.type;
        }
    }

    xmlHttp.open("POST","/user/info",true);
    xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlHttp.send("sessionid="+sessionId);
}

//Tel Validator
var checkTel= function(tel){

    try {
        if (checkInternationalPhone(tel)) {
            tel.focus;
            return true;
        } else {
            return false;
        }
    }catch(e){
        return false;
    }
}

//Name field Validator
var checkNames = function(name) {
    var regex = /^[a-zA-Z ]{2,30}$/;

    if (regex.test(name)) {
        return true;
    }
    else {
        return false;
    }
}

//Encrypt Password
var hashPassword = function(password){
    return CryptoJS.SHA256(password);
};

/**
 * DHTML phone number validation script. Courtesy of SmartWebby.com (http://www.smartwebby.com/dhtml/)
 */

// Declaring required variables
var digits = "0123456789";
// non-digit characters which are allowed in phone numbers
var phoneNumberDelimiters = "()- ";
// characters which are allowed in international phone numbers
// (a leading + is OK)
var validWorldPhoneChars = phoneNumberDelimiters + "+";
// Minimum no of digits in an international phone no.
var minDigitsInIPhoneNumber = 10;

function isInteger(s)
{   var i;
    for (i = 0; i < s.length; i++)
    {
        // Check that current character is number.
        var c = s.charAt(i);
        if (((c < "0") || (c > "9"))) return false;
    }
    // All characters are numbers.
    return true;
}
function trim(s)
{   var i;
    var returnString = "";
    // Search through string's characters one by one.
    // If character is not a whitespace, append to returnString.
    for (i = 0; i < s.length; i++)
    {
        // Check that current character isn't whitespace.
        var c = s.charAt(i);
        if (c != " ") returnString += c;
    }
    return returnString;
}
function stripCharsInBag(s, bag)
{   var i;
    var returnString = "";
    // Search through string's characters one by one.
    // If character is not in bag, append to returnString.
    for (i = 0; i < s.length; i++)
    {
        // Check that current character isn't whitespace.
        var c = s.charAt(i);
        if (bag.indexOf(c) == -1) returnString += c;
    }
    return returnString;
}

function checkInternationalPhone(strPhone){
    var bracket=3
    strPhone=trim(strPhone)
    if(strPhone.indexOf("+")>1) return false
    if(strPhone.indexOf("-")!=-1)bracket=bracket+1
    if(strPhone.indexOf("(")!=-1 && strPhone.indexOf("(")>bracket)return false
    var brchr=strPhone.indexOf("(")
    if(strPhone.indexOf("(")!=-1 && strPhone.charAt(brchr+2)!=")")return false
    if(strPhone.indexOf("(")==-1 && strPhone.indexOf(")")!=-1)return false
    s=stripCharsInBag(strPhone,validWorldPhoneChars);
    return (isInteger(s) && s.length >= minDigitsInIPhoneNumber);
}
//Students

//fetchStudents
var fetchStudents = function() {
    var xmlHttp = new XMLHttpRequest();

    //Prepare stateChange Handle
    xmlHttp.onreadystatechange = function(){

        if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
            //Everyting went well
            //Get object
            try {
                var obj = JSON.parse(xmlHttp.responseText);
            }catch(e){
                showSuccess("Response Erreur ; Unknown Object","Erreur",colorError);
            }
            //Check states
            var responseCode = obj["response code"];
            if(responseCode === 1){
                students = obj.students;
                //console.log(students);
                setupMentionnerNote();
            }else if(responseCode === 2){
                updateFeedback(obj["response message"],3000);
            }else{
                showSuccess(obj["response message"],"Erreur",colorError);
            }
        }
    }
    try {
        xmlHttp.open("POST", "/manage-student", true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.send("action=fetchStudents" + "&" + "email=" + userEmail + "&" + "type=" + type);
    }catch(e){
        showSuccess("Server code 500","Erreur",colorError);
    }


};

//set up Note Structure
var setupNote = function(i) {

    var div = document.createElement("div");
    div.setAttribute("class","row");
    var col1 = document.createElement("div");
    var col2 = document.createElement("div");
    var col3 = document.createElement("div");

    col1.setAttribute("class","col-lg-4 col-md-4 pull-left");
    col2.setAttribute("class","col-lg-5 col-md-5");
    col3.setAttribute("class","col-lg-3 col-md-3");

    var form  = document.createElement("div");
    form.setAttribute("class","form-group");
    var input = document.createElement("input");
    input.setAttribute("class","form-control");
    input.setAttribute("id","note"+i);
    input.setAttribute("placeholder","Note");
    input.setAttribute("type","text");
    input.setAttribute("autofocus","");
    form.appendChild(input);
    col1.appendChild(form);
    //
    //
    var input = document.createElement("input");
    input.setAttribute("class","btn btn-block btn-primary");
    input.setAttribute("id","affecterBtn"+i);
    input.setAttribute("onclick","mentionner("+i+");");
    input.setAttribute("type","button");
    input.setAttribute("autofocus","");
    input.setAttribute("autocomplete","on");
    input.setAttribute("value","Mentionner");
    col3.appendChild(input);

    div.appendChild(col1);
    div.appendChild(col2);
    div.appendChild(col3);

    return div;
}

//Set up Mentionner Section
var setupMentionnerNote = function() {
    //Check State if already visible
    if(state == 3){
        return; //Already Visible
    }
    console.log("Mentionner Note");
    //Set up Structures ; not visible
    var midContainer = document.getElementById("mid-container");
    midContainer.innerHTML =""; //Empty content

    //top span
    var topSpan = document.createElement("span");
    topSpan.setAttribute("class","text-uppercase");
    topSpan.style.font.bold;
    topSpan.innerHTML = " Les Etudiants concernés ";

    //feedback
    var divFeed = document.createElement("div");
    var errorSpan = document.createElement("span");
    errorSpan.setAttribute("class","text-center text-danger");
    errorSpan.setAttribute("id","feedback");
    errorSpan.style.fontWeight = "bold";

    divFeed.appendChild(errorSpan);


    //Set up the Panel Group
    var panelGrp = document.createElement("div");
    panelGrp.setAttribute("id","panelParent");
    panelGrp.setAttribute("class","panel-group");
    var student;
    for(var i=0; i < students.length; ++i) {
        //Get Student Object
        student = students[i];
        var panel = document.createElement("div");
        panel.setAttribute("class", "panel panel-success");
        panel.setAttribute("panel","p"+i);
        panel.setAttribute("active","false");
        //
        var panelTitle = document.createElement("div");
        panelTitle.setAttribute("class", "panel-heading");
        var h5 = document.createElement("h5");
        h5.setAttribute("class", "panel-title");
        var a = document.createElement("a");
        a.setAttribute("data-parent", "panelParent");
        a.setAttribute("data-toggle", "collapse");
        a.setAttribute("email",student.email_etudiant);
        a.setAttribute("id","a"+i);
        a.setAttribute("href", "#"+i); //refs body id
        //Set Student Information
        a.innerHTML = student.nom +" "+student.prenom+" ";
        //a.innerHTML = " Student "+i;
        //
        h5.appendChild(a);
        panelTitle.appendChild(h5);

        //Panel Body
        var body = document.createElement("div");
        body.setAttribute("class", "panel-collapse collapse out");
        body.setAttribute("id",i);
        var content = document.createElement("div");
        content.setAttribute("class", "panel-body");
        //create Button and Note Field
        content.appendChild(setupNote(i));
        body.appendChild(content);

        //
        panel.appendChild(panelTitle);
        panel.appendChild(body);

        //Add panel to panel Grp
        panelGrp.appendChild(panel);
    }

    //Add to the Mid Container
    midContainer.appendChild(topSpan);
    midContainer.appendChild(divFeed);
    var hr = document.createElement("hr");
    midContainer.appendChild(hr);
    midContainer.appendChild(panelGrp);

    //
    state = 3;
}

//Verify Fields
var mentionner = function(i) {
    var note  = document.getElementById("note"+i).value;
    var a = document.getElementById("a"+i);
    var email = a.getAttribute("email");

    if(!(note == "")){
        if(!checkNote(note)){
            updateFeedback("Note de  "+email +" est invalide! ",3000);
            return;
        }
    }else{
        updateFeedback("Champ vide : On peut pas mentionner ! ",3000);
        return;
    }
        //All fields were valid : Send Request
        console.log("Email Selected :" + email + " NOte :" + note);
        sendMentionner(email,note);

}

//Verify Number Entered
var checkNote = function(note){

    var filter = /^[0-9]+(\.[0-9]{1,2})?$/;

    if (!filter.test(note) || isInteger(note)) {
        note.focus;
        return false;
    }

    if(note < 0 || note > 20)
        return false;

    return true;
}

//Verify Phase
var checkPhase = function(phase){
    if(phase < 1 || phase > 4){
        return false;
    }
    return true;
}

//Send Mentionner Request
var sendMentionner = function(email,note) {
    console.log("called mentionner");
    //Prepare stateChange Handle
    var xmlHttp = new XMLHttpRequest();

    xmlHttp.onreadystatechange = function () {

        if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
            try {
                var obj = JSON.parse(xmlHttp.responseText);

            var responseCode = obj["response code"];
            if(responseCode === 1){
                showSuccess(obj["response message"],"Succès",colorSuccess);
            }else if(responseCode === 2){
                updateFeedback(obj["response message"],3000);
            }else{
                showSuccess(obj["response message"],"Erreur",colorError);

            }
            }catch(e){
                showSuccess("Response Erreur : Unknown Object","Erreur",colorError);
            }
        }
    };
    try {
        xmlHttp.open("POST", "/manage-student", true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.send("action=mentionner" + "&" + "email=" + email + "&" + "type=" + type+
        "&"+"note="+note);
    }catch(e){
        showSuccess("Server code 500","Erreur",colorError);
    }

}

//Student State Consultation
var fetchEtat = function() {
    var xmlHttp = new XMLHttpRequest();

    //Prepare stateChange Handle
    xmlHttp.onreadystatechange = function(){

        if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
            //Everyting went well
            //Get object
            try {
                var obj = JSON.parse(xmlHttp.responseText);
            }catch(e){
                showSuccess("Response Erreur ; Unknown Object","Erreur",colorError);
            }
            //Check states
            var responseCode = obj["response code"];
            if(responseCode === 1){
                studentsStates = obj.students;
                setupSuivreEtat();
            }else if(responseCode === 2){
                updateFeedback(obj["response message"],3000);
            }else{
                showSuccess(obj["response message"],"Erreur",colorError);
            }
        }
    }
    try {
        xmlHttp.open("POST", "/manage-student", true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.send("action=fetchEtat"+"&"+"email="+userEmail+"&"+"type="+type);
    }catch(e){
        showSuccess("Server code 500","Erreur",colorError);
    }
}

//Structure for Suivre Etat
var setupSuivreEtat = function() {
    //Check State if already visible
    if(state == 4){
        return; //Already Visible
    }

    //Set up Structures ; not visible
    var midContainer = document.getElementById("mid-container");
    midContainer.innerHTML =""; //Empty content

    //top span
    var topSpan = document.createElement("span");
    topSpan.setAttribute("class","text-uppercase");
    topSpan.style.font.bold;
    topSpan.innerHTML = " LES ETATS DES VOS ETUDIANTS ";

    //feedback
    var divFeed = document.createElement("div");
    var errorSpan = document.createElement("span");
    errorSpan.setAttribute("class","text-center text-danger");
    errorSpan.setAttribute("id","feedback");
    errorSpan.style.fontWeight = "bold";

    divFeed.appendChild(errorSpan);


    //Set up the Panel Group
    var panelGrp = document.createElement("div");
    panelGrp.setAttribute("id","panelParent");
    panelGrp.setAttribute("class","panel-group");
    //
    var student;
    var phases = [];
    //
    if(studentsStates.length == 0){
        showSuccess(" List Vide ! ","Erreur",colorError);
        return;
    }
    for(var i=0; i < studentsStates.length; ++i) {
        //Get Student Object
        student = studentsStates[i];
        phases  = student.phases;

        var panel = document.createElement("div");
        panel.setAttribute("class", "panel panel-primary");
        panel.setAttribute("style","background-color: #d58512");
        panel.setAttribute("panel","p"+i);
        panel.setAttribute("active","false");
        //
        var panelTitle = document.createElement("div");
        panelTitle.setAttribute("class", "panel-heading");
        var h5 = document.createElement("h5");
        h5.setAttribute("class", "panel-title");
        var a = document.createElement("a");
        a.setAttribute("data-parent", "panelParent");
        a.setAttribute("data-toggle", "collapse");
        a.setAttribute("email",student.email_etudiant);
        a.setAttribute("id","a"+i);
        a.setAttribute("href", "#"+i); //refs body id
        //Set Student Information
        a.innerHTML = student.nom +" "+student.prenom+" ";
        //a.innerHTML = " Student "+i;
        //
        h5.appendChild(a);
        panelTitle.appendChild(h5);

        //Panel Body
        var body = document.createElement("div");
        body.setAttribute("class", "panel-collapse collapse out");
        body.setAttribute("id",i);
        var content = document.createElement("div");
        content.setAttribute("class", "panel-body");
        //create Table for Etat
        content.appendChild(setupEtat(phases));
        body.appendChild(content);

        //
        panel.appendChild(panelTitle);
        panel.appendChild(body);

        //Add panel to panel Grp
        panelGrp.appendChild(panel);
    }

    //Add to the Mid Container
    midContainer.appendChild(topSpan);
    midContainer.appendChild(divFeed);
    var hr = document.createElement("hr");
    midContainer.appendChild(hr);
    midContainer.appendChild(panelGrp);

    //
    state = 4;

}

//Table for States
var setupEtat = function(phases) {
    var div = document.createElement("div");
    var table = document.createElement("table");
    table.setAttribute("class","table table-hover table-responsive");
    //
    var thead = document.createElement("thead");
    thead.setAttribute("class","alert-danger table-bordered");
    //header
    var tr = document.createElement("tr");
    var phaseTitle = document.createElement("td");
    var text = document.createTextNode("Phase");
    phaseTitle.appendChild(text);
    var noteEns    = document.createElement("td");
    var text = document.createTextNode("Note-Enseignant");
    noteEns.appendChild(text);
    var noteTut    = document.createElement("td");
    var text = document.createTextNode("Note-Tutuer");
    noteTut.appendChild(text);
    tr.appendChild(phaseTitle);
    tr.appendChild(noteEns);
    tr.appendChild(noteTut);
    thead.appendChild(tr);
    var tbody = document.createElement("tbody");
    //Body
    for ( var i = 0; i < phases.length ; ++i) {

        var tr = document.createElement("tr");
        var tdPhase = document.createElement("td");
        //var tdId    = document.createElement("td");
        var tdNoteEns = document.createElement("td");
        var tdNoteTut = document.createElement("td");
        //
        //Add content : loop through Phases (1-4)
        tdPhase.innerHTML = i+1;
        tdNoteEns.innerHTML = phases[i].note_ens;
        tdNoteTut.innerHTML = phases[i].note_tut;
        tr.appendChild(tdPhase);
        tr.appendChild(tdNoteEns);
        tr.appendChild(tdNoteTut);
        tbody.appendChild(tr);
    }
    //End
    //Append body and Head to table
    table.appendChild(thead);
    table.appendChild(tbody);
    div.appendChild(table);

    return div;
}

//Deposer Reclamation Handlers

//setUp Reclamation
var setupReclamation = function() {

    if(state === 5){
        //the proposer content are visible
        //return
        return;
    }

    //Set up Structures ; not visible
    var midContainer = document.getElementById("mid-container");
    midContainer.innerHTML =""; //Empty content

    //feedback
    var divFeed = document.createElement("div");
    //
    var errorSpan = document.createElement("span");
    errorSpan.setAttribute("class","text-center text-danger");
    errorSpan.setAttribute("id","feedback");
    errorSpan.style.fontWeight = "bold";
    //
    divFeed.appendChild(errorSpan);
    midContainer.appendChild(divFeed);
    //Set up Email Sender Box
    var div = document.createElement("div");
    //
    var br = document.createElement("br");

    var label = document.createElement("label");
    label.setAttribute("for","emailSender");
    label.setAttribute("class","text-left text-primary");
    label.innerHTML = " Email ( Enseignant ou Tutuer ) ";

    var emailInput = document.createElement("input");
    emailInput.setAttribute("class","form-control");
    emailInput.setAttribute("id","emailSender");
    emailInput.setAttribute("type","text");
    emailInput.setAttribute("placeholder","Email");
    emailInput.setAttribute("autocomplete","on");
    emailInput.setAttribute("autofocus","");
    //
    div.appendChild(label);
    div.insertBefore(br,label);
    div.appendChild(emailInput);
    //
    midContainer.appendChild(div);


    //Description Box

    var div = document.createElement("div");
    div.setAttribute("class","form-group");

    var label = document.createElement("label");
    label.setAttribute("for","description");
    label.setAttribute("class","text-left text-primary");
    label.innerHTML = "Description";

    var textArea = document.createElement("textarea");
    textArea.setAttribute("class","input-group");
    textArea.setAttribute("id","description");
    textArea.setAttribute("rows","8");


    div.appendChild(label);
    div.appendChild(textArea);
    midContainer.appendChild(div);
    //
    midContainer.appendChild(setupNoteRec());

    //
    state = 5;
}

var setupNoteRec = function() {

    var div = document.createElement("div");
    div.setAttribute("class","row");
    var col1 = document.createElement("div");
    var col2 = document.createElement("div");
    var col3 = document.createElement("div");

    col1.setAttribute("class","col-lg-4 col-md-4 col-sm-4 pull-left");
    col2.setAttribute("class","col-lg-5 col-md-5 col-sm-5");
    col3.setAttribute("class","col-lg-3 col-md-3 col-sm-3");

    var form  = document.createElement("div");
    form.setAttribute("class","form-group");
    var input = document.createElement("input");
    input.setAttribute("class","form-control");
    input.setAttribute("id","idEtat");
    input.setAttribute("placeholder","ID_ NOTE");
    input.setAttribute("type","text");
    input.setAttribute("autofocus","");
    form.appendChild(input);
    col1.appendChild(form);
    //
    //
    var input = document.createElement("input");
    input.setAttribute("class","btn btn-block btn-primary");
    input.setAttribute("id","reclamationBtn");
    input.setAttribute("onclick","sendReclamation();");
    input.setAttribute("type","button");
    input.setAttribute("autofocus","");
    input.setAttribute("autocomplete","on");
    input.setAttribute("value","Réclammer");
    col3.appendChild(input);

    div.appendChild(col1);
    div.appendChild(col2);
    div.appendChild(col3);

    return div;
}

var sendReclamation = function(){
    //validate fields
    var idNote = document.getElementById("idEtat").value;
    var email  = document.getElementById("emailSender").value;
    var descrip = document.getElementById("description").value;
    var regex = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    var filter = /^[0-9]+(\.[0-9]{1,2})?$/;

    if(!(regex.test(email)) || email == ""){
        updateFeedback("Addresse email Invalide!  ",3000);
        return;
    }

    if(!filter.test(idNote) || !(isInteger(idNote)) || descrip.length < 10 || descrip == ""){
        updateFeedback("Un champe Invalide! Verifiez bien SVP  ",3000);
        return;
    }

    //All fields were okay
    var xmlHttp = new XMLHttpRequest();

    //Prepare stateChange Handle
    xmlHttp.onreadystatechange = function(){

        if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
            //Everyting went well
            //Get object
            try {
                var obj = JSON.parse(xmlHttp.responseText);
            }catch(e){
                showSuccess("Response Erreur ; Unknown Object","Erreur",colorError);
            }
            //Check states
            var responseCode = obj["response code"];
            if(responseCode === 1){
                showSuccess(obj["response message"],"Succés",colorSuccess);
            }else if(responseCode === 2){
                updateFeedback(obj["response message"],3000);
            }else{
                showSuccess(obj["response message"],"Erreur",colorError);
            }
        }
    }
    try {
        xmlHttp.open("POST", "/reclamation", true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.send("action=submit"+"&"+"email_sender="+userEmail+"&"+"type="+type
        +"&"+"description="+descrip+"&"+"email_recepteur="+email+"&"+"id_etat="+idNote);
    }catch(e){
        showSuccess("Server code 500","Erreur",colorError);
    }

}

var setupChoisirTheme = function() {

    //set up panels that have theme information
    //Check State if already visible
    if(state == 6){
        return; //Already Visible
    }

    //Set up Structures ; not visible
    var midContainer = document.getElementById("mid-container");
    midContainer.innerHTML =""; //Empty content

    //top span
    var topSpan = document.createElement("span");
    topSpan.setAttribute("class","text-uppercase");
    topSpan.style.font.bold;
    topSpan.innerHTML = " LES THEMES PROPOSES PAR LES RESPONSABLE";

    //feedback
    var divFeed = document.createElement("div");
    var errorSpan = document.createElement("span");
    errorSpan.setAttribute("class","text-center text-danger");
    errorSpan.setAttribute("id","feedback");
    errorSpan.style.fontWeight = "bold";

    divFeed.appendChild(errorSpan);


    //Set up the Panel Group
    var panelGrp = document.createElement("div");
    panelGrp.setAttribute("id","panelParent");
    panelGrp.setAttribute("class","panel-group");
    //
    var theme;
    //
    if(themes.length == 0){
        showSuccess("Pas de themes Proposes; Patientez ","Erreur",colorError);
        return;
    }
    for(var i=0; i < themes.length; ++i) {
        //Get Student Object
        theme = themes[i];


        var panel = document.createElement("div");
        panel.setAttribute("class", "panel panel-primary");
        panel.setAttribute("style","background-color: #d58512");
        panel.setAttribute("panel","p"+i);
        panel.setAttribute("active","false");
        //
        var panelTitle = document.createElement("div");
        panelTitle.setAttribute("class", "panel-heading");
        var h5 = document.createElement("h5");
        h5.setAttribute("class", "panel-title");
        var a = document.createElement("a");
        a.setAttribute("data-parent", "panelParent");
        a.setAttribute("data-toggle", "collapse");
        a.setAttribute("id","a"+i);
        a.setAttribute("href", "#"+i); //refs body id
        //Set Theme Information
        a.innerHTML =" id : "+theme.id+"   Titre : "+ theme.titre ;
        //
        h5.appendChild(a);
        panelTitle.appendChild(h5);

        //Panel Body
        var body = document.createElement("div");
        body.setAttribute("class", "panel-collapse collapse out");
        body.setAttribute("id",i);
        var content = document.createElement("div");
        content.setAttribute("class", "panel-body");
        //create Table for Etat
        content.appendChild(setupTheme(theme.description));
        body.appendChild(content);
        //
        panel.appendChild(panelTitle);
        panel.appendChild(body);

        //Add panel to panel Grp
        panelGrp.appendChild(panel);
    }

    //Add to the Mid Container
    midContainer.appendChild(topSpan);
    midContainer.appendChild(divFeed);
    var hr = document.createElement("hr");
    midContainer.appendChild(hr);
    midContainer.appendChild(panelGrp);
    //
    //Add button and choice fields here
    midContainer.appendChild(setupChoices());
    //
    state = 6;

}

//set up the Theme description
var setupTheme = function(description){
    var div = document.createElement("div");
    //
    var label = document.createElement("label");
    label.setAttribute("for","description");
    label.setAttribute("class","text-left text-primary");
    label.innerHTML = "Description";

    var textArea = document.createElement("textarea");
    textArea.setAttribute("class","input-group");
    textArea.setAttribute("id","descriptionTheme");
    textArea.setAttribute("rows","8");
    textArea.innerHTML = description;

    div.appendChild(label);
    div.appendChild(textArea);

    return div;

}

//set up choices fields
var setupChoices = function(){

    var div = document.createElement("div");
    div.setAttribute("class","row");
    var col1 = document.createElement("div");
    var col2 = document.createElement("div");
    var col3 = document.createElement("div");
    var col4 = document.createElement("div");
    //
    col1.setAttribute("class","col-lg-3 col-md-3 cols-sm-3 cols-xs-3");
    col2.setAttribute("class","col-lg-3 col-md-3 cols-sm-3 cols-xs-3");
    col3.setAttribute("class","col-lg-3 col-md-3 cols-sm-3 cols-xs-3");
    col4.setAttribute("class","col-lg-3 col-md-3 cols-sm-3 cols-xs-3");

    var form  = document.createElement("div");
    form.setAttribute("class","form-group");
    var input = document.createElement("input");
    input.setAttribute("class","form-control");
    input.setAttribute("id","choix1");
    input.setAttribute("placeholder","Choix 1");
    input.setAttribute("type","text");
    input.setAttribute("autofocus","");
    form.appendChild(input);
    col1.appendChild(form);
    //
    //
    var form  = document.createElement("div");
    form.setAttribute("class","form-group");
    var input = document.createElement("input");
    input.setAttribute("class","form-control");
    input.setAttribute("id","choix2");
    input.setAttribute("placeholder","Choix 2");
    input.setAttribute("type","text");
    input.setAttribute("autofocus","");
    form.appendChild(input);
    col2.appendChild(form);
    //
    var form  = document.createElement("div");
    form.setAttribute("class","form-group");
    var input = document.createElement("input");
    input.setAttribute("class","form-control");
    input.setAttribute("id","choix3");
    input.setAttribute("placeholder","Choix 3");
    input.setAttribute("type","text");
    input.setAttribute("autofocus","");
    form.appendChild(input);
    col3.appendChild(form);
    //
    var form  = document.createElement("div");
    var input = document.createElement("input");
    input.setAttribute("class","btn btn-block btn-primary");
    input.setAttribute("id","themeBtn");
    input.setAttribute("onclick","sendThemes();");
    input.setAttribute("type","button");
    input.setAttribute("autofocus","");
    input.setAttribute("autocomplete","on");
    input.setAttribute("value","Souméttre");
    form.appendChild(input);
    col4.appendChild(form);

    div.appendChild(col1);
    div.appendChild(col2);
    div.appendChild(col3);
    div.appendChild(col4);

    return div;





}

//Send Choices
var sendThemes = function(){
    var choix1 = document.getElementById("choix1").value;
    var choix2 = document.getElementById("choix2").value;
    var choix3 = document.getElementById("choix3").value;
    var c1 = themes[0];
    var c2 = themes[1];
    var c3 = themes[2];


    //
    var filter = /^[0-9]+(\.[0-9]{1,2})?$/;
    //
    if( !filter.test(choix1) || !isInteger(choix1) ||
        !filter.test(choix2) || !isInteger(choix2) ||
        !filter.test(choix3) || !isInteger(choix3)) {
        updateFeedback("Entre un nombre SVP, pas de text! ",3000);
        return;
    };


    //Validate fields
    if(choix1 == choix2 ||choix1 == choix3 || choix2 == choix3){
        updateFeedback("Les choix(s) doit etre different! ",3000);
        return;
    };

    if(choix1 == "" || choix2 == "" || choix3 == ""){
        updateFeedback("Aucune theme(s) choisit! ",3000);
        return;
    };
    //
    var ch1 = parseInt(choix1);
    var ch2 = parseInt(choix2);
    var ch3 = parseInt(choix3);

    if( (ch1 != c1.id) ){
        if( (ch1 != c2.id) ) {
            if ((ch1 != c3.id)) {
                updateFeedback("Choix 1 n'existe pas ", 3000);
                return;
            }
        }
    }
    if( (ch2 != c1.id) ){
        if( (ch2 != c2.id) ) {
            if ((ch2 != c3.id)) {
                updateFeedback("Choix 2 n'existe pas ", 3000);
                return;
            }
        }
    }
    if( (ch3 != c1.id) ){
        if( (ch3 != c2.id) ) {
            if ((ch3 != c3.id)) {
                updateFeedback("Choix 3 n'existe pas ", 3000);
                return;
            }
        }
    }
        //Lets send the request now
    //All fields were okay
    var xmlHttp = new XMLHttpRequest();

    //Prepare stateChange Handle
    xmlHttp.onreadystatechange = function(){

        if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
            //Everyting went well
            //Get object
            try {
                var obj = JSON.parse(xmlHttp.responseText);
            }catch(e){
                showSuccess("Response Erreur ; Unknown Object","Erreur",colorError);
            }
            //Check states
            var responseCode = obj["response code"];
            if(responseCode === 1){
                showSuccess(obj["response message"],"Succés",colorSuccess);
            }else if(responseCode === 2){
                updateFeedback(obj["response message"],3000);
            }else{
                showSuccess(obj["response message"],"Erreur",colorError);
            }
        }
    }
    try {
        xmlHttp.open("POST", "/manage-student", true);
         xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
       xmlHttp.send("action=choose"+"&"+"email="+userEmail+"&"+"type="+type
         +"&"+"choix1="+choix1+"&"+"choix2="+choix2+"&"+"choix3="+choix3);

    }catch(e){
        showSuccess("Server code 500","Erreur",colorError);
    }

}

//fetch Themes
var fetchThemes = function() {

    var xmlHttp = new XMLHttpRequest();

    //Prepare stateChange Handle
    xmlHttp.onreadystatechange = function(){

        if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
            //Everyting went well
            //Get object
            try {
                var obj = JSON.parse(xmlHttp.responseText);
            }catch(e){
                showSuccess("Response Erreur ; Unknown Object","Erreur",colorError);
            }
            //Check states
            var responseCode = obj["response code"];
            if(responseCode === 1){
                themes= obj.themes;
                setupChoisirTheme();
            }else if(responseCode === 2){
                updateFeedback(obj["response message"],3000);
            }else{
                showSuccess(obj["response message"],"Erreur",colorError);
            }
        }
    }
    try {
        xmlHttp.open("POST", "/manage-student", true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.send("action=list"+"&"+"email="+userEmail+"&"+"type="+type);
    }catch(e){
        showSuccess("Server code 500","Erreur",colorError);
    }



}

//get Etat of Student
var fetchState = function() {

    var xmlHttp = new XMLHttpRequest();

    //Prepare stateChange Handle
    xmlHttp.onreadystatechange = function(){

        if(xmlHttp.readyState == 4 && xmlHttp.status == 200){
            //Everyting went well
            //Get object
            try {
                var obj = JSON.parse(xmlHttp.responseText);
            }catch(e){
                showSuccess("Response Erreur ; Unknown Object","Erreur",colorError);
            }
            //Check states
            var responseCode = obj["response code"];
            if(responseCode === 1){
                studentEtat = obj.phases;
                setupSuivreStudent(obj);
            }else if(responseCode === 2){
                updateFeedback(obj["response message"],3000);
            }else{
                showSuccess(obj["response message"],"Erreur",colorError);
            }
        }
    }
    try {
        xmlHttp.open("POST", "/manage-student", true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.send("action=consult"+"&"+"email="+userEmail+"&"+"type="+type);
    }catch(e){
        showSuccess("Server code 500","Erreur",colorError);
    }

}

//Suivre Etat Student
var setupSuivreStudent = function(response) {
    if(state == 7){
        return; //Already Visible
    }
    //Set up Structures ; not visible
    var midContainer = document.getElementById("mid-container");
    midContainer.innerHTML =""; //Empty content

    //Jumbotron
    var jumbo = document.createElement("div");
    jumbo.setAttribute("class","jumbotron");
    //top span
    var topSpan = document.createElement("span");
    topSpan.setAttribute("class","text-uppercase");
    topSpan.style.font.bold;
    topSpan.innerHTML = " ETAT DU STAGE ";

    //feedback
    var divFeed = document.createElement("div");
    var errorSpan = document.createElement("span");
    errorSpan.setAttribute("class","text-center text-danger");
    errorSpan.setAttribute("id","feedback");
    errorSpan.style.fontWeight = "bold";

    divFeed.appendChild(errorSpan);
    jumbo.appendChild(divFeed);
    //

        var divEns = document.createElement("div");
        var topSpan = document.createElement("span");
        topSpan.style.font.bold;
        topSpan.innerHTML = "Enseignant   : " + response["email_enseignant"];
        divEns.appendChild(topSpan);
        jumbo.appendChild(divEns);
        var br = document.createElement("br");
        jumbo.appendChild(br);
        midContainer.appendChild(jumbo);
        //
        var divEns = document.createElement("div");
        var topSpan = document.createElement("span");
        topSpan.style.font.bold;
        topSpan.innerHTML = "Tuteur    : " + response["email_tuteur"];
        divEns.appendChild(topSpan);
        jumbo.appendChild(divEns);
        var br = document.createElement("br");
        jumbo.appendChild(br);
        midContainer.appendChild(jumbo);


    //Table for States
    jumbo.appendChild(setupEtatTable(studentEtat));

    midContainer.appendChild(jumbo);
    state = 7;
}

var setupEtatTable = function(phases) {

    var div = document.createElement("div");
    var table = document.createElement("table");
    table.setAttribute("class","table table-hover table-responsive");
    //
    var thead = document.createElement("thead");
    thead.setAttribute("class","alert-danger table-bordered");
    //header
    var tr = document.createElement("tr");
    var phaseTitle = document.createElement("td");
    var text = document.createTextNode("Phase");
    phaseTitle.appendChild(text);
    var tr = document.createElement("tr");
    var idEtat = document.createElement("td");
    var text = document.createTextNode("ID");
    idEtat.appendChild(text);
    var noteEns    = document.createElement("td");
    var text = document.createTextNode("Note-Enseignant");
    noteEns.appendChild(text);
    var noteTut    = document.createElement("td");
    var text = document.createTextNode("Note-Tutuer");
    noteTut.appendChild(text);
    tr.appendChild(phaseTitle);
    tr.appendChild(idEtat);
    tr.appendChild(noteEns);
    tr.appendChild(noteTut);
    thead.appendChild(tr);
    var tbody = document.createElement("tbody");
    //Body
    for ( var i = 0; i < phases.length ; ++i) {

        var tr = document.createElement("tr");
        var tdPhase = document.createElement("td");
        var tdId    = document.createElement("td");
        var tdNoteEns = document.createElement("td");
        var tdNoteTut = document.createElement("td");
        //
        //Add content : loop through Phases (1-4)
        tdPhase.innerHTML = i+1;
        tdId.innerHTML = phases[i].id_etat;
        tdNoteEns.innerHTML = phases[i].note_ens;
        tdNoteTut.innerHTML = phases[i].note_tut;
        tr.appendChild(tdPhase);
        tr.appendChild(tdId);
        tr.appendChild(tdNoteEns);
        tr.appendChild(tdNoteTut);
        tbody.appendChild(tr);
    }
    //End
    //Append body and Head to table
    table.appendChild(thead);
    table.appendChild(tbody);
    div.appendChild(table);

    return div;
}
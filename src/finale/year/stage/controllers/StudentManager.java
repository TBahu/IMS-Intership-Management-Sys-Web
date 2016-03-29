package finale.year.stage.controllers;

import finale.year.stage.models.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by philipchicco on 05/06/15.
 */
@WebServlet(name ="StudentManager")
public class StudentManager extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
       //Get Params
       String emailUser = request.getParameter("email");
       String action    = request.getParameter("action");
       int type         = Integer.parseInt(request.getParameter("type"));

       String responseString = null;

       switch(type){
           //Enseignant
           case 2:  if(action.equals("fetchStudents")){
                        responseString = Enseignant.fetchStudents(emailUser,type);

                    }else if(action.equals("mentionner")){

                        double note = Double.parseDouble(request.getParameter("note"));
                        responseString = Enseignant.mentionnerNote(emailUser,note,type);
                    }else if(action.equals("fetchEtat")){

                        responseString = User.fetchEtat(emailUser,type);
                    }
                    break;
           //Tutuer
           case 1:  if(action.equals("fetchStudents")){
                            responseString = Enseignant.fetchStudents(emailUser,type);

                        }else if(action.equals("mentionner")){
                            double note = Double.parseDouble(request.getParameter("note"));
                            responseString = Enseignant.mentionnerNote(emailUser,note,type);
                        }else if(action.equals("fetchEtat")){
                            responseString = User.fetchEtat(emailUser,type);
                        }
                        //
                        break;
           //Agent de la Scolarite
           case 3:  if(action.equals("consult")){
                        responseString = Agent.scolarite(emailUser);
                    }
                    break;

           //Responsable
           case 4:  if(action.equals("etat")){
                        responseString = Etudiant.consult(emailUser);
                    }
                    break;
           //Student
           case 0 :
                    //
                    if(action.equals("choose")){
                        int choix1 = Integer.parseInt(request.getParameter("choix1"));
                        int choix2 = Integer.parseInt(request.getParameter("choix2"));
                        int choix3 = Integer.parseInt(request.getParameter("choix3"));
                        responseString = Etudiant.choisirThemes(emailUser, choix1, choix2, choix3);
                    }else if(action.equals("list")){
                        responseString = Etudiant.list();
                    }else if(action.equals("consult")){
                        responseString = Etudiant.consult(emailUser);
                    }
                    break;


           default:
                    break;
       }
        //Send Response
       response.setContentType("application/json");
       response.getWriter().println(responseString);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }
}

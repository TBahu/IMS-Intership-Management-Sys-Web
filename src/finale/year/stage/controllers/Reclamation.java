package finale.year.stage.controllers;

import finale.year.stage.models.Enseignant;
import finale.year.stage.models.Etudiant;
import finale.year.stage.models.Responsable;
import finale.year.stage.models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by newton on 5/12/15.
 */
@WebServlet(name = "Reclamation")
public class Reclamation extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userAgent = request.getHeader("User-Agent");
        String etat = request.getParameter("id_etat");
        int idEtat = (etat != null)?Integer.parseInt(etat) : -1;

        String description = request.getParameter("description");
        String emailRecepteur = request.getParameter("email_recepteur");
        String action = request.getParameter("action");

        String responseString = null;
        //handle session
        //submit let Etudiant handle it
        if(action.equals("submit")){

             String senderEmail = request.getParameter("email_sender");
             responseString = Etudiant.submitReclamation(idEtat,description,emailRecepteur,senderEmail);
        }else if (action.equals("list")){
            responseString = User.listReclamation();

        }else if(action.equals("transfer")){
            int valid = Integer.parseInt(request.getParameter("valid"));
            String idStr = request.getParameter("id");
            responseString = User.transferReclamation((idStr != null)? Integer.parseInt(idStr) : -1,valid);
        }else if(action.equals("consulter")){
            responseString = Enseignant.consulterReclamations(emailRecepteur);
        }
        //
        response.setContentType("application/json");
        response.getWriter().println(responseString);


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }
}

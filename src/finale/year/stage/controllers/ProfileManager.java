package finale.year.stage.controllers;

import finale.year.stage.models.Enseignant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by philipchicco on 03/06/15.
 */

@WebServlet(name = "ProfileManager")
public class ProfileManager extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        //Get Params and Header content
        String action        = request.getParameter("action");
        String oldEmail      = request.getParameter("oldEmail");
        String password      = request.getParameter("password");
        String firstName     = request.getParameter("name");
        String lastName      = request.getParameter("lastName");
        String address       = request.getParameter("address");
        String tel           = request.getParameter("tel");
        String sex           = request.getParameter("sex");
        int type             = Integer.parseInt(request.getParameter("userType"));

        String responseString = null;

        //Check for Empty Fields
        if(action.equals("update")){
            responseString = Enseignant.modifierProfile(oldEmail,password, firstName, lastName, address, sex, tel,type);
            System.out.println("TEL"+tel);

        }

        response.setContentType("application/json");
        response.getWriter().println(responseString);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}

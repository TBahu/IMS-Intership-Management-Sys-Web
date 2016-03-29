package finale.year.stage.controllers;

import finale.year.stage.models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by newton on 5/2/15.
 */
@WebServlet(name = "PasswordRecovery")
public class PasswordRecovery extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String demand = request.getParameter("demand");
        String email = request.getParameter("email");
        String responseString = null;
        if(demand.equals("yes")){
             responseString = User.recoverPassword(email);
        }else if(demand.equals("no")){
            String code = request.getParameter("code");
            String password = request.getParameter("password");
            responseString = User.changePassword(email,code,password);
        }
        response.setContentType("application/json");
        response.getWriter().println(responseString);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }
}

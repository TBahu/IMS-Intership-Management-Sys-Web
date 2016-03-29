package finale.year.stage.controllers;

import finale.year.stage.models.Responsable;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by philipchicco on 06/06/15.
 */

@WebServlet(name = "StageManager")
public class StageManager extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");

        String responseObject = null;
        //
        if(action.equals("launchAffectation"))
            responseObject = Responsable.launchAffectation();
        else if(action.equals("listStudents"))
            responseObject = Responsable.listStudents();
        else if(action.equals("launchDeliberation")){
            responseObject =  Responsable.deliberation();
        }
            //Send Response
            response.setContentType("application/json");
            response.getWriter().println(responseObject);
    }
}

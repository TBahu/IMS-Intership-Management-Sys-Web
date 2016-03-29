package finale.year.stage.controllers;

import finale.year.stage.models.Responsable;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by philipchicco on 05/06/15.
 */
@WebServlet(name = "PhaseManager")
public class PhaseManager extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action  = request.getParameter("action");
        int phaseNo    = Integer.parseInt(request.getParameter("phase"));

        String responseObject = null;


        if(action.equals("launch")){
            String dateStart = request.getParameter("dateStart");
            String dateEnd   = request.getParameter("dateEnd");
            java.util.Date start = new java.util.Date(dateStart);
            java.util.Date end = new java.util.Date(dateEnd);
            responseObject
                    = Responsable.launchPhase(phaseNo,
                    new java.sql.Date(start.getTime()),
                    new java.sql.Date(end.getTime()));
        }else if(action.equals("end")){
            responseObject = Responsable.endPhase(phaseNo);

        }
        //Send Response
        response.setContentType("application/json");
        response.getWriter().println(responseObject);
    }
}

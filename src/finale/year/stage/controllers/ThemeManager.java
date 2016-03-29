package finale.year.stage.controllers;

import finale.year.stage.models.User;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by newton on 5/5/15.
 */
@WebServlet(name = "ThemeManager")
public class ThemeManager extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        int userType = (request.getParameter("type") != null) ? Integer.parseInt(request.getParameter("type")):-1;
        System.out.println("action : "+action+" type :"+userType);
        String responseString = null;
        switch (userType) {

            case 2:
            case 4:

                if (action.equals("add")) {
                    //construct a json string holding a theme object from params and pass it
                    responseString = User.addTheme(getJSONStringFromParams(request));

                } else if (action.equals("modify")) {
                    //construct a json string holding a theme object from params and pass it
                    responseString = User.modifyTheme(getJSONStringFromParams(request));

                } else if (action.equals("delete")) {

                    responseString = User.deleteTheme(Integer.parseInt(request.getParameter("id")));//get id and pass it

                } else if (action.equals("list")) {

                    //get all themes
                    responseString = User.listThemes();


                }
                break;
            default:

                if (action.equals("list")) {
                    //get all themes
                    responseString = User.listThemes();
                }


        }

        response.setContentType("application/json");
        response.getWriter().println(responseString);


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    private String getJSONStringFromParams(HttpServletRequest request){
        JSONObject theme = new JSONObject();
        try {
            String id = null;
            if((id = request.getParameter("id")) != null){
                //get the id if it's one of the params
                theme.put("id",id);
            }
            theme.put("email",request.getParameter("email"));
            theme.put("title",request.getParameter("title"));
            theme.put("description",request.getParameter("description"));
            theme.put("type",Integer.parseInt(request.getParameter("type")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return theme.toString();
    }


}

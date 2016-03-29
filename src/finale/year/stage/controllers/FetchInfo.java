package finale.year.stage.controllers;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by newton on 5/29/15.
 */
@WebServlet(name = "FetchInfo")
public class FetchInfo extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String sessionId = request.getParameter("sessionid");
        Object id = session.getAttribute("sessionid");
        JSONObject user = (JSONObject) session.getAttribute("user");
        if(id != null && id.equals(sessionId)){
            JSONObject obj = new JSONObject();
            try {
                obj.put("sessionid",sessionId);
                obj.put("user",user);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            response.getWriter().println(obj.toString());

        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }
}

package finale.year.stage.controllers;

import finale.year.stage.models.User;
import finale.year.stage.util.Util;
import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by newton on 4/29/15.
 */
@WebServlet(name = "Login")
public class Login extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userAgent = request.getHeader("User-Agent");
        String responseString = User.login(email, password);
        JSONObject respObj = null;
        int responseCode = -1;
        HttpSession session = null;
        try {
            respObj = new JSONObject(responseString);
            responseCode = respObj.getInt("response code");
            if(responseCode == 1){
                session = request.getSession();
                String sessionId = Util.encodeToBase64(email+new Random().nextLong());
                JSONObject user = respObj.getJSONObject("user");
                respObj.put("sessionid",sessionId);
                session.setAttribute("sessionid",sessionId);
                session.setAttribute("user",user);

                Iterator<String> it = user.keys();
                String key = null;
                while(it.hasNext()){
                    key = it.next();
                    System.out.println(key+":"+user.get(key));
                }
                System.out.println(user.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        response.setContentType("application/json");
        response.getWriter().println(respObj.toString());

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        }
}

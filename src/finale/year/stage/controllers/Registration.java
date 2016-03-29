package finale.year.stage.controllers;

import finale.year.stage.models.User;
import finale.year.stage.util.Util;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by newton on 4/29/15.
 */
@WebServlet(name = "Registration")
public class Registration extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.println("pass : " + password);
        String userAgent = request.getHeader("User-Agent");
        String responseString = User.register(id, email, password);
        JSONObject respObj = null;
        HttpSession session = null;
        String sessionId = null;
        try {
            respObj = new JSONObject(responseString);
            if(respObj.getInt("response code") == 1){
                session = request.getSession();
                 sessionId = Util.encodeToBase64(email + new Random().nextLong());
                respObj.put("sessionid", sessionId);
                session.setAttribute("user",respObj.get("user"));
                session.setAttribute("sessionid", sessionId);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }



        //check if response code is one before creating sessionid


        /*JSONObject respObj = null;
        int responseCode = -1;
        HttpSession session = null;
        try {
            respObj = new JSONObject(responseString);
            responseCode = respObj.getInt("response code");
            if(responseCode == 1 && !userAgent.equals("desktop") && !userAgent.equals("mobile")){

                request.setAttribute("user", respObj.getJSONObject("user").toString());
                JSONObject user = respObj.getJSONObject("user");
                Iterator<String> it = user.keys();
                String key = null;
                while(it.hasNext()){
                    key = it.next();
                    System.out.println(key+":"+user.get(key));
                }
                getServletContext().getRequestDispatcher("/home_.jsp").forward(request,response);
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        response.setContentType("application/json");
        response.getWriter().println(respObj.toString());
        System.out.println(respObj.toString());


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String sessionId = request.getParameter("sessionid");
        Object id = session.getAttribute("sessionid");
        JSONObject user = (JSONObject) session.getAttribute("user");
        request.setAttribute("user", user);
        if (id != null && id.equals(sessionId)) {
            getServletContext().getRequestDispatcher("/home_.jsp").forward(request, response);
            return;
        }

    }
}

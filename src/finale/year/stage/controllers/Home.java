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
 * Created by newton on 5/11/15.
 */
@WebServlet(name = "Home")
public class Home extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String sessionId = request.getParameter("sessionid");
        Object id = session.getAttribute("sessionid");
        JSONObject user = (JSONObject) session.getAttribute("user");
        request.setAttribute("user", user);
        if (id != null && id.equals(sessionId)) {
            int type = 0;
            try {
                 type = user.getInt("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            switch (type){
                case 0:
                    getServletContext().getRequestDispatcher("/etudiant/home.jsp").forward(request, response);
                    break;
                case 1:
                    getServletContext().getRequestDispatcher("/tuteur//home.jsp").forward(request, response);
                    break;
                case 2:
                    getServletContext().getRequestDispatcher("/enseignant/home.jsp").forward(request, response);
                    break;
            }

            return;
        }else{
            //getServletContext().getRequestDispatcher("/").forward(request,response);
            response.setStatus(307);
            response.setHeader("Location","/");
        }



    }
}

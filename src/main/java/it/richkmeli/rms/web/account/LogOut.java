package it.richkmeli.rms.web.account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/LogOut")
public class LogOut extends HttpServlet {
    it.richkmeli.jframework.network.tcp.server.http.account.LogOut logOut = new it.richkmeli.jframework.network.tcp.server.http.account.LogOut() {
        @Override
        protected void doSpecificAction() {

        }
    };

    public LogOut() {
            super();
        }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logOut.doAction(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logOut.doAction(request,response);
    }
}

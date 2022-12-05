package com.example.casestudy3.controller;

import com.example.casestudy3.DAO.AdminDAO;
import com.example.casestudy3.service.login.LoginService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "LogInServlet", value = "/LogInServlet")
public class LogInServlet extends HttpServlet {
    private LoginService loginService = new LoginService();
    private AdminDAO adminDAO = new AdminDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            case "login":
                goHome(request, response);
                break;
        }
    }

    private void goHome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher ;
        switch (loginService.logIn(request)){
            case 1:  requestDispatcher = request.getRequestDispatcher("homeAdmin.jsp");
                requestDispatcher.forward(request,response);
            break;
            case 2:  requestDispatcher = request.getRequestDispatcher("homeUser.jsp");
                     request.setAttribute("user",adminDAO.findByIdUser(loginService.checkOnline()));
                requestDispatcher.forward(request,response);
            break;
            case 3:  requestDispatcher = request.getRequestDispatcher("homeSinger.jsp");
                     request.setAttribute("singer",adminDAO.findByIdSinger(loginService.checkOnline()));
                requestDispatcher.forward(request,response);
            break;
            default: response.sendRedirect("http://localhost:8080/login/login.jsp");
        }

    }
}

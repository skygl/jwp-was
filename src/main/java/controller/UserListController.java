package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import view.ModelAndView;
import view.RedirectView;
import view.View;

public class UserListController extends AbstractController {
    @Override
    public View doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (isLogined(httpRequest)) {
            ModelAndView view = new ModelAndView("user/list.html");
            view.addAttribute("users", DataBase.findAll());
            return view;
        }
        return new RedirectView("user/login.html");
    }

    private boolean isLogined(HttpRequest httpRequest) {
        return "true".equals(httpRequest.getCookie().get("logined"));
    }
}

package com.example.check_in_app.authcontroller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class ErrorController {

    @RequestMapping("/error")
    public ModelAndView accessDenied() {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("titulo", "404");
        mav.addObject("mensaje", "Página no encontrada");
        return mav;
    }
}
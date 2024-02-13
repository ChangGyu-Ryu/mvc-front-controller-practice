package org.example.mvc;

import org.example.mvc.controller.Controller;
import org.example.mvc.controller.RequestMethod;
import org.example.mvc.view.JspViewResolver;
import org.example.mvc.view.ModelAndView;
import org.example.mvc.view.View;
import org.example.mvc.view.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@WebServlet("/")
public class DispatcherServlet extends HttpServlet {

    private final Logger log = LoggerFactory.getLogger("DispatcherServlet.class");

    private RequestMappingHandlerMapping rmhm;

    private List<HandlerAdaptor> hadnlerAdaptors;

    private List<ViewResolver> viewResolvers;
    @Override
    public void init() throws ServletException {
        rmhm = new RequestMappingHandlerMapping();
        rmhm.init();

        hadnlerAdaptors = List.of(new SimpleContorllerHandlerAdaptor());
        viewResolvers = Collections.singletonList(new JspViewResolver());
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("[DispatcherServlet] service started");

        try {
            Controller handler = rmhm.findHandler(new HandlerKey(RequestMethod.valueOf(request.getMethod()), request.getRequestURI()));

            HandlerAdaptor handlerAdaptor = hadnlerAdaptors.stream()
                    .filter(ha -> ha.supports(handler))
                    .findFirst()
                    .orElseThrow(() -> new ServletException("no adaptor for handler [" + handler + "]"));

            ModelAndView modelAndView = handlerAdaptor.handle(request, response, handler);

            for (ViewResolver viewResolver : viewResolvers) {
                View view = viewResolver.resolveView(modelAndView.getViewName());
                view.rendor(modelAndView.getModel(), request, response);
            }
        } catch(Exception e) {
            log.error("exception occurred : [{}]", e.getMessage(), e);
            throw new ServletException(e);
        }
    }

}

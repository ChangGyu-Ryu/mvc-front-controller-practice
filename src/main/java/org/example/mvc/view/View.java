package org.example.mvc.view;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface View {
    void rendor(Map<String, ? > model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, Exception;
}

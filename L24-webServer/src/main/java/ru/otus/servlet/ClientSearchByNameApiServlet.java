package ru.otus.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import ru.otus.crm.model.Client;
import ru.otus.dao.ClientDao;

public class ClientSearchByNameApiServlet extends HttpServlet {
    private final transient ClientDao clientDao;
    private final transient Gson gson;

    public ClientSearchByNameApiServlet(ClientDao clientDao, Gson gson) {
        this.clientDao = clientDao;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        if (name == null || name.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Не указано имя для поиска");
            return;
        }

        Optional<List<Client>> clients = clientDao.findByName(name);
        response.setContentType("application/json;charset=UTF-8");

        if (clients.isPresent() && !clients.get().isEmpty()) {
            response.getWriter().println(gson.toJson(clients.get().toString()));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("[]");
        }
    }
}

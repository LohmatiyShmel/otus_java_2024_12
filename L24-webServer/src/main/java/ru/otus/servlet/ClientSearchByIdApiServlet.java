package ru.otus.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import ru.otus.crm.model.Client;
import ru.otus.dao.ClientDao;

@SuppressWarnings({"java:S1989"})
public class ClientSearchByIdApiServlet extends HttpServlet {
    private static final int ID_PATH_PARAM_POSITION = 1;

    private final transient ClientDao clientDao;
    private final transient Gson gson;

    public ClientSearchByIdApiServlet(ClientDao clientDao, Gson gson) {
        this.clientDao = clientDao;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long id = extractIdFromRequest(request);
        Optional<Client> client = clientDao.findById(id);

        response.setContentType("application/json;charset=UTF-8");
        if (client.isPresent()) {
            response.getWriter().println(gson.toJson(client.get().toString()));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("{\"error\":\"Клиент не найден\"}");
        }
    }

    private long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split("/");
        String id = (path.length > 1) ? path[ID_PATH_PARAM_POSITION] : String.valueOf(-1);
        return Long.parseLong(id);
    }
}

package ru.otus.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import ru.otus.crm.model.Client;
import ru.otus.dao.ClientDao;
import ru.otus.model.dto.ClientCreateRequest;

public class ClientCreateServlet extends HttpServlet {
    private final transient ClientDao clientDao;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClientCreateServlet(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ClientCreateRequest clientCreateRequest =
                objectMapper.readValue(req.getInputStream(), ClientCreateRequest.class);
        Optional<Client> createdClient = clientDao.createClient(
                clientCreateRequest.getName(), clientCreateRequest.getStreet(), clientCreateRequest.getPhones());

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (createdClient.isPresent()) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("client", createdClient.get().toString());
            objectMapper.writeValue(resp.getWriter(), responseData);
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("status", "error");
            errorData.put("message", "Не удалось создать клиента");
            objectMapper.writeValue(resp.getWriter(), errorData);
        }
    }
}

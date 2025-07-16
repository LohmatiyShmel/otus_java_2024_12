package ru.otus.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.model.dto.ClientCreateDto;
import ru.otus.services.ClientService;

@Controller
@RequestMapping("/clients")
@AllArgsConstructor
public class ClientsController {

    private final ClientService clientService;

    @GetMapping
    public String showClients(Model model) {
        model.addAttribute("clients", clientService.getAllClientsWithRelations());
        model.addAttribute("newClient", new ClientCreateDto());
        return "clients/list";
    }

    @PostMapping
    public String createClient(@ModelAttribute ClientCreateDto dto) {
        clientService.createClient(dto);
        return "redirect:/clients";
    }
}

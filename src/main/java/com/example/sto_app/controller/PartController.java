package com.example.sto_app.controller;

import com.example.sto_app.dto.PartRequest;
import com.example.sto_app.model.Part;
import com.example.sto_app.service.PartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @GetMapping
    public List<Part> getAll() {
        return partService.getAll();
    }

    @GetMapping("/{id}")
    public Part getById(@PathVariable Long id) {
        return partService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Part create(
            @Valid @RequestBody PartRequest request
    ) {
        return partService.create(request);
    }

    @PutMapping("/{id}")
    public Part update(
            @PathVariable Long id,
            @Valid @RequestBody PartRequest request
    ) {
        return partService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        partService.delete(id);
    }
}
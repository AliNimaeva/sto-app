package com.example.sto_app.service;

import com.example.sto_app.dto.PartRequest;
import com.example.sto_app.exception.ResourceNotFoundException;
import com.example.sto_app.model.Part;
import com.example.sto_app.repository.PartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public List<Part> getAll() {
        return partRepository.findAll();
    }

    public Part getById(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Запчасть не найдена: " + id
                        )
                );
    }

    public Part create(PartRequest request) {

        if (request.getArticle() != null &&
                !request.getArticle().isBlank() &&
                partRepository.existsByArticle(request.getArticle())) {

            throw new IllegalArgumentException(
                    "Запчасть с таким артикулом уже существует"
            );
        }

        Part part = new Part();

        part.setName(request.getName());
        part.setArticle(request.getArticle());
        part.setPrice(request.getPrice());
        part.setQuantity(request.getQuantity());
        part.setDescription(request.getDescription());

        return partRepository.save(part);
    }

    public Part update(Long id, PartRequest request) {

        Part part = getById(id);

        if (request.getArticle() != null &&
                !request.getArticle().isBlank() &&
                partRepository.existsByArticleAndIdNot(
                        request.getArticle(),
                        id
                )) {

            throw new IllegalArgumentException(
                    "Запчасть с таким артикулом уже существует"
            );
        }

        part.setName(request.getName());
        part.setArticle(request.getArticle());
        part.setPrice(request.getPrice());
        part.setQuantity(request.getQuantity());
        part.setDescription(request.getDescription());

        return partRepository.save(part);
    }

    public void delete(Long id) {
        Part part = getById(id);
        partRepository.delete(part);
    }
}
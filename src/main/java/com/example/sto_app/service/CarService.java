package com.example.sto_app.service;

import com.example.sto_app.exception.ResourceNotFoundException;
import com.example.sto_app.model.Car;
import com.example.sto_app.repository.CarRepository;
import org.springframework.stereotype.Service;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car getById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Автомобиль не найден: " + id)
                );
    }

    public Car create(
            String brand,
            String model,
            Integer year,
            String plateNumber,
            String clientName,
            String clientPhone
    ) {
        if (carRepository.existsByPlateNumber(plateNumber)) {
            throw new IllegalArgumentException(
                    "Автомобиль с таким госномером уже существует"
            );
        }

        Car car = new Car();

        car.setBrand(brand);
        car.setModel(model);
        car.setYear(year);
        car.setPlateNumber(plateNumber);
        car.setClientName(clientName);
        car.setClientPhone(clientPhone);

        return carRepository.save(car);
    }
}
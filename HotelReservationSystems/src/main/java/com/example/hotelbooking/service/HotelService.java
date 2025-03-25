package com.example.hotelbooking.service;

import com.example.hotelbooking.entity.Hotel;
import com.example.hotelbooking.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + id));
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel updateHotel(Long id, Hotel hotelData) {
        Hotel existingHotel = getHotelById(id);

        // Обновляем поля, которые разрешено менять
        existingHotel.setName(hotelData.getName());
        existingHotel.setTitle(hotelData.getTitle());
        existingHotel.setCity(hotelData.getCity());
        existingHotel.setAddress(hotelData.getAddress());
        existingHotel.setDistanceFromCenter(hotelData.getDistanceFromCenter());
        // rating и ratingCount не трогаем по ТЗ

        return hotelRepository.save(existingHotel);
    }

    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }
    public Page<Hotel> getHotelsPage(Pageable pageable) {
        return hotelRepository.findAll(pageable);
    }

}

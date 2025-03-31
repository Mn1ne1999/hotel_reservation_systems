package com.example.hotelbooking.service;

import com.example.hotelbooking.entity.Hotel;
import com.example.hotelbooking.mapper.HotelMapper;
import com.example.hotelbooking.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    public HotelService(HotelRepository hotelRepository, HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.hotelMapper = hotelMapper;
    }

    @Transactional
    public Hotel updateHotelRating(Long hotelId, Double newMark) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Отель не найден с id: " + hotelId));

        int currentCount = hotel.getNumberOfRatings();
        double currentRating = hotel.getRating();

        double totalRating = currentRating * currentCount;
        totalRating += newMark;

        int newCount = currentCount + 1;
        double newAverage = Math.round((totalRating / newCount) * 10.0) / 10.0;

        hotel.setRating(newAverage);
        hotel.setNumberOfRatings(newCount);

        return hotelRepository.save(hotel);
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
        existingHotel.setName(hotelData.getName());
        existingHotel.setTitle(hotelData.getTitle());
        existingHotel.setCity(hotelData.getCity());
        existingHotel.setAddress(hotelData.getAddress());
        existingHotel.setDistanceFromCenter(hotelData.getDistanceFromCenter());
        return hotelRepository.save(existingHotel);
    }

    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    public Page<Hotel> getHotelsPage(Pageable pageable) {
        return hotelRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Hotel> getHotelsBySpec(org.springframework.data.jpa.domain.Specification<Hotel> spec, Pageable pageable) {
        return hotelRepository.findAll(spec, pageable);
    }
}

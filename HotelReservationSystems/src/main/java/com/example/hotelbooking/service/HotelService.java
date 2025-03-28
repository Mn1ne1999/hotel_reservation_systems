package com.example.hotelbooking.service;

import com.example.hotelbooking.dto.hotel.HotelResponseDto;
import com.example.hotelbooking.dto.hotel.RatingUpdateRequestDto;
import com.example.hotelbooking.entity.Hotel;
import com.example.hotelbooking.mapper.HotelMapper;
import com.example.hotelbooking.repository.HotelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    private final HotelMapper hotelMapper;

    public HotelService(HotelRepository hotelRepository, HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;

        this.hotelMapper = hotelMapper;
    }

    /**
     * Обновляет рейтинг отеля, принимая новую оценку.
     * Новый средний рейтинг вычисляется по формуле:
     * newTotal = currentRating * numberOfRatings + newMark,
     * newCount = numberOfRatings + 1,
     * newRating = newTotal / newCount (округление до одного знака после запятой).
     */
    @Transactional
    public Hotel updateHotelRating(Long hotelId, Double newMark) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Отель не найден с id: " + hotelId));

        int currentCount = hotel.getNumberOfRatings();
        double currentRating = hotel.getRating();

        // Вычисляем сумму всех оценок до новой оценки
        double totalRating = currentRating * currentCount;
        // Добавляем новую оценку
        totalRating += newMark;
        // Увеличиваем количество оценок
        int newCount = currentCount + 1;
        // Вычисляем новый средний рейтинг и округляем до одного знака после запятой
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

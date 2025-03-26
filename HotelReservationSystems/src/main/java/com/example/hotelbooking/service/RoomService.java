package com.example.hotelbooking.service;

import com.example.hotelbooking.entity.Hotel;
import com.example.hotelbooking.entity.Room;
import com.example.hotelbooking.repository.HotelRepository;
import com.example.hotelbooking.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    // Создание комнаты
    public Room createRoom(Room room, Long hotelId) {
        // Находим отель по ID
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Отель не найден по идентификатору: " + hotelId));
        room.setHotel(hotel);
        return roomRepository.save(room);
    }

    // Получение комнаты по ID
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комната не найдена по идентификатору: " + id));
    }

    // Получение списка всех комнат
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // Обновление комнаты
    public Room updateRoom(Long id, Room roomData, Long hotelId) {
        Room existingRoom = getRoomById(id);
        existingRoom.setName(roomData.getName());
        existingRoom.setDescription(roomData.getDescription());
        existingRoom.setRoomNumber(roomData.getRoomNumber());
        existingRoom.setPrice(roomData.getPrice());
        existingRoom.setMaxGuests(roomData.getMaxGuests());
        existingRoom.setUnavailableDates(roomData.getUnavailableDates());
        // Обновляем отель, если необходимо
        if (hotelId != null) {
            Hotel hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new EntityNotFoundException("Отель не найден по идентификатору: " + hotelId));
            existingRoom.setHotel(hotel);
        }
        return roomRepository.save(existingRoom);
    }

    // Удаление комнаты
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}

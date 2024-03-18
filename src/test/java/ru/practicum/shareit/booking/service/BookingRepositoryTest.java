package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.request.service.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.CommentRepository;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    User userOwner = User.builder()
            .email("bob@gamail.com")
            .name("Bob")
            .build();

    User userBooker = User.builder()
            .email("bob2@gamail.com")
            .name("Bob2")
            .build();
    Item item = Item.builder()
            .name("Test")
            .description("Test")
            .owner(userOwner)
            .available(true)
            .build();

    Item itemTwo = Item.builder()
            .name("Test2")
            .description("Test2")
            .owner(userOwner)
            .available(true).build();

    Booking booking = Booking.builder()
            .item(itemTwo)
            .booker(userBooker)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now().minusHours(2))
            .build();

    Booking bookingTwo = Booking.builder()
            .item(itemTwo)
            .booker(userBooker)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(3))
            .end(LocalDateTime.now().minusDays(1))
            .build();

    Booking bookingThree = Booking.builder()
            .item(itemTwo)
            .booker(userBooker)
            .status(BookingStatus.REJECTED)
            .start(LocalDateTime.now().plusHours(2))
            .end(LocalDateTime.now().plusDays(1))
            .build();

    Booking bookingFour = Booking.builder()
            .item(item)
            .booker(userBooker)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusHours(1))
            .end(LocalDateTime.now().plusHours(3))
            .build();

    PageRequest page = PageRequest.of(1 / 10, 10);

    @BeforeEach
    void save() {
        userRepository.save(userOwner);
        userRepository.save(userBooker);
        itemRepository.save(item);
        itemRepository.save(itemTwo);
        bookingRepository.save(booking);
        bookingRepository.save(bookingTwo);
        bookingRepository.save(bookingThree);
        bookingRepository.save(bookingFour);
    }

    @Test
    void findBookingsWhenOwnerIdOneAndBookingsThree() {
        List<Booking> bookings = bookingRepository.findByOwnerId(userOwner.getId(), page);

        System.out.println(bookings);
        assertEquals(bookings.size(), 4);
        assertEquals(bookings.get(0).getId(), bookingThree.getId());
    }

    @Test
    void findBookingsWhenOwnerIdOneAndItemIdTwoInPast() {
        List<Booking> bookings = bookingRepository.findByOwnerIdAndItemIdPastBookings(userOwner.getId(), itemTwo.getId());

        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(1).getId(), bookingTwo.getId());
    }

    @Test
    void findBookingsWhenOwnerIdOneAndStatusRejected() {
        List<Booking> bookings = bookingRepository.findByOwnerIdAndStatus(userOwner.getId(), "REJECTED", page);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), bookingThree.getId());
        assertEquals(bookings.get(0).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void findBookingsWhenOwnerIdOneInFuture() {
        List<Booking> bookings = bookingRepository.findByOwnerIdFutureBookings(userOwner.getId(), page);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), bookingThree.getId());
    }

    @Test
    void findBookingsWhenBookerIdTwoInPresent() {
        List<Booking> bookings = bookingRepository.findByBookerIdCurrentBookings(userBooker.getId(), page);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker().getId(), userBooker.getId());
    }

    @AfterEach
    void delete() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}